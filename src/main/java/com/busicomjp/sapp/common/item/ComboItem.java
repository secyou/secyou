package com.busicomjp.sapp.common.item;

import lombok.Data;

@Data
public class ComboItem {
	
	private String code;
	private String name;
	private String option;
	
	public ComboItem(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public ComboItem(String code, String name, String option) {
		this.code = code;
		this.name = name;
		this.option = option;
	}

}
