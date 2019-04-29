package grimco.ranzer.rafflebot.util;

import grimco.ranzer.rafflebot.GrimcoRaffleBot;
import grimco.ranzer.rafflebot.config.BotConfiguration;
import net.dv8tion.jda.core.entities.PrivateChannel;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logging {

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss Z");

	private static void log(LogLevel level, String message) {
		BotConfiguration config = BotConfiguration.getInstance();

		String line = String.format("[%s\t%s] %s\n",level.name(), getTimestamp(), message);

		System.out.print(line);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(config.getLogLocation(), true))) {
			if (level == LogLevel.DEBUG && !config.isDebug()) {
				return;
			}

			writer.write(line);
			writer.close();
		}catch (FileNotFoundException e){
			System.out.println("you shouldn't be going this way");
			config.getLogLocation().getParentFile().mkdirs();
			try {
				config.getLogLocation().createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}catch (Exception ex) {
			System.out.println("Cannot log to file.");
			ex.printStackTrace();
		}
	}

	public static void debug(String message) {
		log(LogLevel.DEBUG, message);
	}

	public static void info(String message) {
		log( LogLevel.INFO, message);
	}

	public static void error(String message) {
		log(LogLevel.ERROR, message);
	}

    public static void log(Exception ex) {
        log(LogLevel.ERROR, ex.getClass().getCanonicalName() + ": " + ex.getMessage());
        for (StackTraceElement trace : ex.getStackTrace()) {
            log(LogLevel.ERROR, "\tat " + trace.toString());
        }
    }
    
    public static void messageBoss(LogLevel level, String message){
    	String line = String.format("[%s\t%s] %s\n",level.name(), getTimestamp(), message);
    	PrivateChannel channel = GrimcoRaffleBot.getJDA().getUserById(BotConfiguration.getInstance().getOwner()).openPrivateChannel().complete();
    	channel.sendMessage(line).queue();
    }

    private static String getTimestamp() {
        return TIME_FORMAT.format(Calendar.getInstance().getTime());
    }

}
