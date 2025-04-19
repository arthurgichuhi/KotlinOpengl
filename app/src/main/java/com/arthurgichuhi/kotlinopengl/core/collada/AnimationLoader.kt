package com.arthurgichuhi.kotlinopengl.core.collada

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.AnimationData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.JointTransformData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.KeyFrameData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class AnimationLoader(val animNode: XmlNode, val jointsNode:XmlNode, val controller: XmlNode) {

    private var correction = FloatArray(16)
    private val rootNodesList :MutableList<String> = ArrayList()

    init {
        MathUtils.setIdentity4Matrix(correction)
        Matrix.rotateM(correction,0,-90f,1f,0f,0f)
    }

    fun extractAnimationData():AnimationData{
        val rootNode = findRootJointName()
        val times = getKeyTimes()
        val duration = times.last()
        val keyFrames = initKeyFrames(times)
        val animationNodes = animNode.getChildren("animation")
        for(joint in animationNodes){
            loadJointTransforms(keyFrames,joint,"$rootNode/transform")
        }
        return AnimationData(duration,keyFrames,loadInverseTransforms())
    }

    private fun loadJointTransforms(frames: Array<KeyFrameData>, jointData: XmlNode, rootNodeId: String) {
        val jointNameId = getJointName(jointData)
        val dataId = getDataId(jointData)
        val transformData = jointData.getChildWithAttribute("source","id",dataId)
        val rawData = transformData?.getChild("float_array")?.data?.split(" ")
        processTransforms(jointNameId,rawData!!,frames, jointNameId == rootNodeId)
    }

    /**
     * Loads the inverse transform for each joint from the class constructor parameter controller (library_controller) node.
     * @return return a map array of the joint and their respective inverse transforms
     */
    private fun loadInverseTransforms():Map<String,FloatArray>{
        val inverseTrans : MutableMap<String,FloatArray> = HashMap()
        val skinNode = controller.getChild("controller")?.getChild("skin")
        val invTransNode = skinNode?.getChildWithAttribute("source","id","Armature_Cube-skin-bind_poses")
        val namesNode = skinNode?.getChildWithAttribute("source","id","Armature_Cube-skin-joints")
        val namesList = namesNode?.getChild("Name_array")?.data?.split(" ")
        val tmp = invTransNode?.getChild("float_array")?.data?.split(" ")
        var count = 0
        for(name in namesList!!){
            val mat = FloatArray(16)
            for(i in 0..<16){
                mat[i] = tmp!![count + i].toFloat()
            }
            count += 16
            Matrix.transposeM(mat,0,mat,0)
            if(rootNodesList.toSet().contains("$name/transform")){
                Matrix.multiplyMM(mat, 0, correction, 0, mat, 0)
            }
            inverseTrans[name] = mat
        }
        return inverseTrans
    }

    private fun processTransforms(jointName: String, rawData: List<String>, frames: Array<KeyFrameData>, root: Boolean) {
        val matrixData = FloatArray(16)
        for(i in frames.indices){
            for(j in 0..<16){
                matrixData[j] = rawData[i * 16 + j].toFloat()
            }
            val transpose = FloatArray(16)
            Matrix.transposeM(transpose,0,matrixData,0)
            if(root){
                rootNodesList.add(jointName)
                Log.d("TAG","IS__ROOT")
                Matrix.multiplyMM(transpose, 0, correction, 0, transpose, 0)
            }
            frames[i].jointTransforms.add(JointTransformData(jointName, transpose))
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
        return data?.split(" ")?.get(0)!!
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
        val rawTimes = timeData?.data?.split(" ")
        val times = FloatArray(rawTimes?.size!!)
        for( i in times.indices){
            times[i] = rawTimes[i].toFloat()
        }
        return times
    }

    private fun findRootJointName(): String {
        val skeleton = jointsNode.getChild("visual_scene")
            ?.getChildWithAttribute("node", "id", "Armature")
        return skeleton?.getChild("node")?.getAttribute("id")!!
    }
}