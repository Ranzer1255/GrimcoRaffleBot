package net.ranzer.grimco.rafflebot.functions.dice;

import java.util.ArrayList;

public abstract class DiceRoll {
	protected String input;
	protected final ArrayList<Lexer.Token> tokens;
	protected String breakdown;

	public DiceRoll(String input, ArrayList<Lexer.Token> tokens) {
		this.input = input;
		this.tokens = tokens;
	}

	public String getShortReadout() {
		return String.format("%s: (Message to long to display each die) = %s",input,getTotal());
	}

	public String getLongReadout() {
		return String.format("%s = %s", breakdown,getTotal());
	}

	public String getBreakdown() {
		return breakdown;
	}

	public abstract String getTotal();

	public abstract int roll();
}
