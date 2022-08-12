package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

public class PauseCommand extends AbstractMusicSubCommand implements Describable{

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		if(notInSameAudioChannel(event.getMember())){
			event.reply("You must be listening to pause.").setEphemeral(true).queue();
			return;
		}
		event.reply("pausing...").setEphemeral(true).queue();
		process(event.getGuild());

	}

	private void process(Guild guild) {
		GuildPlayerManager.getPlayer(guild).pause();
	}

	@Override
	public String getName() {
		return "pause";
	}

	@Override
	public String getShortDescription() {
		return "Pauses the currently playing song";
	}
	
	@Override
	public String getLongDescription() {
		return getShortDescription()+"\n\n"
				+ "to resume: call this command again, or call `play`\n\n"
				+ "you must be in the same voice channel to pause music.";
	}
}
