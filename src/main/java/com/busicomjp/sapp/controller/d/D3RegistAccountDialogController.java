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
import com.busicomjp.sapp.service.d.D3Service;
import com.busicomjp.sapp.service.e.E1Service;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;

@Component
public class D3RegistAccountDialogController extends BaseController implements Initializable {
	
	@Autowired
	private E1Service e1Service;
	@Autowired
	D3Service d3Service;
	@Autowired
	private D3Controller d3Controller;
	
	@FXML
	public ComboBox<ComboItem> accountKind1Combo;
	@FXML
	public ComboBox<ComboItem> accountKind2Combo;
	@FXML
	public ComboBox<ComboItem> accountKind3Combo;
	@FXML
	public ComboBox<ComboItem> accountKind4Combo;
	@FXML
	public Label kindFlg; 
	@FXML
	public TextField inputAccountName;
	@FXML
	public TextField inputAccountNameKana;
	
	@Getter
	List<ComboItem> accounList = new ArrayList<ComboItem>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 画面項目初期化
		accountKind1Combo.getItems().clear();
		accountKind2Combo.getItems().clear();
		accountKind3Combo.getItems().clear();
		accountKind4Combo.getItems().clear();

		// 分類取得
		getAccountKind1Data();
	}
	
	@FXML
	private void onRegistAccount(Event eve) {
		if (commonAlert.showConfirmationAlert("登録処理を実行しますか？")) {
			// 入力チェック
			inputCheck();
			// 新規採番された勘定科目コード取得
			String accountCode = getUniqueCode("2");
			// 勘定科目マスタ登録
			insertAccountMaster(accountCode);
			// 摘要マスタ登録
			insertSummaryMaster(accountCode);
			// ダイアログを閉じる
			((Node) eve.getSource()).getScene().getWindow().hide();
			// 再表示
			d3Controller.onSearchData();
		}
	}

	private void inputCheck() {
		
		if(accountKind1Combo.getValue() == null) {
			throw new ValidationException("分類1を指定してください。");
		}
		
		if(accountKind2Combo.getValue() == null) {
			throw new ValidationException("分類2を指定してください。");
		}
		
		if(accountKind3Combo.getValue() == null) {
			throw new ValidationException("分類3を指定してください。");
		}
		
		if(accountKind4Combo.getValue() == null) {
			throw new ValidationException("分類4を指定してください。");
		}
		
		// 勘定科目名称
		if (InputCheck.isNullOrBlank(inputAccountName.getText())) {
			throw new ValidationException("勘定科目名称を指定してください。");
		}
		if (inputAccountName.getText().trim().length() > 100) {
			throw new ValidationException("勘定科目名称は100文字以内で指定してください。");
		}

		// 勘定科目名称かな
		if (InputCheck.isNullOrBlank(inputAccountNameKana.getText())) {
			throw new ValidationException("勘定科目名称(かな)を指定してください。");
		}
		if (inputAccountNameKana.getText().trim().length() > 100) {
			throw new ValidationException("勘定科目名称(かな)は100文字以内で指定してください。");
		}

		// 全角
		if(!InputCheck.isZengaku(inputAccountNameKana.getText().trim())) {
				throw new ValidationException("勘定科目名称(かな)は全角ひらがな、全角数字で指定してください。");
		}
	}
	
	/**
	 * 分類1Combo選択イベント
	 */
	@FXML
	protected void onSelectEnter() {
		if (accountKind1Combo == null || accountKind1Combo.getValue() == null) {
			return;
		}

		accountKind2Combo.getItems().clear();
		accountKind3Combo.getItems().clear();
		accountKind4Combo.getItems().clear();
		String selectCode = accountKind1Combo.getValue().getCode();
		
		getAccountKind2Data(selectCode);

	}

	/**
	 * 分類2Combo選択イベント
	 */
	@FXML
	protected void onSelect2Enter() {
		if (accountKind2Combo == null || accountKind2Combo.getValue() == null) {
			return;
		}

		String selectCode = accountKind1Combo.getValue().getCode();
		String selectCode2 = accountKind2Combo.getValue().getCode();
		accountKind3Combo.getItems().clear();
		accountKind4Combo.getItems().clear();
		
		getAccountKind3Data(selectCode, selectCode2);

	}

	/**
	 * 分類3Combo選択イベント
	 */
	@FXML
	protected void onSelect3Enter() {
		if (accountKind3Combo == null || accountKind3Combo.getValue() == null) {
			return;
		}

		String selectCode = accountKind1Combo.getValue().getCode();
		String selectCode2 = accountKind2Combo.getValue().getCode();
		String selectCode3 = accountKind3Combo.getValue().getCode();
		accountKind4Combo.getItems().clear();

		getAccountKind4Data(selectCode, selectCode2, selectCode3);

	}

	/**
	 * 分類4Combo選択イベント
	 */
	@FXML
	protected void onSelect4Enter() {
		if (accountKind4Combo == null || accountKind4Combo.getValue() == null) {
			return;
		}

		String selectCode = accountKind1Combo.getValue().getCode();
		String selectCode2 = accountKind2Combo.getValue().getCode();
		String selectCode3 = accountKind3Combo.getValue().getCode();
		String selectCode4 = accountKind4Combo.getValue().getCode();

		String kindKbn = d3Service.getKindKbn(CompanyUtil.getCompanyCode(),selectCode,selectCode2,selectCode3,selectCode4);
		
		if("1".equals(kindKbn)) {
			kindFlg.setText("借方");
		}else if("2".equals(kindKbn)) {
			kindFlg.setText("貸方");
		}else {
			kindFlg.setText("");
		}
		
	}

	/**
	 * 分類1取得
	 */
	protected void getAccountKind1Data() {
		accountKind1Combo.setConverter(new ComboItemConverter());
		accountKind1Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind1DataList(CompanyUtil.getCompanyCode());
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind1Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 分類2取得
	 */
	private void getAccountKind2Data(String accountKind1) {
		accountKind2Combo.setConverter(new ComboItemConverter());
		accountKind2Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind2DataList(CompanyUtil.getCompanyCode(), accountKind1);
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind2Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 分類3取得
	 */
	private void getAccountKind3Data(String accountKind1, String accountKind2) {
		accountKind3Combo.setConverter(new ComboItemConverter());
		accountKind3Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind3DataList(CompanyUtil.getCompanyCode(), accountKind1,
				accountKind2);
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind3Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 分類4取得
	 */
	private void getAccountKind4Data(String accountKind1, String accountKind2, String accountKind3) {
		accountKind4Combo.setConverter(new ComboItemConverter());
		accountKind4Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind4DataList(CompanyUtil.getCompanyCode(), accountKind1,
				accountKind2, accountKind3);
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind4Combo.getItems().addAll(getAccounList());
	}

	
	//勘定科目コード登録
	private void insertAccountMaster(String accountCd) {

		String kindFlag = "";
		
		if ("借方".equals(kindFlg.getText())){
			kindFlag = "1";
		}else if ("貸方".equals(kindFlg.getText())) {
			kindFlag = "2";
		}
		
		d3Service.insertAccount(CompanyUtil.getCompanyCode(),
								accountKind1Combo.getValue().getCode(),
								accountKind1Combo.getValue().getName(),
								accountKind2Combo.getValue().getCode(),
								accountKind2Combo.getValue().getName(),
								accountKind3Combo.getValue().getCode(),
								accountKind3Combo.getValue().getName(),
								accountKind4Combo.getValue().getCode(),
								accountKind4Combo.getValue().getName(),
								accountCd,
								inputAccountName.getText().trim(),
								inputAccountNameKana.getText().trim(),
								kindFlag);
	}
	
	//摘要マスタ登録
	private void insertSummaryMaster(String accountCd) {
		
		d3Service.insertSummary(CompanyUtil.getCompanyCode(),
								getUniqueCode("3"),
								inputAccountName.getText().trim(),
								inputAccountNameKana.getText().trim(),
								accountCd);
		
	}
	
	//既存デフォルト登録の摘要コード或いは勘定科目コードと区分できるようにするコードを採番する
	private String getUniqueCode(String ketaNum) {

		// DBから取得した最大勘定科目コード
		String maxDBAccountCode = "";
		int numberKeta = 0;
		String zeroNum = "";
		
		//勘定科目コード、DBに３桁、１桁目はアルファベットを除いて、２桁からの桁数で判断
		if ("2".equals(ketaNum)) {
			maxDBAccountCode = d3Service.getMaxAccountCd(CompanyUtil.getCompanyCode());
			numberKeta = 99;
			zeroNum = "00";
		//摘要コード
		}else {
			maxDBAccountCode = d3Service.getMaxSummaryCd(CompanyUtil.getCompanyCode());
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
