package com.busicomjp.sapp.common.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.service.z.Z0SystemSettingService;

import lombok.Getter;

@Component
public class CommonItems {
	
	@Autowired
	Z0SystemSettingService z0Service;
	
	@Getter
	List<ComboItem> defaultItemList = new ArrayList<ComboItem>();
	
	@Getter
	List<ComboItem> selectItemList = new ArrayList<ComboItem>();
	
	@Getter
	List<ComboItem> taxItemList = new ArrayList<ComboItem>();
	
	public void initItems() {
		
		defaultItemList.add(new ComboItem("", ""));
		
		selectItemList.addAll(z0Service.getSelectItems());
		taxItemList.addAll(z0Service.getTaxItems());
	}
	
	

}
