package net.ranzer.grimco.rafflebot.functions.dice;

import net.ranzer.grimco.rafflebot.functions.dice.expressions.Addition;
import net.ranzer.grimco.rafflebot.functions.dice.expressions.Expression;

import java.util.ArrayList;

public abstract class DiceRoll {

	protected int total;
	protected String input;
	protected final ArrayList<Lexer.Token> tokens;
	protected String breakdown;
	protected Expression expr;
	private final boolean hasRolled = false;

	public DiceRoll(String input, ArrayList<Lexer.Token> tokens) {
		this.input = input;
		this.tokens = tokens;
	}

	public String getShortReadout() {
		if (!hasRolled) roll();
		return String.format("%s: (Message to long to display each die) = %s",input,getTotal());
	}

	public String getLongReadout() {
		if (!hasRolled) roll();
		return String.format("%s = %s", breakdown,getTotal());
	}

	public abstract String getTotal();

	public void roll() {
		this.expr = new Addition(tokens);
		breakdown = expr.getDescription();
		total = expr.getValue();
	}
}
