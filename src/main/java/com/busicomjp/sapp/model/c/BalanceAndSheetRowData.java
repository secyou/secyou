package com.busicomjp.sapp.model.c;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class BalanceAndSheetRowData {

	private String kindName1;
	private String kindName2;
	private String kindName3;
	private String amount;
}
