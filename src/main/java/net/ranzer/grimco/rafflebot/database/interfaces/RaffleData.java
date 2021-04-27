package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.ranzer.grimco.rafflebot.data.IRaffleData;

import java.util.List;

public class RaffleData extends AbstractData implements IRaffleData {

	@Override
	public List<Role> allowedRaffleRoles() {
		return null;
	}

	@Override
	public int getRaffleXPThreshold() {
		return 0;
	}

	@Override
	public void setRaffleXPThreshold(int threshold) {

	}

	@Override
	public boolean addAllowedRole(Role r) {
		return false;
	}

	@Override
	public boolean removeAllowedRole(Role r) {
		return false;
	}

	@Override
	public List<Member> getBannedUsers() {
		return null;
	}
}
