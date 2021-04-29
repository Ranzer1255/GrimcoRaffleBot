package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.entities.*;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.data.IMemberData;
import net.ranzer.grimco.rafflebot.data.IRaffleData;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import net.ranzer.grimco.rafflebot.database.model.ChannelDataModel;
import net.ranzer.grimco.rafflebot.database.model.GuildDataModel;
import net.ranzer.grimco.rafflebot.database.model.MemberDataModel;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuildData extends AbstractData implements IGuildData {

	private final Guild guild;
	private final GuildDataModel gdm;

	public GuildData(Guild g){
		this.guild = g;
		GuildDataModel retrieved;
		try(Session session = HibernateManager.getSessionFactory().openSession()) {
			retrieved = session.createQuery("select e " +
					"from GuildDataModel e " +
					"where e.guildID = :id", GuildDataModel.class)
					.setParameter("id", g.getId()).getSingleResult();
		} catch (NoResultException e){
			retrieved = new GuildDataModel(g.getId());
		}

		gdm = retrieved;
		save(gdm);
	}

	//Prefix methods
	@Override
	public String getPrefix() {
		String rtn = gdm.getPrefix();

		if (rtn == null){
			rtn = BotConfiguration.getInstance().getPrefix();
		}
		return rtn;
	}
	@Override
	public void setPrefix(String prefix) {
		gdm.setPrefix(prefix);
		save(gdm);
	}
	@Override
	public void removePrefix() {
		gdm.setPrefix(null);
		save(gdm);
	}

	//XP Methods

	@Override
	public void setXPTimeout(long timeout) {
		gdm.setXPTimeout(timeout);
		save(gdm);
	}
	@Override
	public long getXPTimeout() {
		return gdm.getXPTimeout();
	}
	@Override
	public int getXPLowBound() {
		return gdm.getXPLowBound();
	}
	@Override
	public int getXPHighBound() {
		return gdm.getXPHighBound();
	}
	@Override
	public void setXPBounds(int low, int high) {
		gdm.setXPBounds(low,high);
		save(gdm);
	}

	//memberData methods
	@Override
	public IMemberData getMemberData(Member m) {
		return new MemberData(m,this);
	}
	@Override
	public IMemberData getMemberData(User u) {
		Member m = guild.retrieveMember(u).complete();
		return getMemberData(m);
	}

	@Override
	public void addMember(Member m) {
		gdm.addMember(m);
		save(gdm);
	}

	@Override
	public void deleteMember(Member m) {//TODO test this
		gdm.removeMember(new MemberDataModel(m,gdm));
		save(gdm);
	}

	@Override
	public IChannelData getChannel(TextChannel channel) {

		ChannelDataModel model;
		try(Session s = HibernateManager.getSessionFactory().openSession()) {

			model = s.createQuery("select e " +
					"from ChannelDataModel e " +
					"where e.channelID = :id", ChannelDataModel.class)
					.setParameter("id", channel.getId()).getSingleResult();
		} catch (NoResultException e){
			model = new ChannelDataModel(channel,gdm);
			save(model);
		}

		final ChannelDataModel cdm = model;
		return new IChannelData() {//todo build tess for all of this
			@Override
			public void setXPPerm(boolean earnEXP) {
				cdm.setXpPerm(earnEXP);
				save(cdm);
			}

			@Override
			public boolean getXPPerm() {
				return cdm.hasXpPerm();
			}

			@Override
			public void setRaffle(boolean raffle) {
				cdm.setRafflePerm(raffle);
				save(cdm);
			}

			@Override
			public boolean getRaffle() {
				return cdm.hasRafflePerm();
			}
		};
	}

	@Override
	public void deleteChannel(TextChannel channel) {

		try (Session s = HibernateManager.getSessionFactory().openSession()) {
			ChannelDataModel cdm = s.createQuery("select e " +
					"from ChannelDataModel e " +
					"where e.channelID = :id", ChannelDataModel.class)
					.setParameter("id", channel.getId()).getSingleResult();
			s.remove(cdm);
		}
	}

	@Override
	public void addChannel(TextChannel channel) {
		ChannelDataModel cdm = new ChannelDataModel(channel,gdm);
		save(cdm);
	}

	@Override
	public IRaffleData getRaffleData() {

		return new IRaffleData() {

			@Override
			public List<Role> allowedRaffleRoles() {
				try (Session s = HibernateManager.getSessionFactory().openSession()) {
					s.load(gdm,gdm.getId());
					List<Role> rtn = new ArrayList<>();
					for (String id : gdm.getRaffleRoleIDs()) {
						rtn.add(guild.getRoleById(id));
					}
					return rtn;
				}
			}

			@Override
			public int getRaffleXPThreshold() {
				return gdm.getRaffleThreshold();
			}

			@Override
			public void setRaffleXPThreshold(int threshold) {
				gdm.setRaffleThreshold(threshold);
				save(gdm);
			}

			@Override
			public boolean addAllowedRole(Role r) {
				if (gdm.addRaffleRole(r.getId())){
					save(gdm);
					return true;
				}
				return false;
			}

			@Override
			public boolean removeAllowedRole(Role r) {
				if (gdm.removeRaffleRole(r.getId())){
					save(gdm);
					return true;
				}
				return false;
			}

			@Override
			public List<Member> getBannedUsers() {
				Session s = HibernateManager.getSessionFactory().openSession();

				List<String> userIDs = s.createQuery(
						"SELECT m.userID " +
								"FROM MemberDataModel m " +
								"where m.gdm = :guild and " +
								"m.raffleBan = true",
						String.class)
						.setParameter("guild",gdm)
						.getResultList();

				return userIDs.stream().map(id -> guild.retrieveMemberById(id).complete()).collect(Collectors.toList());
			}
		};
	}

	@Override
	public List<Role> getModRoles() {
		List<Role> rtn = new ArrayList<>();
		for (String id : gdm.getModRoleIDs()){
			rtn.add(guild.getRoleById(id));
		}
		return rtn;
	}

	@Override
	public boolean addModRole(Role r) {
		return gdm.addModRole(r.getId());
	}

	@Override
	public boolean removeModRole(Role r) {
		return gdm.removeModRole(r.getId());
	}

	public GuildDataModel getModel() {
		return gdm;
	}
}