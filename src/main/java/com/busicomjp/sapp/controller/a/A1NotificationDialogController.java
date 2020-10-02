package com.busicomjp.sapp.controller.a;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.text.Text;

@Component
public class A1NotificationDialogController extends BaseController implements Initializable  {

	static final Logger log = LoggerFactory.getLogger(A1NotificationDialogController.class);
	
	@FXML
	private Text billKingaku1;
	@FXML
	private Text billKingaku2;
	@FXML
	private Text billMaturityDate1;
	@FXML
	private Text billMaturityDate2;
	@FXML
	private Text billMaturityDate3;
	@FXML
	private Text billMaturityDate4;
	@FXML
	private Text billAccount;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	public void init(String account, String kingaku, String maturityDate) {
		billAccount.setText(account);
		billKingaku1.setText(StringUtil.commaFormat(kingaku));
		billKingaku2.setText(StringUtil.commaFormat(kingaku));
		billMaturityDate1.setText(StringUtil.dateSlashFormat(maturityDate));
		billMaturityDate2.setText(StringUtil.dateSlashFormat(maturityDate));
		billMaturityDate3.setText(StringUtil.dateSlashFormat(maturityDate));
		billMaturityDate4.setText(StringUtil.dateSlashFormat(maturityDate));
	}

	@FXML
	private void onOK(Event eve) {
		// ダイアログを閉じる
		((Node) eve.getSource()).getScene().getWindow().hide();
	}

}
