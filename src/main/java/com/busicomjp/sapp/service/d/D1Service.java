package com.busicomjp.sapp.service.d;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.d.CompanyData;
import com.busicomjp.sapp.repository.d.D1Repository;

@Service
public class D1Service {

	@Autowired
	D1Repository z1Repo;

	public List<CompanyData> getCompanyDataList() {
		List<CompanyData> dataList = z1Repo.getCompanyDataList();
		if (dataList == null || dataList.size() == 0) {
			return new ArrayList<CompanyData>();
		}
		for (CompanyData data : dataList) {
			data.setTaxKindName(data.getTaxKindFlg());
			data.setTaxAppName(data.getTaxAppFlg());
			// 会計日付を設定
			data.setAccountDate(data.getKimatuYear(), data.getKimatuMonthDay());
		}

		String companyCode = dataList.get(0).getCompanyCode();
		CompanyData companyData = dataList.get(0);
		companyData.setKimatuYearList(new ArrayList<ComboItem>());

		List<CompanyData> resultList = new ArrayList<CompanyData>();
		for (CompanyData data : dataList) {
			if (!companyCode.equals(data.getCompanyCode())) {
				companyCode = data.getCompanyCode();
				resultList.add(companyData);
				companyData = data;
				companyData.setKimatuYearList(new ArrayList<ComboItem>());
			}
			// ComboBoxを設定
			companyData.getKimatuYearList()
					.add(new ComboItem(data.getCompanyCode(), data.getKimatuYear(),
							data.getTaxKindFlg() +
							"," + data.getTaxAppFlg() +
							"," + data.getKimatuYear() +
							"," + data.getKimatuMonthDay() +
							"," + data.getCompanyName() +
							"," + data.getFirstFlg()
							));
		}
		resultList.add(companyData);
		return resultList;
	}

	public List<CompanyData> findCompanyByName(String companyName) {
		CompanyMDto companyMDto = new CompanyMDto();
		companyMDto.setCompanyName(companyName);
		List<CompanyData> resultList = z1Repo.findCompanyDataList(companyMDto);
		if (resultList == null || resultList.size() == 0) {
			return new ArrayList<CompanyData>();
		}

		resultList.forEach(result -> {
			result.setTaxKindName(result.getTaxKindFlg());
			result.setTaxAppName(result.getTaxAppFlg());
			// 会計日付を設定
			result.setAccountDate(result.getKimatuYear(), result.getKimatuMonthDay());
			result.setBusinessTypeName(result.getBusinessType());
			result.setBusinessLineName(result.getBusinessLine());
		});
		return resultList;
	}

	public CompanyData getCompanyByCodeAndKimatuYear(String companyCode, String kimatuYear) {
		CompanyMDto companyMDto = new CompanyMDto();
		companyMDto.setCompanyCode(companyCode);
		companyMDto.setKimatuYear(kimatuYear);
		List<CompanyData> resultList = z1Repo.findCompanyDataList(companyMDto);
		if (resultList == null || resultList.size() > 1) {
			throw new SystemException();
		}
		return resultList.get(0);
	}

	// 会社情報登録
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void insertCompanyData(CompanyMDto companyMDto) {
		z1Repo.insertCompanyData(companyMDto);
		// 会社情報登録
		z1Repo.insertSummaryData(companyMDto.getCompanyCode());
		// 勘定科目マスタ
		z1Repo.insertAccountData(companyMDto.getCompanyCode());
	}

	// 会社コード最大値取得
	public String getMaxCompanyCode() {
		return z1Repo.getMaxCompanyCode();
	}

	// 会社情報変更
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void updateCompanyData(CompanyMDto companyMDto) {
		z1Repo.updateCurrentCompany(companyMDto);
		z1Repo.updateAfterCurrentCompany(companyMDto);
		z1Repo.updateAllCompany(companyMDto);
	}

	/**
	 * 前期繰越データ件数取得
	 */
	public Integer getCarryforwardJournalEntryCount(CompanyDto companyDto) {
		return z1Repo.getCarryforwardJournalEntryCount(companyDto);
	}

	/**
	 * 翌年度の会社マスタが存在するかを確認
	 */
	public Integer getRegistNextYearData(CompanyMDto companyDto) {
		return z1Repo.getRegistNextYearData(companyDto);
	}

}
