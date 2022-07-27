package net.ranzer.grimco.rafflebot.functions.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.ranzer.grimco.rafflebot.util.Logging;

import java.util.HashMap;
import java.util.Map;

public class GuildPlayerManager {

	private static final Map<Guild, GuildPlayer> players = new HashMap<>();
	private static final AudioPlayerManager pm = intPlayerManager();
	
	public static GuildPlayer getPlayer(Guild k){
		GuildPlayer rtn = players.get(k);
		if(rtn == null){
			rtn = new GuildPlayer(pm, k);
			players.put(k, rtn);
		}
		return rtn;
	}

	//construction helper methods
	private static AudioPlayerManager intPlayerManager() {
		Logging.info("creating PlayerManager");
		AudioPlayerManager rtn = new DefaultAudioPlayerManager();
		rtn.getConfiguration().setResamplingQuality(ResamplingQuality.HIGH);
		rtn.registerSourceManager(new YoutubeAudioSourceManager());
		return rtn;
	}
}
