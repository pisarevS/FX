package com.sergey.pisarev.controller;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class Main extends Application {

    public static String title="CNC modeling 2.0";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("sample.fxml")));
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1000);
        primaryStage.setOnCloseRequest(Event::consume);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
