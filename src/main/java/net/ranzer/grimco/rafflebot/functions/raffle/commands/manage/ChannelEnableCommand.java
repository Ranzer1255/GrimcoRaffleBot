package net.ranzer.grimco.rafflebot.functions.raffle.commands.manage;

import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;

import java.util.Arrays;
import java.util.List;

public class ChannelEnableCommand extends AbstractRaffleCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {

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
        return Arrays.asList("enable","disable");
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
}
