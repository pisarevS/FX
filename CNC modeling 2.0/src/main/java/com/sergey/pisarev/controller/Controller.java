package com.sergey.pisarev.controller;

import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.presenter.Presenter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;
import com.sergey.pisarev.model.StyleTextField;

public class Controller implements IController {

    private CodeArea codeAreaProgram=new CodeArea();
    private CodeArea codeAreaParameter=new CodeArea();
    private PresenterImpl presenter;
    private int countClick=2;

    @FXML
    StackPane paneCanvas =new StackPane();

    @FXML
    AnchorPane anchorPaneProgram=new AnchorPane();

    @FXML
    AnchorPane anchorPaneParameter=new AnchorPane();

    @FXML
    Button buttonStart=new Button();

    @FXML
    Button buttonCycleStart=new Button();

    @FXML
    Button buttonSingleBlock=new Button();

    @FXML
    Button buttonReset=new Button();

    @FXML
    public void initialize(){
        ResizableCanvas visualizerCanvas = new ResizableCanvas();
        paneCanvas.getChildren().add(visualizerCanvas);

        presenter=new Presenter(this,visualizerCanvas);
        StyleTextField styleTextField =new StyleTextField();
        styleTextField.setStyle(codeAreaProgram);
        codeAreaProgram.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaProgram));
        StackPane stackPaneProgram = new StackPane(new VirtualizedScrollPane<>(codeAreaProgram));
        AnchorPane.setTopAnchor(stackPaneProgram,0.0);
        AnchorPane.setBottomAnchor(stackPaneProgram,0.0);
        AnchorPane.setLeftAnchor(stackPaneProgram,0.0);
        AnchorPane.setRightAnchor(stackPaneProgram,0.0);
        anchorPaneProgram.getChildren().add(stackPaneProgram);

        styleTextField.setStyle(codeAreaParameter);
        codeAreaParameter.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaParameter));
        StackPane stackPaneParameter = new StackPane(new VirtualizedScrollPane<>(codeAreaParameter));
        AnchorPane.setTopAnchor(stackPaneParameter,0.0);
        AnchorPane.setBottomAnchor(stackPaneParameter,0.0);
        AnchorPane.setLeftAnchor(stackPaneParameter,0.0);
        AnchorPane.setRightAnchor(stackPaneParameter,0.0);
        anchorPaneParameter.getChildren().add(stackPaneParameter);

        buttonStart.setTextFill(Color.BLACK);
        buttonCycleStart.setTextFill(Color.BLACK);
        buttonSingleBlock.setTextFill(Color.BLACK);
        buttonReset.setTextFill(Color.BLACK);
    }

    @FXML
    public void handleDragOverProgram(DragEvent event){
        if(event.getDragboard().hasFiles())
        event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    public void handleDragOverParameter(DragEvent event){
        if(event.getDragboard().hasFiles())
            event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    public void handleDragProgram(DragEvent event){
        presenter.openDragProgram(event);
    }

    @FXML
    public void handleDragParameter(DragEvent event){
        presenter.openDragParameter(event);
    }

    @FXML
    public void onMouseClickedProgram(Event event){
        presenter.onMouseClickedProgram(codeAreaProgram.offsetToPosition(codeAreaProgram.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor());
    }

    @FXML
    public void onStart(ActionEvent actionEvent) {
        buttonReset.setDisable(false);
        buttonStart.setDisable(true);
        buttonCycleStart.setDisable(true);
        buttonSingleBlock.setDisable(true);
        presenter.onStart(codeAreaProgram.getText(),codeAreaParameter.getText());
    }

    @FXML
    public void onCycleStart(ActionEvent actionEvent) {
        buttonReset.setDisable(false);
        buttonStart.setDisable(true);
        buttonCycleStart.setDisable(true);
        buttonSingleBlock.setDisable(false);
        if(buttonSingleBlock.getTextFill()==Color.GREEN) {
            buttonCycleStart.setDisable(false);
        }
        presenter.onCycleStart(codeAreaProgram.getText(),codeAreaParameter.getText());
    }

    @FXML
    public void onSingleBlock(ActionEvent actionEvent) {
        countClick++;
        if(countClick%2==0){
            buttonSingleBlock.setTextFill(Color.BLACK);
            buttonCycleStart.setDisable(true);
            presenter.onSingleBlock(false);
        }else {
            buttonSingleBlock.setTextFill(Color.GREEN);
            buttonCycleStart.setDisable(false);
            buttonStart.setDisable(true);
            presenter.onSingleBlock(true);
        }
    }

    @FXML
    public void onReset(ActionEvent actionEvent) {
        buttonReset.setDisable(true);
        buttonStart.setDisable(false);
        buttonCycleStart.setDisable(false);
        buttonSingleBlock.setDisable(true);
        buttonSingleBlock.setTextFill(Color.BLACK);
        countClick=2;

        String text=codeAreaProgram.getText();
        codeAreaProgram.clear();
        codeAreaProgram.appendText(text);

        presenter.onReset();
    }

    @Override
    public void onDraw(int n) {
        System.out.println(n);
    }

    @Override
    public void showProgram(String text) {
        codeAreaProgram.clear();
        codeAreaProgram.appendText(text);
        buttonStart.setDisable(false);
        buttonCycleStart.setDisable(false);
    }

    @Override
    public void showParameter(String text) {
        codeAreaParameter.clear();
        codeAreaParameter.appendText(text);
    }

    @Override
    public void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setContentText(error);
        Platform.runLater(() -> alert.showAndWait());
    }

    @Override
    public void showFrame(int number) {
        commentLine(number);
    }

    private int commentLine(int l) {
        int start = codeAreaProgram.position(0, 0).toOffset();
        int end;
        int diff = 0;
        try {
            end = codeAreaProgram.position(l + 1, 0).toOffset() - 1;
        } catch (Exception e) {
            end = codeAreaProgram.getLength();
        }
        String line = codeAreaProgram.getText().substring(start, end);
        codeAreaProgram.replaceText(start, end, line);
        return diff;
    }

}
