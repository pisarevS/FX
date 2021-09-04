package com.sergey.pisarev.model

import javafx.beans.value.ObservableValue
import javafx.scene.canvas.Canvas

class ResizableCanvas : Canvas() {
    var gc = graphicsContext2D
    var canvasWidth = 0
    var canvasHeight = 0
    var halfCanvasHeight = 0
    override fun minHeight(width: Double): Double {
        return 1.0
    }

    override fun maxHeight(width: Double): Double {
        return Double.MAX_VALUE
    }

    override fun prefHeight(width: Double): Double {
        return minHeight(width)
    }

    override fun minWidth(height: Double): Double {
        return 1.0
    }

    override fun maxWidth(height: Double): Double {
        return Double.MAX_VALUE
    }

    override fun isResizable(): Boolean {
        return true
    }

    override fun resize(width: Double, height: Double) {
        super.setWidth(width)
        super.setHeight(height)
    }

    /**
     * Constructor
     */
    init {
        // if i didn't add the draw to the @Override resize(double width, double
        // height) then it must be into the below listeners

        // Redraw canvas when size changes.
        widthProperty().addListener { observable: ObservableValue<out Number?>?, oldValue: Number?, newValue: Number? ->
            //System.out.println("Entered WIDTH property");
            canvasWidth = widthProperty().get().toInt()
        }
        heightProperty().addListener { observable: ObservableValue<out Number?>?, oldValue: Number?, newValue: Number? ->
            //System.out.println("Entered HEIGHT property");
            canvasHeight = heightProperty().get().toInt()
            halfCanvasHeight = canvasHeight shr 1
        }
    }
}