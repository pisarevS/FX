package com.sergey.pisarev.controller;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ResizableCanvas extends Canvas {

    public ResizableCanvas(){
        widthProperty().addListener(evt->draw());
        heightProperty().addListener(evt->draw());
    }

    private void draw(){
        double width=getWidth();
        double heigth=getHeight();
        GraphicsContext gc=getGraphicsContext2D();
        gc.clearRect(0,0,width,heigth);
        gc.setStroke(Color.RED);
        
        gc.setFill(Color.BLUE);
        gc.fillOval(500,500,500,500);

    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
