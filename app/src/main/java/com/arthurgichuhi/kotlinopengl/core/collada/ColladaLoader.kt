package com.arthurgichuhi.kotlinopengl.core.collada

import android.content.Context
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.AnimatedModelData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.AnimationData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlParser

class ColladaLoader(private val ctx: Context,private val fileName: String) {
    companion object{
        private val XmlParser =XmlParser()
    }

    fun loadColladaModel(maxWeights:Int):AnimatedModelData{
        val node = XmlParser.readXMLFile(ctx,fileName)
        val skinLoader = SkinLoader(node!!, maxWeights)
        val skinningData = skinLoader.extractSkinData()
        val jointsLoader = SkeletonLoader(node,skinningData.jointOrder)
        val jointsData = jointsLoader.extractBoneData()
        val geoLoader = GeometryLoader(node.getChild("library_geometries")!!,skinningData.verticesSkinData)
        val meshData = geoLoader.extractModelData()
        return AnimatedModelData(jointsData,meshData)
    }

    fun loadColladaAnimation(): AnimationData{
        val node = XmlParser.readXMLFile(ctx,fileName)
        val animNode = node?.getChild("library_animations")
        val jointsNode = node?.getChild("library_visual_scenes")
        val loader = AnimationLoader(animNode!!,jointsNode!!)
        val animData = loader.extractAnimationData()
        return animData
    }
}