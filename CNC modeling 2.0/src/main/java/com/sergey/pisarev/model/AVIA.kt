package com.sergey.pisarev.model

import com.sergey.pisarev.model.core.GCode
import java.lang.StringBuffer
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicReference
import java.util.Arrays
import java.util.function.Consumer
import java.util.stream.Collectors

class AVIA : GCode() {
    private var y = ""
    private var z = ""
    private var angle = ""
    fun convertAviaProgram(aviaProgram: String): String {
        val programList: MutableList<StringBuffer> = ArrayList()
        val atomicY = AtomicReference("")
        val atomicZ = AtomicReference("")
        val atomicAngle = AtomicReference("")
        val list: List<StringBuffer> = Arrays.stream(aviaProgram.split("\n".toRegex()).toTypedArray())
                .filter { frame: String -> "IF(R0" !in frame }
                .filter { frame: String -> "M50" !in frame }
                .filter { frame: String -> "ENDIF" !in frame }
                .filter { frame: String -> "R0=R0" !in frame }
                .filter { frame: String -> frame != "" }
                .map { str: String? -> StringBuffer(str) }
                .collect(Collectors.toList())
        list.stream()
                .filter { frame: StringBuffer -> frame.indexOf(E_TCARR) != -1 }
                .findFirst()
                .ifPresent { frame: StringBuffer ->
                    searchCoordinate(frame)
                    atomicY.set(y)
                    atomicZ.set(z)
                    atomicAngle.set(angle)
                }
        val count = list.stream()
                .filter { frame: StringBuffer -> frame.indexOf(E_TCARR) != -1 }
                .count().toInt()
        var countCheck = 1
        for (frame in list) {
            if (frame.indexOf(E_TCARR) != -1 && count != countCheck) {
                countCheck++
                if (frame.indexOf(atomicY.get()) != -1) {
                    frame.replace(frame.indexOf(atomicY.get()), frame.indexOf(atomicY.get()) + atomicY.get().length, Y_0)
                }
                if (frame.indexOf(atomicZ.get()) != -1) {
                    frame.replace(frame.indexOf(atomicZ.get()), frame.indexOf(atomicZ.get()) + atomicZ.get().length, Z_0)
                }
                if (frame.indexOf(atomicAngle.get()) != -1) {
                    frame.replace(frame.indexOf(atomicAngle.get()), frame.indexOf(atomicAngle.get()) + atomicAngle.get().length, UGOL)
                }
            } else if (frame.indexOf(E_TCARR) != -1 && count == countCheck) {
                searchCoordinate(frame)
                if (frame.indexOf(y) != -1) {
                    frame.replace(frame.indexOf(y), frame.indexOf(y) + y.length, "0")
                }
                if (frame.indexOf(z) != -1) {
                    frame.replace(frame.indexOf(z), frame.indexOf(z) + z.length, "0")
                }
                if (frame.indexOf(angle) != -1) {
                    frame.replace(frame.indexOf(angle), frame.indexOf(angle) + angle.length, "0")
                }
            }
        }
        val tempAngle = StringBuffer("$UGOL=           ;Угол сверления")
        val tempY = StringBuffer("$Y_0=            ;Координата заходной точки по Y")
        val tempZ = StringBuffer("$Z_0=            ;Координата заходной точки по Z")
        countCheck = 0
        for (frame in list) {
            if (frame.indexOf(E_TCARR) != -1) countCheck++
            if (frame.indexOf(E_HEAD) != -1) {
                programList.add(StringBuffer("EXTERN START_SHNEK(INT)"))
                programList.add(StringBuffer("DEF REAL $UGOL,$Y_0,$Z_0"))
                programList.add(frame)
                programList.add(StringBuffer(";**********************************************"))
                programList.add(StringBuffer(StringBuffer(tempAngle.replace(UGOL.length + 1, UGOL.length + 1 + atomicAngle.get().length, atomicAngle.get()))))
                programList.add(StringBuffer(StringBuffer(tempY.replace(Y_0.length + 1, Y_0.length + 1 + atomicY.get().length, atomicY.get()))))
                programList.add(StringBuffer(StringBuffer(tempZ.replace(Y_0.length + 1, Y_0.length + 1 + atomicZ.get().length, atomicZ.get()))))
                programList.add(StringBuffer(";**********************************************"))
                programList.add(StringBuffer("START_SHNEK(10) ;Запускает транспортер каждое n колесо"))
            } else if (frame.indexOf(E_TCARR) != -1 && frame.indexOf(UGOL) == -1 && count != countCheck) {
                //удаляем лишнее кадры
            } else programList.add(frame)
        }
        val program = StringBuffer()
        programList.forEach(Consumer { frame: StringBuffer -> program.append(frame.toString()).append("\n") })
        return program.toString()
    }

    private fun searchCoordinate(frame: StringBuffer) {
        val chArray = frame.toString().toCharArray()
        var n = 0
        var number: StringBuilder
        for (i in chArray.indices) {
            if (chArray[i] == ',') {
                n++
                when (n) {
                    6 -> {
                        number = StringBuilder()
                        var j = i + 1
                        while (j < chArray.size) {
                            if (chArray[j] != ',') {
                                number.append(chArray[j])
                            } else break
                            j++
                        }
                        y = number.toString()
                    }
                    7 -> {
                        number = StringBuilder()
                        var j = i + 1
                        while (j < chArray.size) {
                            if (chArray[j] != ',') {
                                number.append(chArray[j])
                            } else break
                            j++
                        }
                        z = number.toString()
                    }
                    9 -> {
                        number = StringBuilder()
                        var j = i + 1
                        while (j < chArray.size) {
                            if (chArray[j] != ',') {
                                number.append(chArray[j])
                            } else break
                            j++
                        }
                        angle = number.toString()
                    }
                }
            }
        }
    }
}