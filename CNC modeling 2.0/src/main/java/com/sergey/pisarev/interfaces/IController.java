package com.sergey.pisarev.interfaces;

public interface IController {
    void onStop();

    void showProgram(String text);

    void showFrame(String frame);

    void showError(String error);

    void showCaretBoxOnCycleStart(int number, StringBuffer frame);

    void setZooming(double zooming);

    void getCoordinateCanvas(double x,double z);

    void getCoordinateFrame(double x,double z);

    void showCaretBoxOnCanvasClick(int number, StringBuffer frame);

    void showSaveAlert();
}
