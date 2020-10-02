package com.busicomjp.sapp.model.d;

import lombok.Data;

@Data
public class TekiyoData {
	
	//摘要マスタ
	private String tekiyoCode;
	private String tekiyoName;
	private String tekiyoNameKana;
	private String accountKind;
	private String accountKindName;
	private String accountCode;
	private String summaryDelFlg;
	//勘定科目マスタ
	private String accountName;
	private String defaultAccountCode; // 科目分類デフォルトの勘定科目コード
	private String defaultAccountName; // 科目分類デフォルトの勘定科目名
	private String salesAccountCode; // 科目分類販売の勘定科目コード
	private String salesAccountName; // 科目分類販売の勘定科目名
	private String mgmtAccountCode; // 科目分類管理の勘定科目コード
	private String mgmtAccountName; // 科目分類管理の勘定科目名
	private String costAccountCode; // 科目分類原価の勘定科目コード
	private String costAccountName; // 科目分類原価の勘定科目名
	
	private String accountKind1;
	private String accountKind1Name;
	private String accountKind2;
	private String accountKind2Name;
	private String accountKind3;
	private String accountKind3Name;
	private String accountKind4;
	private String accountKind4Name;
	
}
