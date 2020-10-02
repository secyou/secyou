package com.busicomjp.sapp.model.d;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.item.ComboItem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class CompanyData {

	private String companyCode;
	private String kimatuYear;
	private String kimatuMonthDay;
	// 消費税分類フラグ(0:非課税／1:課税)
	private String taxKindFlg;
	// 消費税集計フラグ(1:都度集計／2:月単位集計／3:年単位集計)
	private String taxAppFlg;
	// 事業タイプ
	private String businessType;
	// 会社名(個人事業主名)
	//private String companyName;
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
	// 事業年度期首年月
	private String kisyuYearMonth;
	// 事業年度期末年月
	private String kimatuYearMonth;
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
	// 期間短縮有無
	private String shortFlg;
	// 削除フラグ
	private String delFlg;
	// 初回年度フラグ
	private String firstFlg;

	private String accountStartDay;
	private String accountEndDay;

	private List<ComboItem> kimatuYearList;

	private final SimpleStringProperty companyName;
	private final SimpleStringProperty taxKindName;
	private final SimpleStringProperty taxAppName;
	private final SimpleStringProperty fiscalYear;
	private final SimpleStringProperty businessTypeName;
	private final SimpleStringProperty businessLineName;

	public CompanyData() {
		this.companyName = new SimpleStringProperty();
		this.businessTypeName = new SimpleStringProperty();
		this.businessLineName = new SimpleStringProperty();
		this.taxKindName = new SimpleStringProperty();
		this.taxAppName = new SimpleStringProperty();
		this.fiscalYear = new SimpleStringProperty();
	}
	
	public StringProperty companyNameProperty() {
		return companyName;
	}

	public StringProperty taxKindNameProperty() {
		return taxKindName;
	}

	public StringProperty taxAppNameProperty() {
		return taxAppName;
	}
	
	public StringProperty fiscalYearProperty() {
		return fiscalYear;
	}
	
	public StringProperty businessTypeNameProperty() {
		return businessTypeName;
	}
	
	public StringProperty businessLineNameProperty() {
		return businessLineName;
	}
	
	public String getCompanyName() {
		return companyName.get();
	}

	public String getTaxKindName() {
		return taxKindName.get();
	}

	public String getTaxAppName() {
		return taxAppName.get();
	}
	
	public String getFiscalYear() {
		return fiscalYear.get();
	}
	
	public void setCompanyName(String value) {
		companyName.set(value);
	}
	
	public String getBusinessTypeName() {
		return businessTypeName.get();
	}
	
	public String getBusinessLineName() {
		return businessLineName.get();
	}

	public void setTaxKindName(String taxKindFlg) {
		switch(taxKindFlg) {
		case "0" :
			taxKindName.set("非課税");
			break;
		case "1" :
			taxKindName.set("課税");
			break;
		default :
			taxKindName.set("");
		}
	}

	public void setTaxAppName(String taxAppFlg) {
		switch(taxAppFlg) {
		case "1" :
			taxAppName.set("都度集計");
			break;
		case "2" :
			taxAppName.set("月単位集計");
			break;
		case "3" :
			taxAppName.set("年単位集計");
			break;
		default :
			taxAppName.set("");
		}
	}
	
	public void setFiscalYear(String value) {
		fiscalYear.set(value);
	}
	
	public void setBusinessTypeName(String value) {
		switch(businessType) {
		case "1" :
			businessTypeName.set("法人");
			break;
		case "2" :
			businessTypeName.set("個人");
			break;
		default :
			businessTypeName.set("");
		}
	}
	
	public void setBusinessLineName(String value) {
		switch(businessLine) {
		case "1" :
			businessLineName.set("一般事業");
			break;
		case "2" :
			businessLineName.set("不動産事業");
			break;
		default :
			businessLineName.set("");
		}
	}

	/**
     * 会計日付を設定
     */
	public void setAccountDate(String kimatuYear, String kimatuMonthDay) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
			Calendar calendar = Calendar.getInstance();

			Date endDate = sdf.parse(kimatuYear + kimatuMonthDay);
			calendar.setTime(endDate);
			calendar.add(Calendar.YEAR, -1);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date startDate = calendar.getTime();

			accountStartDay = sdf.format(startDate);
			accountEndDay = sdf.format(endDate);
			fiscalYear.set(sdfSlash.format(startDate) + "～" + sdfSlash.format(endDate));
		} catch (ParseException e) {
			throw new SystemException(e);
		}
	}

}
