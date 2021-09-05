package com.sergey.pisarev.model.base

import com.sergey.pisarev.model.ProgramText
import com.sergey.pisarev.interfaces.IDraw
import com.sergey.pisarev.model.Frame
import com.sergey.pisarev.model.Point
import com.sergey.pisarev.model.Point2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import kotlin.math.*

abstract class BaseDraw : ProgramText {

    protected var draw: IDraw? = null
    private val lineWidth = 1.8
    private val lineWidthDashes = 1.0
    protected var isNumberLine = false
    private val colorLine = Color.valueOf("#0080FF")
    private val colorLineDashes = Color.GRAY
    protected var numberLIne = 0
    private var pStart = Point()
    private var pEnd = Point()

    protected constructor(draw: IDraw?) {
        this.draw = draw
    }

    constructor() {}

    protected fun drawArc(
        gc: GraphicsContext,
        isRapidFeed: Int,
        pointSystemCoordinate: Point,
        pointStart: Point,
        pointEnd: Point,
        radius: Double,
        zoom: Double,
        clockwise: Int
    ) {
        var r = radius
        if (isRapidFeed == 0) {
            gc.stroke = colorLineDashes
            gc.setLineDashes(3.0, 5.0)
            gc.lineWidth = lineWidthDashes
        } else if (isRapidFeed == 1) {
            gc.stroke = colorLine
            gc.setLineDashes()
            gc.lineWidth = lineWidth
        }
        val pStart = Point(pointStart.x, pointStart.z)
        val pEnd = Point(pointEnd.x, pointEnd.z)
        pStart.x = pStart.x * zoom
        pStart.z = pStart.z * zoom
        pEnd.x = pEnd.x * zoom
        pEnd.z = pEnd.z * zoom
        r *= zoom
        val chord = sqrt((pStart.x - pEnd.x).pow(2.0) + (pStart.z - pEnd.z).pow(2.0))
        val sweetAngle = 2 * asin(chord / (2 * r)) * (180 / Math.PI)
        val h = sqrt(r * r - chord / 2 * (chord / 2))
        if (clockwise == 2) {
            val x01 = pStart.x + (pEnd.x - pStart.x) / 2 + h * (pEnd.z - pStart.z) / chord
            val z01 = pStart.z + (pEnd.z - pStart.z) / 2 - h * (pEnd.x - pStart.x) / chord
            calculateStartAngle(gc, pointSystemCoordinate, r, pStart, sweetAngle, x01, z01)
        }
        if (clockwise == 3) {
            val x02 = pStart.x + (pEnd.x - pStart.x) / 2 - h * (pEnd.z - pStart.z) / chord
            val z02 = pStart.z + (pEnd.z - pStart.z) / 2 + h * (pEnd.x - pStart.x) / chord
            calculateStartAngle(gc, pointSystemCoordinate, r, pEnd, sweetAngle, x02, z02)
        }
    }

    private fun calculateStartAngle(
        gc: GraphicsContext,
        pointSystemCoordinate: Point,
        radius: Double,
        pStart: Point,
        sweetAngle: Double,
        x01: Double,
        z01: Double
    ) {
        var cathet: Double
        var startAngle = 0.0
        if (pStart.x > x01 && pStart.z >= z01) {
            cathet = pStart.x - x01
            startAngle = if (pStart.z == z01) 0.0 else 360 - acos(cathet / radius) * (180 / Math.PI)
        }
        if (pStart.x >= x01 && pStart.z < z01) {
            cathet = pStart.x - x01
            startAngle = if (pStart.x == x01) 90.0 else acos(cathet / radius) * (180 / Math.PI)
        }
        if (pStart.x < x01 && pStart.z <= z01) {
            cathet = x01 - pStart.x
            startAngle = if (pStart.z == z01) 180.0 else 180 - acos(cathet / radius) * (180 / Math.PI)
        }
        if (pStart.x <= x01 && pStart.z > z01) {
            cathet = x01 - pStart.x
            startAngle = if (pStart.x == x01) 270.0 else 180 + acos(cathet / radius) * (180 / Math.PI)
        }
        gc.strokeArc(
            pointSystemCoordinate.x + x01 - radius,
            pointSystemCoordinate.z - z01 - radius,
            radius * 2,
            radius * 2,
            360 - startAngle - sweetAngle,
            sweetAngle,
            ArcType.OPEN
        )
    }

