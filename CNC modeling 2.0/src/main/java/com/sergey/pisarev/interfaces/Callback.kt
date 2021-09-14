package com.sergey.pisarev.interfaces

import com.sergey.pisarev.model.core.MyData

interface Callback {
    fun callingBack(data: MyData?)
}