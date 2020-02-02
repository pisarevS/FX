package com.sergey.pisarev.presenter;

import com.sergey.pisarev.controller.ResizableCanvas;
import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.File;
import com.sergey.pisarev.model.MyData;
import com.sergey.pisarev.model.Point;
import com.sergey.pisarev.model.Program;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Presenter implements PresenterImpl {

    private IController controller;
    private double moveX;
    private double moveZ;
    private GraphicsContext gc;
    private StackPane paneCanvas;
    private ResizableCanvas visualizerCanvas;
    private Point pointCoordinateZero;


    public Presenter(IController controller, ResizableCanvas visualizerCanvas, StackPane paneCanvas) {
        this.visualizerCanvas = visualizerCanvas;
        this.controller = controller;
        this.paneCanvas = paneCanvas;
        gc = visualizerCanvas.getGraphicsContext2D();
        init();
        //initSystemCoordinate();
    }

    private void init() {
        pointCoordinateZero = new Point();
    }

    private void initSystemCoordinate() {
        gc.clearRect(0, 0, paneCanvas.getWidth(), paneCanvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5, 5);
        pointCoordinateZero.setX(paneCanvas.getWidth() / 2);
        pointCoordinateZero.setZ(paneCanvas.getHeight() / 2);
        gc.strokeLine(pointCoordinateZero.getX(), 0, pointCoordinateZero.getX(), paneCanvas.getHeight());
        gc.strokeLine(0, pointCoordinateZero.getZ(), paneCanvas.getWidth(), pointCoordinateZero.getZ());
    }

    private void drawSysCoordinate() {
        gc.clearRect(0, 0, paneCanvas.getWidth(), paneCanvas.getHeight());
        gc.strokeLine(pointCoordinateZero.getX(), 0, pointCoordinateZero.getX(), paneCanvas.getHeight());
        gc.strokeLine(0, pointCoordinateZero.getZ(), paneCanvas.getWidth(), pointCoordinateZero.getZ());
    }

    @Override
    public void onMouseDraggedPane(MouseEvent mouseEvent) {
        pointCoordinateZero.setX(mouseEvent.getX() + moveX);
        pointCoordinateZero.setZ(mouseEvent.getY() + moveZ);

        drawSysCoordinate();

    }

    private boolean isClickDown=false;

    @Override
    public void onMouseDown(MouseEvent mouseEvent) {
        double downX = mouseEvent.getX();
        double downZ = mouseEvent.getY();
        moveX = pointCoordinateZero.getX() - downX;
        moveZ = pointCoordinateZero.getZ() - downZ;
        isClickDown=true;
        System.out.println("isClick X=" + moveX);
        System.out.println("isClick Y=" + moveZ);
    }

    @Override
    public void onMouseMove(MouseEvent mouseEvent) {
        if(isClickDown){

        }
    }

    @Override
    public void onMouseUp(MouseEvent mouseEvent) {
        isClickDown=false;
    }


    @Override
    public void onStart(String program, String parameter) {
        Thread thread = new Thread(new Program(program, parameter));
        thread.start();

        initSystemCoordinate();
        /*gc.setStroke(Color.BLACK);
        gc.setLineDashes(5,5);
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
