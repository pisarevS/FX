package com.sergey.pisarev.model.core

import java.util.ArrayList
import java.util.HashMap

class MyList : GCode() {
    val listIgnoreFrame = ArrayList<String>()
    var listTools: MutableMap<String, Double> = HashMap()
    init {
        //ЛПО
        listIgnoreFrame.add("G58 X=0 Z=N_CHUCK_HEIGHT_Z_S1[N_CHUCK_JAWS]")
        listIgnoreFrame.add("G59 X=N_WP_ZP_X_S1 Z=N_WP_ZP_Z_S1")
        listIgnoreFrame.add("G59 X=N_WP_ZP_X_S1")
        listIgnoreFrame.add("G59 X=N_WP_ZP_X_S1 Z=N_WP_ZP_Z_S1")
        listIgnoreFrame.add("G58 X=0 Z=N_CHUCK_HEIGHT_Z_S2[N_CHUCK_JAWS]")
        listIgnoreFrame.add("G59 X=N_WP_ZP_X_S2 Z=N_WP_ZP_Z_S2")
        listIgnoreFrame.add("G58 U=0 W=N_CHUCK_HEIGHT_W_S1[N_CHUCK_JAWS]")
        listIgnoreFrame.add("G59 U=N_WP_ZP_U_S1 W=N_WP_ZP_W_S1")
        listIgnoreFrame.add("G58 U=0 W=N_CHUCK_HEIGHT_W_S2[N_CHUCK_JAWS]")
        listIgnoreFrame.add("G59 U=N_WP_ZP_U_S2 W=N_WP_ZP_W_S2")
        //ЛПО2
        listIgnoreFrame.add("N_ZERO_O(54,X1,0,\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z1,CHUCK_HEIGHT_Z1_S1[0],\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,X1,WP_ZP_X1_S1,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z1,WP_ZP_Z1_S1,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,X1,0,\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z1,CHUCK_HEIGHT_Z1_S2[0],\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,X1,WP_ZP_X1_S2,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z1,WP_ZP_Z1_S2,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,X2,0,\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z2,CHUCK_HEIGHT_Z2_S1[0],\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,X2,WP_ZP_X2_S1,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z2,WP_ZP_Z2_S1,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,X2,0,\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z2,CHUCK_HEIGHT_Z2_S2[0],\"TR\")")
        listIgnoreFrame.add("N_ZERO_O(54,X2,WP_ZP_X2_S2,\"FI\")")
        listIgnoreFrame.add("N_ZERO_O(54,Z2,WP_ZP_Z2_S2,\"FI\")")
        //DEFAULT
        listTools[DEFAULT] = 6.0 //12мм инструмент по умолчанию
        //RQQ
        listTools[T01_RQQ] = 16.0 //32мм черновой кривой
        listTools[T02_RQQ] = 10.0 //20мм кривой чистовой
        listTools[T03_RQQ] = 16.0 //32мм прямой черновой
        listTools[T04_RQQ] = 16.0 //32мм прямой чистовой
        listTools[T05_RQQ] = 3.0 //квадрат расточка
        listTools[T10_RQQ] = 1.6 //ромб поясок
        listTools[T11_RQQ] = 3.0 //квадрат торцовка ступицы
        listTools[T20_RQQ] = 10.0 //20мм прямой чистовой
        listTools[T77_RQQ] = 1.6 //канавка сопля
        listTools[T09_RQQ] = 1.2 //ромб канавка
        listTools[T99_RQQ] = 1.0 //канавка фасон
        //KNUTH
        listTools[T125_KNUTH] = 12.5 //25мм прямой черновой
        listTools[T20_KNUTH] = 10.0 //20мм кривой чистовой
        listTools[T51_KNUTH] = 1.6 //ромб поясок
    }
}