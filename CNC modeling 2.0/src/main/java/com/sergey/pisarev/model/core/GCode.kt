package com.sergey.pisarev.model.core


open class GCode {
    protected val N_GANTRYPOS_X = 800.0
    protected val N_GANTRYPOS_Z = 400.0
    protected val G = "G"
    protected val G17 = "G17"
    protected val G18 = "G18"
    protected val G40 = "G40"
    protected val G41 = "G41"
    protected val G42 = "G42"
    protected val G0 = "G0"
    protected val G00 = "G00"
    protected val G1 = "G1"
    protected val G01 = "G01"
    protected val G2 = "G2"
    protected val G02 = "G02"
    protected val G3 = "G3"
    protected val G03 = "G03"
    protected val M113 = "M113"
    protected val M114 = "M114"
    protected val M3 = "M3"
    protected val M4 = "M4"
    protected val UGOL = "UGOL"
    protected val Y_0 = "Y_0"
    protected val Z_0 = "Z_0"
    protected val E_TCARR = "E_TCARR"
    protected val E_HEAD = "E_HEAD"
    protected val DEF_REAL = "DEF REAL"
    protected val DEF_INT = "DEF INT"
    protected val CR = "CR"
    protected val RND = "RND"
    protected val IC = "=IC"
    protected val OFFN = "OFFN"
    protected val TOOL = "T"
    protected val DIAMON = "DIAMON"
    protected val DIAMOF = "DIAMOF"
    protected val HOME = "HOME"
    protected val GOTOF = "GOTOF"
    protected val DEFAULT = "DEFAULT"
    protected val T01_RQQ = "T=\"T01\""
    protected val T02_RQQ = "T=\"T02\""
    protected val T03_RQQ = "T=\"T03\""
    protected val T04_RQQ = "T=\"T04\""
    protected val T05_RQQ = "T=\"T05\""
    protected val T09_RQQ = "T=\"T09\""
    protected val T10_RQQ = "T=\"T10\""
    protected val T11_RQQ = "T=\"T11\""
    protected val T20_RQQ = "T=\"T20\""
    protected val T77_RQQ = "T=\"T77\""
    protected val T99_RQQ = "T=\"T99\""
    protected val T125_KNUTH = "T125"
    protected val T20_KNUTH = "T20"
    protected val T51_KNUTH = "T51"
    protected open var isRapidFeed = 3
    protected open var isToolRadiusCompensation = 0
    protected open var clockwise = 0
    protected open var isG17 = false
    protected val gCodes = arrayOf(G0, G00, G1, G01, G2, G02, G3, G03, G17, G18, G41, G42, G40)

    fun isContainsOperator(text: StringBuilder): Boolean {
        if ("=" in text) return true
        if ("+" in text) return true
        if ("-" in text) return true
        if ("*" in text) return true
        if ("/" in text) return true
        return if ("(" in text) true else ")" in text
    }

    fun isContainsOperator(text: String): Boolean {
        if ("=" in text) return true
        if ("+" in text) return true
        if ("-" in text) return true
        if ("*" in text) return true
        if ("/" in text) return true
        return if ("(" in text) true else ")" in text
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

    protected fun containsG41G42(gCodes: List<String>): Boolean {
        return gCodes.toString().contains(G41) || gCodes.toString().contains(G42)
    }
}