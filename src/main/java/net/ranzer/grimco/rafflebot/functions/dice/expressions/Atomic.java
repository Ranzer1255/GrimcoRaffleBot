package net.ranzer.grimco.rafflebot.functions.dice.expressions;

import net.ranzer.grimco.rafflebot.functions.dice.Lexer;

import java.util.ArrayList;


public class Atomic extends Expression {

	public Atomic(ArrayList<Lexer.Token> tokens) {
		super(tokens);
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).type == Lexer.TokenType.ATOMIC) {
				value = Integer.parseInt(tokens.get(i).data);
			}
			description += tokens.get(i).data;
		}
	}

}
