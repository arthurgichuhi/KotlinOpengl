package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.AScene
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import com.arthurgichuhi.kotlinopengl.utils.Utils

class SphereObj(
    scale:Float,division:Int,left:String,right:String,
    top:String,bottom:String,front:String,
    back:String
    ):AObject() {
    private lateinit var program: Program
    private val sv = Sphere(division)
    private var vertices:FloatArray = sv.getPositions()
    private lateinit var buffer: VertexBuffer
    private var nVertices = 0
    private var stride = 0
    private val textureIds:MutableList<String> = ArrayList()
    private lateinit var texture: Texture

    private val utils = Utils()
    private val mathUtils =  MathUtils()

    init {
        mathUtils.scale(vertices, scale)
        stride = utils.FloatsPerPosition
        nVertices = vertices.size/stride
        textureIds.addAll(arrayListOf(right,left,top,bottom,front,back))
    }
    override fun onInit() {
        program = mScene.loadProgram("colorcubetexture2")
        buffer = VertexBuffer()
        buffer.load(vertices,true)
        program.use()
        var offset = 0
        program.setFloat("position",utils.FloatsPerPosition,stride,offset)
        offset += utils.FloatsPerPosition
        texture = mScene.loadCubeTex(textureIds.toList())
    }

    override fun destroy(aScene: AScene) {

    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
       //setDepthFunEqual()

        program.use()
        buffer.bind()
        texture.bindTexture()

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawTriangles(0,nVertices)

        //setDepthTestFunLess()
    }
}