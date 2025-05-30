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
    private var start:Float = 0f
    private var speed:Float = .05f

    private val skinModel = model.skinModels[0]

    val skin = Skin()

    fun doAnimation(animation: Animation){
        currentAnimation = animation
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
        animationTime = currentTime - start

        if(animationTime>currentAnimation!!.length){
            start = currentTime - ((animationTime % currentAnimation!!.length))
            animationTime %= currentAnimation!!.length
        }
    }

    private fun calculateCurrentAnimationPose(){
        val frames = getPreviousAndNextFrames()
        interpolateKeyframes(frames[0],frames[1],calculateProgression(frames[0],frames[1]))
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
            if(nextFrame.time>animationTime){
                break
            }
            previousFrame = frame
        }
        return arrayOf(previousFrame,nextFrame)
    }

    private fun calculateProgression(previousFrame: KeyFrame2,nextFrame: KeyFrame2):Float{
        val totalTime = nextFrame.time - previousFrame.time
        val currentTime = animationTime - previousFrame.time
        return (currentTime/totalTime) * speed
    }

    private fun interpolateKeyframes(previous: KeyFrame2, nextFrame: KeyFrame2, alpha: Float) {

        for(node in previous.boneTransforms.keys){
            val index = gltfObj.model.nodeModels.indexOf(node)
            val prev = previous.boneTransforms[node]!!
            val next = nextFrame.boneTransforms[node]!!

            if(node==skinModel.joints[3])Log.d("TAG","$alpha Times ${previous.time} ${nextFrame.time}" +
                    "\n${prev.rotation.x} , ${prev.rotation.y} , ${prev.rotation.z} , ${prev.rotation.w}" +
                    "\n${next.rotation.x} , ${next.rotation.y} , ${next.rotation.z} , ${next.rotation.w}")
            // Interpolate translation
            val trans = prev.translation.lerp(next.translation,alpha)
            // Interpolate rotation (SLERP)
            val rot = prev.rotation.normalize().slerp(next.rotation.normalize(), alpha)
            // Interpolate scale
            val scale = prev.scale.lerp(next.scale, alpha)
            gltfObj.model.nodeModels[index]!!.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }

        }
    }

}