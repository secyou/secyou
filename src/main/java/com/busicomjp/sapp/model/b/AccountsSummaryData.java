package com.busicomjp.sapp.model.b;

import lombok.Data;

@Data
public class AccountsSummaryData {
	private String companyCode;
	private String torihikisakiCode;
	private String torihikisakiName;
	private String accrualDate;
	private Long depPay = 0L;
	private Long request = 0L;
	private Long balance = 0L;
}
