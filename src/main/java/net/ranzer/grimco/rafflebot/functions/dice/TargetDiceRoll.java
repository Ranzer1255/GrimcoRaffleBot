package net.ranzer.grimco.rafflebot.functions.dice;

import java.util.ArrayList;

public class TargetDiceRoll extends DiceRoll {

	public TargetDiceRoll(String input, ArrayList<Lexer.Token> tokens) {
		super(input, tokens);
	}

	@Override
	public String getTotal() {
		if(total == 0){
			return "No Successes";
		} else if (total==1){
			return String.format("%d Success", total);
		} else {
			return String.format("%d Successes", total);
		}
	}
}
