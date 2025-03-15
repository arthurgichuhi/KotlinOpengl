package com.arthurgichuhi.aopengl.models

data class Vec3f(
    var x:Float=0f,
    var y:Float=0f,
    var z:Float=0f,
){
    /**
     * Constructor to create Vec3 object from a float array of size 3
     */
    constructor(array: FloatArray) : this(x = array[0], y = array[1], z = array[2])
    /**
    *Creates an float array object from Vec3 object
     */
    fun toArray():FloatArray{
        return floatArrayOf(x,y,z)
    }
}
