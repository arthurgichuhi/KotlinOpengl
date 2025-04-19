package com.arthurgichuhi.kotlinopengl.core.animation.animatedModel

import android.opengl.Matrix
import android.util.Log

class Joint(val index: Int,val name:String, val localTransform:FloatArray) {
    val children : MutableList<Joint> = ArrayList()
    var animatedTransform = FloatArray(16)
    var inverseTransform = FloatArray(16)


    fun addChild(child:Joint){
        children.add(child)
    }

    fun setAnimationTransform(anim:FloatArray){
        animatedTransform = anim
    }

    fun calcInverseBindTransform(transforms:Map<String,FloatArray>){
        inverseTransform = transforms[name]!!
        for(child in children){
            child.calcInverseBindTransform(transforms)
        }
    }

//    fun calcInverseBindTransform(parentTransform:FloatArray){
//        val bindTransform = FloatArray(16)
//        try {
//            Matrix.multiplyMM(bindTransform,0,parentTransform,0,localTransform,0)
//            Matrix.invertM(inverseTransform,0,bindTransform,0)
//            for(child in children){
//                child.calcInverseBindTransform(bindTransform)
//            }
//        }
//        catch (e:Exception){
//            e.printStackTrace()
//        }
//    }

}