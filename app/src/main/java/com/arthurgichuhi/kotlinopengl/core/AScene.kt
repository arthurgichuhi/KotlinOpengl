package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import android.opengl.GLES32.GL_COLOR_BUFFER_BIT
import android.opengl.GLES32.GL_DEPTH_BUFFER_BIT
import android.opengl.GLES32.glClear
import com.arthurgichuhi.kotlinopengl.camera.MyCamera
import java.util.Date
import javax.microedition.khronos.opengles.GL10

open class AScene(val context: Context) {
    private val TAG="AScene"

    var width=0f
    var height=0f


    private var objects:MutableList<AObject> = ArrayList()
    private var programs:MutableMap<String, Program> = HashMap()
    private var textures:MutableMap<String,Texture> = HashMap()

    var camera=MyCamera()

    fun initObjects(){
        for(obj in objects){
            obj.setup(this)
        }
    }

    fun addObject(obj:AObject){
        objects.add(obj)
    }

    fun updateObjects(){
        val time= Date().time
        for(obj in objects){
            obj.update(time)
        }
    }

    fun drawObjects(){
        for(obj in objects){
            obj.draw(camera.viewMat,camera.projectionMat)
        }
    }

    fun loadProgram(name:String): Program {
        if(programs.containsKey(name)){
                return programs[name]!!
        }
        val p = Program().loadProgram(context,name)
        programs[name]=p
        return p
    }

    fun loadTexture(path:String):Texture{
        if(textures.containsKey(path)){
            return textures[path]!!
        }
        val t=Texture()
        textures[path]=t
        return t.loadTexture(context,path)
    }

    fun loadCubeTex(paths:List<String>):Texture{
        if(textures.containsKey(paths[0])){
            return textures[paths[0]]!!
        }
        val t = Texture().loadCubeTex(context,paths)
        textures[paths[0]]=t
        return t
    }

    fun draw(gl10: GL10?){
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        updateObjects()
        drawObjects()
    }


}