package com.busicomjp.sapp.service.b;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.dto.b.CompanyDto;
import com.busicomjp.sapp.model.b.BalanceData;
import com.busicomjp.sapp.repository.b.B1Repository;

@Service
public class B1Service {
	
	@Autowired
	B1Repository b1Repo;
	
	public List<BalanceData> getBalanceDataList() {
		CompanyDto companyDto = new CompanyDto();
		companyDto.setCompanyCode(CompanyUtil.getCompanyCode());
		companyDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		companyDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		return b1Repo.getBalanceDataList(companyDto);
	}
	
	public String getWorkPeriod() {
		CompanyDto companyDto = new CompanyDto();
		companyDto.setCompanyCode(CompanyUtil.getCompanyCode());
		companyDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		companyDto.setAccountEndDay(CompanyUtil.getAccountEndDay());

		String maxAccrualDate = b1Repo.getMaxAccrualDate(companyDto);
		if (StringUtils.isEmpty(maxAccrualDate)) {
			return "なし";
		} else {
			return StringUtil.dateSlashFormat(CompanyUtil.getAccountStartDay()) + "～"
					+ StringUtil.dateSlashFormat(maxAccrualDate);
		}
	}
	
	public List<BalanceData> getBalanceDetailDataList() {
		CompanyDto companyDto = new CompanyDto();
		companyDto.setCompanyCode(CompanyUtil.getCompanyCode());
		companyDto.setAccountStartDay(CompanyUtil.getAccountStartDay());
		companyDto.setAccountEndDay(CompanyUtil.getAccountEndDay());
		List<BalanceData> list = b1Repo.getBalanceDetailDataList(companyDto);
		
		List<BalanceData> resultList = new ArrayList<BalanceData>();
		
		long sumDebitCarryForwardBalance = 0;
		long sumCreditCarryForwardBalance = 0;
		long sumDebitTotalBalance = 0;
		long sumCreditTotalBalance = 0;
		long sumDebitBalance = 0;
		long sumCreditBalance = 0;
		
		long totalDebitCarryForwardBalance = 0;
		long totalCreditCarryForwardBalance = 0;
		long totalDebitTotalBalance = 0;
		long totalCreditTotalBalance = 0;
		long totalDebitBalance = 0;
		long totalCreditBalance = 0;
		
		// 前データの勘定科目分類
		String prevAccountKind1 = "";
		// 現在処理中データの勘定科目分類
		String nowAccountKind1 = "";
			
		if (list != null) {
			for (BalanceData data : list) {
				nowAccountKind1 = data.getAccountKind1();
				
				// 前データの分類と異なる場合、集計を行う
				if (!prevAccountKind1.equals(nowAccountKind1)) {
					createSummayData(resultList, prevAccountKind1, nowAccountKind1, sumDebitCarryForwardBalance,
							sumCreditCarryForwardBalance, sumDebitTotalBalance, sumCreditTotalBalance, sumDebitBalance,
							sumCreditBalance);
					// 全体の合計残高を累計
					totalDebitCarryForwardBalance = totalDebitCarryForwardBalance + sumDebitCarryForwardBalance;
					totalCreditCarryForwardBalance = totalCreditCarryForwardBalance + sumCreditCarryForwardBalance;
					totalDebitTotalBalance = totalDebitTotalBalance + sumDebitTotalBalance;
					totalCreditTotalBalance = totalCreditTotalBalance + sumCreditTotalBalance;
					totalDebitBalance = totalDebitBalance + sumDebitBalance;
					totalCreditBalance = totalCreditBalance + sumCreditBalance;
					// 分類別の合計残高をリセット
					sumDebitCarryForwardBalance = 0;
					sumCreditCarryForwardBalance = 0;
					sumDebitTotalBalance = 0;
					sumCreditTotalBalance = 0;
					sumDebitBalance = 0;
					sumCreditBalance = 0;
				}
				
				long debitCarryForwardBalance = data.getDebitCarryForwardBalance();
				long creditCarryForwardBalance = data.getCreditCarryForwardBalance();
				long debitSummany = data.getDebitSummany() - debitCarryForwardBalance;
				long creditSummay = data.getCreditSummany() - creditCarryForwardBalance;
				long totalBalace = debitCarryForwardBalance - creditCarryForwardBalance + debitSummany - creditSummay;
				
				// 前期繰越残高を設定
				data.setDebitCFBalance_s(StringUtil.commaFormat(debitCarryForwardBalance));
				data.setCreditCFBalance_s(StringUtil.commaFormat(creditCarryForwardBalance));
				
				sumDebitCarryForwardBalance = sumDebitCarryForwardBalance + debitCarryForwardBalance;
				sumCreditCarryForwardBalance = sumCreditCarryForwardBalance + creditCarryForwardBalance;
				
				// 期末残高を設定
				if (totalBalace >= 0) {
					data.setDebitTotalBalance_s(StringUtil.commaFormat(totalBalace));
					data.setCreditTotalBalance_s("0");
					sumDebitTotalBalance = sumDebitTotalBalance + totalBalace;
				} else {
					data.setDebitTotalBalance_s("0");
					data.setCreditTotalBalance_s(StringUtil.commaFormat(totalBalace * -1));
					sumCreditTotalBalance = sumCreditTotalBalance + (totalBalace * -1);
				}
				
				// 当期発生を設定
				data.setDebitBalance_s(StringUtil.commaFormat(debitSummany));
				data.setCreditBalance_s(StringUtil.commaFormat(creditSummay));
				sumDebitBalance = sumDebitBalance + debitSummany;
				sumCreditBalance = sumCreditBalance + creditSummay;
				
				resultList.add(data);
				prevAccountKind1 = nowAccountKind1;
			}
		}
		
		// 最終分類分の合計を登録
		nowAccountKind1 = "";
		createSummayData(resultList, prevAccountKind1, nowAccountKind1, sumDebitCarryForwardBalance,
				sumCreditCarryForwardBalance, sumDebitTotalBalance, sumCreditTotalBalance, sumDebitBalance,
				sumCreditBalance);
		// 全体の合計残高を累計
		totalDebitCarryForwardBalance = totalDebitCarryForwardBalance + sumDebitCarryForwardBalance;
		totalCreditCarryForwardBalance = totalCreditCarryForwardBalance + sumCreditCarryForwardBalance;
		totalDebitTotalBalance = totalDebitTotalBalance + sumDebitTotalBalance;
		totalCreditTotalBalance = totalCreditTotalBalance + sumCreditTotalBalance;
		totalDebitBalance = totalDebitBalance + sumDebitBalance;
		totalCreditBalance = totalCreditBalance + sumCreditBalance;
		// 全体合計を登録
		BalanceData totalData = new BalanceData();
		totalData.setAccountName("※※　全体の合計　※※");
		totalData.setDebitCFBalance_s(StringUtil.commaFormat(totalDebitCarryForwardBalance));
		totalData.setCreditCFBalance_s(StringUtil.commaFormat(totalCreditCarryForwardBalance));
		totalData.setDebitTotalBalance_s(StringUtil.commaFormat(totalDebitTotalBalance));
		totalData.setCreditTotalBalance_s(StringUtil.commaFormat(totalCreditTotalBalance));
		totalData.setDebitBalance_s(StringUtil.commaFormat(totalDebitBalance));
		totalData.setCreditBalance_s(StringUtil.commaFormat(totalCreditBalance));
		resultList.add(totalData);

		return resultList;
	}
	
