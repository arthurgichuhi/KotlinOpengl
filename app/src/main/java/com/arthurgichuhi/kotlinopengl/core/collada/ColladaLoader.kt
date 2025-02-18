package com.arthurgichuhi.kotlinopengl.core.collada

import android.content.Context
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.AnimatedModelData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlParser
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class ColladaLoader(private val ctx: Context,private val fileName: String) {
    companion object{
        private val XmlParser =XmlParser()
        private val MathUtils = MathUtils()
    }

    fun loadColladaModel(maxWeights:Int):AnimatedModelData{
        val node = XmlParser.readXMLFile(ctx,fileName)
        val skinLoader = SkinLoader(node!!, maxWeights)
        val skinningData = skinLoader.extractSkinData()
        val jointsLoader = SkeletonLoader(node.getChild("library_visual_scenes")!!,skinningData.jointOrder)
        val jointsData = jointsLoader.extractBoneData()
        val geoLoader = GeometryLoader(node.getChild("library_geometries")!!,skinningData.verticesSkinData)
        val meshData = geoLoader.extractModelData()
        return AnimatedModelData(jointsData,meshData)
    }
}