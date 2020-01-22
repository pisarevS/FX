package sample;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;


public class Controller {


    @FXML
    TextArea textAreaProgram=new TextArea();

    @FXML
    TextArea textAreaParameter=new TextArea();

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
        textAreaProgram.setText(text);
    }

    @FXML
    public void handleDragParameter(DragEvent event){
        String text= sample.File.getFileContent(event);
        textAreaParameter.setText(text);
    }

    @FXML
    public void onMouseClickedProgram(Event event){
        event.getEventType().getName().toString();
    }

}
