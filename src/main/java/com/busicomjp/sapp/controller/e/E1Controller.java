package com.busicomjp.sapp.controller.e;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.controller.z.Z2CarryForwardController;

import javafx.fxml.Initializable;

@Component
public class E1Controller extends Z2CarryForwardController implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.z2init();
	}

	public void init1() {
		init(CommonConstants.CARRY_FORWARD.FIRST_TERM);
	}
}
