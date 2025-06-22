package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.IReceiveInput
import com.arthurgichuhi.kotlinopengl.core.InputMode
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Bone
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animation
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animator
import com.arthurgichuhi.kotlinopengl.io_Operations.TouchTracker
import com.arthurgichuhi.kotlinopengl.models.ModelInputs
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel

/**
 * Creates an object to be rendered by Opengl from GLTF file format data.
 * @param model Data read from a gltf file
 */

class GltfObj(val model:GltfModel,val modelInputs: ModelInputs,path:String):AObject() {
    private lateinit var program: Program
    private lateinit var buffer : VertexBuffer
    private lateinit var tex : Texture

    private val texPath = path

    private val primitives = model.meshModels[0].meshPrimitiveModels[0]
    private val skin = model.skinModels

    private val noVertices = primitives.indices.count
    private var animation : Animation? = null
    lateinit var animator : Animator

    var receiver:IReceiveInput

    private lateinit var boneMatrices : Array<FloatArray>

    val bones: MutableMap<NodeModel,Bone> = HashMap()

    init {
        receiver = createReceiver()
        if(modelInputs.hasJointIndices){
            boneMatrices = Array(skin[0].joints.size){FloatArray(16)}
            createBones()
            animator = Animator(model,bones)
            animation = Animator.processAnimation(model.animationModels[0])
        }
    }

    override fun onInit() {
        mScene.updateReceivers(receiver)
        program = mScene.loadProgram(if(modelInputs.hasTextures)"armateur" else "noTexture")

        buffer = VertexBuffer()
        buffer.loadGltfIndices(primitives, true)
        buffer.loadGltfFloats(
            primitives,
            modelInputs,
            loadTex = { tex = mScene.loadTexture(texPath) },
            false
        )
        if (modelInputs.hasJointIndices) buffer.loadGltfInt(primitives, true)

        program.use()
        if(animation!=null)animator.doAnimation(animation!!)
    }

    override fun destroy() {
        this.destroy()
    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        if(animation!=null){
            animator.update()
            addJointsToArray(bones)
        }

        program.use()
        buffer.bind()

        if(modelInputs.hasTextures)tex.bindTexture()

        if(animation!=null){
            for (i in boneMatrices.indices){
                program.setUniformMat("jointTransforms[$i]",boneMatrices[i])
            }
        }

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawElements(noVertices)
    }

    private fun createBones(){
        for(joints in skin[0].joints){
            bones[joints]= Bone(
                    node = joints,
                    animatedTransform = FloatArray(16)
            )
        }
    }

    private fun addJointsToArray(bones:Map<NodeModel,Bone>){
        for(child in bones){
            boneMatrices[skin[0].joints.indexOf(child.key)] = child.value.animatedTransform
        }
    }

    private fun createReceiver():IReceiveInput{
        return object : IReceiveInput {
            override fun scroll(mode: InputMode, xDist: Float, yDist: Float) {

            }

            override fun resetCamera() {

            }

            override fun touchTracker(value:TouchTracker) {

            }

        }
    }
}