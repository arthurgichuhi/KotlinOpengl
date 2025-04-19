package com.arthurgichuhi.kotlinopengl.core

import android.content.Context
import com.arthurgichuhi.kotlinopengl.utils.Utils

class WaveFrontLoader(context: Context,name:String) {
    data class VertData(
        var vertIdx:Int=0,
        var texIdx:Int=0,
        var normalIdx:Int=0)

    class Face(vertData:IntArray){
        val data: MutableList<VertData> = ArrayList()
        init {
            data.addAll(arrayOf(VertData(),VertData(),VertData()))
            data[0].vertIdx = vertData[0]
            data[0].texIdx = vertData[1]
            data[0].normalIdx =  vertData[2]

            data[1].vertIdx = vertData[3]
            data[1].texIdx = vertData[4]
            data[1].normalIdx =  vertData[5]

            data[2].vertIdx = vertData[6]
            data[2].texIdx = vertData[7]
            data[2].normalIdx =  vertData[8]
        }
    }

    private val fileName = name
    private val ctx = context
    private val vertices : MutableList<FloatArray> = ArrayList()
    private val texCoords : MutableList<FloatArray> = ArrayList()
    private val normals : MutableList<FloatArray> = ArrayList()
    private val faces : MutableList<Face> = ArrayList()

    init {
        load()
    }

    private fun load() {
        val text = Utils.readAssetFile(ctx,fileName)
        if(Utils.isNullOrEmpty(text)){
            throw Exception("Error reading file $fileName")
        }
        val lines = text!!.split("\\r?\\n".toRegex())
        for(i in lines.indices){
            var line = lines[i]
            line = line.trim()
            if(line.isEmpty())continue
            if(line.startsWith("#"))continue
            if(line.startsWith("mtllib"))continue
            if(line.startsWith("usemtl"))continue
            if(line.startsWith("o"))continue
            if(line.startsWith("s"))continue

            if(line.startsWith("vn")){
                val tmp = getFloats(line.replace("vn",""),3)
                normals.add(tmp)
                continue
            }

            if(line.startsWith("vt")){
                val tmp = getFloats(line.replace("vt",""),2)
                texCoords.add(tmp)
                continue
            }

            if(line.startsWith("v")){
                val tmp = getFloats(line.replace("v",""),3)
                vertices.add(tmp)
                continue
            }

            if(line.startsWith("f")){
                val tmp = getInts(line.replace("f",""))
                val face = Face(tmp)
                faces.add(face)
                continue
            }
        }
    }

    private fun getFloats(str: String,n:Int):FloatArray{
        val res = str.trim().split("\\s+".toRegex())
        val ret = FloatArray(n)

        var retIdx =0
        for(i in res.indices){
            var curr = res[i]
            if(Utils.isNullOrEmpty(curr))continue
            curr = curr.trim()
            if(Utils.isNullOrEmpty(curr))continue
            ret[retIdx] = curr.toFloat()
            retIdx++
        }

        return ret
    }

    private fun getInts(str: String):IntArray{
        val res = str.trim().split("\\s".toRegex())
        val ret = IntArray(9)

        for(i in res.indices){
            var curr = res[i]
            if(Utils.isNullOrEmpty(curr))continue
            curr = curr.trim()
            if(Utils.isNullOrEmpty(curr))continue

            val vertStrData = curr.trim().split("/")
            if(vertStrData.size!=3)throw Exception("$str has invalid vertex data $vertStrData")

            for(j in 0..2){
                var intStr = vertStrData[j]
                val retIdx = i * 3 + j
                ret[retIdx] = -1
                if(Utils.isNullOrEmpty(intStr))continue
                intStr = intStr.trim()
                if(Utils.isNullOrEmpty(intStr))continue
                val tmp = intStr.toInt()
                if(tmp<=0)throw Exception("$str contains invalid index")
                ret[retIdx] = tmp - 1
            }
        }

        return ret
    }

    fun hasTexture():Boolean{
        return texCoords.isNotEmpty()
    }

    fun hasNormal():Boolean{
        return normals.isNotEmpty()
    }

    fun getFaces(texture:Boolean,normal:Boolean):FloatArray{
        var stride = Utils.FloatsPerPosition
        if(texture)stride+=Utils.FloatsPerTexture
        if(normal)stride+=Utils.FloatsPerNormal

        val ret = FloatArray(stride*faces.size*3)

        var curr = 0

        for(face in faces){
            for(i in 0..2){
                val v = vertices[face.data[i].vertIdx]
                ret[curr] =v[0]
                ret[curr+1] = v[1]
                ret[curr+2] =v[2]
                curr+=3
                if(texture){
                    val t = texCoords[face.data[i].texIdx]
                    ret[curr] = t[0]
                    ret[curr+1] = t[1]
                    curr+=2
                }
                if(normal){
                    val n = normals[face.data[i].normalIdx]
                    ret[curr] =  n[0]
                    ret[curr+1] = n[1]
                    ret[curr+2] = n[2]
                    curr += 3
                }
            }
        }

        return ret
    }
}