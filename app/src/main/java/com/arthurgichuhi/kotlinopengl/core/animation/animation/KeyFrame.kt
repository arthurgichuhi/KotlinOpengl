package com.arthurgichuhi.kotlinopengl.core.animation.animation

import de.javagl.jgltf.model.NodeModel

data class KeyFrame(
    var time:Long = 0,
    var boneTransforms: MutableMap<NodeModel,BoneTransform> = HashMap()
)
