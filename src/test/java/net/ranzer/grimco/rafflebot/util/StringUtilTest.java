package net.ranzer.grimco.rafflebot.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

	@Test
	void arrayToString() {
		String expected = "this is a test array of strings";
		String[] testArray = {"this","is","a","test","array","of","strings"};
		ArrayList<String> testList = new ArrayList<>(Arrays.asList(testArray));

		assertEquals(expected,StringUtil.arrayToString(testArray," "));

		assertEquals(expected,StringUtil.arrayToString(testList," "));
	}

	@Test
	@Disabled
	void calcTime() {
		fail("not yet implemented");
	}

	@Test
	@Disabled
	void truncate() {
		fail("not yet implemented");
	}
}