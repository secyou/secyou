package com.busicomjp.sapp.dto.b;

import lombok.Data;

@Data
public class CompanyDto {
	private String companyCode;
	// 年度会計開始日(yyyyMMdd)
	private String accountStartDay;
	// 年度会計終了日(yyyyMMdd)
	private String accountEndDay;
}
