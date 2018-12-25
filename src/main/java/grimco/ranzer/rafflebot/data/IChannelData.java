package grimco.ranzer.rafflebot.data;

public interface IChannelData {

	boolean DEFAULT_XP_SETTING = true;
	boolean DEFAULT_RAFFLE_SETTING = false;

	/**
	 * sets if this channel can earn xp or not
	 * @param earnEXP
	 */
	void setXPPerm(boolean earnEXP);

	/**
	 * returns if this channel can earn xp or not
	 * @return true if xp can be earned in this channel
	 */
	boolean getXPPerm();

	/**
	 * sets if raffles can be held in this channel
	 * @param raffle
	 */
	void setRaffle(boolean raffle);

	/**
	 *
	 * @return true if raffles can be held in this channel
	 */
	boolean getRaffle();

}
