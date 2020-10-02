package com.busicomjp.sapp.service.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.item.CommonDialog;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.a.A1AssistanceDialogController;
import com.busicomjp.sapp.controller.a.A1NotificationDialogController;
import com.busicomjp.sapp.dto.a.GeneralLedgerDto;
import com.busicomjp.sapp.dto.a.InputDataDto;
import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.model.a.BalanceData;
import com.busicomjp.sapp.model.a.JournalEntryData;
import com.busicomjp.sapp.model.z.AccountData;
import com.busicomjp.sapp.repository.a.A0InputDataRepository;
import com.busicomjp.sapp.repository.a.A1Repository;

import javafx.scene.control.Dialog;
import javafx.util.Pair;

@Service
public class A1Service {
	
	public final String KEY_DEBIT = "DEBIT";
	public final String KEY_CREDIT = "CREDIT";
	
	@Autowired
	private A1AssistanceDialogController a1AssistanceDialogController;
	
	@Autowired
	private A1NotificationDialogController a1NotificationDialogController;
	
	@Autowired
	private A0InputDataService a0InputDataService;
	
	@Autowired
	A1Repository a1Repo;
	
	@Autowired
	A0InputDataRepository a0InputDataRepository;
	
	@Autowired
	Validator validator;
	
	@Autowired
	private CommonDialog commonDialog;
	
	/**
     * 仕訳データを取得
     */
	public List<JournalEntryData> getJournalEntry() {
		List<JournalEntryData> resultList = new ArrayList<JournalEntryData>();
		CompanyDto companyDto = new CompanyDto();
		companyDto.setCompanyCode(CompanyUtil.getCompanyCode());
		companyDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		companyDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		List<JournalEntryData> repoList = a1Repo.getJournalEntryDataList(companyDto);

		if (repoList == null || repoList.isEmpty()) {
			for (int i = 0; i <= 9; i++) {
				resultList.add(new JournalEntryData());
			}
			return resultList;
		}

		if (repoList.size() == 10) {
			return repoList;
		}

		for (int i = 0; i <= 9 - repoList.size(); i++) {
			resultList.add(new JournalEntryData());
		}
		resultList.addAll(repoList);
		return resultList;
	}
	
	/**
     * リアル残高を取得
     */
	public List<BalanceData> getRealBalanceDataList() {
		CompanyDto companyDto = new CompanyDto();
		companyDto.setCompanyCode(CompanyUtil.getCompanyCode());
		companyDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		companyDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		List<BalanceData> dataList = a1Repo.getRealBalanceDataList(companyDto);
		
		long tax = 0l;
		long total = 0l;
		
		for(BalanceData data : dataList) {
			total = total + data.getBalance();
			if ("31".equals(data.getKbn())) {
				// 未払消費税
				tax = tax + data.getBalance();
			} else if ("32".equals(data.getKbn())) {
				// 仮受消費税
				tax = tax + data.getBalance(); //貸方勘定科目
			} else if ("33".equals(data.getKbn())) {
				// 仮払消費税
				tax = tax + data.getBalance();
			}
			
			if (data.getKbn().startsWith("2")) {
				// 貸方勘定科目の場合
				data.setBalance(data.getBalance() * -1);
			}
		}
		
		// 合計残高
		BalanceData totalData = new BalanceData();
		totalData.setKbn("00");
		totalData.setBalance(total);
		// 未払消費税残高
		BalanceData taxData = new BalanceData();
		taxData.setKbn("24");
		taxData.setBalance(tax * -1);
		dataList.add(totalData);
		dataList.add(taxData);
		
		return dataList;
	}
	
