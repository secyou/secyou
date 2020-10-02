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
import com.busicomjp.sapp.common.util.TorihikisakiUtil;
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
public class D2UpdateTorihikisakiDialogController extends BaseController implements Initializable {

	private Logger logger = LoggerFactory.getLogger(D2Controller.class);
	
	@Autowired
	private D2Service z2Service;
	@Autowired
	private D2Controller d2Controller;
	
	@FXML
	private CheckBox checkbox1; 
	@FXML
	private CheckBox checkbox2; 
	@FXML
	private CheckBox checkbox3; 
	@FXML
	private TextField newTorihikisakiName;
	@FXML
	private TextField newTorihikisakiNameKana;
	@FXML
	private ComboBox<ComboItem> supplierCombo;
	@FXML
	private ComboBox<ComboItem> customerCombo;
	@FXML
	private ComboBox<ComboItem> unpaidCombo;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// 各リスト内容を編集
		supplierCombo.setConverter(new ComboItemConverter());
		customerCombo.setConverter(new ComboItemConverter());
		unpaidCombo.setConverter(new ComboItemConverter());

		// 仕入先
		List<ComboItem> supplierResultList = z2Service.supplierDataList(CompanyUtil.getCompanyCode());

		// ComboBoxを設定
		if (supplierResultList != null && supplierResultList.size() > 0) {
			supplierCombo.getItems().clear();
			supplierCombo.getItems().addAll(supplierResultList);
			supplierCombo.getSelectionModel().select(0);
		}

		// 得意先
		List<ComboItem> customerResultList = z2Service.customerDataList(CompanyUtil.getCompanyCode());

		// ComboBoxを設定
		if (customerResultList != null && customerResultList.size() > 0) {
			customerCombo.getItems().clear();
			customerCombo.getItems().addAll(customerResultList);
			customerCombo.getSelectionModel().select(0);
		}

		// 未払先
		List<ComboItem> unpaidResultList = z2Service.unpaidDataList(CompanyUtil.getCompanyCode());

		// ComboBoxを設定
		if (unpaidResultList != null && unpaidResultList.size() > 0) {
			unpaidCombo.getItems().clear();
			unpaidCombo.getItems().addAll(unpaidResultList);
			unpaidCombo.getSelectionModel().select(0);
		}

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

		String code1 = "";
		String code2 = "";
		String code3 = "";
		boolean code1only = false;
		boolean code2only = false;
		boolean code3only = false;
		if (!InputCheck.isNullOrBlank(TorihikisakiUtil.getAccountCode().toString())) {
			if (TorihikisakiUtil.getAccountCode().toString().length() == 3) {
				code1 = TorihikisakiUtil.getAccountCode().toString();
			} else if (TorihikisakiUtil.getAccountCode().toString().length() == 6) {
				code1 = TorihikisakiUtil.getAccountCode().toString().substring(0, 3);
				code2 = TorihikisakiUtil.getAccountCode().toString().substring(3);
			} else if (TorihikisakiUtil.getAccountCode().toString().length() == 9) {
				code1 = TorihikisakiUtil.getAccountCode().toString().substring(0, 3);
				code2 = TorihikisakiUtil.getAccountCode().toString().substring(3, 6);
				code3 = TorihikisakiUtil.getAccountCode().toString().substring(6);
			}
		}

		// 仕入れ先
		if ("〇".equals(TorihikisakiUtil.getSupplierKbn().toString())) {
			checkbox1.setSelected(true);
			if (supplierResultList != null && supplierResultList.size() > 0) {
				for (int i = 0; i < supplierResultList.size(); i++) {
					if (supplierResultList.get(i).getCode().equals(code1)) {
						supplierCombo.getSelectionModel().select(i);
						code1only = true;
						break;
					}
				}
				if (!code1only) {
					for (int i = 0; i < supplierResultList.size(); i++) {
						if (supplierResultList.get(i).getCode().equals(code2)) {
							supplierCombo.getSelectionModel().select(i);
							code1only = true;
							break;
						}
					}
					if (!code1only) {
						for (int i = 0; i < supplierResultList.size(); i++) {
							if (supplierResultList.get(i).getCode().equals(code3)) {
								supplierCombo.getSelectionModel().select(i);
								break;
							}
						}
					}
				}
			}

		} else {
			supplierCombo.setDisable(true);
		}

