package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import com.arthurgichuhi.kotlinopengl.utils.Utils
import kotlin.math.sign

class Cube {

    val vertices= floatArrayOf(
        -0.5f,  0.5f,  0.5f,  // parallel X
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,

        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,

        -0.5f, -0.5f, -0.5f,  // parallel Y
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f, -0.5f,

        -0.5f, -0.5f, -0.5f, // parallel z
        0.5f, -0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,

        -0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        -0.5f, -0.5f,  0.5f,

    )

    fun create(scale: Vec3f):FloatArray{
        val ret=FloatArray(vertices.size)
        val myScale= floatArrayOf(scale.x,scale.y,scale.z)
        var i=0
        while (i<3){
            MathUtils.copyMatColumn(vertices,i,3,ret,i,3)
            MathUtils.scaleMatColumn(ret,i,3,myScale[i])
            i++
        }
        return ret
    }

    fun createWithOneFileTex(scale: Vec3f, cols:Int, rows:Int):FloatArray{
        val nVertices = vertices.size/Utils.FloatsPerPosition
        val stride = Utils.FloatsPerPosition + Utils.FloatsPerTexture
        val ret = FloatArray(nVertices*stride)
        val myScale= floatArrayOf(scale.x,scale.y,scale.z)

        for(i in 0..2){
            MathUtils.copyMatColumn(vertices,i,3,ret,i,stride)
            MathUtils.scaleMatColumn(ret,i,stride,myScale[i])
        }
        //make texture
        var i=0
        while(i<nVertices){

            val x=FloatArray(3)
            val y=FloatArray(3)
            val z=FloatArray(3)

            for(j in 0..2){
                x[j]=ret[(i+j)*stride]
                y[j]=ret[(i+j)*stride+1]
                z[j]=ret[(i+j)*stride+2]
            }

            val u=FloatArray(3)
            val v=FloatArray(3)

            var uv:Pair<Float,Float>?

            if(i<12){
                //parallel to x
                for(j in 0..2){
                    uv=matchTexture(-sign(x[j]) *  z[j],y[j],i,cols,rows)
                    u[j]=uv.first
                    v[j]=uv.second
                }
            }
            else if(i<24){
                //parallel to y
                for(j in 0..2){
                    uv=matchTexture(x[j],-sign(y[j]) *z[j],i,cols,rows)
                    u[j]=uv.first
                    v[j]=uv.second
                }
            }
            else{
                //parallel to z
                for(j in 0..2){
                    uv=matchTexture(sign(z[j]) *x[j],y[j],i,cols,rows)
                    u[j]=uv.first
                    v[j]=uv.second
                }
            }

            for(j in 0..2){
                ret[(i+j) * stride + Utils.FloatsPerPosition] = u[j]
                ret[(i+j) * stride + Utils.FloatsPerPosition+1] = v[j]
            }

            i+=3
        }
        return ret
    }

    private fun matchTexture( x:Float,y:Float,vIdx:Int,colsTex:Int,rowsTex:Int):Pair<Float,Float>{
        val faceIdx= vIdx / 6
        val texRowIdx = faceIdx / colsTex
        val texColIdx= faceIdx % colsTex

        val u= (if(x>0) 1 else 0).toFloat()
        val v = (if(y>0) 0 else 1).toFloat()

        val uStep=1f/colsTex.toFloat()
        val vStep = 1f/rowsTex.toFloat()

        return Pair( u * uStep + texColIdx * uStep,v * vStep + texRowIdx * vStep)
    }


    fun createWithOneFileTexture(
        xScale: Float, yScale: Float, zScale: Float,
        nColsInTexture: Int, nRowsInTexture: Int
    ): FloatArray {
        val nVertices: Int =vertices.size / Utils.FloatsPerPosition
        val stride: Int = Utils.FloatsPerPosition + Utils.FloatsPerTexture
        val ret = FloatArray(nVertices * stride)
        val scale = floatArrayOf(xScale, yScale, zScale)
        for (i in 0..2) {
            MathUtils.copyMatColumn(vertices, i, 3, ret, i, stride
            )
            MathUtils.scaleMatColumn(ret, i, stride, scale[i])
        }

        // make the texture
        var i = 0
        while (i < nVertices) {
            val x = FloatArray(3)
            val y = FloatArray(3)
            val z = FloatArray(3)
            for (j in 0..2) {
                x[j] = ret[(i + j) * stride]
                y[j] = ret[(i + j) * stride + 1]
                z[j] = ret[(i + j) * stride + 2]
            }
            val u = FloatArray(3)
            val v = FloatArray(3)
            var uv: Pair<Float, Float>? = null

            if (i < 12) { // parallel to x
                for (j in 0..2) {
                    uv = matchTexture2(-sign(x[j]) * z[j],
                        y[j], i, nColsInTexture, nRowsInTexture
                    )
                    u[j] = uv.first
                    v[j] = uv.second
                }
            } else if (i < 24) { // parallel to y
                for (j in 0..2) {
                    uv = matchTexture2(x[j], -sign(y[j]) * z[j],
                        i,
                        nColsInTexture,
                        nRowsInTexture
                    )
                    u[j] = uv.first
                    v[j] = uv.second
                }
            } else { // paralle to z
                for (j in 0..2) {
                    uv = matchTexture2(sign(z[j]) * x[j], y[j], i, nColsInTexture, nRowsInTexture)
                    u[j] = uv.first
                    v[j] = uv.second
                }
            }
            for (j in 0..2) {
                ret[(i + j) * stride + Utils.FloatsPerPosition] = u[j]
                ret[(i + j) * stride + Utils.FloatsPerPosition + 1] = v[j]
            }
            i += 3
        }

        return ret
    }

    private fun matchTexture2(
        x: Float, y: Float, vIdx: Int,
        nColsInTexture: Int, nRowsInTexture: Int
    ): Pair<Float, Float> {
        val faceIdx = vIdx / 6 // from 0 to 5 since the cube has 6 faces
        val textureRowIdx =
            faceIdx / nColsInTexture // for faces 0, 1, ..., nColsInTexture, the textureRowIdx will be 1
        // for faces nColsInTexture, nColsInTexture+1, ..., 2*nColsInTexture-1,
        //the textureRowIdx will be 2 and so on
        val textureColIdx = faceIdx % nColsInTexture

        val u = (if (x > 0) 1 else 0).toFloat()
        val v = (if (y > 0) 0 else 1).toFloat()
        val uStep = 1.0f / nColsInTexture.toFloat()
        val vStep = 1.0f / nRowsInTexture.toFloat()
        return Pair(
            u * uStep + textureColIdx * uStep,
            v * vStep + textureRowIdx * vStep
        )
    }
}