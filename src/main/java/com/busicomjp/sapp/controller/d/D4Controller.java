package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.CommonDialog;
import com.busicomjp.sapp.model.d.TekiyoData;
import com.busicomjp.sapp.model.d.TekiyoSelectedData;
import com.busicomjp.sapp.service.d.D4Service;

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
import javafx.scene.input.KeyEvent;

@Component
public class D4Controller implements Initializable  {

	@Autowired
	private D4Service d4Service;
	@Autowired
	private CommonDialog commonDiaLog;
	
	@FXML
	private TextField searchTekiyoName;
	@FXML
	private TableView<TekiyoData> tekiyoDataTable;
	@FXML
	private TableColumn<TekiyoData, String> tekiyoName;
	@FXML
	private TableColumn<TekiyoData, String> tekiyoNameKana;
	@FXML
	private TableColumn<TekiyoData, String> defaultAccountName;
	@FXML
	private TableColumn<TekiyoData, String> salesAccountName;
	@FXML
	private TableColumn<TekiyoData, String> mgmtAccountName;
	@FXML
	private TableColumn<TekiyoData, String> costAccountName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tekiyoName.setCellValueFactory(new PropertyValueFactory<>("tekiyoName"));
		tekiyoNameKana.setCellValueFactory(new PropertyValueFactory<>("tekiyoNameKana"));
		defaultAccountName.setCellValueFactory(new PropertyValueFactory<>("defaultAccountName"));
		salesAccountName.setCellValueFactory(new PropertyValueFactory<>("salesAccountName"));
		mgmtAccountName.setCellValueFactory(new PropertyValueFactory<>("mgmtAccountName"));
		costAccountName.setCellValueFactory(new PropertyValueFactory<>("costAccountName"));
		
		tekiyoName.prefWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.20).subtract(1.0));
		tekiyoNameKana.prefWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.24).subtract(1.0));
		defaultAccountName.prefWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		salesAccountName.prefWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		mgmtAccountName.prefWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		costAccountName.prefWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		
		tekiyoName.maxWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.20).subtract(1.0));
		tekiyoNameKana.maxWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.24).subtract(1.0));
		defaultAccountName.maxWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		salesAccountName.maxWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		mgmtAccountName.maxWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		costAccountName.maxWidthProperty().bind(tekiyoDataTable.widthProperty().subtract(10.0).multiply(0.14).subtract(2.0));
		
		searchTekiyoName.setOnKeyPressed(new EventHandler<KeyEvent>()
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
		searchTekiyoName.clear();
		this.onSearchData();
	}
	
	@FXML
	public void onSearchData() {
		
		List<TekiyoData> dataList = d4Service.getSummaryListData(searchTekiyoName.getText().trim());
		
		final ObservableList<TekiyoData> observableList = FXCollections.observableArrayList(dataList);
		tekiyoDataTable.getItems().clear();
		tekiyoDataTable.getItems().addAll(observableList);
	}
	
	@FXML
	private void onRegistData() {
		commonDiaLog.showDialog("摘要登録", "/fxml/d/d4_registTekiyoDialog.fxml", true);
	}
	
	@FXML
	private void onChangeData() {
		
		TekiyoData selectData = tekiyoDataTable.getSelectionModel().getSelectedItem();
		if (selectData == null) {
			// データが選択されない場合
			throw new ValidationException("変更対象を選択してください");
		} else {
			TekiyoSelectedData.setTekiyoUtil(selectData);
		}
		commonDiaLog.showDialog("摘要変更", "/fxml/d/d4_updateTekiyoDialog.fxml", true);
	}

}
