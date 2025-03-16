package com.arthurgichuhi.kotlinopengl.core.animation.loaders

import android.content.Context
import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animation
import com.arthurgichuhi.kotlinopengl.core.animation.animation.JointTransform
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Quartenion
import com.arthurgichuhi.kotlinopengl.core.collada.ColladaLoader
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.JointTransformData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.KeyFrameData
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class AnimationObjLoader{
    private val MathUtils = MathUtils()
    private var CORRECTION = FloatArray(16)

    init {
        MathUtils.setIdentity4Matrix(CORRECTION)
        Matrix.rotateM(CORRECTION,0,-90f,1f,0f,0f)
    }
    /**
     * Loads up a collada animation file, and returns and animation created from
     * the extracted animation data from the file.
     *
     * @param colladaFile
     *            - the collada file containing data about the desired
     *            animation.
     * @return The animation made from the data in the file.
     */
    companion object{
        fun loadAnimation(ctx:Context,fileName:String):Animation{
            val animationData = ColladaLoader.loadColladaAnimation(ctx,fileName)
            val frames : MutableList<KeyFrame> = ArrayList()
            for(i in 0..<animationData.keyFrames.size){
                Log.d("TAG","LA:$i")
                frames.add(createFrameKey(animationData.keyFrames[i]))
            }
            return Animation(animationData.lengthSeconds,frames)
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
            val translation = Vec3f(mat[3],mat[7],mat[11])
            val rotation = Quartenion.fromMatrix(mat)
            return  JointTransform(translation,rotation)
        }
    }
}