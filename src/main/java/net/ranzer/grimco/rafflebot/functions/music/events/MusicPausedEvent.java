package net.ranzer.grimco.rafflebot.functions.music.events;

public class MusicPausedEvent extends MusicEvent {

	private final boolean paused;
	
	public MusicPausedEvent(boolean paused) {
		this.paused=paused;
	}
	
	public boolean getPaused(){
		return paused;
	}
}
