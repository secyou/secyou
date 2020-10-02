package com.busicomjp.sapp.controller.a;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.converter.DatePickerConverter;
import com.busicomjp.sapp.common.converter.MaxLengthConverter;
import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.FeatureGroupController;
import com.busicomjp.sapp.controller.z.Z1HintController;
import com.busicomjp.sapp.dto.a.InputDataDto;
import com.busicomjp.sapp.model.a.BalanceData;
import com.busicomjp.sapp.model.a.JournalEntryData;
import com.busicomjp.sapp.model.z.AccountData;
import com.busicomjp.sapp.service.a.A1Service;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

@Component
public class A1Controller extends Z1HintController implements Initializable {

	static final Logger log = LoggerFactory.getLogger(A1Controller.class);

	static final String HINT_KBN_TORIHIKISAKI = "TORIHIKISAKI";
	static final String HINT_KBN_TEKIYO = "TEKEIYO";
	
	private final String CONST_DATE_PATTERN = "yyyy/MM/dd";
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CONST_DATE_PATTERN);

	static String selectedCompanyCod = "";
	static String selectedKimatuYear = "";
	
	static final Color START_COLOR = Color.rgb(92, 184, 92); // #5cb85c
	
	@Autowired
	private FeatureGroupController featureController;

	@Autowired
	private A1Service a1Service;

	@FXML
	private GridPane a1_main;

	@FXML
	private Text text_bl1; //本日残高
	@FXML
	private Text text_bl2; //現金
	@FXML
	private Text text_bl3; //預貯金
	@FXML
	private Text text_bl4; //受取手形
	@FXML
	private Text text_bl5; //売掛金
	@FXML
	private Text text_bl6; //支払手形
	@FXML
	private Text text_bl7; //買掛金
	@FXML
	private Text text_bl8; //未払金
	@FXML
	private Text text_bl9; //未払消費税
	
	@FXML
	private DatePicker expectDate;
	@FXML
	private Text text_title11;
	@FXML
	private Text text_bl11; 

	@FXML
	private TableView<JournalEntryData> acceptDataTable;
	@FXML
	private TableColumn<JournalEntryData, String> accrualDate;
	@FXML
	private TableColumn<JournalEntryData, String> suppliersName;
	@FXML
	private TableColumn<JournalEntryData, String> tekiyoName;
	@FXML
	private TableColumn<JournalEntryData, String> creditName;
	@FXML
	private TableColumn<JournalEntryData, String> debitName;
	@FXML
	private TableColumn<JournalEntryData, String> amountMoney;
	@FXML
	private TableColumn<JournalEntryData, String> tax;
	@FXML
	private TableColumn<JournalEntryData, String> taxName;
	@FXML
	private TableColumn<JournalEntryData, String> depPayDate;
	@FXML
	private TableColumn<JournalEntryData, String> acceptNo;

	@FXML
	private ComboBox<ComboItem> inputSelectCombo;
	@FXML
	private ComboBox<ComboItem> inputDebitCombo;
	@FXML
	private ComboBox<ComboItem> inputCreditCombo;
	@FXML
	private ComboBox<ComboItem> inputTaxKbnCombo;
	@FXML
	private TextField inputAccrualDate;

	@FXML
	private Text accountSettingKbn;
	@FXML
	private TextField inputKingaku;
	@FXML
	private TextField inputPaymentDate;
	@FXML
	private Text paymentDateText;
	@FXML
	private Button btn_Enter;
	
	private ObjectProperty<Color> deibitComboBackGroundColorProperty = new SimpleObjectProperty<Color>(Color.WHITE);
	private ObjectProperty<Color> creditComboBackGroundColorProperty = new SimpleObjectProperty<Color>(Color.WHITE);
	
	private ObjectProperty<String> debitComboStyleProerty = new SimpleObjectProperty<String>();
	private ObjectProperty<String> creditComboStyleProerty = new SimpleObjectProperty<String>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		z1init(false);
		// 幅のハンドリング
		inputSelectCombo.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.08).subtract(4.0));
		inputAccrualDate.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.06).subtract(4.0));
		searchSuppliersName.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.15).subtract(4.0));
		searchTekiyoName.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.15).subtract(4.0));
		inputDebitCombo.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.15).subtract(4.0));
		inputCreditCombo.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.15).subtract(4.0));
		inputKingaku.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.08).subtract(4.0));
		inputTaxKbnCombo.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.095).subtract(4.0));
		inputPaymentDate.prefWidthProperty().bind(a1_main.widthProperty().subtract(10.0).multiply(0.085).subtract(4.0));

		accrualDate.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.07));
		suppliersName.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.15));
		tekiyoName.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.15));
		creditName.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.16));
		debitName.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.16));
		amountMoney.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.08));
		tax.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.06));
		taxName.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.085));
		depPayDate.prefWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.085));

		accrualDate.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.07));
		suppliersName.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.15));
		tekiyoName.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.15));
		creditName.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.16));
		debitName.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.16));
		amountMoney.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.08));
		tax.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.06));
		taxName.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.085));
		depPayDate.maxWidthProperty().bind(acceptDataTable.widthProperty().subtract(10.0).multiply(0.085));
		
		acceptDataTable.setFixedCellSize(25);
		acceptDataTable.prefHeightProperty().bind(acceptDataTable.fixedCellSizeProperty().multiply(11.0).add(3.0));
		acceptDataTable.minHeightProperty().bind(acceptDataTable.prefHeightProperty());
		acceptDataTable.maxHeightProperty().bind(acceptDataTable.prefHeightProperty());

		// 仕訳データテーブル
		accrualDate.setCellValueFactory(new PropertyValueFactory<>("accrualDate"));
		suppliersName.setCellValueFactory(new PropertyValueFactory<>("suppliersName"));
		tekiyoName.setCellValueFactory(new PropertyValueFactory<>("tekiyoName"));
		creditName.setCellValueFactory(new PropertyValueFactory<>("creditName"));
		debitName.setCellValueFactory(new PropertyValueFactory<>("debitName"));
		amountMoney.setCellValueFactory(new PropertyValueFactory<>("amountMoney"));
		tax.setCellValueFactory(new PropertyValueFactory<>("tax"));
		taxName.setCellValueFactory(new PropertyValueFactory<>("taxRate"));
		depPayDate.setCellValueFactory(new PropertyValueFactory<>("depPayDate"));
		acceptNo.setCellValueFactory(new PropertyValueFactory<>("acceptNo"));

		expectDate.setConverter(new DatePickerConverter());
		inputSelectCombo.setConverter(new ComboItemConverter());
		inputDebitCombo.setConverter(new ComboItemConverter());
		inputCreditCombo.setConverter(new ComboItemConverter());
		inputTaxKbnCombo.setConverter(new ComboItemConverter());
		
		// 入力最大桁数を指定
		inputAccrualDate.setTextFormatter(new TextFormatter<>(new MaxLengthConverter(4), "", null));
		inputPaymentDate.setTextFormatter(new TextFormatter<>(new MaxLengthConverter(8), "", null));
		
		Callback<DatePicker, DateCell> dateRangeFactory = dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				LocalDate localDateStart = LocalDate.of(Integer.parseInt(StringUtil.getToday().substring(0, 4)),
						Integer.parseInt(StringUtil.getToday().substring(4, 6)), Integer.parseInt(StringUtil.getToday().substring(6)));
				if (item.isBefore(localDateStart) || item.equals(localDateStart)) {
					// システム日以前の日付は選択不可とする
					setStyle("-fx-background-color: #939598;");
					Platform.runLater(() -> setDisable(true));
				}
			}
		};
		expectDate.setDayCellFactory(dateRangeFactory);
		
		inputAccrualDate.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
					Boolean newPropertyValue) {
				if (newPropertyValue) {
					// ON FOCUS
				} else {
					// OUT FOCUS
					onAccrualDateEnter();
				}
			}
		});
		
		// 借方勘定科目Comboの背景色Property
		deibitComboBackGroundColorProperty.addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> arg0, Color oldPropertyValue,
					Color newPropertyValue) {
				if (newPropertyValue != null) {
					debitComboStyleProerty.set("-fx-background-color: "+ toRGBCode(newPropertyValue) + ";");
				} 
			}
		});
		inputDebitCombo.styleProperty().bind(debitComboStyleProerty);
		
		// 貸方勘定科目Comboの背景色Property
		creditComboBackGroundColorProperty.addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> arg0, Color oldPropertyValue,
					Color newPropertyValue) {
				if (newPropertyValue != null) {
					creditComboStyleProerty.set("-fx-background-color: "+ toRGBCode(newPropertyValue) + ";");
				} 
			}
		});
		inputCreditCombo.styleProperty().bind(creditComboStyleProerty);
	}

	public void init() {
		searchHint = true;
		
		bindingKeyPress();
		showRealBalanceData();
		showJournalEntryDataList();
		
		if (selectedCompanyCod.equals(CompanyUtil.getCompanyCode())
				&& selectedKimatuYear.equals(CompanyUtil.getKimatuYear())) {
			// 前回表示した会社と同じ場合、初期化をスキップ
			showExpectBalanceData();
			return;
		}
		selectedCompanyCod = CompanyUtil.getCompanyCode();
		selectedKimatuYear = CompanyUtil.getKimatuYear();
		
		initExpectDate();
		showExpectBalanceData();
		try {
			clearHintData();
			clearInputData();
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}
	
	private void initExpectDate() {	
		int year = 0;
		int month = 0;
		int today = Integer.parseInt(StringUtil.getToday().substring(6));
		if (today >= 25) {
			// 25日以降の場合、翌月の予想残高を表示
			year = Integer.parseInt(StringUtil.getNextMonth(StringUtil.getToday()).substring(0, 4));
			month = Integer.parseInt(StringUtil.getNextMonth(StringUtil.getToday()).substring(4));
		} else {
			// 当月の予想残高を表示
			year = Integer.parseInt(StringUtil.getToday().substring(0, 4));
			month = Integer.parseInt(StringUtil.getToday().substring(4, 6));
		}
		LocalDate localDate = LocalDate.of(year, month, 25);
		
		expectDate.getEditor().setText(localDate.format(dateFormatter));
		expectDate.setValue(localDate);
	}

	private void clearInputData() {
		// 分類Combo
		inputSelectCombo.getItems().clear();
		inputSelectCombo.getItems().addAll(commonItems.getSelectItemList());
		inputSelectCombo.getSelectionModel().select(0);
		// 借方勘定科目Combo
		Map<String, List<ComboItem>> accountItemMap = a1Service.getSelectedAccountList(CommonConstants.SHIWAKE_KIND.MONEY_RECEIVED);
		inputDebitCombo.getItems().clear();
		inputDebitCombo.getItems().addAll(accountItemMap.get(a1Service.KEY_DEBIT));
		inputDebitCombo.getSelectionModel().select(0);
		// 貸方勘定科目Combo
		inputCreditCombo.getItems().clear();
		inputCreditCombo.getItems().addAll(accountItemMap.get(a1Service.KEY_CREDIT));
		inputCreditCombo.getSelectionModel().select(0);
		// 消費税Combo
		inputTaxKbnCombo.getItems().clear();
		inputTaxKbnCombo.getItems().addAll(commonItems.getTaxItemList());
		inputTaxKbnCombo.getSelectionModel().select(0);

		paymentDateText.setText("入金日");
		inputAccrualDate.setText("");
		searchSuppliersName.setText("");
		searchSuppliersCode.setText("");
		searchTekiyoName.setText("");
		searchTekiyoCode.setText("");
		inputKingaku.setText("");
		inputPaymentDate.setText("");
		accountSettingKbn.setText("");
	}

	public void showJournalEntryDataList() {
		List<JournalEntryData> dataList = a1Service.getJournalEntry();

		final ObservableList<JournalEntryData> observableList = FXCollections.observableArrayList(dataList);
		acceptDataTable.getItems().clear();
		acceptDataTable.getItems().addAll(observableList);
	}

	private void showRealBalanceData() {
		List<BalanceData> dataList = a1Service.getRealBalanceDataList();
		for (BalanceData data : dataList) {
			switch(data.getKbn()) {
			case "00" :
				text_bl1.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "11" :
				text_bl2.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "12" :
				text_bl3.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "13" :
				text_bl4.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "14" :
				text_bl5.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "21" :
				text_bl6.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "22" :
				text_bl7.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "23" :
				text_bl8.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			case "24" :
				text_bl9.setText(StringUtil.commaFormat(data.getBalance()));
				break;
			default:
			}
		}
	}
	
	@FXML
	private void onGotoBalance() {
	    // 残高試算表に遷移
		featureController.selectTab(featureController.getB1());
	}
	
	private void showExpectBalanceData() {
		String _expectDate = expectDate.getEditor().getText();
		text_title11.setText(_expectDate);
		
		BalanceData data = a1Service.getExpectBalance(StringUtil.replaceDateSlashFormat(_expectDate));
		long todayBalance = Long.valueOf(StringUtil.replaceCommaFormat(text_bl3.getText()));
		text_bl11.setText(StringUtil.commaFormat(todayBalance + data.getBalance()));
	}
	
	@FXML
	private void onExpectDateSelect() {
		if (expectDate == null || StringUtils.isEmpty(expectDate.getEditor().getText())) {
			text_title11.setText("-");
			text_bl11.setText("-");
			return;
		}
		
		if (!InputCheck.isVaildDate(expectDate)) {
			throw new ValidationException("予想日はYYYY/MM/DD形式の日付を入力してください。");
		}
		
		String nextDayString = StringUtil.getNextDay(StringUtil.getToday());
		LocalDate nextDay = LocalDate.of(Integer.parseInt(nextDayString.substring(0, 4)),
				Integer.parseInt(nextDayString.substring(4, 6)), Integer.parseInt(nextDayString.substring(6)));
		if (nextDay.isAfter(expectDate.getValue())) {
			throw new ValidationException("予想日は翌日以降の日付を入力してください。");
		}
		
		showExpectBalanceData();
	}

	@FXML
	private void onInputData() {
		if (commonAlert.showConfirmationAlert("登録処理を実行しますか？")) {
	        // 進捗インジケータ
	        Alert alert = commonAlert.getProgressIndicatorAlert();
	        alert.show();
	        
	        Service<String> service = new Service<String>() {
	            @Override
	            protected Task<String> createTask() {
	                Task<String> task = new Task<String>() {
	                    @Override
	                    protected String call() throws Exception {
	                    	// 登録値取得
	                    	InputDataDto inputData = new InputDataDto();
	            			inputData.setSelectCode(inputSelectCombo.getValue().getCode());
	            			inputData.setAccrualDate(inputAccrualDate.getText());
	            			if (StringUtils.isNotEmpty(searchSuppliersName.getText())) {
	            				inputData.setSuppliersCode(searchSuppliersCode.getText());
	            			}
	            			if (StringUtils.isNotEmpty(searchTekiyoName.getText())) {
	            				inputData.setTekiyoCode(searchTekiyoCode.getText());
	            			}
	            			inputData.setDebit(inputDebitCombo.getValue().getCode());
	            			inputData.setCredit(inputCreditCombo.getValue().getCode());
	            			inputData.setKingaku(inputKingaku.getText());
	            			inputData.setTaxCode(inputTaxKbnCombo.getValue().getCode());
	            			inputData.setPaymentDate(inputPaymentDate.getText());

	            			// 入力値を編集する
	            			inputData = a1Service.editInputData(inputData);
	            			// 入力データをチェック
	            			a1Service.checkInputData(inputData);
	            			// 自動仕訳登録
	            			a1Service.registInputData(inputData);
							Platform.runLater(() -> {
								// 表示内容を最新化
								clearInputData();
								showRealBalanceData();
								showExpectBalanceData();
								showJournalEntryDataList();
							});
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

	@FXML
	public void onDeleteData() {
		JournalEntryData journalEntryData = acceptDataTable.getSelectionModel().getSelectedItem();
		if (journalEntryData == null || StringUtils.isEmpty(journalEntryData.getJournalNo())) {
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
                			a1Service.deleteData(journalEntryData);
							Platform.runLater(() -> {
								// 残高と一覧を最新化
								showRealBalanceData();
								showJournalEntryDataList();
							});
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
     * 分類Combo選択イベント
     */
	@FXML
	private void onSelectAction() {
		if (inputSelectCombo == null || inputSelectCombo.getValue() == null) {
			return;
		}

		// クリア入力項目
		inputAccrualDate.setText("");
		searchSuppliersName.setText("");
		searchSuppliersCode.setText("");
		searchTekiyoName.setText("");
		searchTekiyoCode.setText("");
		inputKingaku.setText("");
		inputPaymentDate.setText("");
		inputTaxKbnCombo.getSelectionModel().select(0);
		accountSettingKbn.setText("");

		String selectCode = inputSelectCombo.getValue().getCode();
		switch(selectCode) {
		case CommonConstants.SHIWAKE_KIND.MONEY_RECEIVED : // 入金
			paymentDateText.setText("入金日");
			break;
		case CommonConstants.SHIWAKE_KIND.WITHDRAWAL : // 出金
			paymentDateText.setText("支払日");
			break;
		case CommonConstants.SHIWAKE_KIND.ACCOUNTS_RECEIVABLE : // 売掛金
			paymentDateText.setText("入金予定日");
			break;
		case CommonConstants.SHIWAKE_KIND.ACCOUNTS_PAYABLE : // 買掛金
		case CommonConstants.SHIWAKE_KIND.UNPAID :           // 未払金
			paymentDateText.setText("支払予定日");
			break;
		case CommonConstants.SHIWAKE_KIND.BILLS_RECIVABLE : // 受取手形
		case CommonConstants.SHIWAKE_KIND.BILLS_PAYMENT : // 支払手形
			paymentDateText.setText("手形満期日");
			break;
		default :
			paymentDateText.setText("入金・支払予定日");
		}

		Map<String, List<ComboItem>> accountItemMap = a1Service.getSelectedAccountList(selectCode);
		inputDebitCombo.getItems().clear();
		inputDebitCombo.getItems().addAll(accountItemMap.get(a1Service.KEY_DEBIT));
		inputDebitCombo.getSelectionModel().select(0);
		inputCreditCombo.getItems().clear();
		inputCreditCombo.getItems().addAll(accountItemMap.get(a1Service.KEY_CREDIT));
		inputCreditCombo.getSelectionModel().select(0);
		
		playTimeLine4Combo("3");
	}
	
	@FXML
	private void onSelectHidden() {
		// 次の入力項目のFocus
		inputAccrualDate.requestFocus();
	}

	/**
     * 借方／貸方勘定科目ComboBoxの背景色変換アニメーション
     */
	private void playTimeLine4Combo(String kbn) {
		if ("1".equals(kbn)) { // 借方勘定科目のみ実施
			Timeline timeline = new Timeline(
					// 3秒間で最初のshallowGreen色から白色に変換
					new KeyFrame(Duration.seconds(0), new KeyValue(deibitComboBackGroundColorProperty, START_COLOR)),
					new KeyFrame(Duration.seconds(3), new KeyValue(deibitComboBackGroundColorProperty, Color.WHITE))
					);
			timeline.setAutoReverse(true);
			timeline.play();
		} 
		if ("2".equals(kbn)) { // 貸方勘定科目のみ実施
			Timeline timeline = new Timeline(
					// 3秒間で最初のshallowGreen色から白色に変換
					new KeyFrame(Duration.seconds(0), new KeyValue(creditComboBackGroundColorProperty, START_COLOR)),
					new KeyFrame(Duration.seconds(3), new KeyValue(creditComboBackGroundColorProperty, Color.WHITE))
					);
			timeline.setAutoReverse(true);
			timeline.play();
		}
		if ("3".equals(kbn)) { // 借方・貸方勘定科目の両方実施
			Timeline timeline = new Timeline(
					// 3秒間で最初のshallowGreen色から白色に変換
					new KeyFrame(Duration.seconds(0), new KeyValue(deibitComboBackGroundColorProperty, START_COLOR)),
					new KeyFrame(Duration.seconds(0), new KeyValue(creditComboBackGroundColorProperty, START_COLOR)),
					new KeyFrame(Duration.seconds(3), new KeyValue(deibitComboBackGroundColorProperty, Color.WHITE)),
					new KeyFrame(Duration.seconds(3), new KeyValue(creditComboBackGroundColorProperty, Color.WHITE)));
			timeline.setAutoReverse(true);
			timeline.play();
		}
	}
	
	/**
     * 色コードを16進数で変換
     */
	private String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	/**
     * 取引発生日欄Enterキーイベント
     */
	@FXML
	private void onAccrualDateEnter() {
		String date = inputAccrualDate.getText();
		if (!StringUtils.isEmpty(date) && date.length() == 3) {
			// 3桁の場合、前に0埋め
			inputAccrualDate.setText("0" + date);
		}
		
		if (CommonConstants.SHIWAKE_KIND.MONEY_RECEIVED.equals(inputSelectCombo.getValue().getCode())
				|| CommonConstants.SHIWAKE_KIND.WITHDRAWAL.equals(inputSelectCombo.getValue().getCode())) {
			// 入金、出金の場合、取引発生日を設定
			inputPaymentDate.setText(a1Service.generateAccrualDate(StringUtil.replaceFull2HalfNumber(inputAccrualDate.getText())));
		}
		
		// 次の入力項目のFocus
		searchSuppliersName.requestFocus();
	}

	@Override
	public void hintClickAction(String hintKbn, String hintCode, String hintName) {
		log.info("ヒントをクリックしました。kbn=" + hintKbn + ",code=" + hintCode + ",name=" + hintName);

		// 分類コードを取得
		String selectCode = inputSelectCombo.getValue().getCode();
		List<ComboItem> comboList = new ArrayList<ComboItem>();
		List<AccountData> accountList = null;
		// 借方勘定科目設定フラグ
		boolean accountSettingFlg = false;

		if (HINT_KBN_TORIHIKISAKI.equals(hintKbn)) {
			// 取引先情報を設定
			searchSuppliersCode.setText(hintCode);
			searchSuppliersName.setText(hintName);
			accountList = z1HintService.getAccountDataListForTorihikisaki(hintCode);
			accountSettingKbn.setText(HINT_KBN_TORIHIKISAKI);
			// 取引先ヒントの場合、勘定科目設定をいつも有効にする
			accountSettingFlg = true;
		} else {
			// 摘要情報を設定
			searchTekiyoCode.setText(hintCode);
			searchTekiyoName.setText(hintName);
			accountList = z1HintService.getAccountDataListForTekiyo(hintCode);
			if (!HINT_KBN_TORIHIKISAKI.equals(accountSettingKbn.getText())
					|| StringUtils.isEmpty(searchSuppliersName.getText())) {
				// 勘定科目設定区分が取引先ではない、または取引先入力欄が空の場合は、勘定科目設定を有効にする
				accountSettingFlg = true;
			}
		}

		if (accountSettingFlg) {
			for (AccountData data : accountList) {
				comboList.add(new ComboItem(data.getAccountCode(), data.getAccountName()));
			}
			// 勘定科目ComboBoxを設定
			if (isCreditCombo(selectCode)) {
				inputCreditCombo.getItems().clear();
				inputCreditCombo.getItems().addAll(comboList);
				inputCreditCombo.getSelectionModel().select(0);
				playTimeLine4Combo("2");
			} else {
				inputDebitCombo.getItems().clear();
				inputDebitCombo.getItems().addAll(comboList);
				inputDebitCombo.getSelectionModel().select(0);
				playTimeLine4Combo("1");
			}
		}

		// 次の項目にFocus
		if (HINT_KBN_TORIHIKISAKI.equals(hintKbn)) {
			searchTekiyoName.requestFocus();
		} else {
			inputDebitCombo.requestFocus();
			inputDebitCombo.show();
		}
	}
	
	private boolean isCreditCombo(String selectCode) {
		switch (selectCode) {
		case "00001": // 入金
		case "00004": // 買掛金
		case "00006": // 手形受取
			return true;
		default:
			return false;
		}
	}
 
	/**
     * 借方勘定科目Combo Hiddenイベント
     */
	@FXML
	private void onDebitHidden() {
		inputCreditCombo.requestFocus();
		inputCreditCombo.show();
	}
	
	/**
     * 貸方勘定科目Combo Hiddenイベント
     */
	@FXML
	private void onCreditHidden() {
		inputKingaku.requestFocus();
	}

	/**
     * 金額欄Enterキーイベント
     */
	@FXML
	private void onKingakuEnter() {
		// 次の入力項目のFocus
		inputTaxKbnCombo.requestFocus();
		inputTaxKbnCombo.show();
	}

	/**
     * 消費税Combo Hiddenイベント
     */
	@FXML
	private void onTaxKbnHidden() {
		inputPaymentDate.requestFocus();
	}

	/**
     * 入金・支払予定日欄Enterキーイベント
     */
	@FXML
	private void onPaymentDateEnter() {
		// データ登録を行う
		onInputData();
	}

	private void bindingKeyPress() {
		App.clearKeyEvent();
		KeyCombination delete = new KeyCodeCombination(KeyCode.DELETE);
		App.putKeyEvent(delete, new Runnable() {
			@Override
			public void run() {
				onDeleteData();
			}
		});
	}

}
