package com.busicomjp.sapp.dto.b;

import com.busicomjp.sapp.common.constant.CommonConstants;

import lombok.Data;

@Data
public class AccountsReceivableDto {
	private String companyCode;
	// 開始年月(yyyyMM)
	private String kiStartYearMonth;
	// 終了年月(yyyyMM)
	private String kiEndYearMonth;
	/** 前期繰越 */
	private String accountCodeCarryforward = CommonConstants.ACCOUNT_CODE.CARRY_FORWARD;
	/** 次期繰越 */
	private String accountCodeNextCarryforward = CommonConstants.ACCOUNT_CODE.NEXT_CARRY_FORWARD;
}
