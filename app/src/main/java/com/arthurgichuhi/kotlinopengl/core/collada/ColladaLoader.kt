package com.arthurgichuhi.kotlinopengl.core.collada

import android.content.Context
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.AnimatedModelData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
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
        val jointsLoader = SkeletonLoader(node,skinningData.jointOrder)
        val jointsData = jointsLoader.extractBoneData()
        val geoLoader = GeometryLoader(node.getChild("library_geometries")!!,skinningData.verticesSkinData)
        val meshData = geoLoader.extractModelData()
        return AnimatedModelData(jointsData,meshData)
    }

    fun loadOneVBO(maxWeights:Int):Pair<FloatArray,MeshData>{
        val meshData = loadColladaModel(maxWeights)
        val ret = FloatArray(meshData.mesh.indices.size*8)
        var count = 0
        Log.d("TAG","VS:${meshData.mesh}---${ret.size}--${meshData.mesh.vertices.size}")
        for(i in 0..<meshData.mesh.indices.size/3){
            ret[count] = meshData.mesh.vertices[meshData.mesh.indices[i * 3].toInt()]
            ret[count + 1] = meshData.mesh.vertices[meshData.mesh.indices[i * 3 + 1].toInt()]
            ret[count + 2] =  meshData.mesh.vertices[meshData.mesh.indices[i * 3 + 2].toInt()]
            count += 3
            //texture Coordinates
            ret[count] = meshData.mesh.textureCords[i * 2]
            ret[count + 1] = meshData.mesh.textureCords[i * 2 + 1]
            count+=2
            //normals
            ret[count] =  meshData.mesh.normals[i * 3]
            ret[count + 1] = meshData.mesh.normals[i * 3 + 1]
            ret[count + 2] = meshData.mesh.normals[i * 3 + 2]
            count+=3
        }
//        for(i in 0..<meshData.mesh.indices.size/3){
//            //vertices
//            ret[count] = meshData.mesh.vertices[i * 3]
//            ret[count + 1] = meshData.mesh.vertices[i * 3 + 1]
//            ret[count + 2] =  meshData.mesh.vertices[i * 3 + 2]
//            count += 3
//            //texture Coords
//            ret[count] = meshData.mesh.textureCords[i * 3]
//            ret[count + 1] = meshData.mesh.textureCords[i * 3 + 1]
//            count+=2
//            //normals
//            ret[count] =  meshData.mesh.normals[i * 3]
//            ret[count + 1] = meshData.mesh.normals[i * 3 + 1]
//            ret[count + 2] = meshData.mesh.normals[i * 3 + 2]
//            count+=3
//        }
//        Log.d("TAG","RET:$count:${ret.last()}")
        return Pair(ret,meshData.mesh)
    }
}