package com.sergey.pisarev.model

import com.sergey.pisarev.interfaces.Callback
import com.sergey.pisarev.model.base.BaseDraw
import java.lang.Runnable
import java.util.Arrays
import java.lang.StringBuffer
import java.util.stream.Collectors
import java.util.HashMap
import java.lang.Exception
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.LinkedHashSet

class ProgramCode(private val program: String, private val callback: Callback) : BaseDraw(), Runnable {
    private var data: MyData? = null
    override fun run() {
        data = MyData()
        val listParameterVariables: List<StringBuilder> = MyFile.getParameter(MyFile.filePath!!) as List<StringBuilder>
        val listParametr: Map<String, String> = readParameterVariables(listParameterVariables )
        replaceParameterVariables(listParametr as MutableMap<String, String>)
        data!!.programList = Arrays.stream(program.split("\n".toRegex()).toTypedArray()).map { str: String? -> StringBuffer(str) }.collect(Collectors.toList())
        val programList = Arrays.stream(program.split("\n".toRegex()).toTypedArray())
                .map { str: String? -> StringBuffer(str) }
                .peek { frame: StringBuffer? -> removeLockedFrame(frame!!) }
                .peek { frame: StringBuffer? -> removeIgnore(frame!!) }
                .peek { frame: StringBuffer? -> readDefVariables(frame!!) }
                .peek { frame: StringBuffer? -> readRVariables(frame!!) }
                .peek { frame: StringBuffer? -> initVariables(frame!!) }
                .peek { frame: StringBuffer? -> replaceProgramVariables(frame!!) }
                .collect(Collectors.toList())
        gotoF(programList)
        callback.callingBack(addFrameList(programList))
    }

    private fun addFrameList(programList: List<StringBuffer>): MyData? {
        val frameList = ArrayList<Frame>()
        val errorListMap: MutableMap<Int, String> = HashMap()
        var tempTOOL: String?
        var tempHorizontal = N_GANTRYPOS_X
        var tempVertical = N_GANTRYPOS_Z
        var tempCR = 0.0
        var tempRND = 0.0
        var tempOFFN = 0.0
        var isVerticalAxis = false
        var isHorizontalAxis = false
        var isCR = false
        var isRND = false
        var isOFFN = false
        var isTOOL = false
        var isRadius = false
        var isDiamon = false
        var strFrame: StringBuffer
        selectCoordinateSystem(programList)
        for (i in programList.indices) {
            strFrame = programList[i]
            val frame = Frame()
            try {
                if (containsGCode(strFrame)) {
                    val gCode: List<String> = searchGCog(strFrame.toString())
                    isRadius = activatedRadius(gCode)
                    frame.gCode = gCode
                    frame.id = i
                    frame.x = tempHorizontal
                    frame.z = tempVertical
                    frameList.add(frame)
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, horizontalAxis + IC)) {
                    tempHorizontal = tempHorizontal + incrementSearch(strFrame, horizontalAxis + IC)
                    isHorizontalAxis = true
                } else if (containsAxis(strFrame, horizontalAxis)) {
                    tempHorizontal = coordinateSearch(strFrame, horizontalAxis).toDouble()
                    if (tempHorizontal != FIBO.toDouble()) {
                        isHorizontalAxis = true
                    } else {
                        errorListMap[i] = strFrame.toString()
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, verticalAxis + IC)) {
                    tempVertical = tempVertical + incrementSearch(strFrame, verticalAxis + IC)
                    isVerticalAxis = true
                } else if (containsAxis(strFrame, verticalAxis)) {
                    tempVertical = coordinateSearch(strFrame, verticalAxis).toDouble()
                    if (tempVertical != FIBO.toDouble()) {
                        isVerticalAxis = true
                    } else {
                        errorListMap[i] = strFrame.toString()
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, CR) && isRadius) {
                    tempCR = coordinateSearch(strFrame, CR).toDouble()
                    if (tempCR != FIBO.toDouble()) {
                        isCR = true
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, RND) && isHorizontalAxis && !contains(strFrame, CR) || contains(strFrame, RND) && isVerticalAxis && !contains(strFrame, CR)) {
                    tempRND = coordinateSearch(strFrame, RND).toDouble()
                    if (tempRND != FIBO.toDouble()) {
                        isRND = true
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, OFFN)) {
                    tempOFFN = coordinateSearch(strFrame, OFFN).toDouble()
                    if (tempOFFN != FIBO.toDouble()) {
                        isOFFN = true
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, DIAMON) || contains(strFrame, DIAMOF)) {
                    if (contains(strFrame, DIAMON)) {
                        isDiamon = true
                    }
                    if (contains(strFrame, DIAMOF)) {
                        isDiamon = false
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (contains(strFrame, HOME)) {
                    isVerticalAxis = true
                    isHorizontalAxis = true
                    frame.isHome = true
                    tempHorizontal = N_GANTRYPOS_X
                    tempVertical = N_GANTRYPOS_Z
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            if (containsTool(strFrame)) {
                frame.id = i
                frame.diamon = isDiamon
                tempTOOL = readTool(strFrame)
                isTOOL = true
                frame.tool = tempTOOL
                frame.isTool = isTOOL
                frame.x = Point().x
                frame.z = Point().z
                frame.isAxisContains = true
                frameList.add(frame)
            }
            if (isCR) {
                frame.id = i
                frame.diamon = isDiamon
                frame.x = tempHorizontal
                frame.z = tempVertical
                frame.offn = tempOFFN
                frame.isOffn = isOFFN
                frame.cr = tempCR
                frame.isCR = true
                frame.isAxisContains = true
                frameList.add(frame)
                isHorizontalAxis = false
                isVerticalAxis = false
                isCR = false
            }
            if (isRND) {
                frame.id = i
                frame.diamon = isDiamon
                frame.x = tempHorizontal
                frame.z = tempVertical
                frame.offn = tempOFFN
                frame.isOffn = isOFFN
                frame.rnd = tempRND
                frame.isRND = true
                frame.isAxisContains = true
                frameList.add(frame)
                isHorizontalAxis = false
                isVerticalAxis = false
                isRND = false
            }
            if (isHorizontalAxis || isVerticalAxis) {
                frame.id = i
                frame.diamon = isDiamon
                frame.x = tempHorizontal
                frame.z = tempVertical
                frame.offn = tempOFFN
                frame.isOffn = isOFFN
                frame.isAxisContains = true
                frameList.add(frame)
                isHorizontalAxis = false
                isVerticalAxis = false
            }
        }
        data!!.errorListMap = errorListMap
        val s: Set<Frame> = LinkedHashSet(frameList)
        frameList.clear()
        frameList.addAll(s)
        correctionForOffn(frameList)
        correctionForDiamon(frameList)
        data!!.setFrameList(frameList)
        return data
    }
}