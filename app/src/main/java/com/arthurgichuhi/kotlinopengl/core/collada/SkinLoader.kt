package com.arthurgichuhi.kotlinopengl.core.collada

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.SkinningData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.VertexSkinData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode

class SkinLoader(private val controllerNode:XmlNode,val maxWeights:Int) {
    var cNode : XmlNode

    init {
        cNode = controllerNode.getChild("controller")!!.getChild("skin")!!
    }

    fun extractSkinData():SkinningData{
        val jointsLists = loadJointsList()
        val weights = loadWeights()
        val weightsDataNode = cNode.getChild("vertex_weights")
        val effectorJointCounts = getEffectiveJointsCount(weightsDataNode!!)
        val vertexWeights = getSkinData(weightsDataNode,effectorJointCounts,weights)
        return SkinningData(jointsLists.toList(),vertexWeights)
    }

    private fun loadJointsList():List<String>{
        val inputNode = cNode.getChild("joints")!!
        val jointDataId = inputNode
            .getChildWithAttribute("input","semantic","JOINT")!!
            .getAttribute("source")?.substring(1)
        val jointsNode = cNode.getChildWithAttribute("source","id",jointDataId!!)!!
            .getChild("Name_array")
        val names = jointsNode?.data?.split(" ")
        val jointsList : MutableList<String> = ArrayList()
        for(name in names!!){
            jointsList.add(name)
        }
        return jointsList
    }

    private fun loadWeights():FloatArray{
        val inputNode = controllerNode.getChild("vertex_weights")!!
        val weightsDataId = inputNode
            .getChildWithAttribute("input","semantic","WEIGHT")!!
            .attributes["source"]!!.substring(1)
        val weightsNode = controllerNode
            .getChildWithAttribute("source","id",weightsDataId)!!
            .getChild("float_array")
        val rawData = weightsNode?.data?.trim()?.split(" ")
        val weights = FloatArray(rawData?.size!!)
        for(i in weights.indices){
            weights[i] = rawData[i].toFloat()
        }
        return weights
    }

    private fun getEffectiveJointsCount(weightsDataNode:XmlNode):IntArray{
        val rawData = weightsDataNode.getChild("vcount")
            ?.data?.trim()?.split(" ")
        val counts = IntArray(rawData?.size?:0)
        for(i in rawData?.indices!!){
            counts[i] = rawData[i].toInt()
        }
        return counts
    }

    private fun getSkinData(weightsNode:XmlNode,counts:IntArray,weights:FloatArray):List<VertexSkinData>{
        val rawData = weightsNode.getChild("v")?.data?.trim()?.split(" ")
        val ret :MutableList<VertexSkinData> = ArrayList()
        var pointer = 0
        for(count in counts){
            val skinData = VertexSkinData()
            for(i in 0..<count){
                val jointId = rawData!![pointer++].toInt()
                val weightId = rawData[pointer++].toInt()
                skinData.addJointEffect(jointId,weights[weightId])
            }
            skinData.limitJointNumber(maxWeights)
            ret.add(skinData)
        }
        return ret
    }
}