package com.busicomjp.sapp.model.d;

import lombok.Data;

@Data
public class TorihikisakiData {

	private String companyCode;
	private String torihikisakiCode;
	private String torihikisakiType;
	private String torihikisakiSearchName;
	private String torihikisakiName;
	private String torihikisakiNameKana;
	private String supplierKbn;
	private String customerKbn;
	private String unpaidKbn;
	private String delFlg;
	
	private String accountKind1;
	private String accountKind2;
	private String accountKind3;
	private String accountKind4;
	private String accountCode;
	private String accountName;
	private String jyufukuCount;
}
