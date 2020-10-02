package com.busicomjp.sapp.controller.a;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.a.SettlementData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

@Component
public class A3Controller extends BaseController implements Initializable {
	
	private List<SettlementData> dataList1 = new ArrayList<SettlementData>();
	private List<SettlementData> dataList2 = new ArrayList<SettlementData>();
	private List<SettlementData> dataList3 = new ArrayList<SettlementData>();
	
	@FXML
	private TableView<SettlementData> settlementTable1;
	@FXML
	private TableColumn<SettlementData, String> accrualDate1;
	@FXML
	private TableColumn<SettlementData, String> accountName1;
	@FXML
	private TableColumn<SettlementData, String> counterAccountName1;
	@FXML
	private TableColumn<SettlementData, String> tekiyoName1;
	@FXML
	private TableColumn<SettlementData, String> taxName1;
	@FXML
	private TableColumn<SettlementData, String> debitAmountMoney1;
	@FXML
	private TableColumn<SettlementData, String> creditAmountMoney1;
	@FXML
	private TableColumn<SettlementData, String> balanceMoney1;
	
	@FXML
	private TableView<SettlementData> settlementTable2;
	@FXML
	private TableColumn<SettlementData, String> accrualDate2;
	@FXML
	private TableColumn<SettlementData, String> accountName2;
	@FXML
	private TableColumn<SettlementData, String> counterAccountName2;
	@FXML
	private TableColumn<SettlementData, String> tekiyoName2;
	@FXML
	private TableColumn<SettlementData, String> taxName2;
	@FXML
	private TableColumn<SettlementData, String> debitAmountMoney2;
	@FXML
	private TableColumn<SettlementData, String> creditAmountMoney2;
	@FXML
	private TableColumn<SettlementData, String> balanceMoney2;
	
	@FXML
	private TableView<SettlementData> settlementTable3;
	@FXML
	private TableColumn<SettlementData, String> accrualDate3;
	@FXML
	private TableColumn<SettlementData, String> accountName3;
	@FXML
	private TableColumn<SettlementData, String> counterAccountName3;
	@FXML
	private TableColumn<SettlementData, String> tekiyoName3;
	@FXML
	private TableColumn<SettlementData, String> taxName3;
	@FXML
	private TableColumn<SettlementData, String> debitAmountMoney3;
	@FXML
	private TableColumn<SettlementData, String> creditAmountMoney3;
	@FXML
	private TableColumn<SettlementData, String> balanceMoney3;
	
	@FXML
	private TableView<SettlementData> settlementTable4;
	
	@FXML
	private TableView<SettlementData> settlementTable5;
	
	@FXML
	private TableView<SettlementData> settlementTable6;
	
	@FXML
	private TableView<SettlementData> settlementTable7;
	
	@FXML
	private TableView<SettlementData> settlementTable8;
	
	@FXML
	private TableView<SettlementData> settlementTable9;
	
	@FXML
	private Button btnFinal1;
	@FXML
	private Button btnFinal2;
	@FXML
	private Button btnFinal3;
	@FXML
	private Button btnFinal4;
	@FXML
	private Button btnFinal5;
	@FXML
	private Button btnFinal6;
	@FXML
	private Button btnFinal7;
	@FXML
	private Button btnFinal8;
	@FXML
	private Button btnFinal9;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accrualDate1.setCellValueFactory(new PropertyValueFactory<>("accrualDate"));
		accountName1.setCellValueFactory(new PropertyValueFactory<>("accountName"));
		counterAccountName1.setCellValueFactory(new PropertyValueFactory<>("counterAccountName"));
		taxName1.setCellValueFactory(new PropertyValueFactory<>("taxName"));
		tekiyoName1.setCellValueFactory(new PropertyValueFactory<>("tekiyoName"));
		debitAmountMoney1.setCellValueFactory(new PropertyValueFactory<>("debitAmountMoney"));
		creditAmountMoney1.setCellValueFactory(new PropertyValueFactory<>("creditAmountMoney"));
		balanceMoney1.setCellValueFactory(new PropertyValueFactory<>("balanceMoney"));
		
