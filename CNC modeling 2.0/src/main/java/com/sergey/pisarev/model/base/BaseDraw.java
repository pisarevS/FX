package com.sergey.pisarev.model.base;

import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.Frame;
import com.sergey.pisarev.model.Point;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.List;

public abstract class BaseDraw {

    protected boolean clockwise;
    protected IDraw draw;
    protected boolean isRapidFeed;
    private double lineWidth = 1.5;
    private double lineWidthDashes = 1;
    protected boolean isNumberLine;
    private Color colorLine = Color.GREEN;
    private Color colorLineDashes = Color.BLACK;
    protected int numberLIne;
    protected boolean isG17 = false;

    protected BaseDraw(IDraw draw) {
        this.draw = draw;
    }

    protected void drawArc(GraphicsContext gc, boolean isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, double radius, double zoom, boolean clockwise) {
        if (isRapidFeed) {
            gc.setStroke(colorLineDashes);
            gc.setLineDashes(3, 5);
            gc.setLineWidth(lineWidthDashes);
        } else {
            gc.setStroke(colorLine);
            gc.setLineDashes();
            gc.setLineWidth(lineWidth);
        }
        Point pStart = new Point(pointStart.getX(), pointStart.getZ());
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        pStart.setX(pStart.getX() * zoom);
        pStart.setZ(pStart.getZ() * zoom);
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        radius *= zoom;
        double startAngle = 0, cathet;
        double chord = Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
        double sweetAngle = 2 * Math.asin(chord / (2 * radius)) * (180 / Math.PI);
        float h = (float) Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
        if (clockwise) {
            double x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
            double z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
            if (pStart.getX() > x01 && pStart.getZ() >= z01) {
                cathet = pStart.getX() - x01;
                if (pStart.getZ() == z01)
                    startAngle = 0;
                else startAngle = (360 - Math.acos(cathet / radius) * (180 / Math.PI));
            }
            if (pStart.getX() >= x01 && pStart.getZ() < z01) {
                cathet = pStart.getX() - x01;
                if (pStart.getX() == x01)
                    startAngle = 90;
                else startAngle = (Math.acos(cathet / radius) * (180 / Math.PI));
            }
            if (pStart.getX() < x01 && pStart.getZ() <= z01) {
                cathet = x01 - pStart.getX();
                if (pStart.getZ() == z01)
                    startAngle = 180;
                else startAngle = (180 - Math.acos(cathet / radius) * (180 / Math.PI));
            }
            if (pStart.getX() <= x01 && pStart.getZ() > z01) {
                cathet = x01 - pStart.getX();
                if (pStart.getX() == x01)
                    startAngle = 270;
                else startAngle = (180 + Math.acos(cathet / radius) * (180 / Math.PI));
            }
            gc.strokeArc(pointSystemCoordinate.getX() + x01 - radius, pointSystemCoordinate.getZ() - z01 - radius, radius * 2, radius * 2, 360 - startAngle - sweetAngle, sweetAngle, ArcType.OPEN);
        } else {
            double x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
            double z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
            if (pEnd.getX() > x02 && pEnd.getZ() >= z02) {
                cathet = pEnd.getX() - x02;
                if (pEnd.getZ() == z02)
                    startAngle = 0;
                else startAngle = (360 - Math.acos(cathet / radius) * (180 / Math.PI));
            }
            if (pEnd.getX() >= x02 && pEnd.getZ() < z02) {
                cathet = pEnd.getX() - x02;
                if (pEnd.getX() == x02)
                    startAngle = 90;
                else startAngle = (Math.acos(cathet / radius) * (180 / Math.PI));
            }
            if (pEnd.getX() < x02 && pEnd.getZ() <= z02) {
                cathet = x02 - pEnd.getX();
                if (pEnd.getZ() == z02)
                    startAngle = 180;
                else startAngle = (180 - Math.acos(cathet / radius) * (180 / Math.PI));
            }
            if (pEnd.getX() <= x02 && pEnd.getZ() > z02) {
                cathet = x02 - pEnd.getX();
                if (pEnd.getX() == x02)
                    startAngle = 270;
                else startAngle = (180 + Math.acos(cathet / radius) * (180 / Math.PI));
            }
            gc.strokeArc(pointSystemCoordinate.getX() + x02 - radius, pointSystemCoordinate.getZ() - z02 - radius, radius * 2, radius * 2, 360 - startAngle - sweetAngle, sweetAngle, ArcType.OPEN);
        }
    }

