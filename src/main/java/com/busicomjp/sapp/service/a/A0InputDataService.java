package com.busicomjp.sapp.service.a;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.dto.a.BalanceDto;
import com.busicomjp.sapp.dto.a.GeneralLedgerDto;
import com.busicomjp.sapp.dto.a.InputDataDto;
import com.busicomjp.sapp.dto.a.JournalEntryInsertDto;
import com.busicomjp.sapp.model.z.AccountData;
import com.busicomjp.sapp.repository.a.A0InputDataRepository;

@Service
public class A0InputDataService {

	@Autowired
	A0InputDataRepository a0InputDataRepository;
	
	/**
     * 入力データを仕訳TBLと総勘定元帳TBLに登録する
     */
	public void inputData(InputDataDto inputData) {
		
		String companyCode = CompanyUtil.getCompanyCode();
		
		// 前期繰越登録プロセス
		if (!inputData.isCarryForwardFlg()) {
			// 借方・貸方勘定科目のいずれかが「前期繰越」ではない場合、実施
			inputCarryForwardDataProcess(inputData.getDebit());
			inputCarryForwardDataProcess(inputData.getCredit());
		}

		// 仕訳No
		String journalNo = a0InputDataRepository.getNextJournalNo(companyCode);

		boolean companyTaxKbn = CommonConstants.COMPANY_TAX_KIND.TAX.equals(CompanyUtil.getTaxKindFlg());
		// 税込金額
		long amountMoney = Long.valueOf(inputData.getKingaku());
		int taxtRate = a0InputDataRepository.getTaxRate(inputData.getTaxCode());
		// 税金
		long tax = Math.round(Double.valueOf(taxtRate) / (Double.valueOf(taxtRate) + 100) * amountMoney);

		// 仕訳TBLへ登録
		JournalEntryInsertDto jDto = new JournalEntryInsertDto();
		jDto.setCompanyCode(companyCode);
		jDto.setJournalNo(journalNo);
		jDto.setOrgJournalNo(inputData.getOrgJournalNo());
		jDto.setKindCode(inputData.getSelectCode());
		jDto.setAccrualDate(inputData.getAccrualDate());
		jDto.setSuppliersCode(inputData.getSuppliersCode());
		jDto.setTekiyoCode(inputData.getTekiyoCode());
		jDto.setDebitAccount(inputData.getDebit());
		jDto.setCreditAccount(inputData.getCredit());
		jDto.setAmountMoney(amountMoney);
		jDto.setAmountMoneyTax(tax);
		jDto.setTaxCode(inputData.getTaxCode());
		jDto.setDepPayDate(inputData.getPaymentDate());
		jDto.setRedFlg(inputData.getRedFlg());
		a0InputDataRepository.insertJournalEntryData(jDto);

		// 総勘定元帳TBLへ登録
		GeneralLedgerDto gDto = new GeneralLedgerDto();
		gDto.setCompanyCode(companyCode);
		gDto.setJournalNo(journalNo);
		gDto.setAccrualDate(inputData.getAccrualDate());
		gDto.setDepPayDate(inputData.getPaymentDate());
		gDto.setTekiyoCode(inputData.getTekiyoCode());
		if (inputData.isCarryForwardFlg()) {
			// 前期繰越データの場合
			String accountCode = CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(inputData.getDebit())
					? inputData.getCredit()
					: inputData.getDebit();
			GeneralLedgerDto carryForwardData = getCarryForwardGeneralLedger(accountCode);
			if (carryForwardData != null && StringUtils.isNotEmpty(carryForwardData.getCompanyCode())) {
				// 総勘定元帳に既に登録されている場合(売掛金、買掛金、未払金の前期繰越)
				GeneralLedgerDto updateDto = new GeneralLedgerDto();
				updateDto.setCompanyCode(companyCode);
				updateDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
				updateDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
				updateDto.setAccountCode(accountCode);
				updateDto.setCounterAccount(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD);
				updateDto.setDebitAmountMoney(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(inputData.getCredit()) ? amountMoney : 0l);
				updateDto.setCreditAmountMoney(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(inputData.getCredit()) ? 0l : amountMoney);
				// 既存データの借方金額、貸方金額、残高金額を修正
				a0InputDataRepository.updateCarryForwardGeneralLedgerData(updateDto);
				// 残高補正
				updateBalance(carryForwardData.getAccrualDate(), carryForwardData.getGeneralNo(), accountCode, amountMoney);
				return;
			}
		}
		
		if (companyTaxKbn && tax != 0l) {
			// 課税事業 AND 消費税が0ではない場合、消費税関連勘定科目登録要
			// 税抜けの金額で総勘定元帳へ登録
			insertGeneralLedgerData(gDto, inputData.getDebit(), inputData.getCredit(), amountMoney - tax);
			// 税金を総勘定元帳へ登録
			inputTaxGeneralLedgerDataProcess(inputData, gDto, tax);
		} else {
			// 消費税関連勘定科目登録不要
			// 税込みの金額で総勘定元帳へ登録
			insertGeneralLedgerData(gDto, inputData.getDebit(), inputData.getCredit(), amountMoney);
		}

	}
	
