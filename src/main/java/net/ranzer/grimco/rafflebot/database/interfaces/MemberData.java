package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.data.IMemberData;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import net.ranzer.grimco.rafflebot.database.model.MemberDataModel;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table (name="member")
public class MemberData extends AbstractData implements IMemberData {

	MemberDataModel mdm;

	public MemberData(Member member) {
		Session session = HibernateManager.getSessionFactory().openSession();
		mdm = session.createQuery("select e " +
				"from MemberDataModel e " +
				"where e.gdm.guildID = :guildId and " +
				"e.userID = :userId", MemberDataModel.class)
				.setParameter("guildId", member.getGuild().getId())
				.setParameter("userId", member.getUser().getId()).getSingleResult();

		session.close();
	}


	@Override
	public Member getMember() {
		JDA jda = GrimcoRaffleBot.getJDA();
		Guild g = jda.getGuildById(mdm.getGuildId());
		User u = jda.getUserById(mdm.getUserId());
		return Objects.requireNonNull(g).retrieveMember(Objects.requireNonNull(u)).complete();
	}

	@Override
	public int getXP() {
		return mdm.getXp();
	}

	@Override
	public void addXP(int amount) {
		mdm.addXp(amount);
		save(mdm);
	}

	@Override
	public void removeXP(int amount) {
		mdm.removeXP(amount);
		save(mdm);
	}

	@Override
	public long lastXP() {
		return mdm.getLastXP();
	}

	@Override
	public boolean isBannedFromRaffle() {
		return mdm.getRaffleBan();
	}

	@Override
	public void setBannedFromRaffle(boolean banned) {
		mdm.setRaffleBan(banned);
		save(mdm);
	}

	@Override
	public Map<Role, Long> getTimedRoles() {
		Map<Role,Long> rtn = new HashMap<>();

		for (Map.Entry<String,Long> role: mdm.getTimedRoles().entrySet()){
			rtn.put(
					GrimcoRaffleBot.getJDA().getRoleById(role.getKey()),
					role.getValue());
		}

		return rtn;
	}

	@Override
	public void addTimedRole(Role role, long timeToRemoveRole) {
		mdm.getTimedRoles().put(role.getId(),timeToRemoveRole);
		save(mdm);
	}

	@Override
	public void removedTimedRole(Role role) {
		mdm.getTimedRoles().remove(role.getId());
		save(mdm);
	}



}
