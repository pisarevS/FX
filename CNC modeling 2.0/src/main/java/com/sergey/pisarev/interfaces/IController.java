package com.sergey.pisarev.interfaces;

public interface IController {
    void onReset();

    void showProgram(String text);

    void showError(String error);

    void showCaretBoxOnCycleStart(int number, StringBuffer frame);

    void setZooming(double zooming);

    void getCoordinateCanvas(double x,double z);

    void showCaretBoxOnCanvasClick(int number, StringBuffer frame);
}
