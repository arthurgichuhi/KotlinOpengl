package com.arthurgichuhi.kotlinopengl.models

data class Vec4f(
    var x : Float = 0f,
    var y : Float = 0f,
    var z : Float = 0f,
    var w : Float = 0f
){
    constructor(floatArray: FloatArray)
            :this(x = floatArray[0], y = floatArray[1], z = floatArray[2], w = floatArray[3])
    fun toFloatArray():FloatArray{
        return floatArrayOf(x,y,z,w)
    }
}
