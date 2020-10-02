package com.busicomjp.sapp.repository.d;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.d.CompanyData;

@Repository
public interface D1Repository {

	List<CompanyData> getCompanyDataList();

	// 会社情報検索
	List<CompanyData> findCompanyDataList(CompanyMDto companyMDto);

	// 会社情報登録
	void insertCompanyData(CompanyMDto companyMDto);

	// 全体会社情報変更
	void updateAllCompany(CompanyMDto companyMDto);

	// 本年度会社情報変更
	void updateCurrentCompany(CompanyMDto companyMDto);

	// 以降年度会社情報変更
	void updateAfterCurrentCompany(CompanyMDto companyMDto);

	// 摘要マスタ
	void insertSummaryData(String companyCode);

	// 勘定科目マスタ
	void insertAccountData(String companyCode);

	// 会社コード最大値取得
	String getMaxCompanyCode();

	// 前期繰越データ件数取得
	Integer getCarryforwardJournalEntryCount(CompanyDto companyDto);

	//翌年度の会社マスタが存在するかを確認
	Integer getRegistNextYearData(CompanyMDto companyMDto);
}
