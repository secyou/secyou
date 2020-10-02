package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.d.CompanyData;
import com.busicomjp.sapp.service.d.D1Service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;

@Component
public class D1UpdateCompanyDialogController extends BaseController implements Initializable {

	static final Logger log = LoggerFactory.getLogger(D1UpdateCompanyDialogController.class);

	/**
	 * 会社選択フラグ
	 */
	@Getter
	private static boolean companySelectedFlg;
	@Autowired
	private D1Service z1Service;
	@Autowired
	private D1Controller d1Controller;

	@FXML
	private TableView<CompanyData> companyDataTable;
	@FXML
	private Label kimatuYear;
	@FXML
	private Label fiscalYear;
	@FXML
	private TextField newCompanyName;
	@FXML
	private ToggleGroup taxKbnGroup;
	@FXML
	private ToggleGroup taxSummaryGroup;
	@FXML
	private RadioButton radio1;
	@FXML
	private RadioButton radio2;
	@FXML
	private RadioButton radio3;
	@FXML
	private RadioButton radio4;
	@FXML
	private RadioButton radio5;
	
	@FXML
	private TextField newCompanyNameKana;
	@FXML
	private TextField newAddress;
	@FXML
	private TextField newCompanyNo;
	@FXML
	private TextField newEstYear;
	@FXML
	private TextField newEstMonth;
	@FXML
	private TextField newEstDay;
	@FXML
	private TextField newSettlPeriod;
	@FXML
	private TextField newKisyuYear;
	@FXML
	private TextField newKisyuMonth;
	@FXML
	private TextField newKimatuYear;
	@FXML
	private TextField newKimatuMonth;
	@FXML
	private TextField newStartYear;
	@FXML
	private TextField newStartMonth;
	@FXML
	private TextField newTelNo;
	@FXML
	private TextField newFaxNo;
	@FXML
	private TextField newEmail;
	@FXML
	private TextField newDirectorName;
	
	@FXML
	private ToggleGroup companyKbnGroup;
	@FXML
	private ToggleGroup businessTypeGroup;
	@FXML
	private ToggleGroup businessLineGroup;
	@FXML
	private ToggleGroup blueDecGroup;
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
	
	public void init(String companyCode, String kimatuYear) {
		companySelectedFlg = true;
		CompanyData companyData = z1Service.getCompanyByCodeAndKimatuYear(companyCode, kimatuYear);
		if ("1".equals(companyData.getBusinessType())) {
			businessTypeRadio1.setSelected(true);
		} else {
			businessTypeRadio2.setSelected(true);
		}
		newCompanyName.setText(companyData.getCompanyName());
		newCompanyNameKana.setText(companyData.getCompanyNameKana());
		newAddress.setText(companyData.getStreetAddress());
		if ("1".equals(companyData.getBusinessLine())) {
			businessLineRadio1.setSelected(true);
		} else {
			businessLineRadio2.setSelected(true);
		}
		if ("1".equals(companyData.getBuleDec())) {
			blueDecRadio1.setSelected(true);
		} else {
			blueDecRadio2.setSelected(true);
		}
		newCompanyNo.setText(companyData.getCorpNumber());
		newEstYear.setText(companyData.getEstDate().substring(0, 4));
		newEstMonth.setText(companyData.getEstDate().substring(4, 6));
		newEstDay.setText(companyData.getEstDate().substring(6));
		newSettlPeriod.setText(companyData.getSettlPeriod());
		newKisyuYear.setText(companyData.getKisyuYearMonth().substring(0, 4));
		newKisyuMonth.setText(companyData.getKisyuYearMonth().substring(4));
		newKimatuYear.setText(companyData.getKimatuYear());
		newKimatuMonth.setText(companyData.getKimatuYearMonth().substring(4));
		newStartYear.setText(companyData.getInputStartYearMonth().substring(0, 4));
		newStartMonth.setText(companyData.getInputStartYearMonth().substring(4));
		newTelNo.setText(companyData.getTelNumber());
		newFaxNo.setText(companyData.getFaxNumber());
		newEmail.setText(companyData.getMailAddress());
		newDirectorName.setText(companyData.getDirectorName());
		// 消費税事業区分
		if ("1".equals(companyData.getTaxKindFlg())) {
			taxKbnRadio1.setSelected(true);
		} else if ("2".equals(companyData.getTaxKindFlg())) {
			taxKbnRadio2.setSelected(true);
		} else {
			taxKbnRadio3.setSelected(true);
			taxSummary1.setDisable(true);
			taxSummary2.setDisable(true);
			taxSummary3.setDisable(true);
		}
		// 消費税集計区分
		if ("1".equals(companyData.getTaxAppFlg())) {
			taxSummary1.setSelected(true);
		} else if ("2".equals(companyData.getTaxAppFlg())) {
			taxSummary2.setSelected(true);
		} else if ("3".equals(companyData.getTaxAppFlg())) {
			taxSummary3.setSelected(true);
		}
		// 期間短縮有無
		if ("1".equals(companyData.getShortFlg())) {
			shortFlgRadio1.setSelected(true);
		} else {
			shortFlgRadio2.setSelected(true);
		}
	}

