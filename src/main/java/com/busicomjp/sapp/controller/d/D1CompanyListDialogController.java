package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.controller.CompanyManageController;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.d.CompanyData;
import com.busicomjp.sapp.service.d.D1Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;

@Component
public class D1CompanyListDialogController extends BaseController implements Initializable  {

	static final Logger log = LoggerFactory.getLogger(D1CompanyListDialogController.class);
	
	@Autowired
	private CompanyManageController companyManageController;

	/** 会社選択フラグ */
	@Getter
	private static boolean companySelectedFlg;
	//翌年繰越画面遷移フラグ
	@Getter
	private static boolean registNextyearFlg;
	@Autowired
	private D1Service z1Service;

	@FXML
	private TableView<CompanyData> companyDataTable;
	@FXML
	private TableColumn<CompanyData,String> companyName;
	@FXML
	private TableColumn<CompanyData,String> kimatuYear;
	@FXML
	private TableColumn<CompanyData,String> fiscalYear;
	@FXML
	private TableColumn<CompanyData,String> taxKindName;
	@FXML
	private TableColumn<CompanyData,String> taxAppName;
	@FXML
	private TableColumn<CompanyData,String> companyCode;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		companyName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
		kimatuYear.setCellValueFactory(new PropertyValueFactory<>("kimatuYear"));
		fiscalYear.setCellValueFactory(new PropertyValueFactory<>("fiscalYear"));
		taxKindName.setCellValueFactory(new PropertyValueFactory<>("taxKindName"));
		taxAppName.setCellValueFactory(new PropertyValueFactory<>("taxAppName"));
		companyCode.setCellValueFactory(new PropertyValueFactory<>("companyCode"));

		kimatuYear.setCellFactory(col -> {
			ComboBox<ComboItem> comboBox = new ComboBox<ComboItem>();
			comboBox.setConverter(new ComboItemConverter());
	        ComboBoxTableCell<CompanyData, String> cell = new ComboBoxTableCell<CompanyData, String>() {
                @Override
				public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty) {
                    	setText(null);
                    	setGraphic(null);
                    } else {
                    	// ComboBox項目を設定
                    	comboBox.getItems().clear();
                    	comboBox.getItems().addAll(getTableView().getItems().get(getIndex()).getKimatuYearList());
                    	comboBox.getSelectionModel().select(0);
                    	setText(null);
                    	setGraphic(comboBox);
                    }
                }
            };
            // CoboBox値変換リスナー
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                	String companyCode = newValue.getCode();
                	String op = newValue.getOption();
                	String[] codes = op.split(",");
                	String taxKindFlg = codes[0];
                	String taxAppFlg = codes[1];
                	String kimatuYear = codes[2];
                	String kimatuMonthDay = codes[3];
                	String companyName = codes[4];
                	String firstFlg = codes[5];
                	for (CompanyData data : companyDataTable.getItems()) {
                		if (companyCode.equals(data.getCompanyCode())) {
                			data.setCompanyName(companyName);
                			data.setTaxKindName(taxKindFlg);
                			data.setTaxAppName(taxAppFlg);
                			data.setTaxKindFlg(taxKindFlg);
                			data.setTaxAppFlg(taxAppFlg);
                			data.setKimatuYear(kimatuYear);
                			data.setKimatuMonthDay(kimatuMonthDay);
                			data.setAccountDate(kimatuYear, kimatuMonthDay);
                			data.setFirstFlg(firstFlg);
                		}
                	}
                }
            });
	        return cell;
		});

		showCompanyDataList();
		companySelectedFlg = false;
		registNextyearFlg = false;
	}

	private void showCompanyDataList() {
		List<CompanyData> dataList = z1Service.getCompanyDataList();
		final ObservableList<CompanyData> observableList = FXCollections.observableArrayList(dataList);
		companyDataTable.getItems().clear();
		companyDataTable.getItems().addAll(observableList);
	}

	@FXML
	private void onSelectCompany(Event eve) {
		companySelectedFlg = true;
		CompanyData selectData = companyDataTable.getSelectionModel().getSelectedItem();
		if (selectData != null && !StringUtils.isEmpty(selectData.getCompanyCode())) {
			// 会社情報共通変数に設定
			CompanyUtil.setCompanyUtil(selectData);
			log.info("会社選択結果：会社コード=" + selectData.getCompanyCode() + ", 会社名=" + selectData.getCompanyName() );

			//翌年度の会社マスタが存在するかを確認する
			CompanyMDto companyMDto = new CompanyMDto();
			companyMDto.setCompanyCode(selectData.getCompanyCode());
			Integer  nextKimatuYear = Integer.parseInt(selectData.getKimatuYear()) + 1;
			companyMDto.setKimatuYear(nextKimatuYear.toString());
			if(z1Service.getRegistNextYearData(companyMDto) > 0) {
				registNextyearFlg = true;
			}else {
				registNextyearFlg = false;
			}
			// ダイアログを閉じる
			((Node) eve.getSource()).getScene().getWindow().hide();
		}
	}

	@FXML
	private void onCompanyMainte(Event eve) {
		companySelectedFlg = false;
		registNextyearFlg = false;
		// ダイアログを閉じる
		((Node) eve.getSource()).getScene().getWindow().hide();
		// 新規登録プロセス画面を表示
		companyManageController.showScene();
		companyManageController.selectTab(0);
	}

}
