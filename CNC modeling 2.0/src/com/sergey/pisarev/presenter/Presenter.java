package com.sergey.pisarev.presenter;

import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.File;
import javafx.scene.input.DragEvent;

public class Presenter implements PresenterImpl {

    private IController controller;

    public Presenter(IController controller) {
        this.controller = controller;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCycleStart() {

    }

    @Override
    public void onSingleBlock() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public void onMouseClickedProgram(int numberLine) {
        controller.onDraw(numberLine);
    }

    @Override
    public void openDragProgram(DragEvent event) {
        controller.showProgram(File.getFileContent(event));
    }

    @Override
    public void openDragParameter(DragEvent event) {
        controller.showParameter(File.getFileContent(event));
    }
}
