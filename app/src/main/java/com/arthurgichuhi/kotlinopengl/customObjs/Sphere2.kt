package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.kotlinopengl.utils.Utils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Sphere2(radius:Float,gradation: Int) {
    val utils= Utils()
    val pi = Math.PI.toFloat()

    val vertices:MutableList<VD> = ArrayList()
    val triangles:MutableList<IntArray> = ArrayList()

    init {
        createWithOneTex(radius,gradation)
    }

    class VD{
        val utils= Utils()
        private val pi = Math.PI.toFloat()

        var x=0f
        var y=0f
        var z=0f

        var u=0f
        var v=0f

        var phi=0f
        var theta=0f

        var i=0
        var j=0

        var idx=0

        fun fromGrad(i:Int,j:Int,gradation:Int,radius:Float):VD{
            val ret = VD()

            ret.i=i
            ret.j=j

            ret.phi=(j.toFloat()/gradation.toFloat())* pi
            ret.theta=(i.toFloat()/gradation.toFloat())*2f*pi

            ret.x=(radius*cos(ret.theta))* sin(ret.phi)
            ret.z=(radius*sin(ret.theta))* sin(ret.phi)
            ret.y=radius*cos(ret.phi)

            val theta = utils.wrapTo2Pi(ret.theta)
            val phi = utils.wrapTo2Pi(ret.phi)
            val u = theta/(2f*pi)

            if(u>(2*Math.PI)||u<0){
                Log.e("GGGG","${u/Math.PI}")
            }
            val v =phi/Math.PI.toFloat()
            if(v>(2*Math.PI) || v<0){
                Log.e("GGGG","${v/Math.PI}")
            }
            ret.u=1-u
            ret.v=v
            ret.idx=0

            return ret
        }
    }

    private fun addVD(lst:MutableList<VD>,i:Int,j:Int,gradation:Int,radius:Float){
        val v = VD().fromGrad(i,j,gradation,radius)
        v.idx=vertices.size
        vertices.add(v)
        lst.add(v)
    }

    private fun createWithOneTex(radius:Float,gradation: Int){
        var prev:MutableList<VD> = ArrayList()
        addVD(prev,0,0,gradation,radius)

        for(j in 1..gradation){
            val curr:MutableList<VD> = ArrayList()

            if(j == gradation){
                addVD(curr,0,gradation,gradation,radius)
            }
            else{
                for(i in 0..gradation){
                    addVD(curr,i,j,gradation,radius)
                }
            }
            connect(prev,curr)
            prev=curr
        }

    }

    private fun connect(prev:MutableList<VD>,curr:MutableList<VD>){
        if(prev.size==1){
            val one=prev[0]
            for(i in 1..<curr.size){
                triangles.add(intArrayOf(one.idx,curr[i-1].idx,curr[i].idx))
            }
            return
        }
        if(curr.size==1){
            val one = curr[0]
            for(i in 1..<prev.size){
                triangles.add(intArrayOf(one.idx,prev[i-1].idx,prev[i].idx))
            }
            return
        }
        for(i in 1..<prev.size){
            val one = prev[i-1]
            val two = prev[i]
            val three = curr[i]
            val four = curr[i-1]

            triangles.add(intArrayOf(one.idx,two.idx,three.idx))
            triangles.add(intArrayOf(one.idx,three.idx,four.idx))
        }
    }

    fun getPositionsAndTex():FloatArray{
        val vertsTri= 3
        val size = vertsTri * (utils.FloatsPerPosition+utils.FloatsPerPosition) * triangles.size
        val buff = FloatArray(size)
        var buffId = 0

        for( triVerts in triangles){
            for( i in triVerts){
                val v = vertices[i]
                //add x,y,z coordinates of current vertex
                buff[buffId]=v.x
                buffId++
                buff[buffId]=v.y
                buffId++
                buff[buffId]=v.z
                buffId++
                //add corresponding texture coordinates
                buff[buffId]=v.u
                buffId++
                buff[buffId]=v.v
                buffId++
            }
        }
        return buff
    }

}