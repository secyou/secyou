package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.d.AccountSeletedData;
import com.busicomjp.sapp.service.d.D3Service;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

@Component
public class D3UpdateAccountDialogController extends BaseController implements Initializable {

	@Autowired
	private D3Controller d3Controller;
	@Autowired
	D3Service d3Service;
	
	@FXML
	private Label accountKind;
	@FXML
	private Label accountName;
	@FXML
	private TextField inputAccountName;
	@FXML
	private TextField inputAccountNameKana;
	@FXML
	private RadioButton useKbnRadio1;
	@FXML
	private RadioButton useKbnRadio2;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountKind.setText(AccountSeletedData.getKindGroup());
		accountName.setText(AccountSeletedData.getKindFlg());
		inputAccountName.setText(AccountSeletedData.getAccountName());
		inputAccountNameKana.setText(AccountSeletedData.getAccountNameKana());
		if ("〇".equals(AccountSeletedData.getUseFlg())) {
			useKbnRadio1.setSelected(true);
		}else {
			useKbnRadio2.setSelected(true);
		}
	}
	
	@FXML
	private void onUpdateAccount(Event eve) {
		if (commonAlert.showConfirmationAlert("変更処理を実行しますか？")) {
			String selectedUseKbn = "0";
			String selectedUseKbnFlg = "";

			// 入力チェック
			inputCheck();

			if (useKbnRadio1.isSelected()) {
				selectedUseKbn = "1";
				selectedUseKbnFlg = "〇";
			}

			// 何も変更しない
			if (inputAccountName.getText().trim().equals(AccountSeletedData.getAccountName())
					&& inputAccountNameKana.getText().trim().equals(AccountSeletedData.getAccountNameKana())
					&& selectedUseKbnFlg.equals(AccountSeletedData.getUseFlg())) {
				throw new ValidationException("変更前後の項目値が同じです。");
			} else {
				d3Service.updateAccount(CompanyUtil.getCompanyCode(), AccountSeletedData.getAccountCode(),
						inputAccountName.getText().trim(), inputAccountNameKana.getText().trim(), selectedUseKbn);
			}

			((Node) eve.getSource()).getScene().getWindow().hide();
			// 再表示
			d3Controller.onSearchData();
		}
	}

	private void inputCheck() {
		// 勘定科目名称
		if (InputCheck.isNullOrBlank(inputAccountName.getText().trim())) {
			throw new ValidationException("勘定科目名称を指定してください。");
		}
		if (inputAccountName.getText().trim().length() > 100) {
			throw new ValidationException("勘定科目名称は100文字以内で指定してください。");
		}

		// 勘定科目名称かな
		if (InputCheck.isNullOrBlank(inputAccountNameKana.getText().trim())) {
			throw new ValidationException("勘定科目名称(かな)を指定してください。");
		}
		if (inputAccountNameKana.getText().trim().length() > 100) {
			throw new ValidationException("勘定科目名称(かな)は100文字以内で指定してください。");
		}

		// 全角
		if(!InputCheck.isZengaku(inputAccountNameKana.getText().trim())) {
				throw new ValidationException("勘定科目名称(かな)は全角ひらがな、全角数字で指定してください。");
		}
	}
}
