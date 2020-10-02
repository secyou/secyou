package com.busicomjp.sapp.controller.b;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.controller.FeatureGroupController;
import com.busicomjp.sapp.model.b.BalanceData;
import com.busicomjp.sapp.service.b.B1Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

@Component
public class B1Controller extends BaseController implements Initializable  {

	@Autowired
	private B1Service b1Service;
	@Autowired
	private FeatureGroupController featureController;
	@FXML
	private GridPane b1_show;
	@FXML
	private Label label_1;
	@FXML
	private Label label_2;
	@FXML
	private Label label_3;
	@FXML
	private Label label_4;
	@FXML
	private TableView<BalanceData> balanceDataTable;
	@FXML
	private TableColumn<BalanceData,String> accountName;
	@FXML
	private TableColumn<BalanceData,String> debitCFBalance_s;
	@FXML
	private TableColumn<BalanceData,String> creditCFBalance_s;
	@FXML
	private TableColumn<BalanceData,String> debitBalance_s;
	@FXML
	private TableColumn<BalanceData,String> creditBalance_s;
	@FXML
	private TableColumn<BalanceData,String> debitTotalBalance_s;
	@FXML
	private TableColumn<BalanceData,String> creditTotalBalance_s;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
		debitCFBalance_s.setCellValueFactory(new PropertyValueFactory<>("debitCFBalance_s"));
		creditCFBalance_s.setCellValueFactory(new PropertyValueFactory<>("creditCFBalance_s"));
		debitBalance_s.setCellValueFactory(new PropertyValueFactory<>("debitBalance_s"));
		creditBalance_s.setCellValueFactory(new PropertyValueFactory<>("creditBalance_s"));
		debitTotalBalance_s.setCellValueFactory(new PropertyValueFactory<>("debitTotalBalance_s"));
		creditTotalBalance_s.setCellValueFactory(new PropertyValueFactory<>("creditTotalBalance_s"));

		// テーブルカラム幅設定
		accountName.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.5).subtract(2.0));
		debitCFBalance_s.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		creditCFBalance_s.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		debitBalance_s.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.09).subtract(2.0));
		creditBalance_s.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.09).subtract(0.0));
		debitTotalBalance_s.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		creditTotalBalance_s.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(0.0));

		accountName.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.5).subtract(2.0));
		debitCFBalance_s.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		creditCFBalance_s.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		debitBalance_s.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.09).subtract(2.0));
		creditBalance_s.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.09).subtract(0.0));
		debitTotalBalance_s.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		creditTotalBalance_s.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(0.0));
		
		label_1.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.5).subtract(4.0));
		label_2.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.156).subtract(4.0));
		label_3.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.178).subtract(4.0));
		label_4.prefWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.156).subtract(4.0));
		
		label_1.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.5).subtract(4.0));
		label_2.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.156).subtract(4.0));
		label_3.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.178).subtract(4.0));
		label_4.maxWidthProperty().bind(balanceDataTable.widthProperty().subtract(10.0).multiply(0.156).subtract(4.0));
		
		debitCFBalance_s.setCellFactory(tc -> {
            TableCell<BalanceData, String> cell = new TableCell<BalanceData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    onBalanceClick();
                }
            });
            return cell;
        });

		creditCFBalance_s.setCellFactory(tc -> {
			TableCell<BalanceData, String> cell = new TableCell<BalanceData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onBalanceClick();
				}
			});
			return cell;
		});
		
		debitBalance_s.setCellFactory(tc -> {
			TableCell<BalanceData, String> cell = new TableCell<BalanceData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onBalanceClick();
				}
			});
			return cell;
		});
		
		creditBalance_s.setCellFactory(tc -> {
			TableCell<BalanceData, String> cell = new TableCell<BalanceData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onBalanceClick();
				}
			});
			return cell;
		});
		
		debitTotalBalance_s.setCellFactory(tc -> {
			TableCell<BalanceData, String> cell = new TableCell<BalanceData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onBalanceClick();
				}
			});
			return cell;
		});
		
		creditTotalBalance_s.setCellFactory(tc -> {
			TableCell<BalanceData, String> cell = new TableCell<BalanceData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onBalanceClick();
				}
			});
			return cell;
		});
	}

	public void init() {
		getBalanceDataList();
		label_2.setText("期首:"+ StringUtil.dateSlashFormat(CompanyUtil.getAccountStartDay()));
		label_3.setText("作業期間:"+ b1Service.getWorkPeriod());
		label_4.setText("期末:"+ StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()));
	}

	public void getBalanceDataList() {
		List<BalanceData> dataList = b1Service.getBalanceDetailDataList();
		final ObservableList<BalanceData> observableList = FXCollections.observableArrayList(dataList);
		balanceDataTable.getItems().clear();
		balanceDataTable.getItems().addAll(observableList);
	}

	/**
     * 残高CELLクリック
     */
	private void onBalanceClick() {
		BalanceData data = balanceDataTable.getSelectionModel().getSelectedItem();
		if (data != null && StringUtils.isNotEmpty(data.getAccountCode())) {
			featureController.showGeneralLedger(data.getAccountCode(), data.getAccountName());
		}
	}

}
