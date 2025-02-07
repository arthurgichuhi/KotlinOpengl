package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32.*
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.utils.Utils
import com.arthurgichuhi.kotlinopengl.utils.GlUtils

class Program {
    private val TAG="Program"
    var progID=-1
    private var utils=Utils()
    private var mVertexShaderId = -1
    private var mFragmentShaderId = -1
    private var mVertCode: String = ""
    private var mFragCode: String = ""

    fun loadProgram(context: Context,name:String): Program {
        val p= Program()
        p.createProgram(context,name)
        return p
    }

    private fun createProgram(context: Context,name:String){
        mVertexShaderId=compileShader(context,name, GL_VERTEX_SHADER)
        mFragmentShaderId=compileShader(context,name, GL_FRAGMENT_SHADER)
        
        progID= glCreateProgram()

        glAttachShader(progID, mVertexShaderId)
        glAttachShader(progID, mFragmentShaderId)
        glLinkProgram(progID)

        GlUtils().checkErr(0)
        val success = IntArray(1)
        glGetProgramiv(progID, GL_LINK_STATUS, success, 0)
        // error
        if (success[0] == 0) {
            val str = glGetProgramInfoLog(progID)
            Log.e(TAG, "Error Linking Program : $str")
        }
    }

    private fun compileShader(ctx: Context, name: String, type: Int): Int {
        val shaderCode: String
        if (type == GL_VERTEX_SHADER) {
            shaderCode = Utils().readAssetFile(ctx, "$name.vert")!!
            mVertCode = shaderCode
        } else {
            shaderCode = Utils().readAssetFile(ctx, "$name.frag")!!
            mFragCode = shaderCode
        }
        val shaderId = glCreateShader(type)
        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)

        // Get the compilation status.
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val str = glGetShaderInfoLog(shaderId)
            Log.e(TAG, "Error compiling shader: $str")
            glDeleteShader(shaderId)
            return -1
        }
        return shaderId
    }

    private fun getAttribLoc(name:String):Int{
        return glGetAttribLocation(progID,name)
    }

    private fun getUniformLoc(name:String):Int{
        return glGetUniformLocation(progID,name)
    }

    fun setFloat(
        name:String,size:Int,
        stride:Int,offset:Int
    ) {
        val loc=getAttribLoc(name)
        glEnableVertexAttribArray(loc)
        glVertexAttribPointer(
            loc,size, GL_FLOAT,false,
            stride*utils.BytesPerFloat,offset*utils.BytesPerFloat)
    }

    fun setUniform3fv(name:String,value:FloatArray){
        val loc = getUniformLoc(name)
        GLES20.glUniform3fv(loc,1,value,0)
    }

    fun setUniform3f(name:String,value: Vec3){
        val loc=getUniformLoc(name)
        glUniform3f(loc,value.x,value.y,value.z)
    }

    fun setUniformInt(name:String,value:Int){
        val loc=getUniformLoc(name)
        glUniform1i(loc,value)
    }

    fun setUniformFloat(name:String,value:Float){
        val loc = getUniformLoc(name)
        glUniform1f(loc,value)
    }

    fun setUniformMat(name:String,data:FloatArray){
        val loc = getUniformLoc(name)
        glUniformMatrix4fv(loc,1,false,data,0)
    }

    fun use(){
        glUseProgram(progID)
    }

}