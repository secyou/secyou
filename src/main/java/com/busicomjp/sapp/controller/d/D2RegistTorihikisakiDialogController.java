package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.service.d.D2Service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

@Component
public class D2RegistTorihikisakiDialogController extends BaseController implements Initializable {

	private Logger logger = LoggerFactory.getLogger(D2Controller.class);
	
	@Autowired
	private D2Controller d2Controller;
	@Autowired
	public D2Service d2Service;

	@FXML
	public CheckBox checkbox1;
	@FXML
	public CheckBox checkbox2;
	@FXML
	public CheckBox checkbox3;
	@FXML
	public ComboBox<ComboItem> supplierCombo;
	@FXML
	public ComboBox<ComboItem> customerCombo;
	@FXML
	public ComboBox<ComboItem> unpaidCombo;
	@FXML
	public TextField inputTorihikisakiName;
	@FXML
	public TextField inputTorihikisakiNameKana;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 初期化非活性
		supplierCombo.setDisable(true);
		customerCombo.setDisable(true);
		unpaidCombo.setDisable(true);

		// 各リスト内容を編集
		supplierCombo.setConverter(new ComboItemConverter());
		customerCombo.setConverter(new ComboItemConverter());
		unpaidCombo.setConverter(new ComboItemConverter());

		checkbox1.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (checkbox1.isSelected()) {
					supplierCombo.setDisable(false);
				} else if (!checkbox1.isSelected()) {
					supplierCombo.setDisable(true);
				}
			}
		});

		checkbox2.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (checkbox2.isSelected()) {
					customerCombo.setDisable(false);
				} else if (!checkbox2.isSelected()) {
					customerCombo.setDisable(true);
				}
			}
		});

		checkbox3.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (checkbox3.isSelected()) {
					unpaidCombo.setDisable(false);
				} else if (!checkbox3.isSelected()) {
					unpaidCombo.setDisable(true);
				}
			}
		});

		// ComboBox初期化
		String companyCode = CompanyUtil.getCompanyCode();
		// 仕入先
		List<ComboItem> supplierResultList = d2Service.supplierDataList(companyCode);
		// ComboBoxを設定
		if (supplierResultList != null && supplierResultList.size() > 0) {
			supplierCombo.getItems().clear();
			supplierCombo.getItems().addAll(supplierResultList);
			supplierCombo.getSelectionModel().select(0);
		}

		// 得意先
		List<ComboItem> customerResultList = d2Service.customerDataList(companyCode);
		// ComboBoxを設定
		if (customerResultList != null && customerResultList.size() > 0) {
			customerCombo.getItems().clear();
			customerCombo.getItems().addAll(customerResultList);
			customerCombo.getSelectionModel().select(0);
		}

		// 未払先
		List<ComboItem> unpaidResultList = d2Service.unpaidDataList(companyCode);
		// ComboBoxを設定
		if (unpaidResultList != null && unpaidResultList.size() > 0) {
			unpaidCombo.getItems().clear();
			unpaidCombo.getItems().addAll(unpaidResultList);
			unpaidCombo.getSelectionModel().select(0);
		}
	}
	
	@FXML
	private void onRegistTorihikisaki(Event eve) {	
		if (commonAlert.showConfirmationAlert("登録処理を実行しますか？")) {
			logger.info("取引先新規登録 開始");
			// 取引先登録プロセス
			this.registTorihikisakiProcess();
			// ダイアログを閉じる
	        ((Node) eve.getSource()).getScene().getWindow().hide();
	        // 再表示
	        d2Controller.onSearchData();
	        logger.info("取引先新規登録 終了");
		}
	}
	
	private void registTorihikisakiProcess() {
		// 入力チェック
		inputCheck();
		
		String companyCode = CompanyUtil.getCompanyCode();
		// 既存Max取引先コード
		String maxTorihikisakiCode = d2Service.getMaxTorihikisakiCd();
		int maxCode = Integer.parseInt(maxTorihikisakiCode) + 1;
		String maxCd = String.format("%05d", maxCode);

		// 取引先の新規登録
		// 仕入先のみ場合
		boolean checkedFlg = false;
		if (checkbox1.isSelected()) {
			// 仕入先
			torihikisakiInsert(companyCode, maxCd, supplierCombo.getValue().getCode(), "2");
			checkedFlg = true;
		}
		if (checkbox2.isSelected()) {
			// 得意先
			torihikisakiInsert(companyCode, maxCd, customerCombo.getValue().getCode(), "1");
			checkedFlg = true;
		}
		if (checkbox3.isSelected()) {
			// 未払先
			torihikisakiInsert(companyCode, maxCd, unpaidCombo.getValue().getCode(), "3");
			checkedFlg = true;
		}
		if (!checkedFlg) {
			// いずれも選択なしの場合
			torihikisakiInsert(companyCode, maxCd, "", "0");
		}
		// 入力データをクリア
		clearData();
		
	}

	private void inputCheck() {
		// 取引先名称
		if (InputCheck.isNullOrBlank(inputTorihikisakiName.getText())) {
			throw new ValidationException("取引先名称を指定してください。");
		}
		if (inputTorihikisakiName.getText().trim().length() > 100) {
			throw new ValidationException("取引先名称は100文字以内で指定してください。");
		}

		// 取引先名称かな
		if (InputCheck.isNullOrBlank(inputTorihikisakiNameKana.getText())) {
			throw new ValidationException("取引先名称(かな)を指定してください。");
		}
		if (inputTorihikisakiNameKana.getText().trim().length() > 100) {
			throw new ValidationException("取引先名称(かな)は100文字以内で指定してください。");
		}

		// 全角
		if(!InputCheck.isZengaku(inputTorihikisakiNameKana.getText().trim())) {
				throw new ValidationException("取引先名称(かな)は全角ひらがな、全角数字で指定してください。");
		}

		// 仕入先
		if (checkbox1.isSelected()) {
			if (InputCheck.isNullOrBlank(supplierCombo.getValue())) {
				throw new ValidationException("紐づけ勘定科目(仕入先)を指定してください。");
			}
		}

		// 得意先
		if (checkbox2.isSelected()) {
			if (InputCheck.isNullOrBlank(customerCombo.getValue())) {
				throw new ValidationException("紐づけ勘定科目(得意先)を指定してください。");
			}
		}

		// 未払先
		if (checkbox3.isSelected()) {
			if (InputCheck.isNullOrBlank(unpaidCombo.getValue())) {
				throw new ValidationException("紐づけ勘定科目(未払先)を指定してください。");
			}
		}
	}
	
	// 取引先マスター登録
	private void torihikisakiInsert(String companyCode, String torihikisakiMax, String accountCode, String torihikisakiType) {
		d2Service.insertTorihikisakiData(companyCode, torihikisakiMax, inputTorihikisakiName.getText(),
				inputTorihikisakiNameKana.getText(), accountCode, torihikisakiType);
	}
	
	private void clearData() {
		inputTorihikisakiName.setText("");
		inputTorihikisakiNameKana.setText("");

		supplierCombo.getSelectionModel().select(0);
		customerCombo.getSelectionModel().select(0);
		unpaidCombo.getSelectionModel().select(0);
		
		checkbox1.setSelected(false);
		checkbox2.setSelected(false);
		checkbox3.setSelected(false);
	}

}
