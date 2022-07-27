package net.ranzer.grimco.rafflebot.functions.music;

import net.ranzer.grimco.rafflebot.functions.music.events.MusicEvent;

public interface MusicEventListener {

	void handleEvent(MusicEvent event);
}
