package com.sergey.pisarev.model.core

import com.sergey.pisarev.contur.Point
import com.sergey.pisarev.contur.Point2D
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Offn : GCode() {
    private var pEnd = Point()
    private var pStart = Point()
    fun correctionForOffn(frameList: List<Frame>)  {
        for (i in frameList.indices) {
            if (frameList[i].gCodes.contains(G17) || frameList[i].gCodes.contains(G18)) isG17 = G17 in frameList[i].gCodes.toString()
            checkGCode(frameList[i].gCodes)
            if (frameList[i].isCR && frameList[i].isAxisContains && frameList[i].offn > 0) {                             //draw Arc
                radiusCompensationArcOffn(frameList, i, isToolRadiusCompensation, clockwise)
            }
            if (!frameList[i].isCR && !frameList[i].isRND && frameList[i].isAxisContains && frameList[i].offn > 0) {     //draw line
                radiusCompensationLineOffn(frameList, i, isToolRadiusCompensation)
            }
            if (frameList[i].isRND && frameList[i].isAxisContains) {                                                     //draw RND
                pEnd.x = frameList[i].x
                pEnd.z = frameList[i].z
                pStart.x = pEnd.x
                pStart.z = pEnd.z
            }
        }
    }

    private fun radiusCompensationArcOffn(
        frameList: List<Frame>,
        numberLIne: Int,
        isToolRadiusCompensation: Int,
        clockwise: Int
    ) {
        pEnd.x = frameList[numberLIne].x
        pEnd.z = frameList[numberLIne].z
        val radius = frameList[numberLIne].cr
        val offn = frameList[numberLIne].offn
        val chord = sqrt((pStart.x - pEnd.x).pow(2.0) + (pStart.z - pEnd.z).pow(2.0))
        var h = sqrt(radius * radius - chord / 2 * (chord / 2))
        if (java.lang.Double.isNaN(h)) h = 0.0
        if (java.lang.Double.isNaN(chord)) h = 0.0
        if (clockwise == 2 && frameList[numberLIne].offn > 0) {
            val x01 = pStart.x + (pEnd.x - pStart.x) / 2 + h * (pEnd.z - pStart.z) / chord
            val z01 = pStart.z + (pEnd.z - pStart.z) / 2 - h * (pEnd.x - pStart.x) / chord
            if (isToolRadiusCompensation == 1) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x01, z01, G41)
            } //G41
            if (isToolRadiusCompensation == 2) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x01, z01, G42)
            } //G42
        }
        if (clockwise == 3 && frameList[numberLIne].offn > 0) {
            val x02 = pStart.x + (pEnd.x - pStart.x) / 2 - h * (pEnd.z - pStart.z) / chord
            val z02 = pStart.z + (pEnd.z - pStart.z) / 2 + h * (pEnd.x - pStart.x) / chord
            if (isToolRadiusCompensation == 1) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x02, z02, G42)
            } //G41
            if (isToolRadiusCompensation == 2) {
                calculateToolRadiusCompensationClockwise(frameList, numberLIne, radius, offn, x02, z02, G41)
            } //G42
        }
        pStart.x = pEnd.x
        pStart.z = pEnd.z
    }

    private fun calculateToolRadiusCompensationClockwise(
        frameList: List<Frame>,
        numberLIne: Int,
        radius: Double,
        offn: Double,
        x01: Double,
        z01: Double,
        gCode: String
    ) {
        when (gCode) {
            G41 -> {
                val tempStartX = (x01 - pStart.x) * ((radius - offn) / radius)
                val tempStartZ = (z01 - pStart.z) * ((radius - offn) / radius)
                val tempEndX = (x01 - pEnd.x) * ((radius - offn) / radius)
                val tempEndZ = (z01 - pEnd.z) * ((radius - offn) / radius)
                frameList[numberLIne - 1].x = pStart.x + (x01 - pStart.x - tempStartX)
                frameList[numberLIne - 1].z = pStart.z + (z01 - pStart.z - tempStartZ)
                frameList[numberLIne].x = pEnd.x + (x01 - pEnd.x - tempEndX)
                frameList[numberLIne].z = pEnd.z + (z01 - pEnd.z - tempEndZ)
                frameList[numberLIne].cr = radius - offn
            }
            G42 -> {
                val tempStartX = (x01 - pStart.x) * ((radius + offn) / radius)
                val tempStartZ = (z01 - pStart.z) * ((radius + offn) / radius)
                val tempEndX = (x01 - pEnd.x) * ((radius + offn) / radius)
                val tempEndZ = (z01 - pEnd.z) * ((radius + offn) / radius)
                frameList[numberLIne - 1].x = pStart.x + (x01 - pStart.x - tempStartX)
                frameList[numberLIne - 1].z = pStart.z + (z01 - pStart.z - tempStartZ)
                frameList[numberLIne].x = pEnd.x + (x01 - pEnd.x - tempEndX)
                frameList[numberLIne].z = pEnd.z + (z01 - pEnd.z - tempEndZ)
                frameList[numberLIne].cr = radius + offn
            }
        }
    }

    private fun radiusCompensationLineOffn(frameList: List<Frame>, numberLIne: Int, isToolRadiusCompensation: Int) {
        val offn = frameList[numberLIne].offn
        pEnd.x = frameList[numberLIne].x
        pEnd.z = frameList[numberLIne].z
        if (isToolRadiusCompensation == 1) {
            if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    if (frameList[numberLIne - 1].z == frameList[numberLIne].z && frameList[numberLIne - 1].x > frameList[numberLIne].x) {
                        frameList[numberLIne - 1].x = pStart.x - offn
                    } else frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z + offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z + offn
            } //Z==Z -X
            if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    if (frameList[numberLIne - 1].z == frameList[numberLIne].z && frameList[numberLIne - 1].x < frameList[numberLIne].x) {
                        frameList[numberLIne - 1].x = pStart.x + offn
                    } else frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z - offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z - offn
            } //Z==Z +X
            if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x - offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x - offn
                frameList[numberLIne].z = pEnd.z
            } //X==X -Z
            if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x + offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x + offn
                frameList[numberLIne].z = pEnd.z
            } //X==X +Z
            if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
            if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
            if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
            if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
        } //G41
        if (isToolRadiusCompensation == 2) {
            if (pStart.z == pEnd.z && pStart.x > pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z - offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z - offn
            } //Z==Z -X
            if (pStart.z == pEnd.z && pStart.x < pEnd.x) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x
                    frameList[numberLIne - 1].z = pStart.z + offn
                }
                frameList[numberLIne].x = pEnd.x
                frameList[numberLIne].z = pEnd.z + offn
            } //Z==Z +X
            if (pStart.x == pEnd.x && pStart.z > pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x + offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x + offn
                frameList[numberLIne].z = pEnd.z
            } //X==X -Z
            if (pStart.x == pEnd.x && pStart.z < pEnd.z) {
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x - offn
                    frameList[numberLIne - 1].z = pStart.z
                }
                frameList[numberLIne].x = pEnd.x - offn
                frameList[numberLIne].z = pEnd.z
            } //X==X +Z
            if (pStart.x < pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pEnd.x - pStart.x, pStart.z - pEnd.z).angle(pStart.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
            if (pStart.x > pEnd.x && pStart.z > pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x + cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x + cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
            if (pStart.x > pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - 90 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z - cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z - cathet1
            }
            if (pStart.x < pEnd.x && pStart.z < pEnd.z) {
                var angle = Point2D(pStart.x - pEnd.x, pStart.z - pEnd.z).angle(pEnd.x, 0.0)
                angle = 180 - angle
                println(angle)
                val cathet1 = offn * sin(Math.toRadians(angle))
                val cathet2 = sqrt(offn * offn - cathet1 * cathet1)
                if (!containsG41G42(frameList[numberLIne].gCodes)) {
                    frameList[numberLIne - 1].x = pStart.x - cathet2
                    frameList[numberLIne - 1].z = pStart.z + cathet1
                }
                frameList[numberLIne].x = pEnd.x - cathet2
                frameList[numberLIne].z = pEnd.z + cathet1
            }
        } //G42
        pStart.x = pEnd.x
        pStart.z = pEnd.z
    }
}