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
public class C3Service {

	private final String CONST_TEMPLATE_NAME = "Manufacturing_Cost.jrxml";
	private final String CONST_REPORT_NAME = "製造原価";
	@Value("${jasper.jrxml.file.path}")
	private String jasperJrxmlFilePath;
	@Value("${report.output.path}")
	private String reportOutputPath;

	@Autowired
	B1Service b1Service;

	// 勘定科目第一分類コード
	private static String ACCOUNT_KIND1_5 = "5";
	// 勘定科目第二分類コード
	private static String ACCOUNT_KIND2_03 = "03";
	// 勘定科目第三分類コード
	private static String ACCOUNT_KIND3_001 = "001";
	private static String ACCOUNT_KIND3_002 = "002";
	private static String ACCOUNT_KIND3_003 = "003";
	private static String ACCOUNT_KIND3_004 = "004";
	private static String ACCOUNT_KIND3_005 = "005";
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
						&& ACCOUNT_KIND2_03.equals(data.getAccountKind2())
						&& (ACCOUNT_KIND3_001.equals(data.getAccountKind3())
								|| ACCOUNT_KIND3_002.equals(data.getAccountKind3())
								|| ACCOUNT_KIND3_003.equals(data.getAccountKind3())
								|| ACCOUNT_KIND3_004.equals(data.getAccountKind3())
								|| ACCOUNT_KIND3_005.equals(data.getAccountKind3())))
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


		//材料費合計
		Long materialCost = balanceDatas.stream()
				.filter(data -> isMaterialCost(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (materialCost > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("材料費", "", formatNumber(materialCost)));

			//材料費明細部
			balanceDatas.stream().filter(
					data -> isMaterialCost(data.getAccountKind1(), data.getAccountKind2(),
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

		//外注加工費合計
		Long outsourcedProcessingCost = balanceDatas.stream()
				.filter(data -> isOutsourcedProcessing(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (outsourcedProcessingCost > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("外注加工費", "", formatNumber(outsourcedProcessingCost)));

			//外注加工費明細部
			balanceDatas.stream().filter(
					data -> isOutsourcedProcessing(data.getAccountKind1(), data.getAccountKind2(),
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

		//工場経費
		Long factoryExpenses = balanceDatas.stream()
				.filter(data -> isFactoryExpenses(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (factoryExpenses > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("工場経費", "", formatNumber(factoryExpenses)));

			//工場経費明細部
			balanceDatas.stream().filter(
					data -> isFactoryExpenses(data.getAccountKind1(), data.getAccountKind2(),
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

		//製造経費
		Long manufacturingExpenses = balanceDatas.stream()
				.filter(data -> isManufacturing(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (manufacturingExpenses > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("製造経費", "", formatNumber(manufacturingExpenses)));

			//製造経費明細部
			balanceDatas.stream().filter(
					data -> isManufacturing(data.getAccountKind1(), data.getAccountKind2(),
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

		//仕掛品
		Long workProgress = balanceDatas.stream()
				.filter(data -> isWorkProgress(data.getAccountKind1(), data.getAccountKind2(),
						data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		if (workProgress > 0) {
			rows.add(new SellingGeneralAndAdministrativeRowData("仕掛品", "", formatNumber(workProgress)));

			//仕掛品明細部
			balanceDatas.stream().filter(
					data -> isWorkProgress(data.getAccountKind1(), data.getAccountKind2(),
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
	 * 材料費
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isMaterialCost(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_03)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	/**
	 * 外注加工費
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isOutsourcedProcessing(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_03)
				&& kindCode3.equals(ACCOUNT_KIND3_002);
	}

	/**
	 * 工場経費
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isFactoryExpenses(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_03)
				&& kindCode3.equals(ACCOUNT_KIND3_003);
	}

	/**
	 * 製造経費
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isManufacturing(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_03)
				&& kindCode3.equals(ACCOUNT_KIND3_004);
	}

	/**
	 * 仕掛品
	 * @param kindCode1
	 * @param kindCode2
	 * @param kindCode3
	 * @return
	 */
	private boolean isWorkProgress(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_03)
				&& kindCode3.equals(ACCOUNT_KIND3_005);
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
