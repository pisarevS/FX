package sample;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.Collections;

public class Controller {

    private CodeArea codeAreaProgram=new CodeArea();
    private CodeArea codeAreaParameter=new CodeArea();


    @FXML
    AnchorPane anchorPaneProgram=new AnchorPane();

    @FXML
    AnchorPane anchorPaneParameter=new AnchorPane();

    public void initialize(){
        codeAreaProgram.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaProgram));
        StackPane stackPaneProgram = new StackPane(new VirtualizedScrollPane<>(codeAreaProgram));
        AnchorPane.setTopAnchor(stackPaneProgram,0.0);
        AnchorPane.setBottomAnchor(stackPaneProgram,0.0);
        AnchorPane.setLeftAnchor(stackPaneProgram,0.0);
        AnchorPane.setRightAnchor(stackPaneProgram,0.0);
        anchorPaneProgram.getChildren().add(stackPaneProgram);




        codeAreaParameter.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaParameter));
        StackPane stackPaneParameter = new StackPane(new VirtualizedScrollPane<>(codeAreaParameter));
        AnchorPane.setTopAnchor(stackPaneParameter,0.0);
        AnchorPane.setBottomAnchor(stackPaneParameter,0.0);
        AnchorPane.setLeftAnchor(stackPaneParameter,0.0);
        AnchorPane.setRightAnchor(stackPaneParameter,0.0);
        anchorPaneParameter.getChildren().add(stackPaneParameter);
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
        String text= sample.File.getFileContent(event);
        codeAreaProgram.clear();
        codeAreaProgram.appendText(text);


    }

    @FXML
    public void handleDragParameter(DragEvent event){
        String text= sample.File.getFileContent(event);
        codeAreaParameter.clear();
        codeAreaParameter.appendText(text);
    }

    @FXML
    public void onMouseClickedProgram(Event event){

    }
}
