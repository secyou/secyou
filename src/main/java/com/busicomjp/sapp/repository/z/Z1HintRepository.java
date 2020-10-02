package com.busicomjp.sapp.repository.z;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.busicomjp.sapp.dto.z.HintParams;
import com.busicomjp.sapp.model.z.AccountData;
import com.busicomjp.sapp.model.z.HintResultData;

@Repository
public interface Z1HintRepository {
	
	List<HintResultData> getTekiyoHintDataList(HintParams params);
	
	List<AccountData> getAccountDataListForTekiyo(HintParams params);
	
	List<AccountData> getAccountDataListForTorihikisaki(HintParams params);
	
	List<HintResultData> getToriHikiSakiHintDataList(HintParams params);
	
	List<HintResultData> getAccountHintDataList(HintParams params);
}
