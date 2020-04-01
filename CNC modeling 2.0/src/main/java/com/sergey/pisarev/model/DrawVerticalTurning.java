package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.Drawing;
import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.base.BaseDraw;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class DrawVerticalTurning extends BaseDraw implements Drawing {

    public DrawVerticalTurning(IDraw draw) {
        super(draw);
    }

    @Override
    public void drawContour(MyData data, GraphicsContext gc, Point pointCoordinateZero, double zoom, int index) {
        List<Frame> frameList = data.getFrameList();
        boolean isDrawPoint = false;
        Point pStart = new Point();
        Point pEnd = new Point();
        Point point = new Point();
        pStart.setX(650f);
        pStart.setZ(250f);
        pEnd.setX(650f);
        pEnd.setZ(250f);
        double radius;
        double radiusRND;
        for (int i = 0; i < index; i++) {
            if (frameList.get(i).getGCode().contains("G17") || frameList.get(i).getGCode().contains("G18"))
                isG17 = isG17(frameList.get(i).getGCode());
            checkGCode(frameList.get(i).getGCode());
            if (data.getErrorListMap().containsKey(frameList.get(i).getId())) {
                draw.showError(data.getErrorListMap().get(frameList.get(i).getId()));
                break;
            } else {
                if (frameList.get(i).getIsCR() && frameList.get(i).isAxisContains()) {                                  //draw Arc
                    pEnd.setX(frameList.get(i).getX());
                    pEnd.setZ(frameList.get(i).getZ());
                    radius = frameList.get(i).getCr();
                    drawArc(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, radius, zoom, clockwise);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                }
                if (!frameList.get(i).getIsCR() && !frameList.get(i).isRND() && frameList.get(i).isAxisContains()) {    //draw line
                    pEnd.setX(frameList.get(i).getX());
                    pEnd.setZ(frameList.get(i).getZ());
                    drawLine(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, zoom);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                }
                if (frameList.get(i).isRND() && frameList.get(i).isAxisContains()) {                                    //draw RND
                    pEnd.setX(frameList.get(i).getX());
                    pEnd.setZ(frameList.get(i).getZ());
                    radiusRND = frameList.get(i).getRnd();
                    Point pointF = new Point();
                    pointF.setX(frameList.get(i + 1).getX());
                    pointF.setZ(frameList.get(i + 1).getZ());
                    drawRND(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, pointF, radiusRND, zoom);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                    pEnd.setX(frameList.get(i).getX());
                    pEnd.setZ(frameList.get(i).getZ());
                }
                if (isNumberLine && frameList.get(i).getId() == numberLIne) {                                           //draw point
                    point.setX(frameList.get(i).getX());
                    point.setZ(frameList.get(i).getZ());
                    isDrawPoint = true;
                }
            }
        }
        drawPoint(gc, pointCoordinateZero, pEnd, zoom, Color.RED);
        if (isDrawPoint) drawPoint(gc, pointCoordinateZero, point, zoom, Color.web("#3507EE"));
    }

    @Override
    public void setNumberLine(int numberLIne) {
        isNumberLine = true;
        this.numberLIne = numberLIne;
    }
}
