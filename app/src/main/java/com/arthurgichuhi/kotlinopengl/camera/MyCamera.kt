package com.arthurgichuhi.kotlinopengl.camera

import android.opengl.Matrix
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.IReceiveInput
import com.arthurgichuhi.kotlinopengl.core.InputMode
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene
import com.arthurgichuhi.kotlinopengl.io_Operations.TouchTracker
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import kotlin.math.abs

class MyCamera:IReceiveInput {
    val viewMat=FloatArray(16)
    val projectionMat=FloatArray(16)
    var myOrientation:FloatArray

    var defaultPos: Vec3f = Vec3f(0f,0f,-3f)
    var rotation: Vec3f = Vec3f(0f,0f,0f)
    var mUp = Vec3f(0f,1f,0f)

    var defaultOrientation= Vec3f(0f,0f,-1f)

    var width=0f
    var height=0f

    init {
        Matrix.setIdentityM(viewMat,0)
        Matrix.setIdentityM(projectionMat,0)
        Matrix.translateM(viewMat,0,0f,0f,-10f)
        myOrientation=MathUtils.makeANewCopy(defaultOrientation.toArray())
        //resetCamera()
    }



    lateinit var myScene: MyScene

    fun getScene(aScene: MyScene){
        myScene=aScene
    }

    fun update(){
        val aspect = width/height
        Matrix.perspectiveM(projectionMat,0,45f,aspect,.1f,100f)
    }

    private fun updateViewMatrix(angleX:Float,angleY:Float){
        val cross = MathUtils.cross(myOrientation, mUp.toArray())
        val newOrientation = MathUtils.rotateVec3(myOrientation,angleX,cross)
        val aa = MathUtils.angle(newOrientation, mUp.toArray())
        if(abs(aa) <=85){
            myOrientation = newOrientation
        }

        myOrientation = MathUtils.rotateVec3(myOrientation,angleY, mUp.toArray())

        val cent = MathUtils.addFloatArrays(mUp.toArray(),myOrientation)
        Matrix.setLookAtM(viewMat,0,defaultPos.x,defaultPos.y,defaultPos.z,cent[0],cent[1],cent[2],mUp.x,mUp.y,mUp.z)
    }

    override fun scroll(mode: InputMode, xDist: Float, yDist: Float) {
        var xAngle = 0f
        var yAngle = 0f
        when(mode){
            InputMode.MOVE->{
//                defaultPos.x -= 30f*xDist/myScene.width
//                defaultPos.z -= 30f*yDist/myScene.height
            }
            InputMode.ROTATE->{
//                xAngle = 50f*yDist/myScene.height
//                yAngle = 50f*xDist/myScene.width
            }
            InputMode.UP_DOWN->{
//                defaultPos.y += 10f*yDist/myScene.height
            }
        }
        updateViewMatrix(xAngle,yAngle)
    }

    override fun resetCamera(){
        defaultPos= Vec3f(0f,0f,-3f)
        mUp = Vec3f(0f,1f,0f)
        myOrientation=MathUtils.makeANewCopy(defaultOrientation.toArray())
        //updateViewMatrix(0f,0f)
    }

    override fun touchTracker(value: TouchTracker) {

    }

    fun setDefaultView(pos: Vec3f, orientation: Vec3f) {
        defaultPos = pos
        defaultOrientation = orientation
        updateViewMatrix(0f,0f)
    }
}