package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;

import java.util.Arrays;
import java.util.List;

public class PlaylistCommand extends AbstractMusicSubCommand implements Describable {

	private final OptionData PLAYLIST_ID = new OptionData(
			OptionType.STRING, "playlist_id",
			"ID or URL of the song Playlist",
			true
	);

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		boolean insert = event.getOption(INSERT.getName(), false, OptionMapping::getAsBoolean);
		String playlistID = event.getOption(PLAYLIST_ID.getName(),OptionMapping::getAsString);

		if (insert){
			GuildPlayerManager.getPlayer(event.getGuild()).insertID(playlistID);
			event.reply("inserting ahead of the line...").setEphemeral(true).queue();
		} else {
			GuildPlayerManager.getPlayer(event.getGuild()).queueID(playlistID);
			event.reply("adding...").setEphemeral(true).queue();
		}
	}

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		if (args.length == 0){
			event.getChannel().sendMessage("what was the playlist ID again?").queue();
			return;
		}
		if (args[0].charAt(0)=='i' && args.length>1){
			GuildPlayerManager.getPlayer(event.getGuild()).insertID(args[1]);
		} else {
			GuildPlayerManager.getPlayer(event.getGuild()).queueID(args[0]);
		}

		
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

	@Override
	protected SubcommandData getSubCommandData() {
		SubcommandData rtn = super.getSubCommandData();
		rtn.addOptions(PLAYLIST_ID,INSERT);
		return rtn;
	}
}
