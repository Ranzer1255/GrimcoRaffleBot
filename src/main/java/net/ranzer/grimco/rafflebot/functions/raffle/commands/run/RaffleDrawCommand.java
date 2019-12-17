package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.Raffle;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class RaffleDrawCommand extends AbstractRaffleCommand implements Describable {
    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (raffles.containsKey(event.getTextChannel())){
            Raffle r = raffles.get(event.getTextChannel());
            if(r.getNumEntries()<=0){
                event.getChannel().sendMessage(
                        "there are no entries in the raffle"
                ).queue();
                return;
            }
            r.close();
            Member winner = r.draw();

            event.getChannel().sendMessage(String.format(
                    "Congratulations %s, your name has been drawn from the hat",
                    winner.getAsMention()
            )).queue();
        } //silent ignore of command if no raffle exists
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("draw");
    }

    @Override
    public String getShortDescription() {
        return "Draw the lucky winner";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription()+"\n\n" +
                "Closes the raffle, then removes one random entry from the raffle and pings them";
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
                getPrefix(g),
                getName()
        );
    }
}
