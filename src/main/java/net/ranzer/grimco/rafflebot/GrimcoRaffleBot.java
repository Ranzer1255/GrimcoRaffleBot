package net.ranzer.grimco.rafflebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.GuildManager;
import net.ranzer.grimco.rafflebot.functions.levels.LevelUpdater;
import net.ranzer.grimco.rafflebot.functions.listeners.CommandListener;
import net.ranzer.grimco.rafflebot.util.Logging;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

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

		//setup intents
		Collection<GatewayIntent> intents = Arrays.asList(
			GatewayIntent.GUILD_MEMBERS, //privileged
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_VOICE_STATES
		);
		
		//set token
		if (config.isDebug()) {
			build = JDABuilder.create(config.getTestToken(),intents);
		} else {			
			build = JDABuilder.create(config.getToken(),intents);
		}

		build.enableIntents(GatewayIntent.GUILD_MEMBERS);
		build.setMemberCachePolicy(MemberCachePolicy.ALL);
		//add Listeners
		//TODO setup each module as its own command listener
		build.addEventListeners(new LevelUpdater(),
							   new StartUpListener());
		build.setActivity(Activity.watching("Waking up, please wait"));
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
		public void onReady(@NotNull ReadyEvent event) {
			super.onReady(event);
			JDA=event.getJDA();
			JDA.addEventListener(CommandListener.getInstance(),
								 new GuildManager());
			JDA.getPresence().setActivity(Activity.playing(config.getStatus()));
			
			Logging.info("Done Loading and ready to go!");
			JDA.getPresence().setStatus(OnlineStatus.ONLINE);
		}
		
		@Override
		public void onShutdown(@NotNull ShutdownEvent event) {
		
			Logging.info("Shutting down....");
			JDA.getPresence().setStatus(OnlineStatus.IDLE);
			JDA.getPresence().setActivity(Activity.playing("shutting down...."));
		}
		
	}
}
