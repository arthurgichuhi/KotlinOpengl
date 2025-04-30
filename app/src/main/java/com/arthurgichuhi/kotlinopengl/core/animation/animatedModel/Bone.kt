package com.arthurgichuhi.kotlinopengl.core.animation.animatedModel

import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f

data class Bone(
    var node:NodeModel,
    var localTransform:FloatArray,
    var animatedTransform:FloatArray,
    var children:MutableList<Bone> = ArrayList()
){
    fun setAnimationTransform(array: FloatArray){
        animatedTransform = array
    }
}
