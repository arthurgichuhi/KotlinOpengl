package com.arthurgichuhi.kotlinopengl.models

data class CollisionData(
    val success:Int,
    val actorHit: Boolean,
    val npcBone: Boolean,
    val headHit: Boolean,
    val bodyHit: Boolean
)
