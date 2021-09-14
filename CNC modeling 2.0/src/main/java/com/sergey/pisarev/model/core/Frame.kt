package com.sergey.pisarev.model.core

import java.util.ArrayList

class Frame(
    var id :Int = 0,
    var x :Double = 0.0,
    var z :Double = 0.0,
    var cr :Double = 0.0,
    var offn :Double = 0.0,
    var rnd :Double = 0.0,
    var isCR : Boolean = false,
    var isRND : Boolean = false,
    var isOffn : Boolean = false,
    var diamon : Boolean = false,
    var isTool : Boolean = false,
    var isHome : Boolean = false,
    var isAxisContains : Boolean = false,
    var tool: String? = null,
    var gCodes: List<String> = ArrayList()) {

    override fun toString(): String {
        return "$id $offn $tool $gCodes $x $z $cr"
    }
}