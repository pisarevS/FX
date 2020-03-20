package com.sergey.pisarev.controller;

import javafx.event.ActionEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class AboutController {


    public void onAction(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/pisarevS").toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
