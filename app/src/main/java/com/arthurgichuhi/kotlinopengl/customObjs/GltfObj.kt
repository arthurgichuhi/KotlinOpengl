package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Bone
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Joint
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animation
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animator
import com.arthurgichuhi.kotlinopengl.core.animation.animation.BoneTransform
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame2
import de.javagl.jgltf.model.AccessorFloatData
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.AnimationModel
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Quaternionf
import org.joml.Vector3f
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer


/**
 * Creates an object to be rendered by Opengl from GLTF file format data.
 * @param model Data read from a gltf file
 */

class GltfObj(val model:GltfModel,path:String):AObject() {
    private lateinit var program: Program
    private lateinit var buffer : VertexBuffer
    private lateinit var tex : Texture

    private val locs: MutableMap<String,Int> = HashMap()
    private val texPath = path

    private val primitives = model.meshModels[0].meshPrimitiveModels[0]
    private val skin = model.skinModels

    private val noVertices = primitives.indices.count
    private val animation = processAnimation(model.animationModels[0])
    private val animator = Animator(this)

    private val boneMatrices : Array<FloatArray> = Array(skin[0].joints.size){FloatArray(16)}

    val bones: MutableMap<NodeModel,Bone> = HashMap()

    init {
        createBones()
    }

    override fun onInit() {

        buffer = VertexBuffer()
        program = mScene.loadProgram("armateur")

        locs["position"] = program.getAttribLoc("position")
        locs["tex"] = program.getAttribLoc("tex")
        locs["normal"] = program.getAttribLoc("normal")
        locs["jointIndices"] =  program.getAttribLoc("jointIndices")
        locs["weights"] = program.getAttribLoc("weights")

        buffer.loadGltfIndices(primitives,true)
        buffer.loadGltfFloats(primitives,locs,loadTex = {tex=mScene.loadTexture(texPath)},true)
        buffer.loadGltfInt(primitives,locs,true)

        program.use()
        animator.doAnimation(animation)
    }

    override fun destroy() {

    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        animator.update()
        addJointsToArray(bones)

        program.use()
        buffer.bind()
        tex.bindTexture()

        for (i in boneMatrices.indices){
            program.setUniformMat("jointTransforms[$i]",boneMatrices[i])
        }

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawElements(noVertices)
    }

    fun createJoints(){
        val jointData = Joint(0,skin[0].joints[0].name,skin[0].joints[0].computeLocalTransform(FloatArray(16)))

        for(i in skin[0].joints.indices){
            if(i != 0){
                val data = skin[0].joints[i]
                jointData.addChild(Joint(i,data.name,data.computeLocalTransform(FloatArray(16))))
            }
        }
    }

    private fun createBones(){
        for(joints in skin[0].joints){
            bones[joints]= Bone(
                    node = joints,
                    localTransform = joints.computeLocalTransform(FloatArray(16)),
                    animatedTransform = FloatArray(16)
            )

        }
    }

    private fun processAnimation(animation: AnimationModel):Animation{
        val nodeKeyFrames : MutableList<KeyFrame2> = ArrayList()
        for(channel in animation.channels){
            val node = channel.nodeModel
            val path = channel.path // "translation", "rotation", or "scale"
            val sampler = channel.sampler

            val times = getFloatData(sampler.input)
            val values = getFloatData(sampler.output)

            for(i in 0 ..<sampler.input.count){
                val time = times.get(i)
                val keyFrame = nodeKeyFrames.findOrCreate(time,node)
                if(!keyFrame.boneTransforms.containsKey(node)){
                    keyFrame.boneTransforms [node] = BoneTransform()
                }
                when(path){
                    "translation" -> {
                        val translation = Vector3f(
                            values.get(i * 3),
                            values.get(i * 3 + 1),
                            values.get(i * 3 + 2)
                        )
                        keyFrame.boneTransforms[node]!!.translation = translation
                    }

                    "rotation" -> {
                        val rotation = Quaternionf(
                            values.get(i * 4),
                            values.get(i * 4 + 1),
                            values.get(i * 4 + 2),
                            values.get(i * 4 + 3)
                        )
                        keyFrame.boneTransforms[node]!!.rotation = rotation
                    }

                    "scale" -> {
                        val scale = Vector3f(
                            values.get(i * 3),
                            values.get(i * 3 + 1),
                            values.get(i * 3 + 2)
                        )
                        keyFrame.boneTransforms[node]!!.scale = scale
                    }
                }
            }
        }

        return Animation(nodeKeyFrames.last().time,nodeKeyFrames)
    }

    private fun getFloatData(accessor: AccessorModel): AccessorFloatData {
        val data = accessor.accessorData
        require(data is AccessorFloatData) { "Expected float data in accessor!" }
        return data
    }

    private fun MutableList<KeyFrame2>.findOrCreate(time: Float,node: NodeModel): KeyFrame2 {
        return find { it.time == time } ?: KeyFrame2(time, boneTransforms = hashMapOf(node to BoneTransform())).also { add(it) }
    }

    private fun addJointsToArray(bones:Map<NodeModel,Bone>){
        Log.d("TAG","AJT ${boneMatrices.size}")
        for(child in bones){
            boneMatrices[skin[0].joints.indexOf(child.key)] = child.value.animatedTransform
        }
    }
}

private fun readAccessorAsFloatBuffer(accessor: AccessorModel): FloatBuffer {
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

private fun readAccessorAsIntBuffer(accessor: AccessorModel): IntBuffer {

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

private fun readAccessorAsShortBuffer(accessor: AccessorModel): ShortBuffer {

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