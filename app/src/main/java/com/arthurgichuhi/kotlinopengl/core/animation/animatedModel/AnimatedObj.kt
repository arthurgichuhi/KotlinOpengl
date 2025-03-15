package com.arthurgichuhi.kotlinopengl.core.animation.animatedModel

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animation
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animator
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.JointData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.SkeletonData
import com.arthurgichuhi.kotlinopengl.utils.MathUtils
import com.arthurgichuhi.kotlinopengl.utils.Utils

class AnimatedObj(
    val mesh: MeshData,
    skeletonData: SkeletonData,
    val animation: Animation,
    texPath:String
    ):AObject() {

    private val utils= Utils()
    private val mathUtils = MathUtils()
    private lateinit var program: Program
    private lateinit var mBuffer: VertexBuffer
    private var nVertices:Int=0
    private var mTexPath=texPath
    private lateinit var mTex: Texture
    private val locs: MutableMap<String,Int> = HashMap()

    var rootJoint: Joint = createJoints(skeletonData.headJoint)
    private val jointTransforms : MutableList<FloatArray> = ArrayList(50)
    private val animator = Animator(this)
    private val rootMat = FloatArray(16)

    init {
        nVertices=mesh.indices.size
        mathUtils.setIdentity4Matrix(rootMat)
        rootJoint.children.forEach {
            Log.d("TAG","AO:${rootJoint.name}-Children${it.name},${it.children.size}")
        }

    }

    override fun onInit() {
        mBuffer = VertexBuffer()
        rootJoint.calcInverseBindTransform(rootMat)
        program = mScene.loadProgram("armateur")

        locs["position"] = program.getAttribLoc("position")
        locs["tex"] = program.getAttribLoc("tex")
        locs["normal"] = program.getAttribLoc("normal")
        locs["jointIndices"] =  program.getAttribLoc("jointIndices")
        locs["weights"] = program.getAttribLoc("weights")

        mBuffer.loadIndicesBuffer(mesh.indices,true)
        mBuffer.loadFloatVertexData(mesh,locs,true, loadTex = {mTex=mScene.loadTexture(mTexPath)})
        mBuffer.loadIntVertexData(mesh,locs,true)

        program.use()
        Log.d("TAG","Animation:${animation.keyFrames.size}")
        animator.doAnimation(animation)
    }

    override fun destroy() {

    }

    override fun onUpdate(time: Long) {
        animator.update().also {
            addJointsToArray(rootJoint,jointTransforms)
        }
    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {

        program.use()
        mBuffer.bind()
        mTex.bindTexture()

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        jointTransforms.forEachIndexed { index,it-> program.setUniformMat("jointTransforms[$index])",it)}


        drawElements(nVertices)

    }

    fun createJoints(data:JointData):Joint{
        val joint = Joint(data.index,data.nameId,data.localTransform)
        for(child in data.children){
            joint.children.add(createJoints(child))
        }
        return joint
    }

    private fun addJointsToArray(headJoint: Joint,jointMatrices:MutableList<FloatArray>){
        jointMatrices.add(headJoint.index,headJoint.animatedTransform)
        for(child in headJoint.children){
            addJointsToArray(child,jointMatrices)
        }
    }
}