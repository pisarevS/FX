package com.sergey.pisarev.presenter;

import com.sergey.pisarev.model.ResizableCanvas;
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

    private final IController controller;
    private double canvasWidth, canvasHeight, moveX, moveZ;
    private final GraphicsContext gc;
    private final ResizableCanvas canvas;
    private Point pointSystemCoordinate;
    private MyData data;
    private Drawing drawing;
    private int index;
    private Timeline timeline;
    private final double defZoom = 2;
    private double zooming = defZoom;
    private final ArrayList<String> errorList;
    private boolean isStart = false;
    private boolean isCycleStart = false;
    private boolean isSingleBlock = false;
    private boolean isReset = false;    
    private boolean isChangesText = false;
    private boolean isDrawPoint = false;
    private final Map<String, String> variablesList;

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
            controller.setZooming((zooming - defZoom) / defZoom * 100 + 100);
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
                    Optional<Frame> frame = getFrame(point);
                    if (frame.isPresent()) {
                        drawing.setNumberLine(frame.get().getId());
                        startDraw(index);
                        controller.showCaretBoxOnCanvasClick(frame.get().getId(), data.getProgramList().get(frame.get().getId()));
                        isDrawPoint = true;
                    } else if (isDrawPoint) {
                        drawing.setNumberLine(-1);
                        startDraw(index);
                        isDrawPoint = false;
                    }
                }
            }
        });
    }

    private Optional<Frame> getFrame(Point point) {
        int side = 20;
        Rect rect = new Rect();
        rect.setRect(point.getX() - (side >> 1), point.getZ() - (side >> 1), side, side);
        return data.getFrameList().stream()
                .filter(p -> rect.isInsideRect(p.getX(), p.getZ()))
                .min(Comparator.comparingDouble(p -> Math.abs(point.getX() - p.getX()) + Math.abs(point.getZ() - p.getZ())));
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
        drawing = null;
        initSystemCoordinate();
        if (timeline != null) {
            timeline.stop();
        }
        controller.setZooming(100);
    }

    @Override
    public void getCaretPosition(int numberLine) {
        if (isStart || isCycleStart) {
            isChangesText = true;
            if (drawing != null)
                drawing.setNumberLine(numberLine);
            startDraw(index);
            isChangesText = false;
        }
    }

    @Override
    public void openDragProgram(DragEvent event) {
        controller.showProgram(File.getFileContent(event));
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
        drawing = new DrawVerticalTurning(this);
        if (isStart) {
            index = data.getFrameList().size();
        }
    }

    private void startDraw(int index) {
        if (drawing != null) {
            drawSysCoordinate();
            drawing.drawContour(data, gc, pointSystemCoordinate, zooming, index);
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
        parameterList.forEach(p -> {
                    if (p.toString().contains(";")) p.delete(p.indexOf(";"), p.length());
                    if (p.toString().contains("=")) {
                        int key = 0;
                        for (int j = p.indexOf("=") - 1; j >= 0; j--) {
                            char c = p.charAt(j);
                            if (c == ' ') {
                                key = j;
                                break;
                            }
                        }
                        variablesList.put(
                                p.substring(key, p.indexOf("=")).replace(" ", "")
                                , p.substring(p.indexOf("=") + 1, p.length()).replace(" ", ""));
                    }
                });
    }
}
