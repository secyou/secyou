package com.busicomjp.sapp.service.e;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.e.CarryForwardData;
import com.busicomjp.sapp.repository.d.D1Repository;
import com.busicomjp.sapp.repository.e.E2Repository;

@Service
public class E2Service {

	@Autowired
	E2Repository e2Repo;
	@Autowired
	D1Repository d1Repo;

	public CompanyMDto getCompanyData(String companyCode, String kimatuYear) {
		return e2Repo.getCompanyData(companyCode, kimatuYear);
	}

	public void insertCompanyData(CompanyMDto companyMDto) {
		d1Repo.insertCompanyData(companyMDto);
	}

	/**
     * 勘定科目マスタテーブル_分類1取得
     */
	public List<ComboItem> getAccountKind1DataList(String companyCode) {

		return e2Repo.getAccountKind1DataList(companyCode);

	}

	/**
     * 勘定科目マスタテーブル_勘定科目取得
     */
	public List<CarryForwardData> getAccountCodeDataList(String companyCode, String startDate, String endDate) {

		return e2Repo.getAccountCodeDataList(companyCode, startDate, endDate);
	}
}
