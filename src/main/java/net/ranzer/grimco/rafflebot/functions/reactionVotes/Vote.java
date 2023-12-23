package net.ranzer.grimco.rafflebot.functions.reactionVotes;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vote {


	private Message voteMessage;

	private final List<String> options = new ArrayList<>();

	private final List<Integer> counts;

	public Vote(String... options){
		Collections.addAll(this.options, options);
		counts = new ArrayList<>(options.length);
		for (int i = 0; i < options.length; i++) {
			counts.add(-1);
		}
	}

	/**
	 * adds a single count to vote option
	 * @param option the 0 indexed option to add a count to
	 */
	public void addCount(int option){
		counts.set(option,counts.get(option)+1);
	}

	/**
	 * @return the index of the current winner of the vote. In case of tie will return Lowest index.
	 */
	public int getWinner(){
		int winningIndex = -1;
		int high = -1;

		for (int i = 0; i < counts.size(); i++) {
			if(counts.get(i)>high) {
				high=counts.get(i);
				winningIndex = i;
			}
		}
		return winningIndex;
	}

}
