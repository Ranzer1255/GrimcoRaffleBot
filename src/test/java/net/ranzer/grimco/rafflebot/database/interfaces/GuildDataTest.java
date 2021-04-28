package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import net.ranzer.grimco.rafflebot.database.model.GuildDataModel;
import net.ranzer.grimco.rafflebot.util.Logging;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

		try {
			jda = build.build().awaitReady();
		} catch (LoginException | IllegalArgumentException | InterruptedException e) {
			Logging.error(e.getMessage());
			Logging.log(e);
		}

	}

	@BeforeEach
	public void setupDB(){
		Session s = HibernateManager.getSessionFactory().openSession();
		s.beginTransaction();

//		Guild g = jda.getGuildById("530136980252786688");
		GuildDataModel gdm = null;
		for (Guild g: jda.getGuilds()) {
			gdm = new GuildDataModel(g.getId());
			Member m = g.retrieveMemberById("185046589381804032").complete();
//			System.out.println(m.getUser().getId());
			gdm.addMember(m);
			gdm.setPrefix("?");
			s.saveOrUpdate(gdm);
		}

		s.getTransaction().commit();
		s.close();
	}

	@Test
	public void objectCreation(){

		IGuildData g = new GuildData(Objects.requireNonNull(jda.getGuildById("530136980252786688")));
	 	assertEquals("?", g.getPrefix());

	}

	@Test
	public void testSetPrefix(){

		IGuildData g = new GuildData(jda.getGuildById("530136980252786688"));

		g.setPrefix("/");

		IGuildData underTest = new GuildData(jda.getGuildById("530136980252786688"));

		assertEquals("/", underTest.getPrefix());

	}

	@Test
	public void testGetBannedUsers(){
		IGuildData g = new GuildData(jda.getGuildById("530136980252786688"));

		g.getMemberData(jda.getUserById(185046589381804032L)).setBannedFromRaffle(true);

		assertEquals(1,g.getRaffleData().getBannedUsers().size());

	}

	@AfterAll
	static void killAPI(){
		jda.shutdown();
	}

}