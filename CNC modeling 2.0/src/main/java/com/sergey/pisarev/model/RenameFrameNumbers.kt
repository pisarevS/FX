package com.sergey.pisarev.model

import java.lang.StringBuffer
import java.util.concurrent.atomic.AtomicInteger
import java.util.Arrays
import java.util.regex.Matcher
import java.util.regex.Pattern

object RenameFrameNumbers {
   
    fun rename(text: String?, firstNumber: Int, iterNumber: Int): String {
        val program = StringBuffer()
        val iterator = AtomicInteger()
        Arrays.stream(text?.split("\n".toRegex())?.toTypedArray())
                .map { str: String? -> StringBuffer(str) }
                .peek { p: StringBuffer ->
                    val closedFrame = Pattern.compile(";[^\n]*")
                    val numberFrame = Pattern.compile("N(\\d+)")
                    val matcherNumberFrame: Matcher
                    var tempNumberFrame: String? = null
                    val matcherCloseFrame: Matcher = closedFrame.matcher(p)
                    if (matcherCloseFrame.find()) {
                        val substring = p.substring(0, matcherCloseFrame.start())
                        matcherNumberFrame = numberFrame.matcher(substring)
                        if (matcherNumberFrame.find()) {
                            tempNumberFrame = p.substring(matcherNumberFrame.start(), matcherNumberFrame.end())
                            iterator.getAndIncrement()
                        } else if (substring.isNotEmpty()) {
                            iterator.getAndIncrement()
                            val result = firstNumber - iterNumber + iterator.get() * iterNumber
                            val strFrame = "N$result "
                            p.replace(0, 0, strFrame)
                        }
                    } else {
                        matcherNumberFrame = numberFrame.matcher(p)
                        if (matcherNumberFrame.find()) {
                            tempNumberFrame = p.substring(matcherNumberFrame.start(), matcherNumberFrame.end())
                            iterator.getAndIncrement()
                        } else if (p.toString() != "") {
                            iterator.getAndIncrement()
                            val result = firstNumber - iterNumber + iterator.get() * iterNumber
                            val strFrame = "N$result "
                            p.replace(0, 0, strFrame)
                        }
                    }
                    if (tempNumberFrame != null) {
                        val start = p.indexOf(tempNumberFrame)
                        val result = firstNumber - iterNumber + iterator.get() * iterNumber
                        val end = start + tempNumberFrame.length
                        val strFrame = "N$result"
                        p.replace(start, end, strFrame)
                    }
                }
                .peek { p: StringBuffer? -> program.append(p).append("\n") }
                .toArray()
        return program.toString()
    }
}