package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.customObjs.GltfObj
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.impl.v2.Skin
import de.javagl.jgltf.model.NodeModel
import de.javagl.jgltf.model.SkinModel
import org.joml.Matrix4f

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
        //applyAnimationTransform(rootBone,rootBone.computeLocalTransform(FloatArray(16)))
        val currentPose = calculateCurrentAnimationPose()
        applyPoseToJoints(currentPose)
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

    private fun calculateCurrentAnimationPose():Map<NodeModel,Matrix4f>{
        val frames = getPreviousAndNextFrames()
        progression = calculateProgression(frames[0],frames[1])
        Log.d("TAG","Progression $progression")
        return interpolateKeyframes(frames[0],frames[1],progression)
    }

    private fun applyPoseToJoints(
        currentPose: Map<NodeModel, Matrix4f>
    ) {
        for (joint in skinModel.joints) {
            val index = skinModel.joints.indexOf(joint)
            gltfObj.bones[joint]!!.localTransform = currentPose[joint]!!.get(FloatArray(16))
        }
//
        updateJoints(skinModel.joints[0], Matrix4f().invert().get(FloatArray(16)))

        for (joint in skinModel.joints) {
            val index = skinModel.joints.indexOf(joint)
            val inverseTransform = skinModel.getInverseBindMatrix(index,FloatArray(16))
            val globalJointTransform = gltfObj.bones[joint]!!.worldTransform

            Matrix.multiplyMM(
                gltfObj.bones[joint]!!.animatedTransform, 0,
                globalJointTransform, 0,
                inverseTransform, 0,

            )
        }
    }

    private fun updateJoints(joint:NodeModel, parentGlobalTransform:FloatArray){
        val index = skinModel.joints.indexOf(joint)

        Matrix.multiplyMM(
            gltfObj.bones[joint]!!.worldTransform, 0,
            parentGlobalTransform, 0,
            gltfObj.bones[joint]!!.localTransform, 0
        )

        for(child in joint.children){
            updateJoints(child,gltfObj.bones[joint]!!.worldTransform)
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

    private fun interpolateKeyframes(previous: KeyFrame2, nextFrame: KeyFrame2, alpha: Float):Map<NodeModel,Matrix4f> {
        val result :MutableMap<NodeModel,Matrix4f> = HashMap()
        val transMat = Matrix4f()
        val rotMat = Matrix4f()
        val scaleMat = Matrix4f()

        for(node in previous.boneTransforms.keys){
            val prev = previous.boneTransforms[node]!!
            val next = nextFrame.boneTransforms[node]!!
            // Interpolate translation
            val trans = prev.translation.lerp(next.translation,alpha)
            transMat.translate(trans)
            // Interpolate rotation (SLERP)
            val rot = prev.rotation.slerp(next.rotation, alpha)
            rotMat.rotate(rot)
            // Interpolate scale
            val scale = prev.scale.lerp(next.scale, alpha)
            scaleMat.scale(scale)
            gltfObj.model.nodeModels.find { it == node }!!.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }

            result[node] = Matrix4f().translate(trans).rotate(rot).scale(scale)
        }
        return result
    }

    // Helper to find inverse bind matrix for a joint
    private fun findInverseBindFloatArray(node: NodeModel, skin: SkinModel): FloatArray {
        val index = skin.joints.indexOf(node)
        return if (index >= 0) skin.getInverseBindMatrix(index,FloatArray(16)) else Matrix4f().get(FloatArray(16))
    }

    private fun findInverseBindMatrix(node: NodeModel, skin: SkinModel): Matrix4f {
        val index = skin.joints.indexOf(node)
        return if (index >= 0) Matrix4f().set(skin.getInverseBindMatrix(index,FloatArray(16))) else Matrix4f()
    }

}