package com.arthurgichuhi.kotlinopengl.models

data class JoinTransformData(
    var name: String = "",
    var jointLocalTransForm:FloatArray = FloatArray(16)
)