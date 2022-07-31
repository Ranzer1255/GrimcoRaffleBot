package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Arrays;
import java.util.List;

public class ShuffleCommand extends AbstractMusicSubCommand {

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		event.reply("Shuffling...").queue();
		GuildPlayerManager.getPlayer(event.getGuild()).shuffle();
	}

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {
		GuildPlayerManager.getPlayer(event.getGuild()).shuffle();
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("shuffle", "mix");
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
