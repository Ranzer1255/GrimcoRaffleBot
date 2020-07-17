package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;

import java.util.Arrays;
import java.util.List;

public class RaffleCloseCommand extends AbstractRaffleCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (raffles.containsKey(event.getTextChannel())&&raffles.get(event.getTextChannel()).isOpen()){
            raffles.get(event.getTextChannel()).close();
            event.getChannel().sendMessage("The Raffle is now Closed to further Entries").queue();
        } else {
            event.getChannel().sendMessage("There is no open raffle at this time").queue();
        }
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("close");
    }

    @Override
    public String getShortDescription() {
        return "Closes the active raffle if any";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    @Override
    public Permission getPermissionRequirements() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public List<Role> getRoleRequirements(Guild guild) {
        return getAllowedManagementRoles(guild);
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
