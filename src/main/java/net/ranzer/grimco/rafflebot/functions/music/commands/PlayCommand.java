package net.ranzer.grimco.rafflebot.functions.music.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayCommand extends AbstractMusicCommand implements Describable{

	@Override
	public void processPrefix(String[] args, MessageReceivedEvent event) {

		if(getAudioChannel(event.getMember())==null){
			event.getChannel().sendMessage("you must be in a voice channel before i can do anyting").queue();
		}
		GuildPlayer player = GuildPlayerManager.getPlayer(event.getGuild());
		if(args.length>0){
			player.queueID(StringUtil.arrayToString(Arrays.asList(args), " "));
		}
		
		if(!player.isConnected()){
			player.join(getAudioChannel(Objects.requireNonNull(event.getMember())));
		}
		
		player.start();
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("play","p");
	}

	@Override
	public String getShortDescription() {
		return "Start playing the first song in queue";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"this command does several things all in one.\n"
				+ "* if Caex isn't connected to a voice channel, he'll join you as per the `join` command\n\n"
				+ "* if supplied with a search string, he will search Youtube and add the song to the end of the queue as per the `queue` command\n\n"
				+ "* if caex is paused, he'll start playing again\n\n"
				+ "* last but not least, if he's stopped he'll start playing the first song in the queue";
	}
	
	@Override
	public String getUsage(Guild g) {
		return String.format("`%smusic %s [<search string>]`", getPrefix(g), getName());
	}
}
