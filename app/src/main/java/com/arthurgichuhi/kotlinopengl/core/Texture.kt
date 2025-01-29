package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES32.GL_NEAREST
import android.opengl.GLES32.GL_TEXTURE_2D
import android.opengl.GLES32.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES32.glBindTexture
import android.opengl.GLES32.glGenTextures
import android.opengl.GLES32.glGenerateMipmap
import android.opengl.GLES32.glTexParameteri
import android.opengl.GLUtils
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO
import com.arthurgichuhi.kotlinopengl.shaders.Shaders
import com.arthurgichuhi.kotlinopengl.utils.Utils

class Texture {
    var id=-1

    fun loadTexture(context: Context,path:String):Texture{
        val t=Texture()
        t.sendTextureToGL(context,path)
        return t
    }

    fun genID(){
        val textureHandle = IntArray(1)
        glGenTextures(1, textureHandle,0)
        if(textureHandle[0]==0){
            throw RuntimeException("Error Loading texture")
        }
        id=textureHandle[0]
    }

    fun sendTextureToGL(context: Context,path:String){
        genID()
        bindTexture()

        val bitmap=Utils().getBitmapFromAssets(context,path)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glGenerateMipmap(GL_TEXTURE_2D)

        bitmap.recycle()
    }

    fun bindTexture(){
        glBindTexture(GL_TEXTURE_2D,id)
    }
}