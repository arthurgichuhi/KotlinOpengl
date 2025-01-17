package com.arthurgichuhi.kotlinopengl.shaders

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32
import android.util.Log
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO
import java.nio.ByteBuffer
import java.nio.ByteOrder


class Shaders(context: Context) {
    val TAG="Shaders-Class"
    val myIO=MyIO(context)
    fun compileShaderCode(shaderCode:String,type:Int):Int{
        //create shader
        val shaderID= GLES32.glCreateShader(type)
        //compile shader
        GLES32.glShaderSource(shaderID,shaderCode)
        GLES32.glCompileShader(shaderID)
        //verify compilation success
        val compileStatus=IntArray(1)
        GLES20.glGetShaderiv(shaderID,GLES20.GL_COMPILE_STATUS,compileStatus,0)
        if(compileStatus[0]==0){
            Log.e(TAG,"Error -- ${GLES20.glGetShaderInfoLog(shaderID)}")
            return -1
        }
        return shaderID
    }

    fun createProgram(vsName:String,fsName:String):Int{
        val program=GLES32.glCreateProgram()
        if(program==0){
            return -1
        }
        //getting shaders from assets and compiling them
        val vertexShader=compileShaderCode(myIO.readShaders(vsName),GLES32.GL_VERTEX_SHADER)
        val fragmentShader =compileShaderCode(myIO.readShaders(fsName),GLES32.GL_FRAGMENT_SHADER)

        if(vertexShader==-1 || fragmentShader==-1){
            return -1
        }
        //attaching shaders to program
        GLES32.glAttachShader(program,vertexShader)
        GLES32.glAttachShader(program,fragmentShader)
        //
        GLES32.glLinkProgram(program)
        Log.d(TAG,"Program ID -- $program\n Vertex Shader - $vertexShader\n FragShader - $fragmentShader" +
                "")
        return program
    }
    fun sendVertexDataToGL(data: FloatArray, gl_position: Int): IntArray {
        val vertices_data_bytes = ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
        val vertices_data = vertices_data_bytes.asFloatBuffer()
        vertices_data.put(data).position(0)


        val tmp = IntArray(1)
        GLES32.glGenVertexArrays(1, tmp, 0)
        val gl_array_id = tmp[0]

        GLES32.glGenBuffers(1, tmp, 0)
        val gl_buffer_id = tmp[0]

        GLES32.glBindVertexArray(gl_array_id)

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, gl_buffer_id)
        GLES32.glBufferData(
            GLES32.GL_ARRAY_BUFFER,
            data.size * 4,
            myIO.createFloatBuffer(data),
            GLES32.GL_STATIC_DRAW
        )

        GLES32.glEnableVertexAttribArray(gl_position)
        GLES32.glVertexAttribPointer(
            gl_position, 3, GLES32.GL_FLOAT, false,
            12, 0
        )

        return intArrayOf(gl_array_id, gl_buffer_id)
    }
}