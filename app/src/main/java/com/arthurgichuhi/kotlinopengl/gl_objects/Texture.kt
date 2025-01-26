package com.arthurgichuhi.kotlinopengl.gl_objects

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES32.GL_TEXTURE_2D
import android.opengl.GLES32.glBindTexture
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO
import com.arthurgichuhi.kotlinopengl.shaders.Shaders

class Texture {
    var id=-1

    fun loadTexture(name:String,context: Context):Texture{
        val bitmap=MyIO(context).loadTexture(name)
        val t=Texture()
        t.sendTextureToGL(context,bitmap!!)
        return t
    }

    fun sendTextureToGL(context: Context,bitmap: Bitmap):Int{
        id=Shaders(context).sendTextureToGl(bitmap)
        return id
    }

    fun bindTexture(){
        glBindTexture(GL_TEXTURE_2D,id)
    }
}