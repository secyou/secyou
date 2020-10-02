package com.busicomjp.sapp.dto.a;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class InputDataDto {
	
	private String companyCode;
	
	// 分類コード
	private String selectCode;
	
	// オリジナル仕訳No
	private String orgJournalNo;
	
	@NotEmpty(message = "発生日を入力してください。")
	@Size(min=8, max=8, message = "発生日はMMDD形式の4桁で入力してください。")
	private String accrualDate;
	
	private String suppliersCode;
	
	private String tekiyoCode;
	
	@NotEmpty(message = "借方勘定科目を選択してください。")
	private String debit;
	
	@NotEmpty(message = "貸方勘定科目を選択してください。")
	private String credit;
	
	// 金額(税込)
	@NotEmpty(message = "金額を入力してください。")
	@Pattern(regexp = "^([-+]?[0]*[1-9][0-9]*)$", message="金額は0以外の整数で入力してください。")
	private String kingaku;
	
	// 税コード
	@NotEmpty(message = "消費税区分を選択してください。")
	private String taxCode;
	
	@NotEmpty(message = "入金・支払予定日(手形満期日)を入力してください。")
	@Size(min=8, max=8, message = "入金・支払予定日(手形満期日)はYYYYMMDD形式の8桁で入力してください。")
	private String paymentDate;
	
	// 赤データ識別フラグ
	private String redFlg;

	// 手形満期日
	private String billMaturityDate;
	
	// 前期繰越データフラグ
	private boolean carryForwardFlg;

}
