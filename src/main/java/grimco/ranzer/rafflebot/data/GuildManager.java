package grimco.ranzer.rafflebot.data;

import grimco.ranzer.rafflebot.GrimcoRaffleBot;
import grimco.ranzer.rafflebot.database.BotDB;
import grimco.ranzer.rafflebot.util.Logging;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * container for all the GuildData objects
 * 
 * @author Ranzer
 *
 */
public class GuildManager extends ListenerAdapter{
	
	/*
	 * update the DB to match things that happend while bot was offline
	 */
	static{
		Logging.info("updating DB to things that happend while offline");
		
		Logging.info("adding new guilds");
		addNewGuilds();
		
		Logging.info("removeing old guilds");
		removeOldGuilds();
		
		Logging.info("updating members");
		updateMembers();
		
		
	}

	public static IGuildData getGuildData(Guild key){
		return new GuildDB(key);
	}


	//convenance passthrough methods
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
		List<Guild> dbGuilds = pullGuildsFromDB();

		for(Guild g: GrimcoRaffleBot.getJDA().getGuilds()){
			if(!dbGuilds.contains(g)){
				try
				(PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"insert into guild(guild_id) values (?) on duplicate key update guild_id = guild_id"
						)){
					stmt.setString(1, g.getId());

					Logging.info(String.format("%d rows added to Guild Table", stmt.executeUpdate()));


				} catch (SQLException e) {
					Logging.error("issue adding new guilds to the DB");
					Logging.log(e);
				}

			}
		}
	}
	private static List<Guild> pullGuildsFromDB() {
		List<Guild> rtn = new ArrayList<>();

		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				"select guild_id from guild"
		).executeQuery()){

			while(rs.next()){
				rtn.add(GrimcoRaffleBot.getJDA().getGuildById(rs.getString(1)));
			}

		} catch (SQLException e) {
			Logging.error("issue data from DB");
			Logging.log(e);
		}

		return rtn;
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
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				"select guild_id, user_id from member" ,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE
			).executeQuery()){
			while (rs.next()){
				if (GrimcoRaffleBot.getJDA().getGuildById(rs.getString(1)).getMemberById(rs.getString(2))==null){
					rs.deleteRow();
				}
			}
		} catch (SQLException e) {
			Logging.error("issue updating members");
			Logging.log(e);
		}

	}


	//data modification listeners
	@Override
	public void onGuildJoin(GuildJoinEvent event) {

		try(PreparedStatement stmt = BotDB.getConnection().prepareStatement("insert into guild(guild_id) values (?)")){
			stmt.setString(1, event.getGuild().getId());
			stmt.execute();
		} catch (SQLException e) {
			Logging.error("issue joining guild to DB");
			Logging.log(e);
		}

	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
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
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		super.onGuildMemberLeave(event);

		getGuildData(event.getGuild()).deleteMember(event.getMember());

	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		super.onTextChannelDelete(event); //TODO make onTextChannelDelete
		getGuildData(event.getGuild()).deleteChannel(event.getChannel());
	}
}