package com.sergey.pisarev.interfaces;

import javafx.scene.input.DragEvent;

public interface PresenterImpl {
    void onStart();
    void onCycleStart();
    void onSingleBlock();
    void onReset();
    void onMouseClickedProgram(int numberLine);
    void openDragProgram(DragEvent event);
    void openDragParameter(DragEvent event);
}
