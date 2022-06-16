package net.ranzer.grimco.rafflebot.functions.dice.expressions;
import net.ranzer.grimco.rafflebot.functions.dice.Lexer;

import java.util.ArrayList;


public class Multiplication extends Expression {

	public Multiplication(ArrayList<Lexer.Token> tokens) {
		super(tokens);

		boolean twoMultiplications = false;

		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).type == Lexer.TokenType.MULTIPLICATION) {
				twoMultiplications = true;
				Multiplication a = new Multiplication(new ArrayList<>(tokens.subList(0, i)));
				Multiplication b = new Multiplication(new ArrayList<>(tokens.subList(i + 1, tokens.size())));
				value = a.value * b.value;
				description=a.description.append(" * ").append(b.description);
			}
		}

		if (!twoMultiplications) {
			Negation negation = new Negation(tokens);
			value = negation.value;
			fails = negation.fails;
			description = negation.description;
		}

	}

}