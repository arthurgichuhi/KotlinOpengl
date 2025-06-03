package com.arthurgichuhi.kotlinopengl.core

import com.arthurgichuhi.kotlinopengl.io_Operations.TouchTracker

interface IReceiveInput {
    fun scroll(mode:InputMode,xDist:Float,yDist:Float)
    fun resetCamera()
    fun touchTracker(value:TouchTracker)
}