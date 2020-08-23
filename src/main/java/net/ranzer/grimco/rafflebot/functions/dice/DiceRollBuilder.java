package net.ranzer.grimco.rafflebot.functions.dice;

import java.util.ArrayList;

public class DiceRollBuilder {

	public static DiceRoll newDiceRoll(String input){
		ArrayList<Lexer.Token> tokens;

		tokens = Lexer.lex(input);

		for(Lexer.Token t:tokens){
			if(t.type== Lexer.TokenType.TARGETNUMBER){
				return new TargetDiceRoll(input, tokens);
			}
		}
		return new SumDiceRoll(input, tokens);
	}
}
