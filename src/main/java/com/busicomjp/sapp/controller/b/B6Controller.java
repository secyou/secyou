package com.busicomjp.sapp.controller.b;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.b.TorihikisakiSummaryData;
import com.busicomjp.sapp.service.b.B6Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

@Component
public class B6Controller extends BaseController implements Initializable {

	private Logger logger = LoggerFactory.getLogger(B6Controller.class);
	@FXML
	private TableView<TorihikisakiSummaryData> unpaidDataTable;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> torihikisakiAndDate;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> carryForwardAmountMoney;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> month1;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> depPay1;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> request1;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> balance1;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> month2;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> depPay2;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> request2;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> balance2;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> month3;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> depPay3;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> request3;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> balance3;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> month4;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> depPay4;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> request4;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> balance4;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> month5;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> depPay5;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> request5;
	@FXML
	private TableColumn<TorihikisakiSummaryData, String> balance5;
	@FXML
	private Button printPdfBtn;
	@FXML
	private Button firstDataBtn;
	@FXML
	private Button prevDataBtn;
	@FXML
	private Button nextDataBtn;
	@FXML
	private Button lastDataBtn;
	
	@Autowired
	private B6Service b6Service;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
	private Date currentDate = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		torihikisakiAndDate.setCellValueFactory(new PropertyValueFactory<>("torihikisakiAndDate"));
		carryForwardAmountMoney.setCellValueFactory(new PropertyValueFactory<>("carryForwardAmountMoney"));
		month1.setCellValueFactory(new PropertyValueFactory<>("month1"));
		depPay1.setCellValueFactory(new PropertyValueFactory<>("depPay1"));
		request1.setCellValueFactory(new PropertyValueFactory<>("request1"));
		balance1.setCellValueFactory(new PropertyValueFactory<>("balance1"));
		month2.setCellValueFactory(new PropertyValueFactory<>("month2"));
		depPay2.setCellValueFactory(new PropertyValueFactory<>("depPay2"));
		request2.setCellValueFactory(new PropertyValueFactory<>("request2"));
		balance2.setCellValueFactory(new PropertyValueFactory<>("balance2"));
		month3.setCellValueFactory(new PropertyValueFactory<>("month3"));
		depPay3.setCellValueFactory(new PropertyValueFactory<>("depPay3"));
		request3.setCellValueFactory(new PropertyValueFactory<>("request3"));
		balance3.setCellValueFactory(new PropertyValueFactory<>("balance3"));
		month4.setCellValueFactory(new PropertyValueFactory<>("month4"));
		depPay4.setCellValueFactory(new PropertyValueFactory<>("depPay4"));
		request4.setCellValueFactory(new PropertyValueFactory<>("request4"));
		balance4.setCellValueFactory(new PropertyValueFactory<>("balance4"));
		month5.setCellValueFactory(new PropertyValueFactory<>("month5"));
		depPay5.setCellValueFactory(new PropertyValueFactory<>("depPay5"));
		request5.setCellValueFactory(new PropertyValueFactory<>("request5"));
		balance5.setCellValueFactory(new PropertyValueFactory<>("balance5"));

