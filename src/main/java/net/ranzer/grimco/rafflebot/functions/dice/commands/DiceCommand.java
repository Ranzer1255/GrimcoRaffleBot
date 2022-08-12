package net.ranzer.grimco.rafflebot.functions.dice.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.dice.DiceRoll;
import net.ranzer.grimco.rafflebot.functions.dice.DiceRollBuilder;

/**
 * <p> Credit where Credit is due:
 * <p>I borrowed the dice roller code from <a href="https://github.com/JoshCode/gilmore">GilmoreBot</a>
 *  by <a href=https://github.com/JoshCode>JoshCode</a>
 * @author Ranzer 
 *
 */
public class DiceCommand extends BotCommand implements Describable{

	private static final int MAX_MESSAGE_LENGTH = 1000;
	public static final String EXPRESSION = "expression";
	public static final String HIDDEN = "hidden";

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		String expression = event.getOption(EXPRESSION, OptionMapping::getAsString);

		DiceRoll diceRoll = DiceRollBuilder.newDiceRoll(expression);

		diceRoll.roll();

		String user = event.isFromGuild()?event.getMember().getEffectiveName():event.getUser().getName();

		event.reply(String.format("roll for %s: %s",
				user,
				(diceRoll.getLongReadout().length()< MAX_MESSAGE_LENGTH)? diceRoll.getLongReadout():
						diceRoll.getShortReadout()))
				.setEphemeral(event.getOption(HIDDEN,false,OptionMapping::getAsBoolean)).queue();

	}

	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	public String getName() {
		return "roll";
	}

	@Override
	public String getShortDescription() {
		return "Roll the Dice! Standard RPG dice format";
	}

	@Override
	public String getLongDescription() {
		return """
				Rolls dice using the standard RPG dice format
								
				example: !roll 1d20 + 5 [to hit]
				
				__further examples of other mechanics__
				**[comment]:** this is used to leave notes in your roll
				**2d20__kh__X:** keep the __**X**__ highest dice
				**2d20__kl__X:** keep the __**X**__ lowest dice
				**4d6__r<__X:** reroll every die lower than __**X**__
				**4d6__ro<__X:** reroll every die lower than __**X**__, but only once
				**1d10__!__:** exploding die - every time you roll a critical, add an extra die
				**5d6__t__X:** roll Dice and count the number of results above __**X**__
				(note: the target mechanic is incompatible with additive (ie: 1d20+5) based expressions, weird things happen if you combine the two)
				**5d6__!!__:** Shadowrun/Manapunk style compound exploding""";
	}
	
	@Override
	public String getUsage() {
		return String.format("`/%s ,basic rpg notation>",getName());
	}

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());

		rtn.addOption(OptionType.STRING, EXPRESSION,"1d20+5",true);
		rtn.addOption(OptionType.BOOLEAN, HIDDEN,"hide this roll?",false);

		return rtn;
	}
}
