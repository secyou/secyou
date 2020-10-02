package com.busicomjp.sapp.controller.e;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.controller.FeatureGroupController;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.d.CompanyData;
import com.busicomjp.sapp.service.e.E2Service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

@Component
public class E2RegistNextTermInfoDialogController extends BaseController implements Initializable {

	@Autowired
	private FeatureGroupController featureController;
	@Autowired
	private E2Service e2Service;
	@FXML
	private Label companyName;
	@FXML
	private Label businessType;
	@FXML
	private Label businessLine;
	@FXML
	private Label settlPeriod;
	@FXML
	private Label kisyu;
	@FXML
	private Label kimatu;
	@FXML
	private RadioButton taxKbnRadio1;
	@FXML
	private RadioButton taxKbnRadio2;
	@FXML
	private RadioButton taxKbnRadio3;
	@FXML
	private RadioButton taxSummary1;
	@FXML
	private RadioButton taxSummary2;
	@FXML
	private RadioButton taxSummary3;
	@FXML
	private ToggleGroup taxKbnGroup;

	private Integer period;
	private Integer kisyuY;
	private Integer kimatuY;
	private CompanyMDto companyData;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 消費税事業区分
		taxKbnGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (taxKbnGroup.getSelectedToggle() != null) {
					if ("3".equals(taxKbnGroup.getSelectedToggle().getUserData())) {
						taxSummary1.setDisable(true);
						taxSummary1.setSelected(false);
						taxSummary2.setDisable(true);
						taxSummary2.setSelected(false);
						taxSummary3.setDisable(true);
						taxSummary3.setSelected(false);
					} else {
						taxSummary1.setDisable(false);
						taxSummary1.setSelected(true);
						taxSummary2.setDisable(false);
						taxSummary2.setSelected(false);
						taxSummary3.setDisable(false);
						taxSummary3.setSelected(false);
					}
				}
			}
		});
		init();
	}

	public void init() {
		// 画面表示項目設定
		// 会社コード、期末年により会社情報取得
		companyData = new CompanyMDto();
		companyData = e2Service.getCompanyData(CompanyUtil.getCompanyCode(), CompanyUtil.getKimatuYear());

		// 会社名
		companyName.setText(companyData.getCompanyName());
		// 事業タイプ
		if ("1".equals(companyData.getBusinessType())) {
			businessType.setText("法人");
		} else if ("2".equals(companyData.getBusinessType())) {
			businessType.setText("個人");
		} else {
			businessType.setText("");
		}
		// 事業種目
		if ("1".equals(companyData.getBusinessLine())) {
			businessLine.setText("一般事業");
		} else if ("2".equals(companyData.getBusinessLine())) {
			businessLine.setText("不動産事業");
		} else {
			businessLine.setText("");
		}

		// 決算期
		if (!InputCheck.isNullOrBlank(companyData.getSettlPeriod())) {
			period = Integer.parseInt(companyData.getSettlPeriod()) + 1;
			settlPeriod.setText("第".concat(period.toString()).concat("期"));
		} else {
			throw new ValidationException("前年度データがありません");
		}
		// 事業年度期首
		if (!InputCheck.isNullOrBlank(companyData.getKisyuYearMonth())) {
			kisyuY = Integer.parseInt(companyData.getKisyuYearMonth().substring(0, 4)) + 1;
			String kisyuM = companyData.getKisyuYearMonth().substring(4);
			kisyu.setText(kisyuY.toString().concat("年").concat(kisyuM).concat("月"));
		} else {
			throw new ValidationException("前年度データがありません");
		}

		// 事業年度期末
		if (!InputCheck.isNullOrBlank(companyData.getKimatuYearMonth())) {
			kimatuY = Integer.parseInt(companyData.getKimatuYearMonth().substring(0, 4)) + 1;
			String kimatuM = companyData.getKimatuYearMonth().substring(4);
			kimatu.setText(kimatuY.toString().concat("年").concat(kimatuM).concat("月"));
		} else {
			throw new ValidationException("前年度データが存在しません。");
		}
	}
	
	@FXML
	private void onRegistNextTerm(Event eve) {
		if (commonAlert.showConfirmationAlert("登録処理を実行しますか？")) {
			if (InputCheck.isNullOrBlank(period) || InputCheck.isNullOrBlank(kisyuY)
					|| InputCheck.isNullOrBlank(kimatuY)) {
				throw new ValidationException("前年度データが存在しません。");
			}

			if (!taxKbnRadio1.isSelected() && !taxKbnRadio2.isSelected() && !taxKbnRadio3.isSelected()) {
				throw new ValidationException("消費税事業区分を選択してください。");
			}

			CompanyMDto inputData = companyData;

			if (taxKbnRadio1.isSelected() || taxKbnRadio2.isSelected()) {
				// 原則課税または簡易課税
				inputData.setTaxKindFlg("1");
			} else {
				// 免税
				inputData.setTaxKindFlg("0");
			}

			// 消費税集計区分
			if (taxSummary1.isSelected()) {
				// 1:都度集計
				inputData.setTaxAppFlg("1");
			} else if (taxSummary2.isSelected()) {
				// 2:月単位集計
				inputData.setTaxAppFlg("2");
			} else if (taxSummary3.isSelected()) {
				// 3:年単位集計
				inputData.setTaxAppFlg("3");
			} else {
				inputData.setTaxAppFlg("");
			}

			Integer kimatuYear = Integer.parseInt(companyData.getKimatuYear()) + 1;
			inputData.setKimatuYear(kimatuYear.toString());
			inputData.setSettlPeriod(period.toString());
			Integer kisyu_Year = Integer.parseInt(companyData.getKisyuYearMonth().substring(0, 4)) + 1;
			String kisyuYearMonth = kisyu_Year.toString().concat(companyData.getKisyuYearMonth().substring(4));
			inputData.setKimatuYearMonth(kisyuYearMonth);
			Integer kimatu_Year = Integer.parseInt(companyData.getKimatuYearMonth().substring(0, 4)) + 1;
			String kimatuYearMonth = kimatu_Year.toString().concat(companyData.getKimatuYearMonth().substring(4));
			inputData.setKisyuYearMonth(kimatuYearMonth);
			e2Service.insertCompanyData(inputData);

			CompanyData selectData = new CompanyData();
			selectData.setCompanyCode(inputData.getCompanyCode());
			selectData.setCompanyName(inputData.getCompanyName());
			selectData.setKimatuYear(inputData.getKimatuYear());
			selectData.setKimatuMonthDay(inputData.getKimatuMonthDay());
			selectData.setTaxKindFlg(inputData.getTaxKindFlg());
			selectData.setTaxAppFlg(inputData.getTaxAppFlg());
			selectData.setBusinessType(inputData.getBusinessType());
			selectData.setBusinessLine(inputData.getBusinessLine());
			selectData.setAccountDate(inputData.getKimatuYear(), inputData.getKimatuMonthDay());

			CompanyUtil.setCompanyUtil(selectData);

			featureController.setGroupFTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getE2());

			// ダイアログを閉じる
			((Node) eve.getSource()).getScene().getWindow().hide();
		}
	}
}
