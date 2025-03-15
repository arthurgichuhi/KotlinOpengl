package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.aopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.utils.Utils

class Terrain {
    private val size = 800f
    private val vertexCount = 128
    private var position = Vec3f()

    private val utils = Utils()

    fun generateTerrain(){
        val count = vertexCount * vertexCount
        val vertices = FloatArray(count * utils.FloatsPerPosition)
        val normals = FloatArray(count * utils.FloatsPerPosition)
        val texCoords = FloatArray(count * utils.FloatsPerTexture)
    }
}