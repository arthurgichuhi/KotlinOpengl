package com.arthurgichuhi.kotlinopengl.core

import android.opengl.GLES20
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_INT
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
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.utils.Utils
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class VertexBuffer {
    private var vaoID = -1
    private var vboID = -1
    private var eboId = -1
    companion object{
        private val Utils = Utils()
    }
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
        val tmp = IntArray(1)
        glBindVertexArray(vaoID)
        glGenBuffers(1,tmp,0)
        eboId = tmp[0]
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices.size * 2,buffer,if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
    }

    /**
     * Buffer vertice, texture Coordinates, Normals, JointIds, Weights and indices buffer
     * @param meshData contains all the data needed for buffering
     * @param staticDraw determines whether the buffered data will be static or dynamic
     * @param loadTex this is function trigger to send the texture data to OpenGL on the ColladaObj
     */
    fun loadSkinData(meshData: MeshData, staticDraw: Boolean,loadTex:()->Unit){

        val indices = ShortBuffer.wrap(meshData.indices)
        val vertex = FloatBuffer.wrap(meshData.vertices)
        val texCord = FloatBuffer.wrap(meshData.textureCords)
        val normals = FloatBuffer.wrap(meshData.normals)
        val jointIds = IntBuffer.wrap(meshData.jointIds)
        val weights = FloatBuffer.wrap(meshData.vertexWeights)

        glBindVertexArray(vaoID)

        try{
            val tmp = IntArray(5)
            glGenBuffers(tmp.size,tmp,0)
            //bind position
            Log.d("TAG","POS:${tmp[0]}-TEX:${tmp[1]}")
            glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
            glBufferData(GL_ARRAY_BUFFER,meshData.vertices.size * 4,vertex,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)

            //bind texCoords
            glBindBuffer(GL_ARRAY_BUFFER,tmp[1])
            glBufferData(GL_ARRAY_BUFFER,meshData.textureCords.size * 4,texCord,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(4)
            glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0)
            loadTex()

            //bind normals
            glBindBuffer(GL_ARRAY_BUFFER,tmp[2])
            glBufferData(GL_ARRAY_BUFFER,meshData.normals.size * 4,normals,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(3)
            glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0)

            //bind jointIds
            glBindBuffer(GL_ARRAY_BUFFER,tmp[3])
            glBufferData(GL_ARRAY_BUFFER,meshData.jointIds.size * 4, jointIds,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 3, GL_INT, false, 0, 0)

            //bind weights
            glBindBuffer(GL_ARRAY_BUFFER,tmp[4])
            glBufferData(GL_ARRAY_BUFFER,meshData.vertexWeights.size * 4, weights,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(2)
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0)

            //bind indices
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,vboID)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,meshData.indices.size * 2,indices,
                if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            //glBindBuffer(GL_ARRAY_BUFFER,0)
//            glBindBuffer(GL_ARRAY_BUFFER,1)
//            glBindBuffer(GL_ARRAY_BUFFER,2)
//            glBindBuffer(GL_ARRAY_BUFFER,3)
//            glBindBuffer(GL_ARRAY_BUFFER,4)
//            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,5)
        }catch (e:Exception){
            Log.e("TAG","LIV:$e")
        }
    }
    fun loadVertexData(meshData: MeshData,staticDraw: Boolean,loadTex: () -> Unit){
        val indices = ShortBuffer.wrap(meshData.indices)
        val vertex = FloatBuffer.wrap(meshData.vertices)
        val texCord = FloatBuffer.wrap(meshData.textureCords)
        val normals = FloatBuffer.wrap(meshData.normals)
        glBindVertexArray(vaoID)
        val tmp = IntArray(3)
        glGenBuffers(tmp.size,tmp,0)
        //bind position
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.vertices.size * 4,vertex,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        //bind texCoords
        glBindBuffer(GL_ARRAY_BUFFER,tmp[1])
        glBufferData(GL_ARRAY_BUFFER,meshData.textureCords.size * 4,texCord,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0)
        loadTex()

        //bind normals
        glBindBuffer(GL_ARRAY_BUFFER,tmp[2])
        glBufferData(GL_ARRAY_BUFFER,meshData.normals.size * 4,normals,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(3)
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0)

        //bind indices
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,vboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,meshData.indices.size * 2,indices,
            if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
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

    fun checkGlError(name:String) {
        var error = glGetError()
        while (error != GL_NO_ERROR) {
            Log.e("GL_ERROR", "$name----GL Error: $error")
            error = glGetError()
        }
    }
}