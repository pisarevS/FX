package com.sergey.pisarev.interfaces;

import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

public interface PresenterImpl {
    void onStart(String program);
    void onCycleStart(String program);
    void onSingleBlock(boolean isClick);
    void onReset();
    void getCaretPosition(int numberLine);
    void openDragProgram(DragEvent event);
    void setOnChangesTextProgram(String program);
}
