package com.busicomjp.sapp.controller.a;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.model.a.JournalEntryData;
import com.busicomjp.sapp.service.a.A1Service;
import com.busicomjp.sapp.service.a.A2Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

@Component
public class A2ShowController extends BaseController implements Initializable {

	Logger logger = LoggerFactory.getLogger(A2ShowController.class);
	private JournalEntryDto journalCondition = null;

	@FXML
	private Button rJournalBtn;
	@FXML
	private Button rbJournalBtn;
	@FXML
	private Button deleteJournalBtn;
	@FXML
	private GridPane a2_show;
	@FXML
	private TableView<JournalEntryData> shiwakeDataTable;
	@FXML
	private TableColumn<JournalEntryData,String> accrualDate;
	@FXML
	private TableColumn<JournalEntryData,String> suppliersName;
	@FXML
	private TableColumn<JournalEntryData,String> tekiyoName;
	@FXML
	private TableColumn<JournalEntryData,String> debitName;
	@FXML
	private TableColumn<JournalEntryData,String> creditName;
	@FXML
	private TableColumn<JournalEntryData,String> amountMoney;
	@FXML
	private TableColumn<JournalEntryData,String> tax;
	@FXML
	private TableColumn<JournalEntryData,String> taxRate;
	@FXML
	private TableColumn<JournalEntryData,String> depPayDate;
	@FXML
	private TableColumn<JournalEntryData,String> journalNo;
	@Autowired
	private A2UpdateDialogController a2UpdateDialogController;

	@Autowired
	private A1Service a1Service;
	@Autowired
	private A2Service a2Service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accrualDate.setCellValueFactory(new PropertyValueFactory<>("accrualDate"));
		suppliersName.setCellValueFactory(new PropertyValueFactory<>("suppliersName"));
		tekiyoName.setCellValueFactory(new PropertyValueFactory<>("tekiyoName"));
		debitName.setCellValueFactory(new PropertyValueFactory<>("debitName"));
		creditName.setCellValueFactory(new PropertyValueFactory<>("creditName"));
		amountMoney.setCellValueFactory(new PropertyValueFactory<>("amountMoney"));
		tax.setCellValueFactory(new PropertyValueFactory<>("tax"));
		taxRate.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
		depPayDate.setCellValueFactory(new PropertyValueFactory<>("depPayDate"));
		journalNo.setCellValueFactory(new PropertyValueFactory<>("journalNo"));

