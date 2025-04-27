package com.arthurgichuhi.kotlinopengl.core.animation.animation

import de.javagl.jgltf.model.NodeModel

data class KeyFrame2(
    val time:Float = 0f,
    var boneTransforms: MutableMap<NodeModel,BoneTransform> = HashMap()
)
