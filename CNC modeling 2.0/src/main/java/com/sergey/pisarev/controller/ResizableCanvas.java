package com.sergey.pisarev.controller;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ResizableCanvas extends Canvas {
    GraphicsContext gc                  = getGraphicsContext2D();
    int             canvasWidth         = 0;
    int             canvasHeight        = 0;
    int             halfCanvasHeight    = 0;
    /**
     * Constructor
     */
    ResizableCanvas() {
        // if i didn't add the draw to the @Override resize(double width, double
        // height) then it must be into the below listeners

        // Redraw canvas when size changes.
        widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Entered WIDTH property");
            canvasWidth = (int) widthProperty().get();
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Entered HEIGHT property");

            canvasHeight = (int) heightProperty().get();
            halfCanvasHeight = canvasHeight >> 1;
        });

    }

    /**
     * Redraw the Canvas
     */
    public void draw() {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5,5);
        
        gc.strokeLine(canvasWidth/2, 0, canvasWidth/2, canvasHeight);
        gc.strokeLine(0, canvasHeight/2, canvasWidth, canvasHeight/2);
    }

    @Override
    public double minHeight(double width) {
        return 1;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    @Override
    public double prefHeight(double width) {
        return minHeight(width);
    }

    @Override
    public double minWidth(double height) {
        return 1;
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        super.setWidth(width);
        super.setHeight(height);
        draw();
    }

}
