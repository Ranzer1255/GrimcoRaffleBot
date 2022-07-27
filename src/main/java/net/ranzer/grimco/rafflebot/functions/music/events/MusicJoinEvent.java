package net.ranzer.grimco.rafflebot.functions.music.events;

import net.dv8tion.jda.api.entities.AudioChannel;

public class MusicJoinEvent extends MusicEvent {

	final private AudioChannel channelJoined;
	
	public MusicJoinEvent(AudioChannel channel) {
		channelJoined=channel;
	}

	public AudioChannel getChannelJoined() {
		return channelJoined;
	}
}
