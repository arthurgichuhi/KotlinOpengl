package com.arthurgichuhi.kotlinopengl.core.collada

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.SkeletonData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode
import com.arthurgichuhi.kotlinopengl.models.JointData
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class SkeletonLoader(vsNode:XmlNode,boneBorder:List<String>) {
    companion object{
        val MathUtils = MathUtils()
    }
    private var armatureData:XmlNode =
        vsNode.getChild("visual_scene")?.getChildWithAttribute("node","id","Armature")!!

    private var boneOrder:List<String> = boneBorder

    private var jointCount = 0

    fun extractBoneData():SkeletonData{
        val headNode = armatureData.getChild("node")
        val headJoint = loadJointData(headNode!!,true)
        return SkeletonData(jointCount,headJoint)
    }

    private fun loadJointData(jointNode:XmlNode,isRoot:Boolean):JointData{
        val joint = extractMainJointData(jointNode,isRoot)
        for(childNode in jointNode.getChildren("node")){
            joint.addChild(loadJointData(childNode,false))
        }
        return joint
    }

    private fun extractMainJointData(jointNode: XmlNode,isRoot:Boolean):JointData{
        val name = jointNode.getAttribute("id")
        val index = boneOrder.indexOf(name!!)
        val matrixData = jointNode.getChild("matrix")?.data?.trim()?.split(" ")
        var matrix = convertData(matrixData!!)
        Matrix.transposeM(matrix,0,matrix,0)
        if(isRoot){
            matrix = MathUtils.rotateVec3(Vec3(x=1f).toArray(),-90f, matrix)
        }
        jointCount++
        return JointData(index,name,matrix)
    }

    private fun convertData(data:List<String>):FloatArray{
        val matrixData = FloatArray(16)
        for(i in matrixData.indices){
            matrixData[i] = data[i].toFloat()
        }
        return matrixData
    }
}