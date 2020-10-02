package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.service.d.D4Service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.Getter;

@Component
public class D4RegistTekiyoDialogController extends BaseController implements Initializable {

	@Autowired
	private D4Service d4Service;
	@Autowired
	private D4Controller d4Controller;

	@FXML
	public CheckBox checkbox1;
	@FXML
	public CheckBox checkbox2;
	@FXML
	public CheckBox checkbox3;
	@FXML
	public CheckBox checkbox4;
	@FXML
	public TextField inputTekiyoName;
	@FXML
	public TextField inputTekiyoNameKana;
	@FXML
	public ComboBox<ComboItem> defaultKind1Combo;
	@FXML
	public ComboBox<ComboItem> defaultKind2Combo;
	@FXML
	public ComboBox<ComboItem> defaultKind3Combo;
	@FXML
	public ComboBox<ComboItem> defaultKind4Combo;
	@FXML
	public ComboBox<ComboItem> defaultAccountCombo;
	@FXML
	public ComboBox<ComboItem> salesKind1Combo;
	@FXML
	public ComboBox<ComboItem> salesKind2Combo;
	@FXML
	public ComboBox<ComboItem> salesKind3Combo;
	@FXML
	public ComboBox<ComboItem> salesKind4Combo;
	@FXML
	public ComboBox<ComboItem> salesAccountCombo;
	@FXML
	public ComboBox<ComboItem> mgmtKind1Combo;
	@FXML
	public ComboBox<ComboItem> mgmtKind2Combo;
	@FXML
	public ComboBox<ComboItem> mgmtKind3Combo;
	@FXML
	public ComboBox<ComboItem> mgmtKind4Combo;
	@FXML
	public ComboBox<ComboItem> mgmtAccountCombo;
	@FXML
	public ComboBox<ComboItem> costKind1Combo;
	@FXML
	public ComboBox<ComboItem> costKind2Combo;
	@FXML
	public ComboBox<ComboItem> costKind3Combo;
	@FXML
	public ComboBox<ComboItem> costKind4Combo;
	@FXML
	public ComboBox<ComboItem> costAccountCombo;
	
	@Getter
	List<ComboItem> accounList = new ArrayList<ComboItem>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 初期化非活性
		defaultKind1Combo.setDisable(true);
		defaultKind2Combo.setDisable(true);
		defaultKind3Combo.setDisable(true);
		defaultKind4Combo.setDisable(true);
		defaultAccountCombo.setDisable(true);
		salesKind1Combo.setDisable(true);
		salesKind2Combo.setDisable(true);
		salesKind3Combo.setDisable(true);
		salesKind4Combo.setDisable(true);
		salesAccountCombo.setDisable(true);
		mgmtKind1Combo.setDisable(true);
		mgmtKind2Combo.setDisable(true);
		mgmtKind3Combo.setDisable(true);
		mgmtKind4Combo.setDisable(true);
		mgmtAccountCombo.setDisable(true);
		costKind1Combo.setDisable(true);
		costKind2Combo.setDisable(true);
		costKind3Combo.setDisable(true);
		costKind4Combo.setDisable(true);
		costAccountCombo.setDisable(true);

		// 各リスト内容を編集
		defaultKind1Combo.setConverter(new ComboItemConverter());
		defaultKind2Combo.setConverter(new ComboItemConverter());
		defaultKind3Combo.setConverter(new ComboItemConverter());
		defaultKind4Combo.setConverter(new ComboItemConverter());
		defaultAccountCombo.setConverter(new ComboItemConverter());
		salesKind1Combo.setConverter(new ComboItemConverter());
		salesKind2Combo.setConverter(new ComboItemConverter());
		salesKind3Combo.setConverter(new ComboItemConverter());
		salesKind4Combo.setConverter(new ComboItemConverter());
		salesAccountCombo.setConverter(new ComboItemConverter());
		mgmtKind1Combo.setConverter(new ComboItemConverter());
		mgmtKind2Combo.setConverter(new ComboItemConverter());
		mgmtKind3Combo.setConverter(new ComboItemConverter());
		mgmtKind4Combo.setConverter(new ComboItemConverter());
		mgmtAccountCombo.setConverter(new ComboItemConverter());
		costKind1Combo.setConverter(new ComboItemConverter());
		costKind2Combo.setConverter(new ComboItemConverter());
		costKind3Combo.setConverter(new ComboItemConverter());
		costKind4Combo.setConverter(new ComboItemConverter());
		costAccountCombo.setConverter(new ComboItemConverter());

