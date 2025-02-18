package com.arthurgichuhi.kotlinopengl.core.collada

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.SkinningData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.VertexSkinData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode

class SkinLoader(controllerNode:XmlNode,maxWeights:Int) {
    var cNode : XmlNode
    var mWeights : Int =0

    init {
        cNode = controllerNode.getChild("controller")!!.getChild("skin")!!
        mWeights = maxWeights
    }

    fun extractSkinData():SkinningData{
        val jointsLists = loadJointsList()
        val weights = loadWeights()
        val weightsDataNode = cNode.getChild("vertex_weights")
        val effectorJointCounts = getEffectiveJointsCount(weightsDataNode!!)
        val vertexWeights = getSkinData(weightsDataNode,effectorJointCounts,weights)
        return SkinningData(jointsLists.toList(),vertexWeights.toList())
    }

    private fun loadJointsList():List<String>{
        val inputNode = cNode.getChild("vertex_weights")!!
        val jointDataId = inputNode
            .getChildWithAttribute("source","id","JOINT")!!
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
        val inputNode = cNode.getChild("vertex_weights")
        val weightsDataId = inputNode!!
            .getChildWithAttribute("input","semantic","WEIGHT")!!
            .data.substring(1)
        val weightsNode = cNode.getChildWithAttribute("source","id",weightsDataId)!!
            .getChild("float_array")
        val rawData = weightsNode?.data?.split(" ")
        val weights = FloatArray(rawData?.size?:0)
        for(i in weights.indices){
            weights[i] = rawData?.get(i)?.toFloat()?:0f
        }
        return weights
    }

    private fun getEffectiveJointsCount(weightsDataNode:XmlNode):IntArray{
        val rawData = weightsDataNode
            .getChild("vcount")?.data?.split(" ")
        val counts = IntArray(rawData?.size?:0)
        for(i in rawData?.indices!!){
            counts[i] = rawData[i].toInt()
        }
        return counts
    }

    private fun getSkinData(weightsNode:XmlNode,counts:IntArray,weights:FloatArray):List<VertexSkinData>{
        val rawData = weightsNode.getChild("v")?.data?.split(" ")
        val ret :MutableList<VertexSkinData> = ArrayList()
        var pointer = 0
        for(count in counts){
            val skinData = VertexSkinData()
            for(i in counts.indices){
                val jointId = rawData!![pointer++].toInt()
                val weightId = rawData[pointer++].toInt()
                skinData.addJointEffect(jointId,weights[weightId])
            }
            skinData.limitJointNumber(mWeights)
            ret.add(skinData)
        }
        return ret
    }
}