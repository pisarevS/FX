package com.sergey.pisarev.interfaces

import com.sergey.pisarev.model.MyData

interface Callback {
    fun callingBack(data: MyData?)
}