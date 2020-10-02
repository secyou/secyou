package com.busicomjp.sapp.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.item.CommonDialog;
import com.busicomjp.sapp.controller.d.D1CompanyListDialogController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

@Component
public class MainMenuController implements Initializable {

	static final Logger log = LoggerFactory.getLogger(MainMenuController.class);

	@Autowired
	private FeatureGroupController featureController;
	@Autowired
	private CompanyManageController companyManageController;
	@Autowired
	private CommonDialog commonDialog;
	@FXML
	private GridPane main_gp;
	@FXML
	private GridPane sub_gp1;
	@FXML
	private GridPane sub_gp2;
	@FXML
	private GridPane sub_gp3;
	@FXML
	GridPane sub_gp4;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sub_gp1.prefWidthProperty().bind(main_gp.widthProperty().subtract(70.0).multiply(0.25));
		sub_gp2.prefWidthProperty().bind(main_gp.widthProperty().subtract(70.0).multiply(0.25));
		sub_gp3.prefWidthProperty().bind(main_gp.widthProperty().subtract(70.0).multiply(0.25));
		sub_gp4.prefWidthProperty().bind(main_gp.widthProperty().subtract(70.0).multiply(0.25));
	}
	
	@FXML
	private void onMenuExit() {
		Platform.exit();
	}

	/**
     * 会社新規登録
     */
	@FXML
    private void onClick_f0() throws IOException {
		commonDialog.showScrollDialog("会社新規登録", "/fxml/f/f1_registCompany.fxml", true);
    }

	/**
     * 初回繰越登録
     */
	@FXML
    private void onClick_e1() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupETabs();
			featureController.showScene();
			featureController.selectTab(featureController.getE1());
		}
    }

	/**
     * 翌年度繰越登録
     */
	@FXML
    private void onClick_e2() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {

			if(!D1CompanyListDialogController.isRegistNextyearFlg()) {
				commonDialog.showDialog("翌年度情報登録", "/fxml/e/e2_registNextTermInfoDialog.fxml", true);
			}else {

				featureController.setGroupFTabs();
				featureController.showScene();
				featureController.selectTab(featureController.getE2());
			}
		}
    }

	/**
     * 自動仕訳
     */
	@FXML
    private void onClick_a1() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupATabs();
			featureController.showScene();
	        featureController.selectTab(featureController.getA1());
		}
    }

	/**
     * 仕訳帳
     */
	@FXML
    private void onClick_a2() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupATabs();
			featureController.showScene();
			featureController.selectTab(featureController.getA2search());
		}
    }

	/**
     * 年度仕訳
     */
	@FXML
    private void onClick_a3() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupATabs();
			featureController.showScene();
			featureController.selectTab(featureController.getA3());
		}
    }

	/**
     * 残高試算表
     */
	@FXML
    private void onClick_b1() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getB1());
		}
    }

	/**
     * 総勘定元帳
     */
	@FXML
    private void onClick_b2() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getB2());
		}
    }

	/**
     * 資金収支表
     */
	@FXML
    private void onClick_b3() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getB3());
		}
    }

	/**
     * 売掛金集計表
     */
	@FXML
    private void onClick_b4() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getB4());
		}
    }

	/**
     * 買掛金集計表
     */
	@FXML
    private void onClick_b5() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getB5());
		}
    }

	/**
     * 未払金集計表
     */
	@FXML
    private void onClick_b6() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getB6());
		}
    }

	/**
     * 帳票出力
     */
	@FXML
    private void onClick_c1() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupBTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getC0());
		}
    }

	/**
     * 会社情報管理
     */
	@FXML
    private void onClick_d1() throws IOException {
		companyManageController.showScene();
		companyManageController.selectTab(0);
    }

	/**
     * 取引先管理
     */
	@FXML
    private void onClick_d2() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupDTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getD2());
		}
    }

	/**
     * 勘定科目管理
     */
	@FXML
    private void onClick_d3() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupDTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getD3());
		}
    }

	/**
     * 摘要管理
     */
	@FXML
    private void onClick_d4() throws IOException {
		showCompanyInfoDialog();
		if (D1CompanyListDialogController.isCompanySelectedFlg()) {
			featureController.setGroupDTabs();
			featureController.showScene();
			featureController.selectTab(featureController.getD4());
		}
    }

	/**
     * 会社情報選択ダイアログを表示
     */
	public void showCompanyInfoDialog() {
		commonDialog.showDialog("会社選択", "/fxml/d/d1_showCompanyListDialog.fxml", true);
	}

}
