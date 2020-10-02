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
import com.busicomjp.sapp.model.c.SellingGeneralAndAdministrativeReportRowData;
import com.busicomjp.sapp.model.c.SellingGeneralAndAdministrativeRowData;
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
public class C4Service {

	@Autowired
	B1Service b1Service;

	private final String CONST_TEMPLATE_NAME = "Selling_General_Administrative.jrxml";
	private final String CONST_REPORT_NAME = "販売費及び一般管理";
	@Value("${jasper.jrxml.file.path}")
	private String jasperJrxmlFilePath;
	@Value("${report.output.path}")
	private String reportOutputPath;

	// 勘定科目第一分類コード
	// 費用
	private static String ACCOUNT_KIND1_5 = "5";

	// 勘定科目第二分類コード
	private static String ACCOUNT_KIND2_01 = "01";
	// 勘定科目第三分類コード
	private static String ACCOUNT_KIND3_002 = "002";
	private static String ACCOUNT_KIND3_003 = "003";
	private static String ACCOUNT_KIND3_004 = "004";
	private static int MAX_ROW = 38;

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

		if (isReportEmpty(balanceDatas)) {
			Alert dialog = new Alert(AlertType.WARNING);
			dialog.setTitle("警告");
			dialog.getDialogPane().setHeaderText(null);
			dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			dialog.setContentText(
					String.format("%sは該当会計期間の%sデータが存在しません。", CompanyUtil.getCompanyName(), CONST_REPORT_NAME));
			dialog.showAndWait();
			return;
		}

		List<SellingGeneralAndAdministrativeRowData> reports = generateReportData(balanceDatas);

		//整形
		for (int i = MAX_ROW * 2 - reports.size() - 1; i > 0; i--) {
			reports.add(generatePlaceholder());
		}
		reports.add(new SellingGeneralAndAdministrativeRowData("合計", "",
				formatNumber(sum(balanceDatas))));

		List<SellingGeneralAndAdministrativeReportRowData> reportRowDatas = new ArrayList<>();

		for (int i = 0; i < MAX_ROW; i++) {
			reportRowDatas.add(generatePdfRowData(reports.get(i), reports.get(i + MAX_ROW)));
		}

		String filePath = generatePdfReport(reportRowDatas);
		try {
			ReportUtil.openPdf(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 出力対象存在チェック
	 * @param balanceDatas
	 * @return
	 */
	private boolean isReportEmpty(List<BalanceData> balanceDatas) {
		return sum(balanceDatas) <= 0;
	}

	private Long sum(List<BalanceData> balanceDatas) {
		return balanceDatas.stream()
				.filter(data -> ACCOUNT_KIND1_5.equals(data.getAccountKind1())
						&& ACCOUNT_KIND2_01.equals(data.getAccountKind2())
						&& (ACCOUNT_KIND3_002.equals(data.getAccountKind3())
								|| ACCOUNT_KIND3_003.equals(data.getAccountKind3())
								|| ACCOUNT_KIND3_004.equals(data.getAccountKind3())))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
	}

	/**
	 * PDF行データ生成
	 * @param data1
	 * @param data2
	 * @return
	 */
	private SellingGeneralAndAdministrativeReportRowData generatePdfRowData(
			SellingGeneralAndAdministrativeRowData data1,
			SellingGeneralAndAdministrativeRowData data2) {
		return new SellingGeneralAndAdministrativeReportRowData(data1.getKindName1(), data1.getKindName2(),
				data1.getAmount(), data2.getKindName1(), data2.getKindName2(), data2.getAmount());
	}

	/**
	 * プレースホルダー
	 * @return
	 */
	private SellingGeneralAndAdministrativeRowData generatePlaceholder() {
		return new SellingGeneralAndAdministrativeRowData("", "", "");
	}

	private List<SellingGeneralAndAdministrativeRowData> generateReportData(List<BalanceData> balanceDatas) {
		List<SellingGeneralAndAdministrativeRowData> rows = new ArrayList<>();

		// 同様な分類コード金額統合
		Map<String, LongSummaryStatistics> uniqueAmounts = balanceDatas.stream()
				.collect(Collectors.groupingBy(data -> data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4(), Collectors.summarizingLong(BalanceData::getBalance)));

		//役員費用合計
		Long officerExpenses = balanceDatas.stream()
				.filter(data -> isOfficer(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (officerExpenses > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("役員費用", "", formatNumber(officerExpenses)));

			//役員費用明細部
			balanceDatas.stream().filter(
					data -> isOfficer(data.getAccountKind1(), data.getAccountKind2(),
							data.getAccountKind3()))
					.filter(data -> !rows
							.contains(new SellingGeneralAndAdministrativeRowData("", data.getAccountKind4Name(),
									formatNumber(uniqueAmounts.get(data.getAccountKind1()
											+ data.getAccountKind2()
											+ data.getAccountKind3()
											+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new SellingGeneralAndAdministrativeRowData("", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		//販売費合計
		Long sellingExpenses = balanceDatas.stream()
				.filter(data -> isSelling(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (sellingExpenses > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("販売費", "", formatNumber(sellingExpenses)));

			//販売費明細部
			balanceDatas.stream().filter(
					data -> isSelling(data.getAccountKind1(), data.getAccountKind2(),
							data.getAccountKind3()))
					.filter(data -> !rows
							.contains(new SellingGeneralAndAdministrativeRowData("", data.getAccountKind4Name(),
									formatNumber(uniqueAmounts.get(data.getAccountKind1()
											+ data.getAccountKind2()
											+ data.getAccountKind3()
											+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new SellingGeneralAndAdministrativeRowData("", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}

		//管理費合計
		Long administrativeExpenses = balanceDatas.stream()
				.filter(data -> isAdministrative(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (administrativeExpenses > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("管理費", "", formatNumber(administrativeExpenses)));

			//管理費明細部
			balanceDatas.stream().filter(
					data -> isAdministrative(data.getAccountKind1(), data.getAccountKind2(),
							data.getAccountKind3()))
					.filter(data -> !rows
							.contains(new SellingGeneralAndAdministrativeRowData("", data.getAccountKind4Name(),
									formatNumber(uniqueAmounts.get(data.getAccountKind1()
											+ data.getAccountKind2()
											+ data.getAccountKind3()
											+ data.getAccountKind4()).getSum()))))
					.forEach(data -> rows.add(new SellingGeneralAndAdministrativeRowData("", data.getAccountKind4Name(),
							formatNumber(uniqueAmounts.get(data.getAccountKind1()
									+ data.getAccountKind2()
									+ data.getAccountKind3()
									+ data.getAccountKind4()).getSum()))));
		}
		return rows;
	}

	private String generatePdfReport(List<SellingGeneralAndAdministrativeReportRowData> rowDatas) {
		String filePath = ReportUtil.getReportFilePath(reportOutputPath, CONST_REPORT_NAME);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("startDay",
				CompanyUtil.getAccountStartDay().substring(0, 4) + "年"
						+ CompanyUtil.getAccountStartDay().substring(4, 6) + "月"
						+ CompanyUtil.getAccountStartDay().substring(6, 8) + "日");
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
	 * 販売費
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isSelling(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_003);
	}

	/**
	 * 役員費用
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isOfficer(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_002);
	}

	/**
	 * 管理費
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isAdministrative(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_004);
	}

	/**
	 * 数値整形
	 * @param num
	 * @return
	 */
	private String formatNumber(Long num) {
		if (num.equals(0L)) {
			return String.valueOf(num);
		}
		NumberFormat nf = NumberFormat.getNumberInstance();
		return nf.format(num).replace("-", "△");
	}
}
