package com.sergey.pisarev.interfaces

import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

interface PresenterImpl {
    fun onStart(program: String?)
    fun onCycleStart(program: String?, isSelectToolRadius: Boolean?)
    fun onSingleBlock(isClick: Boolean)
    fun onReset()
    fun getCaretPosition(numberLine: Int)
    fun openDragProgram(event: DragEvent?)
    fun setOnChangesTextProgram(program: String?)
    fun onMouseClickedCanvas(event: MouseEvent?)
    fun onMouseMovedCanvas(event: MouseEvent?)
    fun handleZooming(event: ScrollEvent?)
    fun handleMousePressed(event: MouseEvent?)
    fun handleMouseDragged(event: MouseEvent?)
    fun initSystemCoordinate(canvasWidth: Double, canvasHeight: Double)
    fun convertAviaProgram(aviaProgram: String?)
    fun convertTheProgramToG17(program: String?)
    fun convertTheProgramToG18(program: String?)
    fun checkChangesProgram(program: String?)
    fun saveProgram(program: String?)
}