package com.sergey.pisarev.model.core

import com.sergey.pisarev.contur.Point
import com.sergey.pisarev.interfaces.Callback
import com.sergey.pisarev.model.*
import java.lang.Runnable
import java.util.stream.Collectors
import java.lang.Exception
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern
import kotlin.text.StringBuilder

class ProgramCode(private val program: String, private val callback: Callback) : GCode(), Runnable {

    private var data: MyData = MyData()
    private var horizontalAxis: String = "X"
    private var verticalAxis: String = "Z"
    private val defs = arrayOf(DEF_REAL, DEF_INT)
    private var variablesList: MutableMap<String, String> = LinkedHashMap()
    override fun run() {
        val listParameterVariables: List<StringBuilder> = MyFile.getParameter(MyFile.filePath!!) as List<StringBuilder>
        val listParametr: Map<String, String> = readParameterVariables(listParameterVariables)
        replaceParameterVariables(listParametr as MutableMap<String, String>)
        data.programList =
            Arrays.stream(program.split("\n".toRegex()).toTypedArray()).map { str: String? -> StringBuffer(str) }
                .collect(Collectors.toList())
        val programList = Arrays.stream(program.split("\n".toRegex()).toTypedArray())
            .map { str: String? -> StringBuilder(str) }
            .peek { frame: StringBuilder? -> removeLockedFrame(frame!!) }
            .peek { frame: StringBuilder? -> removeIgnore(frame!!) }
            .peek { frame: StringBuilder? -> readDefVariables(frame!!) }
            .peek { frame: StringBuilder? -> readRVariables(frame!!) }
            .peek { frame: StringBuilder? -> initVariables(frame!!) }
            .peek { frame: StringBuilder? -> replaceProgramVariables(frame!!) }
            .collect(Collectors.toList())
        gotoF(programList)
        callback.callingBack(addFrameList(programList))
    }

