package com.busicomjp.sapp.dto.a;

import lombok.Data;

@Data
public class JournalEntryInsertDto {
	private String companyCode;
	private String journalNo;
	private String orgJournalNo;
	private String kindCode;
	private String accrualDate;
	private String suppliersCode;
	private String tekiyoCode;
	private String debitAccount;
	private String creditAccount;
	private long amountMoney;
	private long amountMoneyTax;
	private String taxCode;
	private String depPayDate;
	private String redFlg;
}
