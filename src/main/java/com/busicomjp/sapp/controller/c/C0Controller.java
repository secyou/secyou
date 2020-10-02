package com.busicomjp.sapp.controller.c;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.service.c.C1Service;
import com.busicomjp.sapp.service.c.C2Service;
import com.busicomjp.sapp.service.c.C3Service;
import com.busicomjp.sapp.service.c.C4Service;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

@Component
public class C0Controller extends BaseController implements Initializable {

	@FXML
	Label companyName;
	@FXML
	Label fiscalYear;
	@FXML
	public CheckBox checkboxCover; // 表紙
	@FXML
	public CheckBox checkbox1; // 損益計算書(P/L)
	@FXML
	public CheckBox checkbox2; // 貸借対照表(B/S)
	@FXML
	public CheckBox checkbox3; // 製造原価報告書
	@FXML
	public CheckBox checkbox4; // 販売費・一般管理費計算書
	@FXML
	public CheckBox checkbox5; // キャッシュフロー計算書
	@FXML
	public CheckBox checkbox6; // 株主変動準備金
	@FXML
	public CheckBox checkbox7; // 個別注記表
	@FXML
	public CheckBox checkbox8; // 資金別貸借対照表
	@FXML
	public CheckBox checkbox9; // 財務分析表
	@FXML
	public CheckBox checkboxReportStatement; // 報告文
	@FXML
	public CheckBox checkboxAuditStatement; // 監査文

	@Autowired
	C1Service c1Service;

	@Autowired
	C2Service c2Service;
	@Autowired
	C3Service c3Service;
	@Autowired
	C4Service c4Service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void init() {
		companyName.setText(CompanyUtil.getCompanyName());
		fiscalYear.setText(CompanyUtil.getFiscalYear());
	}

	@FXML
	private void onPrintData() {
		if(checkbox1.isSelected()) {
			c1Service.print();
		}
		if(checkbox2.isSelected()) {
			c2Service.print();
		}
		if(checkbox3.isSelected()) {
			c3Service.print();
		}
		if(checkbox4.isSelected()) {
			c4Service.print();
		}
	}

}
