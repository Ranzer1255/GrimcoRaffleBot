package net.ranzer.grimco.rafflebot.functions.dice;

import java.util.ArrayList;

public class SumDiceRoll extends DiceRoll {

	public SumDiceRoll(String input, ArrayList<Lexer.Token> tokens) {
		super(input, tokens);
	}

	@Override
	public String getTotal() {
		return String.valueOf(total);
	}
}