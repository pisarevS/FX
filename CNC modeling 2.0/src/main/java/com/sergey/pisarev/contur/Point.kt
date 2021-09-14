package com.sergey.pisarev.contur

import com.sergey.pisarev.model.core.GCode

open class Point : GCode {

    open var x = N_GANTRYPOS_X
    open var z = N_GANTRYPOS_Z

    constructor() {}
    constructor( x: Double, z: Double) {
        this.x = x
        this.z = z
    }
}