package com.busicomjp.sapp.repository.b;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.model.b.AccountInfoData;
import com.busicomjp.sapp.model.b.GeneralLedgerData;

@Repository
public interface B2Repository {

	List<AccountInfoData> getAccountList(String companyCd, String sta_date, String end_date);

	List<GeneralLedgerData> getGeneralLedger(String companyCd, String account_code, String sta_date, String end_date);

}
