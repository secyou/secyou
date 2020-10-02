package com.busicomjp.sapp.common.item;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.common.exception.SystemException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

@Component
public class CommonDialog {
	
	@Autowired
	private ConfigurableApplicationContext context;

	public Dialog<Pair<String, String>> getDialog(String title, String fxmlPath, boolean closeBotton) {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("icon.png"));
		if (closeBotton) {
			// Xボタン
			dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
			closeButton.managedProperty().bind(closeButton.visibleProperty());
			closeButton.setVisible(false);
		}

		try {
			FXMLLoader lorder = new FXMLLoader(getClass().getResource(fxmlPath));
			lorder.setControllerFactory(context::getBean);
			GridPane gp = lorder.load();

			dialog.getDialogPane().setPadding(new Insets(10, 10, 10, 10));
			dialog.getDialogPane().setContent(gp);
			dialog.getDialogPane().requestFocus();
		} catch (IOException e) {
			throw new SystemException(e);
		}
		
		return dialog;
	}
	
	public void showDialog(String title, String fxmlPath, boolean closeBotton) {
		Dialog<Pair<String, String>> dialog = getDialog(title, fxmlPath, closeBotton);
		dialog.showAndWait();
	}
	
	public Dialog<Pair<String, String>> getScrollDialog(String title, String fxmlPath, boolean closeBotton) {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setResizable(true);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("icon.png"));
		if (closeBotton) {
			// Xボタン
			dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
			closeButton.managedProperty().bind(closeButton.visibleProperty());
			closeButton.setVisible(false);
		}

		try {
			FXMLLoader lorder = new FXMLLoader(getClass().getResource(fxmlPath));
			lorder.setControllerFactory(context::getBean);
			GridPane gp = lorder.load();
			gp.setCache(false);
			ScrollPane sp = new ScrollPane();
			sp.setPannable(true);
			sp.setCache(false);
			
			sp.setPadding(new Insets(0.01, 0.01, 0.01, 0.01));
			sp.setPrefHeight(650);
			sp.setFitToWidth(true);
			sp.setContent(gp);
			sp.setStyle("-fx-border-color:#008000;-fx-border-width:1px;");

			dialog.getDialogPane().setPadding(new Insets(10, 10, 10, 10));
			dialog.getDialogPane().setCache(false);
			dialog.getDialogPane().setContent(sp);
			dialog.getDialogPane().requestFocus();
		} catch (IOException e) {
			throw new SystemException(e);
		}
		return dialog;
	}
	
	public void showScrollDialog(String title, String fxmlPath, boolean closeBotton) {
		Dialog<Pair<String, String>> dialog = getScrollDialog(title, fxmlPath, closeBotton);
		dialog.showAndWait();
	}
}
