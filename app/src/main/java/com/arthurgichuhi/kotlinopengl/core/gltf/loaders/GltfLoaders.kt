package com.arthurgichuhi.kotlinopengl.core.gltf.loaders

import android.animation.Keyframe
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animation
import com.arthurgichuhi.kotlinopengl.core.animation.animation.JointTransform
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.AnimationModel
import de.javagl.jgltf.model.GltfModel
import org.joml.Vector3f
import java.io.File
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

class GltfLoaders {

    companion object{
        fun loadAnimationsFromGltf(gltfModel: GltfModel): List<Animation> {
            val keyFrames = mutableListOf<KeyFrame>()
            val animations = mutableListOf<Animation>()

            gltfModel.animationModels?.forEach { gltfAnimation ->
                val keyframes = mutableListOf<KeyFrame>()

                // Assuming all channels have the same timeline (simplified)
                val inputAccessor = gltfAnimation.channels[0].sampler.input
                val times = readAccessorAsFloatBuffer(inputAccessor).array()  // Keyframe timestamps

                // For each timestamp, gather all joint transforms
                for (i in times.indices) {
                    val time = times[i]
                    val jointTransforms = mutableMapOf<String,JointTransform>()

                    gltfAnimation.channels.forEach { channel ->
                        val sampler = channel.sampler
                        val outputAccessor = sampler.output
                        val node = channel.nodeModel
                        val jointId = gltfModel.nodeModels.indexOf(node)  // Map node to joint ID


                    }

                    keyframes.add(KeyFrame(time, jointTransforms))
                }
            }

            return animations
        }


//          when (sampler.path) {
//                                      "translation" -> {
//                                          val translations = outputAccessor.floatArray
//                                          val translation = Vector3f(
//                                              translations[i * 3],
//                                              translations[i * 3 + 1],
//                                              translations[i * 3 + 2]
//                                          )
//                                          jointTransforms.add(
//                                              JointTransform(translation = Matrix4f().translate(translation))
//                                          )
//                                      }
//                                      "rotation" -> {
//                                          val rotations = outputAccessor.floatArray
//                                          val rotation = Quaternionf(
//                                              rotations[i * 4],
//                                              rotations[i * 4 + 1],
//                                              rotations[i * 4 + 2],
//                                              rotations[i * 4 + 3]
//                                          )
//                                          jointTransforms.add(
//                                              JointTransform(rotation = Matrix4f().rotate(rotation))
//                                      }
//                                      "scale" -> {
//                                          val scales = outputAccessor.floatArray
//                                          val scale = Vector3f(
//                                              scales[i * 3],
//                                              scales[i * 3 + 1],
//                                              scales[i * 3 + 2]
//                                          )
//                                          jointTransforms.add(
//                                              JointTransform(scale = Matrix4f().scale(scale))
//                                          )
//                                      }
//                                  }


        fun readAccessorAsFloatBuffer(accessor: AccessorModel): FloatBuffer {
            val bufferView = accessor.bufferViewModel
            val byteBuffer = bufferView.bufferViewData
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

            val byteLength = accessor.count * accessor.byteStride
            byteBuffer.position(0)
            byteBuffer.limit(byteLength)

            val directFloatBuffer = FloatBuffer.allocate(accessor.count * accessor.elementType.numComponents) // Direct allocation
            val sourceFloatBuffer = byteBuffer.asFloatBuffer()
            directFloatBuffer.put(sourceFloatBuffer)
            directFloatBuffer.rewind()

            return directFloatBuffer
        }

        fun readAccessorAsIntBuffer(accessor: AccessorModel): IntBuffer {

            val bufferView = accessor.bufferViewModel
            val byteBuffer = bufferView.bufferViewData
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

            val byteLength = accessor.count * accessor.byteStride
            byteBuffer.position(0)
            byteBuffer.limit(byteLength)

            val directIntBuffer = IntBuffer.allocate(accessor.count * accessor.elementType.numComponents) // Direct allocation
            val sourceIntBuffer = byteBuffer.asIntBuffer()
            directIntBuffer.put(sourceIntBuffer)
            directIntBuffer.rewind()
            return directIntBuffer
        }

        fun readAccessorAsShortBuffer(accessor: AccessorModel): ShortBuffer {

            val bufferView = accessor.bufferViewModel
            val byteBuffer = bufferView.bufferViewData
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

            val byteLength = accessor.count * accessor.byteStride // 2 bytes per UNSIGNED_SHORT
            byteBuffer.position(0)
            byteBuffer.limit(byteLength)

            val directBuffer = ShortBuffer.allocate(accessor.count * accessor.elementType.numComponents)
            directBuffer.put(byteBuffer.asShortBuffer())
            directBuffer.rewind()
            return directBuffer
        }
    }
}