	/**
     * 予想残高を取得
     */
	public BalanceData getExpectBalance(String expectDay) {
		String today = StringUtil.getToday();
		String nextDay = StringUtil.getNextDay(today);
		
		long balance = a1Repo.getExpectBalanceData(CompanyUtil.getCompanyCode(), nextDay, expectDay);
		BalanceData balanceData = new BalanceData();
		balanceData.setBalance(balance);

		return balanceData;
	}
	
	
	/**
     * 分類コードに紐づけている勘定科目リストを取得
     */
	public Map<String, List<ComboItem>> getSelectedAccountList(String selectCode) {
		Map<String, List<ComboItem>> map = new HashMap<String, List<ComboItem>>();
		List<ComboItem> debitList = new ArrayList<ComboItem>();
		List<ComboItem> creditList = new ArrayList<ComboItem>();
		
		InputDataDto selectDto = new InputDataDto();
		selectDto.setCompanyCode(CompanyUtil.getCompanyCode());
		selectDto.setSelectCode(selectCode);
		List<ComboItem> dataList = a1Repo.getSelectedAccountList(selectDto);
		if (dataList != null && dataList.size() > 0) {
			for (ComboItem item : dataList) {
				if ("1".equals(item.getOption())) {
					// 借方の勘定科目の場合
					debitList.add(item);
				} else if ("2".equals(item.getOption())) {
					// 貸方の勘定科目の場合
					creditList.add(item);
				}
			}
		}
		
		if (debitList.size() == 0) {
			debitList.add(new ComboItem("", ""));
		}
		if (creditList.size() == 0) {
			creditList.add(new ComboItem("", ""));
		}
		
		map.put(KEY_DEBIT, debitList);
		map.put(KEY_CREDIT, creditList);
		return map;
	}
	
	/**
     * 入力データをチェックする
     */
	public void checkInputData(InputDataDto inputData) {
		String accrualDate = inputData.getAccrualDate();
		if (StringUtils.isEmpty(accrualDate)) {
			throw new ValidationException("発生日を入力してください。");
		}
		
		if (!InputCheck.isVaildDate(accrualDate, "yyyyMMdd")) {
			throw new ValidationException("発生日はMMDD形式の日付を入力してください。");
		}
		
		Set<ConstraintViolation<InputDataDto>> errorResult = validator.validate(inputData);
		if (!errorResult.isEmpty()) {
			// エラーあり
			throw new ValidationException(errorResult.iterator().next().getMessage());
		}

		if (!InputCheck.isVaildDate(inputData.getPaymentDate(), "yyyyMMdd")) {
			throw new ValidationException("入金・支払予定日(満期日)はYYYYMMDD形式の日付を入力してください。");
		}
		
		if (InputCheck.isBefore(inputData.getPaymentDate(), inputData.getAccrualDate(), "yyyyMMdd")) {
			throw new ValidationException("入金・支払予定日(満期日)は発生日以降の日付を入力してください。");
		}
		
		// 取引先相関チェック
		if (!torihikisakiRequiredCheck(inputData)) {
			throw new ValidationException("売掛金・買掛金・未払金に紐づく取引先を入力してください。");
		}
	}
	
	/**
     * 取引先必須チェック(チェックOK：true／チェックNG：false)
     */
	private boolean torihikisakiRequiredCheck(InputDataDto inputData) {
		if (StringUtils.isNotEmpty(inputData.getSuppliersCode())) {
			return true;
		}
		
		AccountData accountDataDebit = a0InputDataRepository.getAccountDetail(CompanyUtil.getCompanyCode(), inputData.getDebit());
		if (CommonConstants.ACCOUNT_KIND_UNION.RECEIVABLE.equals(accountDataDebit.getAccountKindUnion())
				|| CommonConstants.ACCOUNT_KIND_UNION.PAYABLE.equals(accountDataDebit.getAccountKindUnion())
				|| CommonConstants.ACCOUNT_KIND_UNION.UNPAID.equals(accountDataDebit.getAccountKindUnion())) {
			// 借方勘定科目が売掛金・買掛金・未払金の場合
			return false;
		}
		
		AccountData accountDataCredit = a0InputDataRepository.getAccountDetail(CompanyUtil.getCompanyCode(), inputData.getCredit());
		if (CommonConstants.ACCOUNT_KIND_UNION.RECEIVABLE.equals(accountDataCredit.getAccountKindUnion())
				|| CommonConstants.ACCOUNT_KIND_UNION.PAYABLE.equals(accountDataCredit.getAccountKindUnion())
				|| CommonConstants.ACCOUNT_KIND_UNION.UNPAID.equals(accountDataCredit.getAccountKindUnion())) {
			// 貸方勘定科目が売掛金・買掛金・未払金の場合
			return false;
		}
		
		return true;
	}
	
	/**
     * 入力データを編集する
     */
	public InputDataDto editInputData(InputDataDto inputData) {
		
		// 取引発生日
		String accrualDate = generateAccrualDate(StringUtil.replaceFull2HalfNumber(inputData.getAccrualDate()));
		inputData.setAccrualDate(accrualDate);
		
		// 金額(全角数字を半角に変更)
		String kingaku = StringUtil.replaceFull2HalfNumber(inputData.getKingaku());
		if (StringUtils.isNotEmpty(kingaku)) {
			kingaku =  kingaku.replace("ー", "-").replace("―", "-").replace("－", "-").replace("＋", "+");
		}
		inputData.setKingaku(kingaku);
		
		// 入金・支払予定日(全角数字を半角に変更)
		String paymentDate = StringUtil.replaceFull2HalfNumber(inputData.getPaymentDate());
		inputData.setPaymentDate(paymentDate);
		
		return inputData;
	}
	
