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

import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.b.CashFlowData;
import com.busicomjp.sapp.service.b.B3Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

@Component
public class B3Controller extends BaseController implements Initializable {
	private Logger logger = LoggerFactory.getLogger(B3Controller.class);
	@FXML
	private TableView<CashFlowData> cashFlowDataTable;
	@FXML
	private TableColumn<CashFlowData, String> rowTitle;
	@FXML
	private TableColumn<CashFlowData, String> month1;
	@FXML
	private TableColumn<CashFlowData, String> month2;
	@FXML
	private TableColumn<CashFlowData, String> month3;
	@FXML
	private TableColumn<CashFlowData, String> month4;
	@FXML
	private TableColumn<CashFlowData, String> month5;
	@FXML
	private TableColumn<CashFlowData, String> month6;
	@FXML
	private TableColumn<CashFlowData, String> month7;
	@FXML
	private TableColumn<CashFlowData, String> month8;
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
	private B3Service b3Service;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
	private Date currentDate = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		rowTitle.setCellValueFactory(new PropertyValueFactory<>("rowTitle"));
		month1.setCellValueFactory(new PropertyValueFactory<>("money1"));
		month2.setCellValueFactory(new PropertyValueFactory<>("money2"));
		month3.setCellValueFactory(new PropertyValueFactory<>("money3"));
		month4.setCellValueFactory(new PropertyValueFactory<>("money4"));
		month5.setCellValueFactory(new PropertyValueFactory<>("money5"));
		month6.setCellValueFactory(new PropertyValueFactory<>("money6"));
		month7.setCellValueFactory(new PropertyValueFactory<>("money7"));
		month8.setCellValueFactory(new PropertyValueFactory<>("money8"));
		
		rowTitle.setCellFactory(tc -> {
            TableCell<CashFlowData, String> cell = new TableCell<CashFlowData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    setText(empty ? null : item);
                    setTextFill(Color.WHITE);
                    setStyle("-fx-font-weight: bold;-fx-background-color: #5cb85c;-fx-border-color: #fff;-fx-border-width: 1px 1px 0 1px;");
                }
            };
            return cell;
        });
		
		// TABLEVIEWの高さをヘッダ含め19行に設定
		cashFlowDataTable.setFixedCellSize(25);
		cashFlowDataTable.prefHeightProperty().bind(cashFlowDataTable.fixedCellSizeProperty().multiply(19.0).add(3.0));
		cashFlowDataTable.minHeightProperty().bind(cashFlowDataTable.prefHeightProperty());
		cashFlowDataTable.maxHeightProperty().bind(cashFlowDataTable.prefHeightProperty());

		rowTitle.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.2));
		month1.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month2.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month3.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month4.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month5.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month6.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month7.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month8.prefWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));

		rowTitle.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.2));
		month1.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month2.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month3.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month4.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month5.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month6.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month7.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
		month8.maxWidthProperty().bind(cashFlowDataTable.widthProperty().subtract(10.0).multiply(0.1));
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
						b3Service.generatePdfReport(companyCode);
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
			Date startDate = b3Service.getKiStartDate();
			if (b3Service.compareYearMonthDate(currentDate, startDate) != 0) {
				currentDate = b3Service.getYearMonth(currentDate, -1);
				String startYearMonth = dateFormat.format(currentDate);
				String endYearMonth = dateFormat.format(b3Service.getYearMonth(currentDate, 7));
				String companyCode = CompanyUtil.getCompanyCode();

				// 検索
				List<CashFlowData> dataList = b3Service.selectData(companyCode, startYearMonth, endYearMonth, false);
				final ObservableList<CashFlowData> observableList = FXCollections.observableArrayList(dataList);
				cashFlowDataTable.getItems().clear();
				cashFlowDataTable.getItems().addAll(observableList);

				displayYearMonth(startYearMonth, endYearMonth);

				if (dataList != null && dataList.size() > 0) {
					printPdfBtn.setDisable(false);
				} else {
					printPdfBtn.setDisable(true);
				}
			}

			if (b3Service.compareYearMonthDate(currentDate, startDate) == 0) {
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
			currentDate = b3Service.getKiStartDate();
		}

		Date date = b3Service.getYearMonth(currentDate, 7);
		Date kiEndMonth = b3Service.getKiEndMonth();
		Date endDate = null;
		if (b3Service.compareYearMonthDate(date, kiEndMonth) < 0) {
			currentDate = b3Service.getYearMonth(currentDate, 1);
			String startYearMonth = dateFormat.format(currentDate);
			endDate = b3Service.getYearMonth(currentDate, 7);
			String endYearMonth = dateFormat.format(endDate);
			String companyCode = CompanyUtil.getCompanyCode();

			// 検索
			List<CashFlowData> dataList = b3Service.selectData(companyCode, startYearMonth, endYearMonth, false);
			final ObservableList<CashFlowData> observableList = FXCollections.observableArrayList(dataList);
			cashFlowDataTable.getItems().clear();
			cashFlowDataTable.getItems().addAll(observableList);

			displayYearMonth(startYearMonth, endYearMonth);

			if (dataList != null && dataList.size() > 0) {
				printPdfBtn.setDisable(false);
			} else {
				printPdfBtn.setDisable(true);
			}
		}

		if (b3Service.compareYearMonthDate(endDate, kiEndMonth) == 0) {
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
		Date kiEndMonth = b3Service.getKiEndMonth();
		currentDate = b3Service.getYearMonth(kiEndMonth, -7);

		String startYearMonth = dateFormat.format(currentDate);
		String endYearMonth = dateFormat.format(kiEndMonth);
		String companyCode = CompanyUtil.getCompanyCode();

		// 検索
		List<CashFlowData> dataList = b3Service.selectData(companyCode, startYearMonth, endYearMonth, false);
		final ObservableList<CashFlowData> observableList = FXCollections.observableArrayList(dataList);
		cashFlowDataTable.getItems().clear();
		cashFlowDataTable.getItems().addAll(observableList);

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
		Date startDate = b3Service.getKiStartDate();
		currentDate = startDate;

		String startYearMonth = dateFormat.format(startDate);
		String endYearMonth = dateFormat.format(b3Service.getYearMonth(currentDate, 7));
		String companyCode = CompanyUtil.getCompanyCode();

		// 検索
		List<CashFlowData> dataList = b3Service.selectData(companyCode, startYearMonth, endYearMonth, false);
		final ObservableList<CashFlowData> observableList = FXCollections.observableArrayList(dataList);
		cashFlowDataTable.getItems().clear();
		cashFlowDataTable.getItems().addAll(observableList);

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
		List<String> yearMonthList = b3Service.getDisplayYearMonthList(startYearMonth, endYearMonth);
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
			} else if (i == 5) {
				month6.setText(yearMonth);
			} else if (i == 6) {
				month7.setText(yearMonth);
			} else if (i == 7) {
				month8.setText(yearMonth);
			}
		}
	}
}
