package com.sergey.pisarev.model

import java.util.HashMap
import java.lang.StringBuffer
import java.util.ArrayList

open class Text {
    protected val N_GANTRYPOS_X = 800.0
    protected val N_GANTRYPOS_Z = 400.0
    protected val listIgnore = ArrayList<String>()
    protected var toolsMap: MutableMap<String, Double> = HashMap()
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
    protected val gCodes = arrayOf(G0, G00, G1, G01, G2, G02, G3, G03, G17, G18, G41, G42, G40)
    private fun initList() {
        //ЛПО
        listIgnore.add("G58 X=0 Z=N_CHUCK_HEIGHT_Z_S1[N_CHUCK_JAWS]")
        listIgnore.add("G59 X=N_WP_ZP_X_S1 Z=N_WP_ZP_Z_S1")
        listIgnore.add("G59 X=N_WP_ZP_X_S1")
        listIgnore.add("G59 X=N_WP_ZP_X_S1 Z=N_WP_ZP_Z_S1")
        listIgnore.add("G58 X=0 Z=N_CHUCK_HEIGHT_Z_S2[N_CHUCK_JAWS]")
        listIgnore.add("G59 X=N_WP_ZP_X_S2 Z=N_WP_ZP_Z_S2")
        listIgnore.add("G58 U=0 W=N_CHUCK_HEIGHT_W_S1[N_CHUCK_JAWS]")
        listIgnore.add("G59 U=N_WP_ZP_U_S1 W=N_WP_ZP_W_S1")
        listIgnore.add("G58 U=0 W=N_CHUCK_HEIGHT_W_S2[N_CHUCK_JAWS]")
        listIgnore.add("G59 U=N_WP_ZP_U_S2 W=N_WP_ZP_W_S2")
        //ЛПО2
        listIgnore.add("N_ZERO_O(54,X1,0,\"TR\")")
        listIgnore.add("N_ZERO_O(54,Z1,CHUCK_HEIGHT_Z1_S1[0],\"TR\")")
        listIgnore.add("N_ZERO_O(54,X1,WP_ZP_X1_S1,\"FI\")")
        listIgnore.add("N_ZERO_O(54,Z1,WP_ZP_Z1_S1,\"FI\")")
        listIgnore.add("N_ZERO_O(54,X1,0,\"TR\")")
        listIgnore.add("N_ZERO_O(54,Z1,CHUCK_HEIGHT_Z1_S2[0],\"TR\")")
        listIgnore.add("N_ZERO_O(54,X1,WP_ZP_X1_S2,\"FI\")")
        listIgnore.add("N_ZERO_O(54,Z1,WP_ZP_Z1_S2,\"FI\")")
        listIgnore.add("N_ZERO_O(54,X2,0,\"TR\")")
        listIgnore.add("N_ZERO_O(54,Z2,CHUCK_HEIGHT_Z2_S1[0],\"TR\")")
        listIgnore.add("N_ZERO_O(54,X2,WP_ZP_X2_S1,\"FI\")")
        listIgnore.add("N_ZERO_O(54,Z2,WP_ZP_Z2_S1,\"FI\")")
        listIgnore.add("N_ZERO_O(54,X2,0,\"TR\")")
        listIgnore.add("N_ZERO_O(54,Z2,CHUCK_HEIGHT_Z2_S2[0],\"TR\")")
        listIgnore.add("N_ZERO_O(54,X2,WP_ZP_X2_S2,\"FI\")")
        listIgnore.add("N_ZERO_O(54,Z2,WP_ZP_Z2_S2,\"FI\")")
        //DEFAULT
        toolsMap[DEFAULT] = 6.0 //12мм инструмент по умолчанию
        //RQQ
        toolsMap[T01_RQQ] = 16.0 //32мм черновой кривой
        toolsMap[T02_RQQ] = 10.0 //20мм кривой чистовой
        toolsMap[T03_RQQ] = 16.0 //32мм прямой черновой
        toolsMap[T04_RQQ] = 16.0 //32мм прямой чистовой
        toolsMap[T05_RQQ] = 3.0 //квадрат расточка
        toolsMap[T10_RQQ] = 1.6 //ромб поясок
        toolsMap[T11_RQQ] = 3.0 //квадрат торцовка ступицы
        toolsMap[T20_RQQ] = 10.0 //20мм прямой чистовой
        toolsMap[T77_RQQ] = 1.6 //канавка сопля
        toolsMap[T09_RQQ] = 1.2 //ромб канавка
        toolsMap[T99_RQQ] = 1.0 //канавка фасон
        //KNUTH
        toolsMap[T125_KNUTH] = 12.5 //25мм прямой черновой
        toolsMap[T20_KNUTH] = 10.0 //20мм кривой чистовой
        toolsMap[T51_KNUTH] = 1.6 //ромб поясок
    }

    protected fun contains(sb: StringBuffer, findString: String?): Boolean {
        return sb.indexOf(findString) > -1
    }

    protected fun readUp(input: Char): Boolean {
        when (input) {
            'C', 'X', 'G', 'M', 'F', 'W', 'Z', 'D', 'S', 'A', 'U', 'L', 'O', 'H', 'R' -> return false
        }
        return true
    }

    protected fun isSymbol(text: String): Boolean {
        if (text.contains("=")) return true
        if (text.contains("+")) return true
        if (text.contains("-")) return true
        if (text.contains("*")) return true
        if (text.contains("/")) return true
        return if (text.contains("(")) true else text.contains(")")
    }

    protected fun isDigit(input: Char): Boolean {
        when (input) {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> return true
        }
        return false
    }

    init {
        initList()
    }
}