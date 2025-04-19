package com.arthurgichuhi.kotlinopengl.core.animation.loaders

import android.content.Context
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animation
import com.arthurgichuhi.kotlinopengl.core.animation.animation.JointTransform
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Quartenion
import com.arthurgichuhi.kotlinopengl.core.collada.ColladaLoader
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.JointTransformData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.KeyFrameData

class AnimationObjLoader{
    /**
     * Loads up a collada animation file, and returns and animation created from
     * the extracted animation data from the file.
     *
     * @param ctx the Android Content Context
     * @param fileName  the name of the collada file
     * @return The animation made from the data in the file.
     */
    companion object{
        fun loadAnimation(ctx:Context,fileName:String):Animation{
            val animationData = ColladaLoader.loadColladaAnimation(ctx,fileName)
            val frames : MutableList<KeyFrame> = ArrayList()
            for(i in 0..<animationData.keyFrames.size){
                frames.add(createFrameKey(animationData.keyFrames[i]))
            }
            return Animation(animationData.lengthSeconds,frames,animationData.invTransforms)
        }

        private fun createFrameKey(frame: KeyFrameData): KeyFrame {
            val map : MutableMap<String,JointTransform> = HashMap()
            for(jointData in frame.jointTransforms){
                val jointTransform = createTransform(jointData)
                map[jointData.jointName] = jointTransform
            }
            return  KeyFrame(frame.time,map)
        }

        private fun createTransform(data: JointTransformData): JointTransform {
            val mat = data.jointLocalTransform
            val translation = Vec3f(mat[12],mat[13],mat[14])
            val rotation = Quartenion.fromMatrix(mat)
            return  JointTransform(translation,rotation)
        }
    }
}