	@FXML
	private void onUpdateCompany(Event eve) {
		if (commonAlert.showConfirmationAlert("変更処理を実行しますか？")) {
			// 税区分変更チェック
			if (isChangedTaxKind() || isChangedTaxApp()) {
				// 前期繰越データ以外のデータが存在している場合
				if (getCarryforwardJournalEntryCount() > 0) {
					throw new ValidationException(
							"仕訳データが既に登録したため、消費税事業区分、消費税集計区分は変更できません。\n" + "登録済みの仕訳データを削除してから変更を行ってください。");
				}
			}

			// 入力チェック
			inputCheck();

			CompanyMDto companyMDto = new CompanyMDto();
			companyMDto.setCompanyCode(CompanyUtil.getCompanyCode());
			companyMDto.setCompanyName(newCompanyName.getText());
			// 事業タイプ
			if (businessTypeRadio1.isSelected()) {
				// 法人
				companyMDto.setBusinessType("1");
			} else {
				// 個人
				companyMDto.setBusinessType("2");
			}
			// 会社名(個人事業主名)
			companyMDto.setCompanyName(newCompanyName.getText());
			// 会社名カナ(個人事業主名カナ)
			companyMDto.setCompanyNameKana(newCompanyNameKana.getText());
			// 会社住所
			companyMDto.setStreetAddress(newAddress.getText());
			// 法人番号
			companyMDto.setCorpNumber(newCompanyNo.getText());
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
			companyMDto.setEstDate(newEstYear.getText() + String.format("%02d", Integer.parseInt(newEstMonth.getText()))
					+ String.format("%02d", Integer.parseInt(newEstDay.getText())));
			// 決算期
			companyMDto.setSettlPeriod(String.format("%03d", Integer.parseInt(newSettlPeriod.getText())));
			// 事業年度期首
			companyMDto.setKisyuYearMonth(
					newKisyuYear.getText() + String.format("%02d", Integer.parseInt(newKisyuMonth.getText())));
			// 事業年度期末
			companyMDto.setKimatuYearMonth(
					newKimatuYear.getText() + String.format("%02d", Integer.parseInt(newKimatuMonth.getText())));
			// 入力開始年月
			companyMDto.setInputStartYearMonth(
					newStartYear.getText() + String.format("%02d", Integer.parseInt(newStartMonth.getText())));
			// 電話番号
			companyMDto.setTelNumber(newTelNo.getText());
			// FAX
			companyMDto.setFaxNumber(newFaxNo.getText());
			// Email
			companyMDto.setMailAddress(newEmail.getText());
			// 代表取締役名前
			companyMDto.setDirectorName(newDirectorName.getText());
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
			companyMDto.setKimatuYear(newKimatuYear.getText());
			// 期末月日
			String formattedKimatuMonth = String.format("%02d", Integer.parseInt(newKimatuMonth.getText()));
			String kimatuDate = StringUtil.GetLastDay(newKimatuYear.getText() + formattedKimatuMonth);
			companyMDto.setKimatuMonthDay(formattedKimatuMonth.concat(kimatuDate));
			
			// 変更処理
			z1Service.updateCompanyData(companyMDto);
			((Node) eve.getSource()).getScene().getWindow().hide();
			d1Controller.init();
		}
	}

