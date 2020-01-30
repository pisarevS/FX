package com.sergey.pisarev.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../resource/sample.fxml"));
        primaryStage.setTitle("CNC modeling");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1000);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
