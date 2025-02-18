package com.arthurgichuhi.kotlinopengl.models

class JointData(index:Int,name:String,localMat:FloatArray) {
    var myIndex = index
    var myName = name
    var myLocalMat = localMat

    val children: MutableList<JointData> = ArrayList()

    fun addChild(child:JointData){
        children.add(child)
    }
}