package com.sergey.pisarev.model;

public class Point {

    protected double x = Constant.N_GANTRYPOS_X;

    protected double z = Constant.N_GANTRYPOS_Z;

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
