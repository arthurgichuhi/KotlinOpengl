package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.utils.Utils

class Terrain {
    private val size = 800f
    private val vertexCount = 128
    private var position = Vec3f()


    fun generateTerrain(){
        val count = vertexCount * vertexCount
        val vertices = FloatArray(count * Utils.FloatsPerPosition)
        val normals = FloatArray(count * Utils.FloatsPerPosition)
        val texCoords = FloatArray(count * Utils.FloatsPerTexture)
    }
}