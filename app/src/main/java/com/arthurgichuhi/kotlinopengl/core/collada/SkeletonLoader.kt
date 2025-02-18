package com.arthurgichuhi.kotlinopengl.core.collada

import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.SkeletonData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode
import com.arthurgichuhi.kotlinopengl.models.JointData
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class SkeletonLoader(vsNode:XmlNode,boneBorder:List<String>) {
    companion object{
        val MathUtils = MathUtils()
    }
    private lateinit var armatureData:XmlNode

    private lateinit var boneOrder:List<String>

    var jointCount = 0

    init {
        this.armatureData = vsNode.getChild("node")!!
            .getChildWithAttribute("node","id","Armature")!!
        this.boneOrder = boneBorder
    }

    fun extractBoneData():SkeletonData{
        val headNode = armatureData.getChild("node")
        val headJoint = loadJointData(headNode!!,true)
        return SkeletonData(jointCount,headJoint)
    }

    private fun loadJointData(jointNode:XmlNode,isRoot:Boolean):JointData{
        val joint = extractMainJointData(jointNode,isRoot)
        for(childeNode in jointNode.getChildren("node")){
            joint.addChild(loadJointData(childeNode,false))
        }
        return joint
    }

    private fun extractMainJointData(jointNode: XmlNode,isRoot:Boolean):JointData{
        val name = jointNode.getAttribute("id")
        val index = boneOrder.indexOf(name!!)
        val matrixData = jointNode.getChild("matrix")?.data?.split("")
        var matrix = converData(matrixData!!)
        Matrix.transposeM(matrix,0,matrix,0)
        if(isRoot){
            matrix = MathUtils.rotateVec3(matrix,-90f, Vec3(x=1f).toArray())
        }
        jointCount++
        return JointData(index,name,matrix)
    }

    private fun converData(data:List<String>):FloatArray{
        val matrixData = FloatArray(16)
        for(i in matrixData.indices){
            matrixData[i] = data[i].toFloat()
        }
        return matrixData
    }
}