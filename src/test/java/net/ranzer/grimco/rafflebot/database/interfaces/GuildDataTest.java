package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import net.ranzer.grimco.rafflebot.database.model.GuildDataModel;
import net.ranzer.grimco.rafflebot.util.Logging;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GuildDataTest {
	static JDA jda;

	@BeforeAll
	static void setupAPI(){
		BotConfiguration config = BotConfiguration.getInstance();

		JDABuilder build;

		//setup intents
		Collection<GatewayIntent> intents = Arrays.asList(
//				GatewayIntent.GUILD_MEMBERS, //privileged
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.GUILD_MESSAGES
		);

		//set token
		build = JDABuilder.create(config.getTestToken(),intents);

//		build.enableIntents(GatewayIntent.GUILD_MEMBERS);
//		build.setMemberCachePolicy(MemberCachePolicy.ALL);
		//add Listeners
		//TODO setup each module as its own command listener

		//build
		try {
			jda = build.build().awaitReady();
		} catch (LoginException | IllegalArgumentException | InterruptedException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
		}

		Session s = HibernateManager.getSessionFactory().openSession();
		s.beginTransaction();
		for (Guild g : jda.getGuilds()){
			GuildDataModel gdm = new GuildDataModel(g.getId());
			gdm.setPrefix("?");
			s.persist(gdm);
		}
		s.getTransaction().commit();
		s.close();

	}

	@Test
	public void objectCreation(){

		IGuildData g = new GuildData(Objects.requireNonNull(jda.getGuildById("530136980252786688")));
	 	assertEquals("?", g.getPrefix());

	}

	@AfterAll
	static void killAPI(){
		jda.shutdown();
	}

}