    private fun addFrameList(programList: List<StringBuilder>): MyData? {
        var error: StringBuilder = java.lang.StringBuilder()
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
        var isHome = false
        var strFrame: StringBuilder
        selectCoordinateSystem(programList)
        for (i in programList.indices) {
            strFrame = programList[i]
            val frame = Frame()
            try {
                if (containsGCode(strFrame)) {
                    val gCodes: List<String> = searchGCog(strFrame.toString())
                    isRadius = activatedRadius(gCodes)
                    frame.gCodes = gCodes
                    frame.id = i
                    frame.x = tempHorizontal
                    frame.z = tempVertical
                    frameList.add(frame)
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if (horizontalAxis + IC in strFrame) {
                    tempHorizontal = tempHorizontal + incrementSearch(strFrame, horizontalAxis + IC)
                    isHorizontalAxis = true
                } else if (containsAxis(strFrame, horizontalAxis)) {
                    val coordinate = coordinateSearch(
                        frame = strFrame,
                        axis = horizontalAxis
                    )
                    if (coordinate != null) {
                        coordinate.also {
                            error = it
                            Expression.calculate(input = coordinate).also {
                                tempHorizontal = it
                                isHorizontalAxis = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = "$strFrame \n \"$horizontalAxis=$error\""
            }
            try {
                if (verticalAxis + IC in strFrame) {
                    tempVertical = tempVertical + incrementSearch(strFrame, verticalAxis + IC)
                    isVerticalAxis = true
                } else if (containsAxis(strFrame, verticalAxis)) {
                    val coordinate = coordinateSearch(
                        frame = strFrame,
                        axis = verticalAxis
                    )
                    if (coordinate != null) {
                        coordinate.also {
                            error = it
                            Expression.calculate(input = it).also {
                                tempVertical = it
                                isVerticalAxis = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = "$strFrame \n \"$verticalAxis=$error\""
            }
            try {
                if ( CR in strFrame && isRadius) {
                    val coordinate = coordinateSearch(
                        frame = strFrame,
                        axis = CR
                    )
                    if (coordinate != null) {
                        coordinate.also {
                            error = it
                            Expression.calculate(input = it).also {
                                tempCR = it
                                isCR = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = "$strFrame \n \"$CR=$error\""
            }
            try {
                if (RND in strFrame && isHorizontalAxis &&  CR !in strFrame || RND in strFrame && isVerticalAxis && RND !in strFrame) {
                    val coordinate = coordinateSearch(
                        frame = strFrame,
                        axis = RND
                    )
                    if (coordinate != null) {
                        coordinate.also {
                            error = it
                            Expression.calculate(input = it).also {
                                tempRND = it
                                isRND = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = "$strFrame \n \"$RND=$error\""
            }
            try {
                if ( OFFN in strFrame) {
                    val coordinate = coordinateSearch(
                        frame = strFrame,
                        axis = OFFN
                    )
                    if (coordinate != null) {
                        coordinate.also {
                            error = it
                            Expression.calculate(input = it).also {
                                tempOFFN = it
                                isOFFN = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = "$strFrame \n \"$OFFN=$error\""
            }
            try {
                if ( DIAMON in strFrame || DIAMOF in strFrame) {
                    if ( DIAMON in strFrame) {
                        isDiamon = true
                    }
                    if ( DIAMOF in strFrame) {
                        isDiamon = false
                    }
                }
            } catch (e: Exception) {
                errorListMap[i] = strFrame.toString()
            }
            try {
                if ( HOME in strFrame) {
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
                frame.diamon = isDiamon
                tempTOOL = readTool(strFrame)
                isTOOL = true
                frame.tool = tempTOOL
                frame.isTool = isTOOL
                isVerticalAxis = true
                isHorizontalAxis = true
                tempHorizontal = N_GANTRYPOS_X
                tempVertical = N_GANTRYPOS_Z
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
        data.errorListMap = errorListMap
        val s: Set<Frame> = LinkedHashSet(frameList)
        frameList.clear()
        frameList.addAll(s)
        Offn().correctionForOffn(frameList)
        correctionForDiamon(frameList)
        data.frameList = frameList
        return data
    }

    protected fun containsTool(sb: StringBuilder): Boolean {
        for (tool in MyList().listTools.keys) {
            if (sb.indexOf(tool) > -1) return true
        }
        return false
    }

    protected fun containsAxis(frame: StringBuilder, axis: String): Boolean {
        if (axis in frame) {
            val n = frame.indexOf(axis) + 1
            when (frame[n]) {
                '-', '=', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> return true
            }
        }
        return false
    }

    protected fun containsGCode(sb: StringBuilder): Boolean {
        for (g in gCodes) if (sb.indexOf(g) > -1) return true
        return false
    }

    private fun isGCode(g: StringBuilder): Boolean {
        for (gCode in gCodes) if (g.toString().equals(gCode)) return true
        return false
    }

    protected fun removeIgnore(frame: StringBuilder) {
        MyList().listIgnoreFrame.forEach(Consumer { ignore: String? ->
            if (ignore!! in frame.toString()) frame.delete(0, frame.length)
        })
    }

    protected fun searchGCog(frame: String): ArrayList<String> {
        val gCodeList = ArrayList<String>()
        var g = StringBuilder(G)
        if (G in frame) {
            for (i in frame.indices) {
                val c = frame[i]
                if (c == G[0]) {
                    for (j in i + 1..frame.length - 1) {
                        val t = frame[j]
                        if (Character.isDigit(t)) {
                            g.append(t)
                        } else {
                            if (isGCode(g)) gCodeList.add(g.toString())
                            break
                        }
                    }
                    g = java.lang.StringBuilder(G)
                }
            }
        }
        return gCodeList
    }

    protected fun activatedRadius(gCode: List<String?>): Boolean {
        for (code in gCode) {
            when (code) {
                G2, G02, G3, G03 -> return true
                G01, G1, G0, G00 -> return false
            }
        }
        return false
    }

    protected fun gotoF(programList: List<StringBuilder>) {
        var label: String
        for (i in programList.indices) {
            if (GOTOF in programList[i].toString()) {
                label = programList[i].substring(programList[i].indexOf(GOTOF) + GOTOF.length, programList[i].length)
                    .replace(" ", "")
                for (j in i + 1..programList.size - 1) {
                    if ("$label:" !in programList[j].toString()) {
                        programList[j].delete(0, programList[j].length)
                    } else {
                        break
                    }
                }
            }
        }
    }

    protected fun correctionForDiamon(frameList: List<Frame>) {
        for (frame in frameList) {
            if (frame.diamon && frame.isAxisContains) {
                if (frame.tool == null && !frame.isHome) frame.x = frame.x / 2
            }
        }
    }

    protected fun readTool(strFrame: StringBuilder): String {
        for (tool in MyList().listTools.keys) {
            if (strFrame.indexOf(tool) > -1) return tool
        }
        return ""
    }

    protected fun removeLockedFrame(frame: StringBuilder) {
        if ( ";" in frame) frame.delete(frame.indexOf(";"), frame.length)
    }

    protected fun selectCoordinateSystem(programList: List<StringBuilder>) {
        var x = 0
        var u = 0
        programList.forEach(Consumer { valve: StringBuilder ->
            if ("X" in valve.toString()) x++
            if ("U" in valve.toString()) u++
        })
        if (x > u) {
            horizontalAxis = "X"
            verticalAxis = "Z"
        } else if(u > x) {
            horizontalAxis = "U"
            verticalAxis = "W"
        }
    }

    @Throws(Exception::class)
    protected fun incrementSearch(frame: StringBuilder, axis: String): Double {
        val temp = StringBuilder()
        val n = frame.indexOf(axis)
        if (frame[n + axis.length] == '(') {
            for (i in n + axis.length..frame.length - 1) {
                if (!Character.isLetter(frame[i])) {
                    temp.append(frame[i])
                } else {
                    break
                }
            }
            return Expression.calculate(temp)
        }
        return temp.toString().toDouble()
    }

    @Throws(Exception::class)
    protected fun coordinateSearch(frame: StringBuilder, axis: String): StringBuilder? {
        val tempFrame = StringBuilder()
        for (i in frame.indexOf(axis) + axis.length..frame.length - 1) {
            if (!Character.isLetter(frame[i])) {
                tempFrame.append(frame[i])
            } else break
        }
        return tempFrame
    }

    protected fun readParameterVariables(parameterList: List<StringBuilder>): Map<String, String> {
        variablesList.clear()
        parameterList.forEach(Consumer { p: java.lang.StringBuilder ->
            if (";" in p.toString()) p.delete(p.indexOf(";"), p.length)
            if ("=" in p.toString()) {
                var key = 0
                for (j in p.indexOf("=") - 1 downTo 0) {
                    val c = p[j]
                    if (c == ' ') {
                        key = j
                        break
                    }
                }
                variablesList[p.substring(key, p.indexOf("=")).replace(" ", "")] =
                    p.substring(p.indexOf("=") + 1, p.length).replace(" ", "")
            }
        })
        variablesList["N_GANTRYPOS_X"] = N_GANTRYPOS_X.toString()
        variablesList["N_GANTRYPOS_Z"] = N_GANTRYPOS_Z.toString()
        variablesList["N_GANTRYPOS_U"] = N_GANTRYPOS_X.toString()
        variablesList["N_GANTRYPOS_W"] = N_GANTRYPOS_Z.toString()
        variablesList["\$P_TOOLR"] = "16"
        return variablesList
    }

    protected fun replaceParameterVariables(variablesList: MutableMap<String, String>) {
        variablesList.forEach { (key: String, value1: String) ->
            var value = value1
            for (keys in variablesList.keys) {
                if (keys in value) {
                    value = value.replace(keys, variablesList[keys]!!)
                    variablesList[key] = value
                }
            }
        }
    }

    protected fun replaceProgramVariables(frame: StringBuilder) {
        variablesList.forEach { (key: String?, value: String?) ->
            if (key in frame.toString()) {
                var value1 = value
                if (isContainsOperator(value1)) {
                    var newValve = 0.0
                    try {
                        newValve = Expression.calculate(StringBuilder(value1)).toDouble()
                    } catch (e: EmptyStackException) {
                        e.printStackTrace()
                    }
                    value1 = newValve.toString()
                }
                val str = frame.toString().replace(key, value1)
                frame.replace(0, frame.length, str)
            }
        }
    }

    protected fun readDefVariables(frame: StringBuilder) {
        for (def in defs) {
            if (def in frame.toString()) {
                frame.delete(0, frame.indexOf(def) + def.length)
                val arrStr = frame.toString().split(",".toRegex()).toTypedArray()
                for (str in arrStr) {
                    if ("=" in str) {
                        val arrVar = str.split("=".toRegex()).toTypedArray()
                        variablesList[arrVar[0].replace(" ", "")] = arrVar[1].replace(" ", "")
                    } else {
                        variablesList[str.replace(" ", "")] = ""
                    }
                }
            }
        }
    }

    protected fun readRVariables(frame: StringBuilder) {
        val pattern = Pattern.compile("R(\\d+)" + "=")
        val matcher = pattern.matcher(frame)
        while (matcher.find()) {
            variablesList[matcher.group().replace("=", "")] = ""
        }
    }

    protected fun initVariables(frame: StringBuilder) {
        for (def in defs) {
            if (frame.indexOf(def) == -1) {
                variablesList.forEach { (key: String, value: String?) ->
                    if ("$key=" in frame.toString()) {
                        val arrStr = frame.toString().split(" ".toRegex()).toTypedArray()
                        for (str in arrStr) {
                            if ("=" in str) {
                                val arrVar = str.split("=".toRegex()).toTypedArray()
                                variablesList[arrVar[0].replace(" ", "")] = arrVar[1].replace(" ", "")
                            }
                        }
                    }
                }
            }
        }
        replaceParameterVariables(variablesList)
    }
}