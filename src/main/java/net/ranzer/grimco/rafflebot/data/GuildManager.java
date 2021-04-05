package net.ranzer.grimco.rafflebot.data;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.database.BotDB;
import net.ranzer.grimco.rafflebot.util.Logging;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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
		
		Logging.info("adding new guilds");
		addNewGuilds();
		
		Logging.info("removing old guilds");
		removeOldGuilds();

		Logging.info("updating text channels");
		updateTextChannels();

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
					ADD_NEW_GUILDS_SQL
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
					"SELECT guild_id FROM grimcodb.guild" ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE
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

	private static void updateTextChannels(){
		//add new text channels
		try {
			Connection con = BotDB.getConnection();
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(
					"insert into grimcodb.text_channel (guild_id, text_channel_id) " +
							"values (?,?) ON CONFLICT DO NOTHING"
			);
			for(Guild g: GrimcoRaffleBot.getJDA().getGuilds()){
				for(TextChannel t: g.getTextChannels()){
					stmt.setString(1,g.getId());
					stmt.setString(2,t.getId());
					stmt.addBatch();
				}
			}
			stmt.executeBatch();
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			Logging.error("issue adding channels");
			Logging.log(e);
		}


		//delete old text channels
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				"select text_channel_id from grimcodb.text_channel" ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE
		).executeQuery()){
			while(rs.next()){
				if(GrimcoRaffleBot.getJDA().getTextChannelById(rs.getString(1))==null)
					rs.deleteRow();
			}
		} catch (SQLException e) {
			Logging.error("issue deleting channels");
			Logging.log(e);
		}
	}

	private static void updateMembers() {

		//add new members
		try {
			Connection con = BotDB.getConnection();
			con.setAutoCommit(false);
			PreparedStatement stmt = con.prepareStatement(
					"insert into grimcodb.member (guild_id, user_id) " +
							"values (?,?) ON CONFLICT DO NOTHING"
			);
			for(Guild g: GrimcoRaffleBot.getJDA().getGuilds()){
				g.loadMembers(m -> {
					try {
						stmt.setString(1,g.getId());
						stmt.setString(2,m.getUser().getId());
						stmt.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			}
			stmt.executeBatch();
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//old code to delete old members, i'm going to keep it commented out, but likely wont use it
		//see onGuildMemberLeave() for more details

		//delete old members' XP
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				"select guild_id, user_id, xp from grimcodb.member" ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE
			).executeQuery()){
			while (rs.next()){
				if (GrimcoRaffleBot.getJDA().getGuildById(rs.getString(1)).getMemberById(rs.getString(2))==null){
					rs.updateInt(3,0);
				}
			}
		} catch (SQLException e) {
			Logging.error("issue updating members");
			Logging.log(e);
		}

	}


	//data modification listeners
	@Override
	public void onGuildJoin(GuildJoinEvent event) {// TODO: 12/26/2018 extract addGuild(Guild guild) as its own method

		try(PreparedStatement stmt = BotDB.getConnection().prepareStatement("insert into grimcodb.guild(guild_id) values (?)")){
			stmt.setString(1, event.getGuild().getId());
			stmt.execute();
		} catch (SQLException e) {
			Logging.error("issue joining guild to DB");
			Logging.log(e);
		}

	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event) {// TODO: 12/26/2018 extract removeGuild(Guild guild) as its own method
		super.onGuildLeave(event);

		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				"delete from grimcodb.guild where guild_id = ?"
		)){

			stmt.setString(1, event.getGuild().getId());
			Logging.info(String.format("Deleting guild %s(%s",
					event.getGuild().getName(),
					event.getGuild().getId())
			);

			Logging.debug(String.format("%d rows updated", stmt.executeUpdate()));

		} catch (SQLException e){
			Logging.error("issue removing guild from DB: " +event.getGuild().getName());
			Logging.log(e);
		}
	}

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

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		getGuildData(event.getGuild()).deleteChannel(event.getChannel());
	}

	@Override
	public void onTextChannelCreate(TextChannelCreateEvent event) {
		getGuildData(event.getGuild()).addChannel(event.getChannel());
	}
}