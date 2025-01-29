package com.arthurgichuhi.kotlinopengl.utils

import com.arthurgichuhi.aopengl.models.Vec3
import kotlin.math.cos
import kotlin.math.sin


class MathUtils {

    fun setRotateEulerM2(rm:FloatArray,rmOffset:Int,points:Vec3){
        require(rmOffset >= 0) { "rmOffset < 0" }
        require(rm.size >= rmOffset + 16) { "rm.length < rmOffset + 16" }

        points.x *= (Math.PI / 180.0f).toFloat()
        points.y *= (Math.PI / 180.0f).toFloat()
        points.z *= (Math.PI / 180.0f).toFloat()
        val cx = cos(points.x.toDouble()).toFloat()
        val sx = sin(points.x.toDouble()).toFloat()
        val cy = cos(points.y.toDouble()).toFloat()
        val sy = sin(points.y.toDouble()).toFloat()
        val cz = cos(points.z.toDouble()).toFloat()
        val sz = sin(points.z.toDouble()).toFloat()
        val cxsy = cx * sy
        val sxsy = sx * sy

        rm[rmOffset + 0] = cy * cz
        rm[rmOffset + 1] = -cy * sz
        rm[rmOffset + 2] = sy
        rm[rmOffset + 3] = 0.0f

        rm[rmOffset + 4] = sxsy * cz + cx * sz
        rm[rmOffset + 5] = -sxsy * sz + cx * cz
        rm[rmOffset + 6] = -sx * cy
        rm[rmOffset + 7] = 0.0f

        rm[rmOffset + 8] = -cxsy * cz + sx * sz
        rm[rmOffset + 9] = cxsy * sz + sx * cz
        rm[rmOffset + 10] = cx * cy
        rm[rmOffset + 11] = 0.0f

        rm[rmOffset + 12] = 0.0f
        rm[rmOffset + 13] = 0.0f
        rm[rmOffset + 14] = 0.0f
        rm[rmOffset + 15] = 1.0f
    }

    fun scale(a: FloatArray, s: Float) {
        for (i in a.indices) {
            a[i] *= s
        }
    }

    fun scaleMatColumn(mat:FloatArray,colIdx:Int,nColumns:Int,scale:Float){
        var i: Int = colIdx
        while (i < mat.size) {
            mat[i] *= scale
            i += nColumns
        }
    }

    fun shiftMatColumn(mat:FloatArray,colIdx:Int,nColumns:Int,offset:Float){
        var i = colIdx
        while (i < mat.size) {
            mat[i] += offset
            i += nColumns
        }
    }

    fun copyMatColumn(src:FloatArray,srcColIdx:Int,srcNCols:Int,dest:FloatArray,destColIdx:Int,destNCols:Int){
            var i = destColIdx
            var j: Int = srcColIdx
            while (i < dest.size) {
                dest[i] = src[j]
                i += destNCols
                j += srcNCols
            }
    }
}