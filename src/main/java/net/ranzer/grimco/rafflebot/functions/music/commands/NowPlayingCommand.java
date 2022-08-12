package net.ranzer.grimco.rafflebot.functions.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class NowPlayingCommand extends AbstractMusicSubCommand{

	@Override
	public void processSlash(SlashCommandInteractionEvent event) {
		GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());
		if (!gp.isConnected()) {
			event.reply("i'm not playing anything! why not load up some music?").setEphemeral(true).queue();
		} else if (!gp.isPlaying()) {
			event.reply("Music is Paused").setEphemeral(true).queue();
		} else {
			event.replyEmbeds(getNowPlayingEmbed(gp.getPlayingTrack()).build()).setEphemeral(true).queue();
		}
	}

	@NotNull
	private EmbedBuilder getNowPlayingEmbed(AudioTrack playing) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.setAuthor("Now Playing");
		eb.setTitle(
			String.format("%s\n",
				playing.getInfo().title
			),
			playing.getInfo().uri
		);
		eb.setDescription(StringUtil.playingBar(playing.getPosition(), playing.getDuration()));
		eb.setFooter("by "+ playing.getInfo().author,null);
		return eb;
	}

	@Override
	public String getLongDescription() {
		return super.getLongDescription()
				+ "also shows a neat graphical representation of time remaining";
	}
	@Override
	public String getShortDescription() {
		return "Shows the current playing track and time remaining";
	}

	@Override
	public String getName() {
		return "now-playing";
	}

}
