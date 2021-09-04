package com.sergey.pisarev.controller

import java.awt.Desktop
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL

class AboutController {
    fun onAction() {
        try {
            Desktop.getDesktop().browse(URL("https://github.com/pisarevS").toURI())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }
}