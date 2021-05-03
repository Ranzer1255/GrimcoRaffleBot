package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.Raffle;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

import java.util.Arrays;
import java.util.List;

public class RaffleOpenCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        Raffle raffle = new Raffle();
        raffles.put(event.getTextChannel(),raffle);

        event.getChannel().sendMessage("The raffle is now Open, to enter type `"+getPrefix(event.getGuild())+"enter`").queue();
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("open","start");
    }

    @Override
    public String getShortDescription() {
        return "Open a new raffle";
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
