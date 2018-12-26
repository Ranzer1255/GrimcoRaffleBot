package grimco.ranzer.rafflebot.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	private static final long DEFAULT_MESSAGE_TIMEOUT = 60000L;
	private final static int DEFAULT_XP_LOWBOUND = 15, DEFAULT_XP_HIGHBOUND = 25;
	private static final int DEFAULT_RAFFLE_THRESHOLD = 0;

	private Guild guild;
	
	GuildDB(Guild guild) {
		this.guild=guild;
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
			try (PreparedStatement stmt = BotDB.getConnection()
						.prepareStatement(
						"insert into guild (guild_id, prefix) values (?,?) "
						+ "on duplicate key update prefix=?;"
				)){
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
		
		try (PreparedStatement stmt = BotDB.getConnection()
					.prepareStatement("update guild set prefix = null where guild_id = ?;")
		){
			stmt.setString(1, guild.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {

			Logging.error("issue removing prefix");
			Logging.log(e);
		}
	}

	//xp methods
	/**
	 * sets the Timeout between XP earnings
	 *
	 * @param timeout the length of time in milliseconds between earning
	 */
	@Override
	public void setXPTimeout(long timeout) {

		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
					"update guild set xp_timeout = ? where guild_id = ?"
			)){
			stmt.setLong(1,timeout);
			stmt.setString(2,guild.getId());
			stmt.execute();
		} catch (SQLException e){
			Logging.error("issue setting xp timeout in DB");
			Logging.log(e);
		}

	}

	/**
	 * returns the Timeout for XP earnings
	 *
	 * @return the length of time in milliseconds between earnings. -1 if there was an error
	 */
	@Override
	public long getXPTimeout() {

		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				String.format("select xp_timeout from guild where guild_id = %s;", guild.getId())
		).executeQuery()) {
			long rtn = DEFAULT_MESSAGE_TIMEOUT;
			while (rs.next()) {
				rtn = rs.getLong(1);
			}

			return rtn;

		} catch (SQLException e) {

			Logging.error("issue getting xp timeout");
			Logging.log(e);
			return DEFAULT_MESSAGE_TIMEOUT;
		}
	}

	/**
	 * @return the low bound for a random xp earning
	 */
	@Override
	public int getXPLowBound() {
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				String.format("select xp_low from guild where guild_id = %s;", guild.getId())
		).executeQuery()) {
			int rtn = DEFAULT_XP_LOWBOUND;
			while (rs.next()) {
				rtn = rs.getInt(1);
			}

			return rtn;

		} catch (SQLException e) {

			Logging.error("issue getting xp lowbound");
			Logging.log(e);
			return DEFAULT_XP_LOWBOUND;

		}
	}
	/**
	 * @return the high bound for a random xp earning
	 */
	@Override
	public int getXPHighBound() {
		try (ResultSet rs = BotDB.getConnection().prepareStatement(
				String.format("select xp_high from guild where guild_id = %s;", guild.getId())
		).executeQuery()) {
			int rtn = DEFAULT_XP_HIGHBOUND;
			while (rs.next()) {
				rtn = rs.getInt(1);
			}

			return rtn;

		} catch (SQLException e) {

			Logging.error("issue getting xp highbound");
			Logging.log(e);
			return DEFAULT_XP_HIGHBOUND;

		}
	}

	/**
	 * sets the boundres for xp earnings set bounderies equal to guarentee a set outcome
	 *
	 * @param low  the low bound inclusive
	 * @param high the high bound inclusive
	 */
	@Override
	public void setXPBounds(int low, int high) {
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"update guild " +
								"set xp_low = ?, " +
								    "xp_high = ?  " +
								"where guild_id = ?;"
		)){
			stmt.setInt(1,low);
			stmt.setInt(2,high);
			stmt.setString(3, guild.getId());
			stmt.execute();
		} catch (SQLException e){
			Logging.error("issue setting xp bounds in DB");
			Logging.log(e);
		}
	}

	//member data
	/**
	 * @param m Member to retreve Data on
	 * @return IMemberData object containing date for specified Member
	 */
	@Override
	public IMemberData getMemberData(Member m) {
		return new IMemberData() {
			@Override
			public Member getMember() {
				return m;
			}

			@Override
			public int getXP(){

				try (ResultSet rs = BotDB.getConnection().prepareStatement(
						String.format("select xp from member where guild_id = %s and user_id=%s;",guild.getId(), m.getUser().getId())
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

			@Override
			public void addXP(int XP) {
				Logging.debug("adding "+ XP + "XP to "+ m.getUser().getName()+":"+guild.getName());
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"insert into member (guild_id, user_id, xp, last_xp) values (?,?,?,?)"
								+ "on duplicate key update xp=xp+?,last_xp=?;")){

					long timestamp = System.currentTimeMillis();

					stmt.setString(1, m.getGuild().getId());
					stmt.setString(2, m.getUser().getId());
					stmt.setInt(3, XP);
					stmt.setLong(4, timestamp);
					stmt.setInt(5, XP);
					stmt.setLong(6, timestamp);
					System.out.println(stmt.executeUpdate());

				} catch (Exception e){
					Logging.error(e.getMessage());
					Logging.log(e);
				}
			}

			@Override
			public void removeXP(int XP){
				Logging.debug("Removing "+ XP + "XP from "+ m.getUser().getName()+":"+guild.getName());

				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"update member "
								+ "set xp = xp-? "
								+ "where user_id = ? and guild_id = ?;")){

					stmt.setString(3, m.getGuild().getId());
					stmt.setString(2, m.getUser().getId());
					stmt.setInt(1, XP);
					stmt.executeUpdate();

				} catch (Exception e){
					Logging.error(e.getMessage());
					Logging.log(e);
				}
			}

			@Override
			public long lastXP() {
				try (ResultSet rs = BotDB.getConnection().prepareStatement(
						String.format("select last_xp from member where guild_id = %s and user_id = %s;",
								guild.getId(),
								m.getUser().getId())
				).executeQuery()) {
					long rtn = -1;
					while (rs.next()) {
						rtn = rs.getLong(1);
					}

					return rtn;

				} catch (SQLException e) {

					Logging.error("issue getting last XP time");
					Logging.log(e);
					return -1;

				}
			}

			@Override
			public boolean isBannedFromRaffle() {

				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							"select raffle_ban from member " +
									"where user_id = ?" +
									"and guild_id = ?;"
					)){

					stmt.setString(1, m.getUser().getId());
					stmt.setString(2, guild.getId());
					ResultSet rs = stmt.executeQuery();

					boolean rtn = IChannelData.DEFAULT_RAFFLE_SETTING;
					while (rs.next()) {
						rtn = rs.getBoolean(1);
					}
					stmt.close();
					return rtn;


				} catch (SQLException e) {
					Logging.error("problem getting Raffle perm");
					Logging.log(e);
					return IChannelData.DEFAULT_RAFFLE_SETTING;
				}
			}

			@Override
			public void setBannedFromRaffle(boolean banned) {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"update member " +
								"set raffle_ban = ? " +
								"where user_id = ? " +
								"and guild_id = ?;"
				)){

					stmt.setBoolean(1, banned);
					stmt.setString(2, m.getUser().getId());
					stmt.setString(3, guild.getId());
					stmt.execute();

				} catch (SQLException e) {
					Logging.error("problem setting Raffle perm");
					Logging.log(e);
				}
			}
		};
	}

	/**
	 * adds Member m to the data structure represented by this IGuildData Object
	 *
	 * @param m Member to be added
	 */
	@Override
	public void addMember(Member m) {
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				"insert into member (user_id, guild_id) " +
						"values(?,?);"
		)){

			stmt.setString(1, m.getUser().getId());
			stmt.setString(1, guild.getId());
			stmt.execute();


		} catch (SQLException e) {
			Logging.error("problem getting Raffle perm");
			Logging.log(e);
		}
	}

	@Override
	public void deleteMember(Member m){
		deleteMember(m.getGuild().getId(), m.getUser().getId());
	}

	private void deleteMember(String guild, String user) {
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
					"delete from member where guild_id = ? and user_id = ?"
			)){
			
			stmt.setString(1, guild);
			stmt.setString(2, user);
			stmt.executeUpdate();
		} catch (SQLException e) {
			Logging.error("Problem deleting member");
			Logging.log(e);
		}
	}

	//channel data
	@Override
	public IChannelData getChannel(TextChannel channel) {
		return new IChannelData(){

			@Override
			public void setXPPerm(boolean earnEXP) {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							  "insert into text_channel (text_channel_id, guild_id, perm_xp) "
							+ "values (?,?,?) "
							+ "on duplicate key update perm_xp = ?;"
					)){
					
					stmt.setString(1, channel.getId());
					stmt.setString(2, channel.getGuild().getId());
					stmt.setBoolean(3, earnEXP);
					stmt.setBoolean(4, earnEXP);
					
					stmt.executeUpdate();
					
				} catch (SQLException e) {
					Logging.error("problem setting xp perm");
					Logging.log(e);
				}
				
			}

			@Override
			public boolean getXPPerm() {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							"select perm_xp from text_channel "
							+ "where text_channel_id = ?;"
					)){
					
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
					return DEFAULT_XP_SETTING;
				}
			}

			@Override
			public void setRaffle(boolean raffle) {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							"insert into text_channel (text_channel_id, guild_id, perm_raffle) "
									+ "values (?,?,?) "
									+ "on duplicate key update perm_raffle = ?;"
					)){

					stmt.setString(1, channel.getId());
					stmt.setString(2, channel.getGuild().getId());
					stmt.setBoolean(3, raffle);
					stmt.setBoolean(4, raffle);

					stmt.executeUpdate();

				} catch (SQLException e) {
					Logging.error("problem setting raffle perm");
					Logging.log(e);
				}
			}

			@Override
			public boolean getRaffle() {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
							"select perm_raffle from text_channel "
									+ "where text_channel_id = ?;"
					)){

					stmt.setString(1, channel.getId());
					ResultSet rs = stmt.executeQuery();

					boolean rtn = IChannelData.DEFAULT_XP_SETTING;
					while (rs.next()) {
						rtn = rs.getBoolean(1);
					}
					return rtn;


				} catch (SQLException e) {
					Logging.error("problem getting XP perm");
					Logging.log(e);
					return true;
				}
			}

		};
	}

	@Override
	public void deleteChannel(TextChannel channel) {
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
			"delete from text_channel " +
					"where text_channel_id = ?;"
		)){
			stmt.setString(1, channel.getId());
			stmt.execute();
		} catch (SQLException e) {
			Logging.error("problem getting XP perm");
			Logging.log(e);
		}
	}

	@Override
	public IRaffleData getRaffleData() {
		return new IRaffleData() {
			@Override
			public Guild getGuild() {
				return guild;
			}

			@Override
			public List<Role> allowedManagementRoles() {
				List<Role> rtn = new ArrayList<>();

				try(PreparedStatement stmt = BotDB.getConnection().prepareStatement(
					"select role_id from raffle_roles " +
							"where guild_id = ?"
				)){
					stmt.setString(1,guild.getId());

					ResultSet rs = stmt.executeQuery();

					while (rs.next()){
						rtn.add(GrimcoRaffleBot.getJDA().getRoleById(Long.parseLong(rs.getString(1))));
					}
				} catch (SQLException e) {
					Logging.error("problem getting roles");
					Logging.log(e);
				}

				return rtn;
			}

			@Override
			public int getRaffleXPThreshold() {
				try (ResultSet rs = BotDB.getConnection().prepareStatement(
						String.format("select raffle_threshold from guild where guild_id = %s;", guild.getId())
				).executeQuery()) {
					int rtn = DEFAULT_RAFFLE_THRESHOLD;
					while (rs.next()) {
						rtn = rs.getInt(1);
					}

					return rtn;

				} catch (SQLException e) {

					Logging.error("issue getting raffle threshold");
					Logging.log(e);
					return DEFAULT_RAFFLE_THRESHOLD;

				}
			}

			@Override
			public void setRaffleXPThreshold(int threshold) {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"update guild " +
								"set raffle_threshold = ?, " +
								"where guild_id = ?;"
				)){
					stmt.setInt(1,threshold);
					stmt.setString(2, guild.getId());
					stmt.execute();
				} catch (SQLException e){
					Logging.error("issue setting raffle threshold in DB");
					Logging.log(e);
				}
			}

			@Override
			public boolean addAllowedRole(Role r) {
				try(PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"insert ignore into raffle_roles (guild_id,role_id) " +
								"values(?,?)"
				)){
					stmt.setString(1,guild.getId());
					stmt.setString(2,r.getId());
					return stmt.executeUpdate()==1;
				} catch (SQLException e) {
					Logging.error("issue adding allowed role");
					Logging.log(e);
					return false;
				}
			}

			@Override
			public boolean removeAllowedRole(Role r) {
				try(PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						"delete from raffle_roles " +
								"where guild_id = ?" +
								"and role_id = ?"
				)){
					stmt.setString(1,guild.getId());
					stmt.setString(2,r.getId());
					return stmt.executeUpdate()==1;
				} catch (SQLException e) {
					Logging.error("issue removing allowed role");
					Logging.log(e);
					return false;
				}
			}
		};
	}
}
