package com.busicomjp.sapp.service.d;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.model.d.TorihikisakiData;
import com.busicomjp.sapp.repository.d.D2Repository;

@Service
public class D2Service {

	@Autowired
	D2Repository z2Repo;

	//取引先検索
	public List<TorihikisakiData> getTorihikiDataList(String searchName) {
		
		List<TorihikisakiData> dataList = z2Repo.selectToriHikiName(CompanyUtil.getCompanyCode(), searchName);
		List<TorihikisakiData> dataListFinal = new ArrayList<TorihikisakiData>();
		
		if (dataList == null || dataList.size() == 0) {
			return new ArrayList<TorihikisakiData>();
		}
        
		//仕入先
		if ("2".equals(dataList.get(0).getAccountKind1()) &&
				"01".equals(dataList.get(0).getAccountKind2()) &&
				"001".equals(dataList.get(0).getAccountKind3()) &&
				"0002".equals(dataList.get(0).getAccountKind4())) {
			dataList.get(0).setSupplierKbn("〇");
		}else {
			dataList.get(0).setSupplierKbn("");
		}
		
		//得意先
		if ("1".equals(dataList.get(0).getAccountKind1()) &&
				"01".equals(dataList.get(0).getAccountKind2()) &&
				"003".equals(dataList.get(0).getAccountKind3()) &&
				"0002".equals(dataList.get(0).getAccountKind4())) {
			dataList.get(0).setCustomerKbn("〇");
		}else {
			dataList.get(0).setCustomerKbn("");
		}
		
		//未払先
		if ("2".equals(dataList.get(0).getAccountKind1()) &&
				"01".equals(dataList.get(0).getAccountKind2()) &&
				"003".equals(dataList.get(0).getAccountKind3()) &&
				"0001".equals(dataList.get(0).getAccountKind4())) {
			dataList.get(0).setUnpaidKbn("〇");
		}else {
			dataList.get(0).setUnpaidKbn("");
		}

		dataListFinal.add(dataList.get(0));
		
        String comCode = dataList.get(0).getCompanyCode();
        String torihikiCode = dataList.get(0).getTorihikisakiCode();
        
		if (dataList.size() > 1) {
	        for(int i = 1; i <= dataList.size() - 1; i++) {
	        	
	        	if (comCode.equals(dataList.get(i).getCompanyCode()) && torihikiCode.equals(dataList.get(i).getTorihikisakiCode())){

	        		//仕入先
	    			if ("2".equals(dataList.get(i).getAccountKind1()) &&
	    					"01".equals(dataList.get(i).getAccountKind2()) &&
	    					"001".equals(dataList.get(i).getAccountKind3()) &&
	    					"0002".equals(dataList.get(i).getAccountKind4())) {
	    				dataListFinal.get(dataListFinal.size()-1).setSupplierKbn("〇");
	    				dataListFinal.get(dataListFinal.size()-1).setAccountCode(
	    						dataListFinal.get(dataListFinal.size()-1).getAccountCode().concat(dataList.get(i).getAccountCode()));
	    			}
	    			
	    			//得意先
	    			if ("1".equals(dataList.get(i).getAccountKind1()) &&
	    					"01".equals(dataList.get(i).getAccountKind2()) &&
	    					"003".equals(dataList.get(i).getAccountKind3()) &&
	    					"0002".equals(dataList.get(i).getAccountKind4())) {
	    				dataListFinal.get(dataListFinal.size()-1).setCustomerKbn("〇");
	    				dataListFinal.get(dataListFinal.size()-1).setAccountCode(
	    						dataListFinal.get(dataListFinal.size()-1).getAccountCode().concat(dataList.get(i).getAccountCode()));
	    			}
	    			
	    			//未払先
	    			if ("2".equals(dataList.get(i).getAccountKind1()) &&
	    					"01".equals(dataList.get(i).getAccountKind2()) &&
	    					"003".equals(dataList.get(i).getAccountKind3()) &&
	    					"0001".equals(dataList.get(i).getAccountKind4())) {
	    				dataListFinal.get(dataListFinal.size()-1).setUnpaidKbn("〇");
	    				dataListFinal.get(dataListFinal.size()-1).setAccountCode(
	    						dataListFinal.get(dataListFinal.size()-1).getAccountCode().concat(dataList.get(i).getAccountCode()));
	    			}
	    			
	        	}else {
	                comCode = dataList.get(i).getCompanyCode();
	                torihikiCode = dataList.get(i).getTorihikisakiCode();

	    			dataListFinal.add(dataList.get(i));
	    			
	        		//仕入先
	    			if ("2".equals(dataList.get(i).getAccountKind1()) &&
	    					"01".equals(dataList.get(i).getAccountKind2()) &&
	    					"001".equals(dataList.get(i).getAccountKind3()) &&
	    					"0002".equals(dataList.get(i).getAccountKind4())) {
	    				dataListFinal.get(dataListFinal.size()-1).setSupplierKbn("〇");
	    			}else {
	    				dataListFinal.get(dataListFinal.size()-1).setSupplierKbn("");
	    			}
	    			
	    			//得意先
	    			if ("1".equals(dataList.get(i).getAccountKind1()) &&
	    					"01".equals(dataList.get(i).getAccountKind2()) &&
	    					"003".equals(dataList.get(i).getAccountKind3()) &&
	    					"0002".equals(dataList.get(i).getAccountKind4())) {
	    				dataListFinal.get(dataListFinal.size()-1).setCustomerKbn("〇");
	    			}else {
	    				dataListFinal.get(dataListFinal.size()-1).setCustomerKbn("");
	    			}
	    			
	    			//未払先
	    			if ("2".equals(dataList.get(i).getAccountKind1()) &&
	    					"01".equals(dataList.get(i).getAccountKind2()) &&
	    					"003".equals(dataList.get(i).getAccountKind3()) &&
	    					"0001".equals(dataList.get(i).getAccountKind4())) {
	    				dataListFinal.get(dataListFinal.size()-1).setUnpaidKbn("〇");
	    			}else {
	    				dataListFinal.get(dataListFinal.size()-1).setUnpaidKbn("");
	    			}
	    		}
        	}
        }
		
		return dataListFinal;
	}
	
