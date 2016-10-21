package cn.standardai.api.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class DateUtil {

	public static final long Second = 1000;
	public static final long Minute = 60 * Second;
	public static final long HalfHour = 30 * Minute;
	public static final long Hour = 60 * Minute;
	public static final long Day = 24 * Hour;

	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String YYYYMMDD_HHMMSS = "yyyyMMdd HHmmss";
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String YYMMDD = "yyMMdd";
	public static final String HHMMSS = "HHmmss";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYYYMM = "yyyyMM";
	public static final String YYYY = "yyyy";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY__MM = "yyyy/MM";
	public static final String YYYY__MM__DD = "yyyy/MM/dd";
	public static final String YYYY__MM__DD__HH = "yyyy/MM/dd HH";
	public static final String YYYY__MM__DD__HH__MM__SS = "yyyy/MM/dd HH:mm:ss";

	public static String TimeStamp2Date(String timestampString, String formats) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));

		return date;
	}

	public static Date StrToDate(String str) {
		return parse(str, YYYY_MM_DD_HH_MM_SS);
	}

	public static Date parse(String str, String format) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		try {
			return simpleDateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String format(Date date, String format) {
		if (date == null) {
			return "";
		}
		String tmpFormat = format;
		if (tmpFormat.isEmpty()) {
			tmpFormat = YYYY_MM_DD_HH_MM_SS;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tmpFormat);
		return simpleDateFormat.format(date);
	}

	public static Date getSpecifiedDay(Date date, int offset) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + offset);
		return c.getTime();
	}

	public static Date getLastDayOfMonth(int offYear, int offMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.YEAR, offYear);
		c.set(Calendar.MONTH, offMonth);
		c.add(Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
	}

	public static Date getFirstDayOfMonth(int offYear, int offMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.YEAR, offYear);
		c.set(Calendar.MONTH, offMonth - 1);
		return c.getTime();
	}

	public static String getCurrentDteStr(String pattern) {
		Date dte = new Date();
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(dte);
	}
}
