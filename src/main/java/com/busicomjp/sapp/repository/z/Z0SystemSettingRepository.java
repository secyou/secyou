package com.busicomjp.sapp.repository.z;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.common.item.ComboItem;

@Repository
public interface Z0SystemSettingRepository {

	List<ComboItem> getSelectItems();
	
	List<ComboItem> getTaxItems();
}
