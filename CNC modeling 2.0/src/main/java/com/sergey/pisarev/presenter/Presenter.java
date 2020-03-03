package com.sergey.pisarev.presenter;

import com.sergey.pisarev.controller.ResizableCanvas;
import com.sergey.pisarev.interfaces.*;
import com.sergey.pisarev.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class Presenter implements PresenterImpl, IDraw, Callback {

    private IController controller;
    private double canvasWidth, canvasHeight, moveX, moveZ;
    private GraphicsContext gc;
    private ResizableCanvas canvas;
    private Point pointSystemCoordinate;
    private MyData data;
    private DrawVerticalTurning drawVerticalTurning;
    private int index;
    private Timeline timeline;
    private final double defZoom = 2;
    private double zooming = defZoom;
    private ArrayList<String> errorList;
    private boolean isStart = false;
    private boolean isCycleStart = false;
    private boolean isSingleBlock = false;
    private boolean isReset = false;
    private boolean isChangesText = false;
    private Map<String, String> variablesList;

    public Presenter(IController controller, ResizableCanvas resizableCanvas) {
        this.canvas = resizableCanvas;
        this.controller = controller;
        gc = canvas.getGraphicsContext2D();
        canvas.widthProperty().addListener(observable -> initSystemCoordinate());
        canvas.heightProperty().addListener(observable -> initSystemCoordinate());
        handle();
        handleZooming();
        onMouseClickedCanvas();
        onMouseMovedCanvas();
        errorList = new ArrayList<>();
        variablesList = new LinkedHashMap<>();
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
            controller.getZooming((zooming - defZoom) / defZoom * 100 + 100);
        });
    }

    private void onMouseClickedCanvas() {
        canvas.setOnMouseClicked(event -> {
            if (isStart || isCycleStart) {
                if (event.getClickCount() == 2) {
                    Point point = new Point();
                    point.setX((pointSystemCoordinate.getX() - event.getX()) * -1);
                    point.setZ(event.getY());
                    if (point.getZ() > 0) point.setZ(pointSystemCoordinate.getZ() - point.getZ());
                    else point.setZ(pointSystemCoordinate.getZ() + Math.abs(point.getZ()));
                    point.setX(point.getX() / zooming);
                    point.setZ(point.getZ() / zooming);
                    setNumberFrame(point);
                }
            }
        });
    }

    private void onMouseMovedCanvas() {
        canvas.setOnMouseMoved(event -> {
            Point point = new Point();
            point.setX((pointSystemCoordinate.getX() - event.getX()) * -1);
            point.setZ(event.getY());
            if (point.getZ() > 0) point.setZ(pointSystemCoordinate.getZ() - point.getZ());
            else point.setZ(pointSystemCoordinate.getZ() + Math.abs(point.getZ()));
            point.setX(point.getX() / zooming);
            point.setZ(point.getZ() / zooming);
            controller.getCoordinateCanvas(point.getX(), point.getZ());
        });
    }

    private void setNumberFrame(Point point) {
        int side = 7;
        Rect rect = new Rect();
        rect.setRect(point.getX() - (side >> 1), point.getZ() - (side >> 1), side, side);
        if (drawVerticalTurning != null)
            for (Frame frame : data.getFrameList()) {
                if (rect.isInsideRect(frame.getX(), frame.getZ())) {
                    drawVerticalTurning.setNumberLine(frame.getId());
                    startDraw(index);
                    controller.showCaretBoxOnCanvasClick(frame.getId(), data.getProgramList().get(frame.getId()));
                }
            }
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
    public void onStart(String program) {
        if (!isReset && !program.equals("")) {
            isStart = true;
            isReset = true;
            startThread(program);
            startDraw(index);
        }
    }

    @Override
    public void onCycleStart(String program) {
        isCycleStart = true;
        if (!isReset && !program.equals("") && !isSingleBlock) {
            startThread(program);
            assert data != null;
            timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
                index++;
                startDraw(index);
                controller.showCaretBoxOnCycleStart(data.getFrameList().get(index - 1).getId(), data.getProgramList().get(data.getFrameList().get(index - 1).getId()));
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
            controller.showCaretBoxOnCycleStart(data.getFrameList().get(index - 1).getId(), data.getProgramList().get(data.getFrameList().get(index - 1).getId()));
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

    private void reset() {
        isStart = false;
        isReset = false;
        isSingleBlock = false;
        isCycleStart = false;
        data = null;
        index = 0;
        zooming = defZoom;
        errorList.clear();
        drawVerticalTurning = null;
        initSystemCoordinate();
        if (timeline != null) {
            timeline.stop();
        }
        controller.getZooming(100);
    }

    @Override
    public void getCaretPosition(int numberLine) {
        if (isStart || isCycleStart) {
            isChangesText = true;
            if (drawVerticalTurning != null)
                drawVerticalTurning.setNumberLine(numberLine);
            startDraw(index);
            isChangesText = false;
        }
    }

    @Override
    public void openDragProgram(DragEvent event) {
        controller.showProgram(File.getFileContent(event));

        //readParameterVariables(Objects.requireNonNull(File.getParameter(File.fileProgram)));
        //controller.getVariablesList(variablesList);
        reset();
    }

    @Override
    public void setOnChangesTextProgram(String program) {
        if (isStart) {
            isChangesText = true;
            startThread(program);
            startDraw(index);
            isChangesText = false;
        }
    }

    @Override
    public void showError(String error) {
        if (!isChangesText) {
            if (!errorList.contains(error)) {
                errorList.add(error);
                controller.showError(error);
            }
            if (timeline != null)
                timeline.stop();
        }
    }

    @Override
    public void callingBack(MyData data) {
        this.data = data;
        drawVerticalTurning = new DrawVerticalTurning(this);
        if (isStart) {
            index = data.getFrameList().size();
        }
    }

    private void startDraw(int index) {
        if (drawVerticalTurning != null) {
            drawSysCoordinate();
            drawVerticalTurning.drawContour(data, gc, pointSystemCoordinate, zooming, index);
        }
    }

    private void startThread(String program) {
        readParameterVariables(Objects.requireNonNull(File.getParameter(File.fileProgram)));
        Thread thread = new Thread(new Program(program, variablesList, this));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readParameterVariables(List<StringBuffer> parameterList) {
        variablesList.clear();
        for (StringBuffer stringBuffer : parameterList) {
            if (stringBuffer.toString().contains(";")) {
                stringBuffer.delete(stringBuffer.indexOf(";"), stringBuffer.length());
            }
            if (stringBuffer.toString().contains("=")) {
                int key = 0;
                for (int j = stringBuffer.indexOf("=") - 1; j >= 0; j--) {
                    char c = stringBuffer.charAt(j);
                    if (c == ' ') {
                        key = j;
                        break;
                    }
                }
                variablesList.put(
                        stringBuffer.substring(key, stringBuffer.indexOf("=")).replace(" ", "")
                        , stringBuffer.substring(stringBuffer.indexOf("=") + 1, stringBuffer.length()).replace(" ", ""));
            }
        }
    }
}
