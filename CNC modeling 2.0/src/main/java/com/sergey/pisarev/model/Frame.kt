package com.sergey.pisarev.model

import java.util.ArrayList

class Frame {
    var id = 0
    var x = 0.0
    var z = 0.0
    var cr = 0.0
    var offn = 0.0
    var rnd = 0.0
    var isCR = false
    internal var isOffn = false
    var isRND = false
    var isAxisContains = false
    var diamon = false
    internal var isTool = false
    var isHome = false
    internal var tool: String? = null
    var gCode: List<String> = ArrayList()
    fun isOffn(): Boolean {
        return isOffn
    }

    fun setOffn(offn: Boolean) {
        isOffn = offn
    }

    fun getTool(): String? {
        return tool
    }

    fun setTool(tool: String?) {
        this.tool = tool
    }

    fun isTool(): Boolean {
        return isTool
    }

    fun setTool(tool: Boolean) {
        isTool = tool
    }

    override fun toString(): String {
        return "$id $gCode $x $z $cr"
    }
}