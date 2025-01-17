package com.arthurgichuhi.kotlinopengl.shaders

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32
import android.util.Log
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO

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

    fun sendVertexDataToGL(data:FloatArray,position:Int):IntArray{
        Log.d(TAG,"Float Array Size -------- ${data.size}")
        val temp=IntArray(1)
        //create VAO and get VAOID
        GLES32.glGenVertexArrays(1,temp,0)
        val vaoID=temp[0]
        //create VBO and get its ID
        GLES32.glGenBuffers(1,temp,0)
        val vboID=temp[0]

        GLES32.glBindVertexArray(vaoID)

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,vboID)
        GLES32.glBufferData(GLES20.GL_ARRAY_BUFFER,data.size*4,myIO.createFloatBuffer(data),GLES32.GL_STATIC_DRAW)

        GLES32.glEnable(position)

        GLES32.glVertexAttribPointer(position,3,GLES32.GL_FLOAT,false,12,0)

        return intArrayOf(vaoID,vboID)
    }
}