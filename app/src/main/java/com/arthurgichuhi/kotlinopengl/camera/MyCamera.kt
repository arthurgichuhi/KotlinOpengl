package com.arthurgichuhi.kotlinopengl.camera

import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.IReceiveInput
import com.arthurgichuhi.kotlinopengl.core.InputMode
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import kotlin.math.abs

class MyCamera:IReceiveInput {
    private val matUtils = MathUtils()
    val viewMat=FloatArray(16)
    val projectionMat=FloatArray(16)
    var myOrientation:FloatArray

    var defaultPos:Vec3f=Vec3f(0f,0f,-3f)
    var rotation:Vec3f=Vec3f(0f,0f,0f)
    var mUp = Vec3f(0f,1f,0f)

    var defaultOrientation= Vec3f(0f,0f,-1f)

    var width=0f
    var height=0f

    init {
        Matrix.setIdentityM(viewMat,0)
        Matrix.setIdentityM(projectionMat,0)
        Matrix.translateM(viewMat,0,0f,0f,-10f)
        myOrientation=matUtils.makeANewCopy(defaultOrientation.toArray())
        //resetCamera()
    }



    lateinit var myScene: MyScene

    fun getScene(aScene: MyScene){
        myScene=aScene
    }

    fun update(){
        val aspect = width/height
//        Matrix.rotateM(viewMat, 0, rotation.x, 1f, 0f, 0f) // Rotate around X-axis
//        Matrix.rotateM(viewMat, 0, rotation.y, 0f, 1f, 0f)
//        Matrix.rotateM(viewMat,0,rotation.z,0f,0f,1f)

//        Matrix.translateM(viewMat, 0, defaultPos.x, defaultPos.y, defaultPos.z)

        Matrix.perspectiveM(projectionMat,0,45f,aspect,.1f,100f)
    }

    private fun updateViewMatrix(angleX:Float,angleY:Float){
        val cross = matUtils.cross(myOrientation, mUp.toArray())
        val newOrientation = matUtils.rotateVec3(myOrientation,angleX,cross)
        val aa = matUtils.angle(newOrientation, mUp.toArray())
        if(abs(aa) <=85){
            myOrientation = newOrientation
        }

        myOrientation = matUtils.rotateVec3(myOrientation,angleY, mUp.toArray())

        val cent = matUtils.addFloatArrays(mUp.toArray(),myOrientation)
        Matrix.setLookAtM(viewMat,0,defaultPos.x,defaultPos.y,defaultPos.z,cent[0],cent[1],cent[2],mUp.x,mUp.y,mUp.z)
    }

    override fun scroll(mode: InputMode, xDist: Float, yDist: Float) {
        var xAngle = 0f
        var yAngle = 0f
        when(mode){
            InputMode.MOVE->{
                defaultPos.x -= 10f*xDist/myScene.width
                defaultPos.z -= 10f*yDist/myScene.height
            }
            InputMode.ROTATE->{
                xAngle = 30f*yDist/myScene.height
                yAngle = 30f*xDist/myScene.width
            }
            InputMode.UP_DOWN->{
                defaultPos.y += 10f*yDist/myScene.height
            }
        }
        updateViewMatrix(xAngle,yAngle)
    }

    override fun resetCamera(){
        defaultPos=Vec3f(0f,0f,-3f)
        mUp = Vec3f(0f,1f,0f)
        myOrientation=matUtils.makeANewCopy(defaultOrientation.toArray())
        //updateViewMatrix(0f,0f)
    }

    fun setDefaultView(pos: Vec3f, orientation: Vec3f) {
        defaultPos = pos
        defaultOrientation = orientation
        updateViewMatrix(0f,0f)
    }
}