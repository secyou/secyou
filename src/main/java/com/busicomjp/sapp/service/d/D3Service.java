package com.busicomjp.sapp.service.d;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.model.d.AccountMaintData;
import com.busicomjp.sapp.repository.d.D3Repository;

@Service
public class D3Service {

	@Autowired
	D3Repository z3Repo;
	
	//勘定科目検索
	public List<AccountMaintData> getAccountListData(String searchKindName, String searchAccountName) {
		
		//勘定科目一覧検索
		List<AccountMaintData> list = z3Repo.selectAccountMainData(CompanyUtil.getCompanyCode(),searchKindName,searchAccountName);
		
		//なければ
		if (list == null || list.size() == 0) {
			return new ArrayList<AccountMaintData>();
		}
		
		for (int i = 0; i < list.size(); i++) {
			
			//分類1~分類4の名称を結合して表示
			list.get(i).setKindGroup(list.get(i).getAccountKind1Name() + "→" +
					list.get(i).getAccountKind2Name() + "→" +
					list.get(i).getAccountKind3Name() + "→" +
					list.get(i).getAccountKind4Name());
			
			//借/貸
			if ("1".equals(list.get(i).getKindFlg())) {
				list.get(i).setKindFlg("借");
			}else if("2".equals(list.get(i).getKindFlg())) {
				list.get(i).setKindFlg("貸");
			}
			
			//使用フラグ
			if("1".equals(list.get(i).getUseFlg())) {
				list.get(i).setUseFlg("〇");
			}else {
				list.get(i).setUseFlg("");}
		}
		return list;
	}
	
	//借方貸方区分取得
	public String getKindKbn(String companyCode,String selectCode,String selectCode2,String selectCode3,String selectCode4) {
		return z3Repo.getKindKbn(CompanyUtil.getCompanyCode(),selectCode,selectCode2,selectCode3,selectCode4);
	}
	
	//Max勘定科目コード取得
	public String getMaxAccountCd(String companyCode) {
		return z3Repo.getMaxAccountCd(companyCode);
	}
	
	//Max摘要コード取得
	public String getMaxSummaryCd(String companyCode) {
		return z3Repo.getMaxSummaryCd(companyCode);
	}
	
	//ｃ登録
	public void insertAccount(String companyCode,String accountKind1,String accountKind1Name,
							String accountKind2,String accountKind2_name,String accountKind3,
							String accountKind3_name,String accountKind4,String accountKind4_name,
							String accountCode,String accountName,String accountNameKana,String kindFlg) {
		z3Repo.insertAccountM(companyCode,accountKind1,
					accountKind1Name,accountKind2,accountKind2_name,accountKind3,
					accountKind3_name,accountKind4,accountKind4_name,accountCode,accountName,accountNameKana,kindFlg);
	}
	
	//摘要登録
	public void insertSummary(String companyCode,String tekiyoCd,
								String accountName,String accountNameKana,String accountCd) {
		
		z3Repo.insertSummaryM(companyCode,tekiyoCd,
					accountName,accountNameKana,accountCd);
	}
	
	//勘定科目更新
	public void updateAccount(String companyCode,String accounCode,String accountName,String accountNameKana,String useKbn) {
		
		z3Repo.updateAccountM(companyCode,accounCode,accountName,accountNameKana,useKbn);
	}
}
