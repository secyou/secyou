package com.busicomjp.sapp.repository.e;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.e.CarryForwardData;

@Repository
public interface E2Repository {

	CompanyMDto getCompanyData(String companyCode, String kimatuYear);
	List<ComboItem> getAccountKind1DataList(String companyCode);
	List<CarryForwardData> getAccountCodeDataList(String companyCode, String startDate, String endDate);
}
