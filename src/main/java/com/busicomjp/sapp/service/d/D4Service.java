package com.busicomjp.sapp.service.d;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.model.d.TekiyoData;
import com.busicomjp.sapp.repository.d.D4Repository;

@Service
public class D4Service {

	@Autowired
	D4Repository z4Repo;
	
	//勘定科目検索
	public List<TekiyoData> getSummaryListData(String searchName) {
		
		List<TekiyoData> dataListFinal = new ArrayList<TekiyoData>();
		
		//勘定科目一覧検索
		List<TekiyoData> list = z4Repo.selectSummaryMainData(CompanyUtil.getCompanyCode(),searchName);
		
		//なければ
		if (list == null || list.size() == 0) {
			return new ArrayList<TekiyoData>();
		}else {
			String tekiyoCd = list.get(0).getTekiyoCode();
			dataListFinal.add(list.get(0));
			
			for (int i = 0; i < list.size(); i++) {
				if (!tekiyoCd.equals(list.get(i).getTekiyoCode())) {
					tekiyoCd = list.get(i).getTekiyoCode();
					dataListFinal.add(list.get(i));
				}
				if("0".equals(list.get(i).getAccountKind())) {
					dataListFinal.get(dataListFinal.size()-1).setDefaultAccountName(list.get(i).getAccountName());
				}else if("1".equals(list.get(i).getAccountKind())) {
					dataListFinal.get(dataListFinal.size()-1).setSalesAccountName((list.get(i).getAccountName()));
				}else if("2".equals(list.get(i).getAccountKind())) {
					dataListFinal.get(dataListFinal.size()-1).setMgmtAccountName((list.get(i).getAccountName()));
				}else if("3".equals(list.get(i).getAccountKind())) {
					dataListFinal.get(dataListFinal.size()-1).setCostAccountName((list.get(i).getAccountName()));
				}
			}
		}
		
		return dataListFinal;
	}

	/**
     * 勘定科目マスタテーブル_分類1取得
     */
	public List<ComboItem> getKind1DataList(String companyCode,String kindKbnFlg) {

		return z4Repo.getKind1DataList(companyCode,kindKbnFlg);

	}

	/**
     * 勘定科目マスタテーブル_分類2取得
     */
	public List<ComboItem> getKind2DataList(String companyCode,String code1,String kindKbnFlg) {

		return z4Repo.getKind2DataList(companyCode,code1,kindKbnFlg);

	}

	/**
     * 勘定科目マスタテーブル_分類3取得
     */
	public List<ComboItem> getKind3DataList(String companyCode,String code1,String code2,String kindKbnFlg) {

		return z4Repo.getKind3DataList(companyCode,code1,code2,kindKbnFlg);

	}

	/**
     * 勘定科目マスタテーブル_分類4取得
     */
	public List<ComboItem> getKind4DataList(String companyCode,String code1,String code2,String code3,String kindKbnFlg) {

		return z4Repo.getKind4DataList(companyCode,code1,code2,code3,kindKbnFlg);

	}
	
	/**
     * 勘定科目マスタテーブル_勘定科目コード取得
     */
	public List<ComboItem> getKindDataList(String companyCode,String code1,String code2,String code3,String code4,String kindKbnFlg) {

		return z4Repo.getKindDataList(companyCode,code1,code2,code3,code4,kindKbnFlg);

	}
	
	//Max摘要コード取得
	public String getMaxSummaryCd(String companyCode) {
		return z4Repo.getMaxSummaryCd(companyCode);
	}
	
	//摘要マスタ登録
	public void insertSummary(String companyCd,String tekiyoCd,
			String name,String nameKana,String accountKind,String accountCode) {
		z4Repo.insertSummaryMain(companyCd,tekiyoCd,name,nameKana,accountKind,accountCode);
	}
	
	//初期画面表示用ComboList取得
	public List<TekiyoData> getSelectedKindData(String companyCd,String kindKbnFlg,String name) {
		return z4Repo.getSelectedKindData(companyCd,kindKbnFlg,name);
	}
	
	//選択されたレコード情報取得
	public List<TekiyoData> getDataByselected(String companyCd,String tekiyoCd) {
		return z4Repo.getDataByselected(companyCd,tekiyoCd);
	}
	
	//摘要マスタ更新
	public void updateSummary(String companyCd,String tekiyoCd,
			String name,String nameKana,String accountKind,String accountCode) {
		z4Repo.updateSummary(companyCd,tekiyoCd,name,nameKana,accountKind,accountCode);
	}
	
	//摘要マスタ削除
	public void deleteSummary(String companyCd,String accountCode,String tekiyoCd,
			String accountKind) {
		z4Repo.deleteSummary(companyCd,accountCode,tekiyoCd,accountKind);
	}
}
