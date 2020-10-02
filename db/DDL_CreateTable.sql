/*
テーブル削除*/

drop table JOURNAL_ENTRY; /* 仕訳テーブル */
drop table GENERAL_LEDGER; /* 総勘定元帳テーブル */
drop table COMPANY_M; /* 会社マスタテーブル */
drop table ACCOUNT_M; /* 勘定科目マスタテーブル */
drop table SUMMARY_M; /* 摘要マスタテーブル */
drop table TORIHIKISAKI_M; /* 取引マスタテーブル */
drop table TAX_M; /* 消費税マスタテーブル */
drop table CREDIT_DEBIT_KIND_M; /* 借方貸方分類マスタテーブル */



/*
仕訳テーブル
*/
CREATE TABLE JOURNAL_ENTRY (
company_code CHAR(5) NOT NULL,
journal_no CHAR(9) NOT NULL,
org_journal_no CHAR(9),
kind_code CHAR(5),
accrual_date CHAR(8) NOT NULL,
suppliers_code CHAR(6),
tekiyo_code CHAR(4),
debit_account CHAR(3) NOT NULL,
credit_account CHAR(3) NOT NULL,
amount_money BIGINT  NOT NULL,
amount_money_tax BIGINT  NOT NULL,
tax_code CHAR(2) NOT NULL,
dep_pay_date CHAR(8),
red_flg CHAR(1),
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,journal_no)
);



/*
総勘定元帳
*/
CREATE TABLE GENERAL_LEDGER (
company_code CHAR(5) NOT NULL,
general_no CHAR(9) NOT NULL,
journal_no CHAR(9) NOT NULL,
accrual_date CHAR(8) NOT NULL,
account_code CHAR(3) NOT NULL,
tekiyo_code CHAR(4),
counter_account CHAR(3) NOT NULL,
debit_amount_money BIGINT  NOT NULL,
credit_amount_money BIGINT  NOT NULL,
balance_money BIGINT  NOT NULL,
dep_pay_date CHAR(8),
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,general_no)
);

/*
手形取引
*/

CREATE TABLE BILLS_TRANSACTION (
company_code CHAR(5) NOT NULL,
get_journal_no CHAR(9) NOT NULL,
get_date CHAR(8) NOT NULL,
get_suppliers_code CHAR(6),
use_journal_no CHAR(9),
use_date CHAR(8),
use_suppliers_code CHAR(6),
amount_money BIGINT  NOT NULL,
maturity_date CHAR(8) NOT NULL,
status CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,get_journal_no)
);


/*
会社マスタ
*/
CREATE TABLE COMPANY_M (
company_code CHAR(5) NOT NULL,
business_type CHAR(1) NOT NULL,
company_name VARCHAR(100) NOT NULL,
company_name_kana VARCHAR(100) NOT NULL,
street_address VARCHAR(100),
corp_number CHAR(15),
business_line CHAR(1) NOT NULL,
bule_dec CHAR(1) NOT NULL,
est_date CHAR(8),
settl_period CHAR(3) NOT NULL,
kisyu_month CHAR(6) NOT NULL,
kimatu_month CHAR(6) NOT NULL,
kimatu_year CHAR(4) NOT NULL,
kimatu_month_day CHAR(4) NOT NULL,
Input_start_month CHAR(6) NOT NULL,
tel_number VARCHAR(15),
fax_number VARCHAR(15),
mail_address VARCHAR(40),
director_name VARCHAR(40),
tax_kind_flg CHAR(1) NOT NULL,
tax_app_flg CHAR(1),
first_flg CHAR(1) NOT NULL,
short_flg CHAR(1) NOT NULL,
del_flg CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,kimatu_year)
);


/*
勘定科目マスタ
*/
CREATE TABLE ACCOUNT_M (
company_code CHAR(5) NOT NULL,
account_kind1 CHAR(1) NOT NULL,
account_kind1_name VARCHAR(100) NOT NULL,
account_kind2 CHAR(2) NOT NULL,
account_kind2_name VARCHAR(100) NOT NULL,
account_kind3 CHAR(3) NOT NULL,
account_kind3_name VARCHAR(100) NOT NULL,
account_kind4 CHAR(4) NOT NULL,
account_kind4_name VARCHAR(100) NOT NULL,
account_code CHAR(3) NOT NULL,
account_name VARCHAR(100) NOT NULL,
account_name_kana VARCHAR(100) NOT NULL,
kind_flg CHAR(1),
agg_flg CHAR(1) NOT NULL,
use_flg CHAR(1) NOT NULL,
del_flg CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,account_code)
);


/*
摘要マスタ
*/

CREATE TABLE SUMMARY_M (
company_code CHAR(5) NOT NULL,
tekiyo_code CHAR(4) NOT NULL,
account_kind CHAR(1) NOT NULL,
tekiyo_name VARCHAR(100) NOT NULL,
tekiyo_name_kana VARCHAR(100) NOT NULL,
account_code CHAR(3) NOT NULL,
del_flg CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,tekiyo_code,account_kind)
);

/*
取引先マスタ
*/
CREATE TABLE TORIHIKISAKI_M (
company_code CHAR(5) NOT NULL,
torihikisaki_code CHAR(5) NOT NULL,
torihikisaki_type CHAR(1) NOT NULL,
torihikisaki_name VARCHAR(100) NOT NULL,
torihikisaki_name_kana VARCHAR(100) NOT NULL,
account_code CHAR(3),
del_flg CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (company_code,torihikisaki_code,torihikisaki_type)
);

/*
消費税マスタ
*/

CREATE TABLE TAX_M (
tax_code CHAR(2) NOT NULL,
tax_name VARCHAR(100) NOT NULL,
tax_value INT  NOT NULL,
del_flg CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (tax_code)
);


/*
借方貸方分類マスタ
*/

CREATE TABLE CREDIT_DEBIT_KIND_M (
kind_code CHAR(5) NOT NULL,
kind_name VARCHAR(100) NOT NULL,
kind_flg CHAR(1) NOT NULL,
account_kind1 CHAR(1) NOT NULL,
account_kind2 CHAR(2) NOT NULL,
account_kind3 CHAR(3) NOT NULL,
account_kind4 CHAR(4) NOT NULL,
del_flg CHAR(1) NOT NULL,
reg_date TIMESTAMP  NOT NULL,
upd_date TIMESTAMP  NOT NULL,
PRIMARY KEY (kind_code,account_kind1,account_kind2,account_kind3,account_kind4)
);

