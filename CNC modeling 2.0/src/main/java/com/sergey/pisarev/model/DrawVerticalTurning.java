package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.Drawing;
import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.base.BaseDraw;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class DrawVerticalTurning extends BaseDraw implements Drawing {

    public DrawVerticalTurning(IDraw draw) {
        super(draw);
    }

    @Override
    public void drawContour(MyData data, GraphicsContext gc, Point pointCoordinateZero, double zoom, int index) {
        List<Frame> frameList = data.getFrameList();
        Map<Integer, String> errorListMap = data.getErrorListMap();
        Point pStart = new Point();
        Point pEnd = new Point();
        Point point = new Point();
        boolean isDrawPoint = false;
        double radius;
        double radiusRND;
        String tool="";
        for (int i = 0; i < index; i++) {
            if(frameList.get(i).isTool()) tool=frameList.get(i).getTool();
            if (frameList.get(i).getGCode().contains("G17") || frameList.get(i).getGCode().contains("G18")) isG17 = isG17(frameList.get(i).getGCode());
            checkGCode(frameList.get(i).getGCode());
            if (errorListMap.containsKey(frameList.get(i).getId())) {
                draw.showError(errorListMap.get(frameList.get(i).getId()));
                break;
            } else {
                if (frameList.get(i).getIsCR() && frameList.get(i).isAxisContains()&&isRapidFeed!=3&&clockwise!=0) {                                  //draw Arc
                    pEnd.setX(frameList.get(i).getX());
                    pEnd.setZ(frameList.get(i).getZ());
                    radius = frameList.get(i).getCr();
                    drawArc(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, radius, zoom, clockwise);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                }
                if (!frameList.get(i).getIsCR() && !frameList.get(i).isRND() && frameList.get(i).isAxisContains()&&clockwise==0) {    //draw line
                    pEnd.setX(frameList.get(i).getX());
                    pEnd.setZ(frameList.get(i).getZ());
                    drawLine(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, zoom);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                }
                if (frameList.get(i).isRND() && frameList.get(i).isAxisContains()&&isRapidFeed!=3&&clockwise==0) {                                    //draw RND
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
        if (isToolRadiusCompensation != 0 ) {
            drawPoint(gc, pointCoordinateZero, frameList, zoom, Color.web("#D2BF44"), index,tool);
        } else {
            drawPoint(gc, pointCoordinateZero, pEnd, zoom, Color.RED);
        }
        //drawPoint(gc, pointCoordinateZero, pEnd, zoom, Color.RED);
        if (isDrawPoint) drawPoint(gc, pointCoordinateZero, point, zoom, Color.web("#3507EE"));
    }

    @Override
    public void setNumberLine(int numberLIne) {
        isNumberLine = true;
        this.numberLIne = numberLIne;
    }
}
