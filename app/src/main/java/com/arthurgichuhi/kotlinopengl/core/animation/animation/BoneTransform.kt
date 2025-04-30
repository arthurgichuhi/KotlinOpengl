package com.arthurgichuhi.kotlinopengl.core.animation.animation

import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class BoneTransform(
    var translation : Vector3f = Vector3f(),
    var rotation : Quaternionf = Quaternionf(),
    var scale : Vector3f = Vector3f()
) {
    fun getLocalTransform():FloatArray{
        return Matrix4f()
            .translation(translation)
            .rotation(rotation)
            .scale(scale)
            .get(FloatArray(16))
    }
}