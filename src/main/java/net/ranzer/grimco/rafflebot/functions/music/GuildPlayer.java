package net.ranzer.grimco.rafflebot.functions.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.ranzer.grimco.rafflebot.functions.music.events.*;
import net.ranzer.grimco.rafflebot.util.Logging;
import net.ranzer.grimco.rafflebot.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.*;

public class GuildPlayer extends AudioEventAdapter implements AudioSendHandler {

	private final AudioPlayerManager pm;
	private final AudioManager guildAM;
	private final TrackQueue queue;
	private final TrackLoader loader;
	private final AudioPlayer player;
	private AudioFrame lastFrame;
	private boolean loading;
	private boolean insertFlag;

	private final List<MusicEventListener> listeners = new ArrayList<>();
	private final MusicListener musicListener;//This may move or become a different implementation

	public GuildPlayer(AudioPlayerManager pm, Guild guild) {
		this.pm = pm;
		player = this.pm.createPlayer();
		player.addListener(this);
		queue = new TrackQueue();
		loader = new TrackLoader(queue, this.pm);
		guildAM = guild.getAudioManager();
		guildAM.setSendingHandler(this);
		musicListener = new MusicListener(guild);
		addListener(musicListener);

	}

	public void addListener(MusicEventListener listener) {

		listeners.add(listener);
	}

	public MusicListener getMusicListener() {
		return musicListener;
	}

	private void notifyOfEvent(MusicEvent event) {

		for (MusicEventListener l : listeners) {
			l.handleEvent(event);
		}

	}

	public boolean isConnected() {
		return guildAM.isConnected();
	}

	public void join(AudioChannel channel) {
		try {
			guildAM.openAudioConnection(channel);
		} catch (InsufficientPermissionException e) {
			notifyOfEvent(new PermErrorEvent(channel, e));
			return;
		}
		notifyOfEvent(new MusicJoinEvent(channel));
		
		channel.getJDA().addEventListener(new ListenerAdapter() {
			
			@Override
			public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
				channelCheck(event.getChannelLeft());
			}

			@Override
			public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
				channelCheck(event.getChannelLeft());
			}

			private void channelCheck(AudioChannel vc) {
				if (!vc.equals(guildAM.getConnectedChannel())) return;
				
				if (vc.getMembers().size()==1){
					stop(false);
					vc.getJDA().removeEventListener(this);
				}
			}
			
		});
	}

//	/**
//	 * searches Youtube for a song and adds it to the queue
//	 *
//	 * @param search
//	 */
//	public void queueSearch(String search) {
//
//		YouTubeSearcher yts = new YouTubeSearcher();
//		String videoID = yts.searchForVideo(search);
//
//		queueID(videoID);
//	}

	public void queueID(String songID) {
		System.out.println(songID);
		loading = true;
		pm.loadItem(songID, loader);
	}
	
