package grimco.ranzer.rafflebot.data;

import grimco.ranzer.rafflebot.GrimcoRaffleBot;
import grimco.ranzer.rafflebot.database.BotDB;
import grimco.ranzer.rafflebot.util.Logging;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * container for all the GuildData objects
 * 
 * @author Ranzer
 *
 */
public class GuildManager extends ListenerAdapter{
	
	/*
	 * update the DB to match things that happened while bot was offline
	 */
	static{
		Logging.info("updating DB to things that happened while offline");
		
		Logging.info("adding new guilds");
		addNewGuilds();
		
		Logging.info("removing old guilds");
		removeOldGuilds();
		
		Logging.info("updating members");
		updateMembers();
		
		
	}

	public static IGuildData getGuildData(Guild key){
		return new GuildDB(key);
	}


	//convenience pass through methods
	public static String getPrefix(Guild key){
		return new GuildDB(key).getPrefix();
	}
	public static void setPrefix(Guild key, String prefix){
		new GuildDB(key).setPrefix(prefix);
	}
	public static void removePrefix(Guild key){
		new GuildDB(key).removePrefix();
	}

	//DB update methods
	private static void addNewGuilds() {

		try {
			Connection con = BotDB.getConnection();
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(
					"insert ignore into guild(guild_id) values (?)"
			);
			for(Guild g: GrimcoRaffleBot.getJDA().getGuilds()){
				stmt.setString(1,g.getId());
				stmt.addBatch();
			}
			stmt.executeBatch();
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			Logging.error("issue adding new guilds to the DB");
			Logging.log(e);
		}
	}

	private static void removeOldGuilds() {
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
					"select guild_id from guild" ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE
			).executeQuery()){

			while(rs.next()){
				if(GrimcoRaffleBot.getJDA().getGuildById(rs.getString(1))==null)
					rs.deleteRow();
			}

		} catch (SQLException e) {
			Logging.error("problem removing old guilds from DB");
			Logging.log(e);
		}
	}
	private static void updateMembers() {

		//add new members
		try {
			Connection con = BotDB.getConnection();
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(
					"insert ignore into member (guild_id, user_id) " +
							"values (?,?)"
			);
			for(Guild g: GrimcoRaffleBot.getJDA().getGuilds()){
				for(Member m: g.getMembers()){
					stmt.setString(1,g.getId());
					stmt.setString(2,m.getUser().getId());
					stmt.addBatch();
				}
			}
			stmt.executeBatch();
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//old code to delete old members, i'm going to keep it commented out, but likely wont use it
		//see onGuildMemberLeave() for more details

//		//delete old members
//		try (ResultSet rs = BotDB.getConnection().prepareStatement(
//				"select guild_id, user_id from member" ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE
//			).executeQuery()){
//			while (rs.next()){
//				if (GrimcoRaffleBot.getJDA().getGuildById(rs.getString(1)).getMemberById(rs.getString(2))==null){
//					rs.deleteRow();
//				}
//			}
//		} catch (SQLException e) {
//			Logging.error("issue updating members");
//			Logging.log(e);
//		}

	}


	//data modification listeners
	@Override
	public void onGuildJoin(GuildJoinEvent event) {// TODO: 12/26/2018 extract addGuild(Guild guild) as its own method

		try(PreparedStatement stmt = BotDB.getConnection().prepareStatement("insert into guild(guild_id) values (?)")){
			stmt.setString(1, event.getGuild().getId());
			stmt.execute();
		} catch (SQLException e) {
			Logging.error("issue joining guild to DB");
			Logging.log(e);
		}

	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {// TODO: 12/26/2018 extract removeGuild(Guild guild) as its own method
		super.onGuildLeave(event);

		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				"delete from guild where guild_id = ?"
		)){

			stmt.setString(1, event.getGuild().getId());
			Logging.info(String.format("Delteting guild %s(%s",
					event.getGuild().getName(),
					event.getGuild().getId())
			);

			Logging.debug(String.format("%d rows updated", stmt.executeUpdate()));

		} catch (SQLException e){
			Logging.error("issue removiing guild from DB: " +event.getGuild().getName());
			Logging.log(e);
		}
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		getGuildData(event.getGuild()).addMember(event.getMember());
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {

		//we don't want to delete member data when they leave the guild
		//this is to help prevent ban evasion
//		getGuildData(event.getGuild()).deleteMember(event.getMember());

	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		getGuildData(event.getGuild()).deleteChannel(event.getChannel());
	}
}