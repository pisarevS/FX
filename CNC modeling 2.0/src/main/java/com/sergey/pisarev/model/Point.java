package com.sergey.pisarev.model;

public class Point {


    private float x;

    private float z;

    public Point() {

    }

    public Point(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
