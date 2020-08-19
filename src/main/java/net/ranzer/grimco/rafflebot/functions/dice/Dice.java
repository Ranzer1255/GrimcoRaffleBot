package net.ranzer.grimco.rafflebot.functions.dice;

import net.ranzer.grimco.rafflebot.functions.dice.expressions.Addition;

import java.util.ArrayList;


public class Dice {

	private final ArrayList<Lexer.Token> tokens;
	private String breakdown;

	public Dice(String str) {
		tokens = Lexer.lex(str);

	}

	public String getBreakdown() {
		return breakdown;
	}

	public int roll() {
		Addition expr = new Addition(tokens);
		breakdown = expr.description;
		return expr.value;
	}
}	