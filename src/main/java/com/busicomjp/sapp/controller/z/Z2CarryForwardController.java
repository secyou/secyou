package com.busicomjp.sapp.controller.z;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.busicomjp.sapp.common.constant.CommonConstants;
import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.CompanyUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.controller.e.E1RegistCarryForwardDialogController;
import com.busicomjp.sapp.controller.e.E1RegistCarryForwardForTorihikisakiDialogController;
import com.busicomjp.sapp.model.e.CarryForwardData;
import com.busicomjp.sapp.service.e.E1Service;
import com.busicomjp.sapp.service.e.E2Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Getter;

public class Z2CarryForwardController extends BaseController {
	
	@Autowired
	private E1Service e1Service;
	@Autowired
	private E2Service e2Service;
	@Autowired
	private E1RegistCarryForwardDialogController e1RegistController;
	@Autowired
	private E1RegistCarryForwardForTorihikisakiDialogController e1RegistController2;

	@Autowired
	private ConfigurableApplicationContext context;

	@FXML
	public GridPane e1_main;

	@FXML
	public TableView<CarryForwardData> carryforwardDataTable;
	@FXML
	public TableColumn<CarryForwardData,String> accountName;
	@FXML
	public TableColumn<CarryForwardData,String> kindName;
	@FXML
	public TableColumn<CarryForwardData,String> debitAmountMoney;
	@FXML
	public TableColumn<CarryForwardData,String> creditAmountMoney;

	@FXML
	public ComboBox<ComboItem> accountKind1Combo;
	@FXML
	public ComboBox<ComboItem> accountKind2Combo;
	@FXML
	public ComboBox<ComboItem> accountKind3Combo;
	@FXML
	public ComboBox<ComboItem> accountKind4Combo;

	@FXML
	public TextFlow summaryTextFlow;
	@FXML
	public TextFlow debitSummaryTextFlow;
	@FXML
	public TextFlow creditSummaryTextFlow;

	@FXML
	public Text debitSummary;
	@FXML
	public Text creditSummary;

	private String carryForward;
	@Getter
	List<ComboItem> accounList = new ArrayList<ComboItem>();

