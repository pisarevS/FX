package com.sergey.pisarev.model;

public class Rect extends Point {
    double height;
    double width;

    public Rect(){}

    public Rect(Point point, double height, double width) {
        x = point.getX();
        z = point.getZ();
        this.height = height;
        this.width = width;
    }

    public void setRect(Point point, double height, double width){
        x = point.getX();
        z = point.getZ();
        this.height = height;
        this.width = width;
    }

    public void setRect(double x,double z, double height, double width){
        this.x = x;
        this.z = z;
        this.height = height;
        this.width = width;
    }

    public boolean isInsideRect(double x, double z) {
        return (x >= this.x && x <= this.x + width) && (z >= this.z && z <= this.z + height);
    }
}
