package com.busicomjp.sapp.common.converter;

import java.util.HashMap;
import java.util.Map;

import com.busicomjp.sapp.common.item.ComboItem;

import javafx.util.StringConverter;

public class ComboItemConverter extends StringConverter<ComboItem> {
	
	private Map<String, ComboItem> comboItemMap = new HashMap<String, ComboItem>();

	@Override
	public String toString(ComboItem comboItem) {
		if (comboItem == null) {
			return null;
		}
		comboItemMap.put(comboItem.getName(), comboItem);
		return comboItem.getName();
	}

	@Override
	public ComboItem fromString(String name) {
		return comboItemMap.get(name);
	}

}
