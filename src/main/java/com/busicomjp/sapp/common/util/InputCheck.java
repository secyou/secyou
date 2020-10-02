package com.busicomjp.sapp.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javafx.scene.control.DatePicker;

public class InputCheck {
	public static boolean isNullOrBlank(Object _obj) {
		if (_obj == null || _obj.toString().trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isVaildDate(String _dateStr) {
		return isVaildDate(_dateStr, "yyyy/MM/dd");
	}

	public static boolean isVaildDate(String _dateStr, String _datePattern) {
		if (isNullOrBlank(_dateStr)) {
			return true;
		}

		if (isNullOrBlank(_datePattern)) {
			_datePattern = "yyyy/MM/dd";
		}

		SimpleDateFormat df = new SimpleDateFormat(_datePattern);
		df.setLenient(false);

		try {
			df.parse(_dateStr);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}
	
	public static boolean isVaildDate(DatePicker _datePicker) {
		if (_datePicker != null) {
			String _text = _datePicker.getEditor().getText();

			return isVaildDate(_text);
		}

		return true;
	}

	public static boolean isNumeric(String input) {
		if (!isNullOrBlank(input)) {
			if (input.startsWith("-")) {
				input = input.substring(1);
			}
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				if (c < '0' || c > '9') {
					return false;
				}
			}
		}

		return true;
	}
	
    public static boolean isBefore(String _date1, String _date2, String _datePattern) {
    	if (!isVaildDate(_date1, _datePattern) || !isVaildDate(_date2, _datePattern)) {
    		return false;
    	}
    	if(_date1.compareTo(_date2) < 0) {
    		// _date1が_date2より小さい場合
    		return true;
    	} else {
    		return false;
    	}
    }

    //全角ひらがな、数字、ハイフン
	public static boolean isZengaku(String input) {
		if (!isNullOrBlank(input)) {
			for (int i = 0; i < input.length(); i++) {
				// 全角ひらがな
				if (String.valueOf(input.charAt(i)).matches("^[\\u3040-\\u309F]+$")) {
					continue;
				}else if(!String.valueOf(input.charAt(i)).matches("[０-９\\ー]+")) {
					return false;
				}
			}
		}
		return true;
	}
}
