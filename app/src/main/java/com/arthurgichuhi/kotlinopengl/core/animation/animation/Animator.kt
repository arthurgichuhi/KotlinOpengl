package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.AnimatedObj
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Joint
import com.arthurgichuhi.kotlinopengl.utils.Utils

class Animator(
    val entity:AnimatedObj
) {
    companion object{
        val UTILS = Utils()
    }
    var currentAnimation: Animation? = null
    var animationTime:Float = 0f

    fun doAnimation(animation: Animation){
        animationTime = 0f
        currentAnimation = animation

    }

    fun update(){
        if(currentAnimation==null){
            return
        }
        increaseAnimationTime()
        val currentPose = calculateCurrentAnimationPose()
        applyPoseToJoints(currentPose,entity.rootJoint,FloatArray(16))

    }

    /**
     * Increases the current animation time which allows the animation to
     * progress. If the current animation has reached the end then the timer is
     * reset, causing the animation to loop.
     */

    fun increaseAnimationTime(){
        animationTime += UTILS.getCurrentTime()
        Log.d("TAG","TIME=${animationTime}-----${UTILS.getCurrentTime()}")
        if(animationTime>currentAnimation!!.length){
            Log.d("TAG","---------RESET--------${currentAnimation!!.length}")
            animationTime %= currentAnimation!!.length
        }
        else{
            Log.d("TAG","---------NO RESET--------")
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
     * This method first finds the preious and next keyframe, calculates how far
     * between the two the current animation is, and then calculated the pose
     * for the current animation time by interpolating between the transforms at
     * those keyframes.
     *
     * @return The current pose as a map of the desired local-space transforms
     *         for all the joints. The transforms are indexed by the name ID of
     *         the joint that they should be applied to.
     */
    private fun calculateCurrentAnimationPose():Map<String,FloatArray>{
        val frames = getPreviousAndNextFrames()
        val progression = calculateProgression(frames[0],frames[1])
        Log.d("TAG","Progression---$progression")
        return interPolatePoses(frames[0],frames[1],progression)
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
     * @param joint
     *            - the current joint which the pose should be applied to.
     * @param parentTransform
     *            - the desired model-space transform of the parent joint for
     *            the pose.
     */

    private fun applyPoseToJoints(currentPose:Map<String,FloatArray>,joint: Joint,parentTransform: FloatArray){
        val currentLocalTransform = currentPose["${joint.name}/transform"]!!
        val currentTransform=FloatArray(16)
        Matrix.multiplyMM(currentTransform,0,parentTransform,0,currentLocalTransform,0)
        for(child in joint.children){
            applyPoseToJoints(currentPose,child,currentTransform)
        }
        joint.setAnimationTransform(currentTransform)
    }


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

    private fun getPreviousAndNextFrames():Array<KeyFrame>{
        val allFrames = currentAnimation!!.keyFrames
        var previousFrame = allFrames[0]
        var nextFrame = allFrames[0]
        for(frame in allFrames){
            nextFrame = frame
            if(nextFrame.timeStamp>animationTime){
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

    private fun calculateProgression(previousFrame: KeyFrame,nextFrame: KeyFrame):Float{
        val totalTime = nextFrame.timeStamp - previousFrame.timeStamp
        val currentTime = animationTime - previousFrame.timeStamp
        Log.d("TAG","CP:${previousFrame.timeStamp}==${nextFrame.timeStamp}==$currentTime==$totalTime")
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

    private fun interPolatePoses(previousFrame: KeyFrame,nextFrame: KeyFrame,progression:Float):Map<String,FloatArray>{
        val currentPose:MutableMap<String,FloatArray> = HashMap()
        for(jointName in previousFrame.jointKeyTransform.keys){
            val previousTransform = previousFrame.jointKeyTransform[jointName]!!
            val nextTransform = nextFrame.jointKeyTransform[jointName]!!
            val currentTransform = JointTransform.interpolate(previousTransform,nextTransform,progression)
            currentPose[jointName] = currentTransform.getLocalTransform()
        }
        return currentPose
    }
}