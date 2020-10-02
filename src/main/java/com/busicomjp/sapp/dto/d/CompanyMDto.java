package com.busicomjp.sapp.dto.d;

import lombok.Data;

@Data
public class CompanyMDto {

	// 会社コード
	private String companyCode;
	// 事業タイプ
	private String businessType;
	// 会社名(個人事業主名)
	private String companyName;
	// 会社名かな(個人事業主名かな)
	private String companyNameKana;
	// 会社住所
	private String streetAddress;
	// 法人番号
	private String corpNumber;
	// 事業種目
	private String businessLine;
	// 青色申告
	private String buleDec;
	// 設立年月日
	private String estDate;
	// 決算期
	private String settlPeriod;
	
	// 期首年
	private String kisyuYear;
	
	// 期首月
	private String kisyuMonth;
	
	// 事業年度期首年月
	private String kisyuYearMonth;
	
	// 事業年度期末月
	private String kimatuMonth;
	
	// 事業年度期末年
	private String kimatuYear;
	
	// 事業年度期末年月
	private String kimatuYearMonth;
	
	// 期末月日
	private String kimatuMonthDay;
	
	// 入力開始年
	private String inputStartYear;
	
	// 入力開始月
	private String inputStartMonth;
	
	// 入力開始年月
	private String inputStartYearMonth;
	
	// 電話番号
	private String telNumber;
	
	// FAX
	private String faxNumber;
	
	// メールアドレス
	private String mailAddress;
	
	// 代表取締役名前
	private String directorName;
	
	// 消費税分類フラグ
	private String taxKindFlg;
	
	// 消費税集計フラグ
	private String taxAppFlg;
	
	// 期間短縮有無
	private String shortFlg;
	
	// 削除フラグ
	private String delFlg;
}
