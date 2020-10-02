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
import com.busicomjp.sapp.model.d.TekiyoData;
import com.busicomjp.sapp.model.d.TekiyoSelectedData;
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
public class D4UpdateTekiyoDialogController implements Initializable {

	@Autowired
	private D4Service d4Service;
	@Autowired
	private D4Controller d4Controller;
	
	@FXML
	public TextField inputTekiyoName;
	@FXML
	public TextField inputTekiyoNameKana;
	@FXML
	public CheckBox checkbox1;
	@FXML
	public CheckBox checkbox2;
	@FXML
	public CheckBox checkbox3;
	@FXML
	public CheckBox checkbox4;
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
		inputTekiyoName.setText(TekiyoSelectedData.getTekiyoName());
		inputTekiyoNameKana.setText(TekiyoSelectedData.getTekiyoNameKana());

		if (!InputCheck.isNullOrBlank(TekiyoSelectedData.getDefaultAccountName())) {
			checkbox1.setSelected(true);
			List<TekiyoData> dataList = d4Service.getSelectedKindData(CompanyUtil.getCompanyCode(),"1",TekiyoSelectedData.getDefaultAccountName());
			if (dataList != null && dataList.size() > 0) {
				getKind1Data(defaultKind1Combo,dataList.get(0).getAccountKind1(),"1");
				getKind2Data(defaultKind2Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),"1");
				getKind3Data(defaultKind3Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),"1");
				getKind4Data(defaultKind4Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),"1");
				getKindData(defaultAccountCombo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),dataList.get(0).getAccountCode(),"1");
			}
		} else {
			defaultKind1Combo.setDisable(true);
			defaultKind2Combo.setDisable(true);
			defaultKind3Combo.setDisable(true);
			defaultKind4Combo.setDisable(true);
			defaultAccountCombo.setDisable(true);
		}
		
		if (!InputCheck.isNullOrBlank(TekiyoSelectedData.getSalesAccountName())) {
			checkbox2.setSelected(true);
			List<TekiyoData> dataList = d4Service.getSelectedKindData(CompanyUtil.getCompanyCode(),"2",TekiyoSelectedData.getSalesAccountName());
			if (dataList != null && dataList.size() > 0) {
				getKind1Data(salesKind1Combo,dataList.get(0).getAccountKind1(),"2");
				getKind2Data(salesKind2Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),"2");
				getKind3Data(salesKind3Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),"2");
				getKind4Data(salesKind4Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),"2");
				getKindData(salesAccountCombo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),dataList.get(0).getAccountCode(),"2");
			}
		} else {
			salesKind1Combo.setDisable(true);
			salesKind2Combo.setDisable(true);
			salesKind3Combo.setDisable(true);
			salesKind4Combo.setDisable(true);
			salesAccountCombo.setDisable(true);
		}
		
		if (!InputCheck.isNullOrBlank(TekiyoSelectedData.getMgmtAccountName())) {
			checkbox3.setSelected(true);
			List<TekiyoData> dataList = d4Service.getSelectedKindData(CompanyUtil.getCompanyCode(),"3",TekiyoSelectedData.getMgmtAccountName());
			if (dataList != null && dataList.size() > 0) {
				getKind1Data(mgmtKind1Combo,dataList.get(0).getAccountKind1(),"3");
				getKind2Data(mgmtKind2Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),"3");
				getKind3Data(mgmtKind3Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),"3");
				getKind4Data(mgmtKind4Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),"3");
				getKindData(mgmtAccountCombo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),dataList.get(0).getAccountCode(),"3");
			}
		} else {
			mgmtKind1Combo.setDisable(true);
			mgmtKind2Combo.setDisable(true);
			mgmtKind3Combo.setDisable(true);
			mgmtKind4Combo.setDisable(true);
			mgmtAccountCombo.setDisable(true);
		}
		
		if (!InputCheck.isNullOrBlank(TekiyoSelectedData.getCostAccountName())) {
			checkbox4.setSelected(true);
			List<TekiyoData> dataList = d4Service.getSelectedKindData(CompanyUtil.getCompanyCode(),"4",TekiyoSelectedData.getCostAccountName());
			if (dataList != null && dataList.size() > 0) {
				getKind1Data(costKind1Combo,dataList.get(0).getAccountKind1(),"4");
				getKind2Data(costKind2Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),"4");
				getKind3Data(costKind3Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),"4");
				getKind4Data(costKind4Combo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),"4");
				getKindData(costAccountCombo,dataList.get(0).getAccountKind1(),dataList.get(0).getAccountKind2(),dataList.get(0).getAccountKind3(),dataList.get(0).getAccountKind4(),dataList.get(0).getAccountCode(),"4");
			}
		} else {					
			costKind1Combo.setDisable(true);
			costKind2Combo.setDisable(true);
			costKind3Combo.setDisable(true);
			costKind4Combo.setDisable(true);
			costAccountCombo.setDisable(true);
		}

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
					if (InputCheck.isNullOrBlank(defaultKind1Combo.getValue())) {
						getDefaultKind1Data();
					}
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
					if (InputCheck.isNullOrBlank(salesKind1Combo.getValue())) {
						getSalesKind1Data();
					}
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
					if (InputCheck.isNullOrBlank(mgmtKind1Combo.getValue())) {
						getMgmtKind1Data();
					}
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
					if (InputCheck.isNullOrBlank(costKind1Combo.getValue())) {
						getCostKind1Data();
					}
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
	private void onUpdateTekiyo(Event eve) {
		// 入力チェック
		inputCheck();
		
		insertTekiyoM();
		
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
	 * 紐づけ勘定科目分類1取得
	 */
	protected void getKind1Data(ComboBox<ComboItem> combo,String cd,String kbn) {
		combo.setConverter(new ComboItemConverter());
		combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> accounList = d4Service.getKind1DataList(CompanyUtil.getCompanyCode(),kbn);

		// ComboBoxを設定
		if (accounList != null && accounList.size() > 0) {
			combo.getItems().clear();
			combo.getItems().addAll(accounList);
		}

		if (accounList != null && accounList.size() > 0) {
			for (int i = 0; i < accounList.size(); i++) {
				if (accounList.get(i).getCode().equals(cd)) {
					combo.getSelectionModel().select(i);
					break;
				}
			}
		}
	}

	/**
	 * 紐づけ勘定科目分類2取得
	 */
	protected void getKind2Data(ComboBox<ComboItem> combo,String cd1,String cd2,String kbn) {
		combo.setConverter(new ComboItemConverter());
		combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> accounList = d4Service.getKind2DataList(CompanyUtil.getCompanyCode(),cd1,kbn);

		// ComboBoxを設定
		if (accounList != null && accounList.size() > 0) {
			combo.getItems().clear();
			combo.getItems().addAll(accounList);
		}

		if (accounList != null && accounList.size() > 0) {
			for (int i = 0; i < accounList.size(); i++) {
				if (accounList.get(i).getCode().equals(cd2)) {
					combo.getSelectionModel().select(i);
					break;
				}
			}
		}
	}

	/**
	 * 紐づけ勘定科目分類3取得
	 */
	protected void getKind3Data(ComboBox<ComboItem> combo,String cd1,String cd2,String cd3,String kbn) {
		combo.setConverter(new ComboItemConverter());
		combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> accounList = d4Service.getKind3DataList(CompanyUtil.getCompanyCode(),cd1,cd2,kbn);

		// ComboBoxを設定
		if (accounList != null && accounList.size() > 0) {
			combo.getItems().clear();
			combo.getItems().addAll(accounList);
		}

		if (accounList != null && accounList.size() > 0) {
			for (int i = 0; i < accounList.size(); i++) {
				if (accounList.get(i).getCode().equals(cd3)) {
					combo.getSelectionModel().select(i);
					break;
				}
			}
		}
	}

	/**
	 * 紐づけ勘定科目分類4取得
	 */
	protected void getKind4Data(ComboBox<ComboItem> combo,String cd1,String cd2,String cd3,String cd4,String kbn) {
		combo.setConverter(new ComboItemConverter());
		combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> accounList = d4Service.getKind4DataList(CompanyUtil.getCompanyCode(),cd1,cd2,cd3,kbn);

		// ComboBoxを設定
		if (accounList != null && accounList.size() > 0) {
			combo.getItems().clear();
			combo.getItems().addAll(accounList);
		}

		if (accounList != null && accounList.size() > 0) {
			for (int i = 0; i < accounList.size(); i++) {
				if (accounList.get(i).getCode().equals(cd4)) {
					combo.getSelectionModel().select(i);
					break;
				}
			}
		}
	}

	/**
	 * 紐づけ勘定科目取得
	 */
	protected void getKindData(ComboBox<ComboItem> combo,String cd1,String cd2,String cd3,String cd4,String cd5,String kbn) {
		combo.setConverter(new ComboItemConverter());
		combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> accounList = d4Service.getKindDataList(CompanyUtil.getCompanyCode(),cd1,cd2,cd3,cd4,kbn);

		// ComboBoxを設定
		if (accounList != null && accounList.size() > 0) {
			combo.getItems().clear();
			combo.getItems().addAll(accounList);
		}

		if (accounList != null && accounList.size() > 0) {
			for (int i = 0; i < accounList.size(); i++) {
				if (accounList.get(i).getCode().equals(cd5)) {
					combo.getSelectionModel().select(i);
					break;
				}
			}
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
	
	private void insertTekiyoM() {
		
		List<TekiyoData> dataList = d4Service.getDataByselected(CompanyUtil.getCompanyCode(),TekiyoSelectedData.getTekiyoCode());
		
		String accountCd0 = "";
		String accountCd1 = "";
		String accountCd2 = "";
		String accountCd3 = "";
		
		String tekiyoCd = TekiyoSelectedData.getTekiyoCode();
		String tekiyoNm = TekiyoSelectedData.getTekiyoName();
		String tekiyoNmKana = TekiyoSelectedData.getTekiyoNameKana();
		
		boolean accountKind0Flg = false;
		boolean accountKind1Flg = false;
		boolean accountKind2Flg = false;
		boolean accountKind3Flg = false;
		
		if (dataList == null) {
		} else {
			for (int i = 0; i < dataList.size(); i++) {
				if("0".equals(dataList.get(i).getAccountKind())) {
					accountKind0Flg = true;
					accountCd0 = dataList.get(i).getAccountCode();
				}else if("1".equals(dataList.get(i).getAccountKind())) {
					accountKind1Flg = true;
					accountCd1 = dataList.get(i).getAccountCode();
				}else if("2".equals(dataList.get(i).getAccountKind())) {
					accountKind2Flg = true;
					accountCd2 = dataList.get(i).getAccountCode();
				}else if("3".equals(dataList.get(i).getAccountKind())) {
					accountKind3Flg = true;
					accountCd3 = dataList.get(i).getAccountCode();
				}
			}
			
			//通常
			if (checkbox1.isSelected()) {
				if (accountKind0Flg) {
					//更新
					if (accountCd0.equals(defaultAccountCombo.getValue().getCode()) && 
							tekiyoNm.equals(inputTekiyoName.getText().trim()) &&
									tekiyoNmKana.equals(inputTekiyoNameKana.getText().trim())) {
						throw new ValidationException("通常情報を変更してください。");
					} else {
						updateSummary(defaultAccountCombo.getValue().getCode(),tekiyoCd,"0");
					}
				} else {
					//登録
					insertSummary(defaultAccountCombo.getValue().getCode(),tekiyoCd,"0");
				}
			} else {
				if (accountKind0Flg) {
					//削除
					deleteSummary(accountCd0,tekiyoCd,"0");
				}
			}
			
			//販売
			if (checkbox2.isSelected()) {
				if (accountKind1Flg) {
					//更新
					if (accountCd1.equals(salesAccountCombo.getValue().getCode()) && 
							tekiyoNm.equals(inputTekiyoName.getText().trim()) &&
									tekiyoNmKana.equals(inputTekiyoNameKana.getText().trim())) {
						throw new ValidationException("販売情報を変更してください。");
					} else {
						updateSummary(salesAccountCombo.getValue().getCode(),tekiyoCd,"1");
					}
				} else {
					//登録
					insertSummary(salesAccountCombo.getValue().getCode(),tekiyoCd,"1");
				}
			} else {
				if (accountKind1Flg) {
					//削除
					deleteSummary(accountCd1,tekiyoCd,"1");
				}
			}
			
			//管理
			if (checkbox3.isSelected()) {
				if (accountKind2Flg) {
					//更新
					if (accountCd2.equals(mgmtAccountCombo.getValue().getCode()) && 
							tekiyoNm.equals(inputTekiyoName.getText().trim()) &&
									tekiyoNmKana.equals(inputTekiyoNameKana.getText().trim())) {
						throw new ValidationException("管理情報を変更してください。");
					} else {
						updateSummary(mgmtAccountCombo.getValue().getCode(),tekiyoCd,"2");
					}
				} else {
					//登録
					insertSummary(mgmtAccountCombo.getValue().getCode(),tekiyoCd,"2");
				}
			} else {
				if (accountKind2Flg) {
					//削除
					deleteSummary(accountCd2,tekiyoCd,"2");
				}
			}
			
			//原価
			if (checkbox4.isSelected()) {
				if (accountKind3Flg) {
					//更新
					if (accountCd3.equals(costAccountCombo.getValue().getCode()) && 
							tekiyoNm.equals(inputTekiyoName.getText().trim()) &&
									tekiyoNmKana.equals(inputTekiyoNameKana.getText().trim())) {
						throw new ValidationException("原価情報を変更してください。");
					} else {
						updateSummary(costAccountCombo.getValue().getCode(),tekiyoCd,"3");
					}
				} else {
					//登録
					insertSummary(costAccountCombo.getValue().getCode(),tekiyoCd,"3");
				}
			} else {
				if (accountKind3Flg) {
					//削除
					deleteSummary(accountCd3,tekiyoCd,"3");
				}
			}
		}
	}
	
	//摘要マスタ登録
	private void insertSummary(String selectedAccountCode,String tekiyoCd,String accountKind) {
		d4Service.insertSummary(CompanyUtil.getCompanyCode(),tekiyoCd,
				inputTekiyoName.getText().trim(),inputTekiyoNameKana.getText().trim(),accountKind,
				selectedAccountCode);
	}
	
	//摘要マスタ更新
	private void updateSummary(String selectedAccountCode,String tekiyoCd,String accountKind) {
		d4Service.updateSummary(CompanyUtil.getCompanyCode(),tekiyoCd,
				inputTekiyoName.getText().trim(),inputTekiyoNameKana.getText().trim(),accountKind,
				selectedAccountCode);
	}
	
	//摘要マスタ削除
	private void deleteSummary(String accountCode,String tekiyoCd,String accountKind) {
		d4Service.deleteSummary(CompanyUtil.getCompanyCode(),accountCode,tekiyoCd,accountKind);
	}
}
