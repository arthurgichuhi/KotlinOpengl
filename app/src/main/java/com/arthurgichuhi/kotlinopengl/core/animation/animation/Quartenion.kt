package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.util.Log
import kotlin.math.sqrt

class Quartenion(var x: Float =0f, var y:Float, var z:Float, var w:Float) {
    init {
        normalize()
    }

    fun normalize(){
        val mag = sqrt(w * w + x * x + y * y + z * z)
        w /= mag
        x /= mag
        y /= mag
        z /= mag
    }

    /**
     * Converts the quaternion to a 4x4 matrix representing the exact same
     * rotation as this quaternion. (The rotation is only contained in the
     * top-left 3x3 part, but a 4x4 matrix is returned here for convenience
     * seeing as it will be multiplied with other 4x4 matrices).
     *
     * More detailed explanation here:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
     *
     * @return The rotation matrix which represents the exact same rotation as
     *         this quaternion.
     */

    fun toRotationMatrix():FloatArray{
        val matrix = FloatArray(16)
        val xy = x * y
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

    companion object{
        /**
         * Extracts the rotation part of a transformation matrix and converts it to
         * a quaternion using the magic of maths.
         *
         * More detailed explanation here:
         * http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm
         *
         * @param matrix
         *            - the transformation matrix containing the rotation which this
         *            quaternion shall represent.
         */
        fun fromMatrix(matrix:FloatArray):Quartenion{
            val w: Float
            val x: Float
            val y: Float
            val z: Float

            val diagonal = matrix[0] + matrix[5] + matrix[10]
            if (diagonal > 0) {
                val w4 = (sqrt((diagonal + 1f).toDouble()) * 2f).toFloat()
                w = w4 / 4f
                x = (matrix[6] - matrix[9]) / w4
                y = (matrix[8] - matrix[2]) / w4
                z = (matrix[1] - matrix[4]) / w4
            } else if ((matrix[0]> matrix[5]) && (matrix[0] > matrix[10])) {
                val x4 = sqrt(1f + matrix[0] - matrix[5] - matrix[10]) * 2f
                w = (matrix[6] - matrix[9]) / x4
                x = x4 / 4f
                y = (matrix[4] + matrix[1]) / x4
                z = (matrix[8] + matrix[2]) / x4
            } else if (matrix[5] > matrix[10]) {
                val y4 = sqrt(1f + matrix[5] - matrix[0] - matrix[10]) * 2f
                w = (matrix[8] - matrix[2]) / y4
                x = (matrix[4] + matrix[1]) / y4
                y = y4 / 4f
                z = (matrix[9] + matrix[6]) / y4
            } else {
                val z4 = sqrt(1f + matrix[10] - matrix[0] - matrix[5]) * 2f
                w = (matrix[1] - matrix[4]) / z4
                x = (matrix[8] + matrix[2]) / z4
                y = (matrix[9] + matrix[6]) / z4
                z = z4 / 4f
            }
            return Quartenion(x,y,z,w)
        }

        /**
         * Interpolates between two quaternion rotations and returns the resulting
         * quaternion rotation. The interpolation method here is "nlerp", or
         * "normalized-lerp". Another mnethod that could be used is "slerp", and you
         * can see a comparison of the methods here:
         * https://keithmaggio.wordpress.com/2011/02/15/math-magician-lerp-slerp-and-nlerp/
         *
         * and here:
         * http://number-none.com/product/Understanding%20Slerp,%20Then%20Not%20Using%20It/
         *
         * @param a
         * @param b
         * @param blend
         *            - a value between 0 and 1 indicating how far to interpolate
         *            between the two quaternions.
         * @return The resulting interpolated rotation in quaternion format.
         */

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
}