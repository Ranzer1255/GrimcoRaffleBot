package net.ranzer.grimco.rafflebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.util.Logging;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collection;

public class JDATest {

	public static JDA getJDA(){
		BotConfiguration config = BotConfiguration.getInstance();

		JDABuilder build;

		//setup intents
		Collection<GatewayIntent> intents = Arrays.asList(
				GatewayIntent.GUILD_MEMBERS, //privileged
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.GUILD_MESSAGES
		);

		//set token
		build = JDABuilder.create(config.getTestToken(),intents);

		try {
			return build.build().awaitReady();
		} catch (LoginException | IllegalArgumentException | InterruptedException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
			return null;
		}
	}
}
