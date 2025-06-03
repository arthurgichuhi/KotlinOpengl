package com.arthurgichuhi.kotlinopengl.customObjs

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

class GltfObj(val model:GltfModel,path:String):AObject() {
    private lateinit var program: Program
    private lateinit var buffer : VertexBuffer
    private lateinit var tex : Texture

    private val locs: MutableMap<String,Int> = HashMap()
    private val texPath = path

    private val primitives = model.meshModels[0].meshPrimitiveModels[0]
    private val skin = model.skinModels

    private val noVertices = primitives.indices.count
    private var animation : Animation
    var animator : Animator

    var receiver:IReceiveInput

    private val boneMatrices : Array<FloatArray> = Array(skin[0].joints.size){FloatArray(16)}

    val bones: MutableMap<NodeModel,Bone> = HashMap()

    init {
        receiver = createReceiver()
        createBones()
        animator = Animator(model,bones)
        animation = animator.processAnimation(model.animationModels[0])
    }

    override fun onInit() {
        mScene.updateReceivers(receiver)
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
        animator.doAnimation(animation)
    }

    override fun destroy() {
        this.destroy()
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