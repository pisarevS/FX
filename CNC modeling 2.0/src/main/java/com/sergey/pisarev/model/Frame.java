package com.sergey.pisarev.model;

import java.util.ArrayList;
import java.util.List;

public class Frame {

    private int id;
    private double x;
    private double z;
    private double cr;
    private double rnd;
    private boolean isCR = false;
    private boolean isRND = false;
    private boolean isAxisContains = false;
    private List<String> gCode = new ArrayList<>();

    public boolean isAxisContains() {
        return isAxisContains;
    }

    public double getRnd() {
        return rnd;
    }

    public void setRnd(double rnd) {
        this.rnd = rnd;
    }

    public boolean isRND() {
        return isRND;
    }

    public void setRND(boolean RND) {
        isRND = RND;
    }

    public void setAxisContains(boolean axisContains) {
        isAxisContains = axisContains;
    }

    public void setGCode(List<String> gCode) {
        this.gCode = gCode;
    }

    public List<String> getGCode() {
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

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }

    public double getCr() {
        return cr;
    }

    @Override
    public String toString() {
        return id + " " + gCode + " " + x + " " + z + " " + cr;
    }
}
