package com.arthurgichuhi.kotlinopengl.core

import android.opengl.GLES20
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
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.utils.Utils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class VertexBuffer {
    private var vaoID=-1
    private var vboID=-1
    private val TAG = "VertexBuffer"
    companion object{
        private val Utils = Utils()
    }
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

    fun loadIndexVertex(meshData: MeshData,staticDraw: Boolean){
        val indices = createIntBuffer(meshData.indices)
        val vertex = createFloatBuffer(meshData.vertices)
        val texCord = createFloatBuffer(meshData.textureCords)
        val normals = createFloatBuffer(meshData.normals)
        val jointIds = createIntBuffer(meshData.jointIds)
        val weights = createFloatBuffer(meshData.vertexWeights)

        glBindVertexArray(vaoID)
        val tmp = IntArray(1)
        //bind indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,vboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,meshData.indices.size*4,indices,if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)

        //bind position
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.vertices.size * Utils.BytesPerFloat,vertex,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)

        //bind texCoords
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.textureCords.size * Utils.FloatsPerTexture,texCord,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)

        //bind normals
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.normals.size * Utils.FloatsPerNormal,normals,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)

        //bind jointIds
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.jointIds.size * Utils.IntsPerJoint, jointIds,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)

        //bind weights
        GLES20.glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.vertexWeights.size * Utils.FloatsPerWeight, weights,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
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
    }

    private fun createFloatBuffer(data:FloatArray): FloatBuffer {
        val verticesDataBytes = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
        val verticesData = verticesDataBytes.asFloatBuffer()
        verticesData.put(data).position(0)
        verticesData.flip()
        return verticesData
    }

    fun createIntBuffer(data:IntArray): IntBuffer {
        val buffer=ByteBuffer.allocate(data.size*4)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    fun checkGlError(name:String) {
        var error = glGetError()
        while (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", "$name----GL Error: $error")
            error = glGetError()
        }
    }
}