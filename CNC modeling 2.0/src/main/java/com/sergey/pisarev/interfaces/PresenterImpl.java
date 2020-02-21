package com.sergey.pisarev.interfaces;

import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

public interface PresenterImpl {
    void onStart(String program,String parameter);
    void onCycleStart(String program, String parameter);
    void onSingleBlock(boolean isClick);
    void onReset();
    void onMouseClickedProgram(int numberLine);
    void openDragProgram(DragEvent event);
    void openDragParameter(DragEvent event);
    void setOnChangesTextProgram(String program, String parameter);
    void setOnChangesTextParameter(String program, String parameter);
}
