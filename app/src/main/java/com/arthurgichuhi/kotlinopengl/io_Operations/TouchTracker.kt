package com.arthurgichuhi.kotlinopengl.io_Operations

import org.joml.Vector2f

data class TouchTracker(
    var id:Long,
    var released:Boolean = false,
    var startPosition:Vector2f,
    var currentPosition:Vector2f,
    var updated:Boolean = false
)
