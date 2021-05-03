package net.ranzer.grimco.rafflebot.database.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MemberPK implements Serializable {
	//references:
	//https://stackoverflow.com/questions/3585034/how-to-map-a-composite-key-with-jpa-and-hibernate
	protected String userID;

	private GuildDataModel gdm;

	private MemberPK(){}

	public MemberPK(String userID, GuildDataModel gdm) {
		this.userID = userID;
		this.gdm = gdm;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		net.ranzer.grimco.rafflebot.database.model.MemberPK memberPK = (net.ranzer.grimco.rafflebot.database.model.MemberPK) o;
		return userID.equals(memberPK.userID) && gdm.equals(memberPK.gdm);
	}
	@Override
	public int hashCode() {
		return Objects.hash(userID, gdm);
	}
}
