package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

data class AnimationData(
    val lengthSeconds:Float,
    val keyFrames:Array<KeyFrameData>,
    val invTransforms:Map<String,FloatArray>
)
