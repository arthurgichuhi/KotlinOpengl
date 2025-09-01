package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Bone
import com.arthurgichuhi.kotlinopengl.models.InterpolationData
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.impl.v2.Skin
import de.javagl.jgltf.model.AccessorFloatData
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.AnimationModel
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.abs
import kotlin.math.pow

class Animator(
    val gltfObj: GltfModel,
    val bones:MutableMap<NodeModel,Bone>,
    var actor:Boolean = true
) {
    private val model = gltfObj
    var defaultAnimation: Animation? = null
    var currentAnimation: Animation? = null
    var animations: Map<String,Animation> = HashMap()
    private var nextAnimation:Animation? = null
    private var animationTime:Long = 0
    private var transitionTime:Float = .3f

    private val startNanos:Long = 0


    private val skinModel = model.skinModels[0]

    val skin = Skin()

    fun doAnimation(animation: Animation){
        if(currentAnimation==null){
            currentAnimation = animation
        } else {
            if (currentAnimation!!.name != animation.name && nextAnimation==null) {
                nextAnimation = animation
                nextAnimation!!.start = System.nanoTime()
            }
        }
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
        val currentTime = System.nanoTime()
        val stopTime = currentAnimation!!.start + currentAnimation!!.length

        animationTime = currentTime - currentAnimation!!.start

        Log.d("TAG","Current $currentTime  $animationTime")

        currentAnimation!!.current = currentTime
        if(nextAnimation!=null) nextAnimation!!.current = currentTime

        if (nextAnimation != null) {
            if (currentTime - nextAnimation!!.start >= (transitionTime * 1_000_000_000L)) {
                currentAnimation = nextAnimation
                animationTime = (currentTime - nextAnimation!!.start)
                nextAnimation = null
                return
            }
        }

        if(currentAnimation!!.name == "Hit to Body" || currentAnimation!!.name == "Head hit"){
            if(nextAnimation==null){

                if(stopTime - currentTime <= (transitionTime * 1_000_000_000L)){
                    doAnimation(defaultAnimation!!)
                }
            }
        }

        if (animationTime >= currentAnimation!!.length) {
            animationTime %= currentAnimation!!.length
            currentAnimation!!.start = currentTime - animationTime
        }
    }

    private fun calculateCurrentAnimationPose(){
        if(nextAnimation==null){
            val frames = getPreviousAndNextFrames(currentAnimation!!)
            val progression = calculateProgression(
                frames[0],frames[1],currentAnimation!!)
            interpolateKeyframes(frames[0],frames[1],progression)
        }
        else{
            val frames = getPreviousAndNextFrames(currentAnimation!!)
            val progression = calculateProgression(
                frames[0],frames[1],currentAnimation!!)

            if(!actor)Log.d("TAG","CP-IT 1\n$progression\n${frames[0].time} - ${frames[1].time}")

            val frames2 = getPreviousAndNextFrames(nextAnimation!!)
            val progression2 = calculateProgression(
                frames2[0],frames2[1], nextAnimation!!)

            if(!actor)Log.d("TAG","CP-IT 2\n$progression2\n${frames2[0].time} - ${frames2[1].time}")

            interpolateTransition(
                InterpolationData(
                    frames[0],frames[1],progression),
                InterpolationData(
                    frames2[0],frames2[1],progression2)
            )
        }
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
           if(index==0 && !actor){
               Log.d("TAG","Transform" +
                       "\n${bones[joint]!!.animatedTransform.toList()}" +
                       "\n${inverseTransform.toList()}" +
                       "\n${globalJointTransform.toList()}")
           }
        }
    }

    private fun getPreviousAndNextFrames(animation: Animation):Array<KeyFrame>{
        val allFrames = animation.keyFrames
        var previousFrame = allFrames[0].copy()
        var nextFrame = allFrames[0].copy()

        for(frame in allFrames){
             nextFrame = frame.copy(time = frame.time)
            if(nextFrame.time > (animation.current - animation.start))break
            previousFrame = frame.copy(time = frame.time)
        }

        if (previousFrame.time == nextFrame.time && previousFrame.time == currentAnimation!!.keyFrames.first().time){
            previousFrame.time = 15_000_000L
            Log.d("TAG","Similar times 2 ${nextFrame.time}   ${previousFrame.time}")
        }

        return arrayOf(previousFrame,nextFrame)
    }

    private fun calculateProgression(
        previousFrame: KeyFrame, nextFrame: KeyFrame, animation: Animation):Float{
        val elapsed =(animation.current - animation.start)
        val totalTime = nextFrame.time - previousFrame.time
        val currentTime = abs(elapsed - previousFrame.time)
        if(!actor){
            Log.d("TAG","Incorrect $totalTime" +
                    "\n${nextFrame.time} ${previousFrame.time} - $elapsed" +
                    "\n${((currentTime/1_000_000_000f).toDouble()/(totalTime/1_000_000_000f).toDouble()).toFloat()}")
        }
        return ((currentTime/1_000_000_000f).toDouble()/(totalTime/1_000_000_000f).toDouble()).toFloat()
    }

    private fun interpolateKeyframes(previous: KeyFrame, nextFrame: KeyFrame, alpha: Float) {
        if(actor)Log.d("TAG","Interpolation $alpha ${previous.time} ${nextFrame.time}")
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
            gltfObj.nodeModels[index]?.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }

        }
    }

    private fun interpolateTransition(currentData: InterpolationData,nextData: InterpolationData) {
        val elapsed = nextAnimation!!.current - nextAnimation!!.start
        val blendFactor:Float = (elapsed / (transitionTime * 1_000_000_000f))

        Log.d("TAG","Blend $blendFactor")

        for(node in currentData.previousKeyFrame.boneTransforms.keys){
            val index = gltfObj.nodeModels.indexOf(node)
            val prev1 = currentData.previousKeyFrame.boneTransforms[node]!!
            val next1 = currentData.nextKeyFrame.boneTransforms[node]!!

            val prev2 = nextData.previousKeyFrame.boneTransforms[node]!!
            val next2 = nextData.nextKeyFrame.boneTransforms[node]!!
            Log.d("TAG","IT" +
                    "\nPREV" +
                    "\n${prev1.translation.x},${prev1.translation.y},${prev1.translation.z}" +
                    "\n${prev2.translation.x},${prev2.translation.y},${prev2.translation.z}" +
                    "\nNEXT" +
                    "\n${next1.translation.x},${next1.translation.y},${next1.translation.z}" +
                    "\n${next2.translation.x},${next2.translation.y},${next2.translation.z}")
            val trans = Vector3f()
            val trans1 = prev1.translation.lerp(next1.translation,currentData.alpha)
            val trans2 = prev2.translation.lerp(next2.translation,nextData.alpha)
            trans1.mul(1f - blendFactor).add(trans2.mul(blendFactor)).set(trans)

            val rot = Quaternionf()
            val rot1 = prev1.rotation.normalize().slerp(next1.rotation.normalize(), currentData.alpha)
            val rot2 = prev2.rotation.normalize().slerp(next2.rotation.normalize(), nextData.alpha)
            rot1.slerp(rot2,blendFactor).normalize().set(rot)

            val scale = Vector3f()
            val scale1 = prev1.scale.lerp(next1.scale, currentData.alpha)
            val scale2 = prev2.scale.lerp(next2.scale, nextData.alpha)
            scale1.mul(1f - blendFactor).add(scale2.mul(blendFactor)).set(scale)

            gltfObj.nodeModels[index]?.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }
        }
    }

    companion object{
        fun processAnimation(animation: AnimationModel):Animation{

            val nodeKeyFrames : MutableList<KeyFrame> = ArrayList()
            for(channel in animation.channels){
                val node = channel.nodeModel
                val path = channel.path // "translation", "rotation", or "scale"
                val sampler = channel.sampler

                val times = getFloatData(sampler.input)
                val values = getFloatData(sampler.output)

                for(i in 0 ..<sampler.input.count){
                    val time = (times.get(i) * 1_000_000_000).toLong()
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

        fun getFloatData(accessor: AccessorModel): AccessorFloatData {
            val data = accessor.accessorData
            require(data is AccessorFloatData) { "Expected float data in accessor!" }
            return data
        }

        private fun MutableList<KeyFrame>.findOrCreate(time: Long, node: NodeModel): KeyFrame {
            return find { it.time == time } ?: KeyFrame(time, boneTransforms = hashMapOf(node to BoneTransform())).also { add(it) }
        }
    }

}