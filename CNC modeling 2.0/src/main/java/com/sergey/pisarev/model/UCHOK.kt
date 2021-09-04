package com.sergey.pisarev.model

import java.lang.StringBuffer
import java.util.Arrays
import java.util.function.Consumer
import java.util.stream.Collectors

class UCHOK : Text() {
    fun convertProgram(program: String, gCode: String): String {
        val text = StringBuffer()
        val listFrame = Arrays.stream(program.split("\n".toRegex()).toTypedArray())
                .map { str: String? -> StringBuffer(str) }
                .collect(Collectors.toList())
        if (listFrame.toString().contains(G17) && gCode == G18) {
            for (frame in listFrame) {
                if (contains(frame, G17)) frame.replace(frame.indexOf(G17), frame.indexOf(G17) + G17.length, G18)
                if (contains(frame, M3)) frame.replace(frame.indexOf(M3), frame.indexOf(M3) + M3.length, M4)
                if (contains(frame, G41)) frame.replace(frame.indexOf(G41), frame.indexOf(G41) + G41.length, G42) else if (contains(frame, G42)) frame.replace(frame.indexOf(G42), frame.indexOf(G42) + G42.length, G41)
                if (contains(frame, G2)) frame.replace(frame.indexOf(G2), frame.indexOf(G2) + G2.length, G3) else if (contains(frame, G3)) frame.replace(frame.indexOf(G3), frame.indexOf(G3) + G3.length, G2)
            }
        } else if (listFrame.toString().contains(G18) && gCode == G17) {
            for (frame in listFrame) {
                if (contains(frame, G18)) frame.replace(frame.indexOf(G18), frame.indexOf(G18) + G18.length, G17)
                if (contains(frame, M4)) frame.replace(frame.indexOf(M4), frame.indexOf(M4) + M4.length, M3)
                if (contains(frame, G41)) frame.replace(frame.indexOf(G41), frame.indexOf(G41) + G41.length, G42) else if (contains(frame, G42)) frame.replace(frame.indexOf(G42), frame.indexOf(G42) + G42.length, G41)
                if (contains(frame, G2)) frame.replace(frame.indexOf(G2), frame.indexOf(G2) + G2.length, G3) else if (contains(frame, G3)) frame.replace(frame.indexOf(G3), frame.indexOf(G3) + G3.length, G2)
            }
        } else return program
        listFrame.forEach(Consumer { p: StringBuffer? -> text.append(p).append('\n') })
        return text.toString()
    }
}