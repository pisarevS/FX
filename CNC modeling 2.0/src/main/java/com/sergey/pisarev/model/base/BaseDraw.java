package com.sergey.pisarev.model.base;

import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.MyData;
import com.sergey.pisarev.model.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public abstract class BaseDraw {

    protected boolean clockwise;
    protected IDraw draw;
    protected boolean isRapidFeed;
    private double lineWidth = 1.5;
    private double lineWidthDashes = 1;
    protected boolean isDrawPoint;

    protected BaseDraw(IDraw draw) {
        this.draw = draw;
    }

    public abstract void drawContour(MyData data, GraphicsContext gc, Point pointCoordinateZero, double zoom, int index);

    protected void drawArc(GraphicsContext gc, boolean isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, double radius, double zoom, boolean clockwise) {
        if (isRapidFeed) {
            gc.setStroke(Color.BLACK);
            gc.setLineDashes(3, 5);
            gc.setLineWidth(lineWidthDashes);
        } else {
            gc.setStroke(Color.GREEN);
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
            gc.setStroke(Color.BLACK);
            gc.setLineDashes(3, 5);
            gc.setLineWidth(lineWidthDashes);
        } else {
            gc.setStroke(Color.GREEN);
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

    protected void drawPoint(GraphicsContext gc, Point pointSystemCoordinate, Point pointEnd, double zoom, Color color, double radiusPoint) {
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
        else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
        gc.setFill(color);
        gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
    }
}