    protected fun drawLine(
        gc: GraphicsContext,
        isRapidFeed: Int,
        pointSystemCoordinate: Point,
        pointStart: Point,
        pointEnd: Point,
        zoom: Double
    ) {
        if (isRapidFeed == 0) {
            gc.stroke = colorLineDashes
            gc.setLineDashes(3.0, 5.0)
            gc.lineWidth = lineWidthDashes
        } else if (isRapidFeed == 1) {
            gc.stroke = colorLine
            gc.setLineDashes()
            gc.lineWidth = lineWidth
        }
        val pStart = Point(pointStart.x, pointStart.z)
        val pEnd = Point(pointEnd.x, pointEnd.z)
        pStart.x = pStart.x * zoom
        pStart.z = pStart.z * zoom
        pEnd.x = pEnd.x * zoom
        pEnd.z = pEnd.z * zoom
        if (pStart.z > 0) pStart.z = pointSystemCoordinate.z - pStart.z else pStart.z =
            pointSystemCoordinate.z + abs(pStart.z)
        if (pEnd.z > 0) pEnd.z = pointSystemCoordinate.z - pEnd.z else pEnd.z = pointSystemCoordinate.z + abs(pEnd.z)
        gc.strokeLine(pointSystemCoordinate.x + pStart.x, pStart.z, pointSystemCoordinate.x + pEnd.x, pEnd.z)
    }

    protected fun drawPoint(
        gc: GraphicsContext,
        pointSystemCoordinate: Point,
        pointEnd: Point,
        zoom: Double,
        color: Color?
    ) {
        var radiusPoint = 4.0
        val pEnd = Point(pointEnd.x, pointEnd.z)
        pEnd.x = pEnd.x * zoom
        pEnd.z = pEnd.z * zoom
        if (pEnd.z > 0) pEnd.z = pointSystemCoordinate.z - pEnd.z else pEnd.z = pointSystemCoordinate.z + abs(pEnd.z)
        gc.fill = color
        gc.fillOval(
            pointSystemCoordinate.x + pEnd.x - radiusPoint,
            pEnd.z - radiusPoint,
            radiusPoint * 2,
            radiusPoint * 2
        )
        gc.stroke = color
        gc.setLineDashes()
        gc.lineWidth = lineWidth
        radiusPoint += 2.5
        gc.strokeOval(
            pointSystemCoordinate.x + pEnd.x - radiusPoint,
            pEnd.z - radiusPoint,
            radiusPoint * 2,
            radiusPoint * 2
        )
    }

