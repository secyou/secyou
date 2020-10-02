package com.busicomjp.sapp.common.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class DatePickerConverter extends StringConverter<LocalDate> {
	
	private final String CONST_DATE_PATTERN = "yyyy/MM/dd";
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CONST_DATE_PATTERN);
	
	@Override
	public String toString(LocalDate date) {
		if (date != null) {
			return dateFormatter.format(date);
		} else {
			return "";
		}
	}

	@Override
	public LocalDate fromString(String string) {
		if (string != null && !string.isEmpty()) {
			return LocalDate.parse(string, dateFormatter);
		} else {
			return null;
		}
	}
}
