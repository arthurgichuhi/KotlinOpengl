package com.arthurgichuhi.kotlinopengl.utils

import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec3
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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

    fun translateMat4(mat4:FloatArray,xyz:FloatArray){
        for (i in 0..<3){
            mat4[12+i]+=xyz[i]
        }
    }

    fun addFloatArrays(a:FloatArray,b:FloatArray):FloatArray{
        val ret = FloatArray(a.size)
        for(i in a.indices){
            ret[i] = a[i] + b[i]
        }
        return ret
    }

    fun subFloatArrays(a:FloatArray,b:FloatArray):FloatArray{
        val ret = FloatArray(a.size)
        for(i in a.indices){
            ret[i] = a[i] - b[i]
        }
        return ret
    }

    fun scale(a: FloatArray, s: Float) {
        for (i in a.indices) {
            a[i] *= s
        }
    }

    fun norm(a:FloatArray):Float{
        var n=0f
        for(i in a.indices){
            n += a[i] * a[i]

        }
        return sqrt(n.toDouble()).toFloat()
    }

    fun normalize(a:FloatArray){
        var s = norm(a)
        if(s!=0f){
            s=1f/s
            scale(a,s)
        }
    }

    fun setIdentity4Matrix(mat:FloatArray){
        for(i in 0..3){
            for(j in 0..3){
                mat[i*4+j] = if(i==j)1f else 0f
            }
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

    fun cross(a:FloatArray,b: FloatArray):FloatArray{
        val x = a[1] * b[2] - a[2] * b[2]
        val y = a[2] * b[0] - a[0] * b[2]
        val z = a[0] * b[1] - a[1] * b[0]

        return floatArrayOf(x,y,z)
    }

    fun rotateVec3(v:FloatArray,angle:Float,axis:Vec3):FloatArray{
        val m = FloatArray(16)
        setIdentity4Matrix(m)
        Matrix.rotateM(m,0,angle,axis.x,axis.y,axis.z)
        return matVecMultiply(m,v,4)
    }

    fun matVecMultiply(m:FloatArray,v:FloatArray,nRows:Int):FloatArray{
        val l =v.size
        val res = FloatArray(l)
        for(i in 0..<l){
            res[i]=0f
            for(j in 0..<l){
                res[i]+=m[j*nRows+i]*v[j]
            }
        }
        return res
    }

    fun angle(a:FloatArray,b: FloatArray):Float{
        val d = dot(a,b)
        val na = norm(a)
        val nb = norm(b)
        if(na!=0f && nb!=0f){
            val cosAngle = d/(na*nb)
            return (acos(cosAngle) - Math.PI.toFloat()/2f)*180f/Math.PI.toFloat()
        }
        return 0f
    }

    fun dot(a:FloatArray,b:FloatArray):Float{
        var d = 0f
        for(i in a.indices){
            d=a[i]*b[i]
        }
        return d
    }
}