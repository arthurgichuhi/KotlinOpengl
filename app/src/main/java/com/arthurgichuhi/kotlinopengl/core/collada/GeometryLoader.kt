package com.arthurgichuhi.kotlinopengl.core.collada

import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec2f
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.Vertex
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.VertexSkinData
import com.arthurgichuhi.kotlinopengl.core.xmlParser.XmlNode
import com.arthurgichuhi.kotlinopengl.models.Vec4f
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

class GeometryLoader(geometryNode:XmlNode, private val vertexWeights:List<VertexSkinData>) {
    private var correction = FloatArray(16)
    init {
        MathUtils.setIdentity4Matrix(correction)
        Matrix.rotateM(correction,0,-90f,1f,0f,0f)
    }

    private var meshData : XmlNode = geometryNode.getChild("geometry")?.getChild("mesh")!!
    private lateinit var verticesArray : FloatArray
    private lateinit var texturesArray : FloatArray
    private lateinit var normalsArray : FloatArray
    private lateinit var indicesArray : ShortArray
    private lateinit var jointIds : IntArray
    private lateinit var weightsArray : FloatArray

    val vertices : MutableList<Vertex> = ArrayList()
    private val textures : MutableList<Vec2f> = ArrayList()
    private val normals : MutableList<Vec3f> =  ArrayList()
    val indices : MutableList<Short> = ArrayList()

    fun extractModelData(): MeshData {
        readRawData()
        assembleVertices()
        removeUnusedVertices()
        initArrays()
        convertDataToArrays()
        convertIndicesToListArray()
        return MeshData(verticesArray,texturesArray,normalsArray,indicesArray,jointIds,weightsArray)
    }

    private fun readRawData() {
        readPositions()
        readNormals()
        readTexCoords()
    }

    private fun readPositions() {
        val positionId = meshData.getChild("vertices")?.getChild("input")
            ?.getAttribute("source")?.substring(1)
        val positionsData = meshData.getChildWithAttribute("source","id",positionId!!)
            ?.getChild("float_array")
        val count = positionsData?.getAttribute("count")?.toInt()
        val posData = positionsData?.data?.trim()?.split(" ")
        for(i in 0..<(count!!/3)){
            val x = posData!![i * 3].toFloat()
            val y = posData[i * 3 + 1].toFloat()
            val z = posData[i * 3 + 2].toFloat()
            val pos = Vec4f(x, y, z,1f)
            val position = FloatArray(4)
            Matrix.multiplyMV(position,0,correction,0,pos.toFloatArray(),0)
            vertices.add(Vertex(Vec3f(position),vertices.size,vertexWeights[vertices.size]))
        }
    }

    private fun readNormals() {
        val normalId = meshData.getChild("polylist")
            ?.getChildWithAttribute("input","semantic","NORMAL")
            ?.getAttribute("source")?.substring(1)
        val normalsData = meshData
            .getChildWithAttribute("source","id",normalId!!)
            ?.getChild("float_array")
        val count = normalsData?.getAttribute("count")?.toInt()
        val normData = normalsData?.data?.trim()?.split(" ")
        for(i in 0..<(count!!/3)){
            val x = normData!![i*3].toFloat()
            val y = normData[i*3+1].toFloat()
            val z = normData[i*3+2].toFloat()
            val norm = Vec4f(x, y, z)
            val normal = FloatArray(4)
            Matrix.multiplyMV(normal,0,correction,0,norm.toFloatArray(),0)
            normals.add(Vec3f(normal))
        }
    }

    private fun readTexCoords() {
        val texCordId = meshData.getChild("polylist")
            ?.getChildWithAttribute("input","semantic","TEXCOORD")
            ?.getAttribute("source")?.substring(1)
        val texCoordsData = meshData.getChildWithAttribute("source","id",texCordId!!)
            ?.getChild("float_array")
        val count = texCoordsData?.getAttribute("count")?.toInt()
        val texData = texCoordsData?.data?.trim()?.split(" ")
        for(i in 0..<(count!!/2)){
            val s = texData!![i * 2].toFloat()
            val t = texData[i * 2 + 1].toFloat()
            textures.add(Vec2f(s,t))
        }
    }

