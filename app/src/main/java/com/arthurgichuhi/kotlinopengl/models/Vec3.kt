package com.arthurgichuhi.aopengl.models

data class Vec3(
    var x:Float=0f,
    var y:Float=0f,
    var z:Float=0f,
){
    fun toArray():FloatArray{
        return floatArrayOf(x,y,z)
    }
}
