package com.busicomjp.sapp.controller.e;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.e.CarryForwardData;
import com.busicomjp.sapp.service.e.E1Service;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@Component
public class E1RegistCarryForwardDialogController extends BaseController implements Initializable {

	@FXML
	private Label accountName;
	@FXML
	private Label accountKind;
	@FXML
	private  TextField newDebitAmountMoney;
	@FXML
	private  TextField newCreditAmountMoney;
	@Autowired
	private E1Service e1Service;

	private String accountCode;
	private String kindFlg;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void onRegistCarryForward(Event eve) {
		if ((!"0".equals(newCreditAmountMoney.getText())) && !"0".equals(newDebitAmountMoney.getText())) {
			throw new ValidationException("借方金額か借方金額の片方のみ入力してください");
		}

		e1Service.registCarryForward(accountCode, kindFlg, newDebitAmountMoney.getText(),
				newCreditAmountMoney.getText());

		((Node) eve.getSource()).getScene().getWindow().hide();
	}

	public void init(CarryForwardData selectedData) {

		if (selectedData != null) {
			accountName.setText(selectedData.getAccountName());
			newDebitAmountMoney.setText(selectedData.getDebitAmountMoney());
			newCreditAmountMoney.setText(selectedData.getCreditAmountMoney());
			String account_kind = selectedData.getAccountKind1Name().concat("→").concat(selectedData.getAccountKind2Name())
					.concat("→").concat(selectedData.getAccountKind3Name()).concat("→").concat(selectedData.getAccountKind4Name());
			accountKind.setText(account_kind);

			accountCode = selectedData.getAccountCode();
			kindFlg = selectedData.getKindFlg();

			if(CommonConstants.DEBIT_CREDIT_FLG.DEBIT.equals(kindFlg)) {
				newCreditAmountMoney.setText("0");
				newCreditAmountMoney.setDisable(true);
			}else {
				newDebitAmountMoney.setText("0");
				newDebitAmountMoney.setDisable(true);
			}
		}

	}

	@FXML
	private void onCreditCheck() {
		if(newCreditAmountMoney.getText().isBlank()) {
			newCreditAmountMoney.setText("0");
			}
	}

	@FXML
	private void onDebitCheck() {
		if(newDebitAmountMoney.getText().isBlank()) {
			newDebitAmountMoney.setText("0");
			}
	}

}
