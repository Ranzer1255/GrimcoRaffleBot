package net.ranzer.grimco.rafflebot.database.model;

import net.dv8tion.jda.api.entities.Member;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "member")
@IdClass(MemberDataModel.MemberPK.class)
public class MemberDataModel {

	@Id
	@Column(name = "user_id")
	private String userID;

//	@Id
//	@Column(name = "guild_id")
//	private String guildID;

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

	public MemberDataModel(Member m, GuildDataModel gdm) {
		this.gdm = gdm;
		userID=m.getId();
	}

	/* references:
	 * https://www.baeldung.com/hibernate-persisting-maps
	 */
//	//TODO check this
//	@ElementCollection
//	@CollectionTable(name = "timedrole")
//	@Column(name = "remove")
//	Map<Role,Long> timedRoles;


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

	public boolean getRaffleBan(){
		return raffleBan;
	}

	public void setRaffleBan(boolean banned) {
		this.raffleBan = banned;
	}

	@Embeddable
	public static class MemberPK implements Serializable {
		//todo extract this?
		//references:
		//https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-jpa-and-hibernate
		protected String userID;

		private GuildDataModel gdm;

//		@Column(name = "guild_id")
//		protected String guildID;

//		public MemberPK(String userID, String guildID){
//			this.userID=userID;
//			this.guildID=guildID;
//		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			MemberPK memberPK = (MemberPK) o;
			return userID.equals(memberPK.userID) && gdm.equals(memberPK.gdm);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userID, gdm);
		}

	}
}
