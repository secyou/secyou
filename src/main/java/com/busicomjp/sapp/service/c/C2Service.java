package com.busicomjp.sapp.service.c;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.ReportUtil;
import com.busicomjp.sapp.model.b.BalanceData;
import com.busicomjp.sapp.model.c.BalanceAndSheetReportRowData;
import com.busicomjp.sapp.model.c.BalanceAndSheetRowData;
import com.busicomjp.sapp.service.b.B1Service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class C2Service {

	@Autowired
	B1Service b1Service;

	private final String CONST_TEMPLATE_NAME = "Balance_Sheet.jrxml";
	private final String CONST_REPORT_NAME = "貸借対照表";
	@Value("${jasper.jrxml.file.path}")
	private String jasperJrxmlFilePath;
	@Value("${report.output.path}")
	private String reportOutputPath;

	// 勘定科目第一分類コード
	// 資産
	private static String ACCOUNT_KIND1_1 = "1";
	// 負債
	private static String ACCOUNT_KIND1_2 = "2";
	// 純資産
	private static String ACCOUNT_KIND1_3 = "3";

	// 勘定科目第二分類コード
	private static String ACCOUNT_KIND2_01 = "01";
	private static String ACCOUNT_KIND2_02 = "02";
	private static String ACCOUNT_KIND2_03 = "03";
	// 勘定科目第三分類コード
	private static String ACCOUNT_KIND3_001 = "001";
	private static String ACCOUNT_KIND3_002 = "002";
	private static String ACCOUNT_KIND3_003 = "003";
	private static String ACCOUNT_KIND3_004 = "004";
	private static String ACCOUNT_KIND3_005 = "005";
	private static String ACCOUNT_KIND3_006 = "006";

	public void print() {
		// 残高データ取得
		List<BalanceData> balanceDatas = b1Service.getBalanceDataList();
		if (balanceDatas.isEmpty()) {
			Alert dialog = new Alert(AlertType.WARNING);
			dialog.setTitle("警告");
			dialog.getDialogPane().setHeaderText(null);
			dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			dialog.setContentText(String.format("%sは該当会計期間の仕訳データが存在しません。", CompanyUtil.getCompanyName()));
			dialog.showAndWait();
			return;
		}
		//資産データ
		List<BalanceAndSheetRowData> assets = generateAssetsReportData(balanceDatas);
		//負債データ
		List<BalanceAndSheetRowData> liabilities = generateLiabilitiesReportData(balanceDatas);
		// 純資産データ
		List<BalanceAndSheetRowData> equity = generateEquityReportData(balanceDatas,
				assets.size() - liabilities.size());

		//空白整形
		for (int i = liabilities.size() + equity.size() - assets.size(); i > 0; i--) {
			assets.add(generatePlaceholder());
		}

		List<BalanceAndSheetReportRowData> reportRowDatas = new ArrayList<>();
		for (int i = 0; i < liabilities.size(); i++) {
			reportRowDatas.add(generatePdfRowData(assets.get(i), liabilities.get(i)));
		}

		for (int i = 0; i < equity.size(); i++) {
			reportRowDatas.add(generatePdfRowData(assets.get(i + liabilities.size()), equity.get(i)));
		}

		reportRowDatas.add(generatePdfFooterRowData(balanceDatas));
		String filePath = generatePdfReport(reportRowDatas);
		try {
			ReportUtil.openPdf(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * PDF行データ生成
	 * @param data1
	 * @param data2
	 * @return
	 */
	private BalanceAndSheetReportRowData generatePdfRowData(BalanceAndSheetRowData data1,
			BalanceAndSheetRowData data2) {
		return new BalanceAndSheetReportRowData(data1.getKindName1(), data1.getKindName2(), data1.getKindName3(),
				data1.getAmount(), data2.getKindName1(), data2.getKindName2(), data2.getKindName3(), data2.getAmount());
	}

	/**
	 * 末行データ生成
	 * @param balanceDatas
	 * @return
	 */
	private BalanceAndSheetReportRowData generatePdfFooterRowData(List<BalanceData> balanceDatas) {
		return new BalanceAndSheetReportRowData("資産合計", "", "", formatNumber(balanceDatas.stream()
				.filter(data -> ACCOUNT_KIND1_2.equals(data.getAccountKind1())
						|| ACCOUNT_KIND1_3.equals(data.getAccountKind1()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L)), "負債純資産合計", "", "",
				formatNumber(balanceDatas.stream()
						.filter(data -> ACCOUNT_KIND1_2.equals(data.getAccountKind1())
								|| ACCOUNT_KIND1_3.equals(data.getAccountKind1()))
						.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L)));
	}

	/**
	 * プレースホルダー
	 * @return
	 */
	private BalanceAndSheetRowData generatePlaceholder() {
		return new BalanceAndSheetRowData("", "", "", "");
	}

	/**
	 * 資産
	 * @param balanceDatas
	 * @return
	 */
	private List<BalanceAndSheetRowData> generateAssetsReportData(List<BalanceData> balanceDatas) {

		List<BalanceAndSheetRowData> rows = new ArrayList<>();

		// 同様な分類コード金額統合
		Map<String, LongSummaryStatistics> uniqueAmounts = balanceDatas.stream()
				.collect(Collectors.groupingBy(data -> data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4(), Collectors.summarizingLong(BalanceData::getBalance)));

		//タイトル
		rows.add(new BalanceAndSheetRowData("(資産の部)", "", "", ""));
		//流動資産合計
		Long currentAssets = balanceDatas.stream()
				.filter(data -> isCurrentAssets(data.getAccountKind1(), data.getAccountKind2()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("流動資産", "", "", formatNumber(currentAssets)));
		//現金預金合計
		Long cashAndBank = balanceDatas.stream()
				.filter(data -> isCashAndBank(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("", "", "現金預金", formatNumber(cashAndBank)));
		//流動資産現金預金以外の明細部
		balanceDatas.stream().filter(data -> isCurrentAssets(data.getAccountKind1(), data.getAccountKind2()))
				.filter(data -> !isCashAndBank(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
						formatNumber(uniqueAmounts.get(data.getAccountKind1()
								+ data.getAccountKind2()
								+ data.getAccountKind3()
								+ data.getAccountKind4()).getSum()))))
				.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
						formatNumber(uniqueAmounts.get(data.getAccountKind1()
								+ data.getAccountKind2()
								+ data.getAccountKind3()
								+ data.getAccountKind4()).getSum()))));

		//固定資産合計
		Long fixedAssets = balanceDatas.stream()
				.filter(data -> isFixedAssets(data.getAccountKind1(), data.getAccountKind2()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("固定資産", "", "", formatNumber(fixedAssets)));

		//有形合計
		Long tangibleFixedAssets = balanceDatas.stream()
				.filter(data -> isTangibleFixedAssets(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (tangibleFixedAssets > 0) {
			rows.add(new BalanceAndSheetRowData("", "有形", "", formatNumber(tangibleFixedAssets)));
			//有形固定資産明細部
			balanceDatas.stream().filter(
					data -> isTangibleFixedAssets(data.getAccountKind1(), data.getAccountKind2(),
							data.getAccountKind3()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		//無形合計
		Long intangibleAssets = balanceDatas.stream()
				.filter(data -> isIntangibleAssets(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("", "無形", "", formatNumber(intangibleAssets)));
		if (intangibleAssets > 0) {
			//無形固定資産明細部
			balanceDatas.stream().filter(
					data -> isIntangibleAssets(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}
		//投資合計
		Long investments = balanceDatas.stream()
				.filter(data -> isInvestments(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (investments > 0) {
			rows.add(new BalanceAndSheetRowData("", "投資", "", formatNumber(investments)));
			//投資明細部
			balanceDatas.stream()
					.filter(data -> isInvestments(data.getAccountKind1(), data.getAccountKind2(),
							data.getAccountKind3()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		//繰延資産合計
		Long deferredAssets = balanceDatas.stream()
				.filter(data -> isDeferredAssets(data.getAccountKind1(), data.getAccountKind2()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (deferredAssets > 0) {
			rows.add(new BalanceAndSheetRowData("繰延資産", "", "", formatNumber(deferredAssets)));
			//繰延資産明細部
			balanceDatas.stream().filter(data -> isDeferredAssets(data.getAccountKind1(), data.getAccountKind2()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		return rows;
	}

	/**
	 * 負債
	 * @param balanceDatas
	 * @return
	 */
	private List<BalanceAndSheetRowData> generateLiabilitiesReportData(List<BalanceData> balanceDatas) {
		List<BalanceAndSheetRowData> rows = new ArrayList<>();

		// 同様な分類コード金額統合
		Map<String, LongSummaryStatistics> uniqueAmounts = balanceDatas.stream()
				.collect(Collectors.groupingBy(data -> data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4(), Collectors.summarizingLong(BalanceData::getBalance)));

		//タイトル
		rows.add(new BalanceAndSheetRowData("(負債の部)", "", "", ""));
		//流動負債合計
		Long currentLiabilities = balanceDatas.stream()
				.filter(data -> isCurrentLiabilities(data.getAccountKind1(), data.getAccountKind2()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("流動負債", "", "", formatNumber(currentLiabilities)));
		if (currentLiabilities > 0) {
			//流動負債明細部
			balanceDatas.stream().filter(data -> isCurrentLiabilities(data.getAccountKind1(), data.getAccountKind2()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		//固定負債合計
		Long fixedLiabilities = balanceDatas.stream()
				.filter(data -> isFixedLiabilities(data.getAccountKind1(), data.getAccountKind2()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (fixedLiabilities > 0) {
			rows.add(new BalanceAndSheetRowData("固定負債", "", "", formatNumber(fixedLiabilities)));
			//固定負債明細部
			balanceDatas.stream().filter(data -> isFixedLiabilities(data.getAccountKind1(), data.getAccountKind2()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		//負債合計
		Long liabilities = balanceDatas.stream()
				.filter(data -> ACCOUNT_KIND1_2.equals(data.getAccountKind1()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("負債合計", "", "", formatNumber(liabilities)));

		return rows;
	}

	/**
	 * 純資産
	 * @param balanceDatas
	 * @return
	 */
	private List<BalanceAndSheetRowData> generateEquityReportData(List<BalanceData> balanceDatas, int difference) {
		List<BalanceAndSheetRowData> rows = new ArrayList<>();
		// 同様な分類コード金額統合
		Map<String, LongSummaryStatistics> uniqueAmounts = balanceDatas.stream()
				.collect(Collectors.groupingBy(data -> data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4(), Collectors.summarizingLong(BalanceData::getBalance)));

		//タイトル
		rows.add(new BalanceAndSheetRowData("(純資産の部)", "", "", ""));
		//株主資本
		Long shareholdersEquity = balanceDatas.stream()
				.filter(data -> isShareholdersEquity(data.getAccountKind1(), data.getAccountKind2()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("株主資本", "", "", formatNumber(shareholdersEquity)));
		//資本金
		Long capitalStock = balanceDatas.stream()
				.filter(data -> isCapitalStock(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (capitalStock > 0) {
			rows.add(new BalanceAndSheetRowData("", "資本金", "", formatNumber(capitalStock)));
		}
		//新株申込証拠金
		Long depositForSubscriptionsToShares = balanceDatas.stream()
				.filter(data -> isDepositForSubscriptionsToShares(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (depositForSubscriptionsToShares > 0) {
			rows.add(
					new BalanceAndSheetRowData("", "新株申込証拠金", "", formatNumber(depositForSubscriptionsToShares)));
		}
		//元入金
		Long capital = balanceDatas.stream()
				.filter(data -> isCapital(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (capital > 0) {
			rows.add(new BalanceAndSheetRowData("", "元入金", "", formatNumber(capital)));
		}
		//資本剰余金合計
		Long capitalSurplus = balanceDatas.stream()
				.filter(data -> isCapitalSurplus(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (capitalSurplus > 0) {
			rows.add(new BalanceAndSheetRowData("", "資本剰余金", "", formatNumber(capitalSurplus)));
			//資本剰余金明細部
			balanceDatas.stream().filter(
					data -> isCapitalSurplus(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}
		//利益剰余金合計
		Long retainedEarnings = balanceDatas.stream()
				.filter(data -> isRetainedEarnings(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (retainedEarnings > 0) {
			rows.add(new BalanceAndSheetRowData("", "利益剰余金", "", formatNumber(retainedEarnings)));
			//利益剰余金明細部
			balanceDatas.stream().filter(
					data -> isRetainedEarnings(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}
		//自己株式合計
		Long treasuryStock = balanceDatas.stream()
				.filter(data -> isTreasuryStock(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (treasuryStock > 0) {
			rows.add(new BalanceAndSheetRowData("", "自己株式", "", formatNumber(treasuryStock)));
			//自己株式明細部
			balanceDatas.stream()
					.filter(data -> isTreasuryStock(data.getAccountKind1(), data.getAccountKind2(),
							data.getAccountKind3()))
					.filter(data -> !rows.contains(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new BalanceAndSheetRowData("", "", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}
		//空白整形
		for (int i = difference - rows.size(); i > 0; i--) {
			rows.add(generatePlaceholder());
		}

		//純資産合計
		Long equity = balanceDatas.stream()
				.filter(data -> ACCOUNT_KIND1_3.equals(data.getAccountKind1()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		rows.add(new BalanceAndSheetRowData("純資産合計", "", "", formatNumber(equity)));

		return rows;
	}

	private String generatePdfReport(List<BalanceAndSheetReportRowData> rowDatas) {
		String filePath = ReportUtil.getReportFilePath(reportOutputPath, CONST_REPORT_NAME);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("endDay",
				CompanyUtil.getAccountEndDay().substring(0, 4) + "年"
						+ CompanyUtil.getAccountEndDay().substring(4, 6) + "月"
						+ CompanyUtil.getAccountEndDay().substring(6, 8) + "日");
		try (InputStream template = new ClassPathResource(jasperJrxmlFilePath + CONST_TEMPLATE_NAME).getInputStream()) {
			JasperReport report = JasperCompileManager.compileReport(template);
			JRBeanCollectionDataSource collectionDataSource = new JRBeanCollectionDataSource(rowDatas);
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, collectionDataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 流動資産
	 * @param kindCode1
	 * @param kindCode2
	 * @return
	 */
	private boolean isCurrentAssets(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_01);
	}

	/**
	 * 現金預金
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isCashAndBank(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	/**
	 * 固定資産
	 * @param kindCode1
	 * @param kindCode2
	 * @return
	 */
	private boolean isFixedAssets(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_02);
	}

	/**
	 * 固定資産-有形
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isTangibleFixedAssets(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_02)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	/**
	 * 固定資産-無形
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isIntangibleAssets(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_02)
				&& kindCode3.equals(ACCOUNT_KIND3_002);
	}

	/**
	 * 固定資産-投資
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isInvestments(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_02)
				&& kindCode3.equals(ACCOUNT_KIND3_003);
	}

	/**
	 * 繰延資産
	 * @param kindCode1
	 * @param kindCode2
	 * @return
	 */
	private boolean isDeferredAssets(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_1) && kindCode2.equals(ACCOUNT_KIND2_03);
	}

	/**
	 * 流動負債
	 * @param kindCode1
	 * @param kindCode2
	 * @return
	 */
	private boolean isCurrentLiabilities(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_2) && kindCode2.equals(ACCOUNT_KIND2_01);
	}

	/**
	 * 固定負債
	 * @param kindCode1
	 * @param kindCode2
	 * @return
	 */
	private boolean isFixedLiabilities(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_2) && kindCode2.equals(ACCOUNT_KIND2_02);
	}

	/**
	 * 株主資本
	 * @param kindCode1
	 * @param kindCode2
	 * @return
	 */
	private boolean isShareholdersEquity(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01);
	}

	/**
	 * 資本金
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isCapitalStock(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	/**
	 * 新株申込証拠金
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isDepositForSubscriptionsToShares(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_002);
	}

	/**
	 * 元入金
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isCapital(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_003);
	}

	/**
	 * 資本剰余金
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isCapitalSurplus(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_004);
	}

	/**
	 * 利益剰余金
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isRetainedEarnings(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_005);
	}

	/**
	 * 自己株式
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isTreasuryStock(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_3) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_006);
	}

	private String formatNumber(Long num) {
		if (num.equals(0L)) {
			return String.valueOf(num);
		}
		NumberFormat nf = NumberFormat.getNumberInstance();
		return nf.format(num).replace("-", "△");
	}
}
