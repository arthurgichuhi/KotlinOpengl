package com.arthurgichuhi.kotlinopengl.gl_objects

import android.content.Context
import android.util.Log
import com.arthurgichuhi.kotlinopengl.camera.MyCamera
import com.arthurgichuhi.kotlinopengl.shaders.Program
import java.util.Date

open class AScene(val context: Context) {
    private val TAG="AScene"

    var width=0f
    var height=0f

    val mContext=context

    private var objects:MutableList<AObject> = ArrayList()
    private var programs:MutableMap<String,Program> = HashMap()
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

    fun loadProgram(ctx:Context,name:String):Program{
        if(programs.containsKey(name)){
                return programs[name]!!
        }
        val p = Program().loadProgram(context,name)
        programs[name]=p
        return p
    }

    fun loadTexture(name:String):Texture{
        if(textures.containsKey(name)){
            return textures[name]!!
        }
        val t=Texture()
        textures[name]=t
        return t.loadTexture(name,context)
    }



}