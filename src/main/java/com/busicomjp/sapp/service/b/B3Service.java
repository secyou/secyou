package com.busicomjp.sapp.service.b;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.dto.b.CashFlowDto;
import com.busicomjp.sapp.model.b.CashFlowData;
import com.busicomjp.sapp.repository.b.B3Repository;

@Service
public class B3Service extends BAbstractService {
	private final String CONST_INCOME = "収入-";
	private final String CONST_PAYMENT = "支出-";

	@Autowired
	B3Repository b3Repo;

	public List<CashFlowData> selectData(String companyCode, String startYearMonth, String endYearMonth,
			boolean reportFlag) {
		List<CashFlowData> dataList = new ArrayList<CashFlowData>();

		CashFlowDto selectCondition = new CashFlowDto();
		selectCondition.setCompanyCode(companyCode);
		String kiStartYearMonth = dateYMFormat.format(getKiStartDate());
		String kiEndYearMonth = dateYMFormat.format(getKiEndMonth());
		selectCondition.setKiStartYearMonth(kiStartYearMonth);
		selectCondition.setKiEndYearMonth(kiEndYearMonth);

		// 年度の初月繰越高
		long yearTotalMoney = b3Repo.selectAllTotalMoney(selectCondition);

		List<String> yearMonthList = getYearMonthList(startYearMonth, endYearMonth);
		CashFlowData data = null;

		String income = CONST_INCOME;
		String payment = CONST_PAYMENT;
		if (reportFlag) {
			income = "";
			payment = "";
		}

		List<CashFlowData> tempDataList = new ArrayList<CashFlowData>();
		// 収入-現金売上
		data = getGenkinUriageOfIncome(income + "現金売上", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 収入-売掛金入金
		data = getUrikakekinNyukinOfIncome(income + "売掛金入金", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 収入-借入金
		data = getKariirekinOfIncome(income + "借入金", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 収入-預かり金
		data = getAzukariKinOfIncome(income + "預かり金", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 収入-貸付返金
		data = getKashitsukeHenkinOfIncome(income + "貸付返金", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 収入-その他入金
		data = getOtherNyukinOfIncome(income + "その他入金", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 収入-収入合計②
		CashFlowData incomeTotalData = calculateTotalMoney(tempDataList);
		incomeTotalData.setRowTitle(income + "収入合計②");
		tempDataList.add(incomeTotalData);

		dataList.addAll(tempDataList);
		tempDataList.clear();

		// 支出-現金仕入
		data = getGenkinShiireOfPayment(payment + "現金仕入", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-買掛金支払
		data = getKaikakeKinShiharaiOfPayment(payment + "買掛金支払", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-給与支払
		data = getSalaryOfPayment(payment + "給与支払", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-外注費
		data = getGaichuHiOfPayment(payment + "外注費", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-経費
		data = getExpensesOfPayment(payment + "経費", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-借入金返済
		data = getDebtOfPayment(payment + "借入金返済", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-貸付金
		data = getKashitsukeKinOfPayment(payment + "貸付金", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-その他支払
		data = getOtherOfPayment(payment + "その他支払", selectCondition, yearMonthList, reportFlag);
		tempDataList.add(data);

		// 支出-支出合計③
		CashFlowData paymentTotalData = calculateTotalMoney(tempDataList);
		paymentTotalData.setRowTitle(payment + "支出合計③");
		tempDataList.add(paymentTotalData);

		dataList.addAll(tempDataList);
		tempDataList.clear();

		// 翌月繰越高(①＋②ー③)
		CashFlowData nextMonthMoneyTotalData = calculateNextMonthMoney(yearTotalMoney, incomeTotalData,
				paymentTotalData);
		nextMonthMoneyTotalData.setRowTitle("翌月繰越高(①＋②ー③)");
		dataList.add(nextMonthMoneyTotalData);

		// 前月繰越高①
		data = calculatePreMonthMoney(yearTotalMoney, nextMonthMoneyTotalData);
		data.setRowTitle("前月繰越高①");
		dataList.add(0, data);

		_editDataForDisplay(startYearMonth, dataList, yearMonthList, reportFlag);

		return dataList;
	}
	
	public void generatePdfReport(String companyCode) {
	}
	
	/**
	 * 翌月繰越高(①＋②ー③)
	 */
	private CashFlowData calculateNextMonthMoney(long yearTotalMoney, CashFlowData incomeTotaldata, CashFlowData paymentTotaldata) {
		CashFlowData data = new CashFlowData();
		Class<?> _class = data.getClass();
		Field _field = null;
		long totalMoney = 0l;
		long money2 = 0l;
		long money3 = 0l;

		try {
			totalMoney = yearTotalMoney;
			for (int i = 1; i <= 12; i++) {
				money2 = 0l;
				money3 = 0l;
				_field = _class.getDeclaredField("money" + String.valueOf(i));
				if (_field != null) {
					_field.setAccessible(true);
					money2 = Long.valueOf((String) _field.get(incomeTotaldata));
					
					money3 = Long.valueOf((String) _field.get(paymentTotaldata));

					totalMoney = totalMoney + money2 - money3;
					_field.set(data, String.valueOf(totalMoney));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}
	
	/**
	 * 前月繰越高①
	 */
	private CashFlowData calculatePreMonthMoney(long yearTotalMoney, CashFlowData nextMonthMoneyTotalData) {
		CashFlowData data = new CashFlowData();
		Class<?> _class = data.getClass();
		Field _field = null;
		long money = 0l;

		try {
			data.setMoney1(String.valueOf(yearTotalMoney));
			for (int i = 2; i <= 12; i++) {
				money = 0l;
				_field = _class.getDeclaredField("money" + String.valueOf(i - 1));
				if (_field != null) {
					_field.setAccessible(true);
					money = Long.valueOf((String) _field.get(nextMonthMoneyTotalData));
					
					_field = _class.getDeclaredField("money" + String.valueOf(i));
					_field.setAccessible(true);
					_field.set(data, String.valueOf(money));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * 収入-現金売上
	 * 4	収益	01	収益	001	売上高	0001	売上高
	 * 4	収益	01	収益	001	売上高	0002	売上値引
	 * 4	収益	01	収益	001	売上高	0003	売上返品
	 */
	private CashFlowData getGenkinUriageOfIncome(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "4", "01", "001", accountKind4List, true, 1, false, false);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 収入-売掛金入金
	 * 1	資産	01	流動資産	003	売上債権	0002	売掛金
	 */
	private CashFlowData getUrikakekinNyukinOfIncome(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0002");
		_selectCondition = createCondition(selectCondition, "1", "01", "003", accountKind4List, true, 1, false, false);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 収入-借入金
	 * 2	負債	01	流動負債	002	金融債務	0001	短期借入金
	 * 2	負債	01	流動負債	002	金融債務	0003	1年内長期借入金
	 * 2	負債	02	固定負債	001	金融債務	0001	長期借入金
	 * 2	負債	02	固定負債	001	金融債務	0002	リース借入金
	 * 2	負債	02	固定負債	001	金融債務	0003	株主借入金
	 */
	private CashFlowData getKariirekinOfIncome(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0001");
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "2", "01", "002", accountKind4List, true, 2, false, false);
		selectConditionList.add(_selectCondition);

		accountKind4List.clear();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "2", "02", "001", accountKind4List, true, 2, false, false);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}

	/**
	 * 収入-預かり金
	 * 2	負債	01	流動負債	003	その他	0006	預り金（所得税）１
	 * 2	負債	01	流動負債	003	その他	0007	預り金（住民税)
	 * 2	負債	01	流動負債	003	その他	0008	預り金（財形)
	 * 2	負債	01	流動負債	003	その他	0009	他預り金
	 */
	private CashFlowData getAzukariKinOfIncome(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0006");
		accountKind4List.add("0007");
		accountKind4List.add("0008");
		accountKind4List.add("0009");
		_selectCondition = createCondition(selectCondition, "2", "01", "003", accountKind4List, true, 3, true, false);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 収入-貸付返金
	 * 1	資産	01	流動資産	005	その他	0003	貸付金
	 * 1	資産	02	固定資産	003	投資	    0004	長期貸付金
	 */
	private CashFlowData getKashitsukeHenkinOfIncome(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {

		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "1", "01", "005", accountKind4List, true, 2, false, false);
		selectConditionList.add(_selectCondition);

		accountKind4List.clear();
		accountKind4List.add("0004");
		_selectCondition = createCondition(selectCondition, "1", "02", "003", accountKind4List, true, 2, false, false);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 収入-その他入金
	 * 4	収益	01	収益	002	営業外収益	0001	受取利息
	 * 4	収益	01	収益	002	営業外収益	0002	受取配当金
	 * 4	収益	01	収益	002	営業外収益	0003	他営業外収益
	 * 4	収益	01	収益	002	営業外収益	0004	有価証券売却益
	 * 4	収益	01	収益	002	営業外収益	0005	有価証券評価益
	 * 4	収益	01	収益	002	営業外収益	0006	雑収入
	 */
	private CashFlowData getOtherNyukinOfIncome(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		_selectCondition = createCondition(selectCondition, "4", "01", "002", null, true, 1, false, false);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}

	/**
	 * 支出-現金仕入
	 * 5	費用	01	費用	    001	売上原価	0003	仕入高
	 * 5	費用	03	製造原価	001	材料費	0001	他原価原材料仕入
	 * 5	費用	03	製造原価	001	材料費	0002	他原価副資材仕入
	 * 5	費用	03	製造原価	001	材料費	0003	原価原材料仕入値引
	 * 5	費用	03	製造原価	001	材料費	0004	原価原材料仕入返品
	 * 5	費用	03	製造原価	001	材料費	0005	原価期末材料棚卸高
	 */
	private CashFlowData getGenkinShiireOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "5", "01", "001", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		accountKind4List.clear();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		accountKind4List.add("0004");
		accountKind4List.add("0005");
		_selectCondition = createCondition(selectCondition, "5", "03", "001", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 支出-買掛金支払
	 * 2	負債	01	流動負債	001	仕入債務	0002	買掛金
	 */
	private CashFlowData getKaikakeKinShiharaiOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0002");
		_selectCondition = createCondition(selectCondition, "2", "01", "001", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}

	/**
	 * 支出-給与支払
	 * 5	費用	01	費用	002	役員費用	0001	役員役員報酬
	 * 5	費用	01	費用	002	役員費用	0002	役員役員賞与
	 * 5	費用	01	費用	002	役員費用	0003	役員退職金及び掛金
	 * 5	費用	01	費用	002	役員費用	0004	役員法定福利費
	 * 5	費用	01	費用	003	販売費	0001	販売給料手当
	 * 5	費用	01	費用	003	販売費	0002	販売賞与手当
	 * 5	費用	01	費用	003	販売費	0003	販売退職金及び掛金
	 * 5	費用	01	費用	003	販売費	0004	販売雑給
	 * 5	費用	01	費用	003	販売費	0005	販売法定福利費
	 * 5	費用	01	費用	003	販売費	0006	販売福利厚生費
	 * 5	費用	01	費用	004	管理費	0001	管理給料手当
	 * 5	費用	01	費用	004	管理費	0002	管理賞与手当
	 * 5	費用	01	費用	004	管理費	0003	管理退職金及び掛金
	 * 5	費用	01	費用	004	管理費	0004	管理雑給
	 * 5	費用	01	費用	004	管理費	0005	管理法定福利費
	 * 5	費用	01	費用	004	管理費	0006	管理福利厚生費
	 */
	private CashFlowData getSalaryOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {
	
		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		accountKind4List.add("0004");
		_selectCondition = createCondition(selectCondition, "5", "01", "002", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		accountKind4List.clear();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		accountKind4List.add("0004");
		accountKind4List.add("0005");
		accountKind4List.add("0006");
		_selectCondition = createCondition(selectCondition, "5", "01", "003", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		accountKind4List.clear();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		accountKind4List.add("0004");
		accountKind4List.add("0005");
		accountKind4List.add("0006");
		_selectCondition = createCondition(selectCondition, "5", "01", "004", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 支出-外注費
	 * 5	費用	03	製造原価	002	外注加工費	0001	外注加工費

	 */
	private CashFlowData getGaichuHiOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {

		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0001");
		_selectCondition = createCondition(selectCondition, "5", "03", "002", accountKind4List, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 支出-経費
	 * 分類1～分類3は「5/01/003」または「5/01/004」を指定し、分類4は「0001～0006」を除外した結果と一致
	 */
	private CashFlowData getExpensesOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {

		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		accountKind4List.add("0004");
		accountKind4List.add("0005");
		accountKind4List.add("0006");
		_selectCondition = createCondition(selectCondition, "5", "01", "003", accountKind4List, false, 1, false, true);
		selectConditionList.add(_selectCondition);
		
		accountKind4List.clear();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		accountKind4List.add("0004");
		accountKind4List.add("0005");
		accountKind4List.add("0006");
		_selectCondition = createCondition(selectCondition, "5", "01", "004", accountKind4List, false, 1, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 支出-借入金返済
	 * 2	負債	01	流動負債	002	金融債務	0001	短期借入金
	 * 2	負債	01	流動負債	002	金融債務	0003	1年内長期借入金
	 * 2	負債	02	固定負債	001	金融債務	0001	長期借入金
	 * 2	負債	02	固定負債	001	金融債務	0002	リース借入金
	 * 2	負債	02	固定負債	001	金融債務	0003	株主借入金
	 */
	private CashFlowData getDebtOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {

		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0001");
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "2", "01", "002", accountKind4List, true, 4, false, true);
		selectConditionList.add(_selectCondition);
		
		accountKind4List.clear();
		accountKind4List.add("0001");
		accountKind4List.add("0002");
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "2", "02", "001", accountKind4List, true, 4, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 支出-貸付金
	 * 1	資産	01	流動資産	005	その他	0003	貸付金
	 * 1	資産	02	固定資産	003	投資	0004	長期貸付金
	 */
	private CashFlowData getKashitsukeKinOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {

		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		List<String> accountKind4List = new ArrayList<String>();
		accountKind4List.add("0003");
		_selectCondition = createCondition(selectCondition, "1", "01", "005", accountKind4List, true, 4, false, true);
		selectConditionList.add(_selectCondition);

		accountKind4List.clear();
		accountKind4List.add("0004");
		_selectCondition = createCondition(selectCondition, "1", "02", "003", accountKind4List, true, 4, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	/**
	 * 支出-その他支払
	 * 5	費用	01	費用	005	営業外費用	0001	支払利息
	 * 5	費用	01	費用	005	営業外費用	0002	手形売却損
	 * 5	費用	01	費用	005	営業外費用	0003	前期損益修正損
	 * 5	費用	01	費用	005	営業外費用	0004	他営業外損失
	 * 5	費用	01	費用	005	営業外費用	0005	貸倒損失
	 * 5	費用	01	費用	005	営業外費用	0006	有価証券売却損
	 * 5	費用	01	費用	005	営業外費用	0007	有価証券評価損
	 * 5	費用	01	費用	005	営業外費用	0008	雑損失
	 * ※分類1～分類3のみで指定してもOK
	 */
	private CashFlowData getOtherOfPayment(final String title, final CashFlowDto selectCondition,
			List<String> yearMonthList, boolean reportFlag) {

		List<CashFlowDto> selectConditionList = new ArrayList<CashFlowDto>();
		CashFlowDto _selectCondition = null;

		_selectCondition = createCondition(selectCondition, "5", "01", "005", null, true, 1, false, true);
		selectConditionList.add(_selectCondition);

		CashFlowData data = selectTotalMoney(selectConditionList, yearMonthList);
		data.setRowTitle(title);

		return data;
	}
	
	private CashFlowData selectTotalMoney(List<CashFlowDto> selectConditionList, List<String> yearMonthList) {
		List<CashFlowData> dataList = new ArrayList<CashFlowData>();
		CashFlowData data = null;

		for (CashFlowDto selectCondition : selectConditionList) {
			List<Map<String, Object>> moneyList = b3Repo.selectTotalMoneyByAccount(selectCondition);

			data = convertMapToModel(moneyList, yearMonthList);

			dataList.add(data);
		}

		return calculateTotalMoney(dataList);
	}

	private CashFlowData convertMapToModel(List<Map<String, Object>> moneyList, List<String> yearMonthList) {
		CashFlowData data = new CashFlowData();
		Class<?> _class = data.getClass();
		Field _field = null;
		int loop = 1;

		try {
			Map<String, Long> moneyMap = _convertMoneyListToMap(moneyList);
			Long money = 0l;
			for (String yearMonth : yearMonthList) {
				money = moneyMap.get(yearMonth);
				if (money == null) {
					money = 0l;
				}

				_field = _class.getDeclaredField("month" + String.valueOf(loop));
				if (_field != null) {
					_field.setAccessible(true);
					_field.set(data, yearMonth);
				}
				_field = _class.getDeclaredField("money" + String.valueOf(loop));
				if (_field != null) {
					_field.setAccessible(true);
					_field.set(data, String.valueOf(money));
				}

				loop += 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	private CashFlowData calculateTotalMoney(List<CashFlowData> dataList) {
		CashFlowData data = new CashFlowData();
		Class<?> _class = data.getClass();
		Field _field = null;
		long totalMoney = 0l;
		long money = 0l;

		try {
			boolean first = false;
			for (CashFlowData _data : dataList) {
				if (!first) {
					first = true;
					BeanUtils.copyProperties(_data, data);

					continue;
				}

				for (int i = 1; i <= 12; i++) {
					totalMoney = 0l;
					money = 0l;
					_field = _class.getDeclaredField("money" + String.valueOf(i));
					if (_field != null) {
						_field.setAccessible(true);
						money = Long.valueOf((String) _field.get(_data));

						totalMoney = Long.valueOf((String) _field.get(data));
						totalMoney = totalMoney + money;
						_field.set(data, String.valueOf(totalMoney));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	private void _editDataForDisplay(String startYearMonth, List<CashFlowData> dataList, List<String> yearMonthList,
			boolean reportFlag) {
		Class<?> _class = null;
		Field _field = null;
		Field _field2 = null;
		long money = 0l;

		int offset = yearMonthList.indexOf(startYearMonth);

		try {
			if (reportFlag) {
				for (CashFlowData data : dataList) {
					_class = data.getClass();
					for (int i = 1; i <= 12; i++) {
						money = 0l;
						_field = _class.getDeclaredField("money" + String.valueOf(i));
						if (_field != null) {
							_field.setAccessible(true);
							money = Long.valueOf((String) _field.get(data));

							_field.set(data, _formatByComma(money, reportFlag));
						}
					}
				}
			} else {
				for (CashFlowData data : dataList) {
					_class = data.getClass();
					for (int i = 1; i <= 8; i++) {
						money = 0l;
						_field2 = _class.getDeclaredField("money" + String.valueOf(i + offset));
						if (_field2 != null) {
							_field2.setAccessible(true);
							money = Long.valueOf((String) _field2.get(data));
						}

						_field = _class.getDeclaredField("money" + String.valueOf(i));
						if (_field != null) {
							_field.setAccessible(true);

							_field.set(data, _formatByComma(money, reportFlag));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, Long> _convertMoneyListToMap(List<Map<String, Object>> moneyList) {
		Map<String, Long> moneyMap = new HashMap<String, Long>();

		moneyList.forEach(row -> {
			Object totalMoney = row.get("totalMoney");
			if (InputCheck.isNullOrBlank(totalMoney)) {
				totalMoney = "0";
			}
			moneyMap.put((String) row.get("yearMonth"), Long.parseLong(String.valueOf(totalMoney)));
		});

		return moneyMap;
	}
	
	private CashFlowDto createCondition(final CashFlowDto selectCondition, String accountKind1, String accountKind2,
			String accountKind3, List<String> accountKind4List, boolean accountKind4EqIn, int aggregateMethod,
			boolean aggregateAccountCode, boolean paymentFlag) {
		CashFlowDto dto = new CashFlowDto();

		BeanUtils.copyProperties(selectCondition, dto);

		dto.setAccountKind1(accountKind1);
		dto.setAccountKind2(accountKind2);
		dto.setAccountKind3(accountKind3);
		dto.setAccountKind4List(accountKind4List);
		dto.setAccountKind4EqIn(accountKind4EqIn);
		dto.setAggregateMethod(aggregateMethod);
		dto.setAggregateAccountCode(aggregateAccountCode);
		dto.setPaymentFlag(paymentFlag);

		return dto;
	}
}
