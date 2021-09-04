package com.sergey.pisarev.presenter

import com.sergey.pisarev.interfaces.*
import com.sergey.pisarev.model.*
import com.sergey.pisarev.model.MyFile.filePath
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import java.lang.Thread
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color
import javafx.util.Duration
import java.lang.InterruptedException
import java.util.*
import java.util.function.Consumer
import kotlin.math.abs

class Presenter(private val controller: IController, private val gc: GraphicsContext) : Text(), PresenterImpl, IDraw, Callback {
    private var canvasWidth = 0.0
    private var canvasHeight = 0.0
    private var moveX = 0.0
    private var moveZ = 0.0
    private var pointSystemCoordinate: Point = Point()
    private var pointStopCanvas: Point = Point()
    private var data: MyData? = null
    private var drawing: Drawing? = null
    private var index = 0
    private var timeline: Timeline? = null
    private val defZoom = 2.0
    private var zooming = defZoom
    private val errorList: ArrayList<String> = ArrayList()
    private var isStart = false
    private var isCycleStart = false
    private var isSingleBlock = false
    private var isReset = false
    private var isChangesText = false
    private var isDrawPoint = false
    private var coordinateSystemProportionsX = 0.0
    private var coordinateSystemProportionsZ = 0.0
    private var isSelectToolRadius = false

    override fun initSystemCoordinate(canvasWidth: Double, canvasHeight: Double) {
        this.canvasWidth = canvasWidth
        this.canvasHeight = canvasHeight
        pointSystemCoordinate = if (coordinateSystemProportionsX != 0.0 && coordinateSystemProportionsZ != 0.0) Point(canvasWidth * coordinateSystemProportionsX, canvasHeight * coordinateSystemProportionsZ) else Point(canvasWidth * 0.5, canvasHeight * 0.5)
        gc.clearRect(0.0, 0.0, canvasWidth, canvasHeight)
        gc.stroke = Color.BLACK
        gc.setLineDashes(5.0, 5.0)
        gc.globalAlpha = 1.0
        gc.lineWidth = 0.5
        gc.strokeLine(pointSystemCoordinate.x, 0.0, pointSystemCoordinate.x, canvasHeight)
        gc.strokeLine(0.0, pointSystemCoordinate.z, canvasWidth, pointSystemCoordinate.z)
        startDraw(index)
    }

    override fun convertAviaProgram(aviaProgram: String?) {
        controller.showProgram(AVIA().convertAviaProgram(aviaProgram!!))
    }

    override fun convertTheProgramToG17(program: String?) {
        controller.showProgram(UCHOK().convertProgram(program!!, G17))
    }

    override fun convertTheProgramToG18(program: String?) {
        controller.showProgram(UCHOK().convertProgram(program!!, G18))
    }

    override fun checkChangesProgram(program: String?) {
        if (filePath != null) {
            if (program!! != MyFile.getFileTextContent(filePath!!)) {
                controller.showSaveAlert()
            }
        }
    }

    override fun saveProgram(program: String?) {
        if (filePath != null) MyFile.setFileContent(filePath!!, program!!)
    }

    override fun handleMousePressed(event: MouseEvent?) {
        moveX = pointSystemCoordinate.x - event!!.x
        moveZ = pointSystemCoordinate.z - event.y
    }

    override fun handleMouseDragged(event: MouseEvent?) {
        pointSystemCoordinate.x = event!!.x + moveX
        pointSystemCoordinate.z = event.y + moveZ
        setProportionCoordinateSystem(pointSystemCoordinate.x / canvasWidth, pointSystemCoordinate.z / canvasHeight)
        drawSysCoordinate()
        startDraw(index)
    }

