package com.sergey.pisarev.model.base;

import com.sergey.pisarev.interfaces.IDraw;
import com.sergey.pisarev.model.Frame;
import com.sergey.pisarev.model.Point;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDraw {

    protected int clockwise=0;
    protected IDraw draw;
    protected int isRapidFeed=3;
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
    protected Map<String, Double> toolsMap=new HashMap<>();

    private void initToolMap(){
        //RQQ
        toolsMap.put("T=\"T01\"",16D);     //32мм черновой кривой
        toolsMap.put("T=\"T02\"",10D);     //20мм кривой чистовой
        toolsMap.put("T=\"T03\"",16D);     //32мм прямой черновой
        toolsMap.put("T=\"T04\"",16D);     //32мм прямой чистовой
        toolsMap.put("T=\"T05\"",3D);      //квадрат расрочка
        toolsMap.put("T=\"T09\"",1.2);     //ромб канавка
        toolsMap.put("T=\"T10\"",1.6);     //ромб поясок
        toolsMap.put("T=\"T11\"",3D);      //квадрат торцовка ступицы
        toolsMap.put("T=\"T20\"",10D);     //20мм прямой чистовой
        toolsMap.put("T=\"T99\"",1D);      //канавка фасон
        toolsMap.put("T=\"T77\"",1.6D);    //канавка сопля
        //KNUTH
        toolsMap.put("T125",12.5);         //25мм прямой черновой
        toolsMap.put("T20",10D);           //20мм кривой чистовой
        toolsMap.put("T51",1.6);           //ромб поясок
    }

    protected BaseDraw(IDraw draw) {
        this.draw = draw;
        initToolMap();
    }

    public BaseDraw() {
        initToolMap();
    }

    protected void drawArc(GraphicsContext gc, int isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, double radius, double zoom, int clockwise) {
        if (isRapidFeed==0) {
            gc.setStroke(colorLineDashes);
            gc.setLineDashes(3, 5);
            gc.setLineWidth(lineWidthDashes);
        } else if(isRapidFeed==1) {
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
        double chord = Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
        double sweetAngle = 2 * Math.asin(chord / (2 * radius)) * (180 / Math.PI);
        double h =  Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
        if (clockwise==2) {
            double x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
            double z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
            calculateStartAngle(gc, pointSystemCoordinate, radius, pStart, sweetAngle, x01, z01);
        }  if(clockwise==3) {
            double x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
            double z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
            calculateStartAngle(gc, pointSystemCoordinate, radius, pEnd, sweetAngle, x02, z02);
        }
    }

    private void calculateStartAngle(GraphicsContext gc, Point pointSystemCoordinate, double radius, Point pStart, double sweetAngle, double x01, double z01) {
        double cathet;
        double startAngle=0;
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
    }

    protected void drawLine(GraphicsContext gc, int isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, double zoom) {
        if (isRapidFeed==0) {
            gc.setStroke(colorLineDashes);
            gc.setLineDashes(3, 5);
            gc.setLineWidth(lineWidthDashes);
        } else if(isRapidFeed==1) {
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

    protected void drawPoint(GraphicsContext gc, Point pointSystemCoordinate, List<Frame> frameList, double zoom, Color color, int index,String tool) {
        double radiusPoint=6;
        for (String toolStr:toolsMap.keySet()){
            if(tool.equals(toolStr))
                radiusPoint=toolsMap.get(toolStr);
        }
        pEnd=toolRadiusCompensationPoint(frameList,radiusPoint,index);

        if (frameList.get(index - 1).isAxisContains() && isToolRadiusCompensation != 0&&radiusPoint==10||radiusPoint==16||radiusPoint==6||radiusPoint==12.5) {
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
            radiusPoint = radiusPoint/3;
            gc.strokeOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
        }
        if (frameList.get(index - 1).isAxisContains() && isToolRadiusCompensation != 0&&radiusPoint==3) {
            radiusPoint *= zoom;
            pEnd.setX(pEnd.getX() * zoom);
            pEnd.setZ(pEnd.getZ() * zoom);
            if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
            else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
            gc.setFill(color);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint+(18.352591*zoom), pEnd.getZ() - radiusPoint-(4.917562*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint+(13.435029*zoom), pEnd.getZ() - radiusPoint-(23.270153*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(4.917562*zoom), pEnd.getZ() - radiusPoint-(18.352591*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.beginPath();
            gc.moveTo(pointSystemCoordinate.getX() + pEnd.getX() -(2.897777*zoom), pEnd.getZ() +(0.776457*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(0.776457*zoom), pEnd.getZ() +(2.897777*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(19.129048*zoom), pEnd.getZ() -(2.019784*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(21.250368*zoom), pEnd.getZ() -(5.694019*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(16.332806*zoom), pEnd.getZ() -(24.046610*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(12.658572*zoom), pEnd.getZ() -(26.167930*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(5.694019*zoom), pEnd.getZ() -(21.250368*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(7.815339*zoom), pEnd.getZ() -(17.576134*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(2.897777*zoom), pEnd.getZ() +(0.776457*zoom));
            gc.fill();
            gc.setStroke(Color.GRAY);
            radiusPoint=3.5;
            radiusPoint*=zoom;
            gc.strokeOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint+(6.717514*zoom), pEnd.getZ() - radiusPoint-(11.635076*zoom), radiusPoint * 2, radiusPoint * 2);
        }
        if (frameList.get(index - 1).isAxisContains() && isToolRadiusCompensation != 0&&radiusPoint==1.6) {
            radiusPoint *= zoom;
            pEnd.setX(pEnd.getX() * zoom);
            pEnd.setZ(pEnd.getZ() * zoom);
            if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
            else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
            gc.setFill(color);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(1.132803*zoom), pEnd.getZ() - radiusPoint-(12.948001*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(13.834918*zoom), pEnd.getZ() - radiusPoint-(14.059292*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(12.702115*zoom), pEnd.getZ() - radiusPoint-(1.111291*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.beginPath();
            gc.moveTo(pointSystemCoordinate.getX() + pEnd.getX() -(0.139449*zoom), pEnd.getZ() +(1.593912*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(1.593912*zoom), pEnd.getZ() -(0.139449*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(0.461108*zoom), pEnd.getZ() -(13.087451*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(0.993354*zoom), pEnd.getZ() -(14.541913*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(13.695469*zoom), pEnd.getZ() -(15.653204*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(15.42883*zoom), pEnd.getZ() -(13.919843*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(14.296026*zoom), pEnd.getZ() -(0.971842*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(12.841564*zoom), pEnd.getZ() +(0.48262*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(0.139449*zoom), pEnd.getZ() +(1.593912*zoom));
            gc.fill();
            gc.setStroke(Color.GRAY);
            radiusPoint=2.5;
            radiusPoint*=zoom;
            gc.strokeOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(6.917459*zoom), pEnd.getZ() - radiusPoint-(7.029646*zoom), radiusPoint * 2, radiusPoint * 2);
        }
        if (frameList.get(index - 1).isAxisContains() && isToolRadiusCompensation != 0&&radiusPoint==1.2) {
            radiusPoint *= zoom;
            pEnd.setX(pEnd.getX() * zoom);
            pEnd.setZ(pEnd.getZ() * zoom);
            if (pEnd.getZ() > 0) pEnd.setZ(pointSystemCoordinate.getZ() - pEnd.getZ());
            else pEnd.setZ(pointSystemCoordinate.getZ() + Math.abs(pEnd.getZ()));
            gc.setFill(color);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint, pEnd.getZ() - radiusPoint, radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(7.892323*zoom), pEnd.getZ() - radiusPoint-(21.099913*zoom), radiusPoint * 2, radiusPoint * 2);
            radiusPoint=2;
            radiusPoint*=zoom;
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(6.488786*zoom), pEnd.getZ() - radiusPoint-(9.604683*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.fillOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(1.403537*zoom), pEnd.getZ() - radiusPoint-(11.49523*zoom), radiusPoint * 2, radiusPoint * 2);
            gc.beginPath();
            gc.moveTo(pointSystemCoordinate.getX() + pEnd.getX() -(0.945613*zoom), pEnd.getZ() +(0.738794*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(1.198355*zoom), pEnd.getZ() -(0.062803*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(0.593722*zoom), pEnd.getZ() -(11.599902*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() +(0.172484*zoom), pEnd.getZ() -(12.726553*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(6.94671*zoom), pEnd.getZ() -(21.838706*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(9.090679*zoom), pEnd.getZ() -(21.037109*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(8.486045*zoom), pEnd.getZ() -(9.500011*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(8.064807*zoom), pEnd.getZ() -(8.37336*zoom));
            gc.lineTo(pointSystemCoordinate.getX() + pEnd.getX() -(0.945613*zoom), pEnd.getZ() +(0.738794*zoom));
            gc.fill();
            gc.setStroke(Color.GRAY);
            radiusPoint=2.5;
            radiusPoint*=zoom;
            gc.strokeOval(pointSystemCoordinate.getX() + pEnd.getX() - radiusPoint-(3.946162*zoom), pEnd.getZ() - radiusPoint-(10.549956*zoom), radiusPoint * 2, radiusPoint * 2);
        }
    }

    protected void drawRND(GraphicsContext gc, int isRapidFeed, Point pointSystemCoordinate, Point pointStart, Point pointEnd, Point pointF, double radiusRND, double zoom) {
        Point pointStartCR = new Point();
        Point pointEndCR = new Point();
        double differenceX;
        double differenceZ;
        double cathet;
        int clockwiseRND = 3;
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
            clockwiseRND = 2;
        }
        if (pointStart.getX() > pointF.getX() && (pointStart.getZ() + pointF.getZ()) / 2 < pointEnd.getZ()) {
            clockwiseRND = 3;
        }
        if (pointStart.getX() < pointF.getX() && (pointStart.getZ() + pointF.getZ()) / 2 < pointEnd.getZ()) {
            clockwiseRND = 2;
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
                    isRapidFeed = 0;
                    isToolRadiusCompensation = 0;
                    clockwise=0;
                    break;
                case "G1":
                case "G01":
                    isRapidFeed = 1;
                    clockwise=0;
                    break;
                case "G2":
                case "G02":
                    if(isG17) clockwise =2;
                    else clockwise=3;
                    break;
                case "G3":
                case "G03":
                    if(isG17) clockwise = 3;
                    else clockwise=2;
                    break;
                case "G41":
                    if (!isG17) isToolRadiusCompensation = 1;
                    else isToolRadiusCompensation = 2;
                    break;
                case "G42":
                    if (!isG17) isToolRadiusCompensation = 2;
                    else isToolRadiusCompensation = 1;
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
                toolRadiusCompensationArcOffn(frameList, i, isToolRadiusCompensation, clockwise);
            }
            if (!frameList.get(i).getIsCR() && !frameList.get(i).isRND() && frameList.get(i).isAxisContains() && frameList.get(i).getOffn() > 0) {    //draw line
                toolRadiusCompensationLineOffn(frameList, i, isToolRadiusCompensation);
            }
            if (frameList.get(i).isRND() && frameList.get(i).isAxisContains()) {                                    //draw RND
                pEnd.setX(frameList.get(i).getX());
                pEnd.setZ(frameList.get(i).getZ());

                pStart.setX(pEnd.getX());
                pStart.setZ(pEnd.getZ());
            }
        }
    }

    protected void toolRadiusCompensationArcOffn(List<Frame> frameList, int numberLIne, int isToolRadiusCompensation, int clockwise) {
        pEnd.setX(frameList.get(numberLIne).getX());
        pEnd.setZ(frameList.get(numberLIne).getZ());
        double radius = frameList.get(numberLIne).getCr();
        double offn = frameList.get(numberLIne).getOffn();
        double chord = Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
        double h =  Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
        if(Double.isNaN(h)) h=0;
        if(Double.isNaN(chord)) h=0;
        if (clockwise==2 && frameList.get(numberLIne).getOffn() > 0) {
            double x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
            double z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
            if (isToolRadiusCompensation == 1) {
                calculateToolRadiusCompensationСounterclockwise(frameList, numberLIne, radius, offn, x01, z01);
            }  //G41
            if (isToolRadiusCompensation == 2) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x01, z01);
            }  //G42
        }
        if (clockwise==3 && frameList.get(numberLIne).getOffn() > 0) {
            double x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
            double z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
            if (isToolRadiusCompensation == 1) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x02, z02);
            }  //G41
            if (isToolRadiusCompensation == 2) {
                calculateToolRadiusCompensationСounterclockwise(frameList, numberLIne, radius, offn, x02, z02);
            }  //G42
        }
        pStart.setX(pEnd.getX());
        pStart.setZ(pEnd.getZ());
    }

    private void calculateToolRadiusCompensationClockwise(List<Frame> frameList, int numberLIne, double radius, double offn, double x01, double z01) {
        double tempStartX = (x01 - pStart.getX()) * ((radius + offn) / radius);
        double tempStartZ = (z01 - pStart.getZ()) * ((radius + offn) / radius);
        double tempEndX = (x01 - pEnd.getX()) * ((radius + offn) / radius);
        double tempEndZ = (z01 - pEnd.getZ()) * ((radius + offn) / radius);
        frameList.get(numberLIne - 1).setX(pStart.getX() + (x01 - pStart.getX() - tempStartX));
        frameList.get(numberLIne - 1).setZ(pStart.getZ() + (z01 - pStart.getZ() - tempStartZ));
        frameList.get(numberLIne).setX(pEnd.getX() + (x01 - pEnd.getX() - tempEndX));
        frameList.get(numberLIne).setZ(pEnd.getZ() + (z01 - pEnd.getZ() - tempEndZ));
        frameList.get(numberLIne).setCr(radius + offn);
    }

    private void calculateToolRadiusCompensationСounterclockwise(List<Frame> frameList, int numberLIne, double radius, double offn, double x01, double z01) {
        double tempStartX = (x01 - pStart.getX()) * ((radius - offn) / radius);
        double tempStartZ = (z01 - pStart.getZ()) * ((radius - offn) / radius);
        double tempEndX = (x01 - pEnd.getX()) * ((radius - offn) / radius);
        double tempEndZ = (z01 - pEnd.getZ()) * ((radius - offn) / radius);
        frameList.get(numberLIne - 1).setX(pStart.getX() + (x01 - pStart.getX() - tempStartX));
        frameList.get(numberLIne - 1).setZ(pStart.getZ() + (z01 - pStart.getZ() - tempStartZ));
        frameList.get(numberLIne).setX(pEnd.getX() + (x01 - pEnd.getX() - tempEndX));
        frameList.get(numberLIne).setZ(pEnd.getZ() + (z01 - pEnd.getZ() - tempEndZ));
        frameList.get(numberLIne).setCr(radius - offn);
    }

    protected void toolRadiusCompensationLineOffn(List<Frame> frameList, int numberLIne, int isToolRadiusCompensation) {
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
                .ifPresent(g -> b[0] = true);
        return b[0];
    }

    private Point toolRadiusCompensationPoint(List<Frame> frameList,double radiusPoint, int index){
        if (index == 1) index++;
        Point pStart;
        Point pEnd;
        if (containsG41G42(frameList.get(index - 1).getGCode())) {
            pStart = new Point(frameList.get(index - 1).getX(), frameList.get(index - 1).getZ());
            pEnd = new Point(frameList.get(index).getX(), frameList.get(index).getZ());
            checkGCode(frameList.get(index).getGCode());
        } else {
            pStart = new Point(frameList.get(index - 2).getX(), frameList.get(index - 2).getZ());
            pEnd = new Point(frameList.get(index - 1).getX(), frameList.get(index - 1).getZ());
            checkGCode(frameList.get(index - 1).getGCode());
        }

        if (frameList.get(index).getIsCR() && frameList.get(index).isAxisContains() && containsG41G42(frameList.get(index - 1).getGCode())) {
            double radius = frameList.get(index).getCr();
            calculateChord(radiusPoint, pStart, pEnd, radius, pStart.getX(), pStart.getZ());
        } else if (frameList.get(index - 1).getIsCR() && frameList.get(index - 1).isAxisContains() && !containsG41G42(frameList.get(index - 1).getGCode())) {
            double radius = frameList.get(index - 1).getCr();
            calculateChord(radiusPoint, pStart, pEnd, radius, pEnd.getX(), pEnd.getZ());
        }

        if (!frameList.get(index).getIsCR() &&  frameList.get(index).isAxisContains() && containsG41G42(frameList.get(index - 1).getGCode())) {
            if (isToolRadiusCompensation == 1) {
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() > pEnd.getX()) {
                    pEnd.setX(pStart.getX());
                    pEnd.setZ(pStart.getZ() + radiusPoint);
                    pStart.setX(pStart.getX() );
                    pStart.setZ(pStart.getZ() + radiusPoint);
                }  //Z==Z -X
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() < pEnd.getX()) {
                    pEnd.setX(pStart.getX());
                    pEnd.setZ(pStart.getZ() - radiusPoint);
                    pStart.setX(pStart.getX() );
                    pStart.setZ(pStart.getZ() - radiusPoint);
                }  //Z==Z +X
                if (pStart.getX() == pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    pEnd.setX(pStart.getX() - radiusPoint);
                    pEnd.setZ(pStart.getZ());
                    pStart.setX(pStart.getX() - radiusPoint);
                    pStart.setZ(pStart.getZ() );
                }  //X==X -Z
                if (pStart.getX() == pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    pEnd.setX(pStart.getX() + radiusPoint);
                    pEnd.setZ(pStart.getZ() );
                    pStart.setX(pStart.getX() + radiusPoint);
                    pStart.setZ(pStart.getZ() );
                }  //X==X +Z
                if (pStart.getX() < pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pEnd.getX() - pStart.getX(), pStart.getZ() - pEnd.getZ()).angle(pStart.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() - cathet2);
                    pEnd.setZ(pStart.getZ() - cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() - cathet2);
                    pEnd.setZ(pStart.getZ() + cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() + cathet2);
                    pEnd.setZ(pStart.getZ() + cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                }
                if (pStart.getX() < pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() + cathet2);
                    pEnd.setZ(pStart.getZ() - cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                }
            }  //G41
            if (isToolRadiusCompensation == 2) {
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() > pEnd.getX()) {
                    pEnd.setX(pStart.getX());
                    pEnd.setZ(pStart.getZ() - radiusPoint);
                    pStart.setX(pStart.getX() );
                    pStart.setZ(pStart.getZ() - radiusPoint);
                }  //Z==Z -X
                if (pStart.getZ() == pEnd.getZ() && pStart.getX() < pEnd.getX()) {
                    pEnd.setX(pStart.getX());
                    pEnd.setZ(pStart.getZ() + radiusPoint);
                    pStart.setX(pStart.getX() );
                    pStart.setZ(pStart.getZ() + radiusPoint);
                }  //Z==Z +X
                if (pStart.getX() == pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    pEnd.setX(pStart.getX() + radiusPoint);
                    pEnd.setZ(pStart.getZ());
                    pStart.setX(pStart.getX() + radiusPoint);
                    pStart.setZ(pStart.getZ() );
                }  //X==X -Z
                if (pStart.getX() == pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    pEnd.setX(pStart.getX() - radiusPoint);
                    pEnd.setZ(pStart.getZ() );
                    pStart.setX(pStart.getX() - radiusPoint);
                    pStart.setZ(pStart.getZ() );
                }  //X==X +Z
                if (pStart.getX() < pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pEnd.getX() - pStart.getX(), pStart.getZ() - pEnd.getZ()).angle(pStart.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() + cathet2);
                    pEnd.setZ(pStart.getZ() + cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() > pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() + cathet2);
                    pEnd.setZ(pStart.getZ() - cathet1);
                    pStart.setX(pStart.getX() + cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                }
                if (pStart.getX() > pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - 90 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() - cathet2);
                    pEnd.setZ(pStart.getZ() - cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() - cathet1);
                }
                if (pStart.getX() < pEnd.getX() && pStart.getZ() < pEnd.getZ()) {
                    double angle = new Point2D(pStart.getX() - pEnd.getX(), pStart.getZ() - pEnd.getZ()).angle(pEnd.getX(), 0);
                    angle = 180 - angle;
                    double cathet1 = radiusPoint * Math.sin(Math.toRadians(angle));
                    double cathet2 = Math.sqrt(radiusPoint * radiusPoint - cathet1 * cathet1);
                    pEnd.setX(pStart.getX() - cathet2);
                    pEnd.setZ(pStart.getZ() + cathet1);
                    pStart.setX(pStart.getX() - cathet2);
                    pStart.setZ(pStart.getZ() + cathet1);
                }
            }  //G42
        } else if (!frameList.get(index - 1).getIsCR() && frameList.get(index - 1).isAxisContains() && !containsG41G42(frameList.get(index - 1).getGCode())) {    //draw line
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
                    if (frameList.get(index-1).getX()==frameList.get(index).getX()&&!frameList.get(index).getGCode().contains("G0")) {
                        pEnd.setX(pEnd.getX() - radiusPoint);
                        pStart.setZ(pEnd.getZ()+radiusPoint);
                    }else {
                        pEnd.setZ(pEnd.getZ() + radiusPoint);
                        pStart.setZ(pStart.getZ() + radiusPoint);
                    }
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

        return pEnd;
    }

    private void calculateChord(double radiusPoint, Point pStart, Point pEnd, double radius, double x, double z) {
        double chord = Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
        float h = (float) Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
        if (clockwise==2) {
            double x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
            double z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
            if (isToolRadiusCompensation == 1) {
                double tempEndX = (x01 - x) * ((radius - radiusPoint) / radius);
                double tempEndZ = (z01 - z) * ((radius - radiusPoint) / radius);
                pEnd.setX(x + (x01 - x - tempEndX));
                pEnd.setZ(z + (z01 - z - tempEndZ));
            }  //G41
            if (isToolRadiusCompensation == 2) {
                double tempEndX = (x01 - x) * ((radius + radiusPoint) / radius);
                double tempEndZ = (z01 - z) * ((radius + radiusPoint) / radius);
                pEnd.setX(x + (x01 - x - tempEndX));
                pEnd.setZ(z + (z01 - z - tempEndZ));
            }  //G42
        }
        if (clockwise==3) {
            double x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
            double z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
            if (isToolRadiusCompensation == 1) {
                double tempEndX = (x02 - x) * ((radius + radiusPoint) / radius);
                double tempEndZ = (z02 - z) * ((radius + radiusPoint) / radius);
                pEnd.setX(x + (x02 - x - tempEndX));
                pEnd.setZ(z + (z02 - z - tempEndZ));
            }  //G41
            if (isToolRadiusCompensation == 2) {
                double tempEndX = (x02 - x) * ((radius - radiusPoint) / radius);
                double tempEndZ = (z02 - z) * ((radius - radiusPoint) / radius);
                pEnd.setX(x + (x02 - x - tempEndX));
                pEnd.setZ(z + (z02 - z - tempEndZ));
            }  //G42
        }
    }
}
