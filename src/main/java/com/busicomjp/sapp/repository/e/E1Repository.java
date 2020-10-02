package com.busicomjp.sapp.repository.e;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.dto.a.BalanceDto;
import com.busicomjp.sapp.model.e.CarryForwardData;
import com.busicomjp.sapp.model.e.CarryForwardSubData;

@Repository
public interface E1Repository {

	List<ComboItem> getAccountKind1DataList(String companyCode);
	List<ComboItem> getAccountKind2DataList(String companyCode, String selectCode);
	List<ComboItem> getAccountKind3DataList(String companyCode, String selectCode, String selectCode2);
	List<ComboItem> getAccountKind4DataList(String companyCode, String selectCode, String selectCode2, String selectCode3);
	List<CarryForwardData> getAccountCodeDataList(String companyCode, String startDate, String endDate);
	void updateJournalData(String amountMoney, String companyCode, String startDate, String creditAccount, String debitAccount, String suppliersCode);
	void updateGeneralData(String amountMoney, String companyCode, String startDate,String creditAccount, String debitAccount);
	String selectJournalData(String companyCode, String startDate, String creditAccount, String debitAccount,String suppliersCode);
	BalanceDto selectGeneralBalanceMoney(String companyCode, String startDate, String accountCode);
	List<CarryForwardSubData> selectTorihikiData(String companyCode, String startDate, String accountCode);

}
