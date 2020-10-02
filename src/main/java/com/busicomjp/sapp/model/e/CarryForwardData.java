package com.busicomjp.sapp.model.e;

import lombok.Data;

@Data
public class CarryForwardData {

	private String accrualDate;
	private String accountKind1;
	private String accountKind2;
	private String accountKind3;
	private String accountKind4;
	private String accountCode;

	private String accountKind1Name;
	private String accountKind2Name;
	private String accountKind3Name;
	private String accountKind4Name;
	private String accountName;

	private String debitAmountMoney;
	private String creditAmountMoney;

	private String journalNo;

	private String torihikisakiCode;
	private String torihikisakiName;
	private String kindFlg;
	private String kindName;

}
