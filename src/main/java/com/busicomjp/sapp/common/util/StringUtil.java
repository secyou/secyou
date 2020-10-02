package com.busicomjp.sapp.common.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	private static NumberFormat comma = NumberFormat.getNumberInstance();

	public static String commaFormat(long value) {
		return comma.format(value);
	}

	public static String commaFormat(String value) {
		return comma.format(Long.valueOf(value));
	}

	public static String dateSlashFormat(String value) {
		if (StringUtils.isEmpty(value) || value.length() != 8) {
			return value;
		}
		return value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6, 8);
	}

	public static String replaceCommaFormat(String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}
		return value.replace(",", "");
	}

	public static String replaceDateSlashFormat(String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}
		return value.replace("/", "");
	}

	public static String getLastDayOfMonth(String ymd) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Calendar calendar = Calendar.getInstance();
			Date date = sdf.parse(ymd);
			calendar.setTime(date);
			int lastDayOfManth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			return ymd.substring(0, 6) + lastDayOfManth;
		} catch (ParseException e) {
			return ymd;
		}
	}

	public static String GetLastDay(String StartDay) {

		String SysYmd = StartDay;

		int intStartYY = java.lang.Integer.parseInt(SysYmd.substring(0, 4));
		int intStartMM = java.lang.Integer.parseInt(SysYmd.substring(4, 6));
		int intStartDD = 1;

		GregorianCalendar gc = new GregorianCalendar(intStartYY, intStartMM - 1, intStartDD);

		int intTodayD = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		String strSdateDD;
		if (intTodayD < 10) {
			strSdateDD = "0" + String.valueOf(intTodayD);
		} else {
			strSdateDD = String.valueOf(intTodayD);
		}

		return strSdateDD;
	}
	
	public static String getToday() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	
	public static String getNextDay(String ymd) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Calendar calendar = Calendar.getInstance();
			Date date = sdf.parse(ymd);
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			return sdf.format(calendar.getTime());
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String getNextMonth(String ymd) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Calendar calendar = Calendar.getInstance();
			Date date = sdf.parse(ymd);
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, 1);
			return sdf.format(calendar.getTime()).substring(0, 6); // YYYYMM形式
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
     * 全角数字を半角数字に変える
     */
	public static String replaceFull2HalfNumber(String fullNumber) {
		if (StringUtils.isEmpty(fullNumber)) {
			return fullNumber;
		}
		String replace = fullNumber.replace("０", "0").replace("１", "1").replace("２", "2").replace("３", "3")
				.replace("４", "4").replace("５", "5").replace("６", "6").replace("７", "7").replace("８", "8")
				.replace("９", "9");
		if (InputCheck.isNumeric(replace)) {
			return replace;
		} else {
			return fullNumber;
		}
	}
}
