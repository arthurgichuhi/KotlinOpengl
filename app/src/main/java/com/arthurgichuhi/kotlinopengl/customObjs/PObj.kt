package com.arthurgichuhi.kotlinopengl.customObjs

import android.content.Context
import android.opengl.GLES32.*
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.AScene
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.utils.Utils

//Position Object
class PObj(vertices:FloatArray,color:Vec3,): AObject() {
    private val TAG="PObj"
    private lateinit var program: Program
    private lateinit var vertexBuffer: VertexBuffer
    private var mVertices:FloatArray = FloatArray(16)
    private var mColor:Vec3
    private var nVertices=0

    init {
        mVertices = vertices
        mColor=color
        nVertices = vertices.size/3
    }

    override fun onInit() {
        program = mScene.loadProgram("mvp")
        vertexBuffer = VertexBuffer()
        vertexBuffer.load(mVertices,true)
        program.use()
        program.setFloat("position", Utils().FloatsPerPosition, Utils().FloatsPerPosition,0)
    }

    override fun destroy(aScene: AScene) {
        TODO("Not yet implemented")
    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        vertexBuffer.bind()
        program.setUniform3f("color",mColor)
        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)
        drawTriangles(0,nVertices)
    }

    fun checkGlError(name:String) {
        var error = glGetError()
        while (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", "$name----GL Error: $error")
            error = glGetError()
        }
    }
}