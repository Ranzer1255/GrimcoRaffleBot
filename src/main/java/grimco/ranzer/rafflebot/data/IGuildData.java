package grimco.ranzer.rafflebot.data;

import net.dv8tion.jda.core.entities.*;

public interface IGuildData {
    //prefix methods
    String getPrefix();
    void setPrefix(String prefix);
    void removePrefix();

    //xp methods

    /**
     * sets the Timeout between XP earnings
     * @param timeout the length of time in milliseconds between earning
     */
    void setXPTimeout(long timeout);
    /**
     * returns the Timeout for XP earnings
     * @return the length of time in milliseconds between earnings
     */
    long getXPTimeout();
    /**
     * @return the low bound for a random xp earning
     */
    int getXPLowBound();
    /**
     * @return the high bound for a random xp earning
     */
    int getXPHighBound();
    /**
     * sets the boundres for xp earnings set bounderies equal to guarentee a set outcome
     * @param low the low bound inclusive
     * @param high the high bound inclusive
     */
    void setXPBounds(int low, int high);

    //member data

    /**
     *
     * @param m Member to retreve Data on
     * @return IMemberData object containing date for specified Member
     */
    IMemberData getMemberData(Member m);

    /**
     * adds Member m to the data structure represented by this IGuildData Object
     * @param m Member to be added
     */
    void addMember(Member m);

    /**
     * removes Member m from the data Structure represented by this IGuildData Object
     * @param m
     */
    void deleteMember(Member m);

    //channel data
    IChannelData getChannel(TextChannel channel);
    void deleteChannel(TextChannel channel);
    void addChannel(TextChannel channel);

    IRaffleData getRaffleData();
}