	private boolean isChangedTaxKind() {
		return (taxKbnRadio1.isSelected()
				&& ("2".equals(CompanyUtil.getTaxKindFlg()) || "3".equals(CompanyUtil.getTaxKindFlg())))
				|| (taxKbnRadio2.isSelected()
						&& ("1".equals(CompanyUtil.getTaxKindFlg()) || "3".equals(CompanyUtil.getTaxKindFlg())))
				|| (taxKbnRadio3.isSelected()
						&& ("1".equals(CompanyUtil.getTaxKindFlg()) || "2".equals(CompanyUtil.getTaxKindFlg())));
	}

	private boolean isChangedTaxApp() {
		if ("".equals(CompanyUtil.getTaxAppFlg())) {
			return taxSummary1.isSelected() || taxSummary2.isSelected() || taxSummary3.isSelected();
		}

		if ("1".equals(CompanyUtil.getTaxAppFlg())) {
			return taxSummary2.isSelected() || taxSummary3.isSelected() || taxKbnRadio3.isSelected();
		}

		if ("2".equals(CompanyUtil.getTaxAppFlg())) {
			return taxSummary1.isSelected() || taxSummary3.isSelected() || taxKbnRadio3.isSelected();
		}

		if ("3".equals(CompanyUtil.getTaxAppFlg())) {
			return taxSummary1.isSelected() || taxSummary2.isSelected() || taxKbnRadio3.isSelected();
		}
		return false;
	}

	private Integer getCarryforwardJournalEntryCount() {
		CompanyDto companyDto = new CompanyDto();
		companyDto.setCompanyCode(CompanyUtil.getCompanyCode());
		companyDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		companyDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		return z1Service.getCarryforwardJournalEntryCount(companyDto);
	}

