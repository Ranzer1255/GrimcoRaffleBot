package net.ranzer.grimco.rafflebot.functions.dice;

import net.ranzer.grimco.rafflebot.functions.dice.expressions.Addition;

import java.util.ArrayList;

public class TargetDiceRoll extends DiceRoll {
	private int total;

	public TargetDiceRoll(String input, ArrayList<Lexer.Token> tokens) {
		super(input, tokens);
	}

	@Override
	public String getTotal() {
		return String.format("%d Successes", total);
	}

	@Override
	public int roll() {
		Addition expr = new Addition(tokens);
		breakdown = expr.description;
		total = expr.value;
		return total;
	}
}
