package com.sergey.pisarev.interfaces;

import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public interface PresenterImpl {
    void onStart(String program);

    void onCycleStart(String program);

    void onSingleBlock(boolean isClick);

    void onReset();

    void getCaretPosition(int numberLine);

    void openDragProgram(DragEvent event);

    void setOnChangesTextProgram(String program);

    void onMouseClickedCanvas(MouseEvent event);

    void onMouseMovedCanvas(MouseEvent event);

    void handleZooming(ScrollEvent event);

    void handleMousePressed(MouseEvent event);

    void handleMouseDragged(MouseEvent event);

    void initSystemCoordinate(double canvasWidth,double canvasHeight);
}
