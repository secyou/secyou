package com.busicomjp.sapp.common.util;

import com.busicomjp.sapp.model.d.TorihikisakiData;

import lombok.Getter;

public class TorihikisakiUtil {
	
	// 取引先コード
	@Getter
	private static String torihikisakiCode;
	
	// 取引先種別
	@Getter
	private static String torihikisakiType;
	
	// 取引先名
	@Getter
	private static String torihikisakiName;
	
	// 取引先かな
	@Getter
	private static String torihikisakiNameKana;
	
	// 勘定科目コード
	@Getter
	private static String accountCode;
	
	// 支払先
	@Getter
	private static String supplierKbn;
	
	// 得意先
	@Getter
	private static String customerKbn;
	
	// 未払先
	@Getter
	private static String unpaidKbn;
	
	// 削除区分
	@Getter
	private static String delFlg;

	
	public static void setTorihikisakiUtil(TorihikisakiData data) {
		torihikisakiCode = data.getTorihikisakiCode();
		torihikisakiType = data.getTorihikisakiType();
		torihikisakiName = data.getTorihikisakiName();
		torihikisakiNameKana = data.getTorihikisakiNameKana();
		accountCode = data.getAccountCode();
		supplierKbn = data.getSupplierKbn();
		customerKbn = data.getCustomerKbn();
		unpaidKbn = data.getUnpaidKbn();
		delFlg = data.getDelFlg();		
		
	}
	
}
