package com.sergey.pisarev.presenter;

import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.File;
import com.sergey.pisarev.model.MyData;
import com.sergey.pisarev.model.Program;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.StackPane;

public class Presenter implements PresenterImpl {

    private IController controller;
    private GraphicsContext gc;
    private StackPane paneCanvas;

    public Presenter(IController controller, GraphicsContext gc, StackPane paneCanvas) {
        this.controller = controller;
        this.gc = gc;
        this.paneCanvas = paneCanvas;
    }

    @Override
    public void onStart(String program,String parameter) {
        Thread thread=new Thread(new Program(program,parameter));
        thread.start();
        /*gc.setStroke(Color.BLUE);
        gc.strokeLine(0, 100, 200, 200);
        gc.strokeLine(0, paneCanvas.getHeight()/2, paneCanvas.getWidth(), paneCanvas.getHeight()/2);*/
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
