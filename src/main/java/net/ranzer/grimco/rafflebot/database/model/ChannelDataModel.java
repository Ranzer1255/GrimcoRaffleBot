package net.ranzer.grimco.rafflebot.database.model;

import net.dv8tion.jda.api.entities.TextChannel;
import net.ranzer.grimco.rafflebot.database.interfaces.GuildData;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "text_channel")
public class ChannelDataModel {

	@Id
	@Column(name = "text_channel_id")
	private String channelID;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private GuildDataModel gdm;

	@Column(name = "perm_raffle")
	boolean rafflePerm = false;

	@Column(name = "perm_xp")
	boolean xpPerm = true;

	public ChannelDataModel(TextChannel channel, GuildDataModel gdm) {
		channelID = channel.getId();
		this.gdm  = gdm;
	}

	public String getChannelID() {
		return channelID;
	}

	public boolean hasRafflePerm() {
		return rafflePerm;
	}

	public void setRafflePerm(boolean rafflePerm) {
		this.rafflePerm = rafflePerm;
	}

	public boolean hasXpPerm() {
		return xpPerm;
	}

	public void setXpPerm(boolean xpPerm) {
		this.xpPerm = xpPerm;
	}
}