		// 得意先
		if ("〇".equals(TorihikisakiUtil.getCustomerKbn().toString())) {
			checkbox2.setSelected(true);
			if (customerResultList != null && customerResultList.size() > 0) {
				for (int m = 0; m < customerResultList.size(); m++) {
					if (customerResultList.get(m).getCode().equals(code1)) {
						customerCombo.getSelectionModel().select(m);
						code2only = true;
						break;
					}
				}
				if (!code2only) {
					for (int m = 0; m < customerResultList.size(); m++) {
						if (customerResultList.get(m).getCode().equals(code2)) {
							customerCombo.getSelectionModel().select(m);
							code2only = true;
							break;
						}
					}
					if (!code2only) {
						for (int m = 0; m < customerResultList.size(); m++) {
							if (customerResultList.get(m).getCode().equals(code3)) {
								customerCombo.getSelectionModel().select(m);
								break;
							}
						}
					}
				}
			}
		} else {
			customerCombo.setDisable(true);
		}

		// 未払先
		if ("〇".equals(TorihikisakiUtil.getUnpaidKbn().toString())) {
			checkbox3.setSelected(true);
			if (unpaidResultList != null && unpaidResultList.size() > 0) {
				for (int n = 0; n < unpaidResultList.size(); n++) {
					if (unpaidResultList.get(n).getCode().equals(code1)) {
						unpaidCombo.getSelectionModel().select(n);
						code3only = true;
						break;
					}
				}
				if (!code3only) {
					for (int n = 0; n < unpaidResultList.size(); n++) {
						if (unpaidResultList.get(n).getCode().equals(code2)) {
							unpaidCombo.getSelectionModel().select(n);
							code3only = true;
							break;
						}
					}
					if (!code3only) {
						for (int n = 0; n < unpaidResultList.size(); n++) {
							if (unpaidResultList.get(n).getCode().equals(code3)) {
								unpaidCombo.getSelectionModel().select(n);
								break;
							}
						}
					}
				}
			}
		} else {
			unpaidCombo.setDisable(true);
		}

