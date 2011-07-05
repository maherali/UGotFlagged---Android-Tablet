package com.agilismobility.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatter {

	public static DateFormat timeFormat = new SimpleDateFormat("h:mm aa");
	public static DateFormat dateLongFormat = new SimpleDateFormat("EEE, MMM d");
	public static DateFormat dateShortFormat = new SimpleDateFormat("MMM d");
	public static DateFormat dateShortWithDayFormat = new SimpleDateFormat("EEEE, MMMM d");
	public static DateFormat dateYearFormat = new SimpleDateFormat("MMM d, yyyy");
	public static DateFormat dateYearTimeFormat = new SimpleDateFormat("MMM d, yyyy h:mm aa");
	public static DateFormat dateYearTimeLongFormat = new SimpleDateFormat("EEE, MMM d, yyyy h:mm aa");
	public static DateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static DateFormat dateMonthShortFormat = new SimpleDateFormat("MM/d");
	public static DateFormat dayOfWeekWithTimeFormat = new SimpleDateFormat("EEE h:mm aa");

	static {
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateLongFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateShortFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateShortWithDayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateYearFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateYearTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateYearTimeLongFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		serverFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	static synchronized public Date parseDateYearFormat(String yyyyMMddTHHmmss) {
		Date date = null;
		try {
			date = dateYearFormat.parse(yyyyMMddTHHmmss);
		} catch (ParseException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		return date;
	}

	static synchronized public Date parse(String yyyyMMddTHHmmss) {
		Date date = null;
		try {
			date = serverFormat.parse(yyyyMMddTHHmmss);
		} catch (ParseException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		return date;
	}

	static synchronized public String time(String yyyyMMddTHHmmss) {
		return time(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String dateLong(String yyyyMMddTHHmmss) {
		return dateLong(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String dateShort(String yyyyMMddTHHmmss) {
		return dateShort(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String dateYear(String yyyyMMddTHHmmss) {
		return dateYear(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String dateYearFromMilli(long time) {
		return dateYearFormat.format(time);
	}

	static synchronized public String dateYearTime(String yyyyMMddTHHmmss) {
		return dateYearTime(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String dateYearTimeLong(String yyyyMMddTHHmmss) {
		return dateYearTimeLong(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String timeAgo(String yyyyMMddTHHmmss) {
		return timeAgo(parse(yyyyMMddTHHmmss));
	}

	static synchronized public String dayOfWeekWithTimeFormat(Date date) {
		return dayOfWeekWithTimeFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String monthDateShort(Date date) {
		return dateMonthShortFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String time(Date date) {
		return timeFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String dateLong(Date date) {
		return dateLongFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String dateShort(Date date) {
		return dateShortFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String dateShortWithDay(Date date) {
		return dateShortWithDayFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String dateYear(Date date) {
		return dateYearFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String dateYearTime(Date date) {
		return dateYearTimeFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String dateYearTimeLong(Date date) {
		return dateYearTimeLongFormat.format(date).replace(" AM", "am").replace(" PM", "pm");
	}

	static synchronized public String timeAgo(Date date) {
		double littleOverAWeek = 11520.0;
		Date now = new Date();

		double deltaMinutes = (now.getTime() - date.getTime()) / 60000;

		if (deltaMinutes < littleOverAWeek) {
			return distanceOfTimeInWords(deltaMinutes) + " ago";
		} else {
			return "on " + dateYear(date);
		}
	}

	static synchronized private String distanceOfTimeInWords(double minutes) {
		String hourString = " hour";
		if ((int) ((minutes / 60.0) + 0.5) > 1.0) {
			hourString = " hours";
		}

		Object[] array = new Object[] { new Object[] { new Integer(1), "less than a minute" }, new Object[] { new Integer(20), "a few minutes" },
				new Object[] { new Integer(45), "about half an hour" }, new Object[] { new Integer(90), "about one hour" },
				new Object[] { new Integer(1080), (int) (minutes / 60) + hourString }, new Object[] { new Integer(1441), "one day" },
				new Object[] { new Integer(2880), "about one day" }, new Object[] { new Integer(8640), "a few days" },
				new Object[] { new Integer(11520), "about a week" } };

		for (int i = 0; i < array.length; i++) {
			Object[] map = (Object[]) array[i];
			if (minutes < ((Integer) map[0]).intValue()) {
				return (String) map[1];
			}
		}
		return "";
	}

	static synchronized public String dateInServerFormat(long time) {
		// Convert the time to a format accepted by the server YYYY-MM-DD
		Calendar date = Calendar.getInstance();
		date.setTime(new Date(time));
		// date.setTimeZone(TimeZone.getTimeZone("GMT"));
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int dateOfMonth = date.get(Calendar.DATE);

		return year + "-" + (month < 10 ? "0" + String.valueOf(month) : String.valueOf(month)) + "-"
				+ (dateOfMonth < 10 ? "0" + String.valueOf(dateOfMonth) : String.valueOf(dateOfMonth));
	}

	static synchronized public String datetimeInServerFormat(long time) {
		// Convert the time to a format accepted by the server
		// YYYY-MM-DDTHH:mm:ss
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.setTime(new Date(time));
		// date.setTimeZone(TimeZone.getTimeZone("GMT"));
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int dateOfMonth = date.get(Calendar.DATE);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int minute = date.get(Calendar.MINUTE);
		int second = date.get(Calendar.SECOND);

		return year + "-" + (month < 10 ? "0" + String.valueOf(month) : String.valueOf(month)) + "-"
				+ (dateOfMonth < 10 ? "0" + String.valueOf(dateOfMonth) : String.valueOf(dateOfMonth)) + "T"
				+ (hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour)) + ":" + (minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute))
				+ ":" + (second < 10 ? "0" + String.valueOf(second) : String.valueOf(second));
	}

	static synchronized public String datetimeInServerFormatandGMT(long time) {
		// Convert the time to a format accepted by the server
		// YYYY-MM-DDTHH:mm:ss
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		date.setTime(new Date(time));
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int dateOfMonth = date.get(Calendar.DATE);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int minute = date.get(Calendar.MINUTE);
		int second = date.get(Calendar.SECOND);

		return year + "-" + (month < 10 ? "0" + String.valueOf(month) : String.valueOf(month)) + "-"
				+ (dateOfMonth < 10 ? "0" + String.valueOf(dateOfMonth) : String.valueOf(dateOfMonth)) + "T"
				+ (hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour)) + ":" + (minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute))
				+ ":" + (second < 10 ? "0" + String.valueOf(second) : String.valueOf(second));
	}
}
