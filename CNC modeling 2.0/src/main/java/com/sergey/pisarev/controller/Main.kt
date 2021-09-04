package com.sergey.pisarev.controller

import kotlin.Throws
import java.lang.Exception
import com.sergey.pisarev.controller.MainController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.util.Objects
import kotlin.jvm.JvmStatic

class Main : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        MainController.STAGE = primaryStage
        val root = FXMLLoader.load<Parent>(Objects.requireNonNull(javaClass.classLoader.getResource("main.fxml")))
        primaryStage.title = title
        primaryStage.scene = Scene(root, 1000.0, 600.0)
        primaryStage.minHeight = 600.0
        primaryStage.minWidth = 1000.0
        primaryStage.show()
    }

    companion object {
        var title = "CNC modeling 2.0"
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}