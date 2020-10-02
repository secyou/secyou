package com.busicomjp.sapp.service.z;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.dto.z.HintParams;
import com.busicomjp.sapp.model.z.AccountData;
import com.busicomjp.sapp.model.z.HintResultData;
import com.busicomjp.sapp.repository.z.Z1HintRepository;

@Service
public class Z1HintService {

	@Autowired
	Z1HintRepository z1HintRepo;

	/**
	 * ヒントの摘要データ取得
	 */
	public List<HintResultData> getTekiyoHintDataList(String hint) {

		HintParams params = new HintParams();
		params.setCompanyCode(CompanyUtil.getCompanyCode());
		params.setHint("%" + hint + "%");

		return z1HintRepo.getTekiyoHintDataList(params);
	}

	/**
	 * 摘要コードに紐いている勘定科目データを取得
	 */
	public List<AccountData> getAccountDataListForTekiyo(String tekiyoCode) {

		HintParams params = new HintParams();
		params.setCompanyCode(CompanyUtil.getCompanyCode());
		params.setTekiyoCode(tekiyoCode);

		return z1HintRepo.getAccountDataListForTekiyo(params);
	}

	/**
	 * ヒントの取引先データ取得
	 */
	public List<HintResultData> getToriHikiSakiHintDataList(String hint) {
		HintParams params = new HintParams();
		params.setCompanyCode(CompanyUtil.getCompanyCode());
		params.setHint(hint);

		return z1HintRepo.getToriHikiSakiHintDataList(params);
	}

	/**
	 * ヒントの勘定科目データ取得
	 */
	public List<HintResultData> getAccountHintDataList(String hint) {
		HintParams params = new HintParams();
		params.setCompanyCode(CompanyUtil.getCompanyCode());
		params.setHint(hint);

		return z1HintRepo.getAccountHintDataList(params);
	}

	/**
	 * 取引先コードに紐いている勘定科目データを取得
	 */
	public List<AccountData> getAccountDataListForTorihikisaki(String torihikisakiCode) {

		HintParams params = new HintParams();
		params.setCompanyCode(CompanyUtil.getCompanyCode());
		params.setTorihikisakiCode(torihikisakiCode);

		return z1HintRepo.getAccountDataListForTorihikisaki(params);
	}
}
