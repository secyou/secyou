package com.busicomjp.sapp.model.a;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class SettlementData {

	private String accrualDate;
	private String accountName;
	private String counterAccountName;
	private String tekiyoName;
	private String taxName;
	
	private String accountCode;
	private String counterAccountCode;
	private String taxCode;
	
	private final SimpleStringProperty debitAmountMoney;
	private final SimpleStringProperty creditAmountMoney;
	private final SimpleStringProperty balanceMoney;
	
	private boolean debitEditFlg;
	private boolean creditEditFlg;
	private String editItemName;
	
	public SettlementData() {
		this.debitAmountMoney = new SimpleStringProperty();
		this.creditAmountMoney = new SimpleStringProperty();
		this.balanceMoney = new SimpleStringProperty();
	}
	
	public StringProperty debitAmountMoneyProperty() {
		return debitAmountMoney;
	}
	
	public StringProperty creditAmountMoneyProperty() {
		return creditAmountMoney;
	}
	
	public StringProperty balanceMoneyProperty() {
		return balanceMoney;
	}
	
	public String getDebitAmountMoney() {
		return debitAmountMoney.get();
	}
	
	public String getCreditAmountMoney() {
		return creditAmountMoney.get();
	}
	
	public String getBalanceMoney() {
		return balanceMoney.get();
	}
	
	public void setDebitAmountMoney(String value) {
		debitAmountMoney.set(value);
	}
	
	public void setCreditAmountMoney(String value) {
		creditAmountMoney.set(value);
	}
	
	public void setBalanceMoney(String value) {
		balanceMoney.set(value);
	}
}
