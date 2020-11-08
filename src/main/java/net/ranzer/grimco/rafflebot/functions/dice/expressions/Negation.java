package net.ranzer.grimco.rafflebot.functions.dice.expressions;

import net.ranzer.grimco.rafflebot.functions.dice.Lexer;

import java.util.ArrayList;


public class Negation extends Expression {

	public Negation(ArrayList<Lexer.Token> tokens) {
		super(tokens);

		boolean twoNegations = false;

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).type == Lexer.TokenType.NEGATION) {
				twoNegations = true;
				Negation a = new Negation(new ArrayList<>(tokens.subList(i + 1, tokens.size())));
				value = -a.value;
				description = a.description.insert(0,"-");
			}
		}

		if (!twoNegations) {
			// We don't know whether this term is a die roll or just a constant number
			for (Lexer.Token token : tokens) {
				if (token.type == Lexer.TokenType.ATOMIC) {
					Atomic atomic = new Atomic(tokens);
					value = atomic.value;
					description = atomic.description;
					break;
				} else if (token.type == Lexer.TokenType.DIE || token.type == Lexer.TokenType.FATEDIE) {
					Die die = new Die(tokens);
					value = die.value;
					description = die.description;
					break;
				}
			}

		}

	}

}