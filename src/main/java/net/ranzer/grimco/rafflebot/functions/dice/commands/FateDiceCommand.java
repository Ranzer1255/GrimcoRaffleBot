package net.ranzer.grimco.rafflebot.functions.dice.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.functions.dice.DiceRoll;
import net.ranzer.grimco.rafflebot.functions.dice.DiceRollBuilder;

public class FateDiceCommand extends BotCommand {

	private static final int MAX_MESSAGE_LENGTH = 1000;

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		DiceRoll diceRoll = DiceRollBuilder.newDiceRoll("4dF");

		diceRoll.roll();

		String user = event.isFromGuild()?event.getMember().getEffectiveName():event.getUser().getName();

		event.reply(String.format("%s: %s",
				user,
				(diceRoll.getLongReadout().length()< MAX_MESSAGE_LENGTH)? diceRoll.getLongReadout():
						diceRoll.getShortReadout()))
				.queue();

	}

	@Override
	public String getName() {
		return "fate_roll";
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		return Commands.slash(getName(),"shorthand for `/roll 4df`");
	}
}
