package com.sergey.pisarev.interfaces;

import java.util.Map;

public interface IController {
    void onReset();

    void showProgram(String text);

    void showError(String error);

    void showFrame(int number);

    void getVariablesList(Map<String, String> variablesList);

    void getZooming(double zooming);

    void getCoordinateCanvas(double x,double z);
}
