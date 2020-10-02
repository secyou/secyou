package com.busicomjp.sapp.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.item.CommonDialog;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.a.A1Controller;
import com.busicomjp.sapp.controller.a.A2SearchController;
import com.busicomjp.sapp.controller.a.A2ShowController;
import com.busicomjp.sapp.controller.a.A3Controller;
import com.busicomjp.sapp.controller.b.B1Controller;
import com.busicomjp.sapp.controller.b.B2Controller;
import com.busicomjp.sapp.controller.b.B3Controller;
import com.busicomjp.sapp.controller.b.B4Controller;
import com.busicomjp.sapp.controller.b.B5Controller;
import com.busicomjp.sapp.controller.b.B6Controller;
import com.busicomjp.sapp.controller.c.C0Controller;
import com.busicomjp.sapp.controller.d.D1CompanyListDialogController;
import com.busicomjp.sapp.controller.d.D2Controller;
import com.busicomjp.sapp.controller.d.D3Controller;
import com.busicomjp.sapp.controller.d.D4Controller;
import com.busicomjp.sapp.controller.e.E1Controller;
import com.busicomjp.sapp.controller.e.E2Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

@Component
public class FeatureGroupController implements Initializable {

	private static Stage featureStage;
	
	@Autowired
	private CompanyManageController companyManageController;
	@Autowired
	private CommonDialog commonDiaLog;

	@Autowired
	private ConfigurableApplicationContext context;
	@Autowired
	private A1Controller a1Controller;
	@Autowired
	private A2SearchController a2SearchController;
	@Autowired
	private A2ShowController a2Showcontroller;
	@Autowired
	private A3Controller a3Controller;
	@Autowired
	private B1Controller b1Controller;
	@Autowired
	private B2Controller b2Controller;
	@Autowired
	private B3Controller b3Controller;
	@Autowired
	private B4Controller b4Controller;
	@Autowired
	private B5Controller b5Controller;
	@Autowired
	private B6Controller b6Controller;
	@Autowired
	private C0Controller c0Controller;
	@Autowired
	private D2Controller d2Controller;
	@Autowired
	private D3Controller d3Controller;
	@Autowired
	private D4Controller d4Controller;
	@Autowired
	private E1Controller e1Controller;
	@Autowired
	private E2Controller e2Controller;
	
	@FXML
	public VBox mainVBox;
	@FXML
	private TabPane appTab;

	@Getter
	private Tab a1; // 自動仕訳
	@Getter
	private Tab a2search; // 仕訳検索
	@Getter
	private Tab a2show; // 仕訳帳
	@Getter
	private Tab a3; // 年度決算

	@Getter
	private Tab b1; // 合計残高試算表
	@Getter
	private Tab b2; // 総勘定元帳
	@Getter
	private Tab b3; // 資金収支表
	@Getter
	private Tab b4; // 売掛金集計表
	@Getter
	private Tab b5; // 買掛金集計表
	@Getter
	private Tab b6; // 未収金集計表

	@Getter
	private Tab c0; // その他帳票

	@Getter
	private Tab d2; // 取引先管理
	@Getter
	private Tab d3; // 勘定科目管理
	@Getter
	private Tab d4; // 摘要管理

