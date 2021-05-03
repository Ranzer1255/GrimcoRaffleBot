package net.ranzer.grimco.rafflebot.commands;

import java.awt.*;

public enum Category {
			
	ADMIN("Admin", Color.RED),
	RAFFLE("Raffle",Color.ORANGE),
	XP("Xp", Color.blue),
	MISC("Misc", Color.blue);

    public final String NAME;
	public final Color COLOR;
	
	Category(String name, Color color){
		NAME = name;
		COLOR =color;
	}
	
	@Override
	public String toString(){
		return NAME;
	}

}
