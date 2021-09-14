package com.sergey.pisarev.contur

import java.lang.StrictMath.toDegrees
import kotlin.math.acos
import kotlin.math.sqrt

class Point2D(override var x: Double, override var z: Double) : Point() {

    fun distance(x1: Double, z1: Double) :Double {
        val a = x - x1
        val b = z - z1
        return sqrt(a * a + b * b)
    }

    fun angle(x1: Double,z1: Double) :Double{
        val delta :Double = (x * x1 + z * z1) / sqrt(
                (x1 * x1 + z1 * z1) * (x * x + z * z));

        if (delta > 1.0) {
            return 0.0;
        }
        if (delta < -1.0) {
            return 180.0;
        }
        return toDegrees(acos(delta));
    }
}