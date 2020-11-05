package com.sergey.pisarev.model.base;

import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.Frame;
import com.sergey.pisarev.model.MyData;
import com.sergey.pisarev.model.Point;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.List;
import java.util.Map;

public abstract class BaseDraw {

    protected boolean clockwise;
    protected IDraw draw;
    protected boolean isRapidFeed;
    protected int isToolRadiusCompensation = 0;
    private final double lineWidth = 1.5;
    private final double lineWidthDashes = 1;
    protected boolean isNumberLine;
    private final Color colorLine = Color.GREEN;
    private final Color colorLineDashes = Color.BLACK;
    protected int numberLIne;
    protected boolean isG17 = false;
    protected Point pStart = new Point();
    protected Point pEnd = new Point();

    protected BaseDraw(IDraw draw) {
        this.draw = draw;
    }

    public BaseDraw() {
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

    protected void drawPoint(GraphicsContext gc, Point pointSystemCoordinate, List<Frame> frameList, double zoom, Color color, int index) {
        double radiusPoint = 6;
        if (index == 1) index++;
        Point pStart = new Point(frameList.get(index - 2).getX(), frameList.get(index - 2).getZ());
        Point pEnd = new Point(frameList.get(index - 1).getX(), frameList.get(index - 1).getZ());

        if (frameList.get(index - 1).getIsCR() && frameList.get(index - 1).isAxisContains()&&!containsG41G42(frameList.get(index-1).getGCode())) {
            double radius = frameList.get(index - 1).getCr();
            double chord = Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
            float h = (float) Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
            if (clockwise) {
                double x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
                double z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
                if (isToolRadiusCompensation == 1) {
                    double tempEndX = (x01 - pEnd.getX()) * ((radius - radiusPoint) / radius);
                    double tempEndZ = (z01 - pEnd.getZ()) * ((radius - radiusPoint) / radius);
                    pEnd.setX(pEnd.getX() + (x01 - pEnd.getX() - tempEndX));
                    pEnd.setZ(pEnd.getZ() + (z01 - pEnd.getZ() - tempEndZ));
                }  //G41
                if (isToolRadiusCompensation == 2) {
                    double tempEndX = (x01 - pEnd.getX()) * ((radius + radiusPoint) / radius);
                    double tempEndZ = (z01 - pEnd.getZ()) * ((radius + radiusPoint) / radius);
                    pEnd.setX(pEnd.getX() + (x01 - pEnd.getX() - tempEndX));
                    pEnd.setZ(pEnd.getZ() + (z01 - pEnd.getZ() - tempEndZ));
                }  //G42
            }
            if (!clockwise) {
                double x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
                double z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
                if (isToolRadiusCompensation == 1) {
                    double tempEndX = (x02 - pEnd.getX()) * ((radius + radiusPoint) / radius);
                    double tempEndZ = (z02 - pEnd.getZ()) * ((radius + radiusPoint) / radius);
                    pEnd.setX(pEnd.getX() + (x02 - pEnd.getX() - tempEndX));
                    pEnd.setZ(pEnd.getZ() + (z02 - pEnd.getZ() - tempEndZ));
                }  //G41
                if (isToolRadiusCompensation == 2) {
                    double tempEndX = (x02 - pEnd.getX()) * ((radius - radiusPoint) / radius);
                    double tempEndZ = (z02 - pEnd.getZ()) * ((radius - radiusPoint) / radius);
                    pEnd.setX(pEnd.getX() + (x02 - pEnd.getX() - tempEndX));
                    pEnd.setZ(pEnd.getZ() + (z02 - pEnd.getZ() - tempEndZ));
                }  //G42
            }
        }

        if (!frameList.get(index - 1).getIsCR() && !frameList.get(index - 1).isRND() && frameList.get(index - 1).isAxisContains()&&!containsG41G42(frameList.get(index-1).getGCode())) {    //draw line
            if (isToolRadiusCompensation == 1) {
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() > pEnd.getX()) {
                    pEnd.setZ(pEnd.getZ() + radiusPoint);
                    pStart.setZ(pStart.getZ() + radiusPoint);
                }  //Z==Z -X
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() < pEnd.getX()) {
                    pEnd.setZ(pEnd.getZ() - radiusPoint);
                    pStart.setZ(pStart.getZ() - radiusPoint);
                }  //Z==Z +X
                if (pStart.getX() == pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    pEnd.setX(pEnd.getX() - radiusPoint);
                    pStart.setX(pStart.getX() - radiusPoint);
                }  //X==X -Z
                if (pStart.getX() == pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    pEnd.setX(pEnd.getX() + radiusPoint);
                    pStart.setX(pStart.getX() + radiusPoint);
                }  //X==X +Z
                if (pStart.getX() < pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pEnd.getX() - pStart.getX(), pStart.getZ() - pEnd.getZ()).angle(pStart.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                    pEnd.setX(pEnd.getX() - cathet2);
                    pEnd.setZ(pEnd.getZ() - cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                    pEnd.setX(pEnd.getX() - cathet2);
                    pEnd.setZ(pEnd.getZ() + cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                    pEnd.setX(pEnd.getX() + cathet2);
                    pEnd.setZ(pEnd.getZ() + cathet1);
                }
                if (pStart.getX() < pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                    pEnd.setX(pEnd.getX() + cathet2);
                    pEnd.setZ(pEnd.getZ() - cathet1);
                }
            }  //G41
            if (isToolRadiusCompensation == 2) {
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() > pEnd.getX()) {
                    pEnd.setZ(pEnd.getZ() - radiusPoint);
                    pStart.setZ(pStart.getZ() - radiusPoint);
                }  //Z==Z -X
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() < pEnd.getX()) {
                    pEnd.setZ(pEnd.getZ() + radiusPoint);
                    pStart.setZ(pStart.getZ() + radiusPoint);
                }  //Z==Z +X
                if (pStart.getX() == pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    pEnd.setX(pEnd.getX() + radiusPoint);
                    pStart.setX(pStart.getX() + radiusPoint);
                }  //X==X -Z
                if (pStart.getX() == pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    pEnd.setX(pEnd.getX() - radiusPoint);
                    pStart.setX(pStart.getX() - radiusPoint);
                }  //X==X +Z
                if (pStart.getX() < pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pEnd.getX() - pStart.getX(), pStart.getZ() - pEnd.getZ()).angle(pStart.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                    pEnd.setX(pEnd.getX() + cathet2);
                    pEnd.setZ(pEnd.getZ() + cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                    pEnd.setX(pEnd.getX() + cathet2);
                    pEnd.setZ(pEnd.getZ() - cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                    pEnd.setX(pEnd.getX() - cathet2);
                    pEnd.setZ(pEnd.getZ() - cathet1);
                }
                if (pStart.getX() < pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pStart.setX(pStart.getX() - cathet1);
                    pStart.setZ(pStart.getZ() + cathet2);
                    pEnd.setX(pEnd.getX() - cathet1);
                    pEnd.setZ(pEnd.getZ() + cathet2);
                }
            }  //G42
        }

        if (frameList.get(index - 1).isAxisContains() && isToolRadiusCompensation != 0) {
            radiusPoint *= zoom;
            pEnd.setX(pEnd.getX() * zoom);
            pEnd.setZ(pEnd.getZ() * zoom);
            if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
            else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
            gc.setFill(color);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
            gc.setStroke(Color.GRAY);
            gc.setLineDashes();
            gc.setLineWidth(lineWidth);
            radiusPoint = 2;
            radiusPoint *= zoom;
            gc.strokeOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
        }
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

        if (pointStart.getX() > pointF.getX() && (pointStart.getZ() + pointF.getZ()) / 2 > pointEnd.getZ()) {
            clockwiseRND = true;
        }
        if (pointStart.getX() > pointF.getX() && (pointStart.getZ() + pointF.getZ()) / 2 < pointEnd.getZ()) {
            clockwiseRND = false;
        }
        if (pointStart.getX() < pointF.getX() && (pointStart.getZ() + pointF.getZ()) / 2 < pointEnd.getZ()) {
            clockwiseRND = true;
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
                case "G40":
                    isRapidFeed = true;
                    isToolRadiusCompensation = 0;
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
                case "G41":
                    isToolRadiusCompensation = 1;
                    break;
                case "G42":
                    isToolRadiusCompensation = 2;
                    break;
            }
        }
    }

