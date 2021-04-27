package net.ranzer.grimco.rafflebot.database.model;

import net.dv8tion.jda.api.entities.Member;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "member")
@IdClass(MemberPK.class)
public class MemberDataModel {

	@Id
	@Column(name = "user_id")
	private String userID;

	@Id
	@ManyToOne
	@JoinColumn(name = "guild_id")
	private GuildDataModel gdm;

	@Basic
	int xp = 0;
	@Column(name = "last_xp")
	long lastXP = 0;
	@Column(name = "raffle_ban")
	boolean raffleBan = false;

	/* references:
	 * https://www.baeldung.com/hibernate-persisting-maps
	 */

	@ElementCollection
	@CollectionTable(name = "timedrole")
	@MapKeyColumn(name = "role_id")
	@Column(name = "remove")
	private final Map<String,Long> timedRoles = new HashMap<>();

	private MemberDataModel(){}

	public MemberDataModel(Member m, GuildDataModel gdm) {
		this.gdm = gdm;
		userID=m.getId();
	}

	public String getUserId(){
		return userID;
	}

	public String getGuildId(){
		return gdm.getId();
	}

	public int getXp(){
		return xp;
	}

	public void addXp(int amount){
		xp+=amount;
		lastXP=System.currentTimeMillis();
	}

	public void removeXP(int amount) {
		if (xp-amount<0)
			xp=0;
		else
			xp-=amount;
	}

	public long getLastXP(){
		return lastXP;
	}

	public Map<String, Long> getTimedRoles() {
		return timedRoles;
	}

	public boolean getRaffleBan(){
		return raffleBan;
	}

	public void setRaffleBan(boolean banned) {
		this.raffleBan = banned;
	}
}
