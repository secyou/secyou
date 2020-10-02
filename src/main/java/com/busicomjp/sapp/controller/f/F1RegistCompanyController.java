package com.busicomjp.sapp.controller.f;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.service.d.D1Service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

@Component
public class F1RegistCompanyController extends BaseController implements Initializable {
	
	@Autowired
	private F2FinishController f2FinishController;
	@Autowired
	private D1Service d1Service;
	@Autowired
	Validator validator;

	@FXML
	private TextField inputCompanyName;
	@FXML
	private TextField inputCompanyNameKana;
	@FXML
	private TextField inputAddress;
	@FXML
	private TextField inputCompanyNo;
	@FXML
	private TextField inputEstYear;
	@FXML
	private TextField inputEstMonth;
	@FXML
	private TextField inputEstDay;
	@FXML
	private TextField inputSettlPeriod;
	@FXML
	private TextField inputKisyuYear;
	@FXML
	private TextField inputKisyuMonth;
	@FXML
	private TextField inputKimatuYear;
	@FXML
	private TextField inputKimatuMonth;
	@FXML
	private TextField inputStartYear;
	@FXML
	private TextField inputStartMonth;
	@FXML
	private TextField inputTelNo;
	@FXML
	private TextField inputFaxNo;
	@FXML
	private TextField inputEmail;
	@FXML
	private TextField inputDirectorName;

	@FXML
	private GridPane f1_main;

	@FXML
	private ToggleGroup companyKbnGroup;
	@FXML
	private ToggleGroup businessTypeGroup;
	@FXML
	private ToggleGroup businessLineGroup;
	@FXML
	private ToggleGroup blueDecGroup;
	@FXML
	private ToggleGroup taxKbnGroup;
	@FXML
	private ToggleGroup taxSummaryGroup;
	@FXML
	private ToggleGroup shortFlgGroup;

	@FXML
	private RadioButton businessTypeRadio1;
	@FXML
	private RadioButton businessTypeRadio2;
	@FXML
	private RadioButton businessLineRadio1;
	@FXML
	private RadioButton businessLineRadio2;
	@FXML
	private RadioButton blueDecRadio1;
	@FXML
	private RadioButton blueDecRadio2;
	@FXML
	private RadioButton taxKbnRadio1;
	@FXML
	private RadioButton taxKbnRadio2;
	@FXML
	private RadioButton taxKbnRadio3;
	@FXML
	private RadioButton shortFlgRadio1;
	@FXML
	private RadioButton shortFlgRadio2;
	@FXML
	private RadioButton taxSummary1;
	@FXML
	private RadioButton taxSummary2;
	@FXML
	private RadioButton taxSummary3;

