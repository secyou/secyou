package com.busicomjp.sapp.dto.e;


import lombok.Data;

@Data
public class InputDataDto {
	private String companyCode;

	// 分類1コード
	private String selectCode;
	// 分類2コード
	private String selectCode2;
	// 分類3コード
	private String selectCode3;
	// 分類4コード
	private String selectCode4;
	// 分類4コード
	private String accountCode;

	private String amountMoney;

	private String startDate;
	//年度会計開始日
	private String endDate;
	//年度会計期末日

}
