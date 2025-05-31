package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import android.util.Pair
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import com.arthurgichuhi.kotlinopengl.utils.Utils
import kotlin.math.acos
import kotlin.math.atan2

class Sphere(divide:Int) {
    private val TAG = "SPHERE"

    private val X = 0.525f
    private val Z = 0.850f

    private val vertices:MutableList<FloatArray> = ArrayList()
    private var triangles:MutableList<IntArray> = ArrayList()

    private val startVerts:Array<FloatArray> = arrayOf(
        floatArrayOf(-X, 0.0F, Z), floatArrayOf(X, 0.0F, Z), floatArrayOf(-X, 0.0F, -Z), floatArrayOf(X, 0.0F, -Z),
        floatArrayOf(0.0F, Z, X), floatArrayOf(0.0F, Z, -X), floatArrayOf(0.0F, -Z, X), floatArrayOf(0.0F, -Z, -X),
        floatArrayOf(Z, X, 0.0F), floatArrayOf(-Z, X, 0.0F), floatArrayOf(Z, -X, 0.0F), floatArrayOf(-Z, -X, 0.0F)
    )

    private val startTriangles = arrayOf(
        intArrayOf(0,4,1), intArrayOf(0,9,4), intArrayOf(9,5,4), intArrayOf(4,5,8), intArrayOf(4,8,1),
        intArrayOf(8,10,1), intArrayOf(8,3,10), intArrayOf(5,3,8), intArrayOf(5,2,3), intArrayOf(2,7,3),
        intArrayOf(7,10,3), intArrayOf(7,6,10), intArrayOf(7,11,6), intArrayOf(11,0,6), intArrayOf(0,1,6),
        intArrayOf(6,1,10), intArrayOf(9,0,11), intArrayOf(9,11,2), intArrayOf(9,2,5), intArrayOf(7,2,11)
    )

    init {
        for(element in startVerts){
            vertices.add(element)
        }
        for(i in startTriangles){
            triangles.add(i)
        }
        for(i in 0..<divide){
            triangles = divide()
        }
    }


    class MMap{
        val map:MutableMap<String,Int> = HashMap()
        fun key(first: Int, second: Int): String {
            return "$first,$second"
        }

        fun insert(key:String,value:Int):Boolean{
            if(map.containsKey(key))return false
            map[key]=value
            return true
        }

        fun getKey(key:String):Int{
            return map[key]!!
        }
    }

    private fun getVertexIdx(available:MMap,first:Int,second:Int):Int{
        var val1=0
        var val2=0
        if(first<second){
            val1=second
            val2=first
        }
        else{
            val1=first
            val2=second
        }
        val key= MMap().key(val1,val2)
        val inserted = available.insert(key,vertices.size)
        if(inserted){
            val edge= vertices[first]
            val edge2 = vertices[second]
            val newPoint = MathUtils.addFloatArrays(edge,edge2)
            MathUtils.normalize(newPoint)
            vertices.add(newPoint)
        }
        return available.getKey(key)
    }

    private fun divide(): MutableList<IntArray> {
        val mMap = MMap()
        val results:MutableList<IntArray> = ArrayList()

        for(t in triangles){
            val mid = IntArray(3)

            for(edge in 0..2){
                mid[edge]=getVertexIdx(mMap,t[edge],t[(edge+1)%3])
            }
            val newTriangles = arrayOf(
                arrayOf(t[0],mid[0],mid[2]),
                arrayOf(t[1],mid[1],mid[0]),
                arrayOf(t[2],mid[2],mid[1]),
                arrayOf(mid[0],mid[1],mid[2])
            )

            for(element in newTriangles){
                results.add(element.toIntArray())
            }
        }
        return results
    }

    fun getPositions():FloatArray{
        val triangleVerts=3
        val size = triangleVerts * Utils.FloatsPerPosition * triangles.size
        val buff = FloatArray(size)
        var buffId = 0

        for(t in triangles){
            for(vId in t){
                val vert = vertices[vId]
                for(c in vert){
                    buff[buffId]=c
                    buffId++
                }
            }
        }
        return buff
    }

    fun oneTriangleTex(triangleVertIds:IntArray):Pair<FloatArray,FloatArray>{
        val us = FloatArray(triangleVertIds.size)
        val vs = FloatArray(triangleVertIds.size)

        for(i in us.indices){
            val vertIdx=triangleVertIds[i]
            val vert = vertices[vertIdx]
            val rho = MathUtils.norm(vert)
            val theta = Utils.wrapTo2Pi(atan2(vert[0].toDouble(),vert[2].toDouble()).toFloat())
            val phi = Utils.wrapTo2Pi(acos((vert[1]/rho).toDouble()).toFloat())
            val u = theta/(2*Math.PI.toFloat())
            if(u>(2*Math.PI)||u<0){
                Log.e(TAG,"${u/Math.PI.toFloat()}")
            }
            val v =phi/Math.PI.toFloat()
            if(v>(2*Math.PI)||v<0){
                Log.e(TAG,"${v/Math.PI.toFloat()}")
            }
            us[i] = u
            vs[i] = v
        }
        return Pair(us,vs)
    }

    fun getPositionsAndTexture():FloatArray{
        val triangleVerts = 3
        val size =triangleVerts *(Utils.FloatsPerPosition+Utils.FloatsPerTexture)*triangles.size
        val buff = FloatArray(size)
        var buffId = 0
        for(triVerts in triangles){
            val uvs = oneTriangleTex(triVerts)
            for(i in triVerts.indices){
                val vertId=triVerts[i]
                val vert = vertices[vertId]
                for(c in vert){
                    buff[buffId]=c
                    buffId++
                }
                buff[buffId] = uvs.first[i]
                buffId++
                buff[buffId] = uvs.second[i]
                buffId++
            }
        }
        return buff
    }

}