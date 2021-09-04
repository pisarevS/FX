package com.sergey.pisarev.model

import java.lang.StringBuffer
import java.util.ArrayList
import java.util.LinkedHashMap

class MyData {
    var programList: List<StringBuffer> = ArrayList()
    var errorListMap: Map<Int, String> = LinkedHashMap()
    var frameList: List<Frame> = ArrayList()
        private set

    fun setFrameList(frameList: ArrayList<Frame>) {
        this.frameList = frameList
    }
}