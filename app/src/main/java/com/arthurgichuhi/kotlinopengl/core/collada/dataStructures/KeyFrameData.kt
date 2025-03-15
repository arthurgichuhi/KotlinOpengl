package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

data class KeyFrameData(
    val time : Float,
    val jointTransforms : MutableList<JointTransformData> = ArrayList()
)
