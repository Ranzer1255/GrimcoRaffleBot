package net.ranzer.grimco.rafflebot.util;

import java.util.Arrays;
import java.util.Collection;

public class StringUtil {

	/**
	 * pieces together a collection of strings separated by the supplied delimiter
	 * @param stringArray the collection of strings to be appended
	 * @param delimiter the separator between each string in the stringArray
	 * @return a single string of all the strings in the stringArray separated by the delimiter
	 */
	public static String arrayToString(Collection<String> stringArray, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for (String s: stringArray) {
			sb.append(s).append(delimiter);
		}
		sb.delete(sb.length()-delimiter.length(),sb.length());

		return sb.toString();
	}

	/**
	 * overloaded method of {@link StringUtil#arrayToString(Collection stringArray, String delimiter)}
	 */
	public static String arrayToString(String[] stringArray, String delimiter){
		return arrayToString(Arrays.asList(stringArray), delimiter);
	}

	/**
	 * calculates a human readable time from a seconds long
	 * @param runtime time in seconds
	 * @return human readable time string
	 */
	public static String calcTime(long runtime) {
		StringBuilder rtn = new StringBuilder();

		long hrs = runtime / 3600;
		long mins = (runtime % 3600)/60;
		long secs = runtime % 60;

		if(hrs >1){
			rtn.append(hrs).append(" Hours ");
		} else if (hrs==1){
			rtn.append(hrs).append(" Hour ");
		}
		if(mins>1){
			rtn.append(mins).append(" Minutes ");
		} else if (mins==1){
			rtn.append(mins).append(" Minute ");
		}
		if (secs>1){
			rtn.append(secs).append(" Seconds");
		} else if (secs==1){
			rtn.append(secs).append(" Second");
		}
		return rtn.toString();
	}

	/**
	 * @param string String to be shortened
	 * @param size max length of returned string
	 * @return shortened version of the supplied string. if the string is shorter
	 * than the max length, it returns the string as is
	 */
	public static String truncate(String string, int size) {
		if (string.length()<size) {
			return string;
		} else {
			return string.substring(0, size);
		}
	}

	/**
	 * overloaded method with default width of 32
	 * @param current current length to be displayed
	 * @param total the total length to be represented
	 * @return a discord formatted code block with a graphical progress bar
	 * @see StringUtil#playingBar(long current, long total, int width)
	 */
	public static String playingBar(long current, long total){
		return playingBar(current,total,32);
	}

	/**
	 * calculates the proper tic per time
	 * @param current current length to be displayed
	 * @param total the total length to be represented
	 * @param width the overall width of the progress bar to be displayed
	 * @return a discord formatted code block with a graphical progress bar
	 */
	public static String playingBar(long current, long total,int width) {
		StringBuilder sb = new StringBuilder();

		long timePerBar = total/width;

		sb.append("```\n");
		sb.append("-");
		sb.append("-".repeat(Math.max(0, width)));
		sb.append("-\n");
		sb.append("|");

		for (int i = 0; i < current/timePerBar; i++) {
			sb.append("=");
		}
		sb.append("|");
		for (int i = 0; i < width-1-(current/timePerBar); i++) {
			sb.append(" ");
		}

		sb.append("|\n");
		sb.append("-");
		sb.append("-".repeat(Math.max(0, width)));
		sb.append("-\n")
				.append("```\n")
				.append(calcTime(current / 1000))
				.append(" of ")
				.append(calcTime(total / 1000));

		return sb.toString();
	}
}
