package com.arthurgichuhi.kotlinopengl.core.animation.animation

import org.joml.Quaternionf
import org.joml.Vector3f

data class BoneTransform(
    var translation : Vector3f = Vector3f(),
    var rotation : Quaternionf = Quaternionf(),
    var scale : Vector3f = Vector3f()
)