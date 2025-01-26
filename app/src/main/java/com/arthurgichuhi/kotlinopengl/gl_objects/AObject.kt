package com.arthurgichuhi.kotlinopengl.gl_objects

import android.opengl.GLES32.*
import android.opengl.Matrix
import android.os.Build
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.utils.GlUtils

abstract class AObject {
    lateinit var mScene:AScene
    protected var modelMat:FloatArray

    init {
        modelMat=FloatArray(16)
        Matrix.setIdentityM(modelMat,0)
    }

    abstract fun onInit()
    abstract fun destroy(aScene: AScene)
    abstract fun update(time:Long)
    abstract fun draw(viewMat:FloatArray,projectionMat:FloatArray)

    fun setup(scene:AScene){
        modelMat=FloatArray(16)
        Matrix.setIdentityM(modelMat,0)
        mScene=scene
        onInit()
    }

    fun translate(move: Vec3){
        Matrix.translateM(modelMat,0,move.x,move.y,move.z)
    }
     fun rotate(rot:Vec3){
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
             Matrix.setRotateEulerM2(modelMat,0,rot.x,rot.y,rot.z)
         }
         else{
             Matrix.setRotateEulerM(modelMat,0,rot.x,rot.y,rot.z)
         }
     }

    fun drawTriangles(first:Int,count:Int){
        glDrawArrays(GL_TRIANGLES,first,count)
        GlUtils().checkErr(2)
    }
}