	/**
     * 前期繰越データ登録プロセス
     */
	private void inputCarryForwardDataProcess(String accountCode) {
		// 勘定科目コード詳細を取得
		AccountData accountData = a0InputDataRepository.getAccountDetail(CompanyUtil.getCompanyCode(), accountCode);
		// 勘定科目コードの分類1
		String accountKind1 = accountData.getAccountKind1();
		if (!CompanyUtil.isFirstInputYear() && !CommonConstants.ACCOUNT_KIND.ASSETS.equals(accountKind1)
				&& !CommonConstants.ACCOUNT_KIND.LIABILITIES.equals(accountKind1)
				&& !CommonConstants.ACCOUNT_KIND.NET_ASSETS.equals(accountKind1)) {
			// 初回登録年度では、すべての勘定科目に対して前期繰越データを登録する
			// 初回登録年度以外は、資産、負債、純資産以外は繰越データを自動登録しない
			return;
		}
		
		// 前期繰越データが存在するかを判断する
		GeneralLedgerDto carryForwardData = getCarryForwardGeneralLedger(accountCode);
		if (carryForwardData != null && StringUtils.isNotEmpty(carryForwardData.getCompanyCode())) {
			return;
		}

		// 前期繰越データを登録する
		InputDataDto inputData = new InputDataDto();
		inputData.setCarryForwardFlg(true);
		inputData.setAccrualDate(CompanyUtil.getAccountStartDay());
		inputData.setCompanyCode(CompanyUtil.getCompanyCode());
		inputData.setTaxCode(CommonConstants.TAX_CODE.TAX_EXEMPT);
		inputData.setKingaku("0");
		
		if (CommonConstants.DEBIT_CREDIT_FLG.DEBIT.equals(accountData.getKindFlg())) {
			// 借方の勘定科目の場合
			inputData.setDebit(accountCode);
			inputData.setCredit(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD);
		} else {
			// 貸方の勘定科目の場合
			inputData.setDebit(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD);
			inputData.setCredit(accountCode);
		}
		this.inputData(inputData);
	}
	
	private GeneralLedgerDto getCarryForwardGeneralLedger(String accountCode) {
		// 前期繰越データが存在するかを判断する
		GeneralLedgerDto gDto = new GeneralLedgerDto();
		gDto.setCompanyCode(CompanyUtil.getCompanyCode());
		gDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		gDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		gDto.setAccountCode(accountCode);
		gDto.setCounterAccount(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD);// 前期繰越
		return a0InputDataRepository.getCarryForwardData(gDto);
	}
	
