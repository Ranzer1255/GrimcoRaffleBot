package net.ranzer.grimco.rafflebot.functions.excuses.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;

import java.util.Random;

public class ExcusesCommand extends BotCommand implements Describable {

	private final String[] intros = new String[]{
			"Sorry I can't come",
			"Please forgive my absence",
			"This is going to sound crazy but",
			"Get this!",
			"I can't go because",
			"I know you're going to hate me but",
			"I was minding my own business and boom!",
			"I feel terrible but",
			"I regretfully cannot attend,",
			"This is going to sound like an excuse byt"
	};
	private final String[] scapegoats = new String[]{
			"my nephew",
			"the ghost of Hitler",
			"the Pope",
			"my ex",
			"a high school marching band",
			"Dan Rather",
			"a sad clown",
			"the kid from Air Bud",
			"a professional cricket team",
			"my Tinder date"
	};
	private final String[] delays = new String[]{
			"just shit the bed",
			"died in front of me",
			"won't stop telling me knock knock jokes",
			"is having a nervous breakdown",
			"gave me syphilis",
			"poured lemonade in my gas tank",
			"stabbed me",
			"found my box of human teeth",
			"stole my bicycle",
			"posted my nudes on Instagram"
	};

	@Override
	protected boolean isApplicableToPM() {
		return true;
	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		event.reply(getExcuse()).queue();
	}

	@Override
	public Category getCategory() {
		return Category.MISC;
	}

	@Override
	public String getName() {
		return "excuse";
	}

	@Override
	public String getShortDescription() {
		return "generate an excuse for not showing up";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription();
	}

	private String getExcuse(){

		Random r = new Random(System.currentTimeMillis());
		String intro = intros[r.nextInt(intros.length)];
		String scapegoat = scapegoats[r.nextInt(scapegoats.length)];
		String delay = delays[r.nextInt(delays.length)];

		return intro + " " + scapegoat + " " + delay;

	}

	@Override
	public SlashCommandData getSlashCommandData() {
		return Commands.slash(getName(),getShortDescription());
	}
}