		accrualDate2.setCellValueFactory(new PropertyValueFactory<>("accrualDate"));
		accountName2.setCellValueFactory(new PropertyValueFactory<>("accountName"));
		counterAccountName2.setCellValueFactory(new PropertyValueFactory<>("counterAccountName"));
		tekiyoName2.setCellValueFactory(new PropertyValueFactory<>("tekiyoName"));
		taxName2.setCellValueFactory(new PropertyValueFactory<>("taxName"));
		debitAmountMoney2.setCellValueFactory(new PropertyValueFactory<>("debitAmountMoney"));
		creditAmountMoney2.setCellValueFactory(new PropertyValueFactory<>("creditAmountMoney"));
		balanceMoney2.setCellValueFactory(new PropertyValueFactory<>("balanceMoney"));
		
		accrualDate3.setCellValueFactory(new PropertyValueFactory<>("accrualDate"));
		accountName3.setCellValueFactory(new PropertyValueFactory<>("accountName"));
		counterAccountName3.setCellValueFactory(new PropertyValueFactory<>("counterAccountName"));
		tekiyoName3.setCellValueFactory(new PropertyValueFactory<>("tekiyoName"));
		taxName3.setCellValueFactory(new PropertyValueFactory<>("taxName"));
		debitAmountMoney3.setCellValueFactory(new PropertyValueFactory<>("debitAmountMoney"));
		creditAmountMoney3.setCellValueFactory(new PropertyValueFactory<>("creditAmountMoney"));
		balanceMoney3.setCellValueFactory(new PropertyValueFactory<>("balanceMoney"));
		
