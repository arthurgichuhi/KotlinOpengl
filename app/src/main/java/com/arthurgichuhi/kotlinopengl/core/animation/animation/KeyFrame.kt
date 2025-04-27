package com.arthurgichuhi.kotlinopengl.core.animation.animation

import com.arthurgichuhi.kotlinopengl.models.Vec3f
import org.joml.Quaternionf
import org.joml.Vector3f

/**
 *
 * Represents one keyframe of an animation. This contains the timestamp of the
 * keyframe, which is the time (in seconds) from the start of the animation when
 * this keyframe occurs.
 *
 * It also contains the desired bone-space transforms of all of the joints in
 * the animated entity at this keyframe in the animation (i.e. it contains all
 * the joint transforms for the "pose" at this time of the animation.). The
 * joint transforms are stored in a map, indexed by the name of the joint that
 * they should be applied to.
 * @param timeStamp
 *            - the time (in seconds) that this keyframe occurs during the
 *            animation.
 * @param jointKeyFrames
 *            - the local-space transforms for all the joints at this
 *            keyframe, indexed by the name of the joint that they should be
 *            applied to.
 */
data class KeyFrame(
    var timeStamp:Float = 0f,
    var jointKeyTransform:Map<String,JointTransform> = HashMap(),
    )
