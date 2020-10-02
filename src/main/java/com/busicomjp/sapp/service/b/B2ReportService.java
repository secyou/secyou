package com.busicomjp.sapp.service.b;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.ReportUtil;
import com.busicomjp.sapp.model.b.GeneralLedgerData;
import com.busicomjp.sapp.model.b.GeneralLedgerReportData;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class B2ReportService {
	private final String CONST_TEMPLATE_NAME = "General_Ledger.jrxml";
	private final String CONST_REPORT_NAME = "総勘定元帳";
	@Value("${jasper.jrxml.file.path}")
	private String jasperJrxmlFilePath;
	@Value("${report.output.path}")
	private String reportOutputPath;
	
	public void generatePdfReport(List<GeneralLedgerData> searchResultList, String accountYear, String accountCode, String accountName) {
		Map<Integer, List<GeneralLedgerData>> searchResultMap = splitByPage(searchResultList);

		List<String> filePathList = new ArrayList<String>();
		String filePath = null;
		for (Integer pageNo : searchResultMap.keySet()) {
			filePath = generatePdfReportPage(pageNo, searchResultMap.get(pageNo), accountYear, accountCode,
					accountName);

			filePathList.add(filePath);
		}

		try {
			String newfilePath = ReportUtil.getReportFilePath(reportOutputPath, CONST_REPORT_NAME);
			ReportUtil.mergePdfFiles(filePathList, newfilePath);

			ReportUtil.openPdf(newfilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generatePdfReportPage(int pageNo, List<GeneralLedgerData> searchResultList, String accountYear,
			String accountCode, String accountName) {
		String filePath = ReportUtil.getReportFilePath(reportOutputPath, pageNo + "_" + CONST_REPORT_NAME);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pageNo", String.valueOf(pageNo));
		parameters.put("companyName", CompanyUtil.getCompanyName());
		parameters.put("accountCode", accountCode);
		parameters.put("accountName", accountName);
		parameters.put("accountYear", getAccountYear(searchResultList));

		try (InputStream template = new ClassPathResource(jasperJrxmlFilePath + CONST_TEMPLATE_NAME).getInputStream()) {
			JasperReport report = JasperCompileManager.compileReport(template);

			processAccrualDate(searchResultList);

			List<GeneralLedgerReportData> dataList = convertSearchResultToPdfData(searchResultList);
			JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(dataList);
			
			setLastThreeRowData(parameters, searchResultList);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, source);
			JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filePath;
	}
	
	private Map<Integer, List<GeneralLedgerData>> splitByPage(List<GeneralLedgerData> searchResultList) {
		Map<Integer, List<GeneralLedgerData>> pageDataMap = new LinkedHashMap<Integer, List<GeneralLedgerData>>();
		final int pageDataSize = 33;

		List<GeneralLedgerData> _searchResultList = new ArrayList<GeneralLedgerData>();
		for (GeneralLedgerData searchResult : searchResultList) {
			if (InputCheck.isNullOrBlank(searchResult.getGeneralNo())
					&& InputCheck.isNullOrBlank(searchResult.getCounterAccount())) {
				continue;
			}
			_searchResultList.add(searchResult);
		}

		List<GeneralLedgerData> dataList = null;
		int searchResultCounts = _searchResultList.size();
		if (searchResultCounts <= pageDataSize) {
			pageDataMap.put(1, _searchResultList);
		} else {
			int pageNo = 0;
			for (int i = 0; i < searchResultCounts; i++) {
				
				if (i % pageDataSize == 0) {
					pageNo += 1;
					dataList = new ArrayList<GeneralLedgerData>();

					pageDataMap.put(pageNo, dataList);
				}
				dataList.add(_searchResultList.get(i));
			}
		}

		for (Integer pageNo : pageDataMap.keySet()) {
			dataList = pageDataMap.get(pageNo);
			while (dataList.size() < pageDataSize) {
				dataList.add(new GeneralLedgerData());
			}

			pageDataMap.put(pageNo, dataList);
		}

		return pageDataMap;
	}
	
	private void processAccrualDate(List<GeneralLedgerData> searchResultList) {
		List<String> accrualDateList = new ArrayList<String>();
		String accrualDate = null;
		for (GeneralLedgerData searchResult : searchResultList) {
			// 発生日
			accrualDate = searchResult.getAccrualDate();
			if (!InputCheck.isNullOrBlank(accrualDate)) {
				if (accrualDateList.contains(accrualDate)) {
					searchResult.setAccrualDate("");
				}
				
				accrualDateList.add(accrualDate);
			}
		}
	}
	
	private List<GeneralLedgerReportData> convertSearchResultToPdfData(List<GeneralLedgerData> searchResultList)
			throws Exception {
		List<GeneralLedgerReportData> dataList = new ArrayList<GeneralLedgerReportData>();

		GeneralLedgerReportData data = null;
		String[] accrualDates = null;
		String[] moneys = null;
		int loop = 0;
		Class<?> _class = null;
		Field _field = null;
		for (GeneralLedgerData searchResult : searchResultList) {
			loop += 1;
			if (loop == 1) {
				data = new GeneralLedgerReportData();
			}
			_class = data.getClass();

			// 発生日
			if (InputCheck.isNullOrBlank(searchResult.getAccrualDate())) {
				accrualDates = new String[] { "", "", "" };
			} else {
				accrualDates = searchResult.getAccrualDate().split("/");
			}
			_field = _class.getDeclaredField("accrualDateMonth" + String.valueOf(loop));
			if (_field != null) {
				_field.setAccessible(true);
				if (InputCheck.isNullOrBlank(accrualDates[1])) {
					_field.set(data, accrualDates[1]);
				} else {
					_field.set(data, String.valueOf(Integer.parseInt(accrualDates[1])));
				}
			}
			_field = _class.getDeclaredField("accrualDateDay" + String.valueOf(loop));
			if (_field != null) {
				_field.setAccessible(true);
				if (InputCheck.isNullOrBlank(accrualDates[2])) {
					_field.set(data, accrualDates[2]);
				} else {
					_field.set(data, String.valueOf(Integer.parseInt(accrualDates[2])));
				}
			}
			if (InputCheck.isNullOrBlank(searchResult.getGeneralNo())
					&& !InputCheck.isNullOrBlank(searchResult.getCounterAccount())
					&& searchResult.getCounterAccount().indexOf("※※") != -1) {
				searchResult.setTekiyo(searchResult.getCounterAccount());
				searchResult.setCounterAccount("");
			}
			// 相手科目名
			_field = _class.getDeclaredField("counterAccount" + String.valueOf(loop));
			if (_field != null) {
				_field.setAccessible(true);
				if (InputCheck.isNullOrBlank(searchResult.getCounterAccount())) {
					_field.set(data, "");
				} else {
					_field.set(data, searchResult.getCounterAccount());
				}
			}
			// 摘要
			_field = _class.getDeclaredField("tekiyo" + String.valueOf(loop));
			if (_field != null) {
				_field.setAccessible(true);
				if (InputCheck.isNullOrBlank(searchResult.getTekiyo())) {
					_field.set(data, "");
				} else {
					_field.set(data, searchResult.getTekiyo());
				}
			}
			// 借 方 金 額
			moneys = splitMoney(searchResult.getDebitAmountMoney());
			for (int i = 1; i <= 4; i++) {
				_field = _class.getDeclaredField("debitMoney" + String.valueOf(loop) + String.valueOf(i));
				if (_field != null) {
					_field.setAccessible(true);
					_field.set(data, moneys[i - 1]);
				}
			}
			// 貸方金額
			moneys = splitMoney(searchResult.getCreditAmountMoney());
			for (int i = 1; i <= 4; i++) {
				_field = _class.getDeclaredField("creditMoney" + String.valueOf(loop) + String.valueOf(i));
				if (_field != null) {
					_field.setAccessible(true);
					_field.set(data, moneys[i - 1]);
				}
			}
			// 差引残高
			moneys = splitMoney(searchResult.getBalanceMoney());
			for (int i = 1; i <= 4; i++) {
				_field = _class.getDeclaredField("balanceMoney" + String.valueOf(loop) + String.valueOf(i));
				if (_field != null) {
					_field.setAccessible(true);
					_field.set(data, moneys[i - 1]);
				}
			}

			if (loop == 5) {
				loop = 0;
				dataList.add(data);
			}
		}

		return dataList;
	}
	
	private void setLastThreeRowData(Map<String, Object> parameters, List<GeneralLedgerData> searchResultList) {
		GeneralLedgerData searchResult = null;
		String[] accrualDates = null;
		String[] moneys = null;
		for (int i = 30; i < searchResultList.size(); i++) {
			searchResult = searchResultList.get(i);
			// 発生日
			if (InputCheck.isNullOrBlank(searchResult.getAccrualDate())) {
				accrualDates = new String[] { "", "", "" };
			} else {
				accrualDates = searchResult.getAccrualDate().split("/");
			}
			if (InputCheck.isNullOrBlank(accrualDates[1])) {
				parameters.put("accrualDateMonthF" + String.valueOf(i - 29), accrualDates[1]);
			} else {
				parameters.put("accrualDateMonthF" + String.valueOf(i - 29),
						String.valueOf(Integer.parseInt(accrualDates[1])));
			}
			if (InputCheck.isNullOrBlank(accrualDates[2])) {
				parameters.put("accrualDateDayF" + String.valueOf(i - 29), accrualDates[2]);
			} else {
				parameters.put("accrualDateDayF" + String.valueOf(i - 29),
						String.valueOf(Integer.parseInt(accrualDates[2])));
			}
			
			if (InputCheck.isNullOrBlank(searchResult.getGeneralNo())
					&& !InputCheck.isNullOrBlank(searchResult.getCounterAccount())
					&& searchResult.getCounterAccount().indexOf("※※") != -1) {
				searchResult.setTekiyo(searchResult.getCounterAccount());
				searchResult.setCounterAccount("");
			}
			// 相手科目名
			if (InputCheck.isNullOrBlank(searchResult.getCounterAccount())) {
				parameters.put("counterAccountF" + String.valueOf(i - 29), "");
			} else {
				parameters.put("counterAccountF" + String.valueOf(i - 29), searchResult.getCounterAccount());
			}

			// 摘要
			if (InputCheck.isNullOrBlank(searchResult.getTekiyo())) {
				parameters.put("tekiyoF" + String.valueOf(i - 29), "");
			} else {
				parameters.put("tekiyoF" + String.valueOf(i - 29), searchResult.getTekiyo());
			}

			// 借 方 金 額
			moneys = splitMoney(searchResult.getDebitAmountMoney());
			for (int j = 1; j <= 4; j++) {
				parameters.put("debitMoneyF" + String.valueOf(i - 29) + String.valueOf(j), moneys[j - 1]);
			}
			// 貸方金額
			moneys = splitMoney(searchResult.getCreditAmountMoney());
			for (int j = 1; j <= 4; j++) {
				parameters.put("creditMoneyF" + String.valueOf(i - 29) + String.valueOf(j), moneys[j - 1]);
			}

			// 差引残高
			moneys = splitMoney(searchResult.getBalanceMoney());
			for (int j = 1; j <= 4; j++) {
				parameters.put("balanceMoneyF" + String.valueOf(i - 29) + String.valueOf(j), moneys[j - 1]);
			}
		}
	}
	
	private String[] splitMoney(String money) {
		String[] moneys = new String[] { "", "", "", "" };

		if (!InputCheck.isNullOrBlank(money)) {
			String[] _moneys = money.split(",");
			_moneys[0] = _moneys[0].replace("-", "△");
			if (_moneys.length == 1) {
				moneys[3] = _moneys[0];
			} else if (_moneys.length == 2) {
				moneys[2] = _moneys[0];
				moneys[3] = _moneys[1];
			} else if (_moneys.length == 3) {
				moneys[1] = _moneys[0];
				moneys[2] = _moneys[1];
				moneys[3] = _moneys[2];
			} else if (_moneys.length >= 4) {
				moneys[0] = _moneys[0];
				moneys[1] = _moneys[1];
				moneys[2] = _moneys[2];
				moneys[3] = _moneys[3];
			}
		}

		return moneys;
	}
	
	private String getAccountYear(List<GeneralLedgerData> searchResultList) {
		String accountYear = "";

		String accrualDate = null;
		for (GeneralLedgerData searchResult : searchResultList) {
			// 発生日
			accrualDate = searchResult.getAccrualDate();
			if (!InputCheck.isNullOrBlank(accrualDate)) {
				accountYear = accrualDate.substring(2, 4);
			}
		}

		return accountYear;
	}
}
