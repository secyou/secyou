package com.busicomjp.sapp.model.z;

import lombok.Data;

@Data
public class AccountData {
	
	private String accountCode;
	private String accountName;
	
	//分類コード
	private String accountKind1;
	private String accountKind2;
	private String accountKind3;
	private String accountKind4;
	//借方貸方区分
	private String kindFlg;
	
	//分類コード結合文字列
	private String accountKindUnion;

}
