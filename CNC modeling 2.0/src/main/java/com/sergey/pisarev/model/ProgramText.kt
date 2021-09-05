package com.sergey.pisarev.model

import java.util.LinkedHashMap
import java.lang.StringBuffer
import kotlin.Throws
import java.lang.Exception
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.EmptyStackException
import java.util.function.Consumer
import java.util.regex.Pattern

open class ProgramText : Text() {
    private var x = 0
    private var u = 0
    protected var horizontalAxis: String = "X"
    protected var verticalAxis: String = "Z"
    protected var isRapidFeed = 3
    protected var isToolRadiusCompensation = 0
    protected var isG17 = false
    protected var clockwise = 0
    private val defs = arrayOf(DEF_REAL, DEF_INT)
    private var variablesList: MutableMap<String, String> = LinkedHashMap()
    protected val FIBO = 1123581220f
    protected fun containsTool(sb: StringBuffer): Boolean {
        for (tool in toolsMap.keys) {
            if (sb.indexOf(tool) > -1) return true
        }
        return false
    }

    protected fun containsAxis(frame: StringBuffer, axis: String?): Boolean {
        if (contains(frame, axis)) {
            val n = frame.indexOf(axis) + 1
            when (frame[n]) {
                '-', '=', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> return true
            }
        }
        return false
    }

    protected fun containsGCode(sb: StringBuffer): Boolean {
        for (g in gCodes) {
            if (sb.indexOf(g) > -1) return true
        }
        return false
    }

    private fun isGCode(g: String): Boolean {
        for (gCode in gCodes) {
            if (g == gCode) {
                return true
            }
        }
        return false
    }

    protected fun containsG41G42(gCodes: List<String>): Boolean {
        val b = booleanArrayOf(false)
        gCodes.stream()
                .filter { g: String -> g == G41 || g == G42 }
                .findAny()
                .ifPresent { g: String? -> b[0] = true }
        return b[0]
    }

    protected fun isG17(gCodes: List<String>): Boolean {
        for (gCode in gCodes) {
            return gCode.contains(G17)
        }
        return false
    }

    protected fun checkGCode(gCodeList: List<String?>) {
        for (gCode in gCodeList) {
            when (gCode) {
                G0, G00, G40 -> {
                    isRapidFeed = 0
                    isToolRadiusCompensation = 0
                    clockwise = 0
                }
                G1, G01 -> {
                    isRapidFeed = 1
                    clockwise = 0
                }
                G2, G02 -> clockwise = if (isG17) 2 else 3
                G3, G03 -> clockwise = if (isG17) 3 else 2
                G41 -> isToolRadiusCompensation = if (!isG17) 1 else 2
                G42 -> isToolRadiusCompensation = if (!isG17) 2 else 1
            }
        }
    }

    protected fun removeIgnore(frame: StringBuffer) {
        listIgnore.forEach(Consumer { ignore: String? -> if (frame.toString().contains(ignore!!)) frame.delete(0, frame.length) })
    }

