package com.sergey.pisarev.contur

import com.sergey.pisarev.contur.base.BaseDraw
import com.sergey.pisarev.model.core.Frame
import com.sergey.pisarev.model.core.MyList
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

open class Tool(
    override var isToolRadiusCompensation: Int,
    override var clockwise: Int,
    override var isG17: Boolean,
    override var isRapidFeed: Int
) : BaseDraw() {
    protected open var pEnd: Point = Point()
    fun dawTool(
        gc: GraphicsContext,
        pointSystemCoordinate: Point,
        frameList: List<Frame>,
        zoom: Double,
        color: Color?,
        index: Int,
        tool: String,
    ) {
        var toolRadius = if (tool != "")
            MyList().listTools[tool]!!
        else MyList().listTools[DEFAULT]!!
        pEnd = toolRadiusCompensationPoint(frameList, toolRadius, index)
        if (frameList[index - 1].isAxisContains && isToolRadiusCompensation != 0) {
            if (toolRadius in 6.0..16.0) {
                toolRadius *= zoom
                pEnd.x = pEnd.x * zoom
                pEnd.z = pEnd.z * zoom
                if (pEnd.z > 0) pEnd.z = pointSystemCoordinate.z - pEnd.z else pEnd.z =
                    pointSystemCoordinate.z + abs(pEnd.z)
                gc.fill = color
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius,
                    pEnd.z - toolRadius,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.stroke = Color.GRAY
                gc.setLineDashes()
                gc.lineWidth = lineWidth
                toolRadius /= 3
                gc.strokeOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius,
                    pEnd.z - toolRadius,
                    toolRadius * 2,
                    toolRadius * 2
                )
            }
            if (tool == T11_RQQ) {
                toolRadius *= zoom
                pEnd.x = pEnd.x * zoom
                pEnd.z = pEnd.z * zoom
                if (pEnd.z > 0) pEnd.z = pointSystemCoordinate.z - pEnd.z else pEnd.z =
                    pointSystemCoordinate.z + abs(pEnd.z)
                gc.fill = color
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius,
                    pEnd.z - toolRadius,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius + 18.352591 * zoom,
                    pEnd.z - toolRadius - 4.917562 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius + 13.435029 * zoom,
                    pEnd.z - toolRadius - 23.270153 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 4.917562 * zoom,
                    pEnd.z - toolRadius - 18.352591 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.beginPath()
                gc.moveTo(pointSystemCoordinate.x + pEnd.x - 2.897777 * zoom, pEnd.z + 0.776457 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 0.776457 * zoom, pEnd.z + 2.897777 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 19.129048 * zoom, pEnd.z - 2.019784 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 21.250368 * zoom, pEnd.z - 5.694019 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 16.332806 * zoom, pEnd.z - 24.046610 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 12.658572 * zoom, pEnd.z - 26.167930 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 5.694019 * zoom, pEnd.z - 21.250368 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 7.815339 * zoom, pEnd.z - 17.576134 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 2.897777 * zoom, pEnd.z + 0.776457 * zoom)
                gc.fill()
                gc.stroke = Color.GRAY
                toolRadius = 3.5
                toolRadius *= zoom
                gc.strokeOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius + 6.717514 * zoom,
                    pEnd.z - toolRadius - 11.635076 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
            }
            if (tool == T10_RQQ || tool == T51_KNUTH) {
                toolRadius *= zoom
                pEnd.x = pEnd.x * zoom
                pEnd.z = pEnd.z * zoom
                if (pEnd.z > 0) pEnd.z = pointSystemCoordinate.z - pEnd.z else pEnd.z =
                    pointSystemCoordinate.z + abs(pEnd.z)
                gc.fill = color
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius,
                    pEnd.z - toolRadius,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 1.132803 * zoom,
                    pEnd.z - toolRadius - 12.948001 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 13.834918 * zoom,
                    pEnd.z - toolRadius - 14.059292 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 12.702115 * zoom,
                    pEnd.z - toolRadius - 1.111291 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.beginPath()
                gc.moveTo(pointSystemCoordinate.x + pEnd.x - 0.139449 * zoom, pEnd.z + 1.593912 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 1.593912 * zoom, pEnd.z - 0.139449 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 0.461108 * zoom, pEnd.z - 13.087451 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 0.993354 * zoom, pEnd.z - 14.541913 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 13.695469 * zoom, pEnd.z - 15.653204 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 15.42883 * zoom, pEnd.z - 13.919843 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 14.296026 * zoom, pEnd.z - 0.971842 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 12.841564 * zoom, pEnd.z + 0.48262 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 0.139449 * zoom, pEnd.z + 1.593912 * zoom)
                gc.fill()
                gc.stroke = Color.GRAY
                toolRadius = 2.5
                toolRadius *= zoom
                gc.strokeOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 6.917459 * zoom,
                    pEnd.z - toolRadius - 7.029646 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
            }
            if (tool == T09_RQQ) {
                toolRadius *= zoom
                pEnd.x = pEnd.x * zoom
                pEnd.z = pEnd.z * zoom
                if (pEnd.z > 0) pEnd.z = pointSystemCoordinate.z - pEnd.z else pEnd.z =
                    pointSystemCoordinate.z + abs(pEnd.z)
                gc.fill = color
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius,
                    pEnd.z - toolRadius,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 7.892323 * zoom,
                    pEnd.z - toolRadius - 21.099913 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                toolRadius = 2.0
                toolRadius *= zoom
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 6.488786 * zoom,
                    pEnd.z - toolRadius - 9.604683 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.fillOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 1.403537 * zoom,
                    pEnd.z - toolRadius - 11.49523 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
                gc.beginPath()
                gc.moveTo(pointSystemCoordinate.x + pEnd.x - 0.945613 * zoom, pEnd.z + 0.738794 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 1.198355 * zoom, pEnd.z - 0.062803 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 0.593722 * zoom, pEnd.z - 11.599902 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x + 0.172484 * zoom, pEnd.z - 12.726553 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 6.94671 * zoom, pEnd.z - 21.838706 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 9.090679 * zoom, pEnd.z - 21.037109 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 8.486045 * zoom, pEnd.z - 9.500011 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 8.064807 * zoom, pEnd.z - 8.37336 * zoom)
                gc.lineTo(pointSystemCoordinate.x + pEnd.x - 0.945613 * zoom, pEnd.z + 0.738794 * zoom)
                gc.fill()
                gc.stroke = Color.GRAY
                toolRadius = 2.5
                toolRadius *= zoom
                gc.strokeOval(
                    pointSystemCoordinate.x + pEnd.x - toolRadius - 3.946162 * zoom,
                    pEnd.z - toolRadius - 10.549956 * zoom,
                    toolRadius * 2,
                    toolRadius * 2
                )
            }
        }
    }

    private fun toolRadiusCompensationPoint(frameList: List<Frame>, radiusPoint: Double, index: Int): Point {
        var i = index
        if (i == 1) i++
        val pStart: Point
        val pEnd: Point
        if (containsG41G42(frameList[i - 1].gCodes)) {
            pStart = Point(frameList[i - 1].x, frameList[i - 1].z)
            pEnd = Point(frameList[i].x, frameList[i].z)
            checkGCode(frameList[i].gCodes)
        } else {
            pStart = Point(frameList[i - 2].x, frameList[i - 2].z)
            pEnd = Point(frameList[i - 1].x, frameList[i - 1].z)
            checkGCode(frameList[i - 1].gCodes)
        }
        if (frameList[i].isCR && frameList[i].isAxisContains && containsG41G42(frameList[i - 1].gCodes)) {
            val radius = frameList[i].cr
            calculateChord(radiusPoint, pStart, pEnd, radius, pStart.x, pStart.z)
        } else if (frameList[i - 1].isCR && frameList[i - 1].isAxisContains && !containsG41G42(frameList[i - 1].gCodes)) {
            val radius = frameList[i - 1].cr
            calculateChord(radiusPoint, pStart, pEnd, radius, pEnd.x, pEnd.z)
        }
        if (!frameList[i].isCR && frameList[i].isAxisContains && containsG41G42(frameList[i - 1].gCodes)) {
            if (isToolRadiusCompensation == 1) {
                if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                    pEnd.x = pStart.x
                    pEnd.z = pStart.z + radiusPoint
                    pStart.x = pStart.x
                    pStart.z = pStart.z + radiusPoint
                } //Z==Z -X
                if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                    pEnd.x = pStart.x
                    pEnd.z = pStart.z - radiusPoint
                    pStart.x = pStart.x
                    pStart.z = pStart.z - radiusPoint
                } //Z==Z +X
                if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                    pEnd.x = pStart.x - radiusPoint
                    pEnd.z = pStart.z
                    pStart.x = pStart.x - radiusPoint
                    pStart.z = pStart.z
                } //X==X -Z
                if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                    pEnd.x = pStart.x + radiusPoint
                    pEnd.z = pStart.z
                    pStart.x = pStart.x + radiusPoint
                    pStart.z = pStart.z
                } //X==X +Z
                if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x - cathet2
                    pEnd.z = pStart.z - cathet1
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z - cathet1
                }
                if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x - cathet2
                    pEnd.z = pStart.z + cathet1
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z + cathet1
                }
                if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x + cathet2
                    pEnd.z = pStart.z + cathet1
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z + cathet1
                }
                if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x + cathet2
                    pEnd.z = pStart.z - cathet1
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z - cathet1
                }
            } //G41
            if (isToolRadiusCompensation == 2) {
                if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                    pEnd.x = pStart.x
                    pEnd.z = pStart.z - radiusPoint
                    pStart.x = pStart.x
                    pStart.z = pStart.z - radiusPoint
                } //Z==Z -X
                if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                    pEnd.x = pStart.x
                    pEnd.z = pStart.z + radiusPoint
                    pStart.x = pStart.x
                    pStart.z = pStart.z + radiusPoint
                } //Z==Z +X
                if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                    pEnd.x = pStart.x + radiusPoint
                    pEnd.z = pStart.z
                    pStart.x = pStart.x + radiusPoint
                    pStart.z = pStart.z
                } //X==X -Z
                if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                    pEnd.x = pStart.x - radiusPoint
                    pEnd.z = pStart.z
                    pStart.x = pStart.x - radiusPoint
                    pStart.z = pStart.z
                } //X==X +Z
                if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x + cathet2
                    pEnd.z = pStart.z + cathet1
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z + cathet1
                }
                if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x + cathet2
                    pEnd.z = pStart.z - cathet1
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z - cathet1
                }
                if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x - cathet2
                    pEnd.z = pStart.z - cathet1
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z - cathet1
                }
                if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pEnd.x = pStart.x - cathet2
                    pEnd.z = pStart.z + cathet1
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z + cathet1
                }
            } //G42
        } else if (!frameList[i - 1].isCR && frameList[i - 1].isAxisContains && !containsG41G42(frameList[i - 1].gCodes)) {    //draw line
            if (isToolRadiusCompensation == 1) {
                if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                    pEnd.z = pEnd.z + radiusPoint
                    pStart.z = pStart.z + radiusPoint
                } //Z==Z -X
                if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                    pEnd.z = pEnd.z - radiusPoint
                    pStart.z = pStart.z - radiusPoint
                } //Z==Z +X
                if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                    pEnd.x = pEnd.x - radiusPoint
                    pStart.x = pStart.x - radiusPoint
                } //X==X -Z
                if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                    pEnd.x = pEnd.x + radiusPoint
                    pStart.x = pStart.x + radiusPoint
                } //X==X +Z
                if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z - cathet1
                    pEnd.x = pEnd.x - cathet2
                    pEnd.z = pEnd.z - cathet1
                }
                if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z + cathet1
                    pEnd.x = pEnd.x - cathet2
                    pEnd.z = pEnd.z + cathet1
                }
                if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z + cathet1
                    pEnd.x = pEnd.x + cathet2
                    pEnd.z = pEnd.z + cathet1
                }
                if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z - cathet1
                    pEnd.x = pEnd.x + cathet2
                    pEnd.z = pEnd.z - cathet1
                }
            } //G41
            if (isToolRadiusCompensation == 2) {
                if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                    pEnd.z = pEnd.z - radiusPoint
                    pStart.z = pStart.z - radiusPoint
                } //Z==Z -X
                if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                    pEnd.z = pEnd.z + radiusPoint
                    pStart.z = pStart.z + radiusPoint
                } //Z==Z +X
                if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                    pEnd.x = pEnd.x + radiusPoint
                    pStart.x = pStart.x + radiusPoint
                } //X==X -Z
                if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                    pEnd.x = pEnd.x - radiusPoint
                    pStart.x = pStart.x - radiusPoint
                } //X==X +Z
                if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z + cathet1
                    pEnd.x = pEnd.x + cathet2
                    pEnd.z = pEnd.z + cathet1
                }
                if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x + cathet2
                    pStart.z = pStart.z - cathet1
                    pEnd.x = pEnd.x + cathet2
                    pEnd.z = pEnd.z - cathet1
                }
                if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - 90 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x - cathet2
                    pStart.z = pStart.z - cathet1
                    pEnd.x = pEnd.x - cathet2
                    pEnd.z = pEnd.z - cathet1
                }
                if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                    var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                    angle = 180 - angle
                    val cathet1 = radiusPoint * sin(Math.toRadians(angle))
                    val cathet2 = sqrt(radiusPoint * radiusPoint - cathet1 * cathet1)
                    pStart.x = pStart.x - cathet1
                    pStart.z = pStart.z + cathet2
                    pEnd.x = pEnd.x - cathet1
                    pEnd.z = pEnd.z + cathet2
                }
            } //G42
        }
        return pEnd
    }

    private fun calculateChord(radiusPoint: Double, pStart: Point, pEnd: Point, radius: Double, x: Double, z: Double) {
        val chord = sqrt((pStart.x - pEnd.x).pow(2.0) + (pStart.z - pEnd.z).pow(2.0))
        val h = sqrt(radius * radius - chord / 2 * (chord / 2)).toFloat()
        if (clockwise == 2) {
            val x01 = pStart.x + (pEnd.x - pStart.x) / 2 + h * (pEnd.z - pStart.z) / chord
            val z01 = pStart.z + (pEnd.z - pStart.z) / 2 - h * (pEnd.x - pStart.x) / chord
            if (isToolRadiusCompensation == 1) {
                val tempEndX = (x01 - x) * ((radius - radiusPoint) / radius)
                val tempEndZ = (z01 - z) * ((radius - radiusPoint) / radius)
                pEnd.x = x + (x01 - x - tempEndX)
                pEnd.z = z + (z01 - z - tempEndZ)
            } //G41
            if (isToolRadiusCompensation == 2) {
                val tempEndX = (x01 - x) * ((radius + radiusPoint) / radius)
                val tempEndZ = (z01 - z) * ((radius + radiusPoint) / radius)
                pEnd.x = x + (x01 - x - tempEndX)
                pEnd.z = z + (z01 - z - tempEndZ)
            } //G42
        }
        if (clockwise == 3) {
            val x02 = pStart.x + (pEnd.x - pStart.x) / 2 - h * (pEnd.z - pStart.z) / chord
            val z02 = pStart.z + (pEnd.z - pStart.z) / 2 + h * (pEnd.x - pStart.x) / chord
            if (isToolRadiusCompensation == 1) {
                val tempEndX = (x02 - x) * ((radius + radiusPoint) / radius)
                val tempEndZ = (z02 - z) * ((radius + radiusPoint) / radius)
                pEnd.x = x + (x02 - x - tempEndX)
                pEnd.z = z + (z02 - z - tempEndZ)
            } //G41
            if (isToolRadiusCompensation == 2) {
                val tempEndX = (x02 - x) * ((radius - radiusPoint) / radius)
                val tempEndZ = (z02 - z) * ((radius - radiusPoint) / radius)
                pEnd.x = x + (x02 - x - tempEndX)
                pEnd.z = z + (z02 - z - tempEndZ)
            } //G42
        }
    }
}