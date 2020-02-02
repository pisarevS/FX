package com.sergey.pisarev.interfaces;

import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

public interface PresenterImpl {
    void onStart(String program,String parameter);
    void onCycleStart();
    void onSingleBlock();
    void onReset();
    void onMouseClickedProgram(int numberLine);
    void onMouseDraggedPane(MouseEvent mouseEvent);
    void onMouseDown(MouseEvent mouseEvent);
    void onMouseMove(MouseEvent mouseEvent);
    void onMouseUp(MouseEvent mouseEvent);
    void openDragProgram(DragEvent event);
    void openDragParameter(DragEvent event);
}
