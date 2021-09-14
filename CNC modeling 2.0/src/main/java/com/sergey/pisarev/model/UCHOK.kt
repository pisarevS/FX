package com.sergey.pisarev.model

import com.sergey.pisarev.model.core.GCode
import java.util.Arrays
import java.util.function.Consumer
import java.util.stream.Collectors

class UCHOK : GCode() {
    fun convertProgram(program: String, gCode: String): String {
        val text = StringBuilder()
        val listFrame = Arrays.stream(program.split("\n".toRegex()).toTypedArray())
                .map { str: String? -> StringBuilder(str) }
                .collect(Collectors.toList())
        if (G17 in listFrame.toString() && gCode == G18) {
            for (frame in listFrame) {
                if (G17 in frame) frame.replace(frame.indexOf(G17), frame.indexOf(G17) + G17.length, G18)
                if (M3 in frame) frame.replace(frame.indexOf(M3), frame.indexOf(M3) + M3.length, M4)
                if (G41 in frame) frame.replace(frame.indexOf(G41), frame.indexOf(G41) + G41.length, G42) else if (G42 in frame) frame.replace(frame.indexOf(G42), frame.indexOf(G42) + G42.length, G41)
                if (G2 in frame) frame.replace(frame.indexOf(G2), frame.indexOf(G2) + G2.length, G3) else if (G3 in frame) frame.replace(frame.indexOf(G3), frame.indexOf(G3) + G3.length, G2)
            }
        } else if (G18 in listFrame.toString() && gCode == G17) {
            for (frame in listFrame) {
                if (G18 in frame) frame.replace(frame.indexOf(G18), frame.indexOf(G18) + G18.length, G17)
                if (M4 in frame) frame.replace(frame.indexOf(M4), frame.indexOf(M4) + M4.length, M3)
                if (G41 in frame) frame.replace(frame.indexOf(G41), frame.indexOf(G41) + G41.length, G42) else if (G42 in frame) frame.replace(frame.indexOf(G42), frame.indexOf(G42) + G42.length, G41)
                if (G2 in frame) frame.replace(frame.indexOf(G2), frame.indexOf(G2) + G2.length, G3) else if (G3 in frame) frame.replace(frame.indexOf(G3), frame.indexOf(G3) + G3.length, G2)
            }
        } else return program
        listFrame.forEach(Consumer { p: StringBuilder? -> text.append(p).append('\n') })
        return text.toString()
    }
}