package com.busicomjp.sapp.controller.a;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.converter.ComboItemConverter;
import com.busicomjp.sapp.common.item.ComboItem;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.controller.FeatureGroupController;
import com.busicomjp.sapp.dto.a.JournalEntryDto;
import com.busicomjp.sapp.model.a.JournalEntryData;
import com.busicomjp.sapp.service.a.A2Service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

@Component
public class A2UpdateDialogController extends BaseController implements Initializable {
	
	Logger logger = LoggerFactory.getLogger(A2UpdateDialogController.class);
	private JournalEntryDto searchCondition = null;
	private JournalEntryData journalEntry = null;
	
	@Autowired
	private FeatureGroupController featureGroupController;
	
	// 取引先
	@FXML
	private Label suppliersNameLbl;
	// 摘要
	@FXML
	private Label tekiyoNameLbl;
	// 借方勘定科目
	@FXML
	private Label debitNameLbl;
	// 貸方勘定科目
	@FXML
	private Label creditNameLbl;
	// 変更前の取引発生日
	@FXML
	private Text accrualDateTxt;
	// 変更前の金額(税込)
	@FXML
	private Text amountMoneyTxt;
	// 変更前の税額
	@FXML
	private Text taxTxt;
	// 変更前の税率
	@FXML
	private Text taxRateTxt;
	// 変更前の入金・支払予定日
	@FXML
	private Text depPayDateTxt;
	@FXML
	private TextField newAccrualDate;
	@FXML
	private TextField newAmountMoney;
	@FXML
	private TextField newTax;
	@FXML
	private ComboBox<ComboItem> newTaxRate;
	@FXML
	private TextField newDepPayDate;

	@Autowired
	private A2ShowController a2ShowController;
	@Autowired
	private A2Service a2Service;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		newTaxRate.setConverter(new ComboItemConverter());
		newTaxRate.getItems().clear();
		newTaxRate.getItems().addAll(commonItems.getTaxItemList());
	}

	@FXML
	private void onUpdateData(Event event) {
		if (commonAlert.showConfirmationAlert("変更登録処理を実行しますか？")) {
	        // 進捗インジケータ
	        Alert alert = commonAlert.getProgressIndicatorAlert();
	        alert.show();

	        Service<String> service = new Service<String>() {
	            @Override
	            protected Task<String> createTask() {
	                Task<String> task = new Task<String>() {
	                    @Override
	                    protected String call() throws Exception {
	                    	JournalEntryData data = getData();
	            			a2Service.registRedBlackJournal(journalEntry, data);
	            			List<JournalEntryData> searchResultList = a2Service.getJournalList(searchCondition);
	            			a2ShowController.setSearchResult(searchCondition, searchResultList);
	                        return "OK";
	                    }
	                };
	                commonTask.setTaskHandling(task, alert);
	                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	        	        @Override
	        	        public void handle(WorkerStateEvent event) {
	        	        	featureGroupController.mainVBox.setDisable(false);
	        	        	alert.setResult(ButtonType.CLOSE);
	        	        	// ダイアログを閉じる
	        				newTaxRate.getScene().getWindow().hide();
	        	        }
	        	    });
	                return task;
	            }
	        };
	        service.start();
		}
	}

	public void init(JournalEntryDto searchCondition, JournalEntryData selectData) {
		this.searchCondition = searchCondition;
		journalEntry = a2Service.getJournal(selectData);
		// 取引先
		suppliersNameLbl.setText(journalEntry.getSuppliersName());
		// 摘要
		tekiyoNameLbl.setText(journalEntry.getTekiyoName());
		// 借方勘定科目
		debitNameLbl.setText(journalEntry.getDebitName());
		// 貸方勘定科目
		creditNameLbl.setText(journalEntry.getCreditName());
		
		// 変更前の取引発生日
		accrualDateTxt.setText(journalEntry.getAccrualDate());
		// 変更前の金額(税込)
		amountMoneyTxt.setText(journalEntry.getAmountMoney());
		// 変更前の税額
		taxTxt.setText(journalEntry.getTax());
		// 変更前の税率
		taxRateTxt.setText(journalEntry.getTaxRate());
		// 変更前の入金・支払予定日
		depPayDateTxt.setText(journalEntry.getDepPayDate());
		
		// 取引発生日
		newAccrualDate.setText(StringUtil.replaceDateSlashFormat(journalEntry.getAccrualDate()));
		// 金額(税込)
		newAmountMoney.setText(StringUtil.replaceCommaFormat(journalEntry.getAmountMoney()));
		// 税額
		newTax.setText(StringUtil.replaceCommaFormat(journalEntry.getTax()));
		// 税率
		ComboItem comboItem = new ComboItem(journalEntry.getTaxCode(), journalEntry.getTaxRate());
		newTaxRate.setValue(comboItem);
		// 入金・支払予定日
		newDepPayDate.setText(StringUtil.replaceDateSlashFormat(journalEntry.getDepPayDate()));
	}
	
	private JournalEntryData getData() {
		JournalEntryData data = new JournalEntryData();
		
		// 取引発生日
		String _accrualDate = StringUtil.replaceFull2HalfNumber(newAccrualDate.getText());
		if (!InputCheck.isNullOrBlank(_accrualDate)) {
			_accrualDate = _accrualDate.replaceAll("/", "");
			data.setAccrualDate(_accrualDate);
		}
		// 金額
		String _amountMoney = StringUtil.replaceFull2HalfNumber(newAmountMoney.getText());
		if (!InputCheck.isNullOrBlank(_amountMoney)) {
			data.setAmountMoney(_amountMoney);
		}
		// 税率
		ComboItem comboItem = newTaxRate.getValue();
		data.setTaxCode(comboItem.getCode());

		// 入金・支払予定日
		String _depPayDate = StringUtil.replaceFull2HalfNumber(newDepPayDate.getText());
		if (!InputCheck.isNullOrBlank(_depPayDate)) {
			_depPayDate = _depPayDate.replaceAll("/", "");
			data.setDepPayDate(_depPayDate);
		}

		return data;
	}
}