	protected void z2init() {
		accountName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
		kindName.setCellValueFactory(new PropertyValueFactory<>("kindName"));
		debitAmountMoney.setCellValueFactory(new PropertyValueFactory<>("debitAmountMoney"));
		creditAmountMoney.setCellValueFactory(new PropertyValueFactory<>("creditAmountMoney"));

		carryforwardDataTable.prefWidthProperty().bind(e1_main.widthProperty().subtract(240.0));

		accountName.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.4).subtract(1.0));
		kindName.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(3.0));
		debitAmountMoney.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(3.0));
		creditAmountMoney.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(3.0));

		accountName.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.4).subtract(1.0));
		kindName.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(3.0));
		debitAmountMoney.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(3.0));
		creditAmountMoney.maxWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(3.0));

		summaryTextFlow.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.5).subtract(4.0));
		debitSummaryTextFlow.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(3.0));
		creditSummaryTextFlow.prefWidthProperty().bind(carryforwardDataTable.widthProperty().subtract(10.0).multiply(0.25).subtract(3.0));

		accountKind1Combo.setConverter(new ComboItemConverter());
		accountKind2Combo.setConverter(new ComboItemConverter());
		accountKind3Combo.setConverter(new ComboItemConverter());
		accountKind4Combo.setConverter(new ComboItemConverter());

		debitAmountMoney.setCellFactory(tc -> {
			TableCell<CarryForwardData, String> cell = new TableCell<CarryForwardData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onShowRigestDialog();
				}
			});
			return cell;
		});

		creditAmountMoney.setCellFactory(tc -> {
			TableCell<CarryForwardData, String> cell = new TableCell<CarryForwardData, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};
			cell.setOnMouseClicked(e -> {
				if (!cell.isEmpty()) {
					onShowRigestDialog();
				}
			});
			return cell;
		});
	}

	public void init(String carry_Forward) {
		carryForward = carry_Forward;
		initCommon(carryForward) ;
	}
	
	public void initCommon(String carryForward) {
		// 画面項目初期化
		accountKind1Combo.getItems().clear();
		accountKind2Combo.getItems().clear();
		accountKind3Combo.getItems().clear();
		accountKind4Combo.getItems().clear();
		carryforwardDataTable.getItems().clear();

		// 分類取得
		getAccountKind1Data(carryForward);
		// 勘定項目取得
		getAccountCodeData(carryForward);
	}

	/**
	 * データ登録ダイアログ表示
	 */
	private void onShowRigestDialog() {
		CarryForwardData selectedData = carryforwardDataTable.getSelectionModel().getSelectedItem();
		if (selectedData == null) {
			throw new ValidationException("登録対象データを選択してください。");
		}

		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("前期繰越登録");
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("dialog.png"));
		// Xボタン
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.managedProperty().bind(closeButton.visibleProperty());
		closeButton.setVisible(false);

		boolean 売掛金 = false;
		boolean 買掛金 = false;
		boolean 未払金 = false;

		try {

			FXMLLoader lorder = new FXMLLoader(getClass().getResource("/fxml/e/e1_registCarryForwardDialog.fxml"));

			売掛金 = "1".equals(selectedData.getAccountKind1()) && "01".equals(selectedData.getAccountKind2())
					&& "003".equals(selectedData.getAccountKind3()) && "0002".equals(selectedData.getAccountKind4());
			買掛金 = "2".equals(selectedData.getAccountKind1()) && "01".equals(selectedData.getAccountKind2())
					&& "001".equals(selectedData.getAccountKind3()) && "0002".equals(selectedData.getAccountKind4());
			未払金 = "2".equals(selectedData.getAccountKind1()) && "01".equals(selectedData.getAccountKind2())
					&& "003".equals(selectedData.getAccountKind3()) && "0001".equals(selectedData.getAccountKind4());

			if (売掛金 || 買掛金 || 未払金) {
				lorder = new FXMLLoader(
						getClass().getResource("/fxml/e/e1_registCarryForwardForTorihikisakiDialog.fxml"));
			}

			lorder.setControllerFactory(context::getBean);
			GridPane gp = lorder.load();

			dialog.getDialogPane().setPadding(new Insets(10, 10, 10, 10));
			dialog.getDialogPane().setContent(gp);
			dialog.getDialogPane().requestFocus();
		} catch (IOException e) {
			throw new SystemException(e);
		}

		if (売掛金 || 買掛金 || 未払金) {
			e1RegistController2.init(selectedData);
		} else {
			e1RegistController.init(selectedData);
		}

		// 前期繰越登録のダイアログを開く
		dialog.showAndWait();
		// 登録内容再表示
		this.initCommon(carryForward);
	}

	/**
	 * 分類1Combo選択イベント
	 */
	@FXML
	protected void onSelectEnter() {
		if (accountKind1Combo == null || accountKind1Combo.getValue() == null) {
			return;
		}

		accountKind2Combo.getItems().clear();
		accountKind3Combo.getItems().clear();
		accountKind4Combo.getItems().clear();
		String selectCode = accountKind1Combo.getValue().getCode();
		getAccountKind2Data(selectCode);

		for (Iterator<CarryForwardData> iterator = carryforwardDataTable.getItems().iterator(); iterator.hasNext();) {
			CarryForwardData data = iterator.next();
			if (selectCode.equals(data.getAccountKind1())) {
				carryforwardDataTable.getSelectionModel().select(data);
				carryforwardDataTable.scrollTo(data);
				return;
			}
		}
	}

	/**
	 * 分類2Combo選択イベント
	 */
	@FXML
	protected void onSelect2Enter() {
		if (accountKind2Combo == null || accountKind2Combo.getValue() == null) {
			return;
		}

		String selectCode = accountKind1Combo.getValue().getCode();
		String selectCode2 = accountKind2Combo.getValue().getCode();
		accountKind3Combo.getItems().clear();
		accountKind4Combo.getItems().clear();
		getAccountKind3Data(selectCode, selectCode2);

		for (Iterator<CarryForwardData> iterator = carryforwardDataTable.getItems().iterator(); iterator.hasNext();) {
			CarryForwardData data = iterator.next();
			if (selectCode.equals(data.getAccountKind1()) && selectCode2.equals(data.getAccountKind2())) {
				carryforwardDataTable.getSelectionModel().select(data);
				carryforwardDataTable.scrollTo(data);
				return;
			}
		}
	}

	/**
	 * 分類3Combo選択イベント
	 */
	@FXML
	protected void onSelect3Enter() {
		if (accountKind3Combo == null || accountKind3Combo.getValue() == null) {
			return;
		}

		String selectCode = accountKind1Combo.getValue().getCode();
		String selectCode2 = accountKind2Combo.getValue().getCode();
		String selectCode3 = accountKind3Combo.getValue().getCode();
		accountKind4Combo.getItems().clear();

		getAccountKind4Data(selectCode, selectCode2, selectCode3);
		for (Iterator<CarryForwardData> iterator = carryforwardDataTable.getItems().iterator(); iterator.hasNext();) {
			CarryForwardData data = iterator.next();
			if (selectCode.equals(data.getAccountKind1()) && selectCode2.equals(data.getAccountKind2())
					&& selectCode3.equals(data.getAccountKind3())) {
				carryforwardDataTable.getSelectionModel().select(data);
				carryforwardDataTable.scrollTo(data);
				return;
			}
		}
	}

	/**
	 * 分類4Combo選択イベント
	 */
	@FXML
	protected void onSelect4Enter() {
		if (accountKind4Combo == null || accountKind4Combo.getValue() == null) {
			return;
		}

		String selectCode = accountKind1Combo.getValue().getCode();
		String selectCode2 = accountKind2Combo.getValue().getCode();
		String selectCode3 = accountKind3Combo.getValue().getCode();
		String selectCode4 = accountKind4Combo.getValue().getCode();

		for (Iterator<CarryForwardData> iterator = carryforwardDataTable.getItems().iterator(); iterator.hasNext();) {
			CarryForwardData data = iterator.next();
			if (selectCode.equals(data.getAccountKind1()) && selectCode2.equals(data.getAccountKind2())
					&& selectCode3.equals(data.getAccountKind3()) && selectCode4.equals(data.getAccountKind4())) {
				carryforwardDataTable.getSelectionModel().select(data);
				carryforwardDataTable.scrollTo(data);
				return;
			}
		}
	}

	/**
	 * 分類1取得
	 */
	protected void getAccountKind1Data(String carryForward) {
		accountKind1Combo.setConverter(new ComboItemConverter());
		accountKind1Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();
		List<ComboItem> dataList = new ArrayList<ComboItem>();
		if(CommonConstants.CARRY_FORWARD.FIRST_TERM.equals(carryForward)) {
			 dataList = e1Service.getAccountKind1DataList(CompanyUtil.getCompanyCode());
		}else {
			dataList = e2Service.getAccountKind1DataList(CompanyUtil.getCompanyCode());
		}

		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind1Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 分類2取得
	 */
	private void getAccountKind2Data(String accountKind1) {
		accountKind2Combo.setConverter(new ComboItemConverter());
		accountKind2Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind2DataList(CompanyUtil.getCompanyCode(), accountKind1);
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind2Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 分類3取得
	 */
	private void getAccountKind3Data(String accountKind1, String accountKind2) {
		accountKind3Combo.setConverter(new ComboItemConverter());
		accountKind3Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind3DataList(CompanyUtil.getCompanyCode(), accountKind1,
				accountKind2);
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind3Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 分類4取得
	 */
	private void getAccountKind4Data(String accountKind1, String accountKind2, String accountKind3) {
		accountKind4Combo.setConverter(new ComboItemConverter());
		accountKind4Combo.getItems().clear();
		accounList = new ArrayList<ComboItem>();

		List<ComboItem> dataList = e1Service.getAccountKind4DataList(CompanyUtil.getCompanyCode(), accountKind1,
				accountKind2, accountKind3);
		if (dataList == null) {
			accounList.add(new ComboItem("", ""));
		} else {
			for (ComboItem data : dataList) {
				accounList.add(new ComboItem(data.getCode(), data.getName()));
			}
		}
		accountKind4Combo.getItems().addAll(getAccounList());
	}

	/**
	 * 戡定項目取得
	 */
	protected void getAccountCodeData(String carryForward) {
		accounList = new ArrayList<ComboItem>();
		List<CarryForwardData> dataList  = new ArrayList<CarryForwardData>();
		if(CommonConstants.CARRY_FORWARD.FIRST_TERM.equals(carryForward)) {
			 dataList = e1Service.getAccountCodeDataList(CompanyUtil.getCompanyCode(),
						CompanyUtil.getAccountStartDay(), CompanyUtil.getAccountEndDay());
		}else {
			dataList = e2Service.getAccountCodeDataList(CompanyUtil.getCompanyCode(),
					CompanyUtil.getAccountStartDay(), CompanyUtil.getAccountEndDay());
		}

		if (dataList == null) {
			carryforwardDataTable.getItems().clear();
		} else {
			final ObservableList<CarryForwardData> observableList = FXCollections.observableArrayList(dataList);
			carryforwardDataTable.getItems().addAll(observableList);
		}

		long sumCreditAmountMoney = 0;
		long sumDebitAmountMoney = 0;

		for (Iterator<CarryForwardData> iterator = carryforwardDataTable.getItems().iterator(); iterator.hasNext();) {
			CarryForwardData data = iterator.next();
			sumCreditAmountMoney = sumCreditAmountMoney + Integer.parseInt(data.getCreditAmountMoney());
			sumDebitAmountMoney = sumDebitAmountMoney + Integer.parseInt(data.getDebitAmountMoney());
		}

		debitSummary.setText(String.valueOf(sumDebitAmountMoney));
		creditSummary.setText(String.valueOf(sumCreditAmountMoney));
	}
}
