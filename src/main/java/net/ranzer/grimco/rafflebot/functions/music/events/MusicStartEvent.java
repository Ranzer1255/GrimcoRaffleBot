package net.ranzer.grimco.rafflebot.functions.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MusicStartEvent extends MusicEvent {

	private final AudioTrack song;
	
	public MusicStartEvent(AudioTrack track) {
		song=track;
		
	}

	public AudioTrack getSong(){
		return song;
	}
}
