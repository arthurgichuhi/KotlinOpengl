package com.arthurgichuhi.kotlinopengl.core.animation.animation

import de.javagl.jgltf.model.NodeModel

/**
 * Represents an animation that can applied to an AnimatedModel . It
 * contains the length of the animation in seconds, and a list of KeyFrames
 * @param lengthInSeconds the total length of the animation in seconds.
 * @param frames all the keyframes for the animation, ordered by time of
 *            appearance in the animation.
*/

data class Animation(var length:Float,val keyFrames:List<KeyFrame2>)