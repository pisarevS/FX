package com.sergey.pisarev.controller;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class AboutController {

    public void onAction() {
        try {
            Desktop.getDesktop().browse(new URL("https://github.com/pisarevS").toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
