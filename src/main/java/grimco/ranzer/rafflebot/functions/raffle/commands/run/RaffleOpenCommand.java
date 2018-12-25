package grimco.ranzer.rafflebot.functions.raffle.commands.run;

import grimco.ranzer.rafflebot.commands.Category;
import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

/* todo
create new Raffle object and store it in Raffle Map in the Superclass keyed off the Text Channel

if Raffle already exists overwrite with new instance;
 */
public class RaffleOpenCommand extends AbstractRaffleCommand implements Describable {

    // TODO: 12/24/2018
    @Override
    public void process(String[] args, MessageReceivedEvent event) {

    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("open","enable","start");
    }

    // TODO: 12/24/2018
    @Override
    public String getShortDescription() {
        return null;
    }

    // TODO: 12/24/2018
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public List<Role> getRoleRequirements() {
        return allowedManagementRoles;
    }
}
