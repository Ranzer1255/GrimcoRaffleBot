package net.ranzer.grimco.rafflebot.functions.levels.Commands.settings;

import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.functions.levels.Commands.AbstractLevelCommand;
import net.ranzer.grimco.rafflebot.util.Logging;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class XPSettingsCommand extends AbstractLevelCommand implements Describable {

    private static final List<BotCommand> subCommands;

    static{
        subCommands = new ArrayList<>();
        subCommands.add(new XPThresholdCommand());
        subCommands.add(new XPEarningCommand());
        subCommands.add(new XPTimeoutCommand());
    }

    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        if (args.length == 0) {

            event.getChannel().sendMessage(currentSettingsEmbed(event.getGuild())).queue();
            return;
        }

        Optional<BotCommand> c = subCommands.stream().filter(cc -> cc.getAlias().contains(args[0])).findFirst();

        // Silent failure of miss-typed subcommands
        if (!c.isPresent()) {
            Logging.debug("no xp-settings subcommand");
            return;
        }
        Logging.debug("xp-settings Subcommand: "+c.get().getName());
        c.get().runCommand(Arrays.copyOfRange(args, 1, args.length), event);
    }

    private MessageEmbed currentSettingsEmbed(Guild g) {
        IGuildData gd = GuildManager.getGuildData(g);
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Current XP Settings");
        eb.setColor(getCategory().COLOR);
        eb.addField("XP Interval", gd.getXPTimeout() / 1000 +" seconds",true);
        eb.addField("XP Low bound", Integer.toString(gd.getXPLowBound()),true);
        eb.addField("XP high bound", Integer.toString(gd.getXPHighBound()),true);
        eb.addField("Raffle XP threshold",Integer.toString(gd.getRaffleData().getRaffleXPThreshold()),true);

        return eb.build();
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("xp-settings");
    }

    @Override
    public String getShortDescription() {
        return "change the default settings for XP earnings";
    }

    @Override
    public String getLongDescription() {
        StringBuilder sb = new StringBuilder();


        sb.append(getShortDescription()).append("\n\n");

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
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
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
