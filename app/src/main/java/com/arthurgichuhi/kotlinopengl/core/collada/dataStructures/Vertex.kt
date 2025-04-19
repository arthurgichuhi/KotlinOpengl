package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

import com.arthurgichuhi.kotlinopengl.models.Vec3f

data class Vertex(
    val position : Vec3f,
    var index : Int,
    var weightsData : VertexSkinData,
    var dupVertex: Vertex? = null,
    val length : Float = position.length(),
    var textureIndex : Int = -1,
    var normalIndex :Int = -1,
    val tangents : MutableList<Vec3f> = ArrayList(),
    var averagedTangent : Vec3f = Vec3f()
){
    fun isSet():Boolean{
        return textureIndex!=-1 && normalIndex!=-1
    }

    fun hasTexAndNormal(texIndex:Int,normIndex:Int):Boolean{
        return texIndex == textureIndex && normalIndex == normIndex
    }

    fun averageTangents(){
        if(tangents.isEmpty()){
            return
        }
        for(tangent in tangents){
            averagedTangent.add(tangent)
        }
        averagedTangent.normalise()
    }
}
