package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.d.CompanyData;
import com.busicomjp.sapp.service.d.D1Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

@Component
public class D1Controller extends BaseController implements Initializable {

	static final Logger log = LoggerFactory.getLogger(D1Controller.class);

	@Autowired
	private D1UpdateCompanyDialogController d1UpdateCompanyDialogController;
	@Autowired
	private D1Service d1Service;

	@FXML
	private TableView<CompanyData> companyDataTable;
	@FXML
	private TableColumn<CompanyData, String> companyName;
	@FXML
	private TableColumn<CompanyData, String> kimatuYear;
	@FXML
	private TableColumn<CompanyData, String> fiscalYear;
	@FXML
	private TableColumn<CompanyData, String> taxKindName;
	@FXML
	private TableColumn<CompanyData, String> taxAppName;
	@FXML
	private TableColumn<CompanyData, String> businessTypeName;
	@FXML
	private TableColumn<CompanyData, String> businessLineName;
	@FXML
	private TextField searchCompanyName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		companyName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
		businessTypeName.setCellValueFactory(new PropertyValueFactory<>("businessTypeName"));
		businessLineName.setCellValueFactory(new PropertyValueFactory<>("businessLineName"));
		kimatuYear.setCellValueFactory(new PropertyValueFactory<>("kimatuYear"));
		fiscalYear.setCellValueFactory(new PropertyValueFactory<>("fiscalYear"));
		taxKindName.setCellValueFactory(new PropertyValueFactory<>("taxKindName"));
		taxAppName.setCellValueFactory(new PropertyValueFactory<>("taxAppName"));

		companyName.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.3).subtract(2.0));
		businessTypeName.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		businessLineName.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		kimatuYear.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		fiscalYear.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.2).subtract(2.0));
		taxKindName.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1));
		taxAppName.prefWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1));

		companyName.maxWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.3).subtract(2.0));
		businessTypeName.maxWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		businessLineName.maxWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		kimatuYear.maxWidthProperty().bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		fiscalYear.maxWidthProperty().bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.2).subtract(2.0));
		taxKindName.maxWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1));
		taxAppName.maxWidthProperty()
				.bind(companyDataTable.widthProperty().subtract(10.0).multiply(0.1));

		// 会社情報取得
		List<CompanyData> dataList = d1Service.findCompanyByName(null);
		final ObservableList<CompanyData> observableList = FXCollections.observableArrayList(dataList);
		companyDataTable.getItems().clear();
		companyDataTable.getItems().addAll(observableList);
		
		searchCompanyName.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	searchData();
	            }
	        }
	    });
	}

	public void init() {
		bindingKeyPress();
		this.searchData();
	}

	private void searchData() {
		List<CompanyData> dataList = d1Service.findCompanyByName(searchCompanyName.getText());
		final ObservableList<CompanyData> observableList = FXCollections.observableArrayList(dataList);
		companyDataTable.getItems().clear();
		companyDataTable.getItems().addAll(observableList);
	}

	/**
	 * 会社データ検索
	 */
	@FXML
	private void onSearchData() {
		this.searchData();
	}

	/**
	 * 会社データ新規登録
	 */
	@FXML
	private void onRegistData() {
		commonDialog.showScrollDialog("会社新規登録", "/fxml/f/f1_registCompany.fxml", true);
		this.searchData();
	}

	/**
	 * 会社情報変更ダイアログを表示
	 */
	@FXML
	private void onChangeData() {
		CompanyData selectData = companyDataTable.getSelectionModel().getSelectedItem();
		if (selectData == null || StringUtils.isEmpty(selectData.getCompanyCode())) {
			throw new ValidationException("対象会社を選択してください。");
		}
		
		log.info("会社選択結果：会社コード=" + selectData.getCompanyCode() + ", 会社名=" + selectData.getCompanyName());
		Dialog<Pair<String, String>> dialog = commonDialog.getScrollDialog("会社情報更新", "/fxml/d/d1_updateCompanyDialog.fxml", true);
		d1UpdateCompanyDialogController.init(selectData.getCompanyCode(), selectData.getKimatuYear());
		dialog.showAndWait();
		this.searchData();
	}
	
	private void bindingKeyPress() {
		if (App.getCompanyManage() == null || App.getCompanyManage().getAccelerators() == null) {
			return;
		}
		
		App.getCompanyManage().getAccelerators().clear();
		KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
		KeyCombination f2 = new KeyCodeCombination(KeyCode.F2);
		KeyCombination f3 = new KeyCodeCombination(KeyCode.F3);
		App.getCompanyManage().getAccelerators().put(f1, new Runnable() {
			@Override
			public void run() {
				onSearchData();
			}
		});
		App.getCompanyManage().getAccelerators().put(f2, new Runnable() {
			@Override
			public void run() {
				onRegistData();
			}
		});
		App.getCompanyManage().getAccelerators().put(f3, new Runnable() {
			@Override
			public void run() {
				onChangeData();
			}
		});
	}

}
