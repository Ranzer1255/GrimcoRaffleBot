package net.ranzer.grimco.rafflebot.functions.dice.expressions;

import net.ranzer.grimco.rafflebot.functions.dice.Lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Die extends Expression {

	@SuppressWarnings("FieldCanBeLocal")
	private final int MAX_DICE = 1000;

	private int numberOfDice = 0;
	private int numberOfFaces = 1;
	private boolean fate = false;
	private int keepNumber = 0;
	private boolean keepHighest = false;
	private boolean keepLowest = false;
	private boolean exploding = false;
	private boolean compounding = false;
	private int rerollNumber = 0;
	private boolean rerollOnce = false;
	private boolean reroll = false;
	private int critSuccessNumber = numberOfFaces;
	private int critFailNumber = 1;
	private boolean targetRoll = false; //this value determines if this is a target matching role or a summing roll
	private int targetNumber = -1;

	/**
	 * Sets up a die with the selected options. Then roll the dice and add the explanation to the
	 * description.
	 */
	public Die(ArrayList<Lexer.Token> tokens) {
		super(tokens);

//		StringBuilder descriptionAddition = new StringBuilder();

		for (Lexer.Token token : tokens) {
			description.append(token.data);
			processTriggers(token);
		}

		// The reroll cooldown. Only used for rerollOnce
		boolean justRerolled = false;

		Random random = new Random();
		List<Integer> rolls = new ArrayList<>();
		List<String> rollDescriptions = new ArrayList<>();

		// Roll the dice, add extra where necessary
		// will never roll more than MAX_DICE
		int rerolledDice = 0;
		for (int i = 0; i < Math.min(numberOfDice, MAX_DICE); i++) {
			int roll = random.nextInt(numberOfFaces) + 1;
			String rollDescription = "";
			if (!fate)
				rollDescription = String.valueOf(roll);
			else {
				switch (roll){
					case 1:
						rollDescription = "-";
						roll = -1;
						break;
					case 2:
						rollDescription = "b";
						roll = 0;
						break;
					case 3:
						rollDescription = "+";
						roll = 1;
						break;
				}
			}

			// Roll explodes, add one more die
			if (exploding && roll >= critSuccessNumber) {
				numberOfDice++;
				rollDescription += "!";
			}

			if (compounding && roll>= critSuccessNumber){
				while (roll%numberOfFaces==0) {
					roll += random.nextInt(numberOfFaces) + 1;
				}
				rollDescription = roll +"!!";
			}

			// Roll is crit success
			if (roll >= critSuccessNumber && !targetRoll) {
				rollDescription = "**" + rollDescription + "**";
			}

			if (targetRoll && roll >= targetNumber){
				rollDescription = "**" + rollDescription + "**";
			}

			// Roll is crit fail
			if (roll <= critFailNumber) {
				rollDescription = "*" + rollDescription + "*";
			}

			// Roll under rerollOnceNumber
			if (rerollOnce && roll <= rerollNumber && !justRerolled) {
				numberOfDice++;
				rerolledDice++;
				justRerolled = true;
				roll = 0;
				rollDescription = "~~" + rollDescription + "~~";
			}

			// Reset the reroll cooldown
			else if (rerollOnce && justRerolled) {
				justRerolled = false;
			}

			// Roll under rerollNumber
			if (reroll && roll <= rerollNumber) {
				numberOfDice++;
				rerolledDice++;
				roll = 0;
				rollDescription = "~~" + rollDescription + "~~";
			}

			rolls.add(roll);
			rollDescriptions.add(rollDescription);
		}

		// Drop the higher dice
		if (keepLowest) {
			for (int j = 0; j < rolls.size() - keepNumber - rerolledDice; j++) {
				int highest = 1, highestIndex = 0;
				// Find the highest die
				for (int i = 0; i < rolls.size(); i++) {
					if (highest < rolls.get(i) && rolls.get(i) > 0) {
						highest = rolls.get(i);
						highestIndex = i;
					}
				}
				// Destroy it
				rolls.set(highestIndex, 0);
				String desc = rollDescriptions.get(highestIndex);
				if (desc.length() < 2 || !"~~".equals(desc.substring(0, 2))) {
					rollDescriptions.set(highestIndex, "~~" + desc + "~~");
				}
			}
		}

		// Drop the lower dice
		if (keepHighest) {
			for (int j = 0; j < rolls.size() - keepNumber - rerolledDice; j++) {
				int lowest = numberOfFaces, lowestIndex = 0;
				// Find the lowest die
				for (int i = 0; i < rolls.size(); i++) {
					if (lowest > rolls.get(i) && rolls.get(i) > 0) {
						lowest = rolls.get(i);
						lowestIndex = i;
					}
				}
				// Destroy it
				rolls.set(lowestIndex, 0);
				String desc = rollDescriptions.get(lowestIndex);
				if (desc.length() < 2 || !"~~".equals(desc.substring(0, 2))) {
					rollDescriptions.set(lowestIndex, "~~" + desc + "~~");
				}
			}
		}

		// Construct the final variables (value and description) in summing mode^r 1d20+5

		if (targetRoll) { //target roll
			value = 0;
			for (int roll : rolls){
				if (roll>=targetNumber) value++;
			}
		} else { //sum roll
			value = 0;
			for (int roll : rolls) {
				value += roll;
			}
		}

		description.append(" (");
		for (String desc : rollDescriptions) {
			description.append(desc).append(", ");
		}
		if (!rollDescriptions.isEmpty()) {
			description.delete(description.length()-2,description.length());
		}
		description.append(")");

	}

	/**
	 * Convert all the options to variables.
	 */
	private void processTriggers(Lexer.Token token) {

		switch (token.type) {
			// This is the main part, define the type of dice being rolled
			case DIE: {
				String data = token.data.toLowerCase();
				int firstD = data.indexOf('d');

				// Parse number of dice
				if (firstD > 0) {
					numberOfDice = Integer.parseInt(data.substring(0, firstD));
				} else {
					numberOfDice = 1;
				}

				// Parse type of die
				numberOfFaces = Integer.parseInt(data.substring(firstD + 1));
				// Prevent crash
				if (numberOfFaces == 0) {
					numberOfFaces = 1;
				}
				critSuccessNumber = numberOfFaces;
				break;
			}

			case FATEDIE: {
				fate = true;
				String data = token.data.toLowerCase();
				int firstD = data.indexOf('d');

				// Parse number of dice
				if (firstD > 0) {
					numberOfDice = Integer.parseInt(data.substring(0, firstD));
				} else {
					numberOfDice = 1;
				}

				// Parse type of die
				numberOfFaces = 3;

				//disable fail/crit notation
				critSuccessNumber = 99;
				critFailNumber = -99;
				break;
			}

			case EXPLODING: {
				exploding = true;
				compounding = false;
				break;
			}

			case COMPOUNDING: {
				exploding = false;
				compounding = true;
				break;
			}

			case KEEPHIGH: {
				keepHighest = true;
				keepLowest = false;
				String data = token.data.toLowerCase();
				if (data.charAt(1) == 'h') {
					keepNumber = Integer.parseInt(data.substring(2));
				} else {
					keepNumber = Integer.parseInt(data.substring(1));
				}
				break;
			}

			case KEEPLOW: {
				keepHighest = false;
				keepLowest = true;
				String data = token.data.toLowerCase();
				keepNumber = Integer.parseInt(data.substring(2));
				break;
			}

			case CRITSUCCESS: {
				String data = token.data.toLowerCase();
				critSuccessNumber = Integer.parseInt(data.substring(3));
				break;
			}

			case CRITFAIL: {
				String data = token.data.toLowerCase();
				critFailNumber = Integer.parseInt(data.substring(3));
				break;
			}

			case REROLL: {
				reroll = true;
				rerollOnce = false;
				String data = token.data.toLowerCase();
				rerollNumber = Integer.parseInt(data.substring(2));
				break;
			}

			case REROLLONCE: {
				reroll = false;
				rerollOnce = true;
				String data = token.data.toLowerCase();
				rerollNumber = Integer.parseInt(data.substring(3));
				break;
			}

			case TARGETNUMBER: {
				targetRoll = true;
				targetNumber = Integer.parseInt(token.data.substring(1));
			}
			
			default: {
				break;
			}
		}
	}


}