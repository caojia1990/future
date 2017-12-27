package com.caojia.future.trader.javafx.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BarChartMain extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
	    FXMLLoader load = new FXMLLoader(getClass().getResource("/com/caojia/future/trader/javafx/view/BarChart.fxml"));
	    Parent root = load.load();
	    
        primaryStage.setTitle("Tick");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