	//仕入先検索
	public List<ComboItem> supplierDataList(String companyCode) {
		
		List<ComboItem> dataList = z2Repo.selectSupplierList(companyCode);
		
		if (dataList == null || dataList.size() == 0) {
			return new ArrayList<ComboItem>();
		}
		
		return dataList;
	}
	
	//得意先検索
	public List<ComboItem> customerDataList(String companyCode) {
		
		List<ComboItem> dataList = z2Repo.selectCustomerList(companyCode);
		
		if (dataList == null || dataList.size() == 0) {
			return new ArrayList<ComboItem>();
		}
		
		return dataList;
	}
	
	//未払先検索
	public List<ComboItem> unpaidDataList(String companyCode) {
		
		List<ComboItem> dataList = z2Repo.selectUnpaidList(companyCode);
		
		if (dataList == null || dataList.size() == 0) {
			return new ArrayList<ComboItem>();
		}
		
		return dataList;
	}

	// 会社コード最大値取得
	public String getMaxTorihikisakiCd() {
		return z2Repo.getMaxTorihikisakiCode();
	}

	//取引先情報登録
	public void insertTorihikisakiData(String companyCode,String torihikiCode,String torihiki,String kana,String comboSelectedCode,String torihikisakiType) {

		z2Repo.insertTorihikisakiData(companyCode,torihikiCode,torihiki,kana,comboSelectedCode,torihikisakiType);
	}

	//取引先情報一括更新
	public void updateTorihikisakiDataMultiple(String companyCode,String torihikiCode,String torihikiName,String kana) {

		z2Repo.updateTorihikisakiDataMultiple(companyCode,torihikiCode,torihikiName,kana);
	}

	//取引先情報該当レコードのみ更新
	public void updateTorihikisakiDataOnly(String companyCode,String torihikiCode,String supplierCode,String torihikisakiTypeKbn,String delFlg) {

		z2Repo.updateTorihikisakiDataOnly(companyCode,torihikiCode,supplierCode,torihikisakiTypeKbn,delFlg);
	}
	
	//DBに存在してるか確認
	public String existJudgeSelect(String companyCode,String torihikiCode,String torihikisakiType) {
		return z2Repo.existJudgeSelect(companyCode,torihikiCode,torihikisakiType);
	}
}
