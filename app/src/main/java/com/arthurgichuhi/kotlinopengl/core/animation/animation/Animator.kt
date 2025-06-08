package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Bone
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.impl.v2.Skin
import de.javagl.jgltf.model.AccessorFloatData
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.AnimationModel
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Quaternionf
import org.joml.Vector3f

class Animator(
    val gltfObj: GltfModel,
    val bones:MutableMap<NodeModel,Bone>
) {
    private val model = gltfObj
    private var currentAnimation: Animation? = null
    private var nextAnimation:Animation? = null
    private var animationTime:Float = 0f
    private var start:Float = 0f
    private var delta:Float = 0f
    private var transitionTime = 0f
    private var transitionDuration = 0.3f // Adjust as needed
    private var isTransitioning = false

    private var loop :Boolean = true
    private var triggerLoop = true
    private var speed:Float = .001f

    private val skinModel = model.skinModels[0]

    val skin = Skin()

    fun doAnimation(animation: Animation){
        if(currentAnimation==null){
            currentAnimation = animation
        }
        else{
            nextAnimation = animation
        }
    }

    fun triggerLoop(value:Boolean){
        triggerLoop = value
    }

    fun update(){
        if(currentAnimation==null){
            return
        }
        increaseAnimationTime()
        calculateCurrentAnimationPose()
        applyPoseToJoints()
    }

    fun updateDelta(value:Float){
        delta = value
    }

    private fun increaseAnimationTime(){
        val currentTime = Utils.getCurrentTime()
        animationTime = currentTime - start
        if(animationTime>currentAnimation!!.length){
            if(nextAnimation!=null){
                animationTime = currentTime
                start = currentTime - (animationTime % nextAnimation!!.length)
                animationTime %= nextAnimation!!.length
            }
            else{
                start = currentTime - (animationTime % currentAnimation!!.length)
                animationTime %= currentAnimation!!.length
            }

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
                bones[joint]!!.animatedTransform, 0,
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
        if(nextAnimation!=null){
            previousFrame = nextFrame
            nextFrame = nextAnimation!!.keyFrames.first()
        }
        return arrayOf(previousFrame,nextFrame)
    }

    private fun calculateProgression(previousFrame: KeyFrame2,nextFrame: KeyFrame2):Float{
        val totalTime = nextFrame.time - previousFrame.time
        val currentTime = animationTime - previousFrame.time
        return if(nextAnimation!=null)(currentTime/totalTime) * speed else .5f
    }

    private fun interpolateKeyframes(previous: KeyFrame2, nextFrame: KeyFrame2, alpha: Float) {

        for(node in previous.boneTransforms.keys){
            val index = gltfObj.nodeModels.indexOf(node)
            val prev = previous.boneTransforms[node]!!
            val next = nextFrame.boneTransforms[node]!!

            // Interpolate translation
            val trans = prev.translation.lerp(next.translation,alpha)
            // Interpolate rotation (SLERP)
            val rot = prev.rotation.normalize().slerp(next.rotation.normalize(), alpha)
            // Interpolate scale
            val scale = prev.scale.lerp(next.scale, alpha)
            gltfObj.nodeModels[index]!!.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }

            if(nextAnimation!=null){
                currentAnimation = nextAnimation
                nextAnimation = null
            }
        }
    }

    private fun interpolateBetweenAnimations(
        prevFrameA: KeyFrame2, nextFrameA: KeyFrame2, progressionA: Float,
        prevFrameB: KeyFrame2, nextFrameB: KeyFrame2, blendFactor: Float
    ) {
        // First interpolate within each animation
        val poseA = HashMap<NodeModel, BoneTransform>()
        val poseB = HashMap<NodeModel, BoneTransform>()

        // Calculate pose for current animation
        for (node in prevFrameA.boneTransforms.keys) {
            val prev = prevFrameA.boneTransforms[node]!!
            val next = nextFrameA.boneTransforms[node]!!
            poseA[node] = BoneTransform().apply {
                translation = prev.translation.lerp(next.translation, progressionA)
                rotation = prev.rotation.slerp(next.rotation, progressionA)
                scale = prev.scale.lerp(next.scale, progressionA)
            }
        }

        // Calculate pose for next animation
        val progressionB = if (nextFrameB.time == prevFrameB.time) 0f else
            (animationTime - prevFrameB.time) / (nextFrameB.time - prevFrameB.time)

        for (node in prevFrameB.boneTransforms.keys) {
            val prev = prevFrameB.boneTransforms[node]!!
            val next = nextFrameB.boneTransforms[node]!!
            poseB[node] = BoneTransform().apply {
                translation = prev.translation.lerp(next.translation, progressionB)
                rotation = prev.rotation.slerp(next.rotation, progressionB)
                scale = prev.scale.lerp(next.scale, progressionB)
            }
        }

        // Blend between the two poses
        for ((node, transformA) in poseA) {
            val transformB = poseB[node] ?: continue
            val index = gltfObj.nodeModels.indexOf(node)
            gltfObj.nodeModels[index]?.also {
                it.translation = floatArrayOf(
                    transformA.translation.x * (1 - blendFactor) + transformB.translation.x * blendFactor,
                    transformA.translation.y * (1 - blendFactor) + transformB.translation.y * blendFactor,
                    transformA.translation.z * (1 - blendFactor) + transformB.translation.z * blendFactor
                )

                val blendedRot = transformA.rotation.slerp(transformB.rotation, blendFactor)
                it.rotation = floatArrayOf(
                    blendedRot.x, blendedRot.y, blendedRot.z, blendedRot.w
                )

                it.scale = floatArrayOf(
                    transformA.scale.x * (1 - blendFactor) + transformB.scale.x * blendFactor,
                    transformA.scale.y * (1 - blendFactor) + transformB.scale.y * blendFactor,
                    transformA.scale.z * (1 - blendFactor) + transformB.scale.z * blendFactor
                )
            }
        }
    }

    private fun getPreviousAndNextFramesForAnimation(animation: Animation): Array<KeyFrame2> {
        // Similar to getPreviousAndNextFrames but for a specific animation
        val allFrames = animation.keyFrames
        var previousFrame = allFrames[0]
        var nextFrame = allFrames[0]
        for (frame in allFrames) {
            nextFrame = frame
            if (nextFrame.time > animationTime) break
            previousFrame = frame
        }
        return arrayOf(previousFrame, nextFrame)
    }

    companion object{
        fun processAnimation(animation: AnimationModel):Animation{

            val nodeKeyFrames : MutableList<KeyFrame2> = ArrayList()
            for(channel in animation.channels){
                val node = channel.nodeModel
                val path = channel.path // "translation", "rotation", or "scale"
                val sampler = channel.sampler

                val times = getFloatData(sampler.input)
                val values = getFloatData(sampler.output)

                for(i in 0 ..<sampler.input.count){
                    val time = times.get(i)
                    val keyFrame = nodeKeyFrames.findOrCreate(time,node)
                    if(!keyFrame.boneTransforms.containsKey(node)){
                        keyFrame.boneTransforms [node] = BoneTransform()
                    }

                    when(path){
                        "translation" -> {
                            val translation = Vector3f(
                                values.get(i * 3),
                                values.get(i * 3 + 1),
                                values.get(i * 3 + 2)
                            )
                            keyFrame.boneTransforms[node]!!.translation = translation

                        }
                        "rotation" -> {
                            val rotation = Quaternionf(
                                values.get(i * 4),
                                values.get(i * 4 + 1),
                                values.get(i * 4 + 2),
                                values.get(i * 4 + 3)
                            )
                            keyFrame.boneTransforms[node]!!.rotation = rotation

                        }
                        "scale" -> {
                            val scale = Vector3f(
                                values.get(i * 3),
                                values.get(i * 3 + 1),
                                values.get(i * 3 + 2)
                            )
                            keyFrame.boneTransforms[node]!!.scale = scale

                        }
                    }
                }
            }

            return Animation(animation.name,nodeKeyFrames.last().time,nodeKeyFrames)
        }

        private fun getFloatData(accessor: AccessorModel): AccessorFloatData {
            val data = accessor.accessorData
            require(data is AccessorFloatData) { "Expected float data in accessor!" }
            return data
        }

        private fun MutableList<KeyFrame2>.findOrCreate(time: Float,node: NodeModel): KeyFrame2 {
            return find { it.time == time } ?: KeyFrame2(time, boneTransforms = hashMapOf(node to BoneTransform())).also { add(it) }
        }
    }

}