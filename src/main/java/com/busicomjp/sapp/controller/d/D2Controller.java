package com.busicomjp.sapp.controller.d;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.App;
import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.util.TorihikisakiUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.d.TorihikisakiData;
import com.busicomjp.sapp.service.d.D2Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

@Component
public class D2Controller extends BaseController implements Initializable  {

	private Logger logger = LoggerFactory.getLogger(D2Controller.class);

	@Autowired
	private D2Service z2Service;
	
	@FXML
	private TableView<TorihikisakiData> torihikisakiDataTable;
	@FXML
	private TextField searchTorihikisakiName;
	@FXML
	private TableColumn<TorihikisakiData,String> torihikisakiName;
	@FXML
	private TableColumn<TorihikisakiData,String> torihikisakiNameKana;
	@FXML
	private TableColumn<TorihikisakiData,String> supplierKbn;
	@FXML
	private TableColumn<TorihikisakiData,String> customerKbn;
	@FXML
	private TableColumn<TorihikisakiData,String> unpaidKbn;
	@FXML
	private TableColumn<TorihikisakiData,String> delFlg;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		torihikisakiName.setCellValueFactory(new PropertyValueFactory<>("torihikisakiName"));
		torihikisakiNameKana.setCellValueFactory(new PropertyValueFactory<>("torihikisakiNameKana"));
		supplierKbn.setCellValueFactory(new PropertyValueFactory<>("supplierKbn"));
		customerKbn.setCellValueFactory(new PropertyValueFactory<>("customerKbn"));
		unpaidKbn.setCellValueFactory(new PropertyValueFactory<>("unpaidKbn"));
		//delFlg.setCellValueFactory(new PropertyValueFactory<>("delFlg"));
		
		torihikisakiName.prefWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.35).subtract(2.0));
		torihikisakiNameKana.prefWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.35).subtract(2.0));
		supplierKbn.prefWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		customerKbn.prefWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		unpaidKbn.prefWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		//delFlg.prefWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		
		torihikisakiName.maxWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.35).subtract(2.0));
		torihikisakiNameKana.maxWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.35).subtract(2.0));
		supplierKbn.maxWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		customerKbn.maxWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		unpaidKbn.maxWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.1).subtract(2.0));
		//delFlg.maxWidthProperty().bind(torihikisakiDataTable.widthProperty().subtract(10.0).multiply(0.08).subtract(2.0));
		
		searchTorihikisakiName.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	onSearchData();
	            }
	        }
	    });
	}

	public void init() {
		bindingKeyPress();
		//会社コード変更する場合、変更前会社コードで検索された内容をクリア
		searchTorihikisakiName.clear();
		this.onSearchData();
	}
	
	/**
     * 取引先データ検索
     */
	@FXML
	public void onSearchData() {
		
		logger.info("取引先名称検索 開始");

		// 検索実行
		List<TorihikisakiData> searchResultList = z2Service.getTorihikiDataList(searchTorihikisakiName.getText().trim());

		final ObservableList<TorihikisakiData> observableList = FXCollections.observableArrayList(searchResultList);
		torihikisakiDataTable.getItems().clear();
		torihikisakiDataTable.getItems().addAll(observableList);
		
		logger.info("取引先名称検索 終了");
		
	}

	/**
     * 取引先データ新規登録
     */
	@FXML
	private void onRegistData() {
		commonDialog.showDialog("取引先登録", "/fxml/d/d2_registTorihikisakiDialog.fxml", true);
	}
	
	/**
     * 取引先情報変更ダイアログを表示
     */
	@FXML
	private void onChangeData() {
		TorihikisakiData selectData = torihikisakiDataTable.getSelectionModel().getSelectedItem();
		if (selectData == null) {
			// データが選択されない場合
			throw new ValidationException("変更対象を選択してください。");
		} 

        TorihikisakiUtil.setTorihikisakiUtil(selectData);
		commonDialog.showDialog("取引先情報更新", "/fxml/d/d2_updateTorihikisakiDialog.fxml", true);
	}
	
	private void bindingKeyPress() {
		App.clearKeyEvent();
		KeyCombination f1 = new KeyCodeCombination(KeyCode.F1);
		KeyCombination f2 = new KeyCodeCombination(KeyCode.F2);
		KeyCombination f3 = new KeyCodeCombination(KeyCode.F3);
		App.putKeyEvent(f1, new Runnable() {
			@Override
			public void run() {
				onSearchData();
			}
		});
		App.putKeyEvent(f2, new Runnable() {
			@Override
			public void run() {
				onRegistData();
			}
		});
		App.putKeyEvent(f3, new Runnable() {
			@Override
			public void run() {
				onChangeData();
			}
		});
	}

}
