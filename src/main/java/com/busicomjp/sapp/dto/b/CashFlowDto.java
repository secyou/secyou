package com.busicomjp.sapp.dto.b;

import java.util.List;

import com.busicomjp.sapp.common.constant.CommonConstants;

import lombok.Data;

@Data
public class CashFlowDto {
	private String companyCode;
	// 開始年月(yyyyMM)
	private String kiStartYearMonth;
	// 終了年月(yyyyMM)
	private String kiEndYearMonth;
	/** 前期繰越 */
	private String accountCodeCarryforward = CommonConstants.ACCOUNT_CODE.CARRY_FORWARD;
	/** 次期繰越 */
	private String accountCodeNextCarryforward = CommonConstants.ACCOUNT_CODE.NEXT_CARRY_FORWARD;
	private String accountKind1 = null;
	private String accountKind2 = null;
	private String accountKind3 = null;
	private List<String> accountKind4List = null;
	private boolean accountKind4EqIn = true;
	// 集計方式
	private int aggregateMethod = 1;
	// 集計対象勘定科目
	private boolean aggregateAccountCode = false;
	// 支出
	private boolean paymentFlag = false;
}
