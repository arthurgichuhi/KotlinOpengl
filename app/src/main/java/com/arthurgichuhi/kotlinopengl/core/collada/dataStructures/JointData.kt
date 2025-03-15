package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

data class JointData(
    val index:Int,val nameId:String,
    val localTransform:FloatArray,
    val children:MutableList<JointData> = ArrayList()
)
