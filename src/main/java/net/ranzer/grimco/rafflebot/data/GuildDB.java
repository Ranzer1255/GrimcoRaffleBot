package net.ranzer.grimco.rafflebot.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import net.ranzer.grimco.rafflebot.database.BotDB;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.util.Logging;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

@SuppressWarnings("ConstantConditions")
public class GuildDB implements IGuildData { //TODO look at using activeJBDC or other ORM framework

	private static final long DEFAULT_MESSAGE_TIMEOUT = 60000L;
	private final static int DEFAULT_XP_LOWBOUND = 15, DEFAULT_XP_HIGHBOUND = 25;
	private static final int DEFAULT_RAFFLE_THRESHOLD = 0;

	//sql statements
	//prefix commands
	private static final String GET_PREFIX_SQL =
			"SELECT prefix " +
				"FROM grimcodb.guild " +
				"WHERE guild_id = ?";
	private static final String SET_PREFIX_SQL =
			"UPDATE grimcodb.guild " +
				"SET prefix = ? " +
				"WHERE guild_id = ?";
	private static final String REMOVE_PREFIX_SQL =
			"UPDATE grimcodb.guild " +
				"SET prefix = null " +
				"WHERE guild_id = ?;";

	//XP commands
	private static final String SET_XP_TIMEOUT_SQL =
			"UPDATE grimcodb.guild " +
				"SET xp_timeout = ? " +
				"WHERE guild_id = ?";
	private static final String GET_XP_TIMEOUT_SQL =
			"SELECT xp_timeout " +
				"FROM grimcodb.guild " +
				"WHERE guild_id = '%s';";
	private static final String GET_XP_LOW_SQL =
			"SELECT xp_low " +
				"FROM grimcodb.guild " +
				"WHERE guild_id = '%s';";
	private static final String GET_XP_HI_SQL =
			"SELECT xp_high " +
				"FROM grimcodb.guild " +
				"WHERE guild_id = '%s';";
	private static final String SET_XP_BOUNDS_SQL =
			"UPDATE grimcodb.guild " +
				"SET xp_low = ?, xp_high = ?  " +
				"WHERE guild_id = ?;";
	private static final String GET_XP_SQL =
			"SELECT xp " +
				"FROM grimcodb.member " +
				"WHERE guild_id = '%s' " +
				"AND user_id='%s';";
	private static final String ADD_XP_SQL =
			"UPDATE grimcodb.member " +
				"SET xp=xp+?,last_xp=? " +
				"WHERE user_id = ? " +
				"AND guild_id =?;";
	private static final String REMOVE_XP_SQL =
			"UPDATE grimcodb.member " +
				"SET xp = xp-? " +
				"WHERE user_id = ? AND guild_id = ?;";
	private static final String GET_LAST_XP_SQL =
			"SELECT last_xp " +
				"FROM grimcodb.member " +
				"WHERE guild_id = '%s' " +
				"AND user_id = '%s';";
	private static final String GET_IS_BANNED_SQL =
			"SELECT raffle_ban " +
				"FROM grimcodb.member " +
				"WHERE user_id = ?" +
				"AND guild_id = ?;";
	private static final String SET_BANNED_SQL =
			"UPDATE grimcodb.member " +
				"SET raffle_ban = ? " +
				"WHERE user_id = ? " +
				"AND guild_id = ?;";
	private static final String ADD_MEMBER_SQL =
			"INSERT INTO grimcodb.member " +
				"(user_id, guild_id) " +
				"values(?,?);";
	private static final String REMOVE_MEMBER_SQL =
			"DELETE FROM grimcodb.member " +
				"WHERE guild_id = ? " +
				"AND user_id = ?";
	private static final String SET_XP_PERM_SQL =
			"UPDATE grimcodb.text_channel " +
				"SET perm_xp = ? " +
				"WHERE text_channel_id = ?";
	private static final String GET_XP_PERM_SQL =
			"SELECT perm_xp " +
				"FROM grimcodb.text_channel " +
				"WHERE text_channel_id = ?;";
	private static final String SET_RAFFLE_PERM_SQL =
			"UPDATE grimcodb.text_channel " +
				"SET perm_raffle = ? " +
				"WHERE text_channel_id = ?;";
	private static final String GET_RAFFLE_PERM_SQL =
			"SELECT perm_raffle " +
				"FROM grimcodb.text_channel " +
				"WHERE text_channel_id = ?;";
	private static final String REMOVE_CHANNEL_SQL =
			"DELETE FROM grimcodb.text_channel " +
				"WHERE text_channel_id = ?;";
	private static final String ADD_CHANNEL_SQL =
			"INSERT INTO grimcodb.text_channel " +
				"(text_channel_id, guild_id) " +
				"VALUES(?,?);";
	private static final String GET_MANAGEMENT_ROLES_SQL =
			"SELECT role_id " +
				"FROM grimcodb.raffle_roles " +
				"WHERE guild_id = ?";
	private static final String GET_XP_THRESHOLD_SQL =
			"SELECT raffle_threshold " +
				"FROM grimcodb.guild " +
				"WHERE guild_id = '%s';";
	private static final String SET_RAFFLE_THRESHOLD_SQL =
			"UPDATE grimcodb.guild " +
				"SET raffle_threshold = ? " +
				"WHERE guild_id = ?;";
	private static final String ADD_MANAGEMENT_ROLE_SQL =
			"INSERT INTO grimcodb.raffle_roles " +
				"(guild_id,role_id) " +
				"values(?,?) " +
				"ON CONFLICT DO NOTHING;";
	private static final String REMOVE_MANAGEMENT_ROLE_SQL =
			"DELETE FROM grimcodb.raffle_roles " +
				"WHERE guild_id = ?" +
				"AND role_id = ?";
	private static final String GET_BANNED_USERS_SQL =
			"SELECT user_id " +
				"FROM grimcodb.member " +
				"WHERE guild_id = '%s' " +
				"AND raffle_ban = true;";

	private final Guild guild;
	
	GuildDB(Guild guild) {
		this.guild=guild;
	}

	//prefix methods
	@Override
	public String getPrefix() {
		String prefix=null;
		
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				GET_PREFIX_SQL
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
								SET_PREFIX_SQL
				)){
				stmt.setString(1, prefix);
				stmt.setString(2, guild.getId());
				stmt.executeUpdate();
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
					.prepareStatement(REMOVE_PREFIX_SQL)
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
				SET_XP_TIMEOUT_SQL
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
				String.format(GET_XP_TIMEOUT_SQL, guild.getId())
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
				String.format(GET_XP_LOW_SQL, guild.getId())
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
				String.format(GET_XP_HI_SQL, guild.getId())
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
				SET_XP_BOUNDS_SQL
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
						String.format(GET_XP_SQL,guild.getId(), m.getUser().getId())
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
						ADD_XP_SQL)){

					long timestamp = System.currentTimeMillis();

					stmt.setInt(1, XP);
					stmt.setLong(2, timestamp);
					stmt.setString(3, m.getUser().getId());
					stmt.setString(4, m.getGuild().getId());
					stmt.executeUpdate();

				} catch (Exception e){
					Logging.error(e.getMessage());
					Logging.log(e);
				}
			}

			@Override
			public void removeXP(int XP){
				Logging.debug("Removing "+ XP + "XP from "+ m.getUser().getName()+":"+guild.getName());

				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						REMOVE_XP_SQL)){

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
						String.format(GET_LAST_XP_SQL,
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
						GET_IS_BANNED_SQL
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
						SET_BANNED_SQL
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
				ADD_MEMBER_SQL
		)){

			stmt.setString(1, m.getUser().getId());
			stmt.setString(2, guild.getId());
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
				REMOVE_MEMBER_SQL
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
						SET_XP_PERM_SQL
					)){

					stmt.setBoolean(1, earnEXP);
					stmt.setString(2, channel.getId());

					stmt.executeUpdate();
					
				} catch (SQLException e) {
					Logging.error("problem setting xp perm");
					Logging.log(e);
				}
				
			}

			@Override
			public boolean getXPPerm() {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						GET_XP_PERM_SQL
					)){
					
					stmt.setString(1, channel.getId());
					ResultSet rs = stmt.executeQuery();
					
					boolean rtn = DEFAULT_XP_SETTING;
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
						SET_RAFFLE_PERM_SQL
				)){

					stmt.setBoolean(1, raffle);
					stmt.setString(2, channel.getId());

					stmt.executeUpdate();

				} catch (SQLException e) {
					Logging.error("problem setting raffle perm");
					Logging.log(e);
				}
			}

			@Override
			public boolean getRaffle() {
				try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
						GET_RAFFLE_PERM_SQL
					)){

					stmt.setString(1, channel.getId());
					ResultSet rs = stmt.executeQuery();

					boolean rtn = DEFAULT_XP_SETTING;
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
				REMOVE_CHANNEL_SQL
		)){
			stmt.setString(1, channel.getId());
			stmt.execute();
		} catch (SQLException e) {
				Logging.error("problem deleting text channel data");
			Logging.log(e);
		}
	}

	@Override
	public void addChannel(TextChannel channel) {
		try (PreparedStatement stmt = BotDB.getConnection().prepareStatement(
				ADD_CHANNEL_SQL
		)){
			stmt.setString(1, channel.getId());
			stmt.setString(2, channel.getGuild().getId());
			stmt.execute();
		} catch (SQLException e) {
			Logging.error("problem deleting text channel data");
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
						GET_MANAGEMENT_ROLES_SQL
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
						String.format(GET_XP_THRESHOLD_SQL, guild.getId())
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
						SET_RAFFLE_THRESHOLD_SQL
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
						ADD_MANAGEMENT_ROLE_SQL
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
						REMOVE_MANAGEMENT_ROLE_SQL
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

			@Override
			public List<Member> getBannedUsers() { //todo add a command to see a list of banned users
				try (ResultSet rs = BotDB.getConnection().prepareStatement(
						String.format(GET_BANNED_USERS_SQL, guild.getId())
				).executeQuery()) {
					List<Member> rtn = new ArrayList<>();
					while (rs.next()) {
						rtn.add(guild.getMemberById(rs.getString(1)));
					}

					return rtn;

				} catch (SQLException e) {

					Logging.error("issue getting banned members");
					Logging.log(e);
					return null;

				}
			}
		};
	}
}
