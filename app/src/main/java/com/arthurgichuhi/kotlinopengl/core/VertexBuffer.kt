package com.arthurgichuhi.kotlinopengl.core

import android.opengl.GLES20
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_UNSIGNED_SHORT
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLES32.GL_ARRAY_BUFFER
import android.opengl.GLES32.GL_DYNAMIC_DRAW
import android.opengl.GLES32.GL_ELEMENT_ARRAY_BUFFER
import android.opengl.GLES32.GL_NO_ERROR
import android.opengl.GLES32.GL_STATIC_DRAW
import android.opengl.GLES32.glBindBuffer
import android.opengl.GLES32.glBindVertexArray
import android.opengl.GLES32.glBufferData
import android.opengl.GLES32.glDeleteBuffers
import android.opengl.GLES32.glDeleteVertexArrays
import android.opengl.GLES32.glGenBuffers
import android.opengl.GLES32.glGenVertexArrays
import android.opengl.GLES32.glGetError
import android.opengl.GLES32.glVertexAttribIPointer
import android.util.Log
import de.javagl.jgltf.model.MeshPrimitiveModel
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class VertexBuffer {
    private var vaoID = -1
    private var vboID = -1
    private var eboId = -1

    init {
        val tmp=IntArray(1)
        glGenVertexArrays(1,tmp,0)
        vaoID=tmp[0]
        glGenBuffers(1,tmp,0)
        vboID = tmp[0]
    }

    fun load(data:FloatArray,staticDraw:Boolean){
        val vertexData=FloatBuffer.wrap(data)
        glBindVertexArray(vaoID)
        glBindBuffer(GL_ARRAY_BUFFER,vboID)
        glBufferData(
            GL_ARRAY_BUFFER,data.size*4, vertexData,
            if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
    }

    /**
     * This function role is to buffers the indices data to Opengl using
     * @param indices Short array containing data
     * @param staticDraw to determine the type of usage when buffering
     */
    fun loadIndicesBuffer(indices:ShortArray,staticDraw: Boolean){
        val buffer = ShortBuffer.wrap(indices)
        Log.d("TAG","LIB\n${buffer.array().toList()}")
        val tmp = IntArray(1)
        glBindVertexArray(vaoID)
        glGenBuffers(1,tmp,0)
        eboId = tmp[0]
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices.size * 2,buffer,if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
    }

    fun loadGltfIndices(primitive: MeshPrimitiveModel,staticDraw: Boolean){
        val indices = primitive.indices
        val indicesBuffer = indices.bufferViewModel.bufferViewData
        val tmp = IntArray(1)

        glBindVertexArray(vaoID)
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboId)
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity(), indicesBuffer,
            if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
    }

    fun loadGltfFloats(primitive: MeshPrimitiveModel, locs: Map<String, Int>,loadTex: () -> Unit,staticDraw: Boolean){
        glBindVertexArray(vaoID)
        val tmp = IntArray(locs.size)
        glGenBuffers(locs.size,tmp,0)
        var size: Int

        //bind position
        val positions = primitive.attributes["POSITION"]!!
        val positionsBuffer = positions.bufferViewModel.bufferViewData
        size = positions.elementType.numComponents

        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(
            GL_ARRAY_BUFFER, positionsBuffer.capacity(), positionsBuffer,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["position"]!!)
        glVertexAttribPointer(locs["position"]!!, size, GL_FLOAT, false, 0, 0)

        //bind texCoords
        val tex = primitive.attributes["TEXCOORD_0"]!!
        val texBuffer = tex.bufferViewModel.bufferViewData
        size = tex.elementType.numComponents

        glBindBuffer(GL_ARRAY_BUFFER,tmp[1])
        glBufferData(
            GL_ARRAY_BUFFER, texBuffer.capacity(), texBuffer,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["tex"]!!)
        glVertexAttribPointer(locs["tex"]!!, size, GL_FLOAT, false, 0, 0)

        loadTex()

        //bind normals
        val normals = primitive.attributes["NORMAL"]!!
        val normalsBuffer = normals.bufferViewModel.bufferViewData
        size = normals.elementType.numComponents

        glBindBuffer(GL_ARRAY_BUFFER,tmp[2])
        glBufferData(
            GL_ARRAY_BUFFER, normalsBuffer.capacity(), normalsBuffer,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["normal"]!!)
        glVertexAttribPointer(locs["normal"]!!, size, GL_FLOAT, false, 0, 0)

        //bind weights
        if(locs.size>3) {
            val weights = primitive.attributes["WEIGHTS_0"]!!
            val weightsBuffer = weights.bufferViewModel.bufferViewData
            size = weights.elementType.numComponents

            glBindBuffer(GL_ARRAY_BUFFER,tmp[3])
            glBufferData(
                GL_ARRAY_BUFFER, weightsBuffer.capacity(), weightsBuffer,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(locs["weights"]!!)
            glVertexAttribPointer(locs["weights"]!!, size, GL_FLOAT, false, 0, 0)

        }
    }

    fun loadGltfInt(primitive: MeshPrimitiveModel, locs: Map<String, Int>, staticDraw: Boolean){
        val joints = primitive.attributes["JOINTS_0"]!!
        val jointsBuffer = joints.bufferViewModel.bufferViewData
        val size = joints.elementType.numComponents
        val tmp = IntArray(1)

        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(
            GL_ARRAY_BUFFER, jointsBuffer.capacity(), jointsBuffer,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["jointIndices"]!!)
        glVertexAttribIPointer(locs["jointIndices"]!!,size, GL_UNSIGNED_SHORT, 0, 0)
    }

    fun bind(){
        glBindVertexArray(vaoID)
    }

    fun destroy(){
        if(vaoID!=-1){
            glDeleteVertexArrays(1, intArrayOf(vaoID),0)
        }
        if(vboID!=-1){
            glDeleteBuffers(1, intArrayOf(vboID),0)
        }
        if(eboId!=-1){
            GLES20.glDeleteBuffers(1, intArrayOf(eboId),0)
        }
    }

    fun checkGlError(name:String) {
        var error = glGetError()
        while (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", "$name----GL Error: $error")
            error = glGetError()
        }
    }

}