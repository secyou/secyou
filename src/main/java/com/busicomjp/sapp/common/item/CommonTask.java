package com.busicomjp.sapp.common.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.busicomjp.sapp.controller.FeatureGroupController;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

@Component
public class CommonTask {
	
	@Autowired
	private FeatureGroupController featureGroupController;
	
	@Autowired
	private CommonAlert commonAlert;
	
	public void setTaskHandling(Task<String> task, Alert alert) {
		task.setOnRunning(new EventHandler<WorkerStateEvent>() {
	        @Override
	        public void handle(WorkerStateEvent event) {
	        	featureGroupController.mainVBox.setDisable(true);
	        }
	    });
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	        @Override
	        public void handle(WorkerStateEvent event) {
	        	featureGroupController.mainVBox.setDisable(false);
	        	alert.setResult(ButtonType.CLOSE);
	        }
	    });
		task.setOnFailed(new EventHandler<WorkerStateEvent>() {
	        @Override
	        public void handle(WorkerStateEvent event) {
	        	featureGroupController.mainVBox.setDisable(false);
	        	alert.setResult(ButtonType.CLOSE);
	        	Throwable e = task.getException();
	        	commonAlert.showExceptionAlert(e);
	        }
	    });
	}
}
