package com.busicomjp.sapp.service.b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.model.b.AccountInfoData;
import com.busicomjp.sapp.model.b.GeneralLedgerData;
import com.busicomjp.sapp.repository.b.B2Repository;

@Service
public class B2Service {

	@Autowired
	B2Repository b2Repo;

	public List<AccountInfoData> getAccountList(String companyCode) {

		List<AccountInfoData> repoList = b2Repo.getAccountList(companyCode, CompanyUtil.getAccountStartDay(),
				CompanyUtil.getAccountEndDay());

		if (repoList == null || repoList.isEmpty()) {
			return null;
		}else {
			return repoList;
		}
	}

	public List<GeneralLedgerData> getGeneralLedgerInfo(String companyCd,
																						String account_code,
																						String sta_date,
																						String end_date,
																						String taxAppFlg) {

		List<GeneralLedgerData> repoList = null;

//		if("218".equals(account_code)  || "330".equals(account_code)) {
//			if( "1".equals(taxAppFlg)) {
//
//				repoList = b2Repo.getGeneralLedger(companyCd, account_code, sta_date, end_date);
//			}else if ("2".equals(taxAppFlg)){
//
//				 repoList = b2Repo.getGeneralLedgerTaxAppFlg2(companyCd, account_code, sta_date, end_date);
//			}else if ("3".equals(taxAppFlg)) {
//
//				repoList = b2Repo.getGeneralLedgerTaxAppFlg3(companyCd, account_code, sta_date, end_date);
//			}
//
//		}else {
//
//			repoList = b2Repo.getGeneralLedger(companyCd, account_code, sta_date, end_date);
//		}

		repoList = b2Repo.getGeneralLedger(companyCd, account_code, sta_date, end_date);

		if (repoList == null || repoList.isEmpty()) {
			return null;
		}else {
			return repoList;
		}
	}

}