//	public void insertSearch(String search){
//		insertFlag = true;
//		queueSearch(search);
//	}
	
	public void insertID(String songID){
		insertFlag = true;
		queueID(songID);
	}

	public TrackQueue getQueue() {
		return queue;
	}

	public MessageEmbed getQueueEmbed(int queueLength) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.setAuthor("Currently Playing", null, null);
		if(this.getPlayingTrack()!=null){
			eb.setTitle(
					String.format("%s\n"
					              +  "by %s",
					              this.getPlayingTrack().getInfo().title,
					              this.getPlayingTrack().getInfo().author),
					this.getPlayingTrack().getInfo().uri);
		} else {
			eb.setTitle("Nothing Playing",null);
		}

		if(this.getQueue().getQueue().isEmpty()){

			eb.setDescription("Nothing in Queue!");

		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Queue:\n");
			int i = 1;
			long runtime = 0;
			for (AudioTrack track : this.getQueue().getQueue()) {
				if(i>queueLength) break;
				sb.append(String.format("%d: [%s](%s)\n", i++, track.getInfo().title, track.getInfo().uri));
			}
			eb.setDescription(sb.toString());

			for(AudioTrack track:this.getQueue().getQueue()){
				runtime += track.getDuration();
			}
			if (this.getPlayingTrack()!=null) {
				runtime += this.getPlayingTrack().getDuration();
			}
			eb.setFooter("Estimated Runtime: " + StringUtil.calcTime(runtime / 1000), null);

		}
		return eb.build();
	}

	// music controls
	/**
	 * start the queue
	 */
	public void start() {
		if (player.isPaused()) {
			player.setPaused(false);
		}
		
		if (player.getPlayingTrack() == null) {
			playNext(false);
		}
	}

	/**
	 * Play the next song in the queue
	 */
	public void playNext(boolean skip) {
		while (loading) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {}
		}

		if (queue.isEmpty()) {
			stop(true);
			return;
		}
		if (player.getPlayingTrack()==null||skip)
			player.playTrack(queue.remove());
		
		player.setPaused(false);

	}

	/**
	 * stop playing and end the current song
	 */
	public void stop(boolean clearQueue) {
		player.setPaused(true);
		if(clearQueue) {
			player.stopTrack();
			clearQueue();
		}
		notifyOfEvent(new MusicStopEvent());
		close();
	}
	
	public void clearQueue(){
		queue.clear();		
	}
	
	public void close(){
		
		new Thread(){
			public void run() {
				guildAM.closeAudioConnection();
				interrupt();
			}
		}.start();
	}
	
	public void setVol(int vol) {
		if(vol <1 || vol > 150) throw new IllegalArgumentException("volume must be between 1 and 150");
		player.setVolume(vol);
	}

	public int getVol(){
		return player.getVolume();
	}

	public void pause() {
		notifyOfEvent(new MusicPausedEvent(player.isPaused()));
		player.setPaused(!player.isPaused());

	}

	public void shuffle() {
		notifyOfEvent(new ShuffleEvent());
		queue.shuffle();
		
	}

	public boolean isPlaying() {
		return !player.isPaused();
	}

	// AudioSendHandler methods
	@Override
	public boolean canProvide() {
		lastFrame = player.provide();
		return lastFrame != null;
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return ByteBuffer.wrap(lastFrame.getData());
	}

	@Override
	public boolean isOpus() {
		return true;
	}

	// AudioEventHandler methods
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		switch (endReason) {
			//this is handled in onTrackException()
			case LOAD_FAILED, FINISHED -> playNext(true);
			case REPLACED -> notifyOfEvent(new MusicSkipEvent(track));
			default -> {
			} //no-op
		}
	}
	
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		notifyOfEvent(new LoadFailedEvent(exception));
	}
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		notifyOfEvent(new MusicStartEvent(track));
	}

	/**
	 * Loads tracks into the queue for the guild
	 * 
	 * @author Ranzer
	 *
	 */
	private class TrackLoader implements AudioLoadResultHandler {

		TrackQueue queue;

		public TrackLoader(TrackQueue queue, AudioPlayerManager playerManager) {
			this.queue = queue;
		}

		@Override
		public void trackLoaded(AudioTrack track) {
			if(insertFlag){
				queue.insert(track);
				insertFlag=false;
			} else {
				queue.add(track);
			}
			notifyOfEvent(new MusicLoadEvent(track));
			loading = false;

		}

		@Override
		public void playlistLoaded(AudioPlaylist playlist) {
			notifyOfEvent(new PlaylistLoadEvent(playlist));
			
			for (AudioTrack track : playlist.getTracks()) {
				queue.add(track);
			}
			loading = false;

		}

		@Override
		public void noMatches() {
			Logging.debug("No match found in search");
			notifyOfEvent(new NoMatchEvent());
			loading = false;
		}

		@Override
		public void loadFailed(FriendlyException exception) {
			Logging.debug(exception.getMessage());
			notifyOfEvent(new LoadFailedEvent(exception));
			loading =false;
			
		}

	}

	/**
	 * handles queuing of tracks to be played
	 * 
	 * @author Ranzer
	 *
	 */
	public static class TrackQueue {

		Queue<AudioTrack> queue = new LinkedList<>();

		public List<AudioTrack> getQueue() {
			return (LinkedList<AudioTrack>) queue;
		}

		public void shuffle() {
			Collections.shuffle((List<?>) queue);
			
		}

		/**
		 * adds one track to the queue to be played.
		 * 
		 * @param track
		 *            track to be added to the queue
		 */
		public void add(AudioTrack track) {
			queue.add(track);
		}
		
		/**
		 * inserts track at the head of the queue
		 * 
		 * @param track
		 * 			track to be added
		 */
		public void insert(AudioTrack track){
			((LinkedList<AudioTrack>) queue).addFirst(track);
		}
		
		/**
		 * clears the queue
		 */
		public void clear() {
			queue.clear();

		}

		/**
		 * 
		 * @return next track in the queue using FIFO order
		 */
		public AudioTrack remove() {
			return queue.remove();
		}

		public boolean isEmpty() {
			Logging.debug(String.format("value of queue.isEmpty(): %s", queue.isEmpty()));
			return queue.isEmpty();
		}
	}

	public AudioTrack getPlayingTrack() {
		return player.getPlayingTrack();

	}
}