    override fun handleZooming(event: ScrollEvent?) {
        val point = Point()
        if (event!!.deltaY > 0) {
            val zoomUp = 1.2
            zooming *= zoomUp
            point.x = pointStopCanvas.x * zoomUp
            point.z = pointStopCanvas.z * zoomUp
            pointSystemCoordinate.x = pointSystemCoordinate.x + pointStopCanvas.x - point.x
            pointSystemCoordinate.z = pointSystemCoordinate.z + point.z - pointStopCanvas.z
            pointStopCanvas.x = pointStopCanvas.x * zoomUp
            pointStopCanvas.z = pointStopCanvas.z * zoomUp
        }
        if (event.deltaY < 0) {
            val zoomDown = 1 - 0.1666654
            zooming *= zoomDown
            point.x = pointStopCanvas.x * zoomDown
            point.z = pointStopCanvas.z * zoomDown
            pointSystemCoordinate.x = pointSystemCoordinate.x + pointStopCanvas.x - point.x
            pointSystemCoordinate.z = pointSystemCoordinate.z + point.z - pointStopCanvas.z
            pointStopCanvas.x = pointStopCanvas.x * zoomDown
            pointStopCanvas.z = pointStopCanvas.z * zoomDown
        }
        if (zooming > 0) startDraw(index) else zooming = 0.0
        controller.setZooming((zooming - defZoom) / defZoom * 100 + 100)
    }

    override fun onMouseMovedCanvas(event: MouseEvent?) {
        val point = Point()
        point.x = (pointSystemCoordinate.x - event!!.x) * -1
        point.z = event.y
        if (point.z > 0) point.z = pointSystemCoordinate.z - point.z else point.z = pointSystemCoordinate.z + abs(point.z)
        pointStopCanvas = Point(point.x, point.z)
        point.x = point.x / zooming
        point.z = point.z / zooming
        controller.getCoordinateCanvas(point.x, point.z)
    }

    override fun onMouseClickedCanvas(event: MouseEvent?) {
        if (isStart || isCycleStart) {
            if (event!!.clickCount == 2) {
                val point = Point()
                point.x = (pointSystemCoordinate.x - event.x) * -1
                point.z = event.y
                if (point.z > 0) point.z = pointSystemCoordinate.z - point.z else point.z = pointSystemCoordinate.z + abs(point.z)
                point.x = point.x / zooming
                point.z = point.z / zooming
                val frame = getFrame(point)
                if (frame.isPresent) {
                    drawing!!.setNumberLine(frame.get().id)
                    startDraw(index)
                    controller.showCaretBoxOnCanvasClick(frame.get().id, data!!.programList[frame.get().id])
                    controller.getCoordinateFrame(frame.get().x, frame.get().z)
                    isDrawPoint = true
                } else if (isDrawPoint) {
                    drawing!!.setNumberLine(-1)
                    startDraw(index)
                    isDrawPoint = false
                }
            }
        }
    }

    private fun getFrame(point: Point): Optional<Frame> {
        val side = 20
        val rect = Rect()
        rect.setRect(point.x - (side shr 1), point.z - (side shr 1), side.toDouble(), side.toDouble())
        return data!!.frameList.stream()
                .filter { p: Frame -> rect.isInsideRect(p.x, p.z) }
                .min(Comparator.comparingDouble { p: Frame -> abs(point.x - p.x) + abs(point.z - p.z) })
    }

    private fun drawSysCoordinate() {
        gc.clearRect(0.0, 0.0, canvasWidth, canvasHeight)
        gc.stroke = Color.BLACK
        gc.setLineDashes(5.0, 5.0)
        gc.globalAlpha = 1.0
        gc.lineWidth = 0.5
        gc.strokeLine(pointSystemCoordinate.x, 0.0, pointSystemCoordinate.x, canvasHeight)
        gc.strokeLine(0.0, pointSystemCoordinate.z, canvasWidth, pointSystemCoordinate.z)
    }

    override fun onStart(program: String?) {
        if (!isReset && program != "") {
            isStart = true
            isReset = true
            startThread(program!!)
            startDraw(index)
        }
    }

