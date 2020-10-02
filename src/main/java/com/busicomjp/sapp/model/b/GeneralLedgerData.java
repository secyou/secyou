package com.busicomjp.sapp.model.b;

import lombok.Data;

@Data
public class GeneralLedgerData {

	//発生日
	private String accrualDate;
	//相手勘定科目
	private String counterAccount;
	// 摘要
	private String tekiyo;
	//借方金額
	private String debitAmountMoney;
	//貸方金額
	private String creditAmountMoney;
	//残高
	private String balanceMoney;
	//総勘定元帳No
	private String generalNo;
	//合計行判定フラグ
	private String sum_flg;
	//sortKey設定
	private String sortKey;

}