    protected void drawLine(GraphicsContext gc, boolean isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, double zoom) {
        if (isRapidFeed) {
            gc.setStroke(colorLineDashes);
            gc.setLineDashes(3, 5);
            gc.setLineWidth(lineWidthDashes);
        } else {
            gc.setStroke(colorLine);
            gc.setLineDashes();
            gc.setLineWidth(lineWidth);
        }
        Point pStart = new Point(pointStart.getX(), pointStart.getZ());
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        pStart.setX(pStart.getX() * zoom);
        pStart.setZ(pStart.getZ() * zoom);
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        if (pStart.getZ() > 0) pStart.setZ(pointSystemCoordinate.getZ() - pStart.getZ());
        else pStart.setZ(pointSystemCoordinate.getZ() + Math.abs(pStart.getZ()));
        if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
        else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
        gc.strokeLine(pointSystemCoordinate.getX() + pStart.getX(), pStart.getZ(), pointSystemCoordinate.getX() + pEnd.getX(), pEnd.getZ());
    }

    protected void drawPoint(GraphicsContext gc, Point pointSystemCoordinate, Point pointEnd, double zoom, Color color) {
        double radiusPoint = 4;
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
        else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
        gc.setFill(color);
        gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
        gc.setStroke(color);
        gc.setLineDashes();
        gc.setLineWidth(lineWidth);
        radiusPoint += 2.5;
        gc.strokeOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
    }

    protected void drawRND(GraphicsContext gc, boolean isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, Point pointF, double radiusRND, double zoom) {
        Point pointStartCR = new Point();
        Point pointEndCR = new Point();
        double differenceX;
        double differenceZ;
        double cathet;
        boolean clockwiseRND = false;
        double angle = new Point2D(pointEnd.getX() - pointStart.getX(), pointEnd.getZ() - pointStart.getZ()).angle(pointEnd.getX() - pointF.getX(), pointEnd.getZ() - pointF.getZ());
        double firstDistance = new Point2D(pointStart.getX(), pointStart.getZ()).distance(pointEnd.getX(), pointEnd.getZ());
        double secondDistance = new Point2D(pointEnd.getX(), pointEnd.getZ()).distance(pointF.getX(), pointF.getZ());
        if (angle == 90) {
            cathet = radiusRND;
        } else {
            cathet = (180 - angle) / 2 * (Math.PI / 180) * radiusRND;
        }
        differenceX = pointStart.getX() - pointEnd.getX();
        differenceZ = pointStart.getZ() - pointEnd.getZ();

        pointStartCR.setX(differenceX * cathet / firstDistance);
        pointStartCR.setZ(differenceZ * cathet / firstDistance);
        pointStartCR.setX(pointEnd.getX() + pointStartCR.getX());
        pointStartCR.setZ(pointEnd.getZ() + pointStartCR.getZ());

        differenceX = pointF.getX() - pointEnd.getX();
        differenceZ = pointF.getZ() - pointEnd.getZ();

        pointEndCR.setX(differenceX * cathet / secondDistance);
        pointEndCR.setZ(differenceZ * cathet / secondDistance);
        pointEndCR.setX(pointEnd.getX() + pointEndCR.getX());
        pointEndCR.setZ(pointEnd.getZ() + pointEndCR.getZ());

        if (pointStart.getX()>pointF.getX()&&(pointStart.getZ()+pointF.getZ())/2>pointEnd.getZ()) {
            clockwiseRND = true;
        }
        if(pointStart.getX()>pointF.getX()&&(pointStart.getZ()+pointF.getZ())/2<pointEnd.getZ()){
            clockwiseRND=false;
        }
        if(pointStart.getX()<pointF.getX()&&(pointStart.getZ()+pointF.getZ())/2<pointEnd.getZ()){
            clockwiseRND=true;
        }
        drawLine(gc, isRapidFeed, pointSystemCoordinate, pointStart, pointStartCR, zoom);
        drawArc(gc, isRapidFeed, pointSystemCoordinate, pointStartCR, pointEndCR, radiusRND, zoom, clockwiseRND);
        pointEnd.setX(pointEndCR.getX());
        pointEnd.setZ(pointEndCR.getZ());
    }

    protected boolean isG17(List<String> gCodes) {
        for (String gCode : gCodes) {
            return gCode.contains("G17");
        }
        return false;
    }

    protected void checkGCode(List<String> gCodeList) {
        for (String gCode : gCodeList) {
            switch (gCode) {
                case "G0":
                case "G00":
                    isRapidFeed = true;
                    break;
                case "G1":
                case "G01":
                    isRapidFeed = false;
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
