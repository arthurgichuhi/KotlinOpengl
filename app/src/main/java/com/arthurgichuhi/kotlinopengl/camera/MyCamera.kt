package com.arthurgichuhi.kotlinopengl.camera

import android.opengl.GLES32.*
import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec2f
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.alearnTest.gl.core.LScene
import com.arthurgichuhi.kotlinopengl.gl_objects.AScene
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene

class MyCamera {
    val viewMat=FloatArray(16)
    val projectionMat=FloatArray(16)

    init {
        Matrix.setIdentityM(viewMat,0)
        Matrix.setIdentityM(projectionMat,0)
        Matrix.translateM(viewMat,0,0f,0f,-3f)
    }

    var position:Vec3=Vec3(0f,0f,-3f)
    var rotation:Vec2f=Vec2f(0f,0f)

    var width=0
    var height=0

    lateinit var myScene: MyScene

    fun getScene(aScene: MyScene){
        myScene=aScene
    }

    fun update(){
        glViewport(0,0,width,height)
        val aspect = width.toFloat()/height.toFloat()
        Matrix.perspectiveM(projectionMat,0,45f,aspect,.1f,100f)
    }

    fun resetCamera(){
        position=Vec3(0f,0f,-3f)
        rotation=Vec2f(0f,0f)
    }
}