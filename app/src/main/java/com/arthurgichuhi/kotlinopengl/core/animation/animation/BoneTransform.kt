package com.arthurgichuhi.kotlinopengl.core.animation.animation

import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class BoneTransform(
    var translation : Matrix4f = Matrix4f(),
    var rotation : Matrix4f = Matrix4f(),
    var scale : Matrix4f = Matrix4f()
) {
    fun getLocalTransform():FloatArray{
        return Matrix4f()
            .translation(translation.getTranslation(Vector3f()))
            .rotation(rotation.getUnnormalizedRotation(Quaternionf()))
            .scale(scale.getScale(Vector3f()))
            .get(FloatArray(16))
    }
}