		// 棚卸し
		accrualDate1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		accountName1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.2));
		counterAccountName1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.2));
		tekiyoName1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.2));
		taxName1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		debitAmountMoney1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.8));
		creditAmountMoney1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.8));
		balanceMoney1.prefWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.8));
		
		accrualDate1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		accountName1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.2));
		counterAccountName1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.2));
		tekiyoName1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.2));
		taxName1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		debitAmountMoney1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		creditAmountMoney1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		balanceMoney1.maxWidthProperty().bind(settlementTable1.widthProperty().subtract(10.0).multiply(0.08));
		
		// 減価償却
		accrualDate2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		accountName2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.2));
		counterAccountName2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.2));
		tekiyoName2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.2));
		taxName2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		debitAmountMoney2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.8));
		creditAmountMoney2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.8));
		balanceMoney2.prefWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.8));
		
		accrualDate2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		accountName2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.2));
		counterAccountName2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.2));
		tekiyoName2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.2));
		taxName2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		debitAmountMoney2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		creditAmountMoney2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		balanceMoney2.maxWidthProperty().bind(settlementTable2.widthProperty().subtract(10.0).multiply(0.08));
		
		// 引当金
		accrualDate3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		accountName3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.2));
		counterAccountName3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.2));
		tekiyoName3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.2));
		taxName3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		debitAmountMoney3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.8));
		creditAmountMoney3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.8));
		balanceMoney3.prefWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.8));
		
		accrualDate3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		accountName3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.2));
		counterAccountName3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.2));
		tekiyoName3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.2));
		taxName3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		debitAmountMoney3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		creditAmountMoney3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		balanceMoney3.maxWidthProperty().bind(settlementTable3.widthProperty().subtract(10.0).multiply(0.08));
		
		settlementTable1.setFixedCellSize(25);
		settlementTable1.minHeightProperty().bind(settlementTable1.prefHeightProperty());
		settlementTable1.maxHeightProperty().bind(settlementTable1.prefHeightProperty());
		
		settlementTable2.setFixedCellSize(25);
		settlementTable2.minHeightProperty().bind(settlementTable2.prefHeightProperty());
		settlementTable2.maxHeightProperty().bind(settlementTable2.prefHeightProperty());
		
		settlementTable3.setFixedCellSize(25);
		settlementTable3.minHeightProperty().bind(settlementTable3.prefHeightProperty());
		settlementTable3.maxHeightProperty().bind(settlementTable3.prefHeightProperty());
		
		settlementTable4.setFixedCellSize(25);
		settlementTable4.minHeightProperty().bind(settlementTable4.prefHeightProperty());
		settlementTable4.maxHeightProperty().bind(settlementTable4.prefHeightProperty());
		
		settlementTable5.setFixedCellSize(25);
		settlementTable5.minHeightProperty().bind(settlementTable5.prefHeightProperty());
		settlementTable5.maxHeightProperty().bind(settlementTable5.prefHeightProperty());
		
		settlementTable6.setFixedCellSize(25);
		settlementTable6.minHeightProperty().bind(settlementTable6.prefHeightProperty());
		settlementTable6.maxHeightProperty().bind(settlementTable6.prefHeightProperty());
		
		settlementTable7.setFixedCellSize(25);
		settlementTable7.minHeightProperty().bind(settlementTable7.prefHeightProperty());
		settlementTable7.maxHeightProperty().bind(settlementTable7.prefHeightProperty());
		
		settlementTable8.setFixedCellSize(25);
		settlementTable8.minHeightProperty().bind(settlementTable8.prefHeightProperty());
		settlementTable8.maxHeightProperty().bind(settlementTable8.prefHeightProperty());

		settlementTable9.setFixedCellSize(25);
		settlementTable9.minHeightProperty().bind(settlementTable9.prefHeightProperty());
		settlementTable9.maxHeightProperty().bind(settlementTable9.prefHeightProperty());
		
		debitAmountMoney1.setCellFactory(TextFieldTableCell.forTableColumn());
		debitAmountMoney1.setOnEditCommit((TableColumn.CellEditEvent<SettlementData, String> t) -> {
			SettlementData editedData = t.getTableView().getItems().get(t.getTablePosition().getRow());
			String newValue = StringUtil.replaceFull2HalfNumber(t.getNewValue());
			String oldValue = t.getOldValue();
			if (!oldValue.equals(newValue)) {
				// 値を変更した場合
				if (!editedData.isDebitEditFlg() && !editedData.isCreditEditFlg()) {
					// 借方貸方両方が編集不可
					throw new ValidationException(editedData.getEditItemName() + "は金額編集できません。");
				}
				if (!editedData.isDebitEditFlg()) {
					// 借方が編集不可
					throw new ValidationException("借方金額は編集できません。");
				}
				newValue = StringUtil.replaceCommaFormat(newValue); //　カンマを置換
				if (StringUtils.isNotEmpty(newValue) && !InputCheck.isNumeric(newValue)) {
					throw new ValidationException("金額は整数で入力してください。");
				}
				editedData.setDebitAmountMoney(StringUtil.commaFormat(newValue));
				// 再計算
				recalcTable1();
			}
		});
		
	}
	
	public void init() {
		// TODO テスト用
		dataList1 = new ArrayList<SettlementData>();
		dataList2 = new ArrayList<SettlementData>();
		dataList3 = new ArrayList<SettlementData>();
		SettlementData data1 = new SettlementData();
		SettlementData data2 = new SettlementData();
		SettlementData data3 = new SettlementData();
		data1.setAccrualDate(StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()));
		data1.setAccountName("商品");
		data1.setCounterAccountName("期首棚卸高");
		data1.setTekiyoName("前期繰越");
		data1.setTaxName("不課税");
		data1.setDebitAmountMoney("1,000");
		data1.setCreditAmountMoney("-");
		data1.setBalanceMoney("1,000");
		data1.setEditItemName("期首棚卸高");
		
		data2.setAccrualDate(StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()));
		data2.setAccountName("商品");
		data2.setCounterAccountName("-");
		data2.setTekiyoName("在庫棚卸し");
		data2.setTaxName("不課税");
		data2.setDebitAmountMoney("金額入力");
		data2.setCreditAmountMoney("-");
		data2.setBalanceMoney("-");
		data2.setDebitEditFlg(true);
		
		data3.setAccrualDate(StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()));
		data3.setAccountName("商品");
		data3.setCounterAccountName("期末棚卸高");
		data3.setTekiyoName("次期繰越");
		data3.setTaxName("不課税");
		data3.setDebitAmountMoney("-");
		data3.setCreditAmountMoney("1,000");
		data3.setBalanceMoney("0");
		data3.setEditItemName("期末棚卸高");
		
		dataList1.add(data1);
		dataList1.add(data2);
		dataList1.add(data3);

		final ObservableList<SettlementData> observableList1 = FXCollections.observableArrayList(dataList1);
		settlementTable1.getItems().clear();
		settlementTable1.getItems().addAll(observableList1);
		// データ件数により高さを表示
		settlementTable1.prefHeightProperty().bind(settlementTable1.fixedCellSizeProperty().multiply(4.0).add(3.0));
		
		
		data1 = new SettlementData();
		data2 = new SettlementData();
		
		data1.setAccrualDate(StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()));
		data1.setAccountName("減価償却費");
		data1.setCounterAccountName("建物");
		data1.setTaxName("不課税");
		data1.setDebitAmountMoney("2,000");
		data1.setCreditAmountMoney("0");
		data1.setBalanceMoney("2,000");
		
		data2.setAccrualDate(StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()));
		data2.setAccountName("減価償却費");
		data2.setCounterAccountName("設備造作");
		data2.setTaxName("不課税");
		data2.setDebitAmountMoney("1,200");
		data2.setCreditAmountMoney("0");
		data2.setBalanceMoney("3,200");
		
		dataList2.add(data1);
		dataList2.add(data2);
		
		final ObservableList<SettlementData> observableList2 = FXCollections.observableArrayList(dataList2);
		settlementTable2.getItems().clear();
		settlementTable2.getItems().addAll(observableList2);
		settlementTable2.prefHeightProperty().bind(settlementTable2.fixedCellSizeProperty().multiply(3.0).add(3.0));
		
		data1 = new SettlementData();
		data1.setAccrualDate("対象データなし");
		dataList3.add(data1);
		
		final ObservableList<SettlementData> observableList3 = FXCollections.observableArrayList(dataList3);
		settlementTable3.getItems().clear();
		settlementTable3.getItems().addAll(observableList3);
		settlementTable3.prefHeightProperty().bind(settlementTable3.fixedCellSizeProperty().multiply(2.0).add(3.0));
		// 確定ボタンを無効
		btnFinal3.setDisable(true);
		
		settlementTable4.prefHeightProperty().bind(settlementTable4.fixedCellSizeProperty().multiply(1.0 + 1.0).add(3.0));
		settlementTable5.prefHeightProperty().bind(settlementTable5.fixedCellSizeProperty().multiply(1.0 + 1.0).add(3.0));
		settlementTable6.prefHeightProperty().bind(settlementTable6.fixedCellSizeProperty().multiply(1.0 + 1.0).add(3.0));
		settlementTable7.prefHeightProperty().bind(settlementTable7.fixedCellSizeProperty().multiply(1.0 + 1.0).add(3.0));
		settlementTable8.prefHeightProperty().bind(settlementTable8.fixedCellSizeProperty().multiply(1.0 + 1.0).add(3.0));
		settlementTable9.prefHeightProperty().bind(settlementTable9.fixedCellSizeProperty().multiply(1.0 + 1.0).add(3.0));
		// TODO テスト用
	}
	
	private void recalcTable1() {
		// TODO テスト用
		long debit = 0l;
		long credit = 0l;
		long balance = 0l;
		long prevBalance = 0l;
		for (SettlementData data : settlementTable1.getItems()) {
			if (!"期末棚卸高".equals(data.getCounterAccountName())) {
				debit = getLongValue(data.getDebitAmountMoney());
				credit = getLongValue(data.getCreditAmountMoney());
				balance = prevBalance + debit - credit;
				prevBalance = balance;
				data.setBalanceMoney(StringUtil.commaFormat(balance));
			} else {
				if (prevBalance >= 0) {
					data.setDebitAmountMoney("0");
					data.setCreditAmountMoney(StringUtil.commaFormat(prevBalance));
				} else {
					data.setDebitAmountMoney(StringUtil.commaFormat(prevBalance));
					data.setCreditAmountMoney("0");
				}
				data.setBalanceMoney("0");
			}
		}
		// TODO テスト用
	}
	
	private long getLongValue(String s) {
		try {
			return Long.parseLong(StringUtil.replaceCommaFormat(s));
		} catch (Exception e) {
			return 0l;
		}
	}
	
	@FXML
	private void onReloadData() {
		
	}
	
	@FXML
	private void onRegistData() {
		if (commonAlert.showConfirmationAlert("決算登録処理を実行しますか？")) {
			
		}
	}
	
	@FXML
	private void onFinal1() {
		// TODO テスト用
		if (settlementTable1.isDisable()) {
			settlementTable1.setDisable(false);
			btnFinal1.setText("確定");
		} else {
			settlementTable1.setDisable(true);
			btnFinal1.setText("修正");
		}
		// TODO テスト用
	}
	
	@FXML
	private void onFinal2() {
		
	}
	
	@FXML
	private void onFinal3() {
		
	}
	
	@FXML
	private void onFinal4() {
		
	}
	
	@FXML
	private void onFinal5() {
		
	}
	
	@FXML
	private void onFinal6() {
		
	}
	
	@FXML
	private void onFinal7() {
		
	}
	
	@FXML
	private void onFinal8() {
		
	}
	
	@FXML
	private void onFinal9() {
		
	}
	
}
