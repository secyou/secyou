package com.busicomjp.sapp.dto.z;

import lombok.Data;

@Data
public class HintParams {

	// 会社コード
	private String companyCode;
	// 摘要コード
	private String tekiyoCode;
	// 取引先コード
	private String torihikisakiCode;
	// ヒント文字列
	private String hint;
	
}
