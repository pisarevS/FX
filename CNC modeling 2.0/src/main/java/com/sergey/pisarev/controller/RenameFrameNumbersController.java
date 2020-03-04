package com.sergey.pisarev.controller;

import com.sergey.pisarev.model.RenameFrameNumbers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenameFrameNumbersController {

    public static Stage STAGE;

    @FXML
    TextField textFieldFirstNumber=new TextField();

    @FXML
    TextField textFieldStep=new TextField();

    @FXML
    public void buttonOnClickOk(ActionEvent actionEvent) {
        if(!textFieldFirstNumber.getText().isEmpty()&&!textFieldStep.getText().isEmpty()&&!MainController.mainController.getTextCodeArea().isEmpty()){
            MainController.mainController.setTextCodeArea(RenameFrameNumbers.rename(MainController.mainController.getTextCodeArea(),Integer.parseInt(textFieldFirstNumber.getText()),Integer.parseInt(textFieldStep.getText())));
            STAGE.close();
        }
    }

    @FXML
    public void buttonOnClickCancel(ActionEvent actionEvent) {
        STAGE.close();
    }
}
