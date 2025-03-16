package com.arthurgichuhi.aopengl.models

data class Vec3f(
    var x:Float=0f,
    var y:Float=0f,
    var z:Float=0f,
){
    /**
     * Constructor to create Vec3 object from a float array of size 3
     */
    constructor(array: FloatArray) : this(x = if(array.size>1)array[0]else 0f, y =if(array.size>2)array[1] else 0f, z =if(array.size>2) array[2] else 0f)
    /**
    *Creates an float array object from Vec3 object
     */
    fun toArray():FloatArray{
        return floatArrayOf(x,y,z)
    }
}
