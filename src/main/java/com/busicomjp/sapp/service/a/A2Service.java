package com.busicomjp.sapp.service.a;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.dto.a.InputDataDto;
import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.model.a.JournalEntryData;
import com.busicomjp.sapp.repository.a.A2Repository;

@Service
public class A2Service {
	@Autowired
	A2Repository a2Repo;
	@Autowired
	A0InputDataService a0InputDataService;
	@Autowired
	Validator validator;

	public List<JournalEntryData> getJournalList(JournalEntryDto journal) {
		return a2Repo.selectJournalList(journal);
	}

	public JournalEntryData getJournal(JournalEntryData selectData) {
		JournalEntryData journalEntry = new JournalEntryData();

		JournalEntryDto journal = new JournalEntryDto();
		journal.setCompanyCode(selectData.getCompanyCode());
		journal.setJournalNo(selectData.getJournalNo());

		List<JournalEntryData> list = a2Repo.selectJournalList(journal);
		if (list != null && list.size() == 1) {
			journalEntry = list.get(0);
		}

		return journalEntry;
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void registRedJournal(JournalEntryData data) {
		JournalEntryDto journal = new JournalEntryDto();
		journal.setCompanyCode(data.getCompanyCode());
		journal.setJournalNo(data.getJournalNo());

		List<JournalEntryData> list = a2Repo.selectJournalList(journal);
		if (list != null && list.size() == 1) {
			JournalEntryData journalEntry = list.get(0);

			registJournal(journalEntry, true);
		}
	}

	private void registJournal(JournalEntryData journalEntry, boolean redRegistFlag) {
		InputDataDto inputData = new InputDataDto();
		// 会社コード
		inputData.setCompanyCode(journalEntry.getCompanyCode());
		// オリジナル仕訳No
		inputData.setOrgJournalNo(journalEntry.getJournalNo());
		// 分類コード
		inputData.setSelectCode(journalEntry.getKindCode());
		// 発生日
		String accrualDate = journalEntry.getAccrualDate();
		if (!InputCheck.isNullOrBlank(accrualDate)) {
			accrualDate = accrualDate.replaceAll("/", "");
		}
		inputData.setAccrualDate(accrualDate);
		// 取引先コード
		inputData.setSuppliersCode(journalEntry.getSuppliersCode());
		// 摘要コード
		inputData.setTekiyoCode(journalEntry.getTekiyoCode());
		// 借方勘定科目
		inputData.setDebit(journalEntry.getDebitAccount());
		// 貸方勘定科目
		inputData.setCredit(journalEntry.getCreditAccount());
		// 金額
		String amountMoney = journalEntry.getAmountMoney();
		if (!InputCheck.isNullOrBlank(amountMoney)) {
			amountMoney = amountMoney.replaceAll(",", "");
		}
		inputData.setKingaku(amountMoney);
		// 税率コード
		inputData.setTaxCode(journalEntry.getTaxCode());
		// 入金・支払予定日
		String depPayDate = journalEntry.getDepPayDate();
		if (!InputCheck.isNullOrBlank(depPayDate)) {
			depPayDate = depPayDate.replaceAll("/", "");
		}
		inputData.setPaymentDate(depPayDate);
		// 赤データ識別フラグ
		inputData.setRedFlg(getRedFlg(redRegistFlag, journalEntry.getRedFlg()));

		// バリデーションチェック
		Set<ConstraintViolation<InputDataDto>> errorResult = validator.validate(inputData);
		if (!errorResult.isEmpty()) {
			// エラーあり
			throw new ValidationException(errorResult.iterator().next().getMessage());
		}
		
		if (!InputCheck.isNumeric(inputData.getAccrualDate()) || !InputCheck.isVaildDate(inputData.getAccrualDate(), "yyyyMMdd")) {
			throw new ValidationException("取引発生日はYYYYMMDD形式の日付を入力してください。");
		}
		
		if (!InputCheck.isNumeric(inputData.getPaymentDate()) || !InputCheck.isVaildDate(inputData.getPaymentDate(), "yyyyMMdd")) {
			throw new ValidationException("入金・支払予定日はYYYYMMDD形式の日付を入力してください。");
		}
		
		if (InputCheck.isBefore(inputData.getPaymentDate(), inputData.getAccrualDate(), "yyyyMMdd")) {
			throw new ValidationException("入金・支払予定日は取引発生日以降の日付を入力してください。");
		}

		if (inputData.getAccrualDate().compareTo(CompanyUtil.getAccountStartDay()) < 0
				|| inputData.getAccrualDate().compareTo(CompanyUtil.getAccountEndDay()) > 0) {
			throw new ValidationException("取引発生日は" + StringUtil.dateSlashFormat(CompanyUtil.getAccountStartDay()) + "から"
					+ StringUtil.dateSlashFormat(CompanyUtil.getAccountEndDay()) + "までの日付を指定してください");
		}
		
		// チェック後に金額再設定
		long kingaku = 0l;
		if (redRegistFlag) {
			kingaku = Long.parseLong(amountMoney) * -1;
		} else {
			kingaku = Long.parseLong(amountMoney);
		}
		inputData.setKingaku(String.valueOf(kingaku));

		// データ登録
		a0InputDataService.inputData(inputData);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
	public void registRedBlackJournal(JournalEntryData srcData, JournalEntryData data) {
		JournalEntryDto journal = new JournalEntryDto();
		journal.setCompanyCode(srcData.getCompanyCode());
		journal.setJournalNo(srcData.getJournalNo());

		List<JournalEntryData> list = a2Repo.selectJournalList(journal);
		if (list != null && list.size() == 1) {
			JournalEntryData journalEntry = list.get(0);

			registJournal(journalEntry, true);
			
			// 取引発生日
			journalEntry.setAccrualDate(data.getAccrualDate());
			// 金額(税込)
			journalEntry.setAmountMoney(data.getAmountMoney());
			// 税率
			journalEntry.setTaxCode(data.getTaxCode());
			// 入金・支払予定日
			journalEntry.setDepPayDate(data.getDepPayDate());
			registJournal(journalEntry, false);
		}
	}
	
	private String getRedFlg(boolean redRegistFlag, String originalRedFlg) {
		boolean _originalRedFlg = (CommonConstants.RED_FLG.RED.equals(originalRedFlg) ? true : false);
		if (redRegistFlag && _originalRedFlg) {
			// 赤の赤は、黒
			return CommonConstants.RED_FLG.BLACK;
		} 
		
		if (redRegistFlag || _originalRedFlg) {
			// 黒の赤 OR 赤の黒は、赤
			return CommonConstants.RED_FLG.RED;
		} else {
			// 黒の黒は、黒
			return CommonConstants.RED_FLG.BLACK;
		}
	}
	
}
