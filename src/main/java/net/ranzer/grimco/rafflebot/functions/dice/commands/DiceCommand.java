package net.ranzer.grimco.rafflebot.functions.dice.commands;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.dice.Dice;

/**
 * <p> Credit where Credit is due:
 * <p>I borrowed the dice roller code from <a href="https://github.com/JoshCode/gilmore">GilmoreBot</a>
 *  by <a href=https://github.com/JoshCode>JoshCode</a>
 * @author Ranzer 
 *
 */
public class DiceCommand extends BotCommand implements Describable{

	private static final int MAX_MESSAGE_LENGHT = 1000;

	@Override
	public void process(String[] args,  MessageReceivedEvent event) {

		if(!(args.length<1)){
			invalidUsage(event.getGuild());
		}
		String expression = "";
		
		for (int i = 0; i < args.length; i++) {
			expression+=" " + args[i];
		}
		Dice dice = new Dice(expression.substring(1));
		
		int result = dice.roll();
		
		
		event.getChannel().sendMessage(String.format("%s: %s = %d", 
				event.getAuthor().getAsMention(), 
				(dice.getBreakdown().length()<MAX_MESSAGE_LENGHT)?dice.getBreakdown():
					"(Message to long to display each die)"+expression.substring(1), 
				result))
		.queue();
		
		
		
	}

	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("roll","r");
	}

	@Override
	public String getShortDescription() {
		return "Roll the Dice! Standard RPG dice format";
	}

	@Override
	public String getLongDescription() {
		return "Rolls dice using the standard RPG dice format\n"
				+ "Usage: ![roll|dice] [expression]\n"
				+ "for example: !roll 1d20 + 5 [to hit]\n"
				+ "[comment]: this is ignored\n"
				+ "2d20khX: keep the X highest dice\n"
				+ "2d20klX: keep the X lowest dice\n"
				+ "4d6r<X: reroll every die lower than X\n"
				+ "4d6ro<X: reroll every die lower than X, but only once\n"
				+ "1d10!: exploding die - every time you roll a crit, add an extra die";
	}
	
	@Override
	public String getUsage(Guild g) {
		return "`"+getPrefix(g)+getName()+" <basic rpg notation>`";
	}

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}
}
