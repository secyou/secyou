package com.busicomjp.sapp.repository.a;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.a.BalanceDto;
import com.busicomjp.sapp.dto.a.GeneralLedgerDto;
import com.busicomjp.sapp.dto.a.JournalEntryInsertDto;
import com.busicomjp.sapp.model.z.AccountData;

@Repository
public interface A0InputDataRepository {
	
	String getNextJournalNo(String companyCode);
	
	String getNextGeneralNo(String companyCode);
	
	int getTaxRate(String taxCode);
	
	void insertJournalEntryData(JournalEntryInsertDto insertDto);
	
	void insertGeneralLedgerData(GeneralLedgerDto insertDto);
	
	BalanceDto getBalance(GeneralLedgerDto selectDto);
	
	void updateBalance(GeneralLedgerDto updateDto);
	
	GeneralLedgerDto getTaxGeneralLedgerData(GeneralLedgerDto insertDto);
	
	void updateTaxGeneralLedgerData(GeneralLedgerDto updateDto);
	
	// 勘定科目詳細を取得
	AccountData getAccountDetail(String companyCode, String accountCode);
	
	GeneralLedgerDto getCarryForwardData(GeneralLedgerDto selectDto);
	
	void updateCarryForwardGeneralLedgerData(GeneralLedgerDto updateDto);
}