	/**
     * 入力データを登録する
     */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void registInputData(InputDataDto inputData) {
		// 入力補助プロセス
		inputAssistanceProcess(inputData);
		// 入力データの登録
		a0InputDataService.inputData(inputData);
		
		// 手形自動振替処理
		InputDataDto iDto = new InputDataDto();
		iDto.setAccrualDate(inputData.getBillMaturityDate()); // 発生日に満期日を設定
		iDto.setKingaku(inputData.getKingaku()); // 選択した手形の金額
		iDto.setTaxCode(CommonConstants.TAX_CODE.TAX_EXEMPT); // 非課税
		iDto.setPaymentDate(inputData.getBillMaturityDate()); // 入金・支払予定日に満期日を設定
		if (CommonConstants.ACCOUNT_CODE.DISCOUNT_BILLS.equals(inputData.getCredit())) {
			// 割引手形の自動手形振替
			iDto.setSelectCode(CommonConstants.SHIWAKE_KIND.MONEY_RECEIVED); // 入金
			iDto.setDebit(CommonConstants.ACCOUNT_CODE.ORDINARY_DEPOSIT); // 通常預金
			iDto.setCredit(CommonConstants.ACCOUNT_CODE.RECEIPT_BILLS); // 受取手形
			a0InputDataService.inputData(iDto);
			
			iDto.setSelectCode(CommonConstants.SHIWAKE_KIND.WITHDRAWAL); // 出金
			iDto.setDebit(CommonConstants.ACCOUNT_CODE.DISCOUNT_BILLS); // 割引手形
			iDto.setCredit(CommonConstants.ACCOUNT_CODE.ORDINARY_DEPOSIT); // 通常預金
			a0InputDataService.inputData(iDto);
		}
		
		if (CommonConstants.ACCOUNT_CODE.ENDORSEMENT_BILLS.equals(inputData.getCredit())) {
			// 裏書手形の自動手形振替
			iDto.setSelectCode(CommonConstants.SHIWAKE_KIND.MONEY_RECEIVED); // 入金
			iDto.setDebit(CommonConstants.ACCOUNT_CODE.ORDINARY_DEPOSIT); // 通常預金
			iDto.setCredit(CommonConstants.ACCOUNT_CODE.RECEIPT_BILLS); // 受取手形
			a0InputDataService.inputData(iDto);
			
			iDto.setSelectCode(CommonConstants.SHIWAKE_KIND.WITHDRAWAL); // 出金
			iDto.setDebit(CommonConstants.ACCOUNT_CODE.ENDORSEMENT_BILLS); // 割引手形
			iDto.setCredit(CommonConstants.ACCOUNT_CODE.ORDINARY_DEPOSIT); // 通常預金
			a0InputDataService.inputData(iDto);
		}
	}
	
	
	/**
     * 仕訳データ削除
     */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void deleteData(JournalEntryData jData) {
		String companyCode = CompanyUtil.getCompanyCode();
		GeneralLedgerDto selectDto = new GeneralLedgerDto();
		selectDto.setCompanyCode(companyCode);
		selectDto.setJournalNo(jData.getJournalNo());
		
		List<GeneralLedgerDto> dataList = a1Repo.getGeneralLedgerDataList(selectDto);
		if (dataList != null && dataList.size() > 0) {
			for (GeneralLedgerDto gDto : dataList) {
				// 「貸方金額 - 借方金額」を補正残高として設定
				gDto.setBalanceMoney(gDto.getCreditAmountMoney() - gDto.getDebitAmountMoney());
				gDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
				gDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
				// 残高補正
				a0InputDataRepository.updateBalance(gDto);
				// 総勘定元帳データ削除
				a1Repo.deleteGeneralLedgerData(gDto);
			}
		}
		
		long tax = Long.valueOf(StringUtil.replaceCommaFormat(jData.getTax()));
		if (CommonConstants.COMPANY_TAX_KIND.TAX.equals(CompanyUtil.getTaxKindFlg())
				&& !CommonConstants.COMPANY_TAX_SUMMARY.ALWAYS.equals(CompanyUtil.getTaxAppFlg())
				&& tax != 0) {
			// 課税の都度集計以外の場合
			String accrualDate = "";
			if (CommonConstants.COMPANY_TAX_SUMMARY.MONTHLY.equals(CompanyUtil.getTaxAppFlg())) {
				// 月末日
				accrualDate = StringUtil
						.getLastDayOfMonth(StringUtil.replaceDateSlashFormat(jData.getAccrualDate()));
			} else if (CommonConstants.COMPANY_TAX_SUMMARY.ANNUAL.equals(CompanyUtil.getTaxAppFlg())) {
				// 年度会計期末日
				accrualDate = CompanyUtil.getAccountEndDay();
			}
			
			// 消費税額を補正
			if (a0InputDataService.isPlusSelect(jData.getKindCode())) {
				a0InputDataService.updateGeneralLedgerData(accrualDate, jData.getDebitAccount(), CommonConstants.ACCOUNT_CODE.CREDIT_TAX, -tax);
			} else {
				a0InputDataService.updateGeneralLedgerData(accrualDate, CommonConstants.ACCOUNT_CODE.DEBIT_TAX, jData.getCreditAccount(), -tax);
			}
		}
		
		JournalEntryDto jDto = new JournalEntryDto();
		jDto.setCompanyCode(companyCode);
		jDto.setJournalNo(jData.getJournalNo());
		// 仕訳データ削除
		a1Repo.deleteJournalEntryData(jDto);	
	}
	