		torihikisakiAndDate.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.20));
		carryForwardAmountMoney.prefWidthProperty()
				.bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.08));
		depPay1.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request1.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance1.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay2.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request2.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance2.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay3.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request3.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance3.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay4.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request4.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance4.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay5.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request5.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance5.prefWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));

		torihikisakiAndDate.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.20));
		carryForwardAmountMoney.maxWidthProperty()
				.bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.08));
		depPay1.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request1.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance1.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay2.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request2.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance2.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay3.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request3.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance3.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay4.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request4.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance4.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		depPay5.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		request5.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
		balance5.maxWidthProperty().bind(unpaidDataTable.widthProperty().subtract(10.0).multiply(0.048));
	}

	@FXML
	private void onPrintData() {
        // 進捗インジケータ
        Alert alert = commonAlert.getProgressIndicatorAlert();
        alert.show();
        
        Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                Task<String> task = new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                    	String companyCode = CompanyUtil.getCompanyCode();
                		b6Service.generatePdfReport(companyCode);
                        return "OK";
                    }
                };
                commonTask.setTaskHandling(task, alert);
                return task;
            }
        };
        service.start();
	}

	@FXML
	private void onFirstData() {
		logger.info("<<");
		if (currentDate != null) {
			init();
		}
	}

	@FXML
	private void onPrevData() {
		logger.info("<");

		if (currentDate != null) {
			Date startDate = b6Service.getKiStartDate();
			if (b6Service.compareYearMonthDate(currentDate, startDate) != 0) {
				currentDate = b6Service.getYearMonth(currentDate, -1);
				String startYearMonth = dateFormat.format(currentDate);
				String endYearMonth = dateFormat.format(b6Service.getYearMonth(currentDate, 4));
				String companyCode = CompanyUtil.getCompanyCode();

				// 検索
				List<TorihikisakiSummaryData> dataList = b6Service.selectAccountsSummary(companyCode,
						startYearMonth, endYearMonth, false);
				final ObservableList<TorihikisakiSummaryData> observableList = FXCollections
						.observableArrayList(dataList);
				unpaidDataTable.getItems().clear();
				unpaidDataTable.getItems().addAll(observableList);

				displayYearMonth(startYearMonth, endYearMonth);

				if (dataList != null && dataList.size() > 0) {
					printPdfBtn.setDisable(false);
				} else {
					printPdfBtn.setDisable(true);
				}
			}

			if (b6Service.compareYearMonthDate(currentDate, startDate) == 0) {
				firstDataBtn.setDisable(true);
				prevDataBtn.setDisable(true);
				nextDataBtn.setDisable(false);
				lastDataBtn.setDisable(false);
			} else {
				firstDataBtn.setDisable(false);
				prevDataBtn.setDisable(false);
				nextDataBtn.setDisable(false);
				lastDataBtn.setDisable(false);
			}
		}
	}

	@FXML
	private void onNextData() {
		logger.info(">");
		if (currentDate == null) {
			currentDate = b6Service.getKiStartDate();
		}

		Date date = b6Service.getYearMonth(currentDate, 4);
		Date kiEndMonth = b6Service.getKiEndMonth();
		Date endDate = null;
		if (b6Service.compareYearMonthDate(date, kiEndMonth) < 0) {
			currentDate = b6Service.getYearMonth(currentDate, 1);
			String startYearMonth = dateFormat.format(currentDate);
			endDate = b6Service.getYearMonth(currentDate, 4);
			String endYearMonth = dateFormat.format(endDate);
			String companyCode = CompanyUtil.getCompanyCode();

			// 検索
			List<TorihikisakiSummaryData> dataList = b6Service.selectAccountsSummary(companyCode,
					startYearMonth, endYearMonth, false);
			final ObservableList<TorihikisakiSummaryData> observableList = FXCollections.observableArrayList(dataList);
			unpaidDataTable.getItems().clear();
			unpaidDataTable.getItems().addAll(observableList);

			displayYearMonth(startYearMonth, endYearMonth);

			if (dataList != null && dataList.size() > 0) {
				printPdfBtn.setDisable(false);
			} else {
				printPdfBtn.setDisable(true);
			}
		}

		if (b6Service.compareYearMonthDate(endDate, kiEndMonth) == 0) {
			firstDataBtn.setDisable(false);
			prevDataBtn.setDisable(false);
			nextDataBtn.setDisable(true);
			lastDataBtn.setDisable(true);
		} else {
			firstDataBtn.setDisable(false);
			prevDataBtn.setDisable(false);
			nextDataBtn.setDisable(false);
			lastDataBtn.setDisable(false);
		}
	}

	@FXML
	private void onLastData() {
		logger.info(">>");
		Date kiEndMonth = b6Service.getKiEndMonth();
		currentDate = b6Service.getYearMonth(kiEndMonth, -4);

		String startYearMonth = dateFormat.format(currentDate);
		String endYearMonth = dateFormat.format(kiEndMonth);
		String companyCode = CompanyUtil.getCompanyCode();

		// 検索
		List<TorihikisakiSummaryData> dataList = b6Service.selectAccountsSummary(companyCode, startYearMonth,
				endYearMonth, false);
		final ObservableList<TorihikisakiSummaryData> observableList = FXCollections.observableArrayList(dataList);
		unpaidDataTable.getItems().clear();
		unpaidDataTable.getItems().addAll(observableList);

		displayYearMonth(startYearMonth, endYearMonth);

		if (dataList != null && dataList.size() > 0) {
			printPdfBtn.setDisable(false);
		} else {
			printPdfBtn.setDisable(true);
		}

		firstDataBtn.setDisable(false);
		prevDataBtn.setDisable(false);
		nextDataBtn.setDisable(true);
		lastDataBtn.setDisable(true);
	}

	public void init() {
		bindingKeyPress();
		Date startDate = b6Service.getKiStartDate();
		currentDate = startDate;

		String startYearMonth = dateFormat.format(startDate);
		String endYearMonth = dateFormat.format(b6Service.getYearMonth(currentDate, 4));
		String companyCode = CompanyUtil.getCompanyCode();

		// 検索
		List<TorihikisakiSummaryData> dataList = b6Service.selectAccountsSummary(companyCode, startYearMonth,
				endYearMonth, false);
		final ObservableList<TorihikisakiSummaryData> observableList = FXCollections.observableArrayList(dataList);
		unpaidDataTable.getItems().clear();
		unpaidDataTable.getItems().addAll(observableList);

		displayYearMonth(startYearMonth, endYearMonth);

		if (dataList != null && dataList.size() > 0) {
			printPdfBtn.setDisable(false);
		} else {
			printPdfBtn.setDisable(true);
		}

		firstDataBtn.setDisable(true);
		prevDataBtn.setDisable(true);
		nextDataBtn.setDisable(false);
		lastDataBtn.setDisable(false);
	}

	private void displayYearMonth(String startYearMonth, String endYearMonth) {
		List<String> yearMonthList = b6Service.getDisplayYearMonthList(startYearMonth, endYearMonth);
		String yearMonth = null;
		for (int i = 0; i < yearMonthList.size(); i++) {
			yearMonth = yearMonthList.get(i);
			yearMonth = yearMonth.substring(0, 4) + "/" + yearMonth.substring(4);
			if (i == 0) {
				month1.setText(yearMonth);
			} else if (i == 1) {
				month2.setText(yearMonth);
			} else if (i == 2) {
				month3.setText(yearMonth);
			} else if (i == 3) {
				month4.setText(yearMonth);
			} else if (i == 4) {
				month5.setText(yearMonth);
			}
		}
	}
	
	private void bindingKeyPress() {
		App.clearKeyEvent();
		KeyCombination f9 = new KeyCodeCombination(KeyCode.F9);
		App.putKeyEvent(f9, new Runnable() {
			@Override
			public void run() {
				onPrintData();
			}
		});
	}
}
