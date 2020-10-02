package com.busicomjp.sapp.model.a;

import lombok.Data;

@Data
public class JournalEntryData {
	private String companyCode;
	private String journalNo;
	private String orgJournalNo;
	private String kindCode;
	private String accrualDate;
	private String suppliersCode;
	private String suppliersName;
	private String tekiyoCode;
	private String tekiyoName;
	private String debitAccount;
	private String debitName;
	private String creditAccount;
	private String creditName;
	// 金額
	private String amountMoney;
	// 税金
	private String tax;
	private String taxCode;
	// 税率
	private String taxRate;
	private String depPayDate;
	// 赤データ識別フラグ
	private String redFlg;
}
