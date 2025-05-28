package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.customObjs.GltfObj
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.impl.v2.Skin

class Animator(
    val gltfObj: GltfObj
) {
    private val model = gltfObj.model
    private var currentAnimation: Animation? = null
    private var animationTime:Float = 0f

    private var start = 0f
    private var stop = 0f
    private var progression = 0f

    private val skinModel = model.skinModels[0]

    val skin = Skin()

    fun doAnimation(animation: Animation){
        currentAnimation = animation
        start = Utils.getCurrentTime()
        stop = Utils.getCurrentTime() + animation.length
    }

    fun update(){
        if(currentAnimation==null){
            return
        }
        increaseAnimationTime()
        calculateCurrentAnimationPose()
        applyPoseToJoints()
    }

    private fun increaseAnimationTime(){
        val currentTime = Utils.getCurrentTime()
        animationTime = currentTime
        Log.d("TAG","A-time $currentTime")
        if(animationTime>stop){
            Log.d("TAG","RESET")
            start = currentTime
            stop = currentTime + currentAnimation!!.length
        }
        else{
            Log.d("TAG","NO RESET")
        }
    }

    private fun calculateCurrentAnimationPose(){
        val frames = getPreviousAndNextFrames()
        progression = calculateProgression(frames[0],frames[1])
        interpolateKeyframes(frames[0],frames[1],progression)
    }

    private fun applyPoseToJoints() {
        for (joint in skinModel.joints) {
            val index = skinModel.joints.indexOf(joint)
            val inverseTransform = skinModel.getInverseBindMatrix(index,FloatArray(16))
            val globalJointTransform = skinModel.joints[index].computeGlobalTransform(FloatArray(16))

            Matrix.multiplyMM(
                gltfObj.bones[joint]!!.animatedTransform, 0,
                globalJointTransform, 0,
                inverseTransform, 0,

            )
        }
    }

    private fun getPreviousAndNextFrames():Array<KeyFrame2>{
        val allFrames = currentAnimation!!.keyFrames
        var previousFrame = allFrames[0]
        var nextFrame = allFrames[0]
        for(frame in allFrames){
             nextFrame = frame
            if((nextFrame.time+start)>animationTime){
                break
            }
            previousFrame = frame
           }
        return arrayOf(previousFrame,nextFrame)
    }

    private fun calculateProgression(previousFrame: KeyFrame2,nextFrame: KeyFrame2):Float{
        val totalTime = nextFrame.time - previousFrame.time
        val currentTime = animationTime - (previousFrame.time+start)
        return currentTime/totalTime
    }

    private fun interpolateKeyframes(previous: KeyFrame2, nextFrame: KeyFrame2, alpha: Float) {

        for(node in previous.boneTransforms.keys){
            val prev = previous.boneTransforms[node]!!
            val next = nextFrame.boneTransforms[node]!!
            // Interpolate translation
            val trans = prev.translation.lerp(next.translation,alpha)
            // Interpolate rotation (SLERP)
            val rot = prev.rotation.slerp(next.rotation, alpha)
            // Interpolate scale
            val scale = prev.scale.lerp(next.scale, alpha)

            gltfObj.model.nodeModels.find { it == node }!!.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }

        }
    }

}