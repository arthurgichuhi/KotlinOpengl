package com.arthurgichuhi.kotlinopengl.io_Operations

import android.content.Context
import android.content.res.AssetManager
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
    fun readShaders(fileName:String):String{
        val assetManager: AssetManager =context.assets
        val shaderCodeBuffer=StringBuffer()
        //read assets/shaders and create buffered reader
        assetManager.open("shaders/$fileName").use {
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

    fun loadTexture(location:String):Int{
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle,0)
        if(textureHandle[0]==0){
            return 0
        }
        val options = BitmapFactory.Options()
        // No pre-scaling
        options.inScaled = false

        var bitmap: android.graphics.Bitmap?
        try {
            context.assets.open(location).use { inputStream ->
                bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            throw e
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle[0]);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D);
        bitmap?.recycle();
        Log.d(TAG,"Texture Id-----------------${textureHandle[0]}")
        return textureHandle[0]
    }

    fun createFloatBuffer(data:FloatArray): FloatBuffer {
        val vertices_data_bytes = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
        val vertices_data = vertices_data_bytes.asFloatBuffer()
        vertices_data.put(data).position(0)
        return vertices_data
    }

    fun createIntBuffer(data:IntArray):IntBuffer{
        val buffer=ByteBuffer.allocate(data.size*4)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(data)
        buffer.flip()
        return buffer
    }
}