	/**
     * 4桁(MMDD)の入力日付で、8桁(YYYYMMDD)の発生日を生成する
     */
	public String generateAccrualDate(String mmdd) {
		String accrualDate = mmdd;
		if (!StringUtils.isEmpty(mmdd) && mmdd.length() == 4) {
			accrualDate = mmdd.compareTo(CompanyUtil.getKimatuMonthDay()) <= 0 // 期末日より小さい、または同じ日付の場合
					? CompanyUtil.getKimatuYear() + mmdd
					: String.valueOf((Integer.valueOf(CompanyUtil.getKimatuYear()) - 1)) + mmdd;
		}
		return accrualDate;
	}
	
	/**
     * 入力補助プロセス
     */
	private void inputAssistanceProcess(InputDataDto inputData) {
		if (CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(inputData.getDebit())
				|| CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(inputData.getCredit())
				|| CommonConstants.ACCOUNT_CODE.NEXT_CARRY_FORWARD.equals(inputData.getDebit())
				|| CommonConstants.ACCOUNT_CODE.NEXT_CARRY_FORWARD.equals(inputData.getCredit())) {
			// 前期繰越、次期繰越は対象外とする
			return;
		}
		
		if (CommonConstants.ACCOUNT_CODE.DISCOUNT_BILLS.equals(inputData.getCredit())
				|| CommonConstants.ACCOUNT_CODE.ENDORSEMENT_BILLS.equals(inputData.getCredit())) {
			if (CommonConstants.SHIWAKE_KIND.BILLS_RECIVABLE.equals(inputData.getSelectCode()) ||
					CommonConstants.SHIWAKE_KIND.BILLS_PAYMENT.equals(inputData.getSelectCode())) {
				// 手形支払の場合は、入金・支払予定日が満期日となる
				inputData.setBillMaturityDate(inputData.getPaymentDate());
			} else {
				// 満期日補助ダイアログを表示
				commonDialog.showDialog("入力補助", "/fxml/a/a1_assistanceDialog.fxml", true);
				// 選択した手形情報を設定
				String billMaturityDate = a1AssistanceDialogController.getInputBillMaturityDate();
				if (StringUtils.isEmpty(billMaturityDate)) {
					throw new ValidationException("手形満期日が確認できません。");
				} else {
					inputData.setBillMaturityDate(billMaturityDate);
				}
			}
			
			// 満期日補助ダイアログを表示
			Dialog<Pair<String, String>> dialog = commonDialog.getDialog("自動仕訳", "/fxml/a/a1_notificationDialog.fxml", true);
			a1NotificationDialogController.init(
					CommonConstants.ACCOUNT_CODE.DISCOUNT_BILLS.equals(inputData.getCredit()) ? "割引手形" : "裏書手形",
					inputData.getKingaku(), inputData.getBillMaturityDate());
			dialog.showAndWait();
		}
	}
	
	
}
