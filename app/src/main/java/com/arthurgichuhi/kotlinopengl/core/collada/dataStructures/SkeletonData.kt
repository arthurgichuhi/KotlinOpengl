package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

import com.arthurgichuhi.kotlinopengl.models.JointData

data class SkeletonData(
    var jointCount:Int = 0,
    var headJoint: JointData
)
