package grimco.ranzer.rafflebot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import grimco.ranzer.rafflebot.config.BotConfiguration;
import grimco.ranzer.rafflebot.util.Logging;


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
			}
			
			return connection;
		} catch (SQLException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
			return null;
		}
	}
}
