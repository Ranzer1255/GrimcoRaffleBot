package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

import java.util.Arrays;
import java.util.List;

public class ChannelEnableCommand extends AbstractRaffleCommand implements Describable {

    private static final String ENABLE = "enable";

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {
        IChannelData channel = GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel());
        if(event.getOption(ENABLE)!=null){
            channel.setRaffle(event.getOption(ENABLE).getAsBoolean());
        }
        event.reply(channel.getRaffle()?
                            "Raffles are Allowed in this channel":
                            "Raffles are Not Allowed in this channel"
                   ).setEphemeral(true).queue();
    }

    @Override
    public void processPrefix(String[] args, MessageReceivedEvent event) {

        IChannelData channel = GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel());
        channel.setRaffle(!channel.getRaffle());

        event.getChannel().sendMessage(
                (channel.getRaffle())?
                        "raffles are Allowed in this channel":
                        "Raffles are No longer Allowed in this channel"
        ).queue();

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("enabled","enable","disable");
    }

    @Override
    public String getShortDescription() {
        return "toggle if raffles are allowed in this channel";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    @Override
    public List<Role> getRoleRequirements(Guild guild) {
        return getAllowedManagementRoles(guild);
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public String getUsage(Guild g) {
        return String.format(
                "`%sraffle %s`",
                BotCommand.getPrefix(g),
                getName()
        );
    }

    @Override
    protected SubcommandData getSubcommandData() {
        SubcommandData rtn = new SubcommandData(getName(),getShortDescription());

        rtn.addOption(OptionType.BOOLEAN,ENABLE,"Enable raffles in this channel?",false);

        return rtn;
    }
}
