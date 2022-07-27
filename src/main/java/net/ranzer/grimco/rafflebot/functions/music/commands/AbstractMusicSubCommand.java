package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class AbstractMusicSubCommand extends AbstractMusicCommand{
	protected SubcommandData getSubCommandData() {
		return new SubcommandData(getName(),getShortDescription());
	}
}
