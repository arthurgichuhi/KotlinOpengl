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
import com.arthurgichuhi.kotlinopengl.io_Operations.TouchTracker
import com.arthurgichuhi.kotlinopengl.models.ModelInputs
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Vector2f
import org.joml.Vector3f

class ActorNPC(val model: GltfModel, path: String,):AObject() {

    private lateinit var program: Program
    private lateinit var buffer: VertexBuffer
    private lateinit var tex: Texture

    private val modelInputs = ModelInputs(true,true,false,true)
    private val texPath = path

    private val primitives = model.meshModels[0].meshPrimitiveModels[0]
    private val skin = model.skinModels
    private val noVertices = primitives.indices.count

    private var animation: Array<Animation>
    private var animator: Animator

    private val receiver: IReceiveInput = createReceiver()
    private val touches: Array<TouchTracker> = Array(2) {
        TouchTracker(
            0L, true,
            Vector2f(), Vector2f(),
            side = it
        )
    }

    private val speed = 20f
    private var lastFrameTime = 0f
    private var currentFrameTime = 0f
    private var delta = 0f

    private val destination = Vector3f()
    private val current = Vector3f()

    val boneMatrices: Array<FloatArray> = Array(skin[0].joints.size) { FloatArray(16) }
    val bones: MutableMap<NodeModel, Bone> = HashMap()

    init {
        createBones()
        animator = Animator(model, bones)
        animation = Array(model.animationModels.size){
            Animator.processAnimation(model.animationModels[it])
        }

    }

    override fun onInit() {

        buffer = VertexBuffer()
        program = mScene.loadProgram("armateur")

        buffer.loadGltfIndices(primitives, false)
        buffer.loadGltfFloats(
            primitives,
            modelInputs,
            loadTex = { tex = mScene.loadTexture(texPath) },
            false
        )
        buffer.loadGltfInt(primitives, false)

        program.use()
        animator.doAnimation(animation.last())
    }

    override fun destroy() {

    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {

        currentFrameTime = Utils.getCurrentTime()
        delta = currentFrameTime - lastFrameTime
        lastFrameTime = currentFrameTime

        animator.update()
        addJointsToArray(bones)

        program.use()
        buffer.bind()
        tex.bindTexture()

        for (i in boneMatrices.indices) {
            program.setUniformMat("jointTransforms[$i]", boneMatrices[i])
        }

        program.setUniformMat("model", modelMat)
        program.setUniformMat("view", viewMat)
        program.setUniformMat("projection", projectionMat)

        drawElements(noVertices)

    }

    private fun createBones() {
        for (joints in skin[0].joints) {
            bones[joints] = Bone(
                node = joints,
                animatedTransform = FloatArray(16)
            )
        }
    }

    private fun addJointsToArray(bones: Map<NodeModel, Bone>) {
        for (child in bones) {
            boneMatrices[skin[0].joints.indexOf(child.key)] = child.value.animatedTransform
        }
    }

    private fun createReceiver(): IReceiveInput {
        return object : IReceiveInput {
            override fun scroll(mode: InputMode, xDist: Float, yDist: Float) {}

            override fun resetCamera() {}

            override fun touchTracker(value: TouchTracker) {
                if (value.released) {
                    touches[value.side].released = true
                } else {

                }
            }
        }
    }
}