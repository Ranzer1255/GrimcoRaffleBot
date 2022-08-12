package net.ranzer.grimco.rafflebot.functions.raffle.commands.run;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.raffle.commands.AbstractRaffleCommand;

public class RaffleWithdrawCommand extends AbstractRaffleCommand implements Describable {

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		if (raffles.containsKey(event.getTextChannel().getId())) {
			raffles.get(event.getTextChannel().getId()).removeEntry(event.getMember());
			event.reply(String.format(
					USER_WITHDRAW,
					event.getMember().getAsMention()
			                         )).queue();
		}
	}


	@Override
	public String getName() {
		return "withdraw";
	}

	@Override
	public String getShortDescription() {
		return "remove your name from the raffle";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription();
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		return Commands.slash(getName(), getShortDescription());
	}
}
