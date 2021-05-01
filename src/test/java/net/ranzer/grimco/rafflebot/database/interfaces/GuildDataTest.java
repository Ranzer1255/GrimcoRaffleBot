package net.ranzer.grimco.rafflebot.database.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.ranzer.grimco.rafflebot.JDATest;
import net.ranzer.grimco.rafflebot.config.BotConfiguration;
import net.ranzer.grimco.rafflebot.data.IGuildData;
import net.ranzer.grimco.rafflebot.database.HibernateManager;
import org.junit.jupiter.api.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GuildDataTest {
	static JDA jda;

	@BeforeAll
	static void setupAPI(){
		jda = JDATest.getJDA();
	}

	@BeforeEach
//	public void setupDB(){
//		Session s = HibernateManager.getSessionFactory().openSession();
//		s.beginTransaction();
//
////		Guild g = jda.getGuildById("530136980252786688");
//		GuildDataModel gdm = null;
//		for (Guild g: jda.getGuilds()) {
//			gdm = new GuildDataModel(g.getId());
//			Member m = g.retrieveMemberById("185046589381804032").complete();
////			System.out.println(m.getUser().getId());
//			gdm.addMember(m);
//			gdm.setPrefix("?");
//			s.saveOrUpdate(gdm);
//		}
//
//		s.getTransaction().commit();
//		s.close();
//	}

	@Test
	public void objectCreation(){

		IGuildData g = new GuildData(Objects.requireNonNull(jda.getGuildById("530136980252786688")));
	 	assertEquals(BotConfiguration.getInstance().getPrefix(), g.getPrefix());

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

		User u = jda.getUserById(185046589381804032L);

		assertNotNull(u);

		g.getMemberData(u).setBannedFromRaffle(true);

		assertEquals(1,g.getRaffleData().getBannedUsers().size());

	}

	@AfterEach
	public void resetDB(){
	}

	@AfterAll
	static void killAPI(){
		jda.shutdown();
	}

}