package net.ranzer.grimco.rafflebot.functions.reactionVotes;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.BotCommand;
import net.ranzer.grimco.rafflebot.commands.Category;
import net.ranzer.grimco.rafflebot.commands.Describable;

import java.util.ArrayList;
import java.util.List;

public class ReactionVoteCommand extends BotCommand implements Describable {

	@Override
	protected boolean isApplicableToPM() {
		return false;
	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		event.getOptionsByType(OptionType.STRING);

	}

	@Override
	public Category getCategory() {
		return null;
	}

	@Override
	public String getName() {
		return "vote";
	}

	@Override
	public String getShortDescription() {
		return "Start a poll with up to 4 options";
	}

	@Override
	public String getLongDescription() {
		return null;
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(), getShortDescription());

		for (int i = 0; i < 4; i++) {
			rtn.addOption(
					OptionType.STRING,
					"Option_"+i,
					"Option "+i,
					i<2);
		}

		return rtn;
	}


}
