package net.ranzer.grimco.rafflebot.database.entities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.ranzer.grimco.rafflebot.data.IMemberData;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Entity
@Table (name="member")
public class MemberData implements IMemberData {

	@EmbeddedId
	private MemberPK memberPK;
	@Basic
	int xp;
	@Column(name = "last_xp")
	long lastXP;
	@Column(name = "raffle_ban")
	boolean raffleBan;

	//TODO make Role a basic Type?
	Map<Role,Long> timedRoles;

	//TODO make Constructors with default values


	@Override
	public Member getMember() {
		return null;
	}

	@Override
	public int getXP() {
		return xp;
	}

	@Override
	public void addXP(int amount) {
		xp+=amount;
		lastXP=System.currentTimeMillis();
	}

	@Override
	public void removeXP(int amount) {
		if (xp-amount<0)
			xp=0;
		else
			xp-=amount;
	}

	@Override
	public long lastXP() {
		return lastXP;
	}

	@Override
	public boolean isBannedFromRaffle() {
		return raffleBan;
	}

	@Override
	public void setBannedFromRaffle(boolean banned) {
		raffleBan = banned;
	}

	@Override
	public Map<Role, Long> getTimedRoles() {
		return timedRoles;
	}

	@Override
	public void addTimedRole(Role role, long timeToRemoveRole) {

	}

	@Override
	public void removedTimedRole(Role role) {

	}

	@Embeddable
	public static class MemberPK implements Serializable{
		//todo extract this?
		//references:
		//https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-jpa-and-hibernate
		protected String userID;
		protected String guildID;

		public MemberPK(String userID, String guildID){
			this.userID=userID;
			this.guildID=guildID;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			MemberPK memberPK = (MemberPK) o;
			return userID.equals(memberPK.userID) && guildID.equals(memberPK.guildID);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userID, guildID);
		}

	}

	@Entity
	@Table(name = "timed_role")
	public static class TimedRole{

	}
}
