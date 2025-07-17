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
    var currentAnimation: Animation? = null
    private var nextAnimation:Animation? = null
    private var animationTime:Float = 0f
    private var start:Float = 0f

    private val skinModel = model.skinModels[0]

    val skin = Skin()

    fun doAnimation(animation: Animation){
        if(currentAnimation==null){
            currentAnimation = animation
        } else {
            if (currentAnimation!!.name != animation.name && nextAnimation==null) {
                nextAnimation = animation
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
        val currentTime = Utils.getCurrentTime()
        animationTime = currentTime - start
        if (animationTime >= currentAnimation!!.length) {
            start = currentTime - (animationTime % currentAnimation!!.length)
            animationTime %= currentAnimation!!.length
        }
    }

    private fun calculateCurrentAnimationPose(){
        val frames = getPreviousAndNextFrames()
        val progression = calculateProgression(frames[0],frames[1])
        interpolateKeyframes(frames[0],frames[1],progression)
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
        Log.d("TAG","GPN ${currentAnimation?.name}")
        var previousFrame = allFrames[0]
        var nextFrame = allFrames[0]
        for(frame in allFrames){
             nextFrame = frame
            if(nextFrame.time>animationTime){
                break
            }
            previousFrame = frame
        }
        if (nextAnimation != null && currentAnimation!!.name != "transition") {
            val pf = nextFrame
            val nf = nextAnimation!!.keyFrames.first()
            pf.time = .02f
            nf.time = .25f
            currentAnimation = Animation(
                name = "transition",
                length = .26f,
                keyFrames = listOf(pf, nf)
            )
        }
        return arrayOf(previousFrame,nextFrame)
    }

    private fun calculateProgression(previousFrame: KeyFrame2,nextFrame: KeyFrame2):Float{

        val totalTime = nextFrame.time - previousFrame.time
        val currentTime = animationTime - previousFrame.time
        val result = if (totalTime <= 0f || currentTime <= 0f) {
            .5f
        } else {
            (currentTime / totalTime)
        }

        return  result
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
            gltfObj.nodeModels[index]?.also {
                it.translation = floatArrayOf(trans.x,trans.y,trans.z)
                it.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
                it.scale = floatArrayOf(scale.x,scale.y,scale.z)
            }
        }
        if (currentAnimation!!.name == "transition" && alpha > .7f) {
            currentAnimation = nextAnimation
            nextAnimation = null
        }
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

        fun getFloatData(accessor: AccessorModel): AccessorFloatData {
            val data = accessor.accessorData
            require(data is AccessorFloatData) { "Expected float data in accessor!" }
            return data
        }

        private fun MutableList<KeyFrame2>.findOrCreate(time: Float,node: NodeModel): KeyFrame2 {
            return find { it.time == time } ?: KeyFrame2(time, boneTransforms = hashMapOf(node to BoneTransform())).also { add(it) }
        }
    }

}