    protected void correctionForOffn(List<Frame> frameList) {
        for (int i = 0; i < frameList.size(); i++) {
            if (frameList.get(i).getGCode().contains("G17") || frameList.get(i).getGCode().contains("G18"))
                isG17 = isG17(frameList.get(i).getGCode());
            checkGCode(frameList.get(i).getGCode());
            if (frameList.get(i).getIsCR() && frameList.get(i).isAxisContains() && frameList.get(i).getOffn() > 0) {   //drawArc                            //draw Arc
                toolRadiusCompensationArc(frameList, i, isToolRadiusCompensation, clockwise);
            }
            if (!frameList.get(i).getIsCR() && !frameList.get(i).isRND() && frameList.get(i).isAxisContains() && frameList.get(i).getOffn() > 0) {    //draw line
                toolRadiusCompensationLine(frameList, i, isToolRadiusCompensation);
            }
            if (frameList.get(i).isRND() && frameList.get(i).isAxisContains()) {                                    //draw RND
                pEnd.setX(frameList.get(i).getX());
                pEnd.setZ(frameList.get(i).getZ());

                pStart.setX(pEnd.getX());
                pStart.setZ(pEnd.getZ());
            }
        }
    }

    protected void toolRadiusCompensationArc(List<Frame> frameList, int numberLIne, int isToolRadiusCompensation, boolean clockwise) {
        pEnd.setX(frameList.get(numberLIne).getX());
        pEnd.setZ(frameList.get(numberLIne).getZ());
        double radius = frameList.get(numberLIne).getCr();
        double offn = frameList.get(numberLIne).getOffn();
        double chord = Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
        float h = (float) Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
        if (clockwise && frameList.get(numberLIne).getOffn() > 0) {
            double x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
            double z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
            if (isToolRadiusCompensation == 1) {
                double tempStartX = (x01 - pStart.getX()) * ((radius - offn) / radius);
                double tempStartZ = (z01 - pStart.getZ()) * ((radius - offn) / radius);
                double tempEndX = (x01 - pEnd.getX()) * ((radius - offn) / radius);
                double tempEndZ = (z01 - pEnd.getZ()) * ((radius - offn) / radius);
                frameList.get(numberLIne - 1).setX(pStart.getX() + (x01 - pStart.getX() - tempStartX));
                frameList.get(numberLIne - 1).setZ(pStart.getZ() + (z01 - pStart.getZ() - tempStartZ));
                frameList.get(numberLIne).setX(pEnd.getX() + (x01 - pEnd.getX() - tempEndX));
                frameList.get(numberLIne).setZ(pEnd.getZ() + (z01 - pEnd.getZ() - tempEndZ));
                frameList.get(numberLIne).setCr(radius - offn);
            }  //G41
            if (isToolRadiusCompensation == 2) {
                double tempStartX = (x01 - pStart.getX()) * ((radius + offn) / radius);
                double tempStartZ = (z01 - pStart.getZ()) * ((radius + offn) / radius);
                double tempEndX = (x01 - pEnd.getX()) * ((radius + offn) / radius);
                double tempEndZ = (z01 - pEnd.getZ()) * ((radius + offn) / radius);
                frameList.get(numberLIne - 1).setX(pStart.getX() + (x01 - pStart.getX() - tempStartX));
                frameList.get(numberLIne - 1).setZ(pStart.getZ() + (z01 - pStart.getZ() - tempStartZ));
                frameList.get(numberLIne).setX(pEnd.getX() + (x01 - pEnd.getX() - tempEndX));
                frameList.get(numberLIne).setZ(pEnd.getZ() + (z01 - pEnd.getZ() - tempEndZ));
                frameList.get(numberLIne).setCr(radius + offn);
            }  //G42
        }
        if (!clockwise && frameList.get(numberLIne).getOffn() > 0) {
            double x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
            double z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
            if (isToolRadiusCompensation == 1) {
                double tempStartX = (x02 - pStart.getX()) * ((radius + offn) / radius);
                double tempStartZ = (z02 - pStart.getZ()) * ((radius + offn) / radius);
                double tempEndX = (x02 - pEnd.getX()) * ((radius + offn) / radius);
                double tempEndZ = (z02 - pEnd.getZ()) * ((radius + offn) / radius);
                frameList.get(numberLIne - 1).setX(pStart.getX() + (x02 - pStart.getX() - tempStartX));
                frameList.get(numberLIne - 1).setZ(pStart.getZ() + (z02 - pStart.getZ() - tempStartZ));
                frameList.get(numberLIne).setX(pEnd.getX() + (x02 - pEnd.getX() - tempEndX));
                frameList.get(numberLIne).setZ(pEnd.getZ() + (z02 - pEnd.getZ() - tempEndZ));
                frameList.get(numberLIne).setCr(radius + offn);
            }  //G41
            if (isToolRadiusCompensation == 2) {
                double tempStartX = (x02 - pStart.getX()) * ((radius - offn) / radius);
                double tempStartZ = (z02 - pStart.getZ()) * ((radius - offn) / radius);
                double tempEndX = (x02 - pEnd.getX()) * ((radius - offn) / radius);
                double tempEndZ = (z02 - pEnd.getZ()) * ((radius - offn) / radius);
                frameList.get(numberLIne - 1).setX(pStart.getX() + (x02 - pStart.getX() - tempStartX));
                frameList.get(numberLIne - 1).setZ(pStart.getZ() + (z02 - pStart.getZ() - tempStartZ));
                frameList.get(numberLIne).setX(pEnd.getX() + (x02 - pEnd.getX() - tempEndX));
                frameList.get(numberLIne).setZ(pEnd.getZ() + (z02 - pEnd.getZ() - tempEndZ));
                frameList.get(numberLIne).setCr(radius - offn);
            }  //G42
        }
        pStart.setX(pEnd.getX());
        pStart.setZ(pEnd.getZ());
    }

