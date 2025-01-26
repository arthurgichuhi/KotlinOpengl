package com.arthurgichuhi.kotlinopengl.io_Operations

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLUtils
import android.util.Log
import java.io.BufferedReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class MyIO(val context: Context) {
    private val TAG="MyIO"
    //read shader code stored in assets
    fun readShaders(name:String):String{
        val assetManager: AssetManager =context.assets
        val shaderCodeBuffer=StringBuffer()
        //read assets/shaders and create buffered reader
        assetManager.open(name).use {
            val bfReader=it.bufferedReader()
            //read buffered reader
            var read=bfReader.readLine()
            while (read!=null){
                shaderCodeBuffer.append(read+"\n")
                read=bfReader.readLine()
            }
            //delete last new line
            shaderCodeBuffer.deleteCharAt(shaderCodeBuffer.length-1)
        }
        return shaderCodeBuffer.toString()
    }

    fun loadTexture(location:String): Bitmap? {
        val options = BitmapFactory.Options()
        // No pre-scaling
        options.inScaled = false

        var bitmap: Bitmap?
        try {
            context.assets.open(location).use { inputStream ->
                bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            throw e
        }
        return bitmap
    }

    fun createFloatBuffer(data:FloatArray): FloatBuffer {
        val verticesDataBytes = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
        val verticesData = verticesDataBytes.asFloatBuffer()
        verticesData.put(data).position(0)
        return verticesData
    }

    fun createIntBuffer(data:IntArray):IntBuffer{
        val buffer=ByteBuffer.allocate(data.size*4)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(data)
        buffer.flip()
        return buffer
    }
}