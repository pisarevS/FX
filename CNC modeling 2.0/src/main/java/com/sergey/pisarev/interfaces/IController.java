package com.sergey.pisarev.interfaces;

public interface IController {
    void onReset();
    void showProgram(String text);
    void showParameter(String text);
    void showError(String error);
    void showFrame(int number);

}
