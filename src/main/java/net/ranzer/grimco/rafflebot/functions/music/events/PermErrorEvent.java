package net.ranzer.grimco.rafflebot.functions.music.events;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class PermErrorEvent extends MusicEvent {

	private final AudioChannel vc;
	private final InsufficientPermissionException e;
	
	
	public PermErrorEvent(AudioChannel channel, InsufficientPermissionException e) {
		vc = channel;
		this.e = e;
	}
	
	public AudioChannel getAudioChannel(){
		return vc;
	}
	
	public InsufficientPermissionException getException(){
		return e;
	}

}
