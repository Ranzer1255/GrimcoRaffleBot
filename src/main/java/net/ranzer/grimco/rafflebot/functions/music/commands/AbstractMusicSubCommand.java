package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class AbstractMusicSubCommand extends AbstractMusicCommand{

	protected final String     SCO_SONG = "song";
	protected final OptionData SONG = new OptionData(
			OptionType.STRING,
			SCO_SONG,
			"Song URL, ID, or if search is true, song title",
			true
	);

	protected final String     SCO_SEARCH_ID = "search";
	protected final OptionData SEARCH = new OptionData(
			OptionType.BOOLEAN,
			SCO_SEARCH_ID,
			"To search for a song enter \"true\"",
			false
	);

	protected final OptionData INSERT = new OptionData(
			OptionType.BOOLEAN,
			"insert",
			"insert these songs at the head of the queue?",
			false
	);


	protected SubcommandData getSubCommandData() {
		return new SubcommandData(getName(),getShortDescription());
	}

	@Override
	public final SlashCommandData getSlashCommandData() { //children of this class should *not* implement this method.
		return null;
	}
}
