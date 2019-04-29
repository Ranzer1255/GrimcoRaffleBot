package grimco.ranzer.rafflebot.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.jar.Manifest;

public class BotConfiguration {
	
	private static final String CONFIG_PATH = "/raffleBot/config/raffleBot.conf";
	private static BotConfiguration instance;
	
	private String databaseManagementSystem = "mysql";
	private String databaseHostname = "localhost";
	private Integer databasePort = 3306;
	private String databasePassword = "password";
	private String databaseUsername = "username";
	private String databaseName = "db name";
	private String testDatabaseName = "test DB name";
	private Boolean debug = false;
	private File logLocation;
	private String prefix = "!";
	private String botToken = "token";
	private String testToken = "token";
	private String statusMessage = "not here to replace you! -Grimco HR";
	private String owner = "userID";
	private String version = "Test_Build";
	
	private long lastModified = 0;
	
	private BotConfiguration(){

		//Default path for log Location
		String logPath = "/raffleBot/logs/raffleBot.log";
		this.logLocation =new File(System.getProperty("user.home"), logPath);
	}
	
	//getters
	public static BotConfiguration getInstance() {
		
		if (instance==null){
			instance = new BotConfiguration();
			instance.load();
			instance.startConfigMonitorThread();
		}
		return instance;
	}

	public String getStatus() {
		return statusMessage;
	}

	public String getDatabaseManagementSystem() {
		return databaseManagementSystem;
	}

	public String getDatabaseHostname() {
		return databaseHostname;
	}

	public Integer getDatabasePort() {
		return databasePort;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getTestDatabaseName() {
		return testDatabaseName;
	}

	public File getLogLocation() {
		return this.logLocation;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getToken() {
		return botToken;
	}
	
	public String getTestToken() {
		return testToken;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getOwner() {
		return this.owner;
	}

	public String getVersion() {
		return version;
	}

	//setters
	@BotConfigItem(key="statusMessage", type=String.class,_default="flavor text")
	public void setStatus(String status) {
		this.statusMessage = status;
	}
	@BotConfigItem(key="DBMS", type=String.class, _default="mysql")
	public void setDatabaseManagementSystem(String databaseManagementSystem) {
		this.databaseManagementSystem = databaseManagementSystem;
	}
	@BotConfigItem(key="dbHostName", type=String.class, _default="localhost")
	public void setDatabaseHostname(String databaseHostname) {
		this.databaseHostname = databaseHostname;
	}
	@BotConfigItem(key="dbPort", type=Integer.class, _default="3306")
	public void setDatabasePort(Integer databasePort) {
		this.databasePort = databasePort;
	}
	@BotConfigItem(key="dbUsername", type=String.class, _default="username")
	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}
	@BotConfigItem(key="dbPassword", type=String.class, _default="password")
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}
	@BotConfigItem(key="dbName", type=String.class,_default="db-name")
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	@BotConfigItem(key="testDBName", type=String.class, _default="test-db-name")
	public void setTestDatabaseName(String testDatabaseName) {
		this.testDatabaseName = testDatabaseName;
	}
	@BotConfigItem(key="logLocation", type=String.class, _default="/raffleBot/logs/raffleBot.log")
	public void setLogLocation(String logLocation) {
		this.logLocation =new File(System.getProperty("user.home"),logLocation);
	}
	@BotConfigItem(key="debug", type=Boolean.class, _default="false")
	public void setDebug(Boolean debug) {
		this.debug = debug;
	}
	@BotConfigItem(key="prefix", type=String.class, _default="!")
	public void setPrefix(String prefix){
		this.prefix=prefix;
	}
	@BotConfigItem(key="botToken", type=String.class, _default="token")
	public void setBotToken(String token){
		this.botToken = token;
	}
	@BotConfigItem(key="testToken", type=String.class, _default="token")
	public void setTestToken(String token) {
		testToken=token;
	}
	@BotConfigItem(key="owner",type=String.class, _default = "discord userID for owner")
	public void setOwner(String owner){
		this.owner = owner; 
	}

	private void setVersion(String version) {
		System.out.printf("[CaexConfig] setVersion: (%s)\n", version);
		this.version = version;
	}

	private void load() {

		loadVersionFromJAR(); 
		
		try {
			String home = System.getProperty("user.home");
			File configurationFile = new File(home, CONFIG_PATH);
			lastModified = configurationFile.lastModified();
			Properties properties = new Properties();
			properties.load(new FileReader(configurationFile));

			for (Method method : BotConfiguration.class.getMethods()) {
				if (method.isAnnotationPresent(BotConfigItem.class)) {
					BotConfigItem item = method.getAnnotation(BotConfigItem.class);

					if (properties.containsKey(item.key())) {
						if (item.type() == Integer.class) {
							Integer value = Integer.parseInt((String) properties.get(item.key()));
							method.invoke(this, value);
							continue;
						} else if (item.type() == Boolean.class) {
							Boolean value = Boolean.parseBoolean((String) properties.get(item.key()));
							method.invoke(this, value);
							continue;
						}

						String value = (String) properties.get(item.key());
						method.invoke(this, value);
					}
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println(String.format("Configuration file '%s' not found, writing default configuration values.", CONFIG_PATH));
			File configurationFile = new File(System.getProperty("user.home"), CONFIG_PATH);
			configurationFile.getParentFile().mkdirs();
			try {
				configurationFile.createNewFile();
				BufferedWriter w = new BufferedWriter(new FileWriter(configurationFile));
				for (Method method: BotConfiguration.class.getMethods()) {
					if(method.isAnnotationPresent(BotConfigItem.class)){
						BotConfigItem ann = method.getAnnotation(BotConfigItem.class);
						w.write(ann	.key()+"="+ann._default());
						w.newLine();
					}
				}
				w.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			System.out.println(String.format("Exception when loading in configuration, using default configuration values.", CONFIG_PATH));
			ex.printStackTrace();
		}
		
		

	}
	
	private void loadVersionFromJAR(){
				try {
					Manifest manifest = new Manifest(getClass().getClassLoader().getResources("META-INF/MANIFEST.MF").nextElement().openStream());
					String version = manifest.getMainAttributes().getValue("version");
					setVersion(version!=null?version:"TESTING_VERSION");
				} catch (IOException e) {
					System.out.println("error loading version from JAR");
					setVersion("TESTING_VERSION");
				}
	}

	private void startConfigMonitorThread() {
		
	new Thread(){
		@Override
		public void run() {
			
			setName("Config Monitor");
			
			while(true){
				String home = System.getProperty("user.home");
				File configurationFile = new File(home, CONFIG_PATH);
				if(configurationFile.lastModified()!=lastModified){
					load();
				}
				try {sleep(60000);} catch (InterruptedException e) {}
			}
		}
	}.start();
		
	}
}
