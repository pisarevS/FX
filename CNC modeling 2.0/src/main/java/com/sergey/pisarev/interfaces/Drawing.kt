package com.sergey.pisarev.interfaces

import com.sergey.pisarev.model.core.MyData
import com.sergey.pisarev.contur.Point
import javafx.scene.canvas.GraphicsContext

interface Drawing {
    fun drawContour(data: MyData, gc: GraphicsContext, pointCoordinateZero: Point, zoom: Double, index: Int, isSelectToolRadius: Boolean)
    fun setNumberLine(numberLine: Int)
}