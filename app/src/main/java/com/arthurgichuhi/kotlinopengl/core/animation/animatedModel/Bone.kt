package com.arthurgichuhi.kotlinopengl.core.animation.animatedModel

import de.javagl.jgltf.model.NodeModel

data class Bone(
    var node:NodeModel,
    var localTransform:FloatArray = FloatArray(16),
    var worldTransform:FloatArray,
    var animatedTransform:FloatArray=FloatArray(16),
    var children:MutableList<Bone> = ArrayList()
){
    fun setAnimationTransform(array: FloatArray){
        animatedTransform = array
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bone

        if (node != other.node) return false
        if (!localTransform.contentEquals(other.localTransform)) return false
        if (!animatedTransform.contentEquals(other.animatedTransform)) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = node.hashCode()
        result = 31 * result + localTransform.contentHashCode()
        result = 31 * result + animatedTransform.contentHashCode()
        result = 31 * result + children.hashCode()
        return result
    }
}