		// テーブルカラム幅設定
		accrualDate.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		suppliersName.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.13).subtract(2.0));
		tekiyoName.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.13).subtract(2.0));
		debitName.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.15).subtract(2.0));
		creditName.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.15).subtract(2.0));
		amountMoney.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.09));
		tax.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.06));
		taxRate.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.09));
		depPayDate.prefWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.1));

		accrualDate.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		suppliersName.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.13).subtract(2.0));
		tekiyoName.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.13).subtract(2.0));
		debitName.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.15).subtract(2.0));
		creditName.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.15).subtract(2.0));
		amountMoney.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.09));
		tax.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.06));
		taxRate.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.09));
		depPayDate.maxWidthProperty().bind(shiwakeDataTable.widthProperty().subtract(10.0).multiply(0.1));

		List<JournalEntryData> dataList = new ArrayList<JournalEntryData>();
		final ObservableList<JournalEntryData> observableList = FXCollections.observableArrayList(dataList);
		shiwakeDataTable.getItems().clear();
		shiwakeDataTable.getItems().addAll(observableList);
		if (dataList == null || dataList.size() == 0) {
			rJournalBtn.setDisable(true);
			rbJournalBtn.setDisable(true);
			deleteJournalBtn.setDisable(true);
		} else {
			rJournalBtn.setDisable(false);
			rbJournalBtn.setDisable(false);
			deleteJournalBtn.setDisable(false);
		}
	}
	
	public void init() {
		bindingKeyPress();
	}

	/**
     * 赤データ登録処理
     */
	@FXML
	private void onRedData() {
		JournalEntryData selectData = getSelectItem();
		if (selectData == null) {
			throw new ValidationException("処理対象を選択してください。");
		}
		
		if (commonAlert.showConfirmationAlert("赤登録処理を実行しますか？")) {
	        // 進捗インジケータ
	        Alert alert = commonAlert.getProgressIndicatorAlert();
	        alert.show();
	        
	        Service<String> service = new Service<String>() {
	            @Override
	            protected Task<String> createTask() {
	                Task<String> task = new Task<String>() {
	                    @Override
	                    protected String call() throws Exception {
	                    	a2Service.registRedJournal(selectData);
	            			List<JournalEntryData> searchResultList = a2Service.getJournalList(journalCondition);
	            			setSearchResult(journalCondition, searchResultList);
	                        return "OK";
	                    }
	                };
	                commonTask.setTaskHandling(task, alert);
	                return task;
	            }
	        };
	        service.start();
		}
	}

	/**
     * 赤黒データ登録処理
     */
	@FXML
	private void onRedBlackData() {
		JournalEntryData selectData = getSelectItem();
		if (selectData == null) {
			throw new ValidationException("処理対象を選択してください。");
		}
		
		Dialog<Pair<String, String>> dialog = commonDialog.getDialog("仕訳データ変更", "/fxml/a/a2_updateShiwakeDialog.fxml", true);
		a2UpdateDialogController.init(this.journalCondition, selectData);
		dialog.showAndWait();
	}
	
	/**
     * 削除処理
     */
	@FXML
	private void onDeleteData() {
		JournalEntryData selectData = getSelectItem();
		if (selectData == null) {
			throw new ValidationException("削除対象を選択してください。");
		}
		
		if (commonAlert.showConfirmationAlert("削除処理を実行しますか？")) {
	        // 進捗インジケータ
	        Alert alert = commonAlert.getProgressIndicatorAlert();
	        alert.show();
	        
	        Service<String> service = new Service<String>() {
	            @Override
	            protected Task<String> createTask() {
	                Task<String> task = new Task<String>() {
	                    @Override
	                    protected String call() throws Exception {
	                    	// 削除処理
	            			a1Service.deleteData(selectData);
	            			List<JournalEntryData> searchResultList = a2Service.getJournalList(journalCondition);
	            			setSearchResult(journalCondition, searchResultList);
	                        return "OK";
	                    }
	                };
	                commonTask.setTaskHandling(task, alert);
	                return task;
	            }
	        };
	        service.start();
		}
	}

	private JournalEntryData getSelectItem() {
		JournalEntryData selectData = shiwakeDataTable.getSelectionModel().getSelectedItem();
		if (selectData != null) {
			selectData.setCompanyCode(CompanyUtil.getCompanyCode());
		}
		return selectData;
	}

	public void setSearchResult(JournalEntryDto journal, List<JournalEntryData> searchResultList) {
		this.journalCondition = journal;
		List<JournalEntryData> dataList = searchResultList;
		final ObservableList<JournalEntryData> observableList = FXCollections.observableArrayList(dataList);
		shiwakeDataTable.getItems().clear();
		shiwakeDataTable.getItems().addAll(observableList);
		if (dataList == null || dataList.size() == 0) {
			rJournalBtn.setDisable(true);
			rbJournalBtn.setDisable(true);
			deleteJournalBtn.setDisable(true);
		} else {
			rJournalBtn.setDisable(false);
			rbJournalBtn.setDisable(false);
			deleteJournalBtn.setDisable(false);
		}
	}
	
	private void bindingKeyPress() {
		KeyCombination f4 = new KeyCodeCombination(KeyCode.F4);
		KeyCombination f5 = new KeyCodeCombination(KeyCode.F5);
		KeyCombination delete = new KeyCodeCombination(KeyCode.DELETE);
		App.putKeyEvent(f4, new Runnable() {
			@Override
			public void run() {
				onRedData();
			}
		});
		App.putKeyEvent(f5, new Runnable() {
			@Override
			public void run() {
				onRedBlackData();
			}
		});
		App.putKeyEvent(delete, new Runnable() {
			@Override
			public void run() {
				onDeleteData();
			}
		});

		// 他画面のキーイベントを上書き
		KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
		KeyCombination f2 = new KeyCodeCombination(KeyCode.F2);
		KeyCombination f3 = new KeyCodeCombination(KeyCode.F3);
		KeyCombination f9 = new KeyCodeCombination(KeyCode.F9);
		App.putKeyEvent(f1, new Runnable() {
			@Override
			public void run() {
				// Nothing
			}
		});
		App.putKeyEvent(f2, new Runnable() {
			@Override
			public void run() {
				// Nothing
			}
		});
		App.putKeyEvent(f3, new Runnable() {
			@Override
			public void run() {
				// Nothing
			}
		});
		App.putKeyEvent(f9, new Runnable() {
			@Override
			public void run() {
				// Nothing
			}
		});
	}
}
