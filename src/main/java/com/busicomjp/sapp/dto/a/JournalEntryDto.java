package com.busicomjp.sapp.dto.a;

import lombok.Data;

@Data
public class JournalEntryDto {
	// 会社コード
	private String companyCode;
	// 仕訳No
	private String journalNo;
	// 分類コード
	private String kindCode;
	// 取引発生日
	private String accrualStartDate;
	private String accrualEndDate;
	// 取引先
	private String suppliersCode;
	private String suppliersName;
	// 適要
	private String tekiyoCode;
	private String tekiyoName;
	// 借方勘定科目
	private String debitAccount;
	private String debitAccountName;
	// 貸方勘定科目
	private String creditAccount;
	private String creditAccountName;
	// 金額
	private String amountMoneyStart;
	private String amountMoneyEnd;
	// 入金・支払予定日
	private String depPayStartDate;
	private String depPayEndDate;
}
