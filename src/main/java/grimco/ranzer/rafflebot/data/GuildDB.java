package grimco.ranzer.rafflebot.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import grimco.ranzer.rafflebot.GrimcoRaffleBot;
import grimco.ranzer.rafflebot.database.BotDB;
import grimco.ranzer.rafflebot.config.BotConfiguration;
import grimco.ranzer.rafflebot.functions.levels.UserLevel;
import grimco.ranzer.rafflebot.util.Logging;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

@SuppressWarnings("ConstantConditions")
public class GuildDB implements IGuildData {

	public static final boolean DEFAULT_XP_announceMENT = true;
	private Guild guild;
	
	GuildDB(Guild guild) {
		this.guild=guild;
	}

	//xp methods
	@Override
	public void addXP(User author, int XP, MessageChannel channel) {

		int oldLevel = this.getLevel(author);
		Logging.debug("Adding "+ XP + "XP to "+ author.getName()+":"+guild.getName());
		
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
			   	  "insert into member (guild_id, user_id, xp, last_xp) values (?,?,?,?)"
				+ "on duplicate key update xp=xp+?,last_xp=?;")){
			
			long timestamp = System.currentTimeMillis();
			
			stmt.setString(1, guild.getId());
			stmt.setString(2, author.getId());
			stmt.setInt(3, XP);
			stmt.setLong(4, timestamp);
			stmt.setInt(5, XP);
			stmt.setLong(6, timestamp);
			stmt.executeUpdate();
			
//			if(this.getLevel(author)>oldLevel){
//				levelUpAlert(author, channel);
//			}
			
		} catch (Exception e){
			Logging.error(e.getMessage());
			Logging.log(e);
		}
	}
	
	@Override
	public void removeXP(User author, int XP, MessageChannel channel){
		int oldLevel = this.getLevel(author);
		Logging.debug("Removing "+ XP + "XP from "+ author.getName()+":"+guild.getName());
		
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
			   	  "update member "
			   	  + "set xp = xp-? "
			   	  + "where user_id = ? and guild_id = ?");){
			
			stmt.setString(3, guild.getId());
			stmt.setString(2, author.getId());
			stmt.setInt(1, XP);
			stmt.executeUpdate();
			
//			if(this.getLevel(author)<oldLevel){
//				levelDownAlert(author, channel);
//			}
			
		} catch (Exception e){
			Logging.error(e.getMessage());
			Logging.log(e);
		}
	}

	@Override
	public void setXPannouncement(boolean announce){
		try(PreparedStatement stmt = BotDB.getConnection().prepareStatement(
			"update guild set xp_announce=? where guild_id = ?;"	
		)){
			stmt.setBoolean(1, announce);
			stmt.setString(2, guild.getId());
			
			stmt.execute();
		} catch (SQLException e){
			Logging.error(e.getMessage());
			Logging.log(e);
		}
	}
	@Override
	public boolean getXPannouncement() {
		
		boolean rtn = false;
		try(PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				"select xp_announce from guild where guild_id = ?"
				)){
			
			stmt.setString(1, guild.getId());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()){
				rtn = rs.getBoolean(1);
			}
			
		} catch (SQLException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
			rtn = false;
		}
		return rtn;
	}


	@Override
	public UserLevel getUserLevel(Member author) {
		
		try {
			UserLevel rtn=null;
			PreparedStatement stmt = BotDB.getConnection().prepareStatement(
					  "select guild_id, user_id, xp, last_xp from member "
					+ "where user_id = ? and guild_id = ?;"
			);
			stmt.setString(1, author.getUser().getId());
			stmt.setString(2, author.getGuild().getId());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()){
				rtn = new UserLevel(
						GrimcoRaffleBot.getJDA().getGuildById(rs.getString(1)).getMemberById(rs.getString(2)),
						rs.getInt(3),
						rs.getLong(4)
				);
			}
			stmt.close();
			return rtn;
		} catch (SQLException e) {
			Logging.error("issue trying to get user ranzer.grimco.rafflebot.data");
			Logging.log(e);
			return null;
		}
	}

	@Override
	public int getLevel(User author) {
		
		return UserLevel.getLevel(getXP(author));
	}
	
	@Override
	public int getXP(User u){
		
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
					String.format("select xp from member where guild_id = %s and user_id=%s;",guild.getId(), u.getId())
					).executeQuery()){
			int rtn = -1;
			while (rs.next()){
				rtn = rs.getInt(1);
			}
			
			return rtn;
			
		} catch (SQLException e) {

			Logging.error("issue getting user's XP");
			Logging.log(e);
			return -1;
		}
		
	}

	
	//prefix methods
	@Override
	public String getPrefix() {
		String prefix=null;
		
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				"select prefix from guild where guild_id = ?"
		)){
			
			stmt.setString(1, guild.getId());
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				prefix=rs.getString(1);
			}
		} catch (SQLException e) {

			Logging.error("issue getting Prefix");
			Logging.log(e);
		}
		
		if (prefix==null){
			return BotConfiguration.getInstance().getPrefix();
		}
		return prefix;
	}
	
	@Override
	public void setPrefix(String prefix) {
		if (prefix!=null) {
			prefix = prefix.toLowerCase();
			try {
				PreparedStatement stmt = BotDB.getConnection()
						.prepareStatement(
						"insert into guild (guild_id, prefix) values (?,?) "
						+ "on duplicate key update prefix=?;"
				);
				stmt.setString(1, guild.getId());
				stmt.setString(2, prefix);
				stmt.setString(3, prefix);
				stmt.executeUpdate();
				stmt.close();
			} catch (Exception e) {
				Logging.error(e.getMessage());
				Logging.log(e);
			} 
		} else {
			removePrefix();
		}
	}
	
	@Override
	public void removePrefix() {
		
		try {
			PreparedStatement stmt = BotDB.getConnection()
					.prepareStatement("update guild set prefix = null where guild_id = ?;");
			stmt.setString(1, guild.getId());
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {

			Logging.error("issue removeing prefix");
			Logging.log(e);
		}
	}

	@Override
	public void deleteMember(Member m){
		deleteMember(m.getGuild().getId(), m.getUser().getId());
	}

	public void deleteMember(String guild, String user) {
		try {
			PreparedStatement stmt = BotDB.getConnection().prepareStatement(
					"delete from member where guild_id = ? and user_id = ?"
			);
			
			stmt.setString(1, guild);
			stmt.setString(2, user);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			Logging.error("Problem deleting member");
			Logging.log(e);
		}
	}

	@Override
	public IChannelData getChannel(TextChannel channel) {
		return new IChannelData(){

			@Override
			public void setXPPerm(boolean earnEXP) {
				try {
					PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							  "insert into text_channel (text_channel_id, guild_id, perm_xp) "
							+ "values (?,?,?) "
							+ "on duplicate key update perm_xp = ?;"
					);
					
					stmt.setString(1, channel.getId());
					stmt.setString(2, channel.getGuild().getId());
					stmt.setBoolean(3, earnEXP);
					stmt.setBoolean(4, earnEXP);
					
					stmt.executeUpdate();
					stmt.close();
					
				} catch (SQLException e) {
					Logging.error("problem setting xp perm");
					Logging.log(e);
				}
				
			}

			@Override
			public boolean getXPPerm() {
				try {
					PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							"select perm_xp from text_channel "
							+ "where text_channel_id = ?;"
					);
					
					stmt.setString(1, channel.getId());
					ResultSet rs = stmt.executeQuery();
					
					boolean rtn = IChannelData.DEFAULT_XP_SETTING;
					while (rs.next()) {
						rtn = rs.getBoolean(1);
					}
					stmt.close();
					return rtn;
					
					
				} catch (SQLException e) {
					Logging.error("problem getting XP perm");
					Logging.log(e);
					return true;
				}
			}

			@Override
			public void setRaffle(boolean raffle) {

			}

			@Override
			public boolean getRaffle() {
				return false;
			}

		};
	}
}
