package net.ranzer.grimco.rafflebot.database.model;

import net.dv8tion.jda.api.entities.User;
import net.ranzer.grimco.rafflebot.database.interfaces.MemberData;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "guild")
public class GuildDataModel {

	@Id
	@NaturalId
	@Column (name = "guild_id")
	private String guildID;

	@Basic
	private String prefix;

	@Column(name = "xp_timeout")
	private long XPTimeout;
	@Column (name="xp_low")
	private int XPLowBound;
	@Column (name="xp_high")
	private int XPHighBound;


	//TODO
//	@OneToMany(mappedBy = "MemberDataModel",cascade = CascadeType.ALL)
//	private Map<String, MemberDataModel> members = new HashMap<>();

	//TODO ModRole list

	public GuildDataModel(){

	}

	public GuildDataModel(String guildID){
		this.guildID = guildID;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
