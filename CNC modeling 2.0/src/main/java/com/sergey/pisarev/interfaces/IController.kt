package com.sergey.pisarev.interfaces

import java.lang.StringBuffer

interface IController {
    fun onStop()
    fun showProgram(text: String?)
    fun showFrame(frame: String?)
    fun showError(error: String?)
    fun showCaretBoxOnCycleStart(number: Int, frame: StringBuilder?)
    fun setZooming(zooming: Double)
    fun getCoordinateCanvas(x: Double, z: Double)
    fun getCoordinateFrame(x: Double, z: Double)
    fun showCaretBoxOnCanvasClick(number: Int, frame: StringBuilder?)
    fun showSaveAlert()
}