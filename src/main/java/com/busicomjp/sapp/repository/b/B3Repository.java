package com.busicomjp.sapp.repository.b;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.b.CashFlowDto;

@Repository
public interface B3Repository {
	public Long selectAllTotalMoney(CashFlowDto selectCondition);
	
	public List<Map<String, Object>> selectTotalMoneyByAccount(CashFlowDto selectCondition);
}
