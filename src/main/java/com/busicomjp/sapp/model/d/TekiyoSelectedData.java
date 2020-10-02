package com.busicomjp.sapp.model.d;

import lombok.Data;
import lombok.Getter;

@Data
public class TekiyoSelectedData {

	@Getter
	private static String tekiyoName;
	@Getter
	private static String tekiyoCode;
	@Getter
	private static String tekiyoNameKana;
	@Getter
	private static String accountCode; // 勘定科目コード
	@Getter
	private static String defaultAccountCode; // 科目分類デフォルトの勘定科目コード
	@Getter
	private static String defaultAccountName; // 科目分類デフォルトの勘定科目名
	@Getter
	private static String salesAccountCode; // 科目分類販売の勘定科目コード
	@Getter
	private static String salesAccountName; // 科目分類販売の勘定科目名
	@Getter
	private static String mgmtAccountCode; // 科目分類管理の勘定科目コード
	@Getter
	private static String mgmtAccountName; // 科目分類管理の勘定科目名
	@Getter
	private static String costAccountCode; // 科目分類原価の勘定科目コード
	@Getter
	private static String costAccountName; // 科目分類原価の勘定科目名
	
	public static void setTekiyoUtil(TekiyoData data) {
		tekiyoName = data.getTekiyoName();
		tekiyoCode = data.getTekiyoCode();
		tekiyoNameKana = data.getTekiyoNameKana();
		accountCode = data.getAccountCode();
		defaultAccountCode = data.getDefaultAccountCode();
		defaultAccountName = data.getDefaultAccountName();
		salesAccountCode = data.getSalesAccountCode();
		salesAccountName = data.getSalesAccountName();
		mgmtAccountCode = data.getMgmtAccountCode();
		mgmtAccountName = data.getMgmtAccountName();
		costAccountCode = data.getCostAccountCode();
		costAccountName = data.getCostAccountName();
	}	
}
