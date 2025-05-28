package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32.*
import android.util.Log
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.utils.Utils
import com.arthurgichuhi.kotlinopengl.utils.GlUtils
import org.joml.Matrix4f

class Program {
    private val TAG="Program"
    var progID=-1
    private var mVertexShaderId = -1
    private var mFragmentShaderId = -1
    private var mVertCode: String = ""
    private var mFragCode: String = ""

    fun loadProgram(context: Context,name:String): Program {
        val p= Program()
        p.createProgram(context,name)
        return p
    }
    /**
     * Creates a shader Program
     * @param context MainActivity Context
     * @param name Name of the program and name of files(.vert,.frag) where shader code is
     */

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
        glDeleteShader(mVertexShaderId)
        glDeleteShader(mFragmentShaderId)
    }
    /**
    *Compiles the programs shader using
    *@param ctx Context of the Activity
    *@param name  Name of fragment and vertex shader stored in assets
    *@param type Which is the shader type
     */

    private fun compileShader(ctx: Context, name: String, type: Int): Int {
        val shaderCode: String
        if (type == GL_VERTEX_SHADER) {
            shaderCode = Utils.readAssetFile(ctx, "shaders/$name.vert")!!
            mVertCode = shaderCode
        } else {
            shaderCode = Utils.readAssetFile(ctx, "shaders/$name.frag")!!
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

    fun destroy(){
        if(progID != -1){
            glDeleteShader(progID)
            progID = -1
        }
    }
    /**
     * Retrieves Buffer location for the attribute in the vertex shader.
     * @param name The name of the attribute being searched for
     */
    fun getAttribLoc(name:String):Int{
        return glGetAttribLocation(progID,name)
    }

    /**
     * Retrieves Buffer location for the uniform in the vertex shader.
     * @param name The name of the uniform being searched for
     */
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
            stride*Utils.BytesPerFloat,offset*Utils.BytesPerFloat)
    }

    fun setUniform3fv(name:String,value:FloatArray){
        val loc = getUniformLoc(name)
        glUniform3fv(loc,1,value,0)
    }

    fun setUniform3f(name:String,value: Vec3f){
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

    fun setInt(name: String, size: Int, stride: Int, offset: Int) {
        val loc=getAttribLoc(name)
        glEnableVertexAttribArray(loc)
        glVertexAttribPointer(loc,size, GL_INT,false,
                stride * Utils.BytesPerInt,offset * Utils.BytesPerInt)
    }

}