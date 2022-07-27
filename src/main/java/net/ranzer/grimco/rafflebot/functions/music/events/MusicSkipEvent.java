package net.ranzer.grimco.rafflebot.functions.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicSkipEvent extends MusicEvent {

	private final AudioTrack skippedTrack;
	public MusicSkipEvent(AudioTrack track) {
		skippedTrack = track;
	}
	
	public AudioTrack getSkippedTrack() {
		return skippedTrack;
	}
}
