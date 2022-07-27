package net.ranzer.grimco.rafflebot.functions.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.ranzer.grimco.rafflebot.commands.Describable;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayer;
import net.ranzer.grimco.rafflebot.functions.music.GuildPlayerManager;
import net.ranzer.grimco.rafflebot.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class QueueCommand extends AbstractMusicCommand implements Describable {

	private static final int SHOW_QUEUE_LENGTH = 10;

	@Override
	public void processPrefix(String[] args, net.dv8tion.jda.api.events.message.MessageReceivedEvent event) {
		if (args.length<1) {
			GuildPlayer gp = GuildPlayerManager.getPlayer(event.getGuild());
			event.getChannel().sendMessageEmbeds(getQueueEmbed(gp)).queue();
			
		} else {
			GuildPlayerManager.getPlayer(event.getGuild()).queueID(StringUtil.arrayToString(Arrays.asList(args), " "));
		}
	
	}

	public static MessageEmbed getQueueEmbed(GuildPlayer gp) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.setAuthor("Currently Playing", null, null);
		if(gp.getPlayingTrack()!=null){
			eb.setTitle(
					String.format("%s\n"
									+  "by %s",
							gp.getPlayingTrack().getInfo().title,
							gp.getPlayingTrack().getInfo().author),
					gp.getPlayingTrack().getInfo().uri);
		} else {
			eb.setTitle("Nothing Playing",null);
		}

		if(gp.getQueue().getQueue().isEmpty()){

			eb.setDescription("Nothing in Queue!");

		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Queue:\n");
			int i = 1;
			long runtime = 0;
			for (AudioTrack track : gp.getQueue().getQueue()) {
				if(i>SHOW_QUEUE_LENGTH) break;
				sb.append(String.format("%d: [%s](%s)\n", i++, track.getInfo().title, track.getInfo().uri));
			}
			eb.setDescription(sb.toString());

			for(AudioTrack track:gp.getQueue().getQueue()){
				runtime += track.getDuration();
			}
			if (gp.getPlayingTrack()!=null) {
				runtime += gp.getPlayingTrack().getDuration();
			}
			eb.setFooter("Estimated Runtime: "+StringUtil.calcTime(runtime/1000), null);

		}
		return eb.build();
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("queue","add");
	}

	@Override
	public String getShortDescription() {
		return "Add song to the play queue";
	}
	
	@Override
	public String getLongDescription() {
		return super.getLongDescription()+
				"When not given a search string, this command will list the next 10 songs currently in the queue";
	}
	
	@Override
	public String getUsage(Guild g) {
		return String.format("`%smusic %s [<search string>]`", getPrefix(g),getName());
	}
}
