package com.sergey.pisarev.interfaces

import com.sergey.pisarev.model.MyData
import com.sergey.pisarev.model.Point
import javafx.scene.canvas.GraphicsContext

interface Drawing {
    fun drawContour(data: MyData, gc: GraphicsContext, pointCoordinateZero: Point, zoom: Double, index: Int, isSelectToolRadius: Boolean)
    fun setNumberLine(numberLine: Int)
}