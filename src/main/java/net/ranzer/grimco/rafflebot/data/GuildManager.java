package net.ranzer.grimco.rafflebot.data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import net.ranzer.grimco.rafflebot.database.interfaces.GuildData;
import net.ranzer.grimco.rafflebot.database.model.ChannelDataModel;
import net.ranzer.grimco.rafflebot.database.model.GuildDataModel;
import net.ranzer.grimco.rafflebot.database.model.MemberDataModel;
import net.ranzer.grimco.rafflebot.util.Logging;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * container for all the GuildData objects
 * 
 * @author Ranzer
 *
 */
public class GuildManager extends ListenerAdapter{

	//SQL Statements

	private static final String ADD_NEW_GUILDS_SQL =
			"INSERT INTO grimcodb.guild" +
				"(guild_id) " +
				"VALUES (?) " +
				"ON CONFLICT DO NOTHING";

	/*
	 * update the DB to match things that happened while bot was offline
	 */
	static{
		Logging.info("updating DB to things that happened while offline");
		
		Logging.info("removing old guilds");
		removeOldGuilds();

		Logging.info("updating text channels");
		removeOldTextChannels();

		Logging.info("updating members");
		updateMembers();


		
	}

	//Black Magic code copied from the internet
	public static <T> List<T> loadAllData(Class<T> type, Session s){
		CriteriaBuilder builder = s.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(type);
		criteria.from(type);
		return s.createQuery(criteria).getResultList();
	}

	public static IGuildData getGuildData(Guild key){
		return new GuildData(key);
	}


	//convenience pass through methods
	public static String getPrefix(Guild key){
		return new GuildData(key).getPrefix();
	}
	public static void setPrefix(Guild key, String prefix){
		new GuildData(key).setPrefix(prefix);
	}
	public static void removePrefix(Guild key){
		new GuildData(key).removePrefix();
	}

	//DB update methods

	//todo test this
	private static void removeOldGuilds() {//todo test this

		try (Session s = HibernateManager.getSessionFactory().openSession()){
			s.beginTransaction();
			List<GuildDataModel> guilds = loadAllData(GuildDataModel.class,s);
			for (GuildDataModel guild:guilds){
				if(GrimcoRaffleBot.getJDA().getGuildById(guild.getId())==null){
					s.delete(guild);
				}
			}
			s.getTransaction().commit();
		}
	}

	//todo test this
	private static void removeOldTextChannels() {
		try (Session s = HibernateManager.getSessionFactory().openSession()){
			s.beginTransaction();
			List<ChannelDataModel> channels = loadAllData(ChannelDataModel.class,s);
			for (ChannelDataModel channel:channels){
				if(GrimcoRaffleBot.getJDA().getTextChannelById(channel.getID())==null){
					s.delete(channel);
				}
			}
			s.getTransaction().commit();
		}
	}

	//todo test this
	private static void updateMembers() {

		//delete old members' XP
		try (Session s = HibernateManager.getSessionFactory().openSession()) {
			JDA jda = GrimcoRaffleBot.getJDA();
			s.beginTransaction();
			List<MemberDataModel> members = loadAllData(MemberDataModel.class, s);
			for (MemberDataModel member : members) {
				//noinspection ConstantConditions
				if (jda.getGuildById(member.getGuildId())
						.retrieveMemberById(Long.parseLong(member.getUserId()))
						.complete() == null) {
					member.removeAllXP();
				}
			}
			s.getTransaction().commit();
		}
	}


	//data modification listeners
	//todo test this
	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {
		super.onGuildLeave(event);

		try (Session s = HibernateManager.getSessionFactory().openSession()){
			GuildData g = new GuildData(event.getGuild());
			s.delete(g.getModel());
		}
	}

	//todo test this
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		getGuildData(event.getGuild()).addMember(event.getMember());
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

		//remove leaver's xp
		IMemberData md = getGuildData(event.getGuild()).getMemberData(event.getMember());
		int xp = md.getXP();
		md.removeXP(xp);

		//we don't want to delete member data when they leave the guild
		//this is to help prevent ban evasion
//		getGuildData(event.getGuild()).deleteMember(event.getMember());

	}

	//todo see if this is still needed
//	@Override
//	public void onGuildJoin(GuildJoinEvent event) {
//
//		try(PreparedStatement stmt = BotDB.getConnection().prepareStatement("insert into grimcodb.guild(guild_id) values (?)")){
//			stmt.setString(1, event.getGuild().getId());
//			stmt.execute();
//		} catch (SQLException e) {
//			Logging.error("issue joining guild to DB");
//			Logging.log(e);
//		}
//
//	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		getGuildData(event.getGuild()).deleteChannel(event.getChannel());
	}

	@Override
	public void onTextChannelCreate(TextChannelCreateEvent event) {
		getGuildData(event.getGuild()).addChannel(event.getChannel());
	}
}