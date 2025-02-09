package com.arthurgichuhi.kotlinopengl.core

import android.opengl.GLES32.*
import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.utils.GlUtils
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import java.util.Date

abstract class AObject {
    lateinit var mScene:AScene
    protected var modelMat:FloatArray
    private var updateCall:ObjUpdateCall?=null
    private val mathUtils =MathUtils()

    init {
        modelMat=FloatArray(16)
        Matrix.setIdentityM(modelMat,0)
    }

    abstract fun onInit()
    abstract fun destroy(aScene: AScene)
    abstract fun onUpdate(time:Long)
    abstract fun draw(viewMat:FloatArray,projectionMat:FloatArray)

    fun setup(scene:AScene){
        modelMat=FloatArray(16)
        Matrix.setIdentityM(modelMat,0)
        mScene=scene
        onInit()
    }

    fun setUpdateCall(call:ObjUpdateCall){
        updateCall=call
    }

    fun update(ts:Long){
        if(updateCall!=null){
            updateCall?.update(ts,this)
        }
        onUpdate(ts)
    }

    fun translate(move: Vec3){
        mathUtils.translateMat4(modelMat,move.toArray())
    }

    fun setTransMat4(move: Vec3){
        mathUtils.setTransMat4(modelMat,move.toArray())
    }

    fun rotate(angle:Float,rot:Vec3){
         Matrix.rotateM(modelMat,0,angle,rot.x,rot.y,rot.z)
     }

    fun drawTriangles(first:Int,count:Int){
        glDrawArrays(GL_TRIANGLES,first,count)
        GlUtils().checkErr(2)
    }

    fun drawLines(first:Int,count: Int,lineWidth:Float){
        glLineWidth(lineWidth)
        glDrawArrays(GL_LINES,first, count)
    }

    fun setDepthFunEqual(){
        glDepthFunc(GL_EQUAL)
    }

    fun setDepthTestFunLess(){
        glDepthFunc(GL_LESS)
    }
}