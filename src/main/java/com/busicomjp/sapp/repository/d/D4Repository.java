package com.busicomjp.sapp.repository.d;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.model.d.TekiyoData;

@Repository
public interface D4Repository {
	
	//勘定科目一覧取得
	List<TekiyoData> selectSummaryMainData(String companyCode,String searchName);
	List<ComboItem> getKind1DataList(String companyCode,String kindKbnFlg);
	List<ComboItem> getKind2DataList(String companyCode,String code1,String kindKbnFlg);
	List<ComboItem> getKind3DataList(String companyCode,String code1,String code2,String kindKbnFlg);
	List<ComboItem> getKind4DataList(String companyCode,String code1,String code2,String code3,String kindKbnFlg);
	List<ComboItem> getKindDataList(String companyCode,String code1,String code2,String code3,String code4,String kindKbnFlg);

	//Max摘要コード取得
	String getMaxSummaryCd(String companyCode);
	void insertSummaryMain(String companyCd,String tekiyoCd,
			String name,String nameKana,String accountKind,String accountCode);
	
	List<TekiyoData> getSelectedKindData(String companyCode,String kindKbnFlg,String name);
	
	List<TekiyoData> getDataByselected(String companyCode,String tekiyoCode);
	void updateSummary(String companyCd,String tekiyoCd,
			String name,String nameKana,String accountKind,String accountCode);
	void deleteSummary(String companyCd,String accountCode,String tekiyoCd,String accountKind);
}
