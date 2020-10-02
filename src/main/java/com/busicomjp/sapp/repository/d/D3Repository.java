package com.busicomjp.sapp.repository.d;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.model.d.AccountMaintData;

@Repository
public interface D3Repository {
	
	//勘定科目一覧取得
	List<AccountMaintData> selectAccountMainData(String companyCode,String searchKindName, String searchAccountName);
	
	//借方貸方区分取得
	String getKindKbn(String companyCode,String selectCode,String selectCode2,String selectCode3,String selectCode4);
	
	//Max勘定科目コード取得
	String getMaxAccountCd(String companyCode);

	//Max摘要コード取得
	String getMaxSummaryCd(String companyCode);
	
	//勘定科目登録
	void insertAccountM(String companyCode,String accountKind1,String accountKind1Name,
			String accountKind2,String accountKind2_name,
			String accountKind3,String accountKind3_name,
			String accountKind4,String accountKind4_name,
			String accountCode,String accountName,String accountNameKana,String kindFlg);
	
	//摘要登録
	void insertSummaryM(String companyCode,String tekiyoCd,
			String accountName,String accountNameKana,String accountCd);

	//勘定科目登録
	void updateAccountM(String companyCode,String accounCode,String accountName,String accountNameKana,String useKbn);
	
}
