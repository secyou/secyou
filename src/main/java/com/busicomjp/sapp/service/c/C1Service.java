package com.busicomjp.sapp.service.c;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.ReportUtil;
import com.busicomjp.sapp.model.b.BalanceData;
import com.busicomjp.sapp.model.c.ProfitAndLossReportRowData;
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
public class C1Service {

	@Autowired
	B1Service b1Service;

	private final String CONST_TEMPLATE_NAME = "Profit_Loss.jrxml";
	private final String CONST_REPORT_NAME = "損益計算書";
	@Value("${jasper.jrxml.file.path}")
	private String jasperJrxmlFilePath;
	@Value("${report.output.path}")
	private String reportOutputPath;

	// 勘定科目第一分類コード
	private static String ACCOUNT_KIND1_4 = "4";
	private static String ACCOUNT_KIND1_5 = "5";
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
	// 勘定科目第四分類コード
	private static String ACCOUNT_KIND4_0001 = "0001";
	private static String ACCOUNT_KIND4_0002 = "0002";
	private static String ACCOUNT_KIND4_0003 = "0003";
	private static String ACCOUNT_KIND4_0004 = "0004";
	private static String ACCOUNT_KIND4_0005 = "0005";

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
		String filePath = generatePdfReport(convertSearchResultToPdfData(balanceDatas));
		try {
			ReportUtil.openPdf(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generatePdfReport(List<ProfitAndLossReportRowData> rowDatas) {
		String filePath = ReportUtil.getReportFilePath(reportOutputPath, CONST_REPORT_NAME);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("startDay",
				CompanyUtil.getAccountStartDay().substring(0, 4) + "年"
						+ CompanyUtil.getAccountStartDay().substring(4, 6) + "月"
						+ CompanyUtil.getAccountStartDay().substring(6, 8) + "日");
		parameters.put("endDay",
				CompanyUtil.getAccountEndDay().substring(0, 4) + "年"
						+ CompanyUtil.getAccountEndDay().substring(4, 6)+ "月"
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

	private List<ProfitAndLossReportRowData> convertSearchResultToPdfData(List<BalanceData> balanceDatas) {
		// 同様な分類コード金額統合
		Map<String, LongSummaryStatistics> uniqueAmounts = balanceDatas.stream()
			.collect(Collectors.groupingBy(data ->
							data.getAccountKind1()
							+ data.getAccountKind2()
							+ data.getAccountKind3()
							+ data.getAccountKind4(), Collectors.summarizingLong(BalanceData::getBalance)));

		List<ProfitAndLossReportRowData> pLRowDatas = new ArrayList<>();
		//売上高
		Long netSales = 0L;
		Long costOfSales = 0L;
		Long manufacturing  = 0L;
		Optional<BalanceData> optNetSales = balanceDatas.stream()
				.filter(data -> isNetSales(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.findAny();
		if (optNetSales.isPresent()) {
			pLRowDatas.add(new ProfitAndLossReportRowData("売上高","","",""));
			balanceDatas.stream()
			.filter(data -> isNetSales(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//売上高合計
			netSales = balanceDatas.stream()
					.filter(data -> isNetSales(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","売上高合計","",formatNumber(netSales)));
		}

		//売上原価
		Optional<BalanceData> optCostOfSales = balanceDatas.stream()
				.filter(data -> isCostOfSales(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.findAny();
		if (optCostOfSales.isPresent()) {
			pLRowDatas.add(new ProfitAndLossReportRowData("売上原価","","",""));

			Optional<BalanceData> optBeginningInventory = balanceDatas.stream()
					.filter(data -> isBeginningInventory(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3(), data.getAccountKind4()))
					.findAny();
			//期首棚卸高・仕入高
			if (optBeginningInventory.isPresent()) {
				balanceDatas.stream()
				.filter(data -> isBeginningInventory(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3(), data.getAccountKind4()))
				.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum()))))
				.forEach(data -> {
					pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
							+ data.getAccountKind2()
							+ data.getAccountKind3()
							+ data.getAccountKind4()).getSum())));
				});
				//期首棚卸高・仕入高合計
				Long kisyuInventoryPurchase = balanceDatas.stream()
				.filter(data -> isBeginningInventory(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3(), data.getAccountKind4()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
				pLRowDatas.add(new ProfitAndLossReportRowData("","期首棚卸高・仕入高合計","",formatNumber(kisyuInventoryPurchase)));
			}

			Optional<BalanceData> optEndingInventory = balanceDatas.stream()
					.filter(data -> isEndingInventory(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3(), data.getAccountKind4()))
					.findAny();
			if (optEndingInventory.isPresent()) {
				//期末棚卸高
				balanceDatas.stream()
				.filter(data -> isEndingInventory(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3(), data.getAccountKind4()))
				.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum()))))
				.forEach(data -> {
					pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
							+ data.getAccountKind2()
							+ data.getAccountKind3()
							+ data.getAccountKind4()).getSum())));
				});
				//期末棚卸高合計
				Long kimatuInventory = balanceDatas.stream()
				.filter(data -> isEndingInventory(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3(), data.getAccountKind4()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
				pLRowDatas.add(new ProfitAndLossReportRowData("","期末棚卸高合計","",formatNumber(kimatuInventory)));
			}

			Optional<BalanceData> optManufacturing = balanceDatas.stream()
					.filter(data -> isManufacturing(data.getAccountKind1(), data.getAccountKind2()))
					.findAny();
			if (optManufacturing.isPresent()) {
				//当期製品製造原価
				manufacturing = balanceDatas.stream()
						.filter(data -> isManufacturing(data.getAccountKind1(), data.getAccountKind2()))
						.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
				pLRowDatas.add(new ProfitAndLossReportRowData("", "当期製品製造原価", "", formatNumber(manufacturing)));
			}

			//売上原価合計
			costOfSales = balanceDatas.stream()
			.filter(data -> isCostOfSales(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","売上原価合計","",formatNumber(costOfSales)));
		}

		if (optNetSales.isPresent() || optCostOfSales.isPresent()) {
			// 売上総利益・売上総損失
			pLRowDatas.add(new ProfitAndLossReportRowData("", "", (netSales - costOfSales - manufacturing) >= 0 ? "売上総利益" : "売上総損失",
					formatNumber(Math.abs(netSales - costOfSales - manufacturing))));
		}

		Optional<BalanceData> optOfficer = balanceDatas.stream()
		.filter(data -> isOfficer(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if (optOfficer.isPresent()) {
			//役員費用
			pLRowDatas.add(new ProfitAndLossReportRowData("役員費用","","",""));
			balanceDatas.stream()
			.filter(data -> isOfficer(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//役員費用合計
			Long officerExpenses = balanceDatas.stream()
			.filter(data -> isOfficer(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","役員費用合計","",formatNumber(officerExpenses)));
		}

		Optional<BalanceData> optSelling = balanceDatas.stream()
		.filter(data -> isSelling(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if (optSelling.isPresent()) {
			//販売費
			pLRowDatas.add(new ProfitAndLossReportRowData("販売費","","",""));
			balanceDatas.stream()
			.filter(data -> isSelling(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//販売費合計
			Long sellingExpenses = balanceDatas.stream()
			.filter(data -> isSelling(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","販売費合計","",formatNumber(sellingExpenses)));
		}

		Optional<BalanceData> optAdministrative = balanceDatas.stream()
		.filter(data -> isAdministrative(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if(optAdministrative.isPresent()) {
			//管理費
			pLRowDatas.add(new ProfitAndLossReportRowData("管理費","","",""));
			balanceDatas.stream()
			.filter(data -> isAdministrative(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//管理費合計
			Long administrativeExpenses = balanceDatas.stream()
			.filter(data -> isAdministrative(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","管理費合計","",formatNumber(administrativeExpenses)));
		}

		//販売費・一般管理費
		Long operatingCost = 0L;
		Optional<BalanceData> optOperatingCost = balanceDatas.stream()
		.filter(data -> isOperatingCost(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if (optOperatingCost.isPresent()) {
			operatingCost = balanceDatas.stream()
			.filter(data -> isOperatingCost(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("販売費・一般管理費","","",formatNumber(operatingCost)));
		}
		// 営業利益・営業損失
		pLRowDatas.add(new ProfitAndLossReportRowData("", "", (netSales - costOfSales - manufacturing - operatingCost) >= 0 ? "営業利益" : "営業損失",
				formatNumber(Math.abs(netSales - costOfSales - manufacturing - operatingCost))));

		Long nonOperatingProfit = 0L;
		Long nonOperatingLoss = 0L;
		Optional<BalanceData> optNonOperatingProfit = balanceDatas.stream()
		.filter(data -> isNonOperatingProfit(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if(optNonOperatingProfit.isPresent()) {
			//営業外収益
			pLRowDatas.add(new ProfitAndLossReportRowData("営業外収益","","",""));
			balanceDatas.stream()
			.filter(data -> isNonOperatingProfit(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//営業外収益合計
			nonOperatingProfit = balanceDatas.stream()
					.filter(data -> isNonOperatingProfit(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","営業外収益合計","",formatNumber(nonOperatingProfit)));
		}

		Optional<BalanceData> optNonOperatingLoss = balanceDatas.stream()
		.filter(data -> isNonOperatingLoss(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if (optNonOperatingLoss.isPresent()) {
		//営業外費用
			pLRowDatas.add(new ProfitAndLossReportRowData("営業外費用","","",""));
			balanceDatas.stream()
			.filter(data -> isNonOperatingLoss(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//営業外費用合計
			nonOperatingLoss = balanceDatas.stream()
					.filter(data -> isNonOperatingLoss(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","営業外費用合計","",formatNumber(nonOperatingLoss)));
		}
		//経常利益・経常損失
		Long ordinary = netSales - costOfSales  - manufacturing - operatingCost + nonOperatingProfit - nonOperatingLoss;
		pLRowDatas.add(new ProfitAndLossReportRowData("","",ordinary >= 0?"経常利益":"経常損失",formatNumber(Math.abs(ordinary))));

		Long specialProfit = 0L;
		Long specialLoss = 0L;
		Optional<BalanceData> optSpecialProfit = balanceDatas.stream()
		.filter(data -> isSpecialProfit(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if(optSpecialProfit.isPresent()) {
			//特別利益
			pLRowDatas.add(new ProfitAndLossReportRowData("特別利益","","",""));
			balanceDatas.stream()
			.filter(data -> isSpecialProfit(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
			.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum()))))
			.forEach(data -> {
				pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
						+ data.getAccountKind2()
						+ data.getAccountKind3()
						+ data.getAccountKind4()).getSum())));
			});
			//特別利益合計
			specialProfit = balanceDatas.stream()
					.filter(data -> isSpecialProfit(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
					.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
			pLRowDatas.add(new ProfitAndLossReportRowData("","特別利益合計","",formatNumber(specialProfit)));
		}

		Optional<BalanceData> optSpecialLoss = balanceDatas.stream()
		.filter(data -> isSpecialLoss(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3())).findAny();
		if (optSpecialLoss.isPresent()) {
		//特別損失
		pLRowDatas.add(new ProfitAndLossReportRowData("特別損失","","",""));
		balanceDatas.stream()
		.filter(data -> isSpecialLoss(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
		.filter(data -> !pLRowDatas.contains(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
				+ data.getAccountKind2()
				+ data.getAccountKind3()
				+ data.getAccountKind4()).getSum()))))
		.forEach(data -> {
			pLRowDatas.add(new ProfitAndLossReportRowData("",data.getAccountKind4Name(),"",formatNumber(uniqueAmounts.get(data.getAccountKind1()
					+ data.getAccountKind2()
					+ data.getAccountKind3()
					+ data.getAccountKind4()).getSum())));
		});
		//特別損失合計
		specialLoss = balanceDatas.stream()
				.filter(data -> isSpecialLoss(data.getAccountKind1(), data.getAccountKind2(), data.getAccountKind3()))
				.map(BalanceData::getBalance).reduce(Long::sum).orElse(0L);
		pLRowDatas.add(new ProfitAndLossReportRowData("","特別損失合計","",formatNumber(specialLoss)));
		}
		// 税引前当期純利益・税引前当期純損失
		Long pretaxOfTheCurrentTerm = ordinary + specialProfit - specialLoss;
		pLRowDatas.add(new ProfitAndLossReportRowData("","",pretaxOfTheCurrentTerm >= 0?"税引前当期純利益":"税引前当期純損失",formatNumber(Math.abs(pretaxOfTheCurrentTerm))));
		// TODO 法人税、住民税及び事業税
		Long taxs = 0L;
		pLRowDatas.add(new ProfitAndLossReportRowData("法人税、住民税及び事業税","","",formatNumber(Math.abs(taxs))));
		// 当期純利益・当期純損失
		Long profitLoss = pretaxOfTheCurrentTerm - taxs;
		pLRowDatas.add(new ProfitAndLossReportRowData("","",profitLoss >= 0?"当期純利益":"当期純損失",formatNumber(Math.abs(profitLoss))));

		return pLRowDatas;
	}

	/**
	 * 売上高
	 */
	private boolean isNetSales(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_4) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	/**
	 * 期首棚卸高・仕入高
	 */
	private boolean isBeginningInventory(String kindCode1, String kindCode2, String kindCode3, String kindCode4) {
		return (kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_001))
				&& (kindCode4.equals(ACCOUNT_KIND4_0001) || kindCode4.equals(ACCOUNT_KIND4_0002)
						|| kindCode4.equals(ACCOUNT_KIND4_0003));
	}

	/**
	 * 製造原価
	 */
	private boolean isManufacturing(String kindCode1, String kindCode2) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_03);
	}

	private boolean isEndingInventory(String kindCode1, String kindCode2, String kindCode3, String kindCode4) {
		return (kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_001))
				&& (kindCode4.equals(ACCOUNT_KIND4_0004) || kindCode4.equals(ACCOUNT_KIND4_0005));
	}

	private boolean isCostOfSales(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	private boolean isOfficer(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_002);
	}

	private boolean isSelling(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_003);
	}

	private boolean isAdministrative(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_004);
	}

	private boolean isOperatingCost(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5)&&((kindCode2.equals(ACCOUNT_KIND2_01) && (kindCode3.equals(ACCOUNT_KIND3_002)
				|| kindCode3.equals(ACCOUNT_KIND3_003)
				|| kindCode3.equals(ACCOUNT_KIND3_004))));
	}

	private boolean isNonOperatingProfit(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_4) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_002);
	}

	private boolean isNonOperatingLoss(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_01)
				&& kindCode3.equals(ACCOUNT_KIND3_005);
	}

	private boolean isSpecialProfit(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_4) && kindCode2.equals(ACCOUNT_KIND2_02)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	private boolean isSpecialLoss(String kindCode1, String kindCode2, String kindCode3) {
		return kindCode1.equals(ACCOUNT_KIND1_5) && kindCode2.equals(ACCOUNT_KIND2_02)
				&& kindCode3.equals(ACCOUNT_KIND3_001);
	}

	private String formatNumber(Long num) {
		if (num.equals(0L)) {
			return String.valueOf(num);
		}
		NumberFormat nf = NumberFormat.getNumberInstance();
		return nf.format(num).replace("-", "△");
	}
}