	private static Pattern patYear = Pattern.compile("^\\d{4}");
	private static Pattern patMonth = Pattern.compile("^(0?[1-9]|1[0-2])$");
	private static Pattern patDay = Pattern.compile("^\\d{1,2}$");
	private static Pattern patSettlPeriod = Pattern.compile("^\\d{0,3}");
	private static Pattern pattel = Pattern.compile("^\\d{9,10}$");
	private static Pattern patCorpNumber = Pattern.compile("^\\d{12,13}$");
	private static Pattern patMail = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 消費税事業区分
		taxKbnGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (taxKbnGroup.getSelectedToggle() != null) {
					if ("3".equals(taxKbnGroup.getSelectedToggle().getUserData())) {
						taxSummary1.setDisable(true);
						taxSummary1.setSelected(false);
						taxSummary2.setDisable(true);
						taxSummary2.setSelected(false);
						taxSummary3.setDisable(true);
						taxSummary3.setSelected(false);
					} else {
						taxSummary1.setDisable(false);
						taxSummary1.setSelected(true);
						taxSummary2.setDisable(false);
						taxSummary2.setSelected(false);
						taxSummary3.setDisable(false);
						taxSummary3.setSelected(false);
					}
				}
			}
		});
	}
	
	public void init() {
		clearData();
	}

	@FXML
	private void onCompanyRegist(Event eve) {
		if (commonAlert.showConfirmationAlert("登録処理を実行しますか？")) {
	        // 進捗インジケータ
	        Alert alert = commonAlert.getProgressIndicatorAlert();
	        alert.show();
	        CompanyMDto companyMDto = new CompanyMDto();
	        
			Service<String> service = new Service<String>() {
				@Override
				protected Task<String> createTask() {
					Task<String> task = new Task<String>() {
						@Override
						protected String call() throws Exception {
							// 入力チェック
							inputCheck();

							// 既存会社コード
							String maxComnpanyCode = d1Service.getMaxCompanyCode();
							int maxCode = Integer.parseInt(maxComnpanyCode) + 1;
							String companyCode = String.format("%05d", maxCode);
							
							// 既存会社コード+1を設定
							companyMDto.setCompanyCode(companyCode);
							// 事業タイプ
							if (businessTypeRadio1.isSelected()) {
								// 法人
								companyMDto.setBusinessType("1");
							} else {
								// 個人
								companyMDto.setBusinessType("2");
							}
							// 会社名(個人事業主名)
							companyMDto.setCompanyName(inputCompanyName.getText());
							// 会社名カナ(個人事業主名カナ)
							companyMDto.setCompanyNameKana(inputCompanyNameKana.getText());
							// 会社住所
							companyMDto.setStreetAddress(inputAddress.getText());
							// 法人番号
							companyMDto.setCorpNumber(inputCompanyNo.getText());
							// 事業種目
							if (businessLineRadio1.isSelected()) {
								// 一般事業
								companyMDto.setBusinessLine("1");
							} else {
								// 不動産事業
								companyMDto.setBusinessLine("2");
							}
							// 青色申告
							if (blueDecRadio1.isSelected()) {
								// 有
								companyMDto.setBuleDec("1");
							} else {
								// 無
								companyMDto.setBuleDec("2");
							}
							// 設立年月日
							companyMDto.setEstDate(inputEstYear.getText()
									+ String.format("%02d", Integer.parseInt(inputEstMonth.getText()))
									+ String.format("%02d", Integer.parseInt(inputEstDay.getText())));
							// 決算期
							companyMDto.setSettlPeriod(
									String.format("%03d", Integer.parseInt(inputSettlPeriod.getText())));
							// 事業年度期首
							companyMDto.setKisyuYearMonth(inputKisyuYear.getText()
									+ String.format("%02d", Integer.parseInt(inputKisyuMonth.getText())));
							// 事業年度期末
							companyMDto.setKimatuYearMonth(inputKimatuYear.getText()
									+ String.format("%02d", Integer.parseInt(inputKimatuMonth.getText())));
							// 入力開始年月
							companyMDto.setInputStartYearMonth(inputStartYear.getText()
									+ String.format("%02d", Integer.parseInt(inputStartMonth.getText())));
							// 電話番号
							companyMDto.setTelNumber(inputTelNo.getText());
							// FAX
							companyMDto.setFaxNumber(inputFaxNo.getText());
							// Email
							companyMDto.setMailAddress(inputEmail.getText());
							// 代表取締役名前
							companyMDto.setDirectorName(inputDirectorName.getText());
							// 削除フラグ
							companyMDto.setDelFlg("0");
							// 消費税事業区分
							if (taxKbnRadio3.isSelected()) {
								// 免罪
								companyMDto.setTaxKindFlg("0");
							} else {
								// 原則課税または簡易課税
								companyMDto.setTaxKindFlg("1");
							}
							// 消費税集計区分
							if (taxSummary1.isSelected()) {
								// 1:都度集計
								companyMDto.setTaxAppFlg("1");
							} else if (taxSummary2.isSelected()) {
								// 2:月単位集計
								companyMDto.setTaxAppFlg("2");
							} else if (taxSummary3.isSelected()) {
								// 3:年単位集計
								companyMDto.setTaxAppFlg("3");
							} else {
								companyMDto.setTaxAppFlg("");
							}

							// 期間短縮有無
							if (shortFlgRadio1.isSelected()) {
								// 有
								companyMDto.setShortFlg("1");
							} else {
								// 無
								companyMDto.setShortFlg("2");
							}

							// 期末年
							companyMDto.setKimatuYear(inputKimatuYear.getText());
							// 期末月日
							String formattedKimatuMonth = String.format("%02d",
									Integer.parseInt(inputKimatuMonth.getText()));
							String kimatuDate = StringUtil.GetLastDay(inputKimatuYear.getText() + formattedKimatuMonth);
							companyMDto.setKimatuMonthDay(formattedKimatuMonth.concat(kimatuDate));

							d1Service.insertCompanyData(companyMDto);

							return "OK";
						}
					};
					task.setOnRunning(new EventHandler<WorkerStateEvent>() {
				        @Override
				        public void handle(WorkerStateEvent event) {
				        	f1_main.setDisable(true);
				        }
				    });
					task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				        @Override
				        public void handle(WorkerStateEvent event) {
				        	// Step2に進む
				        	f1_main.setDisable(false);
				        	alert.setResult(ButtonType.CLOSE);
				        	f1_main.getScene().getWindow().hide();
				        	Dialog<Pair<String, String>> dialog = commonDialog.getDialog("会社新規登録完了", "/fxml/f/f2_finish.fxml", true);
				        	f2FinishController.init(companyMDto);
				        	dialog.showAndWait();
				        }
				    });
					task.setOnFailed(new EventHandler<WorkerStateEvent>() {
				        @Override
				        public void handle(WorkerStateEvent event) {
				        	f1_main.setDisable(false);
				        	alert.setResult(ButtonType.CLOSE);
				        	Throwable e = task.getException();
				        	commonAlert.showExceptionAlert(e);
				        }
				    });
					return task;
				}
			};
			service.start();
		}
	}

	private void inputCheck() {
		// 会社名
		if (InputCheck.isNullOrBlank(inputCompanyName.getText())) {
			throw new ValidationException("会社名(個人事業主名)を入力してください。");
		}
		if (inputCompanyName.getText().length() > 100) {
			throw new ValidationException("会社(個人事業主)名は100文字以内で指定してください。");
		}
		// 会社名カナ
		if (InputCheck.isNullOrBlank(inputCompanyNameKana.getText())) {
			throw new ValidationException("会社名(個人事業主名)かなを入力してください。");
		}
		if (inputCompanyNameKana.getText().length() > 100) {
			throw new ValidationException("会社名(個人事業主名)かなは100文字以内で指定してください。");
		}
		// 住所
		if (InputCheck.isNullOrBlank(inputAddress.getText())) {
			throw new ValidationException("住所を入力してください。");
		}
		if (inputAddress.getText().length() > 100) {
			throw new ValidationException("住所は100文字以内で指定してください。");
		}
		// 法人番号
		if (businessTypeRadio1.isSelected()) {
			if (InputCheck.isNullOrBlank(inputCompanyNo.getText())) {
				throw new ValidationException("法人番号を入力してください。");
			}
			if (!patCorpNumber.matcher(inputCompanyNo.getText()).matches()) {
				throw new ValidationException("12桁または13桁数字の法人番号を指定してください。");
			}
			// 設立年月日_年
			if (InputCheck.isNullOrBlank(inputEstYear.getText())) {
				throw new ValidationException("設立年月日_年を入力してください。");
			}
			// 設立年月日_月
			if (InputCheck.isNullOrBlank(inputEstMonth.getText())) {
				throw new ValidationException("設立年月日_月を入力してください。");
			}
			// 設立年月日_日
			if (InputCheck.isNullOrBlank(inputEstDay.getText())) {
				throw new ValidationException("設立年月日_日を入力してください。");
			}
		}
		if (!inputEstYear.getText().isBlank()) {
			if (!patYear.matcher(inputEstYear.getText()).matches()) {
				throw new ValidationException("設立年月日_年は4桁数字(YYYY)で指定してください。");
			}
		}
		if (!inputEstMonth.getText().isBlank()) {
			if (!patMonth.matcher(inputEstMonth.getText()).matches()) {
				throw new ValidationException("正確な設立年月日_月(MM)を指定してください。");
			}
		}
		if (!inputEstDay.getText().isBlank()) {
			if (!patDay.matcher(inputEstDay.getText()).matches()) {
				throw new ValidationException("正確な設立年月日_日(DD)を指定してください。");
			}
		}
		if (!inputEstYear.getText().isBlank() && !inputEstMonth.getText().isBlank()
				&& !inputEstDay.getText().isBlank()) {
			String estDateString = inputEstYear.getText()
					+ String.format("%02d", Integer.parseInt(inputEstMonth.getText()))
					+ String.format("%02d", Integer.parseInt(inputEstDay.getText()));
			if (!InputCheck.isVaildDate(estDateString, "yyyyMMdd")) {
				throw new ValidationException("設立年月日は有効な日付ではありません。");
			}
		}
		//決算期
		if (InputCheck.isNullOrBlank(inputSettlPeriod.getText())) {
			throw new ValidationException("決算期を入力してください。");
		}
		if (!patSettlPeriod.matcher(inputSettlPeriod.getText()).matches()) {
			throw new ValidationException("決算期は3桁以内数字で指定してください。");
		}
		// 期首年
		if (InputCheck.isNullOrBlank(inputKisyuYear.getText())) {
			throw new ValidationException("事業年度期首（本年度）_年を入力してください。");
		}
		if (!patYear.matcher(inputKisyuYear.getText()).matches()) {
			throw new ValidationException("事業年度期首（本年度）_年は4桁数字(YYYY)で指定してください。");
		}
		// 期首月
		if (InputCheck.isNullOrBlank(inputKisyuMonth.getText())) {
			throw new ValidationException("事業年度期首（本年度）_月を入力してください。");
		}
		if (!patMonth.matcher(inputKisyuMonth.getText()).matches()) {
			throw new ValidationException("正確な事業年度期首（本年度）_月(MM)を指定してください。");
		}
		// 期末年
		if (InputCheck.isNullOrBlank(inputKimatuYear.getText())) {
			throw new ValidationException("事業年度期末（本年度）_年を入力してください。");
		}
		if (!patYear.matcher(inputKimatuYear.getText()).matches()) {
			throw new ValidationException("事業年度期末（本年度）_年は4桁数字(YYYY)で指定してください。");
		}
		// 期末月
		if (InputCheck.isNullOrBlank(inputKimatuMonth.getText())) {
			throw new ValidationException("事業年度期末（本年度）_月を入力してください。");
		}
		if (!patMonth.matcher(inputKimatuMonth.getText()).matches()) {
			throw new ValidationException("正確な事業年度期末（本年度）_月(MM)を指定してください。");
		}
		
		//期首期末整合性
		LocalDate kisyuLocal = LocalDate.of(Integer.parseInt(inputKisyuYear.getText()),
				Integer.parseInt(inputKisyuMonth.getText()), 1);
		LocalDate kimatuLocal = LocalDate.of(Integer.parseInt(inputKimatuYear.getText()),
				Integer.parseInt(inputKimatuMonth.getText()), 1);
		if(!kisyuLocal.plusMonths(11).isEqual(kimatuLocal)) {
			throw new ValidationException("事業年度期首から事業年度期末まで1年間を設定してください。");
		}
		//入力開始年
		if (InputCheck.isNullOrBlank(inputStartYear.getText())) {
			throw new ValidationException("入力開始年を入力してください。");
		}
		if (!patYear.matcher(inputStartYear.getText()).matches()) {
			throw new ValidationException("入力開始_年は4桁数字(YYYY)で指定してください。");
		}
		//入力開始月
		if (InputCheck.isNullOrBlank(inputStartMonth.getText())) {
			throw new ValidationException("入力開始_月を入力してください。");
		}
		if (!patMonth.matcher(inputStartMonth.getText()).matches()) {
			throw new ValidationException("正確な入力開始_月(MM)を指定してください。");
		}
		//電話番号
		if (!inputTelNo.getText().isBlank()) {
			if (!pattel.matcher(inputTelNo.getText()).matches()) {
				throw new ValidationException("9桁または10桁数字の電話番号を指定してください。");
			}
		}
		//FAX
		if (!inputFaxNo.getText().isBlank()) {
			if (!pattel.matcher(inputFaxNo.getText()).matches()) {
				throw new ValidationException("9桁または10桁数字のFAX番号を指定してください。");
			}
		}
		//Email
		if (!inputEmail.getText().isBlank()) {
			if (!patMail.matcher(inputEmail.getText()).matches()) {
				throw new ValidationException("正確なE-mailを指定してください。");
			}
		}
		//代表取締役代表取締役名前
		if (!inputDirectorName.getText().isBlank()) {
			if (inputDirectorName.getText().length() > 40) {
				throw new ValidationException("代表取締役名前は40文字以内で指定してください。");
			}
		}
	}
	
	private void clearData() {
		businessTypeRadio1.setSelected(true);
		businessLineRadio1.setSelected(true);
		blueDecRadio1.setSelected(true);
		inputCompanyName.setText("");
		inputCompanyNameKana.setText("");
		inputAddress.setText("");
		inputCompanyNo.setText("");
		inputEstYear.setText("");
		inputEstMonth.setText("");
		inputEstDay.setText("");
		inputSettlPeriod.setText("");
		inputKisyuYear.setText("");
		inputKisyuMonth.setText("");
		inputKimatuYear.setText("");
		inputKimatuMonth.setText("");
		inputStartYear.setText("");
		inputStartMonth.setText("");
		inputTelNo.setText("");
		inputFaxNo.setText("");
		inputEmail.setText("");
		inputDirectorName.setText("");
		
		taxKbnRadio1.setSelected(true);
		shortFlgRadio1.setSelected(true);
	}

}
