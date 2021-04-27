package net.ranzer.grimco.rafflebot.database.model;

import net.dv8tion.jda.api.entities.Member;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private long XPTimeout = IGuildData.DEFAULT_MESSAGE_TIMEOUT;

	@Column (name="xp_low")
	private int XPLowBound = IGuildData.DEFAULT_XP_LOWBOUND;
	@Column (name="xp_high")
	private int XPHighBound = IGuildData.DEFAULT_XP_HIGHBOUND;


	//TODO
	@OneToMany(mappedBy = "gdm",cascade = CascadeType.ALL)
	private List<MemberDataModel> members = new ArrayList<>();

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

	public void addMember(Member m){
		members.add(new MemberDataModel(m,this));
	}

	public String getId() {
		return guildID;
	}
}
