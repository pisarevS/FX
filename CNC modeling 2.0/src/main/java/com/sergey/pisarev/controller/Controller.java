package com.sergey.pisarev.controller;

import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.File;
import com.sergey.pisarev.presenter.Presenter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;
import com.sergey.pisarev.model.StyleText;

public class Controller implements IController {

    private CodeArea codeAreaProgram=new CodeArea();
    private CodeArea codeAreaParameter=new CodeArea();
    private PresenterImpl presenter;
    private int countClick=2;
    private boolean isDownSingleBlock=false;
    private boolean isCycleStart=false;

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
        paneCanvas.setStyle("-fx-background-color: #F5F5F5");
        paneCanvas.getChildren().add(visualizerCanvas);

        presenter=new Presenter(this,visualizerCanvas);
        StyleText styleText =new StyleText();
        styleText.setStyle(codeAreaProgram);
        codeAreaProgram.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaProgram));
        StackPane stackPaneProgram = new StackPane(new VirtualizedScrollPane<>(codeAreaProgram));
        AnchorPane.setTopAnchor(stackPaneProgram,0.0);
        AnchorPane.setBottomAnchor(stackPaneProgram,0.0);
        AnchorPane.setLeftAnchor(stackPaneProgram,0.0);
        AnchorPane.setRightAnchor(stackPaneProgram,0.0);
        anchorPaneProgram.getChildren().add(stackPaneProgram);

        styleText.setStyle(codeAreaParameter);
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

        TableUtils.installCopyPasteHandler(codeAreaProgram);
        TableUtils.installCopyPasteHandler(codeAreaParameter);
    }

    @FXML
    public void handleDragOverProgram(DragEvent event){
        if(event.getDragboard().hasFiles())
        event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    public void handleDragOverParameter(DragEvent event){
        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
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
        isCycleStart=true;
        if(isDownSingleBlock) {
            buttonCycleStart.setDisable(false);
        }
        presenter.onCycleStart(codeAreaProgram.getText(),codeAreaParameter.getText());
    }

    @FXML
    public void onSingleBlock(ActionEvent actionEvent) {
        countClick++;
        if(countClick%2==0){
            isDownSingleBlock=false;
            buttonSingleBlock.setStyle("-fx-background-color: ");
            buttonCycleStart.setDisable(true);
            presenter.onSingleBlock(false);
        }else {
            isDownSingleBlock=true;
            buttonSingleBlock.setStyle("-fx-background-color: yellow");
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
        buttonSingleBlock.setStyle("-fx-background-color: ");
        countClick=2;
        presenter.onReset();
        if(isCycleStart){
            String text=codeAreaProgram.getText();
            codeAreaProgram.clear();
            codeAreaProgram.appendText(text);
            isCycleStart=false;
        }
    }

    @Override
    public void onReset() {
        buttonCycleStart.setDisable(true);
        buttonSingleBlock.setStyle("-fx-background-color: ");
        buttonSingleBlock.setDisable(true);
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
        Platform.runLater(alert::showAndWait);
    }

    @Override
    public void showFrame(int number) {
        commentLine(number);
    }

    private void commentLine(int l) {
        int start = codeAreaProgram.position(0, 0).toOffset();
        int end;
        try {
            end = codeAreaProgram.position(l + 1, 0).toOffset() - 1;
        } catch (Exception e) {
            end = codeAreaProgram.getLength();
        }
        String line = codeAreaProgram.getText().substring(start, end);
        codeAreaProgram.replaceText(start, end, line);
    }

    @FXML
    public void menuSaveAll(ActionEvent actionEvent) {
        if(File.fileProgram!=null) {
            File.setFileContent(File.fileProgram,codeAreaProgram.getText());
        }  if(File.fileParameter!=null){
            File.setFileContent(File.fileParameter,codeAreaParameter.getText());
        } if(File.fileProgram!=null||File.fileParameter!=null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setAlertType(AlertType.INFORMATION);
            alert.setContentText("All saved!");
            alert.showAndWait();
        }
    }

    @FXML
    public void menuSaveProgram(ActionEvent actionEvent) {
        if(File.fileProgram!=null) {
            File.setFileContent(File.fileProgram,codeAreaProgram.getText());
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setAlertType(AlertType.INFORMATION);
            alert.setContentText("Program saved!");
            alert.showAndWait();
        }
    }

    @FXML
    public void menuSaveParameter(ActionEvent actionEvent) {
        if(File.fileParameter!=null){
            File.setFileContent(File.fileParameter,codeAreaParameter.getText());
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setAlertType(AlertType.INFORMATION);
            alert.setContentText("Parameter saved!");
            alert.showAndWait();
        }
    }

    @FXML
    public void menuQuit(ActionEvent actionEvent) {
        saveChanges();
        Platform.exit();
    }

    private void saveChanges(){
        if(File.fileProgram!=null) {
            String programFile=File.getFileContent(File.fileProgram);
            if(!programFile.equals(codeAreaProgram.getText())){
                alertSaveChanges(File.fileProgram,codeAreaProgram.getText(),"Program");
            }
        } if(File.fileParameter!=null) {
            String parameterFile=File.getFileContent(File.fileParameter);
            if(!parameterFile.equals(codeAreaParameter.getText())){
                alertSaveChanges(File.fileParameter,codeAreaParameter.getText(),"Parameter");
            }
        }
    }

    private void alertSaveChanges(java.io.File file,String text,String message){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setAlertType(AlertType.INFORMATION);
        alert.setContentText("Do you want to save " + message + " changes?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setOnAction(event -> {
            System.out.println("yes");
            File.setFileContent(file,text);
        });
        alert.showAndWait();
    }

}
