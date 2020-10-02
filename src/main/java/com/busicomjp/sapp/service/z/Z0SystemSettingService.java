package com.busicomjp.sapp.service.z;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.repository.z.Z0SystemSettingRepository;

@Service
public class Z0SystemSettingService {
	
	@Autowired
	Z0SystemSettingRepository z0Repo;
	
	/**
	 * 分類の項目を取得
	 */
	public List<ComboItem> getSelectItems() {
		return z0Repo.getSelectItems();
	}

	/**
	 * 消費税の項目を取得
	 */
	public List<ComboItem> getTaxItems() {
		return z0Repo.getTaxItems();
	}
}
