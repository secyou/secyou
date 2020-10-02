package com.busicomjp.sapp.model.a;

import lombok.Data;

@Data
public class BalanceData {
	// 区分
	private String kbn;
	private long balance;
	
	// 分類コード
	private String kindCode;
}
