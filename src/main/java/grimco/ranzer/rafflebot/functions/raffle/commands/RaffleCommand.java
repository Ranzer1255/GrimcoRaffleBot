package grimco.ranzer.rafflebot.functions.raffle.commands;

import grimco.ranzer.rafflebot.commands.BotCommand;
import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.commands.admin.HelpCommand;
import grimco.ranzer.rafflebot.functions.raffle.commands.manage.BanUserCommand;
import grimco.ranzer.rafflebot.functions.raffle.commands.manage.ModifyRolesCommand;
import grimco.ranzer.rafflebot.functions.raffle.commands.run.*;
import grimco.ranzer.rafflebot.util.Logging;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RaffleCommand extends AbstractRaffleCommand implements Describable {


    //TODO grant Lyrium nuclear launch codes
    private static List<BotCommand> subCommands;

    static {
        subCommands=new ArrayList<>();
        subCommands.add(new RaffleOpenCommand());
        subCommands.add(new RaffleEnterCommand());
        subCommands.add(new RaffleCloseCommand());
        subCommands.add(new RaffleDrawCommand());
        subCommands.add(new RaffleEndCommand());
        subCommands.add(new ModifyRolesCommand());
        subCommands.add(new BanUserCommand());

    }

    @Override
    public boolean isApplicableToPM() {
        return false;
    }

    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        /*
        TODO on no args, send help message or current raffle status?
         */
        if (args.length == 0) {
            event.getTextChannel().sendMessage(new MessageBuilder().setEmbed(HelpCommand.getDescription(this, event.getGuild())).build()).queue();
            return;
        }

        Optional<BotCommand> c = subCommands.stream().filter(cc -> cc.getAlias().contains(args[0])).findFirst();

        // Silent failure of miss-typed subcommands
        if (!c.isPresent()) {
            Logging.debug("no raffle subcommand");
            return;
        }
        Logging.debug("raffle Subcommand: "+c.get().getName());
        c.get().runCommand(Arrays.copyOfRange(args, 1, args.length), event);

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("raffle","r","giveaway");
    }

    @Override
    public String getShortDescription() {
        return "Be fair when giving stuff";
    }

    @Override
    public String getLongDescription() {
        StringBuilder sb = new StringBuilder();


        sb.append(getShortDescription() +"\n\n");

        for (BotCommand cmd : subCommands) {
            sb.append(
                    String.format("**%s**: %s\n", cmd.getName(), ((Describable)cmd).getShortDescription())
            );
        }

        return sb.toString();
    }

    @Override
    public String getUsage(Guild g) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("`%s%s {", getPrefix(g), getName()));
        for(BotCommand cmd : subCommands){
            sb.append(String.format("%s|", cmd.getName()));
        }
        sb.delete(sb.length()-1,sb.length());
        sb.append("}`");

        return sb.toString();
    }

    @Override
    public boolean hasSubcommands() {
        return true;
    }

    @Override
    public List<BotCommand> getSubcommands() {
        return subCommands;
    }
}
