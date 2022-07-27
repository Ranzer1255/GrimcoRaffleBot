package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Arrays;
import java.util.List;

public class PlaylistCommand extends AbstractMusicCommand implements Describable {

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		if (args.length == 0){
			event.getChannel().sendMessage("what was the playlist ID again?").queue();
			return;
		}
		if (args[0].charAt(0)=='i' && args.length>1){
			GuildPlayerManager.getPlayer(event.getGuild()).insertID(args[1]);
		}
		GuildPlayerManager.getPlayer(event.getGuild()).queueID(args[0]);

		
	}
	
	@Override
	public List<String> getAlias() {
		return Arrays.asList("playlist", "pl");
	}

	@Override
	public String getShortDescription() {
		return "skips the search process and inserts a Youtube video or playlist code into the playlist";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription() +
		"input a URL, video code, or playlist code \n\n"
		+ "examples:\n"
		+ "`https://www.youtube.com/watch?v=dQw4w9WgXcQ`\n"
		+ "`dQw4w9WgXcQ`\n"
		+ "`PL7atuZxmT954bCkC062rKwXTvJtcqFB8i`";
	}
	
	@Override
	public String getUsage(Guild g) {
		return String.format("`%smusic %s <video URL or Code>`", getPrefix(g),getName());
	}
}
