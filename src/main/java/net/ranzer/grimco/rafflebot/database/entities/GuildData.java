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
import java.util.List;

@Entity
@Table(name = "guild")
public class GuildData implements IGuildData {

	@Basic
	private String prefix;


	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(String prefix) {

	}

	@Override
	public void removePrefix() {
		prefix=null;
	}

	@Override
	public void setXPTimeout(long timeout) {

	}

	@Override
	public long getXPTimeout() {
		return 0;
	}

	@Override
	public int getXPLowBound() {
		return 0;
	}

	@Override
	public int getXPHighBound() {
		return 0;
	}

	@Override
	public void setXPBounds(int low, int high) {

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
