package com.sergey.pisarev.model;

import java.util.ArrayList;

public class Frame {

    private int id;
    private float x;
    private float z;
    private float cr;
    private float offn;
    private boolean isCR = false;
    private boolean isAxisContains = false;
    private ArrayList<String> gCode = new ArrayList<>();

    public boolean isAxisContains() {
        return isAxisContains;
    }

    public float getOffn() {
        return offn;
    }

    public void setOffn(float offn) {
        this.offn = offn;
    }

    public void setAxisContains(boolean axisContains) {
        isAxisContains = axisContains;
    }

    public void setGCode(ArrayList<String> gCode) {
        this.gCode = gCode;
    }

    public ArrayList<String> getGCode() {
        return gCode;
    }

    public void setIsCR(boolean isCR) {
        this.isCR = isCR;
    }

    public boolean getIsCR() {
        return isCR;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getZ() {
        return z;
    }

    public void setCr(float cr) {
        this.cr = cr;
    }

    public float getCr() {
        return cr;
    }

    @Override
    public String toString() {
        String frame = id + " " + gCode + " " + x + " " + z + " " + cr + " " + offn;
        return frame;
    }
}
