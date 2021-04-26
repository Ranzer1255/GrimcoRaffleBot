package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.entities.*;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.IChannelData;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.data.IMemberData;
import net.ranzer.grimco.rafflebot.data.IRaffleData;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import net.ranzer.grimco.rafflebot.database.model.GuildDataModel;
import org.hibernate.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildData implements IGuildData {

	GuildDataModel gdm;

	private long XPTimeout;
	private int XPLowBound;
	private int XPHighBound;

//	//TODO
//	@OneToMany(mappedBy = "MemberData",cascade = CascadeType.ALL)
	private Map<User,MemberData> members = new HashMap<>();

	public GuildData(Guild g){

		Session session = HibernateManager.getSessionFactory().openSession();
		gdm = session.createQuery("select e " +
				"from GuildDataModel e " +
				"where e.guildID = :id",GuildDataModel.class)
				.setParameter("id",g.getId()).getSingleResult();
	}

	//Prefix methods
	@Override
	public String getPrefix() {
		String rtn = gdm.getPrefix();

		if (rtn == null){
			rtn = BotConfiguration.getInstance().getPrefix();
		}
		return gdm.getPrefix();
	}
	@Override
	public void setPrefix(String prefix) {
		gdm.setPrefix(prefix);
		save();
	}

	@Override
	public void removePrefix() {
		gdm.setPrefix(null);
		save();
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
		return members.get(m.getUser());
	}

	@Override
	public IMemberData getMemberData(User u) {
		return members.get(u);
	}

	@Override
	public void addMember(Member m) {
		members.put(m.getUser(),new MemberData(m));
	}

	@Override
	public void deleteMember(Member m) {
		members.remove(m.getUser());
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

	private void save() {
		Session s = HibernateManager.getSessionFactory().openSession();
		s.beginTransaction();
		s.persist(gdm);
	}
}