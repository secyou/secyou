package com.busicomjp.sapp.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.controller.d.D1Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@Component
public class CompanyManageController implements Initializable {
	
	private static Stage companyManageStage;
	
	@FXML
	private TabPane appTab;
	@FXML
	private D1Controller d1Controller;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML
	private void onMenuExit() {
		companyManageStage.hide();
	}

	public void showScene() {
		if (companyManageStage == null) {
			companyManageStage = new Stage();
		}

		if (companyManageStage.isShowing()) {
			companyManageStage.toFront();
		} else {
			companyManageStage.setScene(App.getCompanyManage());
			companyManageStage.setMaximized(true);
			companyManageStage.setTitle("会計アプリ - 会社管理");
			companyManageStage.getIcons().add(new Image("icon.png"));
			companyManageStage.show();
		}
	}
	
	public void selectTab(int index) {
		appTab.getSelectionModel().select(index);

		if (index == 0) {
			d1Controller.init();
		}
	}
}
