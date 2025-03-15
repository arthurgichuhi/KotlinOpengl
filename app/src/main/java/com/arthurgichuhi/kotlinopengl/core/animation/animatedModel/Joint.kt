package com.arthurgichuhi.kotlinopengl.core.animation.animatedModel

import android.opengl.Matrix

class Joint(val index: Int,val name:String, val localTransForm:FloatArray) {
    val children : MutableList<Joint> = ArrayList()
    var animatedTransform = FloatArray(16)
    var inverseTransform = FloatArray(16)
    private val list:MutableList<Pair<String,Joint>> = ArrayList()

    fun addChild(child:Joint){
        children.add(child)
    }

    fun setAnimationTransform(anim:FloatArray){
        animatedTransform = anim
    }

//    fun calcInverseBindTransform(parentTransform:FloatArray){
//        try {
//            val bindTransform = FloatArray(16)
//            Matrix.setIdentityM(bindTransform,0)
//            Matrix.multiplyMM(bindTransform,0,parentTransform,0,localTransForm,0)
//            Matrix.invertM(inverseTransform,0,bindTransform,0)
//
//            for(child in children){
//                calcInverseBindTransform(bindTransform)
//            }
//        }
//        catch (e:Exception){
//            e.printStackTrace()
//        }
//    }

    fun calcInverseBindTransform(rootParentTransform: FloatArray) {
        val stack = ArrayDeque<Pair<Joint, FloatArray>>()
        stack.add(Pair(this, rootParentTransform))

        while (stack.isNotEmpty()) {
            val (joint, parentTransform) = stack.removeLast()
            val bindTransform = FloatArray(16)
            //Matrix.setIdentityM(bindTransform, 0)
            Matrix.multiplyMM(bindTransform, 0, parentTransform, 0, joint.localTransForm, 0)
            Matrix.invertM(joint.inverseTransform, 0, bindTransform, 0)

            // Add children to stack in reverse order to process in original order
            for (child in joint.children.reversed()) {
                stack.add(Pair(child, bindTransform.clone())) // Clone to avoid overwriting
            }
        }
    }

}