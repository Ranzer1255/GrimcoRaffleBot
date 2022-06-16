package net.ranzer.grimco.rafflebot.functions.dice;

import java.util.ArrayList;

public class TargetDiceRoll extends DiceRoll {

	public TargetDiceRoll(String input, ArrayList<Lexer.Token> tokens) {
		super(input, tokens);
	}

	@Override
	public String getTotal() {
		StringBuilder rtn = new StringBuilder();
		System.out.println(expr.getFails());

		if(total == 0){
			rtn.append("No Successes");
		} else if (total==1){
			rtn.append(String.format("%d Success", total));
		} else {
			rtn.append(String.format("%d Successes", total));
		}

		if (expr.getFails() != 0) {
			if (expr.getFails() == 1) {
				rtn.append(String.format(", %d Failure!", expr.getFails()));
			} else {
				rtn.append(String.format(", %d Failures!", expr.getFails()));
			}
		}


		return rtn.toString();
	}
}
