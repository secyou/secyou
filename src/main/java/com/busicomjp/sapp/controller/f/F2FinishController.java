package com.busicomjp.sapp.controller.f;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.controller.FeatureGroupController;
import com.busicomjp.sapp.dto.d.CompanyMDto;
import com.busicomjp.sapp.model.d.CompanyData;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;

@Component
public class F2FinishController extends BaseController implements Initializable {

	private CompanyData companyData;
	
	@Autowired
	private FeatureGroupController featureController;

	@FXML
	private Label companyName;
	@FXML
	private Label settlPeriod;
	@FXML
	private Label fiscalYear;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void init(CompanyMDto dto) {
		companyName.setText(dto.getCompanyName());
		settlPeriod.setText("第" + dto.getSettlPeriod() + "期");		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
			Calendar calendar = Calendar.getInstance();

			Date endDate = sdf.parse(dto.getKimatuYear() + dto.getKimatuMonthDay());
			calendar.setTime(endDate);
			calendar.add(Calendar.YEAR, -1);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date startDate = calendar.getTime();

			fiscalYear.setText(sdfSlash.format(startDate) + "～" + sdfSlash.format(endDate));
		} catch (ParseException e) {
			throw new SystemException(e);
		}
		
		companyData = new CompanyData();
		companyData.setCompanyCode(dto.getCompanyCode());
		companyData.setCompanyName(dto.getCompanyName());
		companyData.setKimatuYear(dto.getKimatuYear());
		companyData.setKimatuMonthDay(dto.getKimatuMonthDay());
		companyData.setAccountDate(dto.getKimatuYear(), dto.getKimatuMonthDay());
		companyData.setFirstFlg("1"); // True固定値
	}
	
	@FXML
	private void onMainteData(Event eve) {
		CompanyUtil.setCompanyUtil(companyData);
		((Node) eve.getSource()).getScene().getWindow().hide();
		featureController.setGroupDTabs();
		featureController.showScene();
		featureController.selectTab(featureController.getD2());
	}
	
	@FXML
	private void onCarryForward(Event eve) {
		CompanyUtil.setCompanyUtil(companyData);
		((Node) eve.getSource()).getScene().getWindow().hide();
		featureController.setGroupETabs();
		featureController.showScene();
		featureController.selectTab(featureController.getE1());
	}
	
	@FXML
	private void onRegistShiwake(Event eve) {
		CompanyUtil.setCompanyUtil(companyData);
		((Node) eve.getSource()).getScene().getWindow().hide();
		featureController.setGroupATabs();
		featureController.showScene();
        featureController.selectTab(featureController.getA1());
	}
	
	@FXML
	private void onFinish(Event eve) {
		((Node) eve.getSource()).getScene().getWindow().hide();
	}
	
}
