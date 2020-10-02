package com.busicomjp.sapp.repository.b;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.b.AccountsReceivableDto;
import com.busicomjp.sapp.model.b.AccountsSummaryData;

@Repository
public interface B6Repository {
	public List<Map<String, Object>> selectCarryForwardMoney(AccountsReceivableDto selectCondition);

	public List<Map<String, Object>> selectLastTradingDate(AccountsReceivableDto selectCondition);

	public List<AccountsSummaryData> selectAccountsUnpaidSummary(AccountsReceivableDto selectCondition);
}
