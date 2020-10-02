package com.busicomjp.sapp.repository.a;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.dto.a.GeneralLedgerDto;
import com.busicomjp.sapp.dto.a.InputDataDto;
import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.model.a.BalanceData;
import com.busicomjp.sapp.model.a.JournalEntryData;

@Repository
public interface A1Repository {
	
	List<BalanceData> getRealBalanceDataList(CompanyDto companyDto);
	
	long getExpectBalanceData(String companyCode, String startDay, String endDay);
	
	List<JournalEntryData> getJournalEntryDataList(CompanyDto companyDto);

	List<ComboItem> getSelectedAccountList(InputDataDto selectDto);
	
	List<GeneralLedgerDto> getGeneralLedgerDataList(GeneralLedgerDto selectDto);
	
	void deleteJournalEntryData(JournalEntryDto deleteDto);
	
	void deleteGeneralLedgerData(GeneralLedgerDto deleteDto);
}
