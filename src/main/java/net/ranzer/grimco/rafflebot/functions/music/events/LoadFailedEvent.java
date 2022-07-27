package net.ranzer.grimco.rafflebot.functions.music.events;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

public class LoadFailedEvent extends MusicEvent {
	
	final FriendlyException ex;

	public LoadFailedEvent(FriendlyException exception) {
		ex=exception;
	}
	
	public FriendlyException getException(){
		return ex;
	}

}