    protected void toolRadiusCompensationLine(List<Frame> frameList, int numberLIne, int isToolRadiusCompensation) {
        double offn = frameList.get(numberLIne).getOffn();
        pEnd.setX(frameList.get(numberLIne).getX());
        pEnd.setZ(frameList.get(numberLIne).getZ());
        if (isToolRadiusCompensation == 1) {
            if (pStart.getZ() == pEnd.getZ() && pStart.getX() > pEnd.getX()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    if (frameList.get(numberLIne - 1).getZ() == frameList.get(numberLIne).getZ() && frameList.get(numberLIne - 1).getX() > frameList.get(numberLIne).getX()) {
                        frameList.get(numberLIne - 1).setX(pStart.getX() - offn);
                    } else frameList.get(numberLIne - 1).setX(pStart.getX());
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() + offn);
                }
                frameList.get(numberLIne).setX(pEnd.getX());
                frameList.get(numberLIne).setZ(pEnd.getZ() + offn);
            }  //Z==Z -X
            if (pStart.getZ() == pEnd.getZ() && pStart.getX() < pEnd.getX()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    if (frameList.get(numberLIne - 1).getZ() == frameList.get(numberLIne).getZ() && frameList.get(numberLIne - 1).getX() < frameList.get(numberLIne).getX()) {
                        frameList.get(numberLIne - 1).setX(pStart.getX() + offn);
                    } else frameList.get(numberLIne - 1).setX(pStart.getX());
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() - offn);
                }
                frameList.get(numberLIne).setX(pEnd.getX());
                frameList.get(numberLIne).setZ(pEnd.getZ() - offn);
            }  //Z==Z +X
            if (pStart.getX() == pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() - offn);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ());
                }
                frameList.get(numberLIne).setX(pEnd.getX() - offn);
                frameList.get(numberLIne).setZ(pEnd.getZ());
            }  //X==X -Z
            if (pStart.getX() == pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() + offn);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ());
                }
                frameList.get(numberLIne).setX(pEnd.getX() + offn);
                frameList.get(numberLIne).setZ(pEnd.getZ());
            }  //X==X +Z
            if (pStart.getX() < pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                double angle = new Point2D(pEnd.getX() - pStart.getX(), pStart.getZ() - pEnd.getZ()).angle(pStart.getX(), 0);
                angle = 180 - 90 - angle;
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() - cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() - cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() - cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() - cathet1);
            }
            if (pStart.getX() > pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                angle = 180 - 90 - angle;
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() - cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() + cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() - cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() + cathet1);
            }
            if (pStart.getX() > pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                angle = 180 - 90 - angle;
                System.out.println(angle);
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() + cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() + cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() + cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() + cathet1);
            }
            if (pStart.getX() < pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                angle = 180 - angle;
                System.out.println(angle);
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() + cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() - cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() + cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() - cathet1);
            }
        }  //G41
        if (isToolRadiusCompensation == 2) {
            if (pStart.getZ() == pEnd.getZ() && pStart.getX() > pEnd.getX()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX());
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() - offn);
                }
                frameList.get(numberLIne).setX(pEnd.getX());
                frameList.get(numberLIne).setZ(pEnd.getZ() - offn);
            }  //Z==Z -X
            if (pStart.getZ() == pEnd.getZ() && pStart.getX() < pEnd.getX()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX());
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() + offn);
                }
                frameList.get(numberLIne).setX(pEnd.getX());
                frameList.get(numberLIne).setZ(pEnd.getZ() + offn);
            }  //Z==Z +X
            if (pStart.getX() == pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() + offn);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ());
                }
                frameList.get(numberLIne).setX(pEnd.getX() + offn);
                frameList.get(numberLIne).setZ(pEnd.getZ());
            }  //X==X -Z
            if (pStart.getX() == pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() - offn);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ());
                }
                frameList.get(numberLIne).setX(pEnd.getX() - offn);
                frameList.get(numberLIne).setZ(pEnd.getZ());
            }  //X==X +Z
            if (pStart.getX() < pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                double angle = new Point2D(pEnd.getX() - pStart.getX(), pStart.getZ() - pEnd.getZ()).angle(pStart.getX(), 0);
                angle = 180 - 90 - angle;
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() + cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() + cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() + cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() + cathet1);
            }
            if (pStart.getX() > pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                angle = 180 - 90 - angle;
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() + cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() - cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() + cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() - cathet1);
            }
            if (pStart.getX() > pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                angle = 180 - 90 - angle;
                System.out.println(angle);
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() - cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() - cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() - cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() - cathet1);
            }
            if (pStart.getX() < pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                angle = 180 - angle;
                System.out.println(angle);
                double cathet1 = offn * Math.sin(Math.toRadians(angle));
                double cathet2 = Math.sqrt(offn * offn - cathet1 * cathet1);
                if (!containsG41G42(frameList.get(numberLIne).getGCode())) {
                    frameList.get(numberLIne - 1).setX(pStart.getX() - cathet2);
                    frameList.get(numberLIne - 1).setZ(pStart.getZ() + cathet1);
                }
                frameList.get(numberLIne).setX(pEnd.getX() - cathet2);
                frameList.get(numberLIne).setZ(pEnd.getZ() + cathet1);
            }
        }  //G42
        pStart.setX(pEnd.getX());
        pStart.setZ(pEnd.getZ());
    }

    protected boolean containsG41G42(List<String> gCodes) {
        final boolean[] b = {false};
        gCodes.stream()
                .filter(g -> g.equals("G41") || g.equals("G42"))
                .findAny()
                .ifPresent(g -> {
                    b[0] = true;
                });
        return b[0];
    }
}
