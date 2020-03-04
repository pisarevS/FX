package com.sergey.pisarev.controller;

import com.sun.jndi.toolkit.url.Uri;
import javafx.event.ActionEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
