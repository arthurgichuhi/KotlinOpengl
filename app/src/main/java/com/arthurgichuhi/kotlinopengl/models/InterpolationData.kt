package com.arthurgichuhi.kotlinopengl.models

import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame

data class InterpolationData(
    val previousKeyFrame: KeyFrame,
    val nextKeyFrame: KeyFrame,
    val alpha: Float
)
