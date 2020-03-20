package com.sergey.pisarev.interfaces;

import com.sergey.pisarev.model.MyData;
import com.sergey.pisarev.model.Point;
import javafx.scene.canvas.GraphicsContext;

public interface Drawing {
     void drawContour(MyData data, GraphicsContext gc, Point pointCoordinateZero, double zoom, int index);
     void setNumberLine(int numberLine);
}
