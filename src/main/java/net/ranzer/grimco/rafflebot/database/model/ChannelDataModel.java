package net.ranzer.grimco.rafflebot.database.model;

import net.dv8tion.jda.api.entities.TextChannel;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.data.IRaffleData;

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
	boolean rafflePerm = IChannelData.DEFAULT_RAFFLE_SETTING;

	@Column(name = "perm_xp")
	boolean xpPerm = true;

	private ChannelDataModel(){}

	public ChannelDataModel(TextChannel channel, GuildDataModel gdm) {
		channelID = channel.getId();
		this.gdm  = gdm;
	}

	public String getID() {
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
