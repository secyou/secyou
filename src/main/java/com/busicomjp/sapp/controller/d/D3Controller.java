package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.d.AccountMaintData;
import com.busicomjp.sapp.model.d.AccountSeletedData;
import com.busicomjp.sapp.service.d.D3Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

@Component
public class D3Controller extends BaseController implements Initializable  {

	@Autowired
	private D3Service d3Service;

	@FXML
	private TextField searchKindName;
	@FXML
	private TextField searchAccountName;
	@FXML
	private TableView<AccountMaintData> accountDataTable;
	@FXML
	private TableColumn<AccountMaintData, String> kindGroup;
	@FXML
	private TableColumn<AccountMaintData, String> kindFlg;
	@FXML
	private TableColumn<AccountMaintData, String> accountName;
	@FXML
	private TableColumn<AccountMaintData, String> accountNameKana;
	@FXML
	private TableColumn<AccountMaintData, String> useFlg;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		kindGroup.setCellValueFactory(new PropertyValueFactory<>("kindGroup"));
		kindFlg.setCellValueFactory(new PropertyValueFactory<>("kindFlg"));
		accountName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
		accountNameKana.setCellValueFactory(new PropertyValueFactory<>("accountNameKana"));
		useFlg.setCellValueFactory(new PropertyValueFactory<>("useFlg"));
		
		kindGroup.prefWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.35).subtract(2.0));
		kindFlg.prefWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		accountName.prefWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.2).subtract(2.0));
		accountNameKana.prefWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(2.0));
		useFlg.prefWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		
		kindGroup.maxWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.35).subtract(2.0));
		kindFlg.maxWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		accountName.maxWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.2).subtract(2.0));
		accountNameKana.maxWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(2.0));
		useFlg.maxWidthProperty().bind(accountDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		

		searchKindName.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	onSearchData();
	            }
	        }
	    });

		searchAccountName.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	onSearchData();
	            }
	        }
	    });
	}

	public void init() {
		bindingKeyPress();
		
		searchKindName.clear();
		searchAccountName.clear();
		
		this.onSearchData();
	}
	
	@FXML
	public void onSearchData() {
		
		List<AccountMaintData> dataList = d3Service.getAccountListData(searchKindName.getText().trim(), 
				                                                       searchAccountName.getText().trim() );
		
		final ObservableList<AccountMaintData> observableList = FXCollections.observableArrayList(dataList);
		accountDataTable.getItems().clear();
		accountDataTable.getItems().addAll(observableList);	
	}
	
	@FXML
	private void onRegistData() {
		commonDialog.showDialog("勘定科目登録", "/fxml/d/d3_registAccountDialog.fxml", true);
	}
	
	@FXML
	private void onChangeData() {
		AccountMaintData selectData = accountDataTable.getSelectionModel().getSelectedItem();
		if (selectData == null) {
			// データが選択されない場合
			throw new ValidationException("変更対象を選択してください。");
		}
		
		AccountSeletedData.setAccountSeleteData(selectData);
		commonDialog.showDialog("勘定科目変更", "/fxml/d/d3_updateAccountDialog.fxml", true);
	}
	
	private void bindingKeyPress() {
		App.clearKeyEvent();
		KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
		KeyCombination f2 = new KeyCodeCombination(KeyCode.F2);
		KeyCombination f3 = new KeyCodeCombination(KeyCode.F3);
		App.putKeyEvent(f1, new Runnable() {
			@Override
			public void run() {
				onSearchData();
			}
		});
		App.putKeyEvent(f2, new Runnable() {
			@Override
			public void run() {
				onRegistData();
			}
		});
		App.putKeyEvent(f3, new Runnable() {
			@Override
			public void run() {
				onChangeData();
			}
		});
	}

}
