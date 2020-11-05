package com.sergey.pisarev.model;

public class Point {

    protected double x = 650f;

    protected double z = 250f;

    public Point() {

    }

    public Point(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
