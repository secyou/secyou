package com.busicomjp.sapp.service.b;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;

public abstract class BAbstractService {
	protected final SimpleDateFormat dateYMFormat = new SimpleDateFormat("yyyyMM");
	protected final SimpleDateFormat dateYMDFormat = new SimpleDateFormat("yyyyMMdd");
	protected final String REPORT_DATE_PATTERN = "yyyy年MM月dd日";
	protected final String REPORT_DATE_FROMTO_PATTERN = "自%s 至 %s";

	@Value("${jasper.jrxml.file.path}")
	protected String jasperJrxmlFilePath;
	@Value("${report.output.path}")
	protected String reportOutputPath;

	public Date getKiStartDate() {
		Date startDate = null;
		String kimatuMonthDay = CompanyUtil.getKimatuMonthDay();
		if (!InputCheck.isNullOrBlank(kimatuMonthDay)) {
			int year = Integer.parseInt(CompanyUtil.getKimatuYear());
			int month = Integer.parseInt(kimatuMonthDay.substring(0, 2));
			int date = 1;

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(year, month, date, 0, 0, 0);
			endCalendar.add(Calendar.YEAR, -1);

			startDate = endCalendar.getTime();
		}

		return startDate;
	}

	public Date getKiEndMonth() {
		Date endDate = null;
		String kimatuMonthDay = CompanyUtil.getKimatuMonthDay();
		if (!InputCheck.isNullOrBlank(kimatuMonthDay)) {
			int year = Integer.parseInt(CompanyUtil.getKimatuYear());
			int month = Integer.parseInt(kimatuMonthDay.substring(0, 2));
			int date = 1;

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(year, month, date, 0, 0, 0);
			endCalendar.add(Calendar.MONTH, -1);

			endDate = endCalendar.getTime();
		}

		return endDate;
	}

	public Date getYearMonth(Date currentDate, int offsetMonth) {
		Date date = null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);

		calendar.add(Calendar.MONTH, offsetMonth);

		if (offsetMonth > 0) {
			Date kiEndDate = getKiEndMonth();
			if (compareYearMonthDate(calendar.getTime(), kiEndDate) > 0) {
				date = kiEndDate;
			} else {
				date = calendar.getTime();
			}
		} else if (offsetMonth < 0) {
			Date kiStartDate = getKiStartDate();
			if (compareYearMonthDate(calendar.getTime(), kiStartDate) < 0) {
				date = kiStartDate;
			} else {
				date = calendar.getTime();
			}
		} else if (offsetMonth == 0) {
			date = currentDate;
		}

		return date;
	}

	public int compareYearMonthDate(Date date1, Date date2) {
		String _date1 = dateYMFormat.format(date1);
		String _date2 = dateYMFormat.format(date2);

		return _date1.compareTo(_date2);
	}

	public List<String> getDisplayYearMonthList(String startYearMonth, String endYearMonth) {
		List<String> yearMonthList = new ArrayList<String>();

		try {
			Date startDate = dateYMDFormat.parse(startYearMonth + "01");
			for (int offsetMonth = 0; offsetMonth < 12; offsetMonth++) {
				String yearMonth = dateYMFormat.format(getYearMonth(startDate, offsetMonth));
				yearMonthList.add(yearMonth);

				if (yearMonth.equals(endYearMonth)) {
					break;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return yearMonthList;
	}
	
	protected List<String> getYearMonthList(String startYearMonth, String endYearMonth) {
		List<String> yearMonthList = new ArrayList<String>();

		Date kiStartDate = getKiStartDate();
		for (int offsetMonth = 0; offsetMonth < 12; offsetMonth++) {
			String yearMonth = dateYMFormat.format(getYearMonth(kiStartDate, offsetMonth));

			yearMonthList.add(yearMonth);
		}

		return yearMonthList;
	}
	
	protected String _formatByComma(Long number, boolean reportFlag) {
		if (number == null) {
			return "0";
		}
		String strNumber = String.format("%,d", number);
		if (reportFlag) {
			strNumber = strNumber.replace("-", "△");
		}

		return strNumber;
	}
}
