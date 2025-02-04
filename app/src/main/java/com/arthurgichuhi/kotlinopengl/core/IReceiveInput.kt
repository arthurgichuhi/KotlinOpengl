package com.arthurgichuhi.kotlinopengl.core

interface IReceiveInput {
    fun scroll(mode:InputMode,xDist:Float,yDist:Float)
    fun resetCamera()
}