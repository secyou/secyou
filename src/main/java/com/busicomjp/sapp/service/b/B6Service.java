package com.busicomjp.sapp.service.b;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.ReportUtil;
import com.busicomjp.sapp.dto.b.AccountsReceivableDto;
import com.busicomjp.sapp.model.b.AccountsSummaryData;
import com.busicomjp.sapp.model.b.TorihikisakiSummaryData;
import com.busicomjp.sapp.repository.b.B6Repository;

@Service
public class B6Service extends B4_5_6AbstractService {
	private final String CONST_REPORT_NAME = "未払金集計表";
	private final String CONST_ACCOUNT_TYPE_NYUUKIN = "支払";
	@Autowired
	B6Repository b6Repo;

	public List<TorihikisakiSummaryData> selectAccountsSummary(String companyCode, String startYearMonth,
			String endYearMonth, boolean reportFlag) {
		List<TorihikisakiSummaryData> dataList = new ArrayList<TorihikisakiSummaryData>();

		AccountsReceivableDto selectCondition = new AccountsReceivableDto();
		selectCondition.setCompanyCode(companyCode);
		String kiStartYearMonth = dateYMFormat.format(getKiStartDate());
		String kiEndYearMonth = dateYMFormat.format(getKiEndMonth());
		selectCondition.setKiStartYearMonth(kiStartYearMonth);
		selectCondition.setKiEndYearMonth(kiEndYearMonth);

		// 前期繰越
		List<Map<String, Object>> carryForwardMoneyList = b6Repo.selectCarryForwardMoney(selectCondition);
		Map<String, Long> carryForwardMoneyMap = _getCarryForwardMoneyOfSuppliers(carryForwardMoneyList);

		// 最終取引日
		List<Map<String, Object>> lastTradingDateList = b6Repo.selectLastTradingDate(selectCondition);
		Map<String, String> lastTradingDateMap = _getLastTradingDateOfSuppliers(lastTradingDateList);

		// 月ごとの集計
		List<AccountsSummaryData> searchResultList = b6Repo.selectAccountsUnpaidSummary(selectCondition);
		if (searchResultList != null && searchResultList.size() > 0) {
			dataList = _editDataForDisplay(startYearMonth, endYearMonth, searchResultList, carryForwardMoneyMap,
					lastTradingDateMap, reportFlag);
		}

		return dataList;
	}

	public void generatePdfReport(String companyCode) {
		try {
			Date startDate = this.getKiStartDate();
			String startYearMonth = dateYMFormat.format(startDate);
			String endYearMonth = dateYMFormat.format(this.getYearMonth(startDate, 11));

			List<TorihikisakiSummaryData> dataList = this.selectAccountsSummary(companyCode, startYearMonth,
					endYearMonth, true);

			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(startDate);

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(startDate);
			endCalendar.add(Calendar.MONTH, 12);
			endCalendar.add(Calendar.DAY_OF_MONTH, -1);

			String filePath = _generatePdfReport(CONST_REPORT_NAME, CONST_ACCOUNT_TYPE_NYUUKIN, startCalendar,
					endCalendar, dataList);

			ReportUtil.openPdf(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
