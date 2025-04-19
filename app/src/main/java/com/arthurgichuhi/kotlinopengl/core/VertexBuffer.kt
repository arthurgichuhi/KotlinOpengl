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
import android.opengl.GLES32.glVertexAttribIPointer
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.MeshPrimitiveModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class VertexBuffer {
    private var vaoID = -1
    private var vboID = -1
    private var eboId = -1
    private lateinit var floatBuffer: FloatBuffer
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

    /**
     * Buffer vertice, texture Coordinates, Normals, JointIds, Weights and indices buffer
     * @param meshData contains all the data needed for buffering
     * @param locs a HashMap that contains buffer locations
     * @param staticDraw determines whether the buffered data will be static or dynamic
     * @param loadTex this is function trigger to send the texture data to OpenGL on the ColladaObj
     */

    fun loadFloatVertexData(meshData: MeshData,locs:Map<String,Int>,staticDraw: Boolean,loadTex: () -> Unit){
        val vertex = FloatBuffer.wrap(meshData.vertices)
        val texCord = FloatBuffer.wrap(meshData.textureCords)
        val normals = FloatBuffer.wrap(meshData.normals)
        val weights = if(locs.size>3)FloatBuffer.wrap(meshData.vertexWeights) else null
        glBindVertexArray(vaoID)
        val tmp = IntArray(locs.size)
        glGenBuffers(locs.size,tmp,0)
        //bind position
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.vertices.size * 4,vertex,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["position"]!!)
        glVertexAttribPointer(locs["position"]!!, 3, GL_FLOAT, false, 0, 0)

        //bind texCoords
        glBindBuffer(GL_ARRAY_BUFFER,tmp[1])
        glBufferData(GL_ARRAY_BUFFER,meshData.textureCords.size * 4,texCord,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["tex"]!!)
        glVertexAttribPointer(locs["tex"]!!, 2, GL_FLOAT, false, 0, 0)
        loadTex()

        //bind normals
        glBindBuffer(GL_ARRAY_BUFFER,tmp[2])
        glBufferData(GL_ARRAY_BUFFER,meshData.normals.size * 4,normals,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["normal"]!!)
        glVertexAttribPointer(locs["normal"]!!, 3, GL_FLOAT, false, 0, 0)

       //bind weights
        if(locs.size>3) {
            glBindBuffer(GL_ARRAY_BUFFER,tmp[3])
            glBufferData(GL_ARRAY_BUFFER,meshData.vertexWeights.size * 4,weights,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(locs["weights"]!!)
            glVertexAttribPointer(locs["weights"]!!, 3, GL_FLOAT, false, 0, 0)
        }
    }

    fun loadIntVertexData(meshData: MeshData,locs:Map<String,Int>,staticDraw: Boolean){
        val tmp = IntArray(1)
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(GL_ARRAY_BUFFER,meshData.jointIds.size*4,IntBuffer.wrap(meshData.jointIds),if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["jointIndices"]!!)
        glVertexAttribIPointer(locs["jointIndices"]!!,3, GL_INT, 3 * 4, 0)
    }

    fun loadGltfIndices(primitive: MeshPrimitiveModel,staticDraw: Boolean){
        val indices = primitive.indices
        val iBuff = readAccessorAsShortBuffer(indices)

        Log.d("TAG","INDICES${indices.bufferViewModel.bufferViewData.capacity()} or ${iBuff.capacity()}")
        val tmp = IntArray(1)
        glBindVertexArray(vaoID)
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboId)
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER,primitive.indices.count * 2,
            iBuff,
            if(staticDraw)GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
    }

    fun loadGltfFloats(primitive: MeshPrimitiveModel, locs: Map<String, Int>,loadTex: () -> Unit,staticDraw: Boolean){
        glBindVertexArray(vaoID)
        val tmp = IntArray(locs.size)
        glGenBuffers(locs.size,tmp,0)

        //bind position
        val positions = primitive.attributes["POSITION"]!!
        floatBuffer = readAccessorAsFloatBuffer(positions)
        floatBuffer.flip()
        Log.d("TAG","Positions${floatBuffer.capacity()} or ${positions.bufferViewModel.bufferViewData.capacity()}\n${floatBuffer.array().toList()}")
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(
            GL_ARRAY_BUFFER,positions.count * Utils.FloatsPerPosition,
            positions.bufferViewModel.bufferViewData,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["position"]!!)
        glVertexAttribPointer(locs["position"]!!, 3, GL_FLOAT, false, 0, 0)
        floatBuffer.clear()

        //bind texCoords
        val tex = primitive.attributes["TEXCOORD_0"]!!
        floatBuffer = readAccessorAsFloatBuffer(tex)
        floatBuffer.flip()
        Log.d("TAG","TextureCoords ${floatBuffer.array().toList()}")
        glBindBuffer(GL_ARRAY_BUFFER,tmp[1])
        glBufferData(
            GL_ARRAY_BUFFER,tex.count * Utils.FloatsPerTexture,
            tex.bufferViewModel.bufferViewData,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["tex"]!!)
        glVertexAttribPointer(locs["tex"]!!, 2, GL_FLOAT, false, 0, 0)
        loadTex()
        floatBuffer.clear()
        //bind normals
        val normals = primitive.attributes["NORMAL"]!!
        floatBuffer = readAccessorAsFloatBuffer(normals)
        floatBuffer.flip()
        Log.d("TAG","Positions\n${floatBuffer.array().toList()}")
        glBindBuffer(GL_ARRAY_BUFFER,tmp[2])
        glBufferData(
            GL_ARRAY_BUFFER,normals.count * Utils.FloatsPerNormal,
            normals.bufferViewModel.bufferViewData,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["normal"]!!)
        glVertexAttribPointer(locs["normal"]!!, 3, GL_FLOAT, false, 0, 0)
        floatBuffer.clear()
        //bind weights
        if(locs.size>3) {
            val weights = primitive.attributes["WEIGHTS_0"]!!
            floatBuffer = readAccessorAsFloatBuffer(weights)
            floatBuffer.flip()
            Log.d("TAG","Positions\n${floatBuffer.array().toList()}")
            glBindBuffer(GL_ARRAY_BUFFER,tmp[3])
            glBufferData(
                GL_ARRAY_BUFFER,weights.count * Utils.FloatsPerWeight,
                weights.bufferViewModel.bufferViewData,
                if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
            glEnableVertexAttribArray(locs["weights"]!!)
            glVertexAttribPointer(locs["weights"]!!, weights.elementType.numComponents, GL_FLOAT, false, 0, 0)
            floatBuffer.clear()
        }
    }

    fun loadGltfInt(primitive: MeshPrimitiveModel, locs: Map<String, Int>, staticDraw: Boolean){
        val joints = primitive.attributes["JOINTS_0"]!!

        val tmp = IntArray(1)
        glGenBuffers(1,tmp,0)
        glBindBuffer(GL_ARRAY_BUFFER,tmp[0])
        glBufferData(
            GL_ARRAY_BUFFER,joints.count * 4,
            joints.bufferViewModel.bufferViewData,
            if(staticDraw) GL_STATIC_DRAW else GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(locs["jointIndices"]!!)
        glVertexAttribIPointer(locs["jointIndices"]!!,3, GL_INT, 8, 0)
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

    private fun readAccessorAsFloatBuffer(accessor: AccessorModel): FloatBuffer {
        val bufferView = accessor.bufferViewModel
        val byteBuffer = bufferView.bufferViewData
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val byteLength = accessor.count * accessor.byteStride
        byteBuffer.position(0)
        byteBuffer.limit(byteLength)

        val directFloatBuffer = FloatBuffer.allocate(accessor.count * accessor.elementType.numComponents) // Direct allocation
        val sourceFloatBuffer = byteBuffer.asFloatBuffer()
        directFloatBuffer.put(sourceFloatBuffer)
        directFloatBuffer.rewind()

        return directFloatBuffer
    }

    private fun readAccessorAsIntBuffer(accessor: AccessorModel): IntBuffer{

        val bufferView = accessor.bufferViewModel
        val byteBuffer = bufferView.bufferViewData
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val byteLength = accessor.count * accessor.byteStride
        byteBuffer.position(0)
        byteBuffer.limit(byteLength)

        val directIntBuffer = IntBuffer.allocate(accessor.count * accessor.elementType.numComponents) // Direct allocation
        val sourceIntBuffer = byteBuffer.asIntBuffer()
        directIntBuffer.put(sourceIntBuffer)
        directIntBuffer.rewind()
        return directIntBuffer
    }

    private fun readAccessorAsShortBuffer(accessor: AccessorModel): ShortBuffer{

        val bufferView = accessor.bufferViewModel
        val byteBuffer = bufferView.bufferViewData
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        val byteLength = accessor.count * accessor.byteStride // 2 bytes per UNSIGNED_SHORT
        byteBuffer.position(0)
        byteBuffer.limit(byteLength)

        val directBuffer = ShortBuffer.allocate(accessor.count * accessor.elementType.numComponents)
        directBuffer.put(byteBuffer.asShortBuffer())
        directBuffer.rewind()
        return directBuffer
    }
}