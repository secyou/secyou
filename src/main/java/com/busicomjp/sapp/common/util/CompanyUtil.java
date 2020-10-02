package com.busicomjp.sapp.common.util;

import com.busicomjp.sapp.model.d.CompanyData;

import lombok.Getter;

public class CompanyUtil {
	
	// 会社コード
	@Getter
	private static String companyCode;
	
	// 会社名称
	@Getter
	private static String companyName;
	
	// 年度会計開始日(yyyyMMdd)
	@Getter
	private static String accountStartDay;
	
	// 年度会計終了日(yyyyMMdd)
	@Getter
	private static String accountEndDay;
	
	// 会計期間(yyyy/MM/dd～yyyy/MM/dd)
	@Getter
	private static String fiscalYear;
	
	// 会計期末年度
	@Getter
	private static String kimatuYear;
	
	// 会計期末月日
	@Getter
	private static String kimatuMonthDay;
	
	// 消費税分類フラグ(0:非課税／1:課税)
	@Getter
	private static String taxKindFlg;
	
	// 消費税集計フラグ(1:都度集計／2:月単位集計／3:年単位集計)
	@Getter
	private static String taxAppFlg;
	
	// 最初入力年度フラグ
	@Getter
	private static boolean firstInputYear;
	
	
	public static void setCompanyUtil(CompanyData data) {
		companyCode = data.getCompanyCode();
		companyName = data.getCompanyName();
		kimatuYear = data.getKimatuYear();
		kimatuMonthDay = data.getKimatuMonthDay();
		taxKindFlg = data.getTaxKindFlg();
		taxAppFlg = data.getTaxAppFlg();
		accountStartDay = data.getAccountStartDay();
		accountEndDay = data.getAccountEndDay();
		fiscalYear = data.getFiscalYear();
		firstInputYear = "1".equals(data.getFirstFlg()) ? true : false;
	}
	
}
