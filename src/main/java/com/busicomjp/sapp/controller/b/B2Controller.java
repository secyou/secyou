package com.busicomjp.sapp.controller.b;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.converter.DatePickerConverter;
import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.b.AccountInfoData;
import com.busicomjp.sapp.model.b.GeneralLedgerData;
import com.busicomjp.sapp.service.b.B2ReportService;
import com.busicomjp.sapp.service.b.B2Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import lombok.Getter;

@Component
public class B2Controller extends BaseController implements Initializable {

	static final Logger log = LoggerFactory.getLogger(B2Controller.class);
	static String selectedCompanyCod = "";
	static String selectedKimatuYear = "";
	static String selectedAccountCode = "";
	static String selectedAccountName = "";
	static String accountStartDay = "";
	static String accountEndDay = "";
	private final String CONST_DATE_PATTERN = "yyyy/MM/dd";
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CONST_DATE_PATTERN);

	@Autowired
	private B2Service b2Service;
	@Autowired
	private B2ReportService b2ReportService;

	@FXML
	private GridPane b2_show;
	@FXML
	private TableView<GeneralLedgerData> generalLedgerDataTable;
	@FXML
	private TableColumn<GeneralLedgerData, String> accrualDate;
	@FXML
	private TableColumn<GeneralLedgerData, String> counterAccount;
	@FXML
	private TableColumn<GeneralLedgerData, String> tekiyo;
	@FXML
	private TableColumn<GeneralLedgerData, String> debitAmountMoney;
	@FXML
	private TableColumn<GeneralLedgerData, String> creditAmountMoney;
	@FXML
	private TableColumn<GeneralLedgerData, String> balanceMoney;
	@FXML
	private TableColumn<GeneralLedgerData, String> generalNo;
	@FXML
	private TableColumn<GeneralLedgerData, String> sum_flg;
	@FXML
	private ComboBox<ComboItem> comboAccountName;
	@FXML
	private DatePicker searchAccrualDateStart;
	@FXML
	private DatePicker searchAccrualDateEnd;
	@Getter
	List<ComboItem> accounList = new ArrayList<ComboItem>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accrualDate.setCellValueFactory(new PropertyValueFactory<>("accrualDate"));
		counterAccount.setCellValueFactory(new PropertyValueFactory<>("counterAccount"));
		tekiyo.setCellValueFactory(new PropertyValueFactory<>("tekiyo"));
		debitAmountMoney.setCellValueFactory(new PropertyValueFactory<>("debitAmountMoney"));
		creditAmountMoney.setCellValueFactory(new PropertyValueFactory<>("creditAmountMoney"));
		balanceMoney.setCellValueFactory(new PropertyValueFactory<>("balanceMoney"));
		generalNo.setCellValueFactory(new PropertyValueFactory<>("generalNo"));
		sum_flg.setCellValueFactory(new PropertyValueFactory<>("sum_flg"));
		// テーブルカラム幅設定
		accrualDate.prefWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		counterAccount.prefWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.22).subtract(3.0));
		tekiyo.prefWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.34).subtract(2.0));
		debitAmountMoney.prefWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.12).subtract(1.0));
		creditAmountMoney.prefWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.12).subtract(1.0));
		balanceMoney.prefWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.12).subtract(1.0));

		accrualDate.maxWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		counterAccount.maxWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.22).subtract(3.0));
		tekiyo.maxWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.34).subtract(2.0));
		debitAmountMoney.maxWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.12).subtract(1.0));
		creditAmountMoney.maxWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.12).subtract(1.0));
		balanceMoney.maxWidthProperty().bind(generalLedgerDataTable.widthProperty().subtract(10.0).multiply(0.12).subtract(1.0));

		searchAccrualDateStart.setConverter(new DatePickerConverter());
		searchAccrualDateEnd.setConverter(new DatePickerConverter());
	}

	//初期化処理
	public void init() {
		bindingKeyPress();
		// 勘定項目取得
		getAccountData();
		if (selectedCompanyCod.equals(CompanyUtil.getCompanyCode())
				&& selectedKimatuYear.equals(CompanyUtil.getKimatuYear())) {
			if (StringUtils.isNotEmpty(selectedAccountCode)) {
				comboAccountName.getSelectionModel().select(new ComboItem(selectedAccountCode, selectedAccountName));
			}
			// 前回表示した会社と同じ場合、初期化をスキップ
			return;
		}
		selectedAccountCode = "";
		selectedAccountName = "";
		selectedCompanyCod = CompanyUtil.getCompanyCode();
		selectedKimatuYear = CompanyUtil.getKimatuYear();
		// 一覧データをクリア
		generalLedgerDataTable.getItems().clear();
		// 取引日取得
		getAccountDay();
	}

	/**
     * 総勘定元帳データ検索
     */
	@FXML
	public void onRedisplayData() {
		generalLedgerDataTable.getItems().clear();
		// 検索実行
		ComboItem selected = comboAccountName.getValue();
		if (selected == null || selected.getCode().isEmpty()) {
			throw new ValidationException("勘定項目を選択してください。");
		}
		
        // 進捗インジケータ
        Alert alert = commonAlert.getProgressIndicatorAlert();
        alert.show();
        
        Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                Task<String> task = new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                    	// 入力チェック
                		inputCheck();
                		
                		String accountCode = selected.getCode();
                		selectedAccountCode = accountCode;
                		selectedAccountName = selected.getName();
                		// 取引日
                		String searcStart = searchAccrualDateStart.getEditor().getText();
                		if (!InputCheck.isNullOrBlank(searcStart)) {
                			searcStart = searcStart.replaceAll("/", "");
                		}
                		String searcEnd = searchAccrualDateEnd.getEditor().getText();
                		if (!InputCheck.isNullOrBlank(searcEnd)) {
                			searcEnd = searcEnd.replaceAll("/", "");
                		}

                		// 消費税集計フラグ取得
                		String taxAppFlg = CompanyUtil.getTaxAppFlg();
                		List<GeneralLedgerData> dataList = b2Service.getGeneralLedgerInfo(CompanyUtil.getCompanyCode(), accountCode,
                				searcStart, searcEnd, taxAppFlg);

                		if (dataList != null) {
                			final ObservableList<GeneralLedgerData> observableList = FXCollections.observableArrayList(dataList);
                			generalLedgerDataTable.getItems().clear();
                			generalLedgerDataTable.getItems().addAll(observableList);
                		}
                        return "OK";
                    }
                };
                commonTask.setTaskHandling(task, alert);
                return task;
            }
        };
        service.start();
	}

	/**
     * 帳票出力
     */
	@FXML
	private void onPrintData() {
		ComboItem selected = comboAccountName.getValue();
		if (selected == null || selected.getCode().isEmpty()) {
			throw new ValidationException("勘定項目を選択してください。");
		}
		
        // 進捗インジケータ
        Alert alert = commonAlert.getProgressIndicatorAlert();
        alert.show();
        
        Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                Task<String> task = new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                    	// 入力チェック
                		inputCheck();
                		String accountCode = selected.getCode();
            			// 取引日
            			String searcStart = searchAccrualDateStart.getEditor().getText();
            			if (!InputCheck.isNullOrBlank(searcStart)) {
            				searcStart = searcStart.replaceAll("/", "");
            			}
            			String searcEnd = searchAccrualDateEnd.getEditor().getText();
            			if (!InputCheck.isNullOrBlank(searcEnd)) {
            				searcEnd = searcEnd.replaceAll("/", "");
            			}

            			// 消費税集計フラグ取得
            			String taxAppFlg = CompanyUtil.getTaxAppFlg();
            			List<GeneralLedgerData> searchResultList = b2Service.getGeneralLedgerInfo(CompanyUtil.getCompanyCode(),
            					accountCode, searcStart, searcEnd, taxAppFlg);

            			if (searchResultList != null && searchResultList.size() > 0) {
            				String accountYear = CompanyUtil.getKimatuYear();
            				accountCode = selected.getCode();
            				String accountName = selected.getName();

            				try {
            					b2ReportService.generatePdfReport(searchResultList, accountYear, accountCode, accountName);
            					log.info("元帳の作成は完了しました");
            				} catch (Exception e) {
            					throw new SystemException(e);
            				}
            			}
                        return "OK";
                    }
                };
                commonTask.setTaskHandling(task, alert);
                return task;
            }
        };
        service.start();
	}

	/**
     * 勘定項目取得
     */
	private void getAccountData() {
		log.info("getAccountData 開始.");

		comboAccountName.setConverter(new ComboItemConverter());
		comboAccountName.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<AccountInfoData> dataList = b2Service.getAccountList(CompanyUtil.getCompanyCode());
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (AccountInfoData data : dataList) {
				accounList.add(new ComboItem(data.getAccountCode(), data.getAccountName()));
			}
		}
		comboAccountName.getItems().addAll(getAccounList());
		comboAccountName.getSelectionModel().select(0);
		log.info("getAccountData 終了.");
	}

	//取引日取得
	private void getAccountDay() {

		LocalDate defaultStartDay = LocalDate.parse(CompanyUtil.getAccountStartDay(),
				DateTimeFormatter.ofPattern("yyyyMMdd"));
		LocalDate defaultEndDay = LocalDate.parse(CompanyUtil.getAccountEndDay(),
				DateTimeFormatter.ofPattern("yyyyMMdd"));

			searchAccrualDateStart.getEditor().setText(defaultStartDay.format(dateFormatter));
			searchAccrualDateStart.setValue(defaultStartDay);

			searchAccrualDateEnd.getEditor().setText(defaultEndDay.format(dateFormatter));
			searchAccrualDateEnd.setValue(defaultEndDay);

			accountStartDay = defaultStartDay.toString().replace("-", "");
			accountEndDay = defaultEndDay.toString().replace("-", "");
	}

	private void inputCheck() {
		// 取引日
		LocalDate _searchStartDate = null;
		if (!InputCheck.isVaildDate(searchAccrualDateStart)) {
			throw new ValidationException("取引開始日はYYYY/MM/DD形式の日付を入力してください");
		} else {
			_searchStartDate = searchAccrualDateStart.getValue();
		}
		LocalDate _searchEndDate = null;
		if (!InputCheck.isVaildDate(searchAccrualDateEnd)) {
			throw new ValidationException("取引終了日はYYYY/MM/DD形式の日付を入力してください");
		} else {
			_searchEndDate = searchAccrualDateEnd.getValue();
		}

		if(_searchStartDate == null) {
			throw new ValidationException("取引開始日を入力してください");
		}

		if(_searchEndDate == null) {
			throw new ValidationException("取引終了日を入力してください");
		}

		LocalDate defaultStartDay = LocalDate.parse(CompanyUtil.getAccountStartDay(),
				DateTimeFormatter.ofPattern("yyyyMMdd"));
		LocalDate defaultEndDay = LocalDate.parse(CompanyUtil.getAccountEndDay(),
				DateTimeFormatter.ofPattern("yyyyMMdd"));
		if (_searchStartDate.compareTo(defaultStartDay) < 0) {
			throw new ValidationException("取引開始日は"
					+ defaultStartDay.format(DateTimeFormatter.ofPattern(CONST_DATE_PATTERN)) + "以降の日付を指定してください");
		}

		if (_searchEndDate.compareTo(defaultEndDay) > 0) {
			throw new ValidationException("取引終了日は"
					+ defaultEndDay.format(DateTimeFormatter.ofPattern(CONST_DATE_PATTERN)) + "以前の日付を指定してください");
		}

		if (_searchStartDate != null && _searchEndDate != null) {
			if (_searchStartDate.compareTo(_searchEndDate) > 0) {
				throw new ValidationException("取引開始日が取引終了日より大きいです");
			}
		}

		if (comboAccountName.getValue() == null || StringUtils.isEmpty(comboAccountName.getValue().getCode())) {
			throw new ValidationException("勘定科目を選択してください");
		}
	}

	//残高試算表から総勘定元帳画面へ遷移
	public void init2(String accountCode, String accountName) {

		bindingKeyPress();
		//取引日取得
		getAccountDay();

		generalLedgerDataTable.getItems().clear();

		selectedCompanyCod = CompanyUtil.getCompanyCode();
		selectedKimatuYear = CompanyUtil.getKimatuYear(); 
		comboAccountName.setConverter(new ComboItemConverter());
		comboAccountName.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<AccountInfoData> dataList = b2Service.getAccountList(selectedCompanyCod);
		for(AccountInfoData data : dataList) {
			accounList.add(new ComboItem(data.getAccountCode(),data.getAccountName()));
		}
		comboAccountName.getItems().addAll(getAccounList());
		comboAccountName.getSelectionModel().select(new ComboItem(accountCode,accountName));

		// 取引日
		String searcStart = searchAccrualDateStart.getEditor().getText();
		if (!InputCheck.isNullOrBlank(searcStart)) {
			searcStart = searcStart.replaceAll("/", "");
		}
		String searcEnd = searchAccrualDateEnd.getEditor().getText();
		if (!InputCheck.isNullOrBlank(searcEnd)) {
			searcEnd = searcEnd.replaceAll("/", "");
		}

		// 消費税集計フラグ取得
		String taxAppFlg = CompanyUtil.getTaxAppFlg();
		List<GeneralLedgerData> dataList1= b2Service.getGeneralLedgerInfo(CompanyUtil.getCompanyCode(), accountCode,
				searcStart, searcEnd, taxAppFlg);

		if(dataList1 != null) {
			final ObservableList<GeneralLedgerData> observableList = FXCollections.observableArrayList(dataList1);
			generalLedgerDataTable.getItems().clear();
			generalLedgerDataTable.getItems().addAll(observableList);
		}

	}
	
	private void bindingKeyPress() {
		App.clearKeyEvent();
		KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
		KeyCombination f9 = new KeyCodeCombination(KeyCode.F9);
		App.putKeyEvent(f1, new Runnable() {
			@Override
			public void run() {
				onRedisplayData();
			}
		});
		App.putKeyEvent(f9, new Runnable() {
			@Override
			public void run() {
				onPrintData();
			}
		});
	}
}
