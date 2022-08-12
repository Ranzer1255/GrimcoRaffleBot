package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

public class RaffleEnterCommand extends AbstractRaffleCommand implements Describable {

    @Override
    protected void processSlash(SlashCommandInteractionEvent event) {
        if (raffles.containsKey(event.getTextChannel().getId())) {

            //check entrant's eligibility
            if (barred(event.getMember())) {
                event.reply(String.format(
                        BARRED_MESSAGE,
                        event.getMember().getAsMention()
                )).setEphemeral(true).queue();
            } else if (notActive(event.getMember())) {
                event.reply(String.format(
                        INACTIVE_MESSAGE,
                        event.getMember().getAsMention()
                )).setEphemeral(true).queue();
            } else {
                switch (raffles.get(event.getTextChannel().getId()).addEntry(event.getMember())) {
                    case added -> event.reply(String.format(
                            ENTER_RAFFLE,
                            event.getMember().getAsMention()
                    )).queue();
                    case closed -> event.reply(String.format(
                            RAFFLE_CLOSED_MESSAGE,
                            event.getMember().getAsMention()
                    )).setEphemeral(true).queue();
                    case exists -> event.reply(String.format(
                            RAFFLE_EXISTS_MESSAGE,
                            event.getMember().getAsMention(),
                            "/"
                    )).setEphemeral(true).queue();
                }
            }

        } else {
            event.reply(NO_RAFFLE_MESSAGE).setEphemeral(true).queue();
        }
    }

    @Override
    public String getName() {
        return "enter";
    }

    @Override
    public String getShortDescription() {
        return "enter an active raffle in this channel";
    }

    @Override
    public String getLongDescription() {
        return getShortDescription();
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash(getName(),getShortDescription());
    }
}
