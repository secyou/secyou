package com.busicomjp.sapp.common.converter;

import org.apache.commons.lang3.StringUtils;

import javafx.util.StringConverter;

public class MaxLengthConverter extends StringConverter<String> {
	
	private int maxLength;
	
	public MaxLengthConverter(int length) {
		if (length > 0) {
			maxLength = length;
		} else {
			maxLength = 0;
		}
	}

	@Override
	public String toString(String object) {
		return object;
	}

	@Override
	public String fromString(String string) {
		if (StringUtils.isEmpty(string)) {
			return string;
		}
		if (string.length() > maxLength) {
			return string.substring(0, maxLength);
		}
		return string;
	}

}
