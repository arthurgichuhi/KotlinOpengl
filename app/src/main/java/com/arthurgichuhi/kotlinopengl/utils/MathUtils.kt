package com.arthurgichuhi.kotlinopengl.utils


class MathUtils {
    fun scaleMatColum(mat:FloatArray,colIdx:Int,nColumns:Int,scale:Float){
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