    override fun onCycleStart(program: String?, isSelectToolRadius: Boolean?) {
        this.isSelectToolRadius = isSelectToolRadius!!
        isCycleStart = true
        if (!isReset && program != "") {
            startThread(program!!)
            assert(data != null)
            timeline = Timeline(KeyFrame(Duration.millis(200.0), { event: ActionEvent? ->
                if (index < data!!.frameList.size) index++
                startDraw(index)
                controller.showCaretBoxOnCycleStart(data!!.frameList[index - 1].id, data!!.programList[data!!.frameList[index - 1].id])
                controller.getCoordinateFrame(data!!.frameList[index - 1].x, data!!.frameList[index - 1].z)
                if (index == data!!.frameList.size) controller.onStop()
            }))
            timeline!!.cycleCount = data!!.frameList.size
            timeline!!.play()
            if (isSingleBlock) timeline!!.stop()
        }
        if (isSingleBlock) {
            if (index < data!!.frameList.size) index++
            if (index <= data!!.frameList.size) startDraw(index)
            if (index == data!!.frameList.size) controller.onStop()
            controller.showCaretBoxOnCycleStart(data!!.frameList[index - 1].id, data!!.programList[data!!.frameList[index - 1].id])
            controller.getCoordinateFrame(data!!.frameList[index - 1].x, data!!.frameList[index - 1].z)
        }
        isReset = true
    }

    override fun onSingleBlock(isClick: Boolean) {
        if (isClick) {
            isSingleBlock = true
            if (timeline != null) timeline!!.stop()
        } else {
            isSingleBlock = false
            if (timeline != null) timeline!!.play()
        }
    }

    override fun onReset() {
        reset()
    }

    private fun reset() {
        isStart = false
        isReset = false
        isSingleBlock = false
        isCycleStart = false
        data = null
        index = 0
        errorList.clear()
        drawing = null
        setProportionCoordinateSystem(pointSystemCoordinate.x / canvasWidth, pointSystemCoordinate.z / canvasHeight)
        initSystemCoordinate(canvasWidth, canvasHeight)
        if (timeline != null) timeline!!.stop()
    }

    override fun getCaretPosition(numberLine: Int) {
        if (isStart || isCycleStart) {
            isChangesText = true
            if (drawing != null) {
                data!!.frameList.forEach(Consumer { frame: Frame ->
                    if (frame.id == numberLine) {
                        controller.showFrame(data!!.programList[numberLine].toString())
                        controller.getCoordinateFrame(frame.x, frame.z)
                    }
                })
                drawing!!.setNumberLine(numberLine)
            }
            startDraw(index)
            isChangesText = false
        }
    }

    override fun openDragProgram(event: DragEvent?) {
        controller.showProgram(MyFile.getFileTextContent(event!!))
        reset()
    }

    override fun setOnChangesTextProgram(program: String?) {
        if (isStart) {
            isChangesText = true
            startThread(program!!)
            startDraw(index)
            isChangesText = false
        }
    }

    override fun showError(error: String?) {
        if (!isChangesText) {
            if (!errorList.contains(error)) {
                errorList.add(error!!)
                controller.showError(error)
            }
            if (timeline != null) timeline!!.stop()
        }
    }

    override fun callingBack(data: MyData?) {
        this.data = data
        drawing = DrawVerticalTurning(this)
        if (isStart) index = data!!.frameList.size
    }

    private fun startDraw(index: Int) {
        if (drawing != null) {
            drawSysCoordinate()
            drawing!!.drawContour(data!!, gc, pointSystemCoordinate, zooming, index, isSelectToolRadius)
        }
    }

    private fun startThread(program: String) {
        val thread = Thread(ProgramCode(program, this))
        thread.start()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setProportionCoordinateSystem(width: Double, height: Double) {
        coordinateSystemProportionsX = width
        coordinateSystemProportionsZ = height
    }

}