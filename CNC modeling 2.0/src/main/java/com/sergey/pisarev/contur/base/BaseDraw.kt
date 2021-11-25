package com.sergey.pisarev.contur.base

import com.sergey.pisarev.contur.Point
import com.sergey.pisarev.contur.Point2D
import com.sergey.pisarev.interfaces.IDraw
import com.sergey.pisarev.model.core.GCode
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import kotlin.math.*

abstract class BaseDraw : GCode {
    protected var draw: IDraw? = null
    protected val lineWidth = 1.8
    private val lineWidthDashes = 1.0
    protected var isNumberLine = false
    private val colorLine = Color.valueOf("#0080FF")
    private val colorLineDashes = Color.GRAY
    protected var numberLIne = 0
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
        when (isRapidFeed){
            0 -> {
                gc.stroke = colorLineDashes
                gc.setLineDashes(3.0, 5.0)
                gc.lineWidth = lineWidthDashes
            }
            1 ->{
                gc.stroke = colorLine
                gc.setLineDashes()
                gc.lineWidth = lineWidth
            }
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
        when (isRapidFeed){
            0 -> {
                gc.stroke = colorLineDashes
                gc.setLineDashes(3.0, 5.0)
                gc.lineWidth = lineWidthDashes
            }
            1 ->{
                gc.stroke = colorLine
                gc.setLineDashes()
                gc.lineWidth = lineWidth
            }
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

}