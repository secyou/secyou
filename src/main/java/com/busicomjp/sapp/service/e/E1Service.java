package com.busicomjp.sapp.service.e;

import java.util.List;
import java.util.regex.Pattern;

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
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.dto.a.BalanceDto;
import com.busicomjp.sapp.dto.a.InputDataDto;
import com.busicomjp.sapp.model.e.CarryForwardData;
import com.busicomjp.sapp.model.e.CarryForwardSubData;
import com.busicomjp.sapp.repository.e.E1Repository;
import com.busicomjp.sapp.service.a.A0InputDataService;
import com.busicomjp.sapp.service.a.A1Service;

@Service
public class E1Service {
	
	private static Pattern patternMoney = Pattern.compile("^([+]?[0-9]*)$");

	@Autowired
	E1Repository e1Repo;
	@Autowired
	Validator validator;
	@Autowired
	A0InputDataService a0InputDataService;
	@Autowired
	private A1Service a1Service;

	/**
     * 勘定科目マスタテーブル_分類1取得
     */
	public List<ComboItem> getAccountKind1DataList(String companyCode) {

		return e1Repo.getAccountKind1DataList(companyCode);

	}

	/**
     * 勘定科目マスタテーブル_分類2取得
     */
	public List<ComboItem> getAccountKind2DataList(String companyCode, String accountKind1) {
		return e1Repo.getAccountKind2DataList(companyCode, accountKind1);
	}

	/**
     * 勘定科目マスタテーブル_分類3取得
     */
	public List<ComboItem> getAccountKind3DataList(String companyCode, String accountKind1, String accountKind2) {
		return e1Repo.getAccountKind3DataList(companyCode, accountKind1, accountKind2);
	}
	/**
     * 勘定科目マスタテーブル_分類4取得
     */
	public List<ComboItem> getAccountKind4DataList(String companyCode,
																				String accountKind1,
																				String accountKind2,
																				String accountKind3) {

		return e1Repo.getAccountKind4DataList(companyCode,
																		accountKind1,
																		accountKind2,
																		accountKind3);
	}
	
	/**
     * 勘定科目マスタテーブル_勘定科目取得
     */
	public List<CarryForwardData> getAccountCodeDataList(String companyCode, String startDate, String endDate) {

		return e1Repo.getAccountCodeDataList(companyCode, startDate, endDate);
	}
	
	/**
	 * 仕訳データ更新
	 */
	public void updateJournalData(String amountMoney, String companyCode, String startDate, String endDate,
			String creditAccount, String debitAccount, String suppliersCode) {
		e1Repo.updateJournalData(amountMoney, companyCode, startDate, creditAccount, debitAccount, suppliersCode);
	}

	/**
	 * 総勘定元帳データ更新及び残高補正
	 */
	public void updateGeneralLegeLedger(String amountMoney, String companyCode, String startDate, String endDate,
			String creditAccount, String debitAccount) {
		BalanceDto balance;
		String accountCode;

		if (CommonConstants.ACCOUNT_CODE.CARRY_FORWARD.equals(creditAccount)) {
			balance = e1Repo.selectGeneralBalanceMoney(companyCode, startDate, debitAccount);
			accountCode = debitAccount;
		} else {
			balance = e1Repo.selectGeneralBalanceMoney(companyCode, startDate, creditAccount);
			accountCode = creditAccount;
		}
		
		e1Repo.updateGeneralData(amountMoney, companyCode, startDate, creditAccount, debitAccount);

		// 残高補正
		long balanceMoneyAfter = Long.valueOf(amountMoney) - balance.getBalanceMoney();
		a0InputDataService.updateBalance(startDate, balance.getGeneralNo(), accountCode, balanceMoneyAfter);
	}

	public String  selectJournalData(String companyCode, String startDate,String creditAccount, String debitAccount,String suppliersCode) {
		return e1Repo.selectJournalData(companyCode, startDate, creditAccount,debitAccount,suppliersCode);
	}

	//取引先一覧取得
	public List<CarryForwardSubData>  selectTorihikiData(String companyCode, String startDate, String accountCode) {
		return e1Repo.selectTorihikiData(companyCode, startDate, accountCode) ;
	}
	
