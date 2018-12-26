package grimco.ranzer.rafflebot.functions.raffle.commands.manage;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.commands.admin.HelpCommand;
import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.data.IGuildData;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

/**
 * used to Bar and Unbar members from raffles
 *
 * usage raffle manage {ban | unban} [user]
 *
 * if bar. prevent user from entering raffles
 * if unbar, allow user to enter raffles
 */
public class BanUserCommand extends AbstractRaffleCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (!(args.length==2)){
            event.getChannel().sendMessage(HelpCommand.getDescription(this,event.getGuild())).queue();
        }
        IGuildData gd = GuildManager.getGuildData(event.getGuild());
        List<Member> members = event.getMessage().getMentionedMembers();
        switch (args[0]) {
            case "ban":
                for (Member m :members) {
                    gd.getMemberData(m).setBannedFromRaffle(true);
                    event.getChannel().sendMessage(m.getEffectiveName()+" is banned from future raffles.").queue();
                }
                break;
            case "unban":
                for(Member m: members) {
                    gd.getMemberData(m).setBannedFromRaffle(false);
                    event.getChannel().sendMessage(m.getEffectiveName() + " is unbanned from future raffles.").queue();
                }
                break;
            default:
                event.getChannel().sendMessage("I didn't understand please use `ban` or `unban`").queue();
        }
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("user","manage");
    }

    @Override
    public String getShortDescription() {
        return "Manage user access to raffles";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "ban: prevent user from entering raffles\n" +
                "unban: allow a banned user to enter raffles";
    }

    @Override
    public String getUsage(Guild g) {
        return String.format(
                "`%s%s {ban | unban} <@user>`",
                getPrefix(g),
                getName()
        );
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }
    @Override
    public List<Role> getRoleRequirements(Guild guild) {
        return getAllowedManagementRoles(guild);
    }
}
