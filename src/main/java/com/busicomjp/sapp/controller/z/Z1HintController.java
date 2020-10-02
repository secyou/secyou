package com.busicomjp.sapp.controller.z;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.busicomjp.sapp.common.exception.SystemException;
import com.busicomjp.sapp.common.util.InputCheck;
import com.busicomjp.sapp.common.util.StringUtil;
import com.busicomjp.sapp.controller.BaseController;
import com.busicomjp.sapp.model.z.HintResultData;
import com.busicomjp.sapp.service.z.Z1HintService;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public abstract class Z1HintController extends BaseController {
	
	protected boolean searchHint;
	
	protected static final String HINT_KBN_TORIHIKISAKI = "TORIHIKISAKI";
	protected static final String HINT_KBN_TEKIYO = "TEKEIYO";
	protected static final String HINT_KBN_DEBIT = "DEBIT";
	protected static final String HINT_KBN_CREDIT = "CREDIT";

	@Autowired
	protected Z1HintService z1HintService;

	/**
     * ヒント区分（どこ項目に対するヒント化を管理する）
     */
	@FXML
	protected Text hintKbn;

	@FXML
	protected Text hintCode1;
	@FXML
	protected Text hintName1;
	@FXML
	protected Text hintCode2;
	@FXML
	protected Text hintName2;
	@FXML
	protected Text hintCode3;
	@FXML
	protected Text hintName3;
	@FXML
	protected Text hintCode4;
	@FXML
	protected Text hintName4;
	@FXML
	protected Text hintCode5;
	@FXML
	protected Text hintName5;
	@FXML
	protected Text hintCode6;
	@FXML
	protected Text hintName6;
	@FXML
	protected Text hintCode7;
	@FXML
	protected Text hintName7;
	@FXML
	protected Text hintName8;
	@FXML
	protected Text hintCode8;
	@FXML
	protected Text hintName9;
	@FXML
	protected Text hintCode9;
	@FXML
	protected Text hintCode10;
	@FXML
	protected Text hintName10;
	@FXML
	protected Text hintCode11;
	@FXML
	protected Text hintName11;
	@FXML
	protected Text hintCode12;
	@FXML
	protected Text hintName12;
	@FXML
	protected Text hintCode13;
	@FXML
	protected Text hintName13;
	@FXML
	protected Text hintCode14;
	@FXML
	protected Text hintName14;
	@FXML
	protected Text hintCode15;
	@FXML
	protected Text hintName15;
	@FXML
	protected Text hintCode16;
	@FXML
	protected Text hintName16;
	@FXML
	protected Text hintCode17;
	@FXML
	protected Text hintName17;
	@FXML
	protected Text hintName18;
	@FXML
	protected Text hintCode18;
	@FXML
	protected Text hintName19;
	@FXML
	protected Text hintCode19;
	@FXML
	protected Text hintName20;
	@FXML
	protected Text hintCode20;
	@FXML
	protected Text hintName21;
	@FXML
	protected Text hintCode21;
	
	// 取引先
	@FXML
	protected TextField searchSuppliersName;
	@FXML
	protected Text searchSuppliersCode;
	
	// 摘要
	@FXML
	protected TextField searchTekiyoName;
	@FXML
	protected Text searchTekiyoCode;
	
	// 借方勘定科目
	@FXML
	protected TextField searchDebitName;
	@FXML
	protected Text searchDebitCode;
	
	// 貸方勘定科目
	@FXML
	protected TextField searchCreditName;
	@FXML
	protected Text searchCreditCode;

	abstract public void hintClickAction(String hintKbn, String hintCode, String hintName);
	
	protected void z1init(boolean accountInit) {
		searchSuppliersName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
					String newPropertyValue) {
				if (StringUtils.isNotEmpty(newPropertyValue)) {
					searchSuppliersNameHint(newPropertyValue);
				}
			}
		});
		
		searchTekiyoName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
					String newPropertyValue) {
				if (StringUtils.isNotEmpty(newPropertyValue)) {
					searchTekiyoNameHint(newPropertyValue);
				}
			}
		});
		
		if (accountInit) {
			searchDebitName.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
						String newPropertyValue) {
					if (StringUtils.isNotEmpty(newPropertyValue)) {
						searchDebitAccountNameHint(newPropertyValue);
					}
				}
			});

			searchCreditName.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> arg0, String oldPropertyValue,
						String newPropertyValue) {
					if (StringUtils.isNotEmpty(newPropertyValue)) {
						searchCreditAccountNameHint(newPropertyValue);
					}
				}
			});
		}
	}
	
	/**
     * ヒントクリックイベント
     */
	@FXML
	protected void onHintClick(Event eve) {
		//
		Text hintNameText = (Text) eve.getSource();
		String id = hintNameText.getId();
		String index = id.substring("hintName".length());
		try {
			Field code = getClass().getSuperclass().getDeclaredField("hintCode" + index);
			String hintCode = ((Text) code.get(this)).getText();
			String hintName = hintNameText.getText();
			hintName = hintName.substring(hintName.indexOf("]") + 1);
			// 後続アクションを呼び出す
			hintClickAction(hintKbn.getText(), hintCode, hintName);
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

	/**
     * ヒントデータを全部クリア
     */
	protected void clearHintData() throws Exception {
		hintKbn.setText("");
		for (int i = 1; i <= 21; i++) {
			Field code = getClass().getSuperclass().getDeclaredField("hintCode" + i);
			Field name = getClass().getSuperclass().getDeclaredField("hintName" + i);
			code.setAccessible(true);
			name.setAccessible(true);
			((Text) code.get(this)).setText("");
			((Text) name.get(this)).setText("");
		}
	}

	/**
     * ヒントデータを画面に表示
     */
	protected void setHintData(String hintKbnValue, List<HintResultData> hintList) throws Exception {
		hintKbn.setText(hintKbnValue);
		if (!hintList.isEmpty()) {
			int i = 1;
			for (HintResultData data : hintList) {
				Field code = getClass().getSuperclass().getDeclaredField("hintCode" + i);
				Field name = getClass().getSuperclass().getDeclaredField("hintName" + i);
				code.setAccessible(true);
				name.setAccessible(true);
				((Text) code.get(this)).setText(data.getHintResultCode());
				((Text) name.get(this)).setText("[" + i + "]" + data.getHintResultName());
				i++;
			}
		}
	}

	protected boolean inputNumberAction(String hintNumber, String inputHintKbn) throws Exception {
		if (!hintKbn.getText().equals(inputHintKbn)) {
			// ヒント区分が異なる場合
			return false;
		}
		int number = Integer.valueOf(hintNumber);
		if (number >=1 && number <=21) {
			Field code = getClass().getSuperclass().getDeclaredField("hintCode" + number);
			Field name = getClass().getSuperclass().getDeclaredField("hintName" + number);
			String hintCode = ((Text) code.get(this)).getText();
			String hintName = ((Text) name.get(this)).getText();
			hintName = hintName.substring(hintName.indexOf("]") + 1);
			if (StringUtils.isNotEmpty(hintCode)) {
				hintClickAction(hintKbn.getText(), hintCode, hintName);
				return true;
			}
		}
		return false;
	}
	
	/**
     * 取引先欄Enterキーイベント
     */
	@FXML
	protected void onSuppliersNameEnter(ActionEvent event) {
		String name = searchSuppliersName.getText();
		if (StringUtils.isNotEmpty(name)) {
			try {
				String hintNo = StringUtil.replaceFull2HalfNumber(name.trim());
				if (InputCheck.isNumeric(hintNo)) {
					// 数値を入力した場合
					searchSuppliersCode.setText("");
					searchSuppliersName.setText("");
					searchHint = false;
					inputNumberAction(hintNo, HINT_KBN_TORIHIKISAKI);
				}
			} catch (Exception e) {
				throw new SystemException(e);
			} finally {
				searchHint = true;
			}
		}
	}
	
	/**
     * 取引先ヒント検索
     */
	protected void searchSuppliersNameHint(String value) {
		if (StringUtils.isNotEmpty(value) && searchHint) {
			String hintNo = StringUtil.replaceFull2HalfNumber(value.trim());
			if (InputCheck.isNumeric(hintNo)) {
				return;
			}
			List<HintResultData> hintList = z1HintService.getToriHikiSakiHintDataList(value);
			if (!hintList.isEmpty()) {
				// 取引先入力情報をクリア
				Platform.runLater(() -> {
					searchSuppliersCode.setText("");
					searchSuppliersName.setText("");
				});
			}
			Platform.runLater(() -> {
				try {
					clearHintData();
					setHintData(HINT_KBN_TORIHIKISAKI, hintList);
				} catch (Exception e) {
					throw new SystemException(e);
				}
			});
		}
	}

	/**
     * 摘要名称欄Enterキーイベント
     */
	@FXML
	protected void onTekiyoNameEnter(ActionEvent event) {
		String name = searchTekiyoName.getText();
		if (StringUtils.isNotEmpty(name)) {
			try {
				String hintNo = StringUtil.replaceFull2HalfNumber(name.trim());
				if (InputCheck.isNumeric(hintNo)) {
					// 数値を入力した場合
					searchTekiyoCode.setText("");
					searchTekiyoName.setText("");
					searchHint = false;
					inputNumberAction(hintNo, HINT_KBN_TEKIYO);
				}
			} catch (Exception e) {
				throw new SystemException(e);
			} finally {
				searchHint = true;
			}
		}
	}
	
	/**
     * 摘要ヒント検索
     */
	protected void searchTekiyoNameHint(String value) {
		if (StringUtils.isNotEmpty(value) && searchHint) {
			String hintNo = StringUtil.replaceFull2HalfNumber(value.trim());
			if (InputCheck.isNumeric(hintNo)) {
				// 数値を入力した場合
				return;
			}
			List<HintResultData> hintList = z1HintService.getTekiyoHintDataList(value);
			if (!hintList.isEmpty()) {
				// 摘要入力情報をクリア
				Platform.runLater(() -> {
					searchTekiyoCode.setText("");
					searchTekiyoName.setText("");
				});
			}
			Platform.runLater(() -> {
				try {
					clearHintData();
					setHintData(HINT_KBN_TEKIYO, hintList);
				} catch (Exception e) {
					throw new SystemException(e);
				}
			});
		}
	}

	/**
     * 借方勘定科目名称欄Enterキーイベント
     */
	@FXML
	protected void onDebitAccountNameEnter(ActionEvent event) {
		String name = searchDebitName.getText();
		if (StringUtils.isNotEmpty(name)) {
			try {
				String hintNo = StringUtil.replaceFull2HalfNumber(name.trim());
				if (InputCheck.isNumeric(hintNo)) {
					// 数値を入力した場合
					searchDebitCode.setText("");
					searchDebitName.setText("");
					searchHint = false;
					inputNumberAction(hintNo, HINT_KBN_DEBIT);
				}
			} catch (Exception e) {
				throw new SystemException(e);
			} finally {
				searchHint = true;
			}
		}
	}
	
	/**
     * 借方勘定科目ヒント検索
     */
	protected void searchDebitAccountNameHint(String value) {
		if (StringUtils.isNotEmpty(value) && searchHint) {
			String hintNo = StringUtil.replaceFull2HalfNumber(value.trim());
			if (InputCheck.isNumeric(hintNo)) {
				// 数値を入力した場合
				return;
			}
			List<HintResultData> hintList = z1HintService.getAccountHintDataList(value);
			if (!hintList.isEmpty()) {
				// 借方勘定科目入力情報をクリア
				Platform.runLater(() -> {
					searchDebitCode.setText("");
					searchDebitName.setText("");
				});
			}
			Platform.runLater(() -> {
				try {
					clearHintData();
					setHintData(HINT_KBN_DEBIT, hintList);
				} catch (Exception e) {
					throw new SystemException(e);
				}
			});
		}
	}

	/**
     * 貸方勘定科目名称欄Enterキーイベント
     */
	@FXML
	protected void onCreditAccountNameEnter(ActionEvent event) {
		String name = searchCreditName.getText();
		if (StringUtils.isNotEmpty(name)) {
			try {
				String hintNo = StringUtil.replaceFull2HalfNumber(name.trim());
				if (InputCheck.isNumeric(hintNo)) {
					// 数値を入力した場合
					searchCreditCode.setText("");
					searchCreditName.setText("");
					searchHint = false;
					inputNumberAction(hintNo, HINT_KBN_CREDIT);
				}
			} catch (Exception e) {
				throw new SystemException(e);
			} finally {
				searchHint = true;
			}
		}
	}
	
	/**
     * 貸方勘定科目ヒント検索
     */
	protected void searchCreditAccountNameHint(String value) {
		if (StringUtils.isNotEmpty(value) && searchHint) {
			String hintNo = StringUtil.replaceFull2HalfNumber(value.trim());
			if (InputCheck.isNumeric(hintNo)) {
				// 数値を入力した場合
				return;
			}
			List<HintResultData> hintList = z1HintService.getAccountHintDataList(value);
			if (!hintList.isEmpty()) {
				// 貸方勘定科目入力情報をクリア
				Platform.runLater(() -> {
					searchCreditCode.setText("");
					searchCreditName.setText("");
				});
			}
			Platform.runLater(() -> {
				try {
					clearHintData();
					setHintData(HINT_KBN_CREDIT, hintList);
				} catch (Exception e) {
					throw new SystemException(e);
				}
			});
		}
	}

}