    protected fun searchGCog(frame: String): ArrayList<String> {
        val gCodeList = ArrayList<String>()
        var g = StringBuilder(G)
        if (frame.contains(G)) {
            for (i in frame.indices) {
                val c = frame[i]
                if (c == G[0]) {
                    for (j in i + 1 until frame.length) {
                        val t = frame[j]
                        if (isDigit(t)) {
                            g.append(t)
                        } else {
                            if (isGCode(g.toString())) gCodeList.add(g.toString())
                            break
                        }
                    }
                    g = StringBuilder(G)
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

    protected fun gotoF(programList: List<StringBuffer>) {
        var label: String
        for (i in programList.indices) {
            if (programList[i].toString().contains(GOTOF)) {
                label = programList[i].substring(programList[i].indexOf(GOTOF) + GOTOF.length, programList[i].length).replace(" ", "")
                for (j in i + 1 until programList.size) {
                    if (!programList[j].toString().contains("$label:")) {
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

    protected fun readTool(strFrame: StringBuffer): String {
        for (tool in toolsMap.keys) {
            if (strFrame.indexOf(tool) > -1) return tool
        }
        return ""
    }

    protected fun removeLockedFrame(frame: StringBuffer) {
        if (frame.toString().contains(";")) frame.delete(frame.indexOf(";"), frame.length)
    }

    protected fun selectCoordinateSystem(programList: List<StringBuffer>) {
        programList.forEach(Consumer { valve: StringBuffer ->
            if (valve.toString().contains("X")) x++
            if (valve.toString().contains("U")) u++
            if (x > u) {
                horizontalAxis = "X"
                verticalAxis = "Z"
            } else {
                horizontalAxis = "U"
                verticalAxis = "W"
            }
        })
    }

    @Throws(Exception::class)
    protected fun incrementSearch(frame: StringBuffer, axis: String): Float {
        val temp = StringBuilder()
        val n = frame.indexOf(axis)
        if (frame[n + axis.length] == '(') {
            for (i in n + axis.length until frame.length) {
                if (readUp(frame[i])) {
                    temp.append(frame[i])
                } else {
                    break
                }
            }
            return Expression.calculate(temp.toString())
        }
        return temp.toString().toFloat()
    }

    @Throws(Exception::class)
    protected fun coordinateSearch(frame: StringBuffer, axis: String): Float {
        val temp = StringBuilder()
        for (i in frame.indexOf(axis) + axis.length until frame.length) {
            if (readUp(frame[i])) {
                temp.append(frame[i])
            } else {
                break
            }
        }
        if (isSymbol(temp.toString()) && temp.indexOf("=") == 0) {
            if (temp.indexOf("=") != -1) {
                val index = temp.indexOf("=")
                temp.replace(index, index + 1, "")
            }
            return Expression.calculate(temp.toString())
        } else if (!isSymbol(temp.toString()) || temp.indexOf("-") == 0) {
            return temp.toString().toFloat()
        }
        return FIBO
    }

    protected fun readParameterVariables(parameterList: List<StringBuilder>): Map<String, String> {
        variablesList.clear()
        parameterList.forEach(Consumer { p: StringBuilder ->
            if (p.toString().contains(";")) p.delete(p.indexOf(";"), p.length)
            if (p.toString().contains("=")) {
                var key = 0
                for (j in p.indexOf("=") - 1 downTo 0) {
                    val c = p[j]
                    if (c == ' ') {
                        key = j
                        break
                    }
                }
                variablesList[p.substring(key, p.indexOf("=")).replace(" ", "")] = p.substring(p.indexOf("=") + 1, p.length).replace(" ", "")
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
                if (value.contains(keys)) {
                    value = value.replace(keys, variablesList[keys]!!)
                    variablesList[key] = value
                }
            }
        }
    }

    protected fun replaceProgramVariables(frame: StringBuffer) {
        variablesList.forEach { (key: String?, value: String?) ->
            if (frame.toString().contains(key)) {
                var value1 = value
                if (isSymbol(value1)) {
                    var newValve = 0.0
                    try {
                        newValve = Expression.calculate(value1).toDouble()
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

    protected fun readDefVariables(frame: StringBuffer) {
        for (def in defs) {
            if (frame.toString().contains(def)) {
                frame.delete(0, frame.indexOf(def) + def.length)
                val arrStr = frame.toString().split(",".toRegex()).toTypedArray()
                for (str in arrStr) {
                    if (str.contains("=")) {
                        val arrVar = str.split("=".toRegex()).toTypedArray()
                        variablesList[arrVar[0].replace(" ", "")] = arrVar[1].replace(" ", "")
                    } else {
                        variablesList[str.replace(" ", "")] = ""
                    }
                }
            }
        }
    }

    protected fun readRVariables(frame: StringBuffer?) {
        val pattern = Pattern.compile("R(\\d+)" + "=")
        val matcher = pattern.matcher(frame)
        while (matcher.find()) {
            variablesList[matcher.group().replace("=", "")] = ""
        }
    }

    protected fun initVariables(frame: StringBuffer) {
        for (def in defs) {
            if (frame.indexOf(def) == -1) {
                variablesList.forEach { (key: String, value: String?) ->
                    if (frame.toString().contains("$key=")) {
                        val arrStr = frame.toString().split(" ".toRegex()).toTypedArray()
                        for (str in arrStr) {
                            if (str.contains("=")) {
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