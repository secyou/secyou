package com.busicomjp.sapp.repository.b;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.model.b.BalanceData;

@Repository
public interface B1Repository {
	
	List<BalanceData> getBalanceDataList(CompanyDto companyDto);
	
	List<BalanceData> getBalanceDetailDataList(CompanyDto companyDto);
	
	String getMaxAccrualDate(CompanyDto companyDto);
}
