package com.busicomjp.sapp.dto.a;

import lombok.Data;

@Data
public class GeneralLedgerDto {
	
	private String companyCode;
	private String generalNo;
	private String journalNo;
	private String accrualDate;
	private String accountCode;
	private String counterAccount;
	private String tekiyoCode;
	private long debitAmountMoney;
	private long creditAmountMoney;
	private long balanceMoney;
	private String depPayDate;
	
	private String accountStartDay;
	private String accountEndDay;

}
