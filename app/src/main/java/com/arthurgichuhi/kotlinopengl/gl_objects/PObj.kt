package com.arthurgichuhi.kotlinopengl.gl_objects

import android.content.Context
import android.opengl.GLES32.*
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.utils.Utils
import com.arthurgichuhi.kotlinopengl.shaders.Program

//Position Object
class PObj(val vertices:FloatArray,val color:Vec3,):AObject() {
    val TAG="PObj"
    lateinit var program: Program
    lateinit var vertexBuffer: VertexBuffer
    lateinit var ctx:Context
    var mVertices:FloatArray = FloatArray(16)
    lateinit var mColor:Vec3
    var nVertices=0

    init {
        mVertices = vertices
        mColor=color
        nVertices = vertices.size/3
    }

    override fun onInit() {
        ctx = super.mScene.context
        program = mScene.loadProgram(ctx,"mvp")
        vertexBuffer = VertexBuffer()
        vertexBuffer.load(mVertices,true)
        program.use()
        program.setFloat("position", Utils().FloatsPerPosition, Utils().FloatsPerPosition,0)
    }

    override fun destroy(aScene: AScene) {
        TODO("Not yet implemented")
    }

    override fun update(time: Long) {
    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        vertexBuffer.bind()
        program.setUniform3f("color",color)
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