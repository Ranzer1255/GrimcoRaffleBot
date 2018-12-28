package grimco.ranzer.rafflebot.functions.raffle.commands.run;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class RaffleEndCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (raffles.containsKey(event.getTextChannel())) {
            raffles.remove(event.getTextChannel());
            event.getChannel().sendMessage("raffle is now ended.").queue();
        } //silent ignore of command if no active raffle
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("end");
    }

    @Override
    public String getShortDescription() {
        return "end any active raffle in this channel";
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
