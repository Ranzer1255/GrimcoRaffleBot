package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

public class ShuffleCommand extends AbstractMusicSubCommand {

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		event.reply("Shuffling...").queue();
		GuildPlayerManager.getPlayer(event.getGuild()).shuffle();
	}

	@Override
	public String getName() {
		return "shuffle";
	}

	@Override
	public String getShortDescription() {
		return "shuffle shuffle!";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"randomly shuffles the order of the queue";
	}
}
