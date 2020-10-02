package com.busicomjp.sapp.controller.a;

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
import com.busicomjp.sapp.common.converter.DatePickerConverter;
import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.FeatureGroupController;
import com.busicomjp.sapp.controller.z.Z1HintController;
import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.model.a.JournalEntryData;
import com.busicomjp.sapp.service.a.A2Service;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

@Component
public class A2SearchController extends Z1HintController implements Initializable {
	private Logger logger = LoggerFactory.getLogger(A2SearchController.class);
	private final String CONST_DATE_PATTERN = "yyyy/MM/dd";
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CONST_DATE_PATTERN);

	static String selectedCompanyCod = "";

	@Autowired
	private FeatureGroupController featureController;
	@Autowired
	private A2ShowController a2ShowController;
	@FXML
	private GridPane a2_search;
	// 取引発生日
	@FXML
	private DatePicker searchStartDate;
	@FXML
	private DatePicker searchEndDate;
	
	// 金額
	@FXML
	private TextField searchAmountMoneyStart;
	@FXML
	private TextField searchAmountMoneyEnd;
	// 入金・支払予定日
	@FXML
	private DatePicker searchDepPayStartDate;
	@FXML
	private DatePicker searchDepPayEndDate;

	@Autowired
	private A2Service a2Service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		z1init(true);
		searchStartDate.setConverter(new DatePickerConverter());
		searchEndDate.setConverter(new DatePickerConverter());
		searchDepPayStartDate.setConverter(new DatePickerConverter());
		searchDepPayEndDate.setConverter(new DatePickerConverter());
	}

	@Override
	public void hintClickAction(String hintKbn, String hintCode, String hintName) {
		if (HINT_KBN_TORIHIKISAKI.equals(hintKbn)) {
			// 取引先情報を設定
			searchSuppliersCode.setText(hintCode);
			searchSuppliersName.setText(hintName);
			searchTekiyoName.requestFocus();
		} else if (HINT_KBN_TEKIYO.equals(hintKbn)) {
			// 摘要情報を設定
			searchTekiyoCode.setText(hintCode);
			searchTekiyoName.setText(hintName);
			searchDebitName.requestFocus();
		} else if (HINT_KBN_DEBIT.equals(hintKbn)) {
			// 借方勘定科目
			searchDebitCode.setText(hintCode);
			searchDebitName.setText(hintName);
			searchCreditName.requestFocus();
		} else if (HINT_KBN_CREDIT.equals(hintKbn)) {
			// 貸方勘定科目
			searchCreditCode.setText(hintCode);
			searchCreditName.setText(hintName);
			searchAmountMoneyStart.requestFocus();
		}
	}

	@FXML
	public void onSearchData() {
		logger.info("仕訳帳検索 開始");

		a2ShowController.setSearchResult(new JournalEntryDto(), new ArrayList<JournalEntryData>());

		// 入力チェック
		inputCheck();
		// 検索実行
		JournalEntryDto journal = getSearchCondition();
		List<JournalEntryData> searchResultList = a2Service.getJournalList(journal);
		a2ShowController.setSearchResult(journal, searchResultList);

		// Tab切替
		featureController.selectTab(featureController.getA2show());

		logger.info("仕訳帳検索 終了");
	}

	public void init() {
		searchHint = true;
		
		bindingKeyPress();
		// 取引発生日
		String companyCode = CompanyUtil.getCompanyCode();
		selectedCompanyCod = companyCode;
		if (!InputCheck.isNullOrBlank(companyCode)) {
			LocalDate localDateEnd = getKimatuDate(companyCode);
			if (localDateEnd != null) {
				LocalDate _localDate = localDateEnd.plusYears(-1).plusMonths(1);
				LocalDate localDateStart = LocalDate.of(_localDate.getYear(), _localDate.getMonth(), 1);
				searchStartDate.getEditor().setText(localDateStart.format(dateFormatter));
				searchStartDate.setValue(localDateStart);
				searchEndDate.getEditor().setText(localDateEnd.format(dateFormatter));
				searchEndDate.setValue(localDateEnd);
			} else {
				searchStartDate.getEditor().setText("");
				searchEndDate.getEditor().setText("");
			}
		}
		
		Callback<DatePicker, DateCell> dateRangeFactory = dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);

				LocalDate localDateStart = null;
				String companyCode = CompanyUtil.getCompanyCode();
				if (!InputCheck.isNullOrBlank(companyCode)) {
					LocalDate localDateEnd = getKimatuDate(companyCode);
					if (localDateEnd != null) {
						LocalDate _localDate = localDateEnd.plusYears(-1).plusMonths(1);
						localDateStart = LocalDate.of(_localDate.getYear(), _localDate.getMonth(), 1);

						if (item.isBefore(localDateStart) || item.isAfter(localDateStart.plusYears(1).plusDays(-1))) {
							setStyle("-fx-background-color: #939598;");
							Platform.runLater(() -> setDisable(true));
						}
					}
				}
			}
		};
		searchStartDate.setDayCellFactory(dateRangeFactory);
		searchEndDate.setDayCellFactory(dateRangeFactory);

		if (selectedCompanyCod.equals(companyCode)) {
			// 前回表示した会社コードを同じ場合、初期化をスキップ
			return;
		}

		// 取引先
		searchSuppliersName.setText("");
		searchSuppliersCode.setText("");
		// 摘要
		searchTekiyoName.setText("");
		searchTekiyoCode.setText("");
		// 借方勘定科目
		searchDebitName.setText("");
		searchDebitCode.setText("");
		// 貸方勘定科目
		searchCreditName.setText("");
		searchCreditCode.setText("");
		// 金額
		searchAmountMoneyStart.setText("");
		searchAmountMoneyEnd.setText("");
		// 入金・支払予定日
		searchDepPayStartDate.getEditor().setText("");
		searchDepPayEndDate.getEditor().setText("");

		try {
			clearHintData();
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

	private void inputCheck() {

		// 取引発生日
		LocalDate _searchStartDate = null;
		if (!InputCheck.isVaildDate(searchStartDate)) {
			throw new ValidationException("取引開始日はYYYY/MM/DD形式の日付を入力してください。");
		} else {
			_searchStartDate = searchStartDate.getValue();
		}
		LocalDate _searchEndDate = null;
		if (!InputCheck.isVaildDate(searchEndDate)) {
			throw new ValidationException("取引終了日はYYYY/MM/DD形式の日付を入力してください。");
		} else {
			_searchEndDate = searchEndDate.getValue();
		}
		if (_searchStartDate != null && _searchEndDate != null) {
			if (_searchStartDate.compareTo(_searchEndDate) > 0) {
				throw new ValidationException("取引開始日が取引終了日より大きいです。");
			}
		}

		LocalDate defaultStartDay = LocalDate.parse(CompanyUtil.getAccountStartDay(), DateTimeFormatter.ofPattern("yyyyMMdd"));
		LocalDate defaultEndDay = LocalDate.parse(CompanyUtil.getAccountEndDay(), DateTimeFormatter.ofPattern("yyyyMMdd"));
		if (_searchStartDate.compareTo(defaultStartDay) < 0) {
			throw new ValidationException("取引開始日は"+ defaultStartDay.format(dateFormatter) +"以降の日付を指定してください。");
		}

		if (defaultEndDay.compareTo(defaultEndDay) > 0) {
			throw new ValidationException("取引終了日は"+ defaultEndDay.format(dateFormatter) +"以前の日付を指定してください。");
		}

		// 金額
		String _amountMoneyStart = StringUtil.replaceFull2HalfNumber(searchAmountMoneyStart.getText());
		if (!InputCheck.isNumeric(_amountMoneyStart)) {
			throw new ValidationException("開始金額は整数で入力してください。");
		}
		String _amountMoneyEnd = StringUtil.replaceFull2HalfNumber(searchAmountMoneyEnd.getText());
		if (!InputCheck.isNumeric(_amountMoneyEnd)) {
			throw new ValidationException("終了金額は整数で入力してください。");
		}
		// 入金・支払予定日
		LocalDate _depPayStartDate = null;
		if (!InputCheck.isVaildDate(searchDepPayStartDate)) {
			throw new ValidationException("入金・支払予定開始日はYYYY/MM/DD形式で入力してください。");
		} else {
			_depPayStartDate = searchDepPayStartDate.getValue();
		}
		LocalDate _depPayEndDate = null;
		if (!InputCheck.isVaildDate(searchDepPayEndDate)) {
			throw new ValidationException("入金・支払予定終了日はYYYY/MM/DD形式で入力してください。");
		} else {
			_depPayEndDate = searchDepPayEndDate.getValue();
		}
		if (_depPayStartDate != null && _depPayEndDate != null) {
			if (_depPayStartDate.compareTo(_depPayEndDate) > 0) {
				throw new ValidationException("入金・支払予定開始日が入金・支払予定終了日より大きいです。");
			}
		}
	}

	private JournalEntryDto getSearchCondition() {
		JournalEntryDto journal = new JournalEntryDto();

		// 会社コード
		journal.setCompanyCode(CompanyUtil.getCompanyCode());
		LocalDate localDateEnd = null;
		String companyCode = CompanyUtil.getCompanyCode();
		if (!InputCheck.isNullOrBlank(companyCode)) {
			localDateEnd = getKimatuDate(companyCode);
		}
		// 取引発生日
		String _accrualStartDate = searchStartDate.getEditor().getText();
		if (!InputCheck.isNullOrBlank(_accrualStartDate)) {
			_accrualStartDate = _accrualStartDate.replaceAll("/", "");
			journal.setAccrualStartDate(_accrualStartDate);
		} else {
			if (localDateEnd != null) {
				LocalDate _localDate = localDateEnd.plusYears(-1).plusMonths(1);
				LocalDate localDateStart = LocalDate.of(_localDate.getYear(), _localDate.getMonth(), 1);
				journal.setAccrualStartDate(localDateStart.format(dateFormatter));
			}
		}

		String _accrualEndDate = searchEndDate.getEditor().getText();
		if (!InputCheck.isNullOrBlank(_accrualEndDate)) {
			_accrualEndDate = _accrualEndDate.replaceAll("/", "");
			journal.setAccrualEndDate(_accrualEndDate);
		} else {
			if (localDateEnd != null) {
				journal.setAccrualEndDate(localDateEnd.format(dateFormatter));
			}
		}

		// 取引先
		String _suppliersCode = searchSuppliersCode.getText();
		String _suppliersName = searchSuppliersName.getText();
		if (StringUtils.isEmpty(_suppliersName)) {
			_suppliersCode = "";
		}
		if (!InputCheck.isNullOrBlank(_suppliersCode)) {
			journal.setSuppliersCode(_suppliersCode);
		} else if (!InputCheck.isNullOrBlank(_suppliersName)) {
			journal.setSuppliersName(_suppliersName);
		}
		// 適要
		String _tekiyoCode = searchTekiyoCode.getText();
		String _tekiyoName = searchTekiyoName.getText();
		if (StringUtils.isEmpty(_tekiyoName)) {
			_tekiyoCode = "";
		}
		if (!InputCheck.isNullOrBlank(_tekiyoCode)) {
			journal.setTekiyoCode(_tekiyoCode);
		} else if (!InputCheck.isNullOrBlank(_tekiyoName)) {
			journal.setTekiyoName(_tekiyoName);
		}
		// 借方勘定科目
		String _debitAccount = searchDebitCode.getText();
		String _debitAccountName = searchDebitName.getText();
		if (StringUtils.isEmpty(_debitAccountName)) {
			_debitAccount = "";
		}
		if (!InputCheck.isNullOrBlank(_debitAccount)) {
			journal.setDebitAccount(_debitAccount);
		} else if (!InputCheck.isNullOrBlank(_debitAccountName)) {
			journal.setDebitAccountName(_debitAccountName);
		}
		// 貸方勘定科目
		String _creditAccount = searchCreditCode.getText();
		String _creditAccountName = searchCreditName.getText();
		if (StringUtils.isEmpty(_creditAccountName)) {
			_creditAccount = "";
		}
		if (!InputCheck.isNullOrBlank(_creditAccount)) {
			journal.setCreditAccount(_creditAccount);
		} else if (!InputCheck.isNullOrBlank(_creditAccountName)) {
			journal.setCreditAccountName(_creditAccountName);
		}
		// 金額
		String _amountMoneyStart = StringUtil.replaceFull2HalfNumber(searchAmountMoneyStart.getText());
		if (!InputCheck.isNullOrBlank(_amountMoneyStart)) {
			journal.setAmountMoneyStart(_amountMoneyStart);
		}
		String _amountMoneyEnd = StringUtil.replaceFull2HalfNumber(searchAmountMoneyEnd.getText());
		if (!InputCheck.isNullOrBlank(_amountMoneyEnd)) {
			journal.setAmountMoneyEnd(_amountMoneyEnd);
		}
		// 入金・支払予定日
		String _depPayStartDate = searchDepPayStartDate.getEditor().getText();
		if (!InputCheck.isNullOrBlank(_depPayStartDate)) {
			_depPayStartDate = _depPayStartDate.replaceAll("/", "");
			journal.setDepPayStartDate(_depPayStartDate);
		}
		String _depPayEndDate = searchDepPayEndDate.getEditor().getText();
		if (!InputCheck.isNullOrBlank(_depPayEndDate)) {
			_depPayEndDate = _depPayEndDate.replaceAll("/", "");
			journal.setDepPayEndDate(_depPayEndDate);
		}

		return journal;
	}

	private LocalDate getKimatuDate(String companyCode) {
		LocalDate kimatuDate = null;
		String kimatuMonthDay = CompanyUtil.getKimatuMonthDay();
		if (!InputCheck.isNullOrBlank(kimatuMonthDay)) {
			int year = Integer.parseInt(CompanyUtil.getKimatuYear());
			int month = Integer.parseInt(kimatuMonthDay.substring(0, 2));
			int dayOfMonth = Integer.parseInt(kimatuMonthDay.substring(2));

			kimatuDate = LocalDate.of(year, month, dayOfMonth);
		}
		return kimatuDate;
	}
	
	private void bindingKeyPress() {
		App.clearKeyEvent();
		KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
		App.putKeyEvent(f1, new Runnable() {
			@Override
			public void run() {
				onSearchData();
			}
		});
	}
}
