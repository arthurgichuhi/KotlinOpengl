package com.arthurgichuhi.kotlinopengl.camera

import android.opengl.GLES32.*
import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec3
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
    var rotation:Vec3=Vec3(0f,0f,0f)

    var width=0
    var height=0

    lateinit var myScene: MyScene

    fun getScene(aScene: MyScene){
        myScene=aScene
    }

    fun update(){
        glViewport(0,0,width,height)
        val aspect = width.toFloat()/height.toFloat()

        Matrix.setIdentityM(viewMat,0)

        Matrix.rotateM(viewMat, 0, rotation.x, 1f, 0f, 0f) // Rotate around X-axis
        Matrix.rotateM(viewMat, 0, rotation.y, 0f, 1f, 0f)
        Matrix.rotateM(viewMat,0,rotation.z,0f,0f,1f)

        Matrix.translateM(viewMat, 0, position.x, position.y, position.z)

        Matrix.perspectiveM(projectionMat,0,45f,aspect,.1f,100f)
    }

    fun resetCamera(){
        position=Vec3(0f,0f,-3f)
        rotation=Vec3(0f,0f,0f)
        Matrix.setIdentityM(viewMat,0)
        Matrix.translateM(viewMat,0,position.x, position.y, position.z)

    }
}