		checkbox1.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				if (checkbox1.isSelected()) {
					defaultKind1Combo.setDisable(false);
					defaultKind2Combo.setDisable(false);
					defaultKind3Combo.setDisable(false);
					defaultKind4Combo.setDisable(false);
					defaultAccountCombo.setDisable(false);
					
					// 分類取得
					getDefaultKind1Data();
				} else if (!checkbox1.isSelected()) {
					defaultKind1Combo.setDisable(true);
					defaultKind2Combo.setDisable(true);
					defaultKind3Combo.setDisable(true);
					defaultKind4Combo.setDisable(true);
					defaultAccountCombo.setDisable(true);
				}
			}
		});
		checkbox2.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (checkbox2.isSelected()) {
					salesKind1Combo.setDisable(false);
					salesKind2Combo.setDisable(false);
					salesKind3Combo.setDisable(false);
					salesKind4Combo.setDisable(false);
					salesAccountCombo.setDisable(false);
					
					// 分類取得
					getSalesKind1Data();
				} else if (!checkbox2.isSelected()) {
					salesKind1Combo.setDisable(true);
					salesKind2Combo.setDisable(true);
					salesKind3Combo.setDisable(true);
					salesKind4Combo.setDisable(true);
					salesAccountCombo.setDisable(true);
				}
			}
		});
		checkbox3.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (checkbox3.isSelected()) {
					mgmtKind1Combo.setDisable(false);
					mgmtKind2Combo.setDisable(false);
					mgmtKind3Combo.setDisable(false);
					mgmtKind4Combo.setDisable(false);
					mgmtAccountCombo.setDisable(false);
					
					// 分類取得
					getMgmtKind1Data();
				} else if (!checkbox3.isSelected()) {
					mgmtKind1Combo.setDisable(true);
					mgmtKind2Combo.setDisable(true);
					mgmtKind3Combo.setDisable(true);
					mgmtKind4Combo.setDisable(true);
					mgmtAccountCombo.setDisable(true);
				}
			}
		});
		checkbox4.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (checkbox4.isSelected()) {
					costKind1Combo.setDisable(false);
					costKind2Combo.setDisable(false);
					costKind3Combo.setDisable(false);
					costKind4Combo.setDisable(false);
					costAccountCombo.setDisable(false);
					
					// 分類取得
					getCostKind1Data();
				} else if (!checkbox4.isSelected()) {
					costKind1Combo.setDisable(true);
					costKind2Combo.setDisable(true);
					costKind3Combo.setDisable(true);
					costKind4Combo.setDisable(true);
					costAccountCombo.setDisable(true);
				}
			}
		});
	}
	
	@FXML
	private void onRegistTekiyo(Event eve) {
		
		// 入力チェック
		inputCheck();
		String tekiyoCd= getUniqueCode("3");
		//デフォルト
		if (checkbox1.isSelected()) {
			insertSummary(defaultAccountCombo.getValue().getCode(),tekiyoCd,"0");
		}
		
		//販売
		if(checkbox2.isSelected()) {
			insertSummary(salesAccountCombo.getValue().getCode(),tekiyoCd,"1");
		}
		
		//管理
		if(checkbox3.isSelected()) {
			insertSummary(mgmtAccountCombo.getValue().getCode(),tekiyoCd,"2");
		}
		
		//原価
		if(checkbox4.isSelected()) {
			insertSummary(costAccountCombo.getValue().getCode(),tekiyoCd,"3");
		}
		
		// ダイアログを閉じる
        ((Node) eve.getSource()).getScene().getWindow().hide();
        
        // 再表示
        d4Controller.onSearchData();
		
	}

	private void inputCheck() {
		
		if (!checkbox1.isSelected() && !checkbox2.isSelected() && !checkbox3.isSelected() && !checkbox4.isSelected()) {
			throw new ValidationException("科目区分を指定してください。");
		}
		
		// 摘要名称
		if (InputCheck.isNullOrBlank(inputTekiyoName.getText())) {
			throw new ValidationException("摘要名称を指定してください。");
		}
		if (inputTekiyoName.getText().trim().length() > 100) {
			throw new ValidationException("摘要名称は100文字以内で指定してください。");
		}

		// 摘要名称かな
		if (InputCheck.isNullOrBlank(inputTekiyoNameKana.getText())) {
			throw new ValidationException("摘要科目名称(かな)を指定してください。");
		}
		if (inputTekiyoNameKana.getText().trim().length() > 100) {
			throw new ValidationException("摘要科目名称(かな)は100文字以内で指定してください。");
		}

		// 全角
		if(!InputCheck.isZengaku(inputTekiyoNameKana.getText().trim())) {
				throw new ValidationException("摘要名称(かな)は全角ひらがな、全角数字で指定してください。");
		}
		
		if (checkbox1.isSelected() && (defaultKind1Combo.getValue() == null || defaultKind2Combo.getValue() == null || 
				defaultKind3Combo.getValue() == null || defaultKind4Combo.getValue() == null || defaultAccountCombo.getValue() == null)) {
			throw new ValidationException("紐づけ勘定科目(通常)を指定してください。");
		}
		
		if (checkbox2.isSelected() && (salesKind1Combo.getValue() == null || salesKind2Combo.getValue() == null || 
				salesKind3Combo.getValue() == null || salesKind4Combo.getValue() == null || salesAccountCombo.getValue() == null)) {
			throw new ValidationException("紐づけ勘定科目(販売)を指定してください。");
		}
		
		if (checkbox3.isSelected() && (mgmtKind1Combo.getValue() == null || mgmtKind2Combo.getValue() == null || 
				mgmtKind3Combo.getValue() == null || mgmtKind4Combo.getValue() == null || mgmtAccountCombo.getValue() == null)) {
			throw new ValidationException("紐づけ勘定科目(管理)を指定してください。");
		}
		
		if (checkbox4.isSelected() && (costKind1Combo.getValue() == null || costKind2Combo.getValue() == null || 
				costKind3Combo.getValue() == null || costKind4Combo.getValue() == null || costAccountCombo.getValue() == null)) {
			throw new ValidationException("紐づけ勘定科目(原価)を指定してください。");
		}
	}

	/**
	 * 紐づけ勘定科目(通常)分類1取得
	 */
	protected void getDefaultKind1Data() {
		defaultKind1Combo.setConverter(new ComboItemConverter());
		defaultKind1Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind1DataList(CompanyUtil.getCompanyCode(),"1");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		defaultKind1Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(通常)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect11Enter() {
		if (defaultKind1Combo == null || defaultKind1Combo.getValue() == null) {
			return;
		}
		defaultKind2Combo.getItems().clear();
		defaultKind3Combo.getItems().clear();
		defaultKind4Combo.getItems().clear();
		defaultAccountCombo.getItems().clear();
		String selectCode = defaultKind1Combo.getValue().getCode();
		
		getDefaultKind2Combo(selectCode);

	}
	
	/**
	 * 分類2取得
	 */
	private void getDefaultKind2Combo(String accountKind1) {
		defaultKind2Combo.setConverter(new ComboItemConverter());
		defaultKind2Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind2DataList(CompanyUtil.getCompanyCode(), accountKind1,"1");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		defaultKind2Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(通常)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect12Enter() {
		if (defaultKind2Combo == null || defaultKind2Combo.getValue() == null) {
			return;
		}
		defaultKind3Combo.getItems().clear();
		defaultKind4Combo.getItems().clear();
		defaultAccountCombo.getItems().clear();
		String selectCode = defaultKind1Combo.getValue().getCode();
		String selectCode2 = defaultKind2Combo.getValue().getCode();
		
		getDefaultKind3Combo(selectCode,selectCode2);

	}
	
	/**
	 * 分類3取得
	 */
	private void getDefaultKind3Combo(String accountKind1,String accountKind2) {
		defaultKind3Combo.setConverter(new ComboItemConverter());
		defaultKind3Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind3DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,"1");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		defaultKind3Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(通常)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect13Enter() {
		if (defaultKind3Combo == null || defaultKind3Combo.getValue() == null) {
			return;
		}
		defaultKind4Combo.getItems().clear();
		defaultAccountCombo.getItems().clear();
		String selectCode = defaultKind1Combo.getValue().getCode();
		String selectCode2 = defaultKind2Combo.getValue().getCode();
		String selectCode3 = defaultKind3Combo.getValue().getCode();
		
		getDefaultKind4Combo(selectCode,selectCode2,selectCode3);

	}
	
	/**
	 * 分類4取得
	 */
	private void getDefaultKind4Combo(String accountKind1,String accountKind2,String accountKind3) {
		defaultKind4Combo.setConverter(new ComboItemConverter());
		defaultKind4Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind4DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,"1");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		defaultKind4Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(通常)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect14Enter() {
		if (defaultKind4Combo == null || defaultKind4Combo.getValue() == null) {
			return;
		}
		defaultAccountCombo.getItems().clear();
		String selectCode = defaultKind1Combo.getValue().getCode();
		String selectCode2 = defaultKind2Combo.getValue().getCode();
		String selectCode3 = defaultKind3Combo.getValue().getCode();
		String selectCode4 = defaultKind4Combo.getValue().getCode();
		
		getDefaultKind5Combo(selectCode,selectCode2,selectCode3,selectCode4);

	}
	/**
	 * 分類5取得
	 */
	private void getDefaultKind5Combo(String accountKind1,String accountKind2,String accountKind3,String accountKind4) {
		defaultAccountCombo.setConverter(new ComboItemConverter());
		defaultAccountCombo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKindDataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,accountKind4,"1");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		defaultAccountCombo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(販売)分類1取得
	 */
	protected void getSalesKind1Data() {
		salesKind1Combo.setConverter(new ComboItemConverter());
		salesKind1Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind1DataList(CompanyUtil.getCompanyCode(),"2");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		salesKind1Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(販売)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect21Enter() {
		if (salesKind1Combo == null || salesKind1Combo.getValue() == null) {
			return;
		}
		salesKind2Combo.getItems().clear();
		salesKind3Combo.getItems().clear();
		salesKind4Combo.getItems().clear();
		salesAccountCombo.getItems().clear();
		String selectCode = salesKind1Combo.getValue().getCode();
		
		getSalesKind2Combo(selectCode);

	}
	
	/**
	 * 分類2取得
	 */
	private void getSalesKind2Combo(String accountKind1) {
		salesKind2Combo.setConverter(new ComboItemConverter());
		salesKind2Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind2DataList(CompanyUtil.getCompanyCode(), accountKind1,"2");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		salesKind2Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(販売)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect22Enter() {
		if (salesKind2Combo == null || salesKind2Combo.getValue() == null) {
			return;
		}
		salesKind3Combo.getItems().clear();
		salesKind4Combo.getItems().clear();
		salesAccountCombo.getItems().clear();
		String selectCode = salesKind1Combo.getValue().getCode();
		String selectCode2 = salesKind2Combo.getValue().getCode();
		
		getSalesKind3Combo(selectCode,selectCode2);

	}
	
	/**
	 * 分類3取得
	 */
	private void getSalesKind3Combo(String accountKind1,String accountKind2) {
		salesKind3Combo.setConverter(new ComboItemConverter());
		salesKind3Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind3DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,"2");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		salesKind3Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(販売)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect23Enter() {
		if (salesKind3Combo == null || salesKind3Combo.getValue() == null) {
			return;
		}
		salesKind4Combo.getItems().clear();
		salesAccountCombo.getItems().clear();
		String selectCode = salesKind1Combo.getValue().getCode();
		String selectCode2 = salesKind2Combo.getValue().getCode();
		String selectCode3 = salesKind3Combo.getValue().getCode();
		
		getSalesKind4Combo(selectCode,selectCode2,selectCode3);

	}
	
	/**
	 * 分類4取得
	 */
	private void getSalesKind4Combo(String accountKind1,String accountKind2,String accountKind3) {
		salesKind4Combo.setConverter(new ComboItemConverter());
		salesKind4Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind4DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,"2");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		salesKind4Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(販売)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect24Enter() {
		if (salesKind4Combo == null || salesKind4Combo.getValue() == null) {
			return;
		}
		salesAccountCombo.getItems().clear();
		String selectCode = salesKind1Combo.getValue().getCode();
		String selectCode2 = salesKind2Combo.getValue().getCode();
		String selectCode3 = salesKind3Combo.getValue().getCode();
		String selectCode4 = salesKind4Combo.getValue().getCode();
		
		getSalesKind5Combo(selectCode,selectCode2,selectCode3,selectCode4);

	}
	
	/**
	 * 分類5取得
	 */
	private void getSalesKind5Combo(String accountKind1,String accountKind2,String accountKind3,String accountKind4) {
		salesAccountCombo.setConverter(new ComboItemConverter());
		salesAccountCombo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKindDataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,accountKind4,"2");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		salesAccountCombo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(管理)分類1取得
	 */
	protected void getMgmtKind1Data() {
		mgmtKind1Combo.setConverter(new ComboItemConverter());
		mgmtKind1Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind1DataList(CompanyUtil.getCompanyCode(),"3");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		mgmtKind1Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(管理)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect31Enter() {
		if (mgmtKind1Combo == null || mgmtKind1Combo.getValue() == null) {
			return;
		}
		mgmtKind2Combo.getItems().clear();
		mgmtKind3Combo.getItems().clear();
		mgmtKind4Combo.getItems().clear();
		mgmtAccountCombo.getItems().clear();
		String selectCode = mgmtKind1Combo.getValue().getCode();
		
		getMgmtKind2Combo(selectCode);

	}
	
	/**
	 * 分類2取得
	 */
	private void getMgmtKind2Combo(String accountKind1) {
		mgmtKind2Combo.setConverter(new ComboItemConverter());
		mgmtKind2Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind2DataList(CompanyUtil.getCompanyCode(), accountKind1,"3");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		mgmtKind2Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(管理)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect32Enter() {
		if (mgmtKind2Combo == null || mgmtKind2Combo.getValue() == null) {
			return;
		}
		mgmtKind3Combo.getItems().clear();
		mgmtKind4Combo.getItems().clear();
		mgmtAccountCombo.getItems().clear();
		String selectCode = mgmtKind1Combo.getValue().getCode();
		String selectCode2 = mgmtKind2Combo.getValue().getCode();
		
		getMgmtKind3Combo(selectCode,selectCode2);

	}
	
	/**
	 * 分類3取得
	 */
	private void getMgmtKind3Combo(String accountKind1,String accountKind2) {
		mgmtKind3Combo.setConverter(new ComboItemConverter());
		mgmtKind3Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind3DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,"3");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		mgmtKind3Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(管理)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect33Enter() {
		if (mgmtKind3Combo == null || mgmtKind3Combo.getValue() == null) {
			return;
		}
		mgmtKind4Combo.getItems().clear();
		mgmtAccountCombo.getItems().clear();
		String selectCode = mgmtKind1Combo.getValue().getCode();
		String selectCode2 = mgmtKind2Combo.getValue().getCode();
		String selectCode3 = mgmtKind3Combo.getValue().getCode();
		
		getMgmtKind4Combo(selectCode,selectCode2,selectCode3);

	}
	
	/**
	 * 分類4取得
	 */
	private void getMgmtKind4Combo(String accountKind1,String accountKind2,String accountKind3) {
		mgmtKind4Combo.setConverter(new ComboItemConverter());
		mgmtKind4Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind4DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,"3");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		mgmtKind4Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(管理)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect34Enter() {
		if (mgmtKind4Combo == null || mgmtKind4Combo.getValue() == null) {
			return;
		}
		mgmtAccountCombo.getItems().clear();
		String selectCode = mgmtKind1Combo.getValue().getCode();
		String selectCode2 = mgmtKind2Combo.getValue().getCode();
		String selectCode3 = mgmtKind3Combo.getValue().getCode();
		String selectCode4 = mgmtKind4Combo.getValue().getCode();
		
		getMgmtKind5Combo(selectCode,selectCode2,selectCode3,selectCode4);

	}
	
	/**
	 * 分類5取得
	 */
	private void getMgmtKind5Combo(String accountKind1,String accountKind2,String accountKind3,String accountKind4) {
		mgmtAccountCombo.setConverter(new ComboItemConverter());
		mgmtAccountCombo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKindDataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,accountKind4,"3");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		mgmtAccountCombo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(原価)分類1取得
	 */
	protected void getCostKind1Data() {
		costKind1Combo.setConverter(new ComboItemConverter());
		costKind1Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind1DataList(CompanyUtil.getCompanyCode(),"4");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		costKind1Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(原価)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect41Enter() {
		if (costKind1Combo == null || costKind1Combo.getValue() == null) {
			return;
		}
		costKind2Combo.getItems().clear();
		costKind3Combo.getItems().clear();
		costKind4Combo.getItems().clear();
		costAccountCombo.getItems().clear();
		String selectCode = costKind1Combo.getValue().getCode();
		
		getcostKind2Combo(selectCode);

	}
	
	/**
	 * 分類2取得
	 */
	private void getcostKind2Combo(String accountKind1) {
		costKind2Combo.setConverter(new ComboItemConverter());
		costKind2Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind2DataList(CompanyUtil.getCompanyCode(), accountKind1,"4");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		costKind2Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(原価)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect42Enter() {
		if (costKind2Combo == null || costKind2Combo.getValue() == null) {
			return;
		}
		costKind3Combo.getItems().clear();
		costKind4Combo.getItems().clear();
		costAccountCombo.getItems().clear();
		String selectCode = costKind1Combo.getValue().getCode();
		String selectCode2 = costKind2Combo.getValue().getCode();
		
		getcostKind3Combo(selectCode,selectCode2);

	}
	
	/**
	 * 分類3取得
	 */
	private void getcostKind3Combo(String accountKind1,String accountKind2) {
		costKind3Combo.setConverter(new ComboItemConverter());
		costKind3Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind3DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,"4");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		costKind3Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(原価)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect43Enter() {
		if (costKind3Combo == null || costKind3Combo.getValue() == null) {
			return;
		}
		costKind4Combo.getItems().clear();
		costAccountCombo.getItems().clear();
		String selectCode = costKind1Combo.getValue().getCode();
		String selectCode2 = costKind2Combo.getValue().getCode();
		String selectCode3 = costKind3Combo.getValue().getCode();
		
		getcostKind4Combo(selectCode,selectCode2,selectCode3);

	}
	
	/**
	 * 分類4取得
	 */
	private void getcostKind4Combo(String accountKind1,String accountKind2,String accountKind3) {
		costKind4Combo.setConverter(new ComboItemConverter());
		costKind4Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKind4DataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,"4");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		costKind4Combo.getItems().addAll(getAccounList());
	}
	
	/**
	 * 紐づけ勘定科目(原価)分類1Combo選択イベント
	 */
	@FXML
	protected void onSelect44Enter() {
		if (costKind4Combo == null || costKind4Combo.getValue() == null) {
			return;
		}
		costAccountCombo.getItems().clear();
		String selectCode = costKind1Combo.getValue().getCode();
		String selectCode2 = costKind2Combo.getValue().getCode();
		String selectCode3 = costKind3Combo.getValue().getCode();
		String selectCode4 = costKind4Combo.getValue().getCode();
		
		getCostKind5Combo(selectCode,selectCode2,selectCode3,selectCode4);

	}
	
	/**
	 * 分類5取得
	 */
	private void getCostKind5Combo(String accountKind1,String accountKind2,String accountKind3,String accountKind4) {
		costAccountCombo.setConverter(new ComboItemConverter());
		costAccountCombo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = d4Service.getKindDataList(CompanyUtil.getCompanyCode(), accountKind1,accountKind2,accountKind3,accountKind4,"4");
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		costAccountCombo.getItems().addAll(getAccounList());
	}
	
	//摘要マスタ登録
	private void insertSummary(String selectedAccountCode,String tekiyoCd,String accountKind) {
		d4Service.insertSummary(CompanyUtil.getCompanyCode(),tekiyoCd,
				inputTekiyoName.getText().trim(),inputTekiyoNameKana.getText().trim(),accountKind,
				selectedAccountCode);
	}
	
	//既存デフォルト登録の摘要コードと区分できるようにするコードを採番する
	private String getUniqueCode(String ketaNum) {

		// DBから取得した最大勘定科目コード
		String maxDBAccountCode = "";
		int numberKeta = 0;
		String zeroNum = "";
		
		//勘定科目コード、DBに３桁、１桁目はアルファベットを除いて、２桁からの桁数で判断
		if ("2".equals(ketaNum)) {
			//maxDBAccountCode = d4Service.getMaxAccountCd(CompanyUtil.getCompanyCode());
			numberKeta = 99;
			zeroNum = "00";
		//摘要コード
		}else {
			maxDBAccountCode = d4Service.getMaxSummaryCd(CompanyUtil.getCompanyCode());
			numberKeta = 999;
			zeroNum = "000";
		}
		
		String first = maxDBAccountCode.substring(0,1);
		String second = maxDBAccountCode.substring(1);
		
		String maxCodeFinal = "";
		String maxCd = "";
		
		if (first.matches("^[A-Z]+$")) {
			if (Integer.parseInt(second) == numberKeta) {
				switch(first) {
					case "A":
						maxCodeFinal = "B";
						break;
					case "B":
						maxCodeFinal = "C";
						break;
					case "C":
						maxCodeFinal = "D";
						break;
					case "D":
						maxCodeFinal = "E";
						break;
					case "E":
						maxCodeFinal = "F";
						break;
					case "F":
						maxCodeFinal = "G";
						break;
					case "G":
						maxCodeFinal = "H";
						break;
					case "H":
						maxCodeFinal = "I";
						break;
					case "I":
						maxCodeFinal = "J";
						break;
					case "J":
						maxCodeFinal = "K";
						break;
					case "K":
						maxCodeFinal = "L";
						break;
					case "L":
						maxCodeFinal = "M";
						break;
					case "M":
						maxCodeFinal = "N";
						break;
					case "N":
						maxCodeFinal = "O";
						break;
					case "O":
						maxCodeFinal = "P";
						break;
					case "P":
						maxCodeFinal = "Q";
						break;
					case "Q":
						maxCodeFinal = "R";
						break;
					case "R":
						maxCodeFinal = "S";
						break;
					case "S":
						maxCodeFinal = "T";
						break;
					case "T":
						maxCodeFinal = "U";
						break;
					case "U":
						maxCodeFinal = "V";
						break;
					case "V":
						maxCodeFinal = "W";
						break;
					case "W":
						maxCodeFinal = "X";
						break;
					case "X":
						maxCodeFinal = "Y";
						break;
					case "Y":
						maxCodeFinal = "Z";
						break;
					case "Z":
						maxCodeFinal = "a";
						break;
				}
				
				maxCodeFinal = maxCodeFinal.concat(zeroNum);
				
			}else {
				int maxCode = Integer.parseInt(second) + 1;
				if ("2".equals(ketaNum)) {
					maxCd = String.format("%02d", maxCode);
				}else {
					maxCd = String.format("%03d", maxCode);
				}
				maxCodeFinal = first.concat(maxCd);
			}
		}else {

			if ("2".equals(ketaNum)) {
				maxCodeFinal = "A00";
			}else {
				maxCodeFinal = "A000";
			}
		}
		
		return maxCodeFinal;
	}
}
