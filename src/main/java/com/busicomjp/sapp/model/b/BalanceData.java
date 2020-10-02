package com.busicomjp.sapp.model.b;

import lombok.Data;

@Data
public class BalanceData {
	
	private String accountCode;
	private String accountName;
	private long debitSummany;
	private long creditSummany;
	private long balance;

	private String accountKind1;
	private String accountKind1Name;
	private String accountKind2;
	private String accountKind2Name;
	private String accountKind3;
	private String accountKind3Name;
	private String accountKind4;
	private String accountKind4Name;
	
	private long debitCarryForwardBalance;
	private long creditCarryForwardBalance;
	
	private String debitCFBalance_s; // 前期繰越借方残高
	private String creditCFBalance_s; // 前期繰越貸方残高
	private String debitBalance_s; // 当期借方残高
	private String creditBalance_s; // 当期貸方残高
	private String debitTotalBalance_s; // 合計借方残高
	private String creditTotalBalance_s; // 合計貸方残高

}