		// 取引先名
		newTorihikisakiName.setText(TorihikisakiUtil.getTorihikisakiName().toString());
		// 取引先かな
		newTorihikisakiNameKana.setText(TorihikisakiUtil.getTorihikisakiNameKana().toString());
	}
	
	@FXML
	private void onUpdateTorihikisaki(Event eve) {
		if (commonAlert.showConfirmationAlert("変更処理を実行しますか？")) {
			logger.info("取引先更新 開始");
			// 入力チェック
			inputCheck();

			// 仕入れ先
			if ("〇".equals(TorihikisakiUtil.getSupplierKbn())) {
				// チェックボックス外し場合、該当するレコードのみ削除
				if (!checkbox1.isSelected()) {
					z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
							TorihikisakiUtil.getTorihikisakiCode().toString(), supplierCombo.getValue().getCode(), "2",
							"1");
				} else if (checkbox1.isSelected()) {
					// 紐づけ勘定科目（仕入れ先）変更された場合、該当するレコードのみに反映する
					if (TorihikisakiUtil.getAccountCode().indexOf(supplierCombo.getValue().getCode()) < 0) {
						z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString(), supplierCombo.getValue().getCode(),
								"2", "0");
					}
				}
				// 仕入れ先CheckBox新規選択された場合、情報登録
			} else {
				if (checkbox1.isSelected()) {

					// DBにあるか
					String existCount1 = z2Service.existJudgeSelect(CompanyUtil.getCompanyCode(),
							TorihikisakiUtil.getTorihikisakiCode(), "2");
					// 存在しなければ
					if ("0".equals(existCount1)) {

						z2Service.insertTorihikisakiData(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString().trim(),
								newTorihikisakiName.getText().trim(), newTorihikisakiNameKana.getText().trim(),
								supplierCombo.getValue().getCode(), "2");
					} else {
						z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString().trim(),
								supplierCombo.getValue().getCode(), "2", "0");
					}
				}
			}

			// 得意先
			if ("〇".equals(TorihikisakiUtil.getCustomerKbn())) {
				// チェックボックス外し場合、該当するレコードのみ削除
				if (!checkbox2.isSelected()) {
					z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
							TorihikisakiUtil.getTorihikisakiCode().toString(), customerCombo.getValue().getCode(), "1",
							"1");
				} else if (checkbox2.isSelected()) {
					// 紐づけ勘定科目（得意先）変更された場合、該当するレコードのみに反映する
					if (TorihikisakiUtil.getAccountCode().indexOf(customerCombo.getValue().getCode()) < 0) {
						z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString(), customerCombo.getValue().getCode(),
								"1", "0");
					}
				}
				// 得意先CheckBox新規選択された場合、情報登録
			} else {
				if (checkbox2.isSelected()) {

					// DBにあるか
					String existCount2 = z2Service.existJudgeSelect(CompanyUtil.getCompanyCode(),
							TorihikisakiUtil.getTorihikisakiCode(), "1");
					// 存在しなければ
					if ("0".equals(existCount2)) {

						z2Service.insertTorihikisakiData(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString().trim(),
								newTorihikisakiName.getText().trim(), newTorihikisakiNameKana.getText().trim(),
								customerCombo.getValue().getCode(), "1");
					} else {
						z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString().trim(),
								customerCombo.getValue().getCode(), "1", "0");
					}
				}
			}

			// 未払先
			if ("〇".equals(TorihikisakiUtil.getUnpaidKbn())) {
				// チェックボックス外し場合、該当するレコードのみ削除
				if (!checkbox3.isSelected()) {
					z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
							TorihikisakiUtil.getTorihikisakiCode().toString(), unpaidCombo.getValue().getCode(), "3",
							"1");
				} else if (checkbox3.isSelected()) {
					// 紐づけ勘定科目（未払先）変更された場合、該当するレコードのみに反映する
					if (TorihikisakiUtil.getAccountCode().indexOf(unpaidCombo.getValue().getCode()) < 0) {
						z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString(), unpaidCombo.getValue().getCode(),
								"3", "0");
					}
				}
				// 未払先CheckBox新規選択された場合、情報登録
			} else {
				if (checkbox3.isSelected()) {

					// DBにあるか
					String existCount3 = z2Service.existJudgeSelect(CompanyUtil.getCompanyCode(),
							TorihikisakiUtil.getTorihikisakiCode(), "3");
					// 存在しなければ
					if ("0".equals(existCount3)) {

						z2Service.insertTorihikisakiData(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString().trim(),
								newTorihikisakiName.getText().trim(), newTorihikisakiNameKana.getText().trim(),
								unpaidCombo.getValue().getCode(), "3");
					} else {
						z2Service.updateTorihikisakiDataOnly(CompanyUtil.getCompanyCode(),
								TorihikisakiUtil.getTorihikisakiCode().toString().trim(),
								unpaidCombo.getValue().getCode(), "3", "0");
					}
				}
			}

			// 取引先名称、カナが更新された場合、同じ取引先コードのすべてのデータに反映する
			if (!(TorihikisakiUtil.getTorihikisakiName().trim().equals(newTorihikisakiName.getText().trim()))
					|| !(TorihikisakiUtil.getTorihikisakiNameKana().trim()
							.equals(newTorihikisakiNameKana.getText().trim()))) {
				z2Service.updateTorihikisakiDataMultiple(CompanyUtil.getCompanyCode(),
						TorihikisakiUtil.getTorihikisakiCode().toString(), newTorihikisakiName.getText().trim(),
						newTorihikisakiNameKana.getText().trim());
			}

			((Node) eve.getSource()).getScene().getWindow().hide();
			// 再表示
			d2Controller.onSearchData();
			logger.info("取引先更新 終了");
		}
	}

	@FXML
	private void inputCheck() {
		// 取引先名称
		if (InputCheck.isNullOrBlank(newTorihikisakiName.getText())) {
			throw new ValidationException("取引先名称を指定してください。");
		}
		if (newTorihikisakiName.getText().trim().length() > 100) {
			throw new ValidationException("取引先名称は100文字以内で指定してください。");
		}

		// 取引先名称かな
		if (InputCheck.isNullOrBlank(newTorihikisakiNameKana.getText())) {
			throw new ValidationException("取引先名称(かな)を指定してください。");
		}
		if (newTorihikisakiNameKana.getText().trim().length() > 100) {
			throw new ValidationException("取引先名称(かな)は100文字以内で指定してください。");
		}

		// 全角
		if(!InputCheck.isZengaku(newTorihikisakiNameKana.getText().trim())) {
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
}
