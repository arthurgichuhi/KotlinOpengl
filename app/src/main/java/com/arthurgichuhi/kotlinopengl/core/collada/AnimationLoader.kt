package com.arthurgichuhi.kotlinopengl.core.collada

import android.opengl.Matrix
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.AnimationData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.JointTransformData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.KeyFrameData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class AnimationLoader(val animNode: XmlNode, val jointsNode:XmlNode) {

    companion object{
        private val MathUtils = MathUtils()
        private var CORRECTION = FloatArray(16)
    }
    init {
        MathUtils.setIdentity4Matrix(CORRECTION)
        Matrix.rotateM(CORRECTION,0,-90f,1f,0f,0f)
    }

    fun extractAnimationData():AnimationData{
        val rootNode = findRootJointName()
        val times = getKeyTimes()
        val duration = times.last()
        val keyFrames = initKeyFrames(times)
        val animationNodes = animNode.getChildren("animation")
        for(joint in animationNodes){
            loadJointTransforms(keyFrames,joint,rootNode)
        }
        return AnimationData(duration,keyFrames)
    }

    private fun loadJointTransforms(frames: Array<KeyFrameData>, jointData: XmlNode, rootNodeId: String) {
        val jointNameId = getJointName(jointData)
        val dataId = getDataId(jointData)
        val transformData = jointData.getChildWithAttribute("source","id",dataId)
        val rawData = transformData?.getChild("float_array")?.data?.split(" ")
        processTransforms(jointNameId,rawData!!,frames, jointNameId == rootNodeId)
    }

    private fun processTransforms(jointName: String, rawData: List<String>, frames: Array<KeyFrameData>, root: Boolean) {
        val matrixData = FloatArray(16)
        for(i in frames.indices){
            for(j in 0..<16){
                matrixData[j] = rawData[i * 16 + j].toFloat()
            }
            Matrix.transposeM(matrixData,0,matrixData,0)
            if(root){
                Matrix.multiplyMM(matrixData,0, CORRECTION,0,matrixData,0)
            }
            frames[i].jointTransforms.add(JointTransformData(jointName,matrixData))
        }
    }

    private fun getDataId(data: XmlNode): String {
        val node = data.getChild("sampler")
            ?.getChildWithAttribute("input","semantic","OUTPUT")
        return node?.getAttribute("source")?.substring(1)!!
    }

    private fun getJointName(jointData: XmlNode): String {
        val channelNode = jointData.getChild("channel")
        val data = channelNode?.getAttribute("target")
        return data?.trim()?.split(" ")?.get(0)!!
    }

    private fun initKeyFrames(times: FloatArray): Array<KeyFrameData> {
        val ret : MutableList<KeyFrameData> = ArrayList(times.size)
        for(i in times.indices){
            ret.add(KeyFrameData(times[i]))
        }
        return ret.toTypedArray()
    }

    private fun getKeyTimes(): FloatArray {
        val timeData = animNode.getChild("animation")
            ?.getChild("source")?.getChild("float_array")
        val rawTimes = timeData?.data?.trim()?.split(" ")
        val times = FloatArray(rawTimes?.size?:0)
        for( i in times.indices){
            times[i] = rawTimes?.get(i)?.toFloat()!!
        }
        return times
    }

    private fun findRootJointName(): String {
        val skeleton = jointsNode.getChild("visual_scene")
            ?.getChildWithAttribute("node", "id", "Armature")
        return skeleton?.getChild("node")?.getAttribute("id")!!
    }
}