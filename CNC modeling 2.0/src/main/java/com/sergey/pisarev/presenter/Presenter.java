package com.sergey.pisarev.presenter;

import com.sergey.pisarev.controller.ResizableCanvas;
import com.sergey.pisarev.interfaces.Callback;
import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;

public class Presenter implements PresenterImpl, IDraw, Callback {

    private IController controller;
    private double canvasWidth, canvasHeight, moveX, moveZ;
    private GraphicsContext gc;
    private ResizableCanvas canvas;
    private Point pointSystemCoordinate;
    private MyData data;
    private DrawVerticalTurning drawVerticalTurning;
    private boolean isReset = false;
    private int index;
    private Timeline timeline;
    private boolean isStart = false;
    private boolean isSingleBlock = false;
    private double zooming = 1.5;
    private ArrayList<String> errorList;

    public Presenter(IController controller, ResizableCanvas resizableCanvas) {
        this.canvas = resizableCanvas;
        this.controller = controller;
        gc = canvas.getGraphicsContext2D();
        canvas.widthProperty().addListener(observable -> initSystemCoordinate());
        canvas.heightProperty().addListener(observable -> initSystemCoordinate());
        handle();
        handleZooming();
        errorList = new ArrayList<>();
    }

    private void initSystemCoordinate() {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        pointSystemCoordinate = new Point(canvasWidth / 2, canvasHeight / 2);
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5, 5);
        gc.setGlobalAlpha(1);
        gc.setLineWidth(0.5);
        gc.strokeLine(pointSystemCoordinate.getX(), 0, pointSystemCoordinate.getX(), canvasHeight);
        gc.strokeLine(0, pointSystemCoordinate.getZ(), canvasWidth, pointSystemCoordinate.getZ());
        startDraw(index);
    }

    private void handle() {
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
            startDraw(index);
        });
    }

    private void handleZooming() {
        canvas.setOnScroll((ScrollEvent event) -> {
            zooming += event.getDeltaY() / 600;
            if (zooming > 0) {
                startDraw(index);
            } else {
                zooming = 0;
            }
        });
    }

    private void drawSysCoordinate() {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5, 5);
        gc.setGlobalAlpha(1);
        gc.setLineWidth(0.5);
        gc.strokeLine(pointSystemCoordinate.getX(), 0, pointSystemCoordinate.getX(), canvasHeight);
        gc.strokeLine(0, pointSystemCoordinate.getZ(), canvasWidth, pointSystemCoordinate.getZ());
    }

    @Override
    public void onStart(String program, String parameter) {
        if (!isReset && !program.equals("")) {
            isStart = true;
            isReset = true;
            startThread(program, parameter);
            startDraw(index);
        }
    }

    @Override
    public void onCycleStart(String program, String parameter) {
        if (!isReset && !program.equals("") && !isSingleBlock) {
            startThread(program, parameter);
            assert data != null;
            timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
                index++;
                startDraw(index);
                controller.showFrame(data.getFrameList().get(index - 1).getId());
                if (index == data.getFrameList().size())
                    controller.onReset();
            }));
            timeline.setCycleCount(data.getFrameList().size());
            timeline.play();
        } else if (isSingleBlock) {
            index++;
            if (index <= data.getFrameList().size())
                startDraw(index);
            if (index == data.getFrameList().size())
                controller.onReset();
            controller.showFrame(data.getFrameList().get(index - 1).getId());
        }
        isReset = true;
    }

    @Override
    public void onSingleBlock(boolean isClick) {
        if (isClick) {
            timeline.stop();
            isSingleBlock = true;
        } else {
            isSingleBlock = false;
            timeline.play();
        }
    }

    @Override
    public void onReset() {
       reset();
    }

    private void reset(){
        isReset = false;
        isSingleBlock = false;
        data = null;
        index = 0;
        zooming = 1;
        errorList.clear();
        drawVerticalTurning = null;
        initSystemCoordinate();
        if (timeline != null) {
            timeline.stop();
        }
    }

    @Override
    public void onMouseClickedProgram(int numberLine) {
        if (drawVerticalTurning != null)
            drawVerticalTurning.getNumberLine(numberLine);
        startDraw(index);
    }

    @Override
    public void openDragProgram(DragEvent event) {
        controller.showProgram(File.getFileContent( event,"program"));
        reset();
    }

    @Override
    public void openDragParameter(DragEvent event) {
        controller.showParameter(File.getFileContent( event,"parameter"));
        reset();
    }

    @Override
    public void showError(String error) {
        if (!errorList.contains(error)) {
            errorList.add(error);
            controller.showError(error);
        }
        if (timeline != null)
            timeline.stop();
    }

    @Override
    public void callingBack(MyData data) {
        this.data = data;
        drawVerticalTurning = new DrawVerticalTurning(this);
        if (isStart) {
            index = data.getFrameList().size();
            isStart = false;
        }
    }

    private void startDraw(int index) {
        if (drawVerticalTurning != null) {
            drawSysCoordinate();
            drawVerticalTurning.drawContour(data, gc, pointSystemCoordinate, zooming, index);
        }
    }

    private void startThread(String program, String parameter) {
        Thread thread = new Thread(new Program(program, parameter, this));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
