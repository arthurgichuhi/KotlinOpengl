package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.utils.MathUtils

/**
 *
 * @param position The position of the joint relative to the parent joint
 *            (bone-space) at a certain keyframe. For example, if this joint
 *            is at (5, 12, 0) in the model's coordinate system, and the
 *            parent of this joint is at (2, 8, 0), then the position of
 *            this joint relative to the parent is (3, 4, 0).
 * @param rotation The rotation of the joint relative to the parent joint
 *            (bone-space) at a certain keyframe.
 */

class JointTransform(pos: Vec3f, rot: Quartenion) {
    private var position: Vec3f = pos
    private var rotation:Quartenion = rot
    /**
     * In this method the bone-space transform matrix is constructed by
     * translating an identity matrix using the position variable and then
     * applying the rotation. The rotation is applied by first converting the
     * quaternion into a rotation matrix, which is then multiplied with the
     * transform matrix.
     *
     * @return This bone-space joint transform as a matrix. The exact same
     *         transform as represented by the position and rotation in this
     *         instance, just in matrix form.
     */
    fun getLocalTransform():FloatArray{
        val matrix = FloatArray(16)
        MathUtils.setIdentity4Matrix(matrix)
        Matrix.multiplyMM(matrix,0,rotation.toRotationMatrix(),0,matrix,0)
        Matrix.translateM(matrix,0,position.x,position.y,position.z)
        return matrix
    }

    companion object{
        fun interpolate(
            frameA: JointTransform,
            frameB: JointTransform,
            progression: Float
        ): JointTransform {
            val pos: Vec3f = interpolateVec3(frameA.position, frameB.position, progression)
            val rot =Quartenion.interpolate(frameA.rotation, frameB.rotation, progression)
            return JointTransform(pos, rot)
        }

        private fun interpolateVec3(start: Vec3f, end: Vec3f, progression: Float): Vec3f {
            val x: Float = start.x + (end.x - start.x) * progression
            val y: Float = start.y + (end.y - start.y) * progression
            val z: Float = start.z + (end.z - start.z) * progression
            return Vec3f(x, y, z)
        }
    }
}