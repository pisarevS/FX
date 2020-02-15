package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.base.BaseDraw;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class DrawHorizontalTurning extends BaseDraw {

    private List<StringBuffer> programList;
    protected ArrayList<Frame> frameList;

    public DrawHorizontalTurning(IDraw draw) {
        super(draw);
    }

    @Override
    public void drawContour(MyData data,GraphicsContext gc, Point pointCoordinateZero, float zoom, int index) {
        programList = data.getProgramList();
        frameList = data.getFrameList();
        boolean isLine = false;
        boolean isRadius = false;
        Point pStart = new Point();
        Point pEnd = new Point();
        pStart.setX(650f);
        pStart.setZ(250f);
        pEnd.setX(650f);
        pEnd.setZ(250f);
        double radius = 0;
        for (int i = 0; i < index; i++) {
            checkGCode(frameList.get(i).getGCode());

            if (data.getErrorListMap().containsKey(frameList.get(i).getId())) {
                draw.showError(data.getErrorListMap().get(frameList.get(i).getId()));
                break;
            } else {
                if (frameList.get(i).getIsCR()) {
                    pEnd.setX(frameList.get(i).getZ());
                    pEnd.setZ(frameList.get(i).getX());
                    radius = frameList.get(i).getCr();
                    isRadius = true;
                } else {
                    pEnd.setX(frameList.get(i).getZ());
                    pEnd.setZ(frameList.get(i).getX());
                    isLine = true;
                }
                if (isRadius && frameList.get(i).isAxisContains()) {
                    drawArc(gc,isRapidFeed, pointCoordinateZero, pStart, pEnd, radius, zoom, !clockwise);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                    isLine = false;
                    isRadius = false;
                }
                if (isLine && frameList.get(i).isAxisContains()) {
                    drawLine(gc,isRapidFeed, pointCoordinateZero, pStart, pEnd, zoom);
                    pStart.setX(pEnd.getX());
                    pStart.setZ(pEnd.getZ());
                }
            }
        }
        drawPoint(gc, pointCoordinateZero, pEnd, zoom);
    }

    private boolean isG17(List<StringBuffer> programList) {
        for (StringBuffer stringBuffer : programList)
            if (stringBuffer.toString().contains("G17")) {
                return true;
            }
        return false;
    }

    protected void checkGCode(List<String> gCodeList) {
        boolean isG17 = isG17(programList);
        for (String gCode : gCodeList) {
            switch (gCode) {
                case "G0":
                case "G00":
                    isRapidFeed=true;
                    break;
                case "G1":
                case "G01":
                    isRapidFeed=false;
                    break;
                case "G2":
                case "G02":
                    clockwise = isG17;
                    break;
                case "G3":
                case "G03":
                    clockwise = !isG17;
                    break;
            }
        }
    }
}