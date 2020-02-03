package com.sergey.pisarev.presenter;

import com.sergey.pisarev.controller.ResizableCanvas;
import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.File;
import com.sergey.pisarev.model.Point;
import com.sergey.pisarev.model.Program;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Presenter implements PresenterImpl {

    private IController controller;
    private double canvasWidth, canvasHeight,moveX,moveZ;
    private GraphicsContext gc;
    private ResizableCanvas canvas;
    private Point pointSystemCoordinate;


    public Presenter(IController controller, ResizableCanvas resizableCanvas) {
        this.canvas = resizableCanvas;
        this.controller = controller;
        gc = canvas.getGraphicsContext2D();
        canvas.widthProperty().addListener(observable -> initSystemCoordinate());
        canvas.heightProperty().addListener(observable -> initSystemCoordinate());
        handle();
    }

    private void initSystemCoordinate() {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        pointSystemCoordinate = new Point(canvasWidth / 2,canvasHeight / 2);
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5, 5);
        gc.strokeLine(pointSystemCoordinate.getX(), 0, pointSystemCoordinate.getX(), canvasHeight);
        gc.strokeLine(0, pointSystemCoordinate.getZ(), canvasWidth, pointSystemCoordinate.getZ());
    }

    public void handle() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            double downX = event.getX();
            double downZ = event.getY();
            moveX = pointSystemCoordinate.getX() - downX;
            moveZ = pointSystemCoordinate.getZ() - downZ;
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            pointSystemCoordinate.setX(event.getX() + moveX);
            pointSystemCoordinate.setZ(event.getY() + moveZ);
            drawSysCoordinate();
        });
    }

    private void drawSysCoordinate() {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5, 5);
        gc.strokeLine(pointSystemCoordinate.getX(), 0, pointSystemCoordinate.getX(), canvasHeight);
        gc.strokeLine(0, pointSystemCoordinate.getZ(), canvasWidth, pointSystemCoordinate.getZ());
    }

    @Override
    public void onStart(String program, String parameter) {
        Thread thread = new Thread(new Program(program, parameter));
        thread.start();
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
