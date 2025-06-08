package com.arthurgichuhi.kotlinopengl.customObjs

import android.opengl.Matrix
import android.util.Log
import com.arthurgichuhi.kotlinopengl.controllers.JoystickController
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
import com.arthurgichuhi.kotlinopengl.utils.Utils
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Actor(
    val model: GltfModel, path: String,
) : AObject() {
    private lateinit var program: Program
    private lateinit var buffer: VertexBuffer
    private lateinit var tex: Texture

    private val locs: MutableMap<String, Int> = HashMap()
    private val texPath = path

    private val primitives = model.meshModels[0].meshPrimitiveModels[0]
    private val skin = model.skinModels
    private val noVertices = primitives.indices.count

    private lateinit var animation: Array<Animation>
    private val animator: Animator

    private val receiver: IReceiveInput = createReceiver()
    private val touches: Array<TouchTracker> = Array(2) {
        TouchTracker(
            0L, true,
            Vector2f(), Vector2f(),
            side = it
        )
    }
    lateinit var controllers: Array<JoystickController>

    var middle = Pair(0f, 0f)
    private val speed = 20f
    private var lastFrameTime = 0f
    private var currentFrameTime = 0f
    private var delta = 0f

    val boneMatrices: Array<FloatArray> = Array(skin[0].joints.size) { FloatArray(16) }
    val bones: MutableMap<NodeModel, Bone> = HashMap()

    init {
        createBones()
        animator = Animator(model, bones)
        animation = Array(model.animationModels.size){
            animator.processAnimation(model.animationModels[it])
        }

    }

    override fun onInit() {
        mScene.updateReceivers(receiver)
        buffer = VertexBuffer()
        program = mScene.loadProgram("armateur")

        locs["position"] = program.getAttribLoc("position")
        locs["tex"] = program.getAttribLoc("tex")
        locs["normal"] = program.getAttribLoc("normal")
        locs["jointIndices"] = program.getAttribLoc("jointIndices")
        locs["weights"] = program.getAttribLoc("weights")

        buffer.loadGltfIndices(primitives, false)
        buffer.loadGltfFloats(
            primitives,
            locs,
            loadTex = { tex = mScene.loadTexture(texPath) },
            false
        )
        buffer.loadGltfInt(primitives, locs, false)

        program.use()
        animator.doAnimation(animation.first())
        middle = Pair(mScene.width, mScene.height)
    }

    override fun destroy() {
        this.destroy()
    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {

        currentFrameTime = Utils.getCurrentTime()
        delta = currentFrameTime - lastFrameTime
        lastFrameTime = currentFrameTime

        if (!touches[1].released) {
            val direction = Vector2f(
                touches[1].currentPosition.x - touches[1].startPosition.x,
                touches[1].currentPosition.y - touches[1].startPosition.y
            )

            // Normalize to get consistent speed in all directions
            if (direction.length() > 0) {
                direction.normalize()
            }

            // Apply movement (scale by speed and delta)
            val distance = speed * delta
            val movement = Vector3f(
                distance * direction.x,  // X-axis movement
                0f,                       // Y-axis (unused in this case)
                -distance * direction.y   // Z-axis movement (negate if needed)
            )

            // Translate model matrix directly
            Matrix.translateM(modelMat,0,movement.x,movement.y,movement.z)
        }

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
                    if (touches[value.side].id == value.id) {
                        touches[value.side].currentPosition = value.currentPosition
                    } else {
                        touches[value.side] = value
                    }
                    if (value.side == 0) rotateActor()
                }
            }
        }
    }

    /*
    This function is triggered every time the receivers are updates
     */
    fun rotateActor() {
        val tracker = touches[0]
        Log.d("TAG", "Pos ${middle.first}  ${tracker.startPosition.x}")
        if (tracker.side == 0) {
            Log.d("TAG", "Rotating")
            //Movement code
            if (tracker.currentPosition != tracker.startPosition) {
                //calculate rotation
                val angle =
                    Math.toDegrees(tracker.startPosition.angle(tracker.currentPosition).toDouble())
                        .toFloat()
                Matrix.rotateM(modelMat, 0, angle, 0f, 1f, 0f)
            }

        }
    }

    //init Joysticks
    fun initializeJoystickControllers(joysticks: Array<JoystickController>) {
        controllers = joysticks
    }

}