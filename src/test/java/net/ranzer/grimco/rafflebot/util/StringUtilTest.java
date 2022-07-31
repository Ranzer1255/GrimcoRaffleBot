package net.ranzer.grimco.rafflebot.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
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

	@Nested
	class TimeTests{

		@Test
		void testSingularTime() {
			String expected = "1 Hour 1 Minute 1 Second";
			assertEquals(expected, StringUtil.calcTime(3661L));
		}

		@Test
		void testPluralTime() {
			String expected = "2 Hours 2 Minutes 2 Seconds";
			assertEquals(expected, StringUtil.calcTime(7322L));
		}

		@Test
		void testNoHours(){
			String expected = "1 Minute 1 Second";
			assertEquals(expected, StringUtil.calcTime(61L));
		}

		@Test
		void testNoMinTime() {
			String expected = "1 Hour 1 Second";
			assertEquals(expected, StringUtil.calcTime(3601L));
		}

		@Test
		void testNoSecondTime() {
			String expected = "1 Hour 1 Minute ";
			assertEquals(expected, StringUtil.calcTime(3660L));
		}
	}

	@Nested
	class TruncateTests {

		@Test
		void longStringTrimming() {

		String toBeTested = "This is a long string that needs to be trimmed";
		String expected = "This is a long string";

		assertEquals(expected, StringUtil.truncate(toBeTested,21));
		}

		@Test
		void shortString(){
			String toBeTested = "this is a short string";
			String expected = "this is a short string";

			assertEquals(expected,StringUtil.truncate(toBeTested,50));
		}
	}
}