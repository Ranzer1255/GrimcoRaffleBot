package grimco.ranzer.rafflebot.functions.raffle.commands.manage;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

/*todo
command args

Add <role>      add role
Remove <role>   remove role
none            see current roles

get role by Guild#getRolesByName();
 */
public class ModifyRolesCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        // TODO: 12/24/2018

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("roles"); // TODO: 12/24/2018
    }

    @Override
    public String getShortDescription() {
        return null;// TODO: 12/24/2018
    }

    @Override
    public String getLongDescription() {
        return null;// TODO: 12/24/2018
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }
}
