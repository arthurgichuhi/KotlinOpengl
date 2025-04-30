package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Bone
import com.arthurgichuhi.kotlinopengl.customObjs.GltfObj
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.model.NodeModel
import de.javagl.jgltf.model.SkinModel
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.nio.FloatBuffer

class Animator(
    val gltfObj: GltfObj
) {
    private val model = gltfObj.model
    private var currentAnimation: Animation? = null
    private var animationTime:Float = 0f

    private var start = 0f
    private var stop = 0f

    private val skinModel = model.skinModels[0]

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
        val currentPose = calculateCurrentAnimationPose()
        applyPoseToJoints(currentPose,gltfObj.root)
    }

    /**
     * Increases the current animation time which allows the animation to
     * progress. If the current animation has reached the end then the timer is
     * reset, causing the animation to loop.
     */

    private fun increaseAnimationTime(){
        val currentTime = Utils.getCurrentTime()
        animationTime = currentTime
        if(animationTime>stop){
            start = currentTime
            stop = currentTime + currentAnimation!!.length
        }
    }

    /**
     * This method returns the current animation pose of the entity. It returns
     * the desired local-space transforms for all the joints in a map, indexed
     * by the name of the joint that they correspond to.
     *
     * The pose is calculated based on the previous and next keyframes in the
     * current animation. Each keyframe provides the desired pose at a certain
     * time in the animation, so the animated pose for the current time can be
     * calculated by interpolating between the previous and next keyframe.
     *
     * This method first finds the previous and next keyframe, calculates how far
     * between the two the current animation is, and then calculated the pose
     * for the current animation time by interpolating between the transforms at
     * those keyframes.
     *
     * @return The current pose as a map of the desired local-space transforms
     *         for all the joints. The transforms are indexed by the name ID of
     *         the joint that they should be applied to.
     */
    private fun calculateCurrentAnimationPose():Map<NodeModel,Matrix4f>{
        val frames = getPreviousAndNextFrames()
        val progression = calculateProgression(frames[0],frames[1])
        return interpolateKeyframes(frames[0],frames[1],progression)
    }

    /**
     * This is the method where the animator calculates and sets those all-
     * important "joint transforms" that I talked about so much in the tutorial(Youtube->Thin Matrix).
     *
     * This method applies the current pose to a given joint, and all of its
     * descendants. It does this by getting the desired local-transform for the
     * current joint, before applying it to the joint. Before applying the
     * transformations it needs to be converted from local-space to model-space
     * (so that they are relative to the model's origin, rather than relative to
     * the parent joint). This can be done by multiplying the local-transform of
     * the joint with the model-space transform of the parent joint.
     *
     * The same thing is then done to all the child joints.
     *
     * Finally the inverse of the joint's bind transform is multiplied with the
     * model-space transform of the joint. This basically "subtracts" the
     * joint's original bind (no animation applied) transform from the desired
     * pose transform. The result of this is then the transform required to move
     * the joint from its original model-space transform to it's desired
     * model-space posed transform. This is the transform that needs to be
     * loaded up to the vertex shader and used to transform the vertices into
     * the current pose.
     *
     * @param currentPose
     *            - a map of the local-space transforms for all the joints for
     *            the desired pose. The map is indexed by the name of the joint
     *            which the transform corresponds to.
     * @param bone
     *            - the current joint which the pose should be applied to.
     * @param parentTransform
     *            - the desired model-space transform of the parent joint for
     *            the pose.
     */

    private fun applyPoseToJoints(currentPose:Map<NodeModel,Matrix4f>,bone: Bone){
        val boneIndex = model.skinModels[0].joints.indexOf(bone.node)
        val joint = model.skinModels[0].joints[boneIndex]

        val parentTransform = joint.parent.computeLocalTransform(FloatArray(16))
        val currentLocalTransform = currentPose[bone.node]!!.get(FloatArray(16))
        val inverseTransform = findInverseBindMatrix(bone.node,skinModel).get(FloatArray(16))
        val currentTransform = FloatArray(16)
        val animTransform = FloatArray(16)

        Matrix.multiplyMM(currentTransform,0,parentTransform,0,currentLocalTransform,0)
        Matrix.multiplyMM(animTransform,0,currentTransform,0,inverseTransform,0)

        bone.setAnimationTransform(animTransform)

        for(child in bone.children){

            Matrix.setIdentityM(parentTransform,0)
            Matrix.setIdentityM(currentLocalTransform,0)
            Matrix.setIdentityM(inverseTransform,0)
            Matrix.setIdentityM(currentTransform,0)
            Matrix.setIdentityM(animTransform,0)

            child.node.parent.computeLocalTransform(parentTransform)
            //currentPose[child.node]!!.get(currentLocalTransform)
            findInverseBindMatrix(child.node,skinModel).get(inverseTransform)

            Matrix.multiplyMM(currentTransform,0,parentTransform,0,currentLocalTransform,0)
            Matrix.multiplyMM(animTransform,0,currentTransform,0,inverseTransform,0)

            child.setAnimationTransform(animTransform)
        }
        bone.setAnimationTransform(currentTransform)
    }

    /*
    val translation = currentPose[bone.node]!!.getTranslation(Vector3f())
        val scale = currentPose[bone.node]!!.getScale(Vector3f())
        val rot = currentPose[bone.node]!!.getUnnormalizedRotation(Quaternionf())
        joint.translation = floatArrayOf(translation.x,translation.y,translation.z)
        joint.rotation = floatArrayOf(rot.x,rot.y,rot.z,rot.w)
        joint.scale = floatArrayOf(scale.x,scale.y,scale.z)
     */

    /**
     * Finds the previous keyframe in the animation and the next keyframe in the
     * animation, and returns them in an array of length 2. If there is no
     * previous frame (perhaps current animation time is 0.5 and the first
     * keyframe is at time 1.5) then the first keyframe is used as both the
     * previous and next keyframe. The last keyframe is used for both next and
     * previous if there is no next keyframe.
     *
     * @return The previous and next keyframes, in an array which therefore will
     *         always have a length of 2.
     */

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

    /**
     * Calculates how far between the previous and next keyframe the current
     * animation time is, and returns it as a value between 0 and 1.
     *
     * @param previousFrame
     *            - the previous keyframe in the animation.
     * @param nextFrame
     *            - the next keyframe in the animation.
     * @return A number between 0 and 1 indicating how far between the two
     *         keyframes the current animation time is.
     */

    private fun calculateProgression(previousFrame: KeyFrame2,nextFrame: KeyFrame2):Float{
        val totalTime = nextFrame.time - previousFrame.time
        val currentTime = animationTime - (previousFrame.time+start)
        return currentTime/totalTime
    }

    /**
     * Calculates all the local-space joint transforms for the desired current
     * pose by interpolating between the transforms at the previous and next
     * keyframes.
     *
     * @param previousFrame
     *            - the previous keyframe in the animation.
     * @param nextFrame
     *            - the next keyframe in the animation.
     * @param progression
     *            - a number between 0 and 1 indicating how far between the
     *            previous and next keyframes the current animation time is.
     * @return The local-space transforms for all the joints for the desired
     *         current pose. They are returned in a map, indexed by the name of
     *         the joint to which they should be applied.
     */

    private fun interpolatePoses(previousFrame: KeyFrame, nextFrame: KeyFrame, progression:Float):Map<String,FloatArray>{
        val currentPose:MutableMap<String,FloatArray> = HashMap()
        for(jointName in previousFrame.jointKeyTransform.keys){
            val previousTransform = previousFrame.jointKeyTransform[jointName]!!
            val nextTransform = nextFrame.jointKeyTransform[jointName]!!
            val currentTransform = JointTransform.interpolate(previousTransform,nextTransform,progression)
            currentPose[jointName] = currentTransform.getLocalTransform()
        }
        return currentPose
    }

    private fun interpolateKeyframes(a: KeyFrame2, b: KeyFrame2, alpha: Float):Map<NodeModel,Matrix4f> {
        val result :MutableMap<NodeModel,Matrix4f> = HashMap()

        for(node in a.boneTransforms.keys){
            val prev = a.boneTransforms[node]!!
            val next = b.boneTransforms[node]!!

            // Interpolate translation
            val trans = prev.translation.lerp(next.translation,alpha)

            // Interpolate rotation (SLERP)
            val rot = prev.rotation.slerp(next.rotation, alpha)

            // Interpolate scale
            val scale = prev.scale.lerp(next.scale, alpha)

            result[node] = Matrix4f().translation(trans).rotation(rot).scale(scale)
        }
        return result
    }

    // Helper to find inverse bind matrix for a joint
    private fun findInverseBindMatrix(node: NodeModel, skin: SkinModel): Matrix4f {
        val index = skin.joints.indexOf(node)
        return if (index >= 0) Matrix4f(FloatBuffer.wrap(skin.getInverseBindMatrix(index,FloatArray(16)))) else Matrix4f()
    }
}