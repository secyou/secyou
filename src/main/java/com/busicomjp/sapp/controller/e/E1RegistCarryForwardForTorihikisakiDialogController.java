package com.busicomjp.sapp.controller.e;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.e.CarryForwardData;
import com.busicomjp.sapp.model.e.CarryForwardSubData;
import com.busicomjp.sapp.service.e.E1Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

@Component
public class E1RegistCarryForwardForTorihikisakiDialogController extends BaseController implements Initializable {

	@FXML
	private TableView<CarryForwardSubData> carryforwardDataTable;
	@FXML
	private TableColumn<CarryForwardSubData,String> torihikisakiName;
	@FXML
	private TableColumn<CarryForwardSubData,String> debitAmountMoney;
	@FXML
	private TableColumn<CarryForwardSubData,String> creditAmountMoney;
	@FXML
	private Label accountKind;
	@FXML
	private Label accountName;
	@FXML
	private Label newDebitAmountMoney;
	@FXML
	private Label newCreditAmountMoney;
	@Autowired
	private E1Service e1Service;

	private String accountCode;
	private String kindFlg;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		torihikisakiName.setCellValueFactory(new PropertyValueFactory<>("torihikisakiName"));
		debitAmountMoney.setCellValueFactory(new PropertyValueFactory<>("debitAmountMoney"));
		creditAmountMoney.setCellValueFactory(new PropertyValueFactory<>("creditAmountMoney"));

		debitAmountMoney.setCellFactory(TextFieldTableCell.forTableColumn());

		debitAmountMoney.setOnEditCommit((TableColumn.CellEditEvent<CarryForwardSubData, String> t) -> {
			CarryForwardSubData editedData = t.getTableView().getItems().get(t.getTablePosition().getRow());
			String newValue = StringUtil.replaceFull2HalfNumber(t.getNewValue());
			if (StringUtils.isEmpty(newValue)) {
				throw new ValidationException("金額を入力してください。");
			}
			if (!InputCheck.isNumeric(newValue)) {
				throw new ValidationException("金額は整数で入力してください。");
			}
			editedData.setDebitAmountMoney(newValue);
		});
		creditAmountMoney.setCellFactory(TextFieldTableCell.forTableColumn());
		creditAmountMoney.setOnEditCommit((TableColumn.CellEditEvent<CarryForwardSubData, String> t) -> {
			CarryForwardSubData editedData = t.getTableView().getItems().get(t.getTablePosition().getRow());
			String newValue = StringUtil.replaceFull2HalfNumber(t.getNewValue());
			if (StringUtils.isEmpty(newValue)) {
				throw new ValidationException("金額を入力してください。");
			}
			if (!InputCheck.isNumeric(newValue)) {
				throw new ValidationException("金額は整数で入力してください。");
			}
			editedData.setCreditAmountMoney(newValue);
		});

		torihikisakiName.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.4).subtract(3.0));
		debitAmountMoney.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.3).subtract(3.0));
		creditAmountMoney.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.3).subtract(3.0));

		torihikisakiName.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.4).subtract(3.0));
		debitAmountMoney.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.3).subtract(3.0));
		creditAmountMoney.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.3).subtract(3.0));
	}


	public void init(CarryForwardData selectedData) {

		if (selectedData != null) {
			accountName.setText(selectedData.getAccountName());
			String account_kind = selectedData.getAccountKind1Name().concat("→").concat(selectedData.getAccountKind2Name())
					.concat("→").concat(selectedData.getAccountKind3Name()).concat("→").concat(selectedData.getAccountKind4Name());
			accountKind.setText(account_kind);

			accountCode = selectedData.getAccountCode();
			kindFlg = selectedData.getKindFlg();

			//取引先一覧取得
			carryforwardDataTable.getItems().clear();
			List<CarryForwardSubData> dataList = e1Service.selectTorihikiData(CompanyUtil.getCompanyCode(),
																													CompanyUtil.getAccountStartDay(),
																													selectedData.getAccountCode());

			final ObservableList<CarryForwardSubData> observableList = FXCollections.observableArrayList(dataList);
			carryforwardDataTable.getItems().addAll(observableList);

			long sumCreditAmountMoney = 0;
			long sumDebitAmountMoney = 0;

			if(carryforwardDataTable.getItems().size() > 0) {

				for(CarryForwardSubData sub : carryforwardDataTable.getItems()) {
					sumCreditAmountMoney = sumCreditAmountMoney + Integer.parseInt(sub.getCreditAmountMoney());
					sumDebitAmountMoney = sumDebitAmountMoney + Integer.parseInt(sub.getDebitAmountMoney());
				}
			}

			newDebitAmountMoney.setText(String.valueOf(sumDebitAmountMoney));
			newCreditAmountMoney.setText(String.valueOf(sumCreditAmountMoney));

			if(CommonConstants.DEBIT_CREDIT_FLG.DEBIT.equals(kindFlg)) {
				creditAmountMoney.setEditable(false);
			}else {
				debitAmountMoney.setEditable(false);
			}
		}
	}

	@FXML
	private void onRegistCarryForward(Event eve) {
		if (commonAlert.showConfirmationAlert("登録処理を実行しますか？")) {
			List<CarryForwardSubData> carryforwardsubData = carryforwardDataTable.getItems();
			// 取引先別の前期繰越データ登録
			e1Service.registCarryForward4Torihikisaki(carryforwardsubData, accountCode, kindFlg);
			((Node) eve.getSource()).getScene().getWindow().hide();
		}
	}

}
