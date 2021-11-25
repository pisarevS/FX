package com.sergey.pisarev.model.core

import java.util.ArrayList
import java.util.LinkedHashMap

 class MyData {
    var programList: List< StringBuilder> = ArrayList()
    var errorListMap: Map<Int, String> = LinkedHashMap()
    var frameList: List<Frame> = ArrayList()
}