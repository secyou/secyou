package com.busicomjp.sapp.common.item;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.ValidationException;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Component
public class CommonAlert {
	
	static final Logger log = LoggerFactory.getLogger(CommonAlert.class);
	
	/**
     * 確認要アラート
     * はい；true／いいえ：false
     */
	public boolean showConfirmationAlert(String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION, null, new ButtonType[] {
				new ButtonType("はい", ButtonData.OK_DONE), new ButtonType("いいえ", ButtonData.CANCEL_CLOSE) });
		alert.setTitle("確認");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("icon.png"));
		alert.getDialogPane().setHeaderText(null);
		alert.getDialogPane().setContentText(message);
		
		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && ButtonData.OK_DONE.equals(result.get().getButtonData());
	}
	
	public Alert getProgressIndicatorAlert() {
		Alert alert = new Alert(AlertType.NONE);
		ProgressIndicator pi = new ProgressIndicator();
		pi.setStyle("-fx-progress-color: #008000;");
		pi.setMinSize(150.0, 150.0);
		alert.getDialogPane().setStyle("-fx-background-color:rgba(0,0,0,0);");
        alert.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().setContent(pi);
        alert.show();
		return alert;
	}
	
	public void showExceptionAlert(Throwable e) {
		Throwable original = e;
		
		while (e.getCause() != null) {
			e = e.getCause();
		}
		
		if (e instanceof ValidationException) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("入力チェック");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image("icon.png"));
			alert.getDialogPane().setHeaderText(null);
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.getDialogPane().setContentText(e.getMessage());
			log.info(e.getMessage());
			alert.showAndWait();
		} else {
			// ValidationException以外の例外は全部システム例外とする
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("システムエラー");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image("icon.png"));
			alert.getDialogPane().setHeaderText(null);
			alert.getDialogPane().setContentText("システム管理者に連絡してください。");

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			original.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label(original.getMessage());

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			alert.getDialogPane().setExpandableContent(expContent);
			e.printStackTrace();
			log.error(exceptionText);
			alert.showAndWait();
		}
    }
}
