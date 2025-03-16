package com.arthurgichuhi.kotlinopengl.core.animation.animation

import android.opengl.Matrix
import com.arthurgichuhi.aopengl.models.Vec3f


class JointTransform(pos: Vec3f, rot: Quartenion) {
    private var position:Vec3f = pos
    private var rotation:Quartenion = rot

    fun getLocalTransform():FloatArray{
        val matrix = FloatArray(16)
        Matrix.translateM(matrix,0,position.x,position.y,position.z)
        Matrix.multiplyMM(matrix,0,matrix,0,rotation.toRotationMatrix(),0)
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