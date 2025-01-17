package com.arthurgichuhi.kotlinopengl.io_Operations

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import java.io.BufferedReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class MyIO(val context: Context) {
    val TAG="MyIO"
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

    fun createFloatBuffer(data:FloatArray): FloatBuffer {
        val buffer=ByteBuffer.allocate(data.size*4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(data).position(0)
        return buffer
    }

    fun createIntBuffer(data:IntArray):IntBuffer{
        val buffer=ByteBuffer.allocate(data.size*4)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(data)
        buffer.flip()
        return buffer
    }
}