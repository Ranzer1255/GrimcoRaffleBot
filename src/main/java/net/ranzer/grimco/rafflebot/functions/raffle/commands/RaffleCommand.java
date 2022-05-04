package net.ranzer.grimco.rafflebot.functions.raffle.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.raffle.Raffle;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.manage.BanUserCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.manage.ChannelEnableCommand;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.manage.RaffleOpenCommand;
import net.ranzer.grimco.rafflebot.util.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
 * Audit log entries for raffles
 * While an interesting thought, this is, unfortunately, not possible. Discord Audit Logs are
 * only on specific moderation based interactions (delete chat message, change roles, ect) so,
 * before i have this thought again and run down this rabbit hole a 3rd time, I'm going to leave
 * this note here so that i dont!
*/
public class RaffleCommand extends AbstractRaffleCommand implements Describable {

    private static final List<BotCommand> subCommands;

    static {
        subCommands=new ArrayList<>();
        subCommands.add(new RaffleOpenCommand());
//        subCommands.add(new ModifyRolesCommand());
        subCommands.add(new BanUserCommand());
        subCommands.add(new ChannelEnableCommand());

    }

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {

        Optional<BotCommand> c = subCommands.stream().filter(cc -> cc.getAlias().contains(event.getSubcommandName())).findFirst();

        // Silent failure of miss-typed subcommands
        if (c.isEmpty()) {
            Logging.debug("no raffle subcommand");
            event.reply(event.getJDA().getUserById(BotConfiguration.getInstance().getOwner()).getAsMention()+
                        " didn't thing this was even possible... he must have done something wrong... " +
                        "yell at him! (no_raffle_subcommand)").queue();
            return;
        }
        Logging.debug("raffle Subcommand: "+c.get().getName());
        if (!(c.get().getName().equals("enabled")||c.get().getName().equals("role"))){
            if (!GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel()).getRaffle()){
                event.reply("Raffles are Disabled in this channel").setEphemeral(true).queue();
                return;
            }
        }
        c.get().runSlashCommand(event);
    }

    @Override
    public void processPrefix(String[] args, MessageReceivedEvent event) {

       if (args.length == 0) {
            if(raffles.containsKey(event.getTextChannel().getId())) {
                Raffle r = raffles.get(event.getTextChannel().getId());
                event.getChannel().sendMessageEmbeds(r.getEmbed()).queue(r::setActiveMessage);
            }
            return;
        }

        Optional<BotCommand> c = subCommands.stream().filter(cc -> cc.getAlias().contains(args[0])).findFirst();

        // Silent failure of miss-typed subcommands
        if (c.isEmpty()) {
            Logging.debug("no raffle subcommand");
            return;
        }
        Logging.debug("raffle Subcommand: "+c.get().getName());
        if (!(c.get().getName().equals("enable")||c.get().getName().equals("role"))){
            if (!GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel()).getRaffle()){
                event.getChannel().sendMessage("Raffles are Disabled in this channel").queue();
                return;
            }
        }
        c.get().runPrefixCommand(Arrays.copyOfRange(args, 1, args.length), event);

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("raffle","giveaway");
    }

    @Override
    public String getShortDescription() {
        return "Be fair when giving stuff";
    }

    @Override
    public String getLongDescription() {
        StringBuilder sb = new StringBuilder();


        sb.append(getShortDescription()).append("\n\n");

        sb.append("__**configuration!**__\n(***NOT COMPATIBLE WITH INDIVIDUAL MEMBER OPERATORS AT THIS TIME***)\n");
        sb.append("To configure roles allowed to operate raffles, add roles and enable them in the Integrations tab of your server settings.\n");
        sb.append("Discord allows for Individual members to be added in this menu, however, it will not work properly at this time.\n\n");

        sb.append("__Subcommand Options__\n");
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

    @Override
    public SlashCommandData getSlashCommandData() {
        SlashCommandData rtn = Commands.slash(getName(),getShortDescription());
        rtn.setDefaultEnabled(false);

        for (BotCommand cmd: subCommands){
            AbstractRaffleCommand rcmd = (AbstractRaffleCommand) cmd;
            if (rcmd.getSubcommandData()!=null){
                rtn.addSubcommands(rcmd.getSubcommandData());
            }
        }

        return rtn;
    }

    @Override
    public List<Role> getRoleRequirements(Guild guild) {
        return getAllowedManagementRoles(guild);
    }
}
