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
import com.arthurgichuhi.kotlinopengl.core.animation.animation.BoneTransform
import com.arthurgichuhi.kotlinopengl.core.animation.animation.KeyFrame2
import com.arthurgichuhi.kotlinopengl.io_Operations.TouchTracker
import com.arthurgichuhi.kotlinopengl.models.ModelInputs
import de.javagl.jgltf.model.AccessorFloatData
import de.javagl.jgltf.model.AccessorModel
import de.javagl.jgltf.model.AnimationModel
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f


/**
 * Creates an object to be rendered by Opengl from GLTF file format data.
 * @param model Data read from a gltf file
 */

class GltfObj(val model:GltfModel,val modelInputs: ModelInputs,path:String):AObject() {
    private lateinit var program: Program
    private lateinit var buffer : VertexBuffer
    private lateinit var tex : Texture
    private var buffers : Array<VertexBuffer>? = null
    private var bound:Boolean = false

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
        if(model.meshModels.size>1){
            buffers = Array(model.meshModels.size){VertexBuffer()}
        }
        if(buffers==null){
            buffer = VertexBuffer()
            buffer.loadGltfIndices(primitives,true)
            buffer.loadGltfFloats(primitives,modelInputs,loadTex = {tex=mScene.loadTexture(texPath)},false)
            if(modelInputs.hasJointIndices)buffer.loadGltfInt(primitives,true)
        }
        else{
            model.meshModels.forEachIndexed{index,it->
                buffers!![index].loadGltfIndices(it.meshPrimitiveModels[0],true)
                buffers!![index].loadGltfFloats(it.meshPrimitiveModels[0],modelInputs, loadTex = {},true)
            }
        }

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
        if(buffers==null){
            buffer.bind()
        }
        if(modelInputs.hasTextures)tex.bindTexture()

        if(animation!=null){
            for (i in boneMatrices.indices){
                program.setUniformMat("jointTransforms[$i]",boneMatrices[i])
            }
        }

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        if(buffers==null){
            drawElements(noVertices)
        }
        else{
            model.meshModels.forEachIndexed{index,it->
                buffers!![index].bind()
                drawElements(it.meshPrimitiveModels[0].indices.count)
            }
        }
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