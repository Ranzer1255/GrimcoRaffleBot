package net.ranzer.grimco.rafflebot.functions.dice.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.functions.dice.DiceRoll;
import net.ranzer.grimco.rafflebot.functions.dice.DiceRollBuilder;

import java.util.Arrays;
import java.util.List;

public class FateDiceCommand extends BotCommand {

	private static final int MAX_MESSAGE_LENGTH = 1000;

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}

	@Override
	protected void process(String[] args, MessageReceivedEvent event) {
		DiceRoll diceRoll = DiceRollBuilder.newDiceRoll("4dF");

		diceRoll.roll();

		event.getChannel().sendMessage(String.format("%s: %s",
				event.getAuthor().getAsMention(),
				(diceRoll.getLongReadout().length()< MAX_MESSAGE_LENGTH)? diceRoll.getLongReadout():
						diceRoll.getShortReadout()))
				.queue();
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("fate", "f");
	}
}