	@FXML
	private void inputCheck() {
		// 会社名
		if (InputCheck.isNullOrBlank(newCompanyName.getText())) {
			throw new ValidationException("会社名(個人事業主名)を入力してください。");
		}
		if (newCompanyName.getText().length() > 100) {
			throw new ValidationException("会社(個人事業主)名は100文字以内で指定してください。");
		}
		// 会社名カナ
		if (InputCheck.isNullOrBlank(newCompanyNameKana.getText())) {
			throw new ValidationException("会社名(個人事業主名)かなを入力してください。");
		}
		if (newCompanyNameKana.getText().length() > 100) {
			throw new ValidationException("会社名(個人事業主名)かなは100文字以内で指定してください。");
		}
		// 住所
		if (InputCheck.isNullOrBlank(newAddress.getText())) {
			throw new ValidationException("住所を入力してください。");
		}
		if (newAddress.getText().length() > 100) {
			throw new ValidationException("住所は100文字以内で指定してください。");
		}
		// 法人番号
		if (businessTypeRadio1.isSelected()) {
			if (InputCheck.isNullOrBlank(newCompanyNo.getText())) {
				throw new ValidationException("法人番号を入力してください");
			}
			if (!patCorpNumber.matcher(newCompanyNo.getText()).matches()) {
				throw new ValidationException("12桁または13桁数字の法人番号を指定してください。");
			}
			// 設立年月日_年
			if (InputCheck.isNullOrBlank(newEstYear.getText())) {
				throw new ValidationException("設立年月日_年を入力してください。");
			}
			// 設立年月日_月
			if (InputCheck.isNullOrBlank(newEstMonth.getText())) {
				throw new ValidationException("設立年月日_月を入力してください。");
			}
			// 設立年月日_日
			if (InputCheck.isNullOrBlank(newEstDay.getText())) {
				throw new ValidationException("設立年月日_日を入力してください。");
			}
		}
		if (!newEstYear.getText().isBlank()) {
			if (!patYear.matcher(newEstYear.getText()).matches()) {
				throw new ValidationException("設立年月日_年は4桁数字(YYYY)で指定してください。");
			}
		}
		if (!newEstMonth.getText().isBlank()) {
			if (!patMonth.matcher(newEstMonth.getText()).matches()) {
				throw new ValidationException("正確な設立年月日_月(MM)を指定してください。");
			}
		}
		if (!newEstDay.getText().isBlank()) {
			if (!patDay.matcher(newEstDay.getText()).matches()) {
				throw new ValidationException("正確な設立年月日_日(DD)を指定してください。");
			}
		}
		if (!newEstYear.getText().isBlank() && !newEstMonth.getText().isBlank() && !newEstDay.getText().isBlank()) {
			String estDateString = newEstYear.getText() + String.format("%02d", Integer.parseInt(newEstMonth.getText()))
					+ String.format("%02d", Integer.parseInt(newEstDay.getText()));
			if (!InputCheck.isVaildDate(estDateString, "yyyyMMdd")) {
				throw new ValidationException("設立年月日は有効な日付ではありません");
			}
		}
		// 決算期
		if (InputCheck.isNullOrBlank(newSettlPeriod.getText())) {
			throw new ValidationException("決算期を入力してください。");
		}
		if (!patSettlPeriod.matcher(newSettlPeriod.getText()).matches()) {
			throw new ValidationException("決算期は3桁以内数字で指定してください。");
		}
		// 期首年
		if (InputCheck.isNullOrBlank(newKisyuYear.getText())) {
			throw new ValidationException("事業年度期首（本年度）_年を入力してください。");
		}
		if (!patYear.matcher(newKisyuYear.getText()).matches()) {
			throw new ValidationException("事業年度期首（本年度）_年は4桁数字(YYYY)で指定してください。");
		}
		// 期首月
		if (InputCheck.isNullOrBlank(newKisyuMonth.getText())) {
			throw new ValidationException("事業年度期首（本年度）_月を入力してください。");
		}
		if (!patMonth.matcher(newKisyuMonth.getText()).matches()) {
			throw new ValidationException("正確な事業年度期首（本年度）_月(MM)を指定してください。");
		}
		// 期末年
		if (InputCheck.isNullOrBlank(newKimatuYear.getText())) {
			throw new ValidationException("事業年度期末（本年度）_年を入力してください。");
		}
		if (!patYear.matcher(newKimatuYear.getText()).matches()) {
			throw new ValidationException("事業年度期末（本年度）_年は4桁数字(YYYY)で指定してください。");
		}
		// 期末月
		if (InputCheck.isNullOrBlank(newKimatuMonth.getText())) {
			throw new ValidationException("事業年度期末（本年度）_月を入力してください。");
		}
		if (!patMonth.matcher(newKimatuMonth.getText()).matches()) {
			throw new ValidationException("正確な事業年度期末（本年度）_月(MM)を指定してください。");
		}

		// 期首期末整合性
		LocalDate kisyuLocal = LocalDate.of(Integer.parseInt(newKisyuYear.getText()),
				Integer.parseInt(newKisyuMonth.getText()), 1);
		LocalDate kimatuLocal = LocalDate.of(Integer.parseInt(newKimatuYear.getText()),
				Integer.parseInt(newKimatuMonth.getText()), 1);
		if (!kisyuLocal.plusMonths(11).isEqual(kimatuLocal)) {
			throw new ValidationException("事業年度期首から事業年度期末まで1年間を設定してください。");
		}
		// 入力開始年
		if (InputCheck.isNullOrBlank(newStartYear.getText())) {
			throw new ValidationException("入力開始年を入力してください。");
		}
		if (!patYear.matcher(newStartYear.getText()).matches()) {
			throw new ValidationException("入力開始_年は4桁数字(YYYY)で指定してください。");
		}
		// 入力開始月
		if (InputCheck.isNullOrBlank(newStartMonth.getText())) {
			throw new ValidationException("入力開始_月を入力してください。");
		}
		if (!patMonth.matcher(newStartMonth.getText()).matches()) {
			throw new ValidationException("正確な入力開始_月(MM)を指定してください。");
		}
		// 電話番号
		if (!newTelNo.getText().isBlank()) {
			if (!pattel.matcher(newTelNo.getText()).matches()) {
				throw new ValidationException("9桁または10桁数字の電話番号を指定してください。");
			}
		}
		// FAX
		if (!newFaxNo.getText().isBlank()) {
			if (!pattel.matcher(newFaxNo.getText()).matches()) {
				throw new ValidationException("9桁または10桁数字のFAX番号を指定してください");
			}
		}
		// Email
		if (!newEmail.getText().isBlank()) {
			if (!patMail.matcher(newEmail.getText()).matches()) {
				throw new ValidationException("正確なE-mailを指定してください。");
			}
		}
		// 代表取締役代表取締役名前
		if (!newDirectorName.getText().isBlank()) {
			if (newDirectorName.getText().length() > 40) {
				throw new ValidationException("代表取締役名前は40文字以内で指定してください");
			}
		}
	}
}
