package net.ranzer.grimco.rafflebot.functions.music.events;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;

public class PlaylistLoadEvent extends MusicEvent {

	private final AudioPlaylist list;
	
	public PlaylistLoadEvent(AudioPlaylist list) {
		this.list=list;
	}
	
	public AudioPlaylist getList() {
		return list;
	}
}
