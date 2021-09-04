package com.sergey.pisarev.model

class Rect : Point {
    var height = 0.0
    var width = 0.0

    constructor() {}
    constructor(point: Point, height: Double, width: Double) {
        x = point.x
        z = point.z
        this.height = height
        this.width = width
    }

    fun setRect(point: Point, height: Double, width: Double) {
        x = point.x
        z = point.z
        this.height = height
        this.width = width
    }

    fun setRect(x: Double, z: Double, height: Double, width: Double) {
        this.x = x
        this.z = z
        this.height = height
        this.width = width
    }

    fun isInsideRect(x: Double, z: Double): Boolean {
        return x >= this.x && x <= this.x + width && z >= this.z && z <= this.z + height
    }
}