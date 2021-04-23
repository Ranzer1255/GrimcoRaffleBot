package net.ranzer.grimco.rafflebot.database.entities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.data.IMemberData;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
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

	/* references:
	 * https://www.baeldung.com/hibernate-persisting-maps
	 */
	//TODO check this
	@ElementCollection
	@CollectionTable(name = "timedrole")
	@Column(name = "remove")
	Map<Role,Long> timedRoles;

	//TODO make Constructors with default values
	public MemberData(Member member) {
		this(member,0,false,new HashMap<>());
	}
	public MemberData(Member member, int xp, boolean raffleBan){
		this(member,xp,raffleBan,new HashMap<>());
	}
	public MemberData(Member member, int xp, boolean raffleBan,Map<Role,Long> timedRoles){
		memberPK = new MemberPK(member.getId(),member.getGuild().getId());
		this.xp = xp;
		this.timedRoles = timedRoles;
		this.raffleBan = raffleBan;

	}

	@Override
	public Member getMember() {
		JDA jda = GrimcoRaffleBot.getJDA();
		Guild g = jda.getGuildById(memberPK.guildID);
		User u = jda.getUserById(memberPK.userID);
		return Objects.requireNonNull(g).retrieveMember(Objects.requireNonNull(u)).complete();
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
		timedRoles.put(role,timeToRemoveRole);
	}

	@Override
	public void removedTimedRole(Role role) {
		timedRoles.remove(role);
	}

	@Embeddable
	public static class MemberPK implements Serializable{
		//todo extract this?
		//references:
		//https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-jpa-and-hibernate
		@Column(name = "user_id")
		protected String userID;
		@Column(name = "guild_id")
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
}
