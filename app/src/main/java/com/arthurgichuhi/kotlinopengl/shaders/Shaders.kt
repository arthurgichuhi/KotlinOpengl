package com.arthurgichuhi.kotlinopengl.shaders

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLES32.*
import android.opengl.GLUtils
import android.util.Log
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO
import com.arthurgichuhi.kotlinopengl.utils.GlUtils


class Shaders(context: Context) {
    private val TAG="Shaders-Class"
    private val myIO=MyIO(context)
    private fun compileShaderCode(shaderCode:String, type:Int):Int{
        //create shader
        val shaderID= glCreateShader(type)
        //compile shader
        glShaderSource(shaderID,shaderCode)
        glCompileShader(shaderID)
        //verify compilation success
        val compileStatus=IntArray(1)
        glGetShaderiv(shaderID,GL_COMPILE_STATUS,compileStatus,0)
        if(compileStatus[0]==0||compileStatus[0]==-1){
            Log.e(TAG,"-Error-\n${glGetShaderInfoLog(shaderID)}")
            return -1
        }
        return shaderID
    }

    fun createProgram(name:String):Int{
        val program=glCreateProgram()
        if(program<=0){
            return -1
        }
        //getting shaders from assets and compiling them
        val vertexShader=compileShaderCode(myIO.readShaders("shaders/$name.vert"),GL_VERTEX_SHADER)
        val fragmentShader =compileShaderCode(myIO.readShaders("shaders/$name.frag"),GL_FRAGMENT_SHADER)

        if(vertexShader<=0 || fragmentShader<=0){
            return -1
        }
        //attaching shaders to program
        glAttachShader(program,vertexShader)
        glAttachShader(program,fragmentShader)
        GlUtils().checkErr(1)
        //
        glLinkProgram(program)
        val linkState=IntArray(1)
        glGetProgramiv(program, GL_LINK_STATUS,linkState,0)
        Log.d(TAG,"Link Status ------- ${linkState[0]}")
        if(linkState[0]==0){
            Log.e(TAG,"Error\n${glGetProgramInfoLog(program)}")
        }
        return program
    }
    fun sendVertexDataToGL(data: FloatArray, glPosition: Int,glColor:Int,glTex:Int): IntArray {
        val tmp = IntArray(1)
        glGenVertexArrays(1, tmp, 0)
        val glArrayId = tmp[0]

        glGenBuffers(1, tmp, 0)
        val glBufferId = tmp[0]

        glBindVertexArray(glArrayId)

        glBindBuffer(GL_ARRAY_BUFFER, glBufferId)
        glBufferData(GL_ARRAY_BUFFER,data.size * 4,myIO.createFloatBuffer(data),GL_STATIC_DRAW)

        glEnableVertexAttribArray(glPosition)
        glVertexAttribPointer(glPosition,3,GL_FLOAT,false, 12,0)

        glEnableVertexAttribArray(glColor)
        glVertexAttribPointer(glColor, 3, GL_FLOAT, false, 32, 12)

        glEnableVertexAttribArray(glTex)
        glVertexAttribPointer(glTex, 2, GL_FLOAT, false, 32, 24)

        return intArrayOf(glArrayId, glBufferId)
    }

    fun sendTextureToGl(bitmap:Bitmap):Int{
        val textureHandle = IntArray(1)
        glGenTextures(1, textureHandle,0)
        if(textureHandle[0]==0){
            return 0
        }
        glBindTexture(GL_TEXTURE_2D, textureHandle[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
        bitmap.recycle();
        return textureHandle[0]
    }

    fun bindTexture(texId:Int){
        glBindTexture(GL_TEXTURE_2D,texId)
    }
}