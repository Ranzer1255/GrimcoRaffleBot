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
                BotCommand.getPrefix(g),
                getName()
        );
    }
}
