package com.busicomjp.sapp.service.b;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;

import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.ReportUtil;
import com.busicomjp.sapp.model.b.AccountsSummaryData;
import com.busicomjp.sapp.model.b.TorihikisakiSummaryData;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public abstract class B4_5_6AbstractService extends BAbstractService {
	private final String CONST_TEMPLATE_NAME = "Account_Money_Summary.jrxml";

	protected List<TorihikisakiSummaryData> _editDataForDisplay(String startYearMonth, String endYearMonth,
			List<AccountsSummaryData> searchResultList, Map<String, Long> carryForwardMoneyMap,
			Map<String, String> lastTradingDateMap,
			boolean reportFlag) {
		List<TorihikisakiSummaryData> dataList = new ArrayList<TorihikisakiSummaryData>();
		TorihikisakiSummaryData data = null;

		try {
			List<String> yearMonthList = getDisplayYearMonthList(startYearMonth, endYearMonth);

			Map<String, Map<String, AccountsSummaryData>> torihikisakiMap = null;
			torihikisakiMap = _getWholeYearDataOfFiscalYear(searchResultList, carryForwardMoneyMap);

			Map<String, AccountsSummaryData> yearMonthMap = null;
			AccountsSummaryData searchResult = null;
			Long carryForwardAmountMoney = 0L;
			String lastTradingDate = "";
			int loop = 1;
			Class<?> _class = null;
			Field _field = null;
			for (String torihikisakiCode : torihikisakiMap.keySet()) {
				data = new TorihikisakiSummaryData();
				_class = data.getClass();
				loop = 1;

				// 前期繰越
				carryForwardAmountMoney = carryForwardMoneyMap.get(torihikisakiCode);
				if (carryForwardAmountMoney == null) {
					carryForwardAmountMoney = 0L;
				}
				data.setCarryForwardAmountMoney(_formatByComma(carryForwardAmountMoney, reportFlag));
				// 最終取引日
				lastTradingDate = lastTradingDateMap.get(torihikisakiCode);
				if (InputCheck.isNullOrBlank(lastTradingDate)) {
					lastTradingDate = "";
				} else {
					lastTradingDate = "(" + lastTradingDate + ")";
				}
				data.setLastTradingDate(lastTradingDate);

				yearMonthMap = torihikisakiMap.get(torihikisakiCode);
				for (String yearMonth : yearMonthList) {
					searchResult = yearMonthMap.get(yearMonth);

					data.setTorihikisakiCode(searchResult.getTorihikisakiCode());
					data.setTorihikisakiName(searchResult.getTorihikisakiName());
					data.setTorihikisakiAndDate(data.getTorihikisakiName() + data.getLastTradingDate());
					_field = _class.getDeclaredField("month" + String.valueOf(loop));
					if (_field != null) {
						_field.setAccessible(true);
						_field.set(data, yearMonth);
					}
					_field = _class.getDeclaredField("depPay" + String.valueOf(loop));
					if (_field != null) {
						_field.setAccessible(true);
						_field.set(data, _formatByComma(searchResult.getDepPay(), reportFlag));
					}
					_field = _class.getDeclaredField("request" + String.valueOf(loop));
					if (_field != null) {
						_field.setAccessible(true);
						_field.set(data, _formatByComma(searchResult.getRequest(), reportFlag));
					}
					_field = _class.getDeclaredField("balance" + String.valueOf(loop));
					if (_field != null) {
						_field.setAccessible(true);
						_field.set(data, _formatByComma(searchResult.getBalance(), reportFlag));
					}

					loop += 1;
				}

				dataList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataList;
	}
	
	private Map<String, Map<String, AccountsSummaryData>> _getWholeYearDataOfFiscalYear(
			List<AccountsSummaryData> searchResultList, Map<String, Long> carryForwardAmountMoneyMap) {
		Map<String, Map<String, AccountsSummaryData>> torihikisakiMap = null;
		torihikisakiMap = new LinkedHashMap<String, Map<String, AccountsSummaryData>>();
		Map<String, AccountsSummaryData> yearMonthMap = null;
		for (AccountsSummaryData searchResult : searchResultList) {
			yearMonthMap = torihikisakiMap.get(searchResult.getTorihikisakiCode());
			if (yearMonthMap == null) {
				yearMonthMap = new HashMap<String, AccountsSummaryData>();
			}
			yearMonthMap.put(searchResult.getAccrualDate(), searchResult);
			torihikisakiMap.put(searchResult.getTorihikisakiCode(), yearMonthMap);
		}

		List<String> yearMonthList = new ArrayList<String>();
		Date kiStartDate = getKiStartDate();
		for (int offsetMonth = 0; offsetMonth < 12; offsetMonth++) {
			String yearMonth = dateYMFormat.format(getYearMonth(kiStartDate, offsetMonth));

			yearMonthList.add(yearMonth);
		}

		AccountsSummaryData searchResult = null;
		AccountsSummaryData data = null;
		for (String torihikisakiCode : torihikisakiMap.keySet()) {
			yearMonthMap = torihikisakiMap.get(torihikisakiCode);
			for (String _yearMonth : yearMonthMap.keySet()) {
				searchResult = yearMonthMap.get(_yearMonth);

				break;
			}

			for (String yearMonth : yearMonthList) {
				data = yearMonthMap.get(yearMonth);
				if (data == null) {
					data = new AccountsSummaryData();
					BeanUtils.copyProperties(searchResult, data);
					// 入金
					data.setDepPay(0L);
					// 請求
					data.setRequest(0L);
					// 残高
					data.setBalance(0L);
				}
				yearMonthMap.put(yearMonth, data);
			}
		}

		String kiStartYearMonth = dateYMFormat.format(getKiStartDate());
		// 残高を計算する
		Long balance = 0L;
		Long carryForwardAmountMoney = 0L;
		for (String torihikisakiCode : torihikisakiMap.keySet()) {
			balance = 0L;
			yearMonthMap = torihikisakiMap.get(torihikisakiCode);
			carryForwardAmountMoney = carryForwardAmountMoneyMap.get(torihikisakiCode);
			if (carryForwardAmountMoney == null) {
				carryForwardAmountMoney = 0L;
			}

			for (String yearMonth : yearMonthList) {
				searchResult = yearMonthMap.get(yearMonth);

				if (kiStartYearMonth.equals(yearMonth)) {
					balance = carryForwardAmountMoney - searchResult.getDepPay() + searchResult.getRequest();
				} else {
					balance = balance - searchResult.getDepPay() + searchResult.getRequest();
				}
				searchResult.setBalance(balance);

				yearMonthMap.put(yearMonth, searchResult);
			}
		}

		return torihikisakiMap;
	}
	
	protected Map<String, Long> _getCarryForwardMoneyOfSuppliers(List<Map<String, Object>> carryForwardMoneyList) {
		Map<String, Long> carryForwardMoneyMap = new HashMap<String, Long>();

		carryForwardMoneyList.forEach(row -> {
			Object amountMoney = row.get("amountMoney");
			if (InputCheck.isNullOrBlank(amountMoney)) {
				amountMoney = "0";
			}
			carryForwardMoneyMap.put((String) row.get("suppliersCode"), Long.parseLong(String.valueOf(amountMoney)));
		});

		return carryForwardMoneyMap;
	}
	
	protected Map<String, String> _getLastTradingDateOfSuppliers(List<Map<String, Object>> lastTradingDateList) {
		Map<String, String> lastTradingDateMap = new HashMap<String, String>();

		lastTradingDateList.forEach(row -> {
			Object tradingDate = row.get("accrualDate");
			if (InputCheck.isNullOrBlank(tradingDate)) {
				tradingDate = "";
			}
			lastTradingDateMap.put((String) row.get("suppliersCode"), String.valueOf(tradingDate));
		});

		return lastTradingDateMap;
	}

	protected String _generatePdfReport(String reportName, String moneyType, Calendar startCalendar, Calendar endCalendar, List<TorihikisakiSummaryData> dataList) {
		String filePath = ReportUtil.getReportFilePath(reportOutputPath, reportName);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reportName", reportName);
		parameters.put("moneyTypeNyuukin", moneyType);

		SimpleDateFormat dateYMDReportFormat = new SimpleDateFormat(REPORT_DATE_PATTERN);
		String startEndSDate = String.format(REPORT_DATE_FROMTO_PATTERN,
				dateYMDReportFormat.format(startCalendar.getTime()), dateYMDReportFormat.format(endCalendar.getTime()));
		parameters.put("startEndSDate", startEndSDate);
		Date kiStartDate = startCalendar.getTime();
		for (int offsetMonth = 0; offsetMonth < 12; offsetMonth++) {
			String yearMonth = dateYMFormat.format(getYearMonth(kiStartDate, offsetMonth));
			yearMonth = yearMonth.substring(0, 4) + "/" + yearMonth.substring(4);

			parameters.put("yearMonth" + String.valueOf(offsetMonth + 1), yearMonth);
		}

		try (InputStream template = new ClassPathResource(jasperJrxmlFilePath + CONST_TEMPLATE_NAME).getInputStream()) {
			JasperReport report = JasperCompileManager.compileReport(template);

			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(dataList);

			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, source);
			JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filePath;
	}
}