	/**
     * 消費税関連勘定科目登録・更新プロセス
     */
	private void inputTaxGeneralLedgerDataProcess(InputDataDto inputData, GeneralLedgerDto gDto, long tax) {
		// 税金を総勘定元帳へ登録
		String kbn = CompanyUtil.getTaxAppFlg(); // 1:都度集計／2:月単位集計／3:年単位集計
		if (!CommonConstants.COMPANY_TAX_SUMMARY.MONTHLY.equals(kbn)
				&& !CommonConstants.COMPANY_TAX_SUMMARY.ANNUAL.equals(kbn)) {
			if (isPlusSelect(inputData.getSelectCode())) {
				insertGeneralLedgerData(gDto, inputData.getDebit(), CommonConstants.ACCOUNT_CODE.CREDIT_TAX, tax);
			} else {
				insertGeneralLedgerData(gDto, CommonConstants.ACCOUNT_CODE.DEBIT_TAX, inputData.getCredit(), tax);
			}
			return;
		}
		
		String accrualDate = "";
		if (CommonConstants.COMPANY_TAX_SUMMARY.MONTHLY.equals(kbn)) {
			// 月末日
			accrualDate = StringUtil.getLastDayOfMonth(inputData.getAccrualDate());
		} else if (CommonConstants.COMPANY_TAX_SUMMARY.ANNUAL.equals(kbn)) {
			// 年度会計期末日
			accrualDate = CompanyUtil.getAccountEndDay();
		}
		gDto.setAccrualDate(accrualDate);
		gDto.setJournalNo(""); // 消費税累計の総勘定元帳データは、仕訳Noをブランクに設定
		gDto.setDepPayDate(null); // 入金・支払予定日をNULLに設定
		
		GeneralLedgerDto selectDto = new GeneralLedgerDto();
		selectDto.setCompanyCode(CompanyUtil.getCompanyCode());
		selectDto.setAccrualDate(accrualDate);
		if (isPlusSelect(inputData.getSelectCode())) {
			selectDto.setAccountCode(inputData.getDebit());
			selectDto.setCounterAccount(CommonConstants.ACCOUNT_CODE.CREDIT_TAX);
		    // 集計用の総勘定元帳データが存在するかを確認する
			GeneralLedgerDto taxDebit = a0InputDataRepository.getTaxGeneralLedgerData(selectDto);
			if (taxDebit == null || StringUtils.isEmpty(taxDebit.getCompanyCode())) {
				// 存在しない場合、新規登録
				insertGeneralLedgerData(gDto, inputData.getDebit(), CommonConstants.ACCOUNT_CODE.CREDIT_TAX, tax);
			} else {
				// 存在する場合、更新
				updateGeneralLedgerData(accrualDate, inputData.getDebit(), CommonConstants.ACCOUNT_CODE.CREDIT_TAX, tax);
			}
		} else {
			selectDto.setAccountCode(CommonConstants.ACCOUNT_CODE.DEBIT_TAX);
			selectDto.setCounterAccount(inputData.getCredit());
			// 集計用の総勘定元帳データが存在するかを確認する
			GeneralLedgerDto taxCredit = a0InputDataRepository.getTaxGeneralLedgerData(selectDto);
			if (taxCredit == null || StringUtils.isEmpty(taxCredit.getCompanyCode())) {
				// 存在しない場合、新規登録
				insertGeneralLedgerData(gDto, CommonConstants.ACCOUNT_CODE.DEBIT_TAX, inputData.getCredit(), tax);
			} else {
				// 存在する場合、更新
				updateGeneralLedgerData(accrualDate, CommonConstants.ACCOUNT_CODE.DEBIT_TAX, inputData.getCredit(), tax);
			}
		}
	}
	
	private void insertGeneralLedgerData(GeneralLedgerDto gDto, String debit, String credit, long amoutMoney) {

		if (!CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(debit) && !CommonConstants.ACCOUNT_CODE.NEXT_CARRY_FORWARD.equals(debit)) {
			// 借方の総勘定元帳登録
			String debitGeneralNo = a0InputDataRepository.getNextGeneralNo(CompanyUtil.getCompanyCode());
			BalanceDto debitBDto = getBalance(gDto.getAccrualDate(), debitGeneralNo, debit);
			gDto.setGeneralNo(debitGeneralNo);
			gDto.setAccountCode(debit);
			gDto.setCounterAccount(credit);
			gDto.setDebitAmountMoney(amoutMoney);
			gDto.setCreditAmountMoney(0l);
			gDto.setBalanceMoney(debitBDto.getBalanceMoney() + amoutMoney);
			a0InputDataRepository.insertGeneralLedgerData(gDto);
			// 借方金額分残高をプラス補正
			updateBalance(gDto.getAccrualDate(), debitGeneralNo, debit, amoutMoney);
		}

		if (!CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(credit) && !CommonConstants.ACCOUNT_CODE.NEXT_CARRY_FORWARD.equals(credit)) {
			// 貸方の総勘定元帳登録
			String creditGeneralNo = a0InputDataRepository.getNextGeneralNo(CompanyUtil.getCompanyCode());
			BalanceDto creditBDto = getBalance(gDto.getAccrualDate(), creditGeneralNo, credit);
			gDto.setGeneralNo(creditGeneralNo);
			gDto.setAccountCode(credit);
			gDto.setCounterAccount(debit);
			gDto.setDebitAmountMoney(0l);
			gDto.setCreditAmountMoney(amoutMoney);
			gDto.setBalanceMoney(creditBDto.getBalanceMoney() - amoutMoney);
			a0InputDataRepository.insertGeneralLedgerData(gDto);
			// 貸方金額分残高をマイナス補正
			updateBalance(gDto.getAccrualDate(), creditGeneralNo, credit, -amoutMoney);
		}

	}
	
