package net.ranzer.grimco.rafflebot.data;

import net.dv8tion.jda.core.entities.Member;

public interface IMemberData {

    Member getMember();

    int getXP();

    void addXP(int amount);
    void removeXP(int amount);
    long lastXP();

    boolean isBannedFromRaffle();
    void setBannedFromRaffle(boolean banned);
}
