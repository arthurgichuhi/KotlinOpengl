package com.arthurgichuhi.kotlinopengl.core.animation.animation

import kotlin.math.sqrt

class Quartenion(private var x: Float =0f,private var y:Float,private var z:Float,private var w:Float) {
    constructor():this(x = 0f, y = 0f, z = 0f, w = 0f,)
    init {
        normalize()
    }

    fun normalize(){
        val mag = sqrt((w*w+x*x+y*y+z*z).toDouble()).toFloat()
        w /= mag
        x /= mag
        y /= mag
        z /= mag
    }

    fun toRotationMatrix():FloatArray{
        val matrix = FloatArray(16)
        val xy = x*y
        val xz = x * z
        val xw = x * w
        val yz = y * z
        val yw = y * w
        val zw = z * w
        val xSquared = x * x
        val ySquared = y * y
        val zSquared = z * z
        matrix[0] = 1 - 2 * (ySquared + zSquared)
        matrix[4] = 2 * (xy - zw)
        matrix[8] = 2 * (xz + yw)
        matrix[12] = 0f
        matrix[1] = 2 * (xy + zw)
        matrix[5] = 1 - 2 * (xSquared + zSquared)
        matrix[9] = 2 * (yz - xw)
        matrix[13] = 0f
        matrix[2] = 2 * (xz - yw)
        matrix[6] = 2 * (yz + xw)
        matrix[10] = 1 - 2 * (xSquared + ySquared)
        matrix[14] = 0f
        matrix[3] = 0f
        matrix[7] = 0f
        matrix[11] = 0f
        matrix[15] = 1f
        return matrix
    }

    fun fromMatrix(matrix:FloatArray):Quartenion{
        val w1: Float
        val x1: Float
        val y1: Float
        val z1: Float

        val diagonal = matrix[0] + matrix[5] + matrix[10]
        if (diagonal > 0) {
            val w4 = (sqrt((diagonal + 1f).toDouble()) * 2f).toFloat()
            w1 = w4 / 4f
            x1 = (matrix[6] - matrix[9]) / w4
            y1 = (matrix[8] - matrix[2]) / w4
            z1 = (matrix[1] - matrix[4]) / w4
        } else if ((matrix[0]> matrix[5]) && (matrix[0] > matrix[10])) {
            val x4 = sqrt(1f + matrix[0] - matrix[5] - matrix[10]) * 2f
            w1 = (matrix[6] - matrix[9]) / x4
            x1 = x4 / 4f
            y1 = (matrix[4] + matrix[1]) / x4
            z1 = (matrix[8] + matrix[2]) / x4
        } else if (matrix[5] > matrix[10]) {
            val y4 = sqrt(1f + matrix[5] - matrix[0] - matrix[10]) * 2f
            w1 = (matrix[8] - matrix[2]) / y4
            x1 = (matrix[4] + matrix[1]) / y4
            y1 = y4 / 4f
            z1 = (matrix[9] + matrix[6]) / y4
        } else {
            val z4 = sqrt(1f + matrix[10] - matrix[0] - matrix[5]) * 2f
            w1 = (matrix[1] - matrix[4]) / z4
            x1 = (matrix[8] + matrix[2]) / z4
            y1 = (matrix[9] + matrix[6]) / z4
            z1 = z4 / 4f
        }
        return Quartenion(x1,y1,z1,w1)
    }

    fun interpolate(a:Quartenion,b:Quartenion,blend:Float):Quartenion{
        val result = Quartenion(0f,0f,0f,1f)
        val dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z
        val blendI = 1f - blend
        if (dot < 0) {
            result.w = blendI * a.w + blend * -b.w
            result.x = blendI * a.x + blend * -b.x
            result.y = blendI * a.y + blend * -b.y
            result.z = blendI * a.z + blend * -b.z
        } else {
            result.w = blendI * a.w + blend * b.w
            result.x = blendI * a.x + blend * b.x
            result.y = blendI * a.y + blend * b.y
            result.z = blendI * a.z + blend * b.z
        }
        result.normalize()
        return result
    }

}