package com.arthurgichuhi.kotlinopengl.customObjs

import android.opengl.GLES32.GL_NO_ERROR
import android.opengl.GLES32.glGetError
import android.util.Log
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.utils.Utils

//Position Object
class PObj(vertices:FloatArray, color: Vec3f,): AObject() {
    private val TAG="PObj"
    private lateinit var program: Program
    private lateinit var vertexBuffer: VertexBuffer
    private var mVertices:FloatArray = FloatArray(16)
    private var mColor: Vec3f
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
        program.setFloat("position", Utils.FloatsPerPosition, Utils.FloatsPerPosition,0)
    }

    override fun destroy() {
        if(::vertexBuffer.isInitialized){
            vertexBuffer.destroy()
        }
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