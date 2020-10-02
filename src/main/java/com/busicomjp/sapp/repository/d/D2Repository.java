package com.busicomjp.sapp.repository.d;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.model.d.TorihikisakiData;

@Repository
public interface D2Repository {
	
	//取引先名検索
	List<TorihikisakiData> selectToriHikiName(String companyCode,String searchName);
	
	//仕入先検索
	List<ComboItem> selectSupplierList(String companyCode);
	
	//得意先検索
	List<ComboItem> selectCustomerList(String companyCode);
	
	//未払先検索
	List<ComboItem> selectUnpaidList(String companyCode);

	// 取引先コード最大値取得
	String getMaxTorihikisakiCode();
	
	//取引先マスタ登録
	void insertTorihikisakiData(String companyCode,String torihikiCode,String torihikiName,String torihikiKana,String comboSelectedCode,String torihikiType);
	
	//取引先マスタ一括更新
	void updateTorihikisakiDataMultiple(String companyCode,String torihikiCode,String torihikiName,String kana);
	
	//取引先マスタ該当レコードのみ更新
	void updateTorihikisakiDataOnly(String companyCode,String torihikiCode,String supplierCode,String torihikisakiTypeKbn,String delFlg);
	
	//DBに存在してるか確認
	String existJudgeSelect(String companyCode,String torihikiCode,String torihikisakiType);
}
