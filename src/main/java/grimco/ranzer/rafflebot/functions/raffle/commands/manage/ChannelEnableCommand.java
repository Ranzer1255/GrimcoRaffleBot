package grimco.ranzer.rafflebot.functions.raffle.commands.manage;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.data.IChannelData;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class ChannelEnableCommand extends AbstractRaffleCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {

        IChannelData channel =GuildManager.getGuildData(event.getGuild()).getChannel(event.getTextChannel());
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
                getPrefix(g),
                getName()
        );
    }
}
