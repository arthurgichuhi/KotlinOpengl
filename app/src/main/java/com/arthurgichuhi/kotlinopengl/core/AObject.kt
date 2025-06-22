package com.arthurgichuhi.kotlinopengl.core

import android.opengl.GLES32.GL_EQUAL
import android.opengl.GLES32.GL_LESS
import android.opengl.GLES32.GL_LINES
import android.opengl.GLES32.GL_TRIANGLES
import android.opengl.GLES32.GL_UNSIGNED_SHORT
import android.opengl.GLES32.glDepthFunc
import android.opengl.GLES32.glDrawArrays
import android.opengl.GLES32.glDrawElements
import android.opengl.GLES32.glLineWidth
import android.opengl.Matrix
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.utils.GlUtils
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import org.joml.Matrix4f
import org.joml.Quaternionf

abstract class AObject {
    lateinit var mScene:AScene
    protected var modelMat:FloatArray = FloatArray(16)
    private var updateCall:ObjUpdateCall?=null
    private var initialized:Boolean=false

    init {
        Matrix.setIdentityM(modelMat,0)
    }

    abstract fun onInit()
    abstract fun destroy()
    abstract fun onUpdate(time:Long)
    abstract fun draw(viewMat:FloatArray,projectionMat:FloatArray)

    fun isInitialized():Boolean{
        return initialized
    }

    fun initialize(value:Boolean){
        initialized = value
    }

    fun setup(scene:AScene){
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

    fun translate(move: Vec3f){
        MathUtils.translateMat4(modelMat,move.toArray())
    }

    fun setTransMat4(move: Vec3f){
        MathUtils.setTransMat4(modelMat,move.toArray())
    }

    fun rotate(angle:Float,rot: Vec3f){
         Matrix.rotateM(modelMat,0,angle,rot.x,rot.y,rot.z)
    }
    
    fun rotateQuartenion(rot:FloatArray){
        Matrix4f().set(modelMat).rotate(Quaternionf(rot[0],rot[1],rot[2],rot[3])).get(modelMat)
    }

    fun scale(scale:Vec3f){
        Matrix.scaleM(modelMat,0,scale.x,scale.y,scale.z)
    }

    fun drawTriangles(first:Int,count:Int){
        glDrawArrays(GL_TRIANGLES,first,count)
    }

    fun drawLines(first:Int,count: Int,lineWidth:Float){
        glLineWidth(lineWidth)
        glDrawArrays(GL_LINES,first, count)
    }

    fun drawElements(verticesNo:Int){
       glDrawElements(GL_TRIANGLES,verticesNo, GL_UNSIGNED_SHORT,0)
    }

    fun setDepthFunEqual(){
        glDepthFunc(GL_EQUAL)
    }

    fun setDepthTestFunLess(){
        glDepthFunc(GL_LESS)
    }
}