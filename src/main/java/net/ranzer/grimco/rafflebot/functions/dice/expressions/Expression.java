package net.ranzer.grimco.rafflebot.functions.dice.expressions;

import net.ranzer.grimco.rafflebot.functions.dice.Lexer;

import java.util.ArrayList;

/**
 * Abstract class for all expressions. Has a list of tokens (atomic, die, addition, etc), a value
 * (the end result, e.g. 19) a description (the explanation, e.g. "1d20")
 *
 * @author Tijmen
 */
public abstract class Expression {

	public ArrayList<Lexer.Token> tokens;
	protected int value;
	protected StringBuilder description = new StringBuilder();

	public Expression(ArrayList<Lexer.Token> tokens) {
		this.tokens = tokens;
	}

	public String getDescription(){
		return description.toString();
	}

	public int getValue() {
		return value;
	}
}