    protected fun dawTool(
        gc: GraphicsContext,
        pointSystemCoordinate: Point,
        frameList: List<Frame>,
        zoom: Double,
        color: Color?,
        index: Int,
        tool: String
    ) {
        var toolRadius = if (tool != "")
            toolsMap[tool]!!
        else toolsMap[DEFAULT]!!
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

    protected fun drawRND(
        gc: GraphicsContext,
        isRapidFeed: Int,
        pointSystemCoordinate: Point,
        pointStart: Point,
        pointEnd: Point,
        pointF: Point,
        radiusRND: Double,
        zoom: Double
    ) {
        val pointStartCR = Point()
        val pointEndCR = Point()
        val cathet: Double
        var clockwiseRND = 3
        val angle = Point2D(pointEnd.x - pointStart.x, pointEnd.z - pointStart.z).angle(
            pointEnd.x - pointF.x,
            pointEnd.z - pointF.z
        )
        val firstDistance = Point2D(pointStart.x, pointStart.z).distance(pointEnd.x, pointEnd.z)
        val secondDistance = Point2D(pointEnd.x, pointEnd.z).distance(pointF.x, pointF.z)
        cathet = if (angle == 90.0) {
            radiusRND
        } else {
            (180 - angle) / 2 * (Math.PI / 180) * radiusRND
        }
        var differenceX: Double = pointStart.x - pointEnd.x
        var differenceZ: Double = pointStart.z - pointEnd.z
        pointStartCR.x = differenceX * cathet / firstDistance
        pointStartCR.z = differenceZ * cathet / firstDistance
        pointStartCR.x = pointEnd.x + pointStartCR.x
        pointStartCR.z = pointEnd.z + pointStartCR.z
        differenceX = pointF.x - pointEnd.x
        differenceZ = pointF.z - pointEnd.z
        pointEndCR.x = differenceX * cathet / secondDistance
        pointEndCR.z = differenceZ * cathet / secondDistance
        pointEndCR.x = pointEnd.x + pointEndCR.x
        pointEndCR.z = pointEnd.z + pointEndCR.z
        if (pointStart.x > pointF.x && (pointStart.z + pointF.z) / 2 > pointEnd.z) {
            clockwiseRND = 2
        }
        if (pointStart.x > pointF.x && (pointStart.z + pointF.z) / 2 < pointEnd.z) {
            clockwiseRND = 3
        }
        if (pointStart.x < pointF.x && (pointStart.z + pointF.z) / 2 < pointEnd.z) {
            clockwiseRND = 2
        }
        drawLine(gc, isRapidFeed, pointSystemCoordinate, pointStart, pointStartCR, zoom)
        drawArc(gc, isRapidFeed, pointSystemCoordinate, pointStartCR, pointEndCR, radiusRND, zoom, clockwiseRND)
        pointEnd.x = pointEndCR.x
        pointEnd.z = pointEndCR.z
    }

    protected fun correctionForOffn(frameList: List<Frame>) {
        for (i in frameList.indices) {
            if (frameList[i].gCode.contains(G17) || frameList[i].gCode.contains(G18)) isG17 = isG17(frameList[i].gCode)
            checkGCode(frameList[i].gCode)
            if (frameList[i].isCR && frameList[i].isAxisContains && frameList[i].offn > 0) {                             //draw Arc
                toolRadiusCompensationArcOffn(frameList, i, isToolRadiusCompensation, clockwise)
            }
            if (!frameList[i].isCR && !frameList[i].isRND && frameList[i].isAxisContains && frameList[i].offn > 0) {     //draw line
                toolRadiusCompensationLineOffn(frameList, i, isToolRadiusCompensation)
            }
            if (frameList[i].isRND && frameList[i].isAxisContains) {                                                     //draw RND
                pEnd.x = frameList[i].x
                pEnd.z = frameList[i].z
                pStart.x = pEnd.x
                pStart.z = pEnd.z
            }
        }
    }

    private fun toolRadiusCompensationArcOffn(
        frameList: List<Frame>,
        numberLIne: Int,
        isToolRadiusCompensation: Int,
        clockwise: Int
    ) {
        pEnd.x = frameList[numberLIne].x
        pEnd.z = frameList[numberLIne].z
        val radius = frameList[numberLIne].cr
        val offn = frameList[numberLIne].offn
        val chord = sqrt((pStart.x - pEnd.x).pow(2.0) + (pStart.z - pEnd.z).pow(2.0))
        var h = sqrt(radius * radius - chord / 2 * (chord / 2))
        if (java.lang.Double.isNaN(h)) h = 0.0
        if (java.lang.Double.isNaN(chord)) h = 0.0
        if (clockwise == 2 && frameList[numberLIne].offn > 0) {
            val x01 = pStart.x + (pEnd.x - pStart.x) / 2 + h * (pEnd.z - pStart.z) / chord
            val z01 = pStart.z + (pEnd.z - pStart.z) / 2 - h * (pEnd.x - pStart.x) / chord
            if (isToolRadiusCompensation == 1) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x01, z01, G41)
            } //G41
            if (isToolRadiusCompensation == 2) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x01, z01, G42)
            } //G42
        }
        if (clockwise == 3 && frameList[numberLIne].offn > 0) {
            val x02 = pStart.x + (pEnd.x - pStart.x) / 2 - h * (pEnd.z - pStart.z) / chord
            val z02 = pStart.z + (pEnd.z - pStart.z) / 2 + h * (pEnd.x - pStart.x) / chord
            if (isToolRadiusCompensation == 1) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x02, z02, G42)
            } //G41
            if (isToolRadiusCompensation == 2) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x02, z02, G41)
            } //G42
        }
        pStart.x = pEnd.x
        pStart.z = pEnd.z
    }

    private fun calculateToolRadiusCompensationClockwise(
        frameList: List<Frame>,
        numberLIne: Int,
        radius: Double,
        offn: Double,
        x01: Double,
        z01: Double,
        gCode: String
    ) {
        when (gCode) {
            G41 -> {
                val tempStartX = (x01 - pStart.x) * ((radius - offn) / radius)
                val tempStartZ = (z01 - pStart.z) * ((radius - offn) / radius)
                val tempEndX = (x01 - pEnd.x) * ((radius - offn) / radius)
                val tempEndZ = (z01 - pEnd.z) * ((radius - offn) / radius)
                frameList[numberLIne - 1].x = pStart.x + (x01 - pStart.x - tempStartX)
                frameList[numberLIne - 1].z = pStart.z + (z01 - pStart.z - tempStartZ)
                frameList[numberLIne].x = pEnd.x + (x01 - pEnd.x - tempEndX)
                frameList[numberLIne].z = pEnd.z + (z01 - pEnd.z - tempEndZ)
                frameList[numberLIne].cr = radius - offn
            }
            G42 -> {
                val tempStartX = (x01 - pStart.x) * ((radius + offn) / radius)
                val tempStartZ = (z01 - pStart.z) * ((radius + offn) / radius)
                val tempEndX = (x01 - pEnd.x) * ((radius + offn) / radius)
                val tempEndZ = (z01 - pEnd.z) * ((radius + offn) / radius)
                frameList[numberLIne - 1].x = pStart.x + (x01 - pStart.x - tempStartX)
                frameList[numberLIne - 1].z = pStart.z + (z01 - pStart.z - tempStartZ)
                frameList[numberLIne].x = pEnd.x + (x01 - pEnd.x - tempEndX)
                frameList[numberLIne].z = pEnd.z + (z01 - pEnd.z - tempEndZ)
                frameList[numberLIne].cr = radius + offn
            }
        }
    }

    private fun toolRadiusCompensationLineOffn(frameList: List<Frame>, numberLIne: Int, isToolRadiusCompensation: Int) {
        val offn = frameList[numberLIne].offn
        pEnd.x = frameList[numberLIne].x
        pEnd.z = frameList[numberLIne].z
        if (isToolRadiusCompensation == 1) {
            if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    if (frameList[numberLIne - 1].z == frameList[numberLIne].z && frameList[numberLIne - 1].x > frameList[numberLIne].x) {
                        frameList[numberLIne - 1].x = pStart.x - offn
                    } else frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z + offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z + offn
            } //Z==Z -X
            if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    if (frameList[numberLIne - 1].z == frameList[numberLIne].z && frameList[numberLIne - 1].x < frameList[numberLIne].x) {
                        frameList[numberLIne - 1].x = pStart.x + offn
                    } else frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z - offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z - offn
            } //Z==Z +X
            if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x - offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x - offn
                frameList[numberLIne].z = pEnd.z
            } //X==X -Z
            if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x + offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x + offn
                frameList[numberLIne].z = pEnd.z
            } //X==X +Z
            if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
            if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
            if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
            if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
        } //G41
        if (isToolRadiusCompensation == 2) {
            if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z - offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z - offn
            } //Z==Z -X
            if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z + offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z + offn
            } //Z==Z +X
            if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x + offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x + offn
                frameList[numberLIne].z = pEnd.z
            } //X==X -Z
            if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x - offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x - offn
                frameList[numberLIne].z = pEnd.z
            } //X==X +Z
            if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
            if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
            if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
            if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCode)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
        } //G42
        pStart.x = pEnd.x
        pStart.z = pEnd.z
    }

    private fun toolRadiusCompensationPoint(frameList: List<Frame>, radiusPoint: Double, index: Int): Point {
        var i = index
        if (i == 1) i++
        val pStart: Point
        val pEnd: Point
        if (containsG41G42(frameList[i - 1].gCode)) {
            pStart = Point(frameList[i - 1].x, frameList[i - 1].z)
            pEnd = Point(frameList[i].x, frameList[i].z)
            checkGCode(frameList[i].gCode)
        } else {
            pStart = Point(frameList[i - 2].x, frameList[i - 2].z)
            pEnd = Point(frameList[i - 1].x, frameList[i - 1].z)
            checkGCode(frameList[i - 1].gCode)
        }
        if (frameList[i].isCR && frameList[i].isAxisContains && containsG41G42(frameList[i - 1].gCode)) {
            val radius = frameList[i].cr
            calculateChord(radiusPoint, pStart, pEnd, radius, pStart.x, pStart.z)
        } else if (frameList[i - 1].isCR && frameList[i - 1].isAxisContains && !containsG41G42(frameList[i - 1].gCode)) {
            val radius = frameList[i - 1].cr
            calculateChord(radiusPoint, pStart, pEnd, radius, pEnd.x, pEnd.z)
        }
        if (!frameList[i].isCR && frameList[i].isAxisContains && containsG41G42(frameList[i - 1].gCode)) {
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
        } else if (!frameList[i - 1].isCR && frameList[i - 1].isAxisContains && !containsG41G42(frameList[i - 1].gCode)) {    //draw line
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
                    if (frameList[i - 1].x == frameList[i].x && !frameList[i].gCode.contains("G0")) {
                        pEnd.x = pEnd.x - radiusPoint
                        pStart.z = pEnd.z + radiusPoint
                    } else {
                        pEnd.z = pEnd.z + radiusPoint
                        pStart.z = pStart.z + radiusPoint
                    }
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