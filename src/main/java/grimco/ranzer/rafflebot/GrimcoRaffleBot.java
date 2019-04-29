package grimco.ranzer.rafflebot;

import java.time.LocalDateTime;

import javax.security.auth.login.LoginException;

import grimco.ranzer.rafflebot.config.BotConfiguration;
import grimco.ranzer.rafflebot.data.GuildManager;
import grimco.ranzer.rafflebot.functions.levels.LevelUpdater;
import grimco.ranzer.rafflebot.functions.listeners.CommandListener;
import grimco.ranzer.rafflebot.util.Logging;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * Raffle bot Built for The League of Ordinary Gamers
 *
 * @author Ranzer
 *
 */
public class GrimcoRaffleBot {
	
	private static JDA JDA;
	public final static LocalDateTime START_TIME = LocalDateTime.now();
	private static final BotConfiguration config = BotConfiguration.getInstance();

	public static void main (String[] args){
		Logging.info("Huu... Wha... who... Oh, I guess it's time to [start up]");
		
		
		
		JDABuilder build;
		
		//set token
		if (config.isDebug()) {
			build = new JDABuilder(AccountType.BOT).setToken(config.getTestToken());
		} else {			
			build = new JDABuilder(AccountType.BOT).setToken(config.getToken());
		}
		
		//add Listeners
		//TODO setup each module as its own command listner
		build.addEventListener(CommandListener.getInstance(),
							   new LevelUpdater(),
							   new StartUpListener());
		build.setGame(Game.playing("Waking up, please wait"));
		build.setStatus(OnlineStatus.DO_NOT_DISTURB);
		
		//build
		try {
			JDA = build.build();
		} catch (LoginException | IllegalArgumentException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
		}
	}	

	public static JDA getJDA(){
		return JDA;
	}
	
	private static class StartUpListener extends ListenerAdapter{
		
		
		
		@Override
		public void onReady(ReadyEvent event) {
			super.onReady(event);
			JDA=event.getJDA();
			JDA.addEventListener(new GuildManager());
			JDA.getPresence().setGame(Game.playing(config.getStatus()));
			
			Logging.info("Done Loading and ready to go!");
			JDA.getPresence().setStatus(OnlineStatus.ONLINE);
		}
		
		@Override
		public void onShutdown(ShutdownEvent event) {
		
			Logging.info("Shutting down....");
			JDA.getPresence().setStatus(OnlineStatus.IDLE);
			JDA.getPresence().setGame(Game.playing("shutting down...."));
		}
		
	}
}
