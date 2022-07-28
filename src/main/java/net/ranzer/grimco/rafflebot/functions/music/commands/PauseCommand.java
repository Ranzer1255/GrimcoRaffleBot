package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Collections;
import java.util.List;

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

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		if(notInSameAudioChannel(event.getMember())){
			event.getChannel().sendMessage("You must be listening to pause.").queue();
			return;
		}
		process(event.getGuild());
	}

	private void process(Guild guild) {
		GuildPlayerManager.getPlayer(guild).pause();
	}

	@Override
	public List<String> getAlias() {
		return Collections.singletonList("pause");
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

	@Override
	public SlashCommandData getSlashCommandData() {
		return super.getSlashCommandData();
	}
}