	/**
     * 前期繰越データ登録
     */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void registCarryForward(String accountCode, String kindFlg, String debitAmountMoney, String creditAmountMoney) {
		InputDataDto inputData = new InputDataDto();
		inputData.setAccrualDate(CompanyUtil.getAccountStartDay());
		if (CommonConstants.DEBIT_CREDIT_FLG.DEBIT.equals(kindFlg)) {
			inputData.setCredit(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD); // 前期繰越
			inputData.setDebit(accountCode);
			inputData.setKingaku(debitAmountMoney);
		} else {
			inputData.setCredit(accountCode);
			inputData.setDebit(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD); // 前期繰越
			inputData.setKingaku(creditAmountMoney);
		}
		inputData.setTaxCode(CommonConstants.TAX_CODE.TAX_EXEMPT); // 非課税
		inputData.setPaymentDate(CompanyUtil.getAccountStartDay());
		inputData.setCarryForwardFlg(true);

		// 入力値を編集する
		inputData = a1Service.editInputData(inputData);
		// 入力データをチェック
		this.inputCheck(inputData.getKingaku());

		String amountMoney = this.selectJournalData(CompanyUtil.getCompanyCode(), CompanyUtil.getAccountStartDay(),
				inputData.getCredit(), inputData.getDebit(), null);

		if (amountMoney == null) {
			// 自動仕訳新規登録
			a0InputDataService.inputData(inputData);
		} else {
			// 仕訳データ更新
			this.updateJournalData(inputData.getKingaku(), CompanyUtil.getCompanyCode(),
					CompanyUtil.getAccountStartDay(), CompanyUtil.getAccountEndDay(), inputData.getCredit(),
					inputData.getDebit(), null);
			// 総勘定元帳データ更新
			long balance = Long.parseLong(debitAmountMoney) - Long.parseLong(creditAmountMoney);
			this.updateGeneralLegeLedger(String.valueOf(balance), CompanyUtil.getCompanyCode(),
					CompanyUtil.getAccountStartDay(), CompanyUtil.getAccountEndDay(), inputData.getCredit(),
					inputData.getDebit());
		}
	}
	
	/**
     * 取引先別の前期繰越データ登録
     */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void registCarryForward4Torihikisaki(List<CarryForwardSubData> carryforwardsubData, String accountCode, String kindFlg) {
		boolean isUpdate = false;
		if (carryforwardsubData != null && carryforwardsubData.size() > 0) {
			InputDataDto inputData = new InputDataDto();
			long sumAmountMoney = 0l;
			String debitAccountCode = "";
			String creditAccountCode = "";					
			for (CarryForwardSubData sub : carryforwardsubData) {

				if (CommonConstants.DEBIT_CREDIT_FLG.DEBIT.equals(kindFlg)) {
					inputData.setCredit(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD); // 前期繰越
					inputData.setDebit(accountCode);
					inputData.setKingaku(sub.getDebitAmountMoney());
					sumAmountMoney = sumAmountMoney + Long.parseLong(sub.getDebitAmountMoney()); // 借方金額の合計
					debitAccountCode = accountCode;
					creditAccountCode = CommonConstants.ACCOUNT_CODE.CARRY_FORWARD;
				} else {
					inputData.setDebit(CommonConstants.ACCOUNT_CODE.CARRY_FORWARD); // 前期繰越
					inputData.setCredit(accountCode);
					inputData.setKingaku(sub.getCreditAmountMoney());
					sumAmountMoney = sumAmountMoney + Long.parseLong(sub.getCreditAmountMoney()); // 貸方金額の合計
					debitAccountCode = CommonConstants.ACCOUNT_CODE.CARRY_FORWARD;
					creditAccountCode = accountCode;
				}

				inputData.setAccrualDate(CompanyUtil.getAccountStartDay());
				inputData.setTaxCode(CommonConstants.TAX_CODE.TAX_EXEMPT); // 非課税
				inputData.setPaymentDate(CompanyUtil.getAccountStartDay());
				inputData.setSuppliersCode(sub.getTorihikisakiCode());
				inputData.setCarryForwardFlg(true);

				// 入力値を編集する
				inputData = a1Service.editInputData(inputData);
				// 入力データをチェック;
				this.inputCheck(inputData.getKingaku());

				String amountMoney = this.selectJournalData(CompanyUtil.getCompanyCode(),
						CompanyUtil.getAccountStartDay(), inputData.getCredit(), inputData.getDebit(),
						inputData.getSuppliersCode());

				if (amountMoney == null) {
					// 自動仕訳新規登録
					a0InputDataService.inputData(inputData);
				} else {
					isUpdate = true;
					// 仕訳更新
					this.updateJournalData(inputData.getKingaku(), CompanyUtil.getCompanyCode(),
							CompanyUtil.getAccountStartDay(), CompanyUtil.getAccountEndDay(), inputData.getCredit(),
							inputData.getDebit(), inputData.getSuppliersCode());
				}
			}
			
			if (isUpdate) {
				// 総勘定元帳データ更新(1回のみ実施)
				this.updateGeneralLegeLedger(String.valueOf(sumAmountMoney), CompanyUtil.getCompanyCode(),
						CompanyUtil.getAccountStartDay(), CompanyUtil.getAccountEndDay(), creditAccountCode,
						debitAccountCode);
			}
		}
	}
	
	public void inputCheck(String value) {
		if (StringUtils.isEmpty(value)) {
			throw new ValidationException("金額を入力してください。");
		}
		if (!patternMoney.matcher(value).matches()) {
			throw new ValidationException("金額は0以上の整数で入力してください。");
		}
	}

}
