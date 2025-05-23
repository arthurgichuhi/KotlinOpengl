package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import android.opengl.GLES32.GL_CLAMP_TO_EDGE
import android.opengl.GLES32.GL_NEAREST
import android.opengl.GLES32.GL_TEXTURE_2D
import android.opengl.GLES32.GL_TEXTURE_CUBE_MAP
import android.opengl.GLES32.GL_TEXTURE_CUBE_MAP_POSITIVE_X
import android.opengl.GLES32.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES32.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES32.GL_TEXTURE_WRAP_R
import android.opengl.GLES32.GL_TEXTURE_WRAP_S
import android.opengl.GLES32.GL_TEXTURE_WRAP_T
import android.opengl.GLES32.glBindTexture
import android.opengl.GLES32.glDeleteTextures
import android.opengl.GLES32.glGenTextures
import android.opengl.GLES32.glGenerateMipmap
import android.opengl.GLES32.glTexParameteri
import android.opengl.GLUtils
import com.arthurgichuhi.kotlinopengl.utils.Utils
import javax.microedition.khronos.opengles.GL10.GL_LINEAR

class Texture {
    var id=-1
    var isCube = false

    fun loadTexture(context: Context,path:String):Texture{
        val t=Texture()
        t.isCube = false
        t.sendTextureToGL(context,path)
        return t
    }

    fun loadCubeTex(context: Context,textures:List<String>):Texture{
        val t = Texture()
        t.isCube = true
        t.sendCubeTexToGL(context,textures)
        return t
    }

    private fun genID(){
        val textureHandle = IntArray(1)
        glGenTextures(1, textureHandle,0)
        if(textureHandle[0]==0){
            throw RuntimeException("Error Loading texture")
        }
        id=textureHandle[0]
    }

    private fun sendTextureToGL(context: Context,path:String){
        genID()
        bindTexture()

        val bitmap= Utils.getBitmapFromAssets(context,path)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glGenerateMipmap(GL_TEXTURE_2D)

        bitmap.recycle()
    }

    private fun sendCubeTexToGL(context: Context,texIds:List<String>){
        genID()
        bindTexture()

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

        for(i in texIds.indices){
            val bitmap = Utils.getBitmapFromAssets(context,texIds[i])
            GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X+i,0,bitmap,0)
            bitmap.recycle()
        }
    }

    fun bindTexture(){
        if(isCube){
            glBindTexture(GL_TEXTURE_CUBE_MAP,id)
        }
        else{
            glBindTexture(GL_TEXTURE_2D,id)
        }
    }

    fun destroy(){
        if(id!=-1){
            glDeleteTextures(1, intArrayOf(id),0)
        }
    }
}