package net.ranzer.grimco.rafflebot.functions.music.events;

public class VolumeChangeEvent extends MusicEvent {

	private final int vol;
	
	public VolumeChangeEvent(int vol) {
		this.vol = vol;
	}
	
	public int getVol() {
		return vol;
	}
}
