package com.busicomjp.sapp.model.d;

import lombok.Getter;

public class AccountSeletedData {
	
	// 分類
	@Getter
	private static String kindGroup;
	
	// 借方貸方区分
	@Getter
	private static String kindFlg;
	
	// 勘定科目名
	@Getter
	private static String accountName;
	
	// 勘定科目名
	@Getter
	private static String accountCode;
	
	// 勘定科目かな
	@Getter
	private static String accountNameKana;
	
	// 使用区分
	@Getter
	private static String useFlg;
	
	public static void setAccountSeleteData(AccountMaintData data) {
		kindGroup = data.getKindGroup();
		kindFlg = data.getKindFlg();
		accountName = data.getAccountName();
		accountCode = data.getAccountCode();
		accountNameKana = data.getAccountNameKana();
		useFlg = data.getUseFlg();
	}
	
}