	private void createSummayData(
			List<BalanceData> resultList,
			String prevAccountKind1, 
			String nowAccountKind1,
			long sumDebitCarryForwardBalance,
			long sumCreditCarryForwardBalance, 
			long sumDebitTotalBalance, 
			long sumCreditTotalBalance,
			long sumDebitBalance, 
			long sumCreditBalance) {
		
		if (StringUtils.isNotEmpty(prevAccountKind1)) {
			BalanceData bData = new BalanceData();
			String title = "";
			switch (prevAccountKind1) {
			case CommonConstants.ACCOUNT_KIND.ASSETS :
				title = "※※　資産の合計　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.LIABILITIES :
				title = "※※　負債の合計　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.NET_ASSETS :
				title = "※※　純資産の合計　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.PROFIT :
				title = "※※　収益の合計　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.COST :
				title = "※※　費用の合計　※※";
				break;
			}
			bData.setAccountName(title);
			bData.setDebitCFBalance_s(StringUtil.commaFormat(sumDebitCarryForwardBalance));
			bData.setCreditCFBalance_s(StringUtil.commaFormat(sumCreditCarryForwardBalance));
			bData.setDebitTotalBalance_s(StringUtil.commaFormat(sumDebitTotalBalance));
			bData.setCreditTotalBalance_s(StringUtil.commaFormat(sumCreditTotalBalance));
			bData.setDebitBalance_s(StringUtil.commaFormat(sumDebitBalance));
			bData.setCreditBalance_s(StringUtil.commaFormat(sumCreditBalance));
			resultList.add(bData);
			// 空行
			resultList.add(new BalanceData());
		}
		
		if (StringUtils.isNotEmpty(nowAccountKind1)) {
			BalanceData bData = new BalanceData();
			String title = "";
			switch (nowAccountKind1) {
			case CommonConstants.ACCOUNT_KIND.ASSETS:
				title = "※※　資産の部　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.LIABILITIES:
				title = "※※　負債の部　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.NET_ASSETS:
				title = "※※　純資産の部　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.PROFIT:
				title = "※※　収益の部　※※";
				break;
			case CommonConstants.ACCOUNT_KIND.COST:
				title = "※※　費用の部　※※";
				break;
			}
			bData.setAccountName(title);
			resultList.add(bData);
		}
	}

}
