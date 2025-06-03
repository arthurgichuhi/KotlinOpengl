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
import de.javagl.jgltf.model.GltfModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Vector3f

class Actor(val model: GltfModel, path:String):AObject() {
    private lateinit var program: Program
    private lateinit var buffer : VertexBuffer
    private lateinit var tex : Texture

    private val locs: MutableMap<String,Int> = HashMap()
    private val texPath = path

    private val primitives = model.meshModels[0].meshPrimitiveModels[0]
    private val skin = model.skinModels
    private val noVertices = primitives.indices.count

    private var animation : Animation
    private val animator : Animator

    private val receiver : IReceiveInput =createReceiver()
    private val touches:MutableMap<Long,TouchTracker> = HashMap()

    val boneMatrices : Array<FloatArray> = Array(skin[0].joints.size){FloatArray(16)}
    val bones: MutableMap<NodeModel, Bone> = HashMap()

    init {
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
            override fun scroll(mode: InputMode, xDist: Float, yDist: Float) {}

            override fun resetCamera() {}

            override fun touchTracker(value:TouchTracker) {
                if(touches.containsKey(value.id)){
                    if(value.released){
                        touches.remove(value.id)
                    }
                    else{
                        touches[value.id]!!.currentPosition = value.currentPosition
                        touches[value.id]!!.updated = true
                        receiverConsumer(touches[value.id]!!)
                    }
                }
                else{
                    touches[value.id] = value
                }
            }

        }
    }
    /*
    This function is triggered every time the receivers are updates
     */
    fun receiverConsumer(value: TouchTracker){
        val middle = Pair(mScene.width,mScene.height)
        val tracker = touches[value.id]!!
        if(tracker.startPosition.x<middle.first){
            //Movement code
            if(tracker.currentPosition!= tracker.startPosition){
                //calculate rotation
                val angle = tracker.startPosition.angle(tracker.currentPosition)
                val modelMatrix = Matrix4f().set(modelMat)
                val trans = modelMatrix.getTranslation(Vector3f())
                trans.z = 0f
                modelMatrix.rotate(angle,Vector3f(floatArrayOf(0f,1f,0f)))
                    .get(modelMat)

            }

        }
        else{

        }
    }
}