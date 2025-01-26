package com.arthurgichuhi.kotlinopengl.gl_objects

import android.opengl.GLES32.*
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexBuffer {
    private var vaoID=-1
    private var vboID=-1
    private val TAG = "VertexBuffer"
    init {
        val tmp=IntArray(1)
        glGenVertexArrays(1,tmp,0)
        vaoID=tmp[0]
        glGenBuffers(1,tmp,0)
        vboID=tmp[0]
    }

    fun load(data:FloatArray,staticDraw:Boolean){
        val vertexData=createFloatBuffer(data)
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER,vboID)
        glBufferData(
            GL_ARRAY_BUFFER,data.size*4, vertexData,
            if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
    }

    fun bind(){
        glBindVertexArray(vaoID)
    }

    private fun createFloatBuffer(data:FloatArray): FloatBuffer {
        val verticesDataBytes = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
        val verticesData = verticesDataBytes.asFloatBuffer()
        verticesData.put(data).position(0)
        return verticesData
    }

    fun checkGlError(name:String) {
        var error = glGetError()
        while (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", "$name----GL Error: $error")
            error = glGetError()
        }
    }
}