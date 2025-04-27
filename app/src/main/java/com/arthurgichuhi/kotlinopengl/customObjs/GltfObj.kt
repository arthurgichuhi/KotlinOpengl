package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Joint
import com.arthurgichuhi.kotlinopengl.core.animation.animation.BoneTransform
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame2
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.Bone
import de.javagl.jgltf.model.AccessorFloatData
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.AnimationModel
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import de.javagl.jgltf.model.SkinModel
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.nio.FloatBuffer


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

    private val nodeKeyFrames =  mutableMapOf<String,KeyFrame>()

    override fun onInit() {
        extractAnimations()
        val nodeModel:NodeModel
        model.skinModels[0].joints.indexOf(skin[0].joints[0])
        //Log.d("TAG","Animations:${model.animationModels.size}")
        processAnimation(model.animationModels[0])
//        Log.d("TAG","SKIN\n${skin[0].joints[1].name}")
//        Log.d("TAG","SKIN\n${skin[0].joints[0].children[0].matrix.toList()}")
//        Log.d("TAG","SKIN\n${skin[0]}")
        buffer = VertexBuffer()
        program = mScene.loadProgram("armateur")

        locs["position"] = program.getAttribLoc("position")
        locs["tex"] = program.getAttribLoc("tex")
        locs["normal"] = program.getAttribLoc("normal")
        locs["jointIndices"] =  program.getAttribLoc("jointIndices")
        locs["weights"] = program.getAttribLoc("weights")

        buffer.loadGltfIndices(primitives,false)
        buffer.loadGltfFloats(primitives,locs,loadTex = {tex=mScene.loadTexture(texPath)},false)
        buffer.loadGltfInt(primitives,locs,false)

        program.use()

    }

    override fun destroy() {

    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        buffer.bind()
        tex.bindTexture()

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

    // Build skeleton tree
    fun buildSkeleton(rootNode: NodeModel, skin: SkinModel): Bone {
        val rootBone = Bone(rootNode, rootNode.name)
        rootBone.inverseBindMatrix= Matrix4f(FloatBuffer.wrap(skin.getInverseBindMatrix(0,FloatArray(16)) ))// Assuming root is first

        fun addChildren(parentBone: Bone, parentNode: NodeModel) {
            parentNode.children.forEach { childNode ->
                val childBone = Bone(
                    node = childNode,
                    name = childNode.name,
                    inverseBindMatrix = findInverseBindMatrix(childNode, skin)
                )
                parentBone.children.add(childBone)
                addChildren(childBone, childNode)
            }
        }

        addChildren(rootBone, rootNode)
        return rootBone
    }

    // Helper to find inverse bind matrix for a joint
    fun findInverseBindMatrix(node: NodeModel, skin: SkinModel): Matrix4f {
        val index = skin.joints.indexOf(node)
        return if (index >= 0) Matrix4f(FloatBuffer.wrap(skin.getInverseBindMatrix(index,FloatArray(16)))) else Matrix4f()
    }

    private fun extractAnimations(){
        Log.d("TAG","Animations:${model.animationModels[0].channels[0]}")
        var count = 0
        for(animation in model.animationModels){
            for(channel in animation.channels){
                val targetNode = channel.nodeModel
                val path = channel.path

                val sampler =  channel.sampler

                val input = sampler.input
                val outPut = sampler.output
                count++
                Log.d("TAG","$count Target\n${targetNode.name}\nProperty\n${path}")
            }
        }
    }

    private fun processAnimation(animation: AnimationModel):MutableList<KeyFrame2>{
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
                Log.d("TAG","PAT\n${node.name}\n${keyFrame.boneTransforms.size}")
                when(path){
                    "translation" -> {
                        val translation = Vector3f(
                            values.get(i * 3),
                            values.get(i * 3 + 1),
                            values.get(i * 3 + 2)
                        )
                        keyFrame.boneTransforms[node]!!.translation = Matrix4f().translate(translation)
                    }
                    "rotation" -> {
                        val rotation = Quaternionf(
                            values.get(i * 4),
                            values.get(i * 4 + 1),
                            values.get(i * 4 + 2),
                            values.get(i * 4 + 3)
                        )
                        keyFrame.boneTransforms[node]!!.rotation = Matrix4f().rotate(rotation)
                    }

                    "scale" -> {
                        val scale = Vector3f(
                            values.get(i * 3),
                            values.get(i * 3 + 1),
                            values.get(i * 3 + 2)
                        )
                        keyFrame.boneTransforms[node]!!.scale = Matrix4f().scale(scale)
                    }
                }
            }
        }
        Log.d("TAG","PA\n${nodeKeyFrames[0].boneTransforms.toList()[2].first.name}")
        return nodeKeyFrames
    }

    private fun getFloatData(accessor: AccessorModel): AccessorFloatData {
        val data = accessor.accessorData
        require(data is AccessorFloatData) { "Expected float data in accessor!" }
        return data
    }

    private fun MutableList<KeyFrame2>.findOrCreate(time: Float,node: NodeModel): KeyFrame2 {
        return find { it.time == time } ?: KeyFrame2(time, boneTransforms = hashMapOf(node to BoneTransform())).also { add(it) }
    }
//
//    fun interpolateKeyframes(a: KeyFrame, b: KeyFrame, alpha: Float): KeyFrame {
//        val result = KeyFrame()
//        result.timeStamp = a.timeStamp + (b.timeStamp - a.timeStamp) * alpha
//
//        // Interpolate translation
//        a.translation.lerp(b.translation, alpha, result.translation)
//
//        // Interpolate rotation (SLERP)
//        a.rotation.slerp(b.rotation, alpha, result.rotation)
//
//        // Interpolate scale
//        a.scale.lerp(b.scale, alpha, result.scale)
//
//        return result
//    }



    fun applyKeyFrames(){

    }
}