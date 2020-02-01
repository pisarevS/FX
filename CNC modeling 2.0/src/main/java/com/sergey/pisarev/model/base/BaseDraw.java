package java.com.sergey.pisarev.model.base;

import java.com.sergey.pisarev.model.Frame;
import java.com.sergey.pisarev.model.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDraw {

   /* private Paint paintFullLine;
    private Paint paintDottedLine;
    protected boolean clockwise;
    protected Paint line;
    private List<StringBuffer> programList;
    protected ArrayList<Frame> frameList;
    protected IDraw draw;
    protected MyData data;

    protected BaseDraw(IDraw draw) {
        this.draw = draw;
        this.data = data;
        init();
    }

    private void init() {

        line = new Paint();
        paintFullLine = new Paint();
        paintFullLine.setColor(Color.GREEN);
        paintFullLine.setStyle(Paint.Style.STROKE);
        paintFullLine.setAntiAlias(true);
        paintDottedLine = new Paint();
        paintDottedLine.setColor(Color.GRAY);
        paintDottedLine.setStyle(Paint.Style.STROKE);
        paintDottedLine.setAntiAlias(true);
        paintDottedLine.setPathEffect(new DashPathEffect(new float[]{12f, 7f}, 0f));
    }

    public abstract void drawContour(Canvas canvas, Point pointCoordinateZero, float zoom, int index);

    protected void drawLine(Canvas canvas, Paint paint, Point pointCoordinateZero, Point pointStart, Point pointEnd, float zoom) {
        Path path = new Path();
        Point pStart = new Point(pointStart.getX(), pointStart.getZ());
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        pStart.setX(pStart.getX() * zoom);
        pStart.setZ(pStart.getZ() * zoom);
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        if (pStart.getZ() > 0) pStart.setZ(pointCoordinateZero.getZ() - pStart.getZ());
        else pStart.setZ(pointCoordinateZero.getZ() + Math.abs(pStart.getZ()));
        if (pEnd.getZ() > 0) pEnd.setZ(pointCoordinateZero.getZ() - pEnd.getZ());
        else pEnd.setZ(pointCoordinateZero.getZ() + Math.abs(pEnd.getZ()));
        path.moveTo(pointCoordinateZero.getX() + pStart.getX(), pStart.getZ());
        path.lineTo(pointCoordinateZero.getX() + pEnd.getX(), pEnd.getZ());
        canvas.drawPath(path, paint);
    }

    protected void drawArc(Canvas canvas, Paint paint, Point pointCoordinateZero, Point pointStart, Point pointEnd, float radius, float zoom, boolean clockwise) {
        Path path = new Path();
        Point pStart = new Point(pointStart.getX(), pointStart.getZ());
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        RectF rectF = new RectF();
        pStart.setX(pStart.getX() * zoom);
        pStart.setZ(pStart.getZ() * zoom);
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        radius *= zoom;
        float startAngle = 0, sweetAngle, cathetus;
        float chord = (float) Math.sqrt(Math.pow(pStart.getX() - pEnd.getX(), 2) + Math.pow(pStart.getZ() - pEnd.getZ(), 2));
        float h = (float) Math.sqrt(radius * radius - (chord / 2) * (chord / 2));
        if (clockwise) {
            float x01 = (pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 + h * (pEnd.getZ() - pStart.getZ()) / chord);
            float z01 = (pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 - h * (pEnd.getX() - pStart.getX()) / chord);
            if (pStart.getX() > x01 && pStart.getZ() >= z01) {
                cathetus = pStart.getX() - x01;
                if (pStart.getZ() == z01)
                    startAngle = 0;
                else startAngle = (float) (360 - Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            if (pStart.getX() >= x01 && pStart.getZ() < z01) {
                cathetus = pStart.getX() - x01;
                if (pStart.getX() == x01)
                    startAngle = 90;
                else startAngle = (float) (Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            if (pStart.getX() < x01 && pStart.getZ() <= z01) {
                cathetus = x01 - pStart.getX();
                if (pStart.getZ() == z01)
                    startAngle = 180;
                else startAngle = (float) (180 - Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            if (pStart.getX() <= x01 && pStart.getZ() > z01) {
                cathetus = x01 - pStart.getX();
                if (pStart.getX() == x01)
                    startAngle = 270;
                else startAngle = (float) (180 + Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            rectF.set(pointCoordinateZero.getX() + x01 - radius, pointCoordinateZero.getZ() - z01 - radius, pointCoordinateZero.getX() + x01 + radius, pointCoordinateZero.getZ() - z01 + radius);
        } else {
            float x02 = pStart.getX() + (pEnd.getX() - pStart.getX()) / 2 - h * (pEnd.getZ() - pStart.getZ()) / chord;
            float z02 = pStart.getZ() + (pEnd.getZ() - pStart.getZ()) / 2 + h * (pEnd.getX() - pStart.getX()) / chord;
            if (pEnd.getX() > x02 && pEnd.getZ() >= z02) {
                cathetus = pEnd.getX() - x02;
                if (pEnd.getZ() == z02)
                    startAngle = 0;
                else startAngle = (float) (360 - Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            if (pEnd.getX() >= x02 && pEnd.getZ() < z02) {
                cathetus = pEnd.getX() - x02;
                if (pEnd.getX() == x02)
                    startAngle = 90;
                else startAngle = (float) (Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            if (pEnd.getX() < x02 && pEnd.getZ() <= z02) {
                cathetus = x02 - pEnd.getX();
                if (pEnd.getZ() == z02)
                    startAngle = 180;
                else startAngle = (float) (180 - Math.acos(cathetus / radius) * (180 / Math.PI));
            }

            if (pEnd.getX() <= x02 && pEnd.getZ() > z02) {
                cathetus = x02 - pEnd.getX();
                if (pEnd.getX() == x02)
                    startAngle = 270;
                else startAngle = (float) (180 + Math.acos(cathetus / radius) * (180 / Math.PI));
            }
            rectF.set(pointCoordinateZero.getX() + x02 - radius, pointCoordinateZero.getZ() - z02 - radius, pointCoordinateZero.getX() + x02 + radius, pointCoordinateZero.getZ() - z02 + radius);
        }
        sweetAngle = (float) (2 * Math.asin(chord / (2 * radius)) * (180 / Math.PI));
        path.addArc(rectF, startAngle, sweetAngle);
        canvas.drawPath(path, paint);
    }

    protected void drawPoint(Canvas canvas, Point pointCoordinateZero, Point pointEnd, float zoom) {
        float radiusPoint = 7F;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        Path path = new Path();
        Point pEnd = new Point(pointEnd.getX(), pointEnd.getZ());
        pEnd.setX(pEnd.getX() * zoom);
        pEnd.setZ(pEnd.getZ() * zoom);
        if (pEnd.getZ() > 0) pEnd.setZ(pointCoordinateZero.getZ() - pEnd.getZ());
        else pEnd.setZ(pointCoordinateZero.getZ() + Math.abs(pEnd.getZ()));
        path.addCircle(pointCoordinateZero.getX() + pEnd.getX(), pEnd.getZ(), radiusPoint, Path.Direction.CW);
        canvas.drawPath(path, paint);
    }

    private boolean isG17(ArrayList<StringBuffer> programList) {
        for (int i = 0; i < programList.size(); i++)
            if (programList.get(i).toString().contains("G17")) {
                return true;
            }
        return false;
    }

    protected void checkGCode(ArrayList<String> gCodeList) {
        boolean isG17 = isG17(programList);
        for (String gCode : gCodeList) {
            switch (gCode) {
                case "G0":
                case "G00":
                    line = paintDottedLine;
                    break;
                case "G1":
                case "G01":
                    line = paintFullLine;
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
    }*/
}
