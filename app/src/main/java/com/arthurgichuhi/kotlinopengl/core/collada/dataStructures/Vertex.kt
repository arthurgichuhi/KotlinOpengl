package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

import com.arthurgichuhi.aopengl.models.Vec3

data class Vertex(
    val position : Vec3,
    var index : Int,
    var weightsData : VertexSkinData,
    var dupVertex: Vertex? = null,
    val length : Int = 3,
    var textureIndex : Int = -1,
    var normalIndex :Int = -1,
    val tangents : MutableList<Vec3> = ArrayList(),
    val averagedTangent : Vec3 =Vec3()
){
    fun isSet():Boolean{
        return textureIndex!=-1 && normalIndex!=-1
    }

    fun hasTexAndNormal(texIndex:Int,normIndex:Int):Boolean{
        return texIndex == textureIndex && normalIndex == normIndex
    }
}
