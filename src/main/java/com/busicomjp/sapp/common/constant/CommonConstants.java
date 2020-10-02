package com.busicomjp.sapp.common.constant;

public class CommonConstants {

	public static class SHIWAKE_KIND {
		/** 入金 */
		public final static String MONEY_RECEIVED = "00001";
		/** 出金 */
		public final static String WITHDRAWAL = "00002";
		/** 売掛金 */
		public final static String ACCOUNTS_RECEIVABLE = "00003";
		/** 買掛金 */
		public final static String ACCOUNTS_PAYABLE = "00004";
		/** 未払 */
		public final static String UNPAID = "00005";
		/** 受取手形 */
		public final static String BILLS_RECIVABLE = "00006";
		/** 支払手形 */
		public final static String BILLS_PAYMENT = "00007";
	}

	public static class COMPANY_TAX_KIND {
		/** 免税事業 */
		public final static String NO_TAX = "0";
		/** 課税事業 */
		public final static String TAX = "1";
	}

	public static class COMPANY_TAX_SUMMARY {
		/** 都度集計 */
		public final static String ALWAYS = "1";
		/** 月単位集計 */
		public final static String MONTHLY = "2";
		/** 年単位集計 */
		public final static String ANNUAL = "3";
	}

	public static class TAX_CODE {
		/** 不課税 */
		public final static String TAX_FREE = "01"; // 課税対象ではないこと
		/** 非課税 */
		public final static String TAX_EXEMPT = "02"; // 本来は課税取引なのに、何の理由により免税すること
	}

	public static class ACCOUNT_CODE {
		/** 通常預金 */
		public final static String ORDINARY_DEPOSIT = "121";

		/** 仮払消費税等 */
		public final static String DEBIT_TAX = "218";
		/** 仮受消費税等 */
		public final static String CREDIT_TAX = "330";

		/** 受取手形 */
		public final static String RECEIPT_BILLS = "180";
		/** 支払手形 */
		public final static String PAYMENT_BILLS = "301";
		/** 割引手形 */
		public final static String DISCOUNT_BILLS = "319";
		/** 裏書手形 */
		public final static String ENDORSEMENT_BILLS = "320";

		/** 前期繰越 */
		public final static String CARRY_FORWARD = "997";
		/** 次期繰越 */
		public final static String NEXT_CARRY_FORWARD = "998";
	}

	public static class ACCOUNT_KIND {
		/** 資産 */
		public final static String ASSETS = "1";
		/** 負債 */
		public final static String LIABILITIES = "2";
		/** 純資産 */
		public final static String NET_ASSETS = "3";
		/** 収益 */
		public final static String PROFIT = "4";
		/** 費用 */
		public final static String COST = "5";
	}

	public static class ACCOUNT_KIND_UNION {
		/** 売掛金 */
		public final static String RECEIVABLE = "1,01,003,0002";
		/** 買掛金 */
		public final static String PAYABLE = "2,01,001,0002";
		/** 未払金 */
		public final static String UNPAID = "2,01,003,0001";
	}

	public static class BILL_TRANSACTION_STATUS {
		/** 受取 */
		public final static String RECEIPT = "1";
		/** 支払 */
		public final static String PAYMENT = "2";
		/** 割引 */
		public final static String DISCOUNT = "3";
		/** 裏書 */
		public final static String ENDORSEMENT = "4";
		/** 不渡 */
		public final static String NON_DELIVERY = "7";
		/** 取消 */
		public final static String CANCEL = "8";
		/** 満期 */
		public final static String MATURITY = "9";
	}

	public static class RED_FLG {
		/** 赤データ */
		public final static String RED = "1";
		/** 黒データ */
		public final static String BLACK = "0";
	}

	public static class DEBIT_CREDIT_FLG {
		/** 借方 */
		public final static String DEBIT = "1";
		/** 貸方 */
		public final static String CREDIT = "2";
	}

	public static class CARRY_FORWARD {
		/** 初回繰越 */
		public final static String FIRST_TERM = "1";
		/** 翌年繰越 */
		public final static String NEXT_TERM = "2";
	}
}
