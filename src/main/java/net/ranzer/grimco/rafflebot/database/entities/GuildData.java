package net.ranzer.grimco.rafflebot.database.entities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.data.IMemberData;
import net.ranzer.grimco.rafflebot.data.IRaffleData;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guild")
public class GuildData implements IGuildData {

	@Basic
	private String prefix;

	@Column (name = "xp_timeout")
	private long XPTimeout;
	@Column (name="xp_low")
	private int XPLowBound;
	@Column (name="xp_high")
	private int XPHighBound;

	//TODO
	private List<IMemberData> members;

	//TODO ModRole list

	//Prefix methods
	@Override
	public String getPrefix() {
		return prefix;
	}
	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	@Override
	public void removePrefix() {
		prefix=null;
	}

	//XP Methods
	@Override
	public void setXPTimeout(long timeout) {
		XPTimeout = timeout;
	}
	@Override
	public long getXPTimeout() {
		return XPTimeout;
	}
	@Override
	public int getXPLowBound() {
		return XPLowBound;
	}
	@Override
	public int getXPHighBound() {
		return XPHighBound;
	}
	@Override
	public void setXPBounds(int low, int high) {
		XPLowBound = low;
		XPHighBound = high;
	}

	@Override
	public IMemberData getMemberData(Member m) {
		return null;
	}

	@Override
	public IMemberData getMemberData(User u) {
		return null;
	}

	@Override
	public void addMember(Member m) {

	}

	@Override
	public void deleteMember(Member m) {

	}

	@Override
	public IChannelData getChannel(TextChannel channel) {
		return null;
	}

	@Override
	public void deleteChannel(TextChannel channel) {

	}

	@Override
	public void addChannel(TextChannel channel) {

	}

	@Override
	public IRaffleData getRaffleData() {
		return null;
	}

	@Override
	public List<Role> getModRoles() {
		return null;
	}

	@Override
	public boolean addModRole(Role r) {
		return false;
	}

	@Override
	public boolean removeModRole(Role r) {
		return false;
	}
}
