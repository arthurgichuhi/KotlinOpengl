package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import android.opengl.GLES32.GL_COLOR_BUFFER_BIT
import android.opengl.GLES32.GL_DEPTH_BUFFER_BIT
import android.opengl.GLES32.glClear
import com.arthurgichuhi.kotlinopengl.camera.MyCamera
import com.arthurgichuhi.kotlinopengl.io_Operations.Input
import java.util.Date

abstract class AScene(val context: Context) {

    var width=0f
    var height=0f

    var objects:MutableList<AObject> = ArrayList()
    private var programs:MutableMap<String, Program> = HashMap()
    private var textures:MutableMap<String,Texture> = HashMap()

    var camera=MyCamera()
    var myInput: Input?=null

    protected var sceneUpdate:SceneUpdateCall?=null
    fun setUpdateCall(sceneUpdateCall: SceneUpdateCall){
        sceneUpdate = sceneUpdateCall
    }

    abstract fun updateReceivers(receiver:IReceiveInput)

    fun initObjects(){
        for(obj in objects){
            obj.setup(this)
            obj.initialize(true)
        }
    }

    open fun destroy(){
        for(obj in objects){
            obj.destroy()
        }
        objects.clear()
        for(prog in programs.values){
            prog.destroy()
        }
        programs.clear()
        for(tex in textures.values){
            tex.destroy()
        }
        textures.clear()
    }

    fun addObject(obj:AObject){
        objects.add(obj)
    }

    fun removeObj(obj: AObject){
        objects.remove(obj)
        obj.destroy()
    }

    fun updateObjects(){
        val time= Date().time
        for(obj in objects){
            if(!obj.isInitialized()){
                obj.onInit()
                obj.initialize(true)
            }
            obj.update(time)
        }
    }

    private fun drawObjects(){
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

    fun draw() {
        val ts = Date().time
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        updateObjects()
        if(sceneUpdate!=null){
            sceneUpdate?.updateScene(ts,this)
        }
        drawObjects()
    }


}