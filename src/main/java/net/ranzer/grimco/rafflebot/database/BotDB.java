package net.ranzer.grimco.rafflebot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.util.Logging;


public class BotDB {

	private static Connection connection;
	
	public static Connection getConnection(){
		BotConfiguration config = BotConfiguration.getInstance();
		try {
			if (connection == null || connection.isClosed()){
				String DBMS = config.getDatabaseManagementSystem();
				String host = config.getDatabaseHostname();
				Integer port = config.getDatabasePort();
				String user = config.getDatabaseUsername();
				String pw = config.getDatabasePassword();
				
				String DB;
				if (config.isDebug()) {
					DB = config.getTestDatabaseName();
				} else {
					DB = config.getDatabaseName();					
				}
				 
				connection = DriverManager.getConnection(String.format("jdbc:%s://%s:%d/%s?useSSL=false",DBMS,host,port,DB), user, pw);
				initDB(connection);
			}


			return connection;
		} catch (SQLException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
			return null;
		}
	}

	private static void initDB(Connection connection) throws SQLException {

		createSchema(connection);
		createTableGuild(connection);
		createTableMember(connection);
		createTableModeration_roles(connection);
		createTableRaffle_Roles(connection);
		createTableText_channel(connection);
		createTableTimedrole(connection);
		connection.commit();
	}

	private static void createSchema(Connection connection) throws SQLException {
		String SQL = "CREATE SCHEMA IF NOT EXISTS grimcodb;";
		connection.prepareStatement(SQL).execute();
	}

	private static void createTableGuild(Connection connection) throws SQLException {
		String SQL = "CREATE TABLE IF NOT EXISTS grimcodb.timedrole\n" +
				"(\n" +
				"   guild_id  varchar(20)   NOT NULL,\n" +
				"   user_id   varchar(20)   NOT NULL,\n" +
				"   role_id   varchar(20)   NOT NULL,\n" +
				"   remove    bigint        DEFAULT '0'::bigint\n" +
				");\n" +
				"\n" +
				"ALTER TABLE grimcodb.timedrole\n" +
				"   ADD CONSTRAINT timedrole_pkey\n" +
				"   PRIMARY KEY (guild_id, user_id);\n" +
				"\n" +
				"ALTER TABLE timedrole\n" +
				"  ADD CONSTRAINT timedrole_guild_id_fkey FOREIGN KEY (guild_id, user_id)\n" +
				"  REFERENCES grimcodb.member (guild_id, user_id)\n" +
				"  ON UPDATE CASCADE\n" +
				"  ON DELETE CASCADE;";

		connection.prepareStatement(SQL).execute();
	}

	private static void createTableMember(Connection connection) throws SQLException {
		String SQL = "CREATE TABLE IF NOT EXISTS grimcodb.member\n" +
				"(\n" +
				"   guild_id    varchar(20)   NOT NULL,\n" +
				"   user_id     varchar(20)   NOT NULL,\n" +
				"   xp          bigint        DEFAULT '0'::bigint,\n" +
				"   last_xp     bigint        DEFAULT '0'::bigint,\n" +
				"   raffle_ban  boolean       DEFAULT false\n" +
				");\n" +
				"\n" +
				"ALTER TABLE grimcodb.member\n" +
				"   ADD CONSTRAINT member_pkey\n" +
				"   PRIMARY KEY (guild_id, user_id);\n" +
				"\n" +
				"ALTER TABLE member\n" +
				"  ADD CONSTRAINT member_guild_id_fkey FOREIGN KEY (guild_id)\n" +
				"  REFERENCES grimcodb.guild (guild_id)\n" +
				"  ON UPDATE CASCADE\n" +
				"  ON DELETE CASCADE;";

		connection.prepareStatement(SQL).execute();
	}

	private static void createTableModeration_roles(Connection connection) throws SQLException {
		String SQL = "CREATE TABLE IF NOT EXISTS grimcodb.moderation_roles\n" +
				"(\n" +
				"   guild_id  varchar(20)   NOT NULL,\n" +
				"   role_id   varchar(20)   NOT NULL\n" +
				");\n" +
				"\n" +
				"ALTER TABLE moderation_roles\n" +
				"  ADD CONSTRAINT moderation_roles_guild_id_fkey FOREIGN KEY (guild_id)\n" +
				"  REFERENCES grimcodb.guild (guild_id)\n" +
				"  ON UPDATE CASCADE\n" +
				"  ON DELETE CASCADE;";

		connection.prepareStatement(SQL).execute();
	}

	private static void createTableRaffle_Roles(Connection connection) throws SQLException {
		String SQL = "CREATE TABLE IF NOT EXISTS grimcodb.raffle_roles\n" +
				"(\n" +
				"   guild_id  varchar(20)   NOT NULL,\n" +
				"   role_id   varchar(20)   NOT NULL\n" +
				");\n" +
				"\n" +
				"ALTER TABLE raffle_roles\n" +
				"  ADD CONSTRAINT raffle_roles_guild_id_fkey FOREIGN KEY (guild_id)\n" +
				"  REFERENCES grimcodb.guild (guild_id)\n" +
				"  ON UPDATE CASCADE\n" +
				"  ON DELETE CASCADE;";

		connection.prepareStatement(SQL).execute();
	}

	private static void createTableText_channel(Connection connection) throws SQLException {
		String SQL = "CREATE TABLE IF NOT EXISTS grimcodb.text_channel\n" +
				"(\n" +
				"   text_channel_id  varchar(20)   NOT NULL,\n" +
				"   guild_id         varchar(20)   NOT NULL,\n" +
				"   perm_raffle      boolean       DEFAULT false,\n" +
				"   perm_xp          boolean       DEFAULT true\n" +
				");\n" +
				"\n" +
				"ALTER TABLE grimcodb.text_channel\n" +
				"   ADD CONSTRAINT text_channel_pkey\n" +
				"   PRIMARY KEY (text_channel_id);\n" +
				"\n" +
				"ALTER TABLE text_channel\n" +
				"  ADD CONSTRAINT text_channel_guild_id_fkey FOREIGN KEY (guild_id)\n" +
				"  REFERENCES grimcodb.guild (guild_id)\n" +
				"  ON UPDATE CASCADE\n" +
				"  ON DELETE CASCADE;";

		connection.prepareStatement(SQL).execute();
	}

	private static void createTableTimedrole(Connection connection) throws SQLException {
		String SQL = "CREATE TABLE IF NOT EXISTS grimcodb.timedrole\n" +
				"(\n" +
				"   guild_id  varchar(20)   NOT NULL,\n" +
				"   user_id   varchar(20)   NOT NULL,\n" +
				"   role_id   varchar(20)   NOT NULL,\n" +
				"   remove    bigint        DEFAULT '0'::bigint\n" +
				");\n" +
				"\n" +
				"ALTER TABLE grimcodb.timedrole\n" +
				"   ADD CONSTRAINT timedrole_pkey\n" +
				"   PRIMARY KEY (guild_id, user_id);\n" +
				"\n" +
				"ALTER TABLE timedrole\n" +
				"  ADD CONSTRAINT timedrole_guild_id_fkey FOREIGN KEY (guild_id, user_id)\n" +
				"  REFERENCES grimcodb.member (guild_id, user_id)\n" +
				"  ON UPDATE CASCADE\n" +
				"  ON DELETE CASCADE;";

		connection.prepareStatement(SQL).execute();
	}
}
