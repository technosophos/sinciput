package com.technosophos.rhizome.util;

import java.text.DateFormat;
import java.util.Date;

/**
 * Standard time signature methods.
 * This class provides a number of utility features
 * for converting to and from a standard timestamp 
 * format.
 * @author mbutcher
 * @since 0.1
 */
public class Timestamp {

	/**
	 * Get a timestamp with the current date/time.
	 * @return the timestamp representing the current system time.
	 */
	public static String now() {
		return getTimeStamp(new Date());
	}
	
	/**
	 * Given a date, get a timestamp.
	 * @param d A date
	 * @return the timestamp for the given date
	 */
	public static String getTimeStamp(Date d) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String time = df.format(d);
		return time;
	}
	
}
