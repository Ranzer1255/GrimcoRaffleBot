package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

import java.util.Arrays;
import java.util.List;

public class RaffleEnterCommand extends AbstractRaffleCommand implements Describable {

    public static final String BARRED_MESSAGE = "sorry %s, but you have been barred from entry";
    public static final String INACTIVE_MESSAGE = "Sorry %s, but you haven't been active enough in the community to be eligible for raffles";
    public static final String RAFFLE_EXISTS_MESSAGE = "%s, You've already entered. call %swithdraw if you would like to be removed.";
    public static final String RAFFLE_CLOSED_MESSAGE = "sorry %s, but the raffle's been closed and a drawing is about to happen.";
    public static final String NO_RAFFLE_MESSAGE = "I'm sorry, but there isn't a raffle currently";

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
        return Arrays.asList("enter");
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
