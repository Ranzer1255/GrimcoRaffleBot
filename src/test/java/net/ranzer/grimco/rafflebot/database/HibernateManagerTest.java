package net.ranzer.grimco.rafflebot.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HibernateManagerTest {

	@Test
	void returnsASessionFactory() {
		assertNotNull(HibernateManager.getSessionFactory());
	}
}