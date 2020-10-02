package com.busicomjp.sapp.controller.a;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.controller.BaseController;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import lombok.Getter;

@Component
public class A1AssistanceDialogController extends BaseController implements Initializable  {

	static final Logger log = LoggerFactory.getLogger(A1AssistanceDialogController.class);

	@Getter
	private String inputBillMaturityDate;
	
	@FXML
	private TextField billMaturityDate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		inputBillMaturityDate = ""; // 初期化
	}

	@FXML
	private void onInputData(Event eve) {
		String date = billMaturityDate.getText();
		if (StringUtils.isEmpty(date)) {
			throw new ValidationException("手形満期日を入力してください。");
		}
		
		if (!InputCheck.isVaildDate(date, "yyyyMMdd")) {
			throw new ValidationException("手形満期日はYYYYMMDD形式の日付を入力してください。");
		}
		inputBillMaturityDate = date;
		// ダイアログを閉じる
		((Node) eve.getSource()).getScene().getWindow().hide();
	}

}