	@Getter
	private Tab e1; // 初回繰越登録
	@Getter
	private Tab e2; // 翌年度繰越登録

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTabs();
		appTab.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				tabInit(newValue);
			}
		});
	}
	
	private void tabInit(Tab selectedTab) {
		if (selectedTab == null) {
			return;
		}
		
		if (selectedTab == a1) {
			a1Controller.init();
		} else if (selectedTab == a2search) {
			a2SearchController.init();
		} else if (selectedTab == a2show) {
			a2Showcontroller.init();
		} else if (selectedTab == a3) {
			a3Controller.init();
		} else if (selectedTab == b1) {
			b1Controller.init();
		} else if (selectedTab == b2) {
			b2Controller.init();
		} else if (selectedTab == b3) {
			b3Controller.init();
		} else if (selectedTab == b4) {
		    b4Controller.init();
		} else if (selectedTab == b5) {
			b5Controller.init();
		} else if (selectedTab == b6) {
			b6Controller.init();
		} else if (selectedTab == c0) {
			c0Controller.init();
		} else if (selectedTab == d2) {
			d2Controller.init();
		} else if (selectedTab == d3) {
			d3Controller.init();
		} else if (selectedTab == d4) {
			d4Controller.init();
		} else if (selectedTab == e1) {
			e1Controller.init1();
		} else if (selectedTab == e2) {
			e2Controller.init2();
		}
	}
	
	@FXML
	private void onMenuA1() {
		setGroupATabs();
		selectTab(a1);
	}
	
	@FXML
	private void onMenuA2() {
		setGroupATabs();
		selectTab(a2search);
	}
	
	@FXML
	private void onMenuA3() {
		setGroupATabs();
		selectTab(a3);
	}
	
	@FXML
	private void onMenuE1() {
		setGroupETabs();
		selectTab(e1);
	}
	
	@FXML
	private void onMenuE2() {
		setGroupFTabs();
		selectTab(e2);
	}
	
	@FXML
	private void onMenuB1() {
		setGroupBTabs();
		selectTab(b1);
	}
	
	@FXML
	private void onMenuB2() {
		setGroupBTabs();
		selectTab(b2);
	}
	
	@FXML
	private void onMenuB3() {
		setGroupBTabs();
		selectTab(b3);
	}
	
	@FXML
	private void onMenuB4() {
		setGroupBTabs();
		selectTab(b4);
	}
	
	@FXML
	private void onMenuB5() {
		setGroupBTabs();
		selectTab(b5);
	}
	
	@FXML
	private void onMenuB6() {
		setGroupBTabs();
		selectTab(b6);
	}
	
	@FXML
	private void onMenuC0() {
		setGroupBTabs();
		selectTab(c0);
	}
	
	@FXML
	private void onMenuD1() {
		companyManageController.showScene();
		companyManageController.selectTab(0);
	}
	
	@FXML
	private void onMenuD2() {
		setGroupDTabs();
		selectTab(d2);
	}
	
	@FXML
	private void onMenuD3() {
		setGroupDTabs();
		selectTab(d3);
	}
	
	@FXML
	private void onMenuD4() {
		setGroupDTabs();
		selectTab(d4);
	}
	
	@FXML
	private void onMenuSwitch() {
		Tab selected = appTab.getSelectionModel().getSelectedItem();
		commonDiaLog.showDialog("会社選択", "/fxml/d/d1_showCompanyListDialog.fxml", true);
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureStage.setTitle("会計アプリ - " + CompanyUtil.getCompanyName() + " ( " + CompanyUtil.getFiscalYear() + " )");
			tabInit(selected);
		}
	}
	
	@FXML
	private void onMenuExit() {
		featureStage.hide();
	}

	public void showScene() {
		if (featureStage == null) {
	        featureStage = new Stage();
		}

		featureStage.setTitle("会計アプリ - " + CompanyUtil.getCompanyName() + " ( " + CompanyUtil.getFiscalYear() + " )");
		if (featureStage.isShowing()) {
			featureStage.setMaximized(true);
			featureStage.toFront();
		} else {
			featureStage.setScene(App.getFeature());
			featureStage.setMaximized(true);
			featureStage.getIcons().add(new Image("icon.png"));
			featureStage.show();
		}
	}

	public void hideScene() {
		if (featureStage != null && featureStage.isShowing()) {
			featureStage.getScene().getWindow().hide();
		}
	}

	/**
     * グループAのTabを設定
     */
	public void setGroupATabs() {
		removeTabs();
		// グループA
		appTab.getTabs().add(a1);
		appTab.getTabs().add(a2search);
		appTab.getTabs().add(a2show);
		appTab.getTabs().add(b1);
		appTab.getTabs().add(b2);
		appTab.getTabs().add(a3);
	}

	/**
     * グループBのTabを設定
     */
	public void setGroupBTabs() {
		removeTabs();
		// グループB
		appTab.getTabs().add(b1);
		appTab.getTabs().add(b2);
		appTab.getTabs().add(b3);
		appTab.getTabs().add(b4);
		appTab.getTabs().add(b5);
		appTab.getTabs().add(b6);
		appTab.getTabs().add(c0);
	}

	/**
     * グループDのTabを設定
     */
	public void setGroupDTabs() {
		removeTabs();
		// グループD
		appTab.getTabs().add(d2);
		appTab.getTabs().add(d3);
		appTab.getTabs().add(d4);
	}

	/**
     * グループEのTabを設定
     */
	public void setGroupETabs() {
		removeTabs();
		// グループE
		appTab.getTabs().add(e1);
	}

	/**
     * グループEのTabを設定
     */
	public void setGroupFTabs() {
		removeTabs();
		// グループF
		appTab.getTabs().add(e2);
	}

	/**
     * Tabコンテンツの初期化
     */
	private void initTabs() {
		a1 = setTabContent("仕訳登録", "/fxml/a/a1_autoShiwake.fxml");
		a2search = setTabContent("仕訳検索", "/fxml/a/a2_searchShiwake.fxml");
		a2show = setTabContent("仕訳帳", "/fxml/a/a2_showShiwake.fxml");
		a3 = setTabContent("年度決算", "/fxml/a/a3_settlement.fxml");

		b1 = setTabContent("合計残高試算表", "/fxml/b/b1_showBalance.fxml");
		b2 = setTabContent("総勘定元帳", "/fxml/b/b2_showGeneralLedger.fxml");
		b3 = setTabContent("資金収支表", "/fxml/b/b3_showCashFlow.fxml");
		b4 = setTabContent("売掛金集計表", "/fxml/b/b4_showReceivable.fxml");
		b5 = setTabContent("買掛金集計表", "/fxml/b/b5_showPayable.fxml");
		b6 = setTabContent("未収金集計表", "/fxml/b/b6_showUnpaid.fxml");

		c0 = setTabContent("その他帳票出力", "/fxml/c/c0_printReport.fxml");

		d2 = setTabContent("取引先管理", "/fxml/d/d2_maintenanceTorihikisaki.fxml");
		d3 = setTabContent("勘定科目管理", "/fxml/d/d3_maintenanceAccount.fxml");
		d4 = setTabContent("摘要管理", "/fxml/d/d4_maintenanceTekiyo.fxml");

		e1 = setTabContent("前期(初回)繰越", "/fxml/e/e1_showCarryForward.fxml");
		e2 = setTabContent("翌年度繰越", "/fxml/e/e2_showNextTermCarryForward.fxml");
	}

	private Tab setTabContent(String tabName, String fxmlPath) {
		Tab tab = new Tab();
		tab.setText(tabName);
		tab.setClosable(false);

		try {
			FXMLLoader lorder = new FXMLLoader(getClass().getResource(fxmlPath));
			lorder.setControllerFactory(context::getBean);
			GridPane gp = lorder.load();
			tab.setContent(gp);
		} catch (IOException e) {
			throw new SystemException(e);
		}
		return tab;
	}

	/**
     * 既存のTabを全部削除
     */
	private void removeTabs() {
		while (appTab.getTabs().size() > 0) {
			appTab.getTabs().remove(0);
		}
	}

	/**
     * Tab切替
     */
	public void selectTab(Tab tab) {
		appTab.getSelectionModel().select(tab);
	}

	/**
     * 残高試算表から総勘定元帳画面へ遷移
     */
	public void showGeneralLedger(String accountCode, String accountName) {
		appTab.getSelectionModel().select(b2);

		b2Controller.init2(accountCode, accountName);
	}
	
	

}
