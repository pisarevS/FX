package com.sergey.pisarev.model;

import java.util.ArrayList;
import java.util.List;

public class Frame {

    private int id;
    private double x;
    private double z;
    private double cr;
    private double offn;
    private double rnd;
    private boolean isCR = false;
    private boolean isOffn = false;
    private boolean isRND = false;
    private boolean isAxisContains = false;
    private boolean isDiamon=false;
    private boolean isTool=false;
    private boolean isHome = false;
    private String tool;
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

    public double getOffn() {
        return offn;
    }

    public void setOffn(double offn) {
        this.offn = offn;
    }

    public boolean isOffn() {
        return isOffn;
    }

    public void setOffn(boolean offn) {
        isOffn = offn;
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

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public boolean isTool() {
        return isTool;
    }

    public void setTool(boolean tool) {
        isTool = tool;
    }

    public boolean getDiamon() {
        return isDiamon;
    }

    public void setDiamon(boolean diamon) {
        isDiamon = diamon;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    @Override
    public String toString() {
        return id + " " + gCode + " " + x + " " + z + " " + cr;
    }
}
