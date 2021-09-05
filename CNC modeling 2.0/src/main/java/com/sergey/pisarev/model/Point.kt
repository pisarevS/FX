package com.sergey.pisarev.model

open class Point : Text {

    open var x = N_GANTRYPOS_X
    open var z = N_GANTRYPOS_Z

    constructor() {}
    constructor(x: Double, z: Double) {
        this.x = x
        this.z = z
    }
}