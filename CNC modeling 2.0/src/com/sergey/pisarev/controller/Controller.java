package com.sergey.pisarev.controller;

import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.presenter.Presenter;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
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
    private GraphicsContext gc;
    private ResizableCanvas canvas;


    @FXML
    Pane pane=new Pane();

    @FXML
    AnchorPane anchorPaneProgram=new AnchorPane();

    @FXML
    AnchorPane anchorPaneParameter=new AnchorPane();

    public void initialize(){
        presenter=new Presenter(this);
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

        canvas=new ResizableCanvas();
        pane.getChildren().add(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());


        gc=canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillOval(50,50,50,50);



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
        presenter.onStart();
    }

    @FXML
    public void onCycleStart(ActionEvent actionEvent) {
        presenter.onCycleStart();
    }

    @FXML
    public void onSingleBlock(ActionEvent actionEvent) {
        presenter.onSingleBlock();
    }

    @FXML
    public void onReset(ActionEvent actionEvent) {
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
    }

    @Override
    public void showParameter(String text) {
        codeAreaParameter.clear();
        codeAreaParameter.appendText(text);
    }
}
