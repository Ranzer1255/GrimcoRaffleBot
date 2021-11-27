package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

import java.util.Collections;
import java.util.List;

public class RaffleEnterCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (raffles.containsKey(event.getTextChannel().getId())) {

            //check entrant's eligibility
            if (barred(event.getMember())) {
                event.getChannel().sendMessage(String.format(
                        BARRED_MESSAGE,
                        event.getAuthor().getAsMention()
                )).queue();
            } else if (notActive(event.getMember())) {
                event.getChannel().sendMessage(String.format(
                        INACTIVE_MESSAGE,
                        event.getAuthor().getAsMention()
                )).queue();
            } else {
                switch (raffles.get(event.getTextChannel().getId()).addEntry(event.getMember())) {
                    case added:
                        event.getChannel().sendMessage(String.format(
                            "%s you have been entered into the Raffle",
                            event.getAuthor().getAsMention()
                        )).queue();
                        break;
                    case closed:
                        event.getChannel().sendMessage(String.format(
                                RAFFLE_CLOSED_MESSAGE,
                                event.getAuthor().getAsMention()
                        )).queue();
                        break;
                    case exists:
                        event.getChannel().sendMessage(String.format(
                                RAFFLE_EXISTS_MESSAGE,
                                event.getAuthor().getAsMention(),
                                getPrefix(event.getGuild())
                        )).queue();
                        break;
                }
            }

        } else {
            event.getChannel().sendMessage(NO_RAFFLE_MESSAGE).queue();
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("enter");
    }

    @Override
    public String getShortDescription() {
        return "enter the Raffle";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }
}