    private fun assembleVertices() {
        val poly = meshData.getChild("polylist")
        val typeCount = poly?.getChildren("input")?.size
        val indexData = poly?.getChild("p")?.data?.trim()?.split(" ")
        for(i in 0..<(indexData!!.size/typeCount!!)){
            val positionIndex = indexData[i*typeCount].toInt()
            val normalIndex = indexData[i*typeCount + 1].toInt()
            val texCoordIndex = indexData[i*typeCount + 2].toInt()
            processVertex(positionIndex,normalIndex,texCoordIndex)
        }
    }

    private fun processVertex(positionIndex: Int, normalIndex: Int, texCoordIndex: Int):Vertex{
        val currentVertex = vertices[positionIndex]
        if(!currentVertex.isSet()){
            currentVertex.textureIndex = texCoordIndex
            currentVertex.normalIndex = normalIndex
            indices.add(positionIndex.toShort())
            return currentVertex
        }
        else{
            return dealWithProcessedVertex(currentVertex,texCoordIndex,normalIndex)
        }
    }

    private fun dealWithProcessedVertex(previousVertex: Vertex, texCoordIndex: Int, normalIndex: Int): Vertex {
        if(previousVertex.hasTexAndNormal(texCoordIndex,normalIndex)){
            indices.add(previousVertex.index.toShort())
            return previousVertex
        }
        else{
            val anotherVertex = previousVertex.dupVertex
            if(anotherVertex!=null){
                return dealWithProcessedVertex(anotherVertex,texCoordIndex,normalIndex)
            }
            else{
                val dupVertex = Vertex(
                    previousVertex.position,
                    vertices.size,
                    previousVertex.weightsData,
                    textureIndex = texCoordIndex,
                    normalIndex = normalIndex,
                    )
                previousVertex.dupVertex = dupVertex
                vertices.add(dupVertex)
                indices.add(dupVertex.index.toShort())
                return dupVertex
            }
        }
    }

    private fun removeUnusedVertices() {
        for(vertex in vertices){
            vertex.averageTangents()
            if(!vertex.isSet()){
                vertex.textureIndex = 0
                vertex.normalIndex = 0
            }
        }
    }

    private fun initArrays() {
        verticesArray = FloatArray(vertices.size * 3)
        texturesArray = FloatArray(vertices.size * 2)
        normalsArray = FloatArray(vertices.size * 3)
        jointIds = IntArray(vertices.size * 3)
        weightsArray = FloatArray(vertices.size * 3)
    }

    private fun convertDataToArrays() {
        var furthestPoint = 0f
        for(i in vertices.indices){
            val currentVert = vertices[i]
            if(currentVert.length>furthestPoint){
                furthestPoint = currentVert.length
            }
            val weights = currentVert.weightsData
            val position = currentVert.position
            val texCoord = textures[currentVert.textureIndex]
            val normalVector = normals[currentVert.normalIndex]

            verticesArray[i * 3] = position.x
            verticesArray[(i * 3) + 1] = position.y

            verticesArray[(i * 3) + 2] = position.z

            texturesArray[(i * 2)] = texCoord.x
            texturesArray[(i * 2) +1] = 1 - texCoord.y

            normalsArray[i * 3] = normalVector.x
            normalsArray[(i * 3) + 1] = normalVector.y
            normalsArray[(i * 3) + 2] = normalVector.z

            jointIds[i * 3] = weights.jointIds[0]
            jointIds[i * 3 + 1] = weights.jointIds[1]
            jointIds[i * 3 + 2] = weights.jointIds[2]

            weightsArray[i * 3] = weights.weights[0]
            weightsArray[(i * 3) + 1] = weights.weights[1]
            weightsArray[(i * 3) + 2] =  weights.weights[2]
        }
    }

    private fun convertIndicesToListArray():ShortArray{
        indicesArray = ShortArray(indices.size)
        for(i in indices.indices){
            indicesArray[i] = indices[i]
        }
        return indicesArray
    }
}