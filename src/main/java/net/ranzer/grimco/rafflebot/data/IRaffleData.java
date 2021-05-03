package net.ranzer.grimco.rafflebot.data;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public interface IRaffleData {

    int DEFAULT_RAFFLE_THRESHOLD = 0;

//    Guild getGuild();
    List<Role> allowedRaffleRoles();

    int getRaffleXPThreshold();
    void setRaffleXPThreshold(int threshold);

    /**
     * adds a role to the Allowed Role list
     * @param r role to be added
     * @return true if role was added, false if it already existed in the list
     */
    boolean addAllowedRole(Role r);

    /**
     * removes a role from the allowed Role List
     * @param r role to be removed
     * @return true if role was removed, false if role did not exist in the list
     */
    boolean removeAllowedRole(Role r);

    List<Member> getBannedUsers();
}
