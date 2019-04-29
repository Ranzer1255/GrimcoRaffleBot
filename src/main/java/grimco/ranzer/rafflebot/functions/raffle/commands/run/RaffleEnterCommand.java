package grimco.ranzer.rafflebot.functions.raffle.commands.run;

import grimco.ranzer.rafflebot.commands.Describable;
import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.functions.raffle.commands.AbstractRaffleCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class RaffleEnterCommand extends AbstractRaffleCommand implements Describable {

    @Override
    public void process(String[] args, MessageReceivedEvent event) {
        if (raffles.containsKey(event.getTextChannel())) {

            //check entrant's eligibility
            if (barred(event.getMember())) {
                String BARRED_MESSAGE = "sorry %s, but you have been barred from entry";
                event.getChannel().sendMessage(String.format(
                        BARRED_MESSAGE,
                        event.getAuthor().getAsMention()
                )).queue();
            } else if (notActive(event.getMember())) {
                String INACTIVE_MESSAGE = "Sorry %s, but you haven't been active enough in the community to be eligible for raffles";
                event.getChannel().sendMessage(String.format(
                        INACTIVE_MESSAGE,
                        event.getAuthor().getAsMention()
                )).queue();
            } else {
                String RAFFLE_EXISTS_MESSAGE = "%s, You've already entered. call %swithdraw if you would like to be removed.";
                String RAFFLE_CLOSED_MESSAGE = "sorry %s, but the raffle's been closed and a drawing is about to happen.";
                switch (raffles.get(event.getTextChannel()).addEntry(event.getMember())) {
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
            String NO_RAFFLE_MESSAGE = "I'm sorry, but there isn't a raffle currently";
            event.getChannel().sendMessage(NO_RAFFLE_MESSAGE).queue();
        }
    }

    private boolean notActive(Member member) {
        return GuildManager.getGuildData(member.getGuild()).getMemberData(member).getXP()
                <
                getRaffleData(member.getGuild()).getRaffleXPThreshold();
    }

    private boolean barred(Member member) {
        return GuildManager.getGuildData(member.getGuild()).getMemberData(member).isBannedFromRaffle();
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