	public void updateGeneralLedgerData(String accrualDate, String debit, String credit, long amoutMoney) {
		GeneralLedgerDto gDto = new GeneralLedgerDto();
		gDto.setCompanyCode(CompanyUtil.getCompanyCode());
		gDto.setAccrualDate(accrualDate);
		
		// 借方の総勘定元帳更新
		gDto.setAccountCode(debit);
		gDto.setCounterAccount(credit);
		// 借方の総勘定元帳データを取得
		GeneralLedgerDto taxDebit = a0InputDataRepository.getTaxGeneralLedgerData(gDto);
		taxDebit.setDebitAmountMoney(amoutMoney);
		taxDebit.setCreditAmountMoney(0l);
		a0InputDataRepository.updateTaxGeneralLedgerData(taxDebit);
		// 借方金額分残高をプラス補正
		updateBalance(taxDebit.getAccrualDate(), taxDebit.getGeneralNo(), debit, amoutMoney);
		
		// 貸方の総勘定元帳更新
		gDto.setAccountCode(credit);
		gDto.setCounterAccount(debit);
		// 貸方の総勘定元帳データを取得
		GeneralLedgerDto taxCredit = a0InputDataRepository.getTaxGeneralLedgerData(gDto);
		taxCredit.setDebitAmountMoney(0l);
		taxCredit.setCreditAmountMoney(amoutMoney);
		a0InputDataRepository.updateTaxGeneralLedgerData(taxCredit);
		// 貸方金額分残高をマイナス補正
		updateBalance(taxCredit.getAccrualDate(), taxCredit.getGeneralNo(), credit, -amoutMoney);
	}
	
	/**
     * 残高取得(発生日が未来日を除いた最終データの残高)
     */
	private BalanceDto getBalance(String accrualDate, String generalNo, String accountCode) {
		GeneralLedgerDto selectDto = new GeneralLedgerDto();
		selectDto.setCompanyCode(CompanyUtil.getCompanyCode());
		selectDto.setAccrualDate(accrualDate);
		selectDto.setGeneralNo(generalNo);
		selectDto.setAccountCode(accountCode);
		selectDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		selectDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		
		BalanceDto resultDto = a0InputDataRepository.getBalance(selectDto);
		if (resultDto == null || StringUtils.isEmpty(resultDto.getGeneralNo())) {
			resultDto = new BalanceDto();
			resultDto.setBalanceMoney(0l);
			resultDto.setGeneralNo(generalNo);
		}
		
		return resultDto;
	}
	
	/**
     * 残高金額補正
     */
	public void updateBalance(String accrualDate, String generalNo, String accountCode, long balanceMoney) {
		GeneralLedgerDto updateDto = new GeneralLedgerDto();
		updateDto.setCompanyCode(CompanyUtil.getCompanyCode());
		updateDto.setAccrualDate(accrualDate);
		updateDto.setGeneralNo(generalNo);
		updateDto.setAccountCode(accountCode);
		updateDto.setBalanceMoney(balanceMoney);
		// 会計年度単位で補正
		updateDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		updateDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		
		a0InputDataRepository.updateBalance(updateDto);
	}
	
	/**
     * 分類コードが借方か貸方かを確認する
     */
	public boolean isPlusSelect(String selectCode) {
		switch (selectCode) {
		case CommonConstants.SHIWAKE_KIND.MONEY_RECEIVED: // 入金
		case CommonConstants.SHIWAKE_KIND.ACCOUNTS_RECEIVABLE: // 売掛金
		case CommonConstants.SHIWAKE_KIND.BILLS_RECIVABLE: // 手形受取
			return true;
		default:
			return false;
		}
	}
}
