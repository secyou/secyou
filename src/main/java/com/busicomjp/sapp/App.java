package com.busicomjp.sapp;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.busicomjp.sapp.common.exception.ValidationException;
import com.busicomjp.sapp.common.item.CommonItems;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.Getter;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.busicomjp.sapp.repository")
public class App extends Application {
	
	static final Logger log = LoggerFactory.getLogger(App.class);

	private static Scene mainMenu;
	@Getter
	private static Scene feature;
	@Getter
	private static Scene companyManage;
	@Getter
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
    public void init() throws Exception {
		context = SpringApplication.run(App.class);
        FXMLLoader mainMenuLoader = new FXMLLoader(getClass().getResource("/fxml/mainMenu.fxml"));
        mainMenuLoader.setControllerFactory(context::getBean);
        mainMenu = new Scene(mainMenuLoader.load());
        // 画面部品の初期化
        context.getBean(CommonItems.class).initItems();

        FXMLLoader featureLoader = new FXMLLoader(getClass().getResource("/fxml/featureGroup.fxml"));
        featureLoader.setControllerFactory(context::getBean);
        feature = new Scene(featureLoader.load());
        
        FXMLLoader companyManageLoader = new FXMLLoader(getClass().getResource("/fxml/companyManage.fxml"));
        companyManageLoader.setControllerFactory(context::getBean);
        companyManage = new Scene(companyManageLoader.load());
    }

	@Override
	public void start(Stage stage) throws Exception {
		// 共通例外ハンドリング
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
        Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);
        // メインステージ
		stage.setScene(mainMenu);
	    stage.setMaximized(true);
		stage.setTitle("会計アプリ");
		stage.getIcons().add(new Image("icon.png"));
		stage.show();
	}

	@Override
    public void stop() throws Exception {
        context.close();
    }

	private void showErrorDialog(Thread t, Throwable e) {
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
			alert.getDialogPane().setContentText("システム管理者に連絡してください");

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
	
	public static void clearKeyEvent() {
		if (feature == null || feature.getAccelerators() == null) {
			return;
		}
		feature.getAccelerators().clear();
	}
	
	public static void putKeyEvent(KeyCombination key, Runnable value) {
		if (feature == null || feature.getAccelerators() == null) {
			return;
		}
		feature.getAccelerators().put(key, value);
	}

}
