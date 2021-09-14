package com.sergey.pisarev.contur

import com.sergey.pisarev.interfaces.IDraw
import com.sergey.pisarev.contur.base.BaseDraw
import com.sergey.pisarev.interfaces.Drawing
import com.sergey.pisarev.model.core.MyData
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class DrawVerticalTurning(draw: IDraw?) : BaseDraw(draw), Drawing {
    override fun drawContour(
        data: MyData,
        gc: GraphicsContext,
        pointCoordinateZero: Point,
        zoom: Double,
        index: Int,
        isSelectToolRadius: Boolean
    ) {
        val frameList = data.frameList
        val errorListMap = data.errorListMap
        val pStart = Point()
        val pEnd = Point()
        val point = Point()
        var isDrawPoint = false
        var radius: Double
        var radiusRND: Double
        var tool: String? = ""
        for (i in 0 until index) {
            if (frameList[i].isTool) tool = frameList[i].tool
            if (frameList[i].gCodes.contains(G17) || frameList[i].gCodes.contains(G18))
                isG17 = G17 in frameList[i].gCodes.toString()
            checkGCode(frameList[i].gCodes)
            if (errorListMap.keys.isNotEmpty())
                if (errorListMap.containsKey(frameList[i].id)|| errorListMap.keys.first() < frameList[i].id) {
                    draw!!.showError(errorListMap[errorListMap.keys.first()])
                    break
                }
            if (frameList[i].isCR && frameList[i].isAxisContains && isRapidFeed != 3 && clockwise != 0) {            //draw Arc
                pEnd.x = frameList[i].x
                pEnd.z = frameList[i].z
                radius = frameList[i].cr
                drawArc(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, radius, zoom, clockwise)
                pStart.x = pEnd.x
                pStart.z = pEnd.z
            }
            if (!frameList[i].isCR && !frameList[i].isRND && frameList[i].isAxisContains && clockwise == 0) {        //draw line
                pEnd.x = frameList[i].x
                pEnd.z = frameList[i].z
                drawLine(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, zoom)
                pStart.x = pEnd.x
                pStart.z = pEnd.z
            }
            if (frameList[i].isRND && frameList[i].isAxisContains && isRapidFeed != 3 && clockwise == 0) {           //draw RND
                pEnd.x = frameList[i].x
                pEnd.z = frameList[i].z
                radiusRND = frameList[i].rnd
                val pointF = Point()
                pointF.x = frameList[i + 1].x
                pointF.z = frameList[i + 1].z
                drawRND(gc, isRapidFeed, pointCoordinateZero, pStart, pEnd, pointF, radiusRND, zoom)
                pStart.x = pEnd.x
                pStart.z = pEnd.z
                pEnd.x = frameList[i].x
                pEnd.z = frameList[i].z
            }
            if (isNumberLine && frameList[i].id == numberLIne) {                                                     //draw point
                point.x = frameList[i].x
                point.z = frameList[i].z
                isDrawPoint = true
            }
        }
        if (isSelectToolRadius) {
            if (isToolRadiusCompensation != 0 && frameList[index - 1].isAxisContains)
                Tool(isToolRadiusCompensation, clockwise, isG17, isRapidFeed).dawTool(
                    gc,
                    pointCoordinateZero,
                    frameList,
                    zoom,
                    Color.web("#D2BF44"),
                    index,
                    tool!!
                )
            else
                drawPoint(gc, pointCoordinateZero, pEnd, zoom, Color.RED)
        } else drawPoint(gc, pointCoordinateZero, pEnd, zoom, Color.RED)
        if (isDrawPoint) drawPoint(gc, pointCoordinateZero, point, zoom, Color.web("#3507EE"))
    }

    override fun setNumberLine(numberLine: Int) {
        isNumberLine = true
        this.numberLIne = numberLine
    }
}