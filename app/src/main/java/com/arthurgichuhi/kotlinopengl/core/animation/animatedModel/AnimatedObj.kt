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
import de.javagl.jgltf.model.SkinModel

class AnimatedObj(
    private val mesh: MeshData,
    skeletonData: SkeletonData,
    val animation: Animation,
    texPath:String
    ):AObject() {
        
    private lateinit var program: Program
    private lateinit var mBuffer: VertexBuffer
    private var nVertices:Int=0
    private var mTexPath=texPath
    private lateinit var mTex: Texture
    private val locs: MutableMap<String,Int> = HashMap()

    var rootJoint: Joint = createJoints(skeletonData.headJoint)
    private val jointTransforms : MutableList<FloatArray> = ArrayList(skeletonData.jointCount)
    //private val animator = Animator(this)
    private val rootMat = FloatArray(16)

    init {
        MathUtils.scale(mesh.vertices, .1f)
        nVertices = mesh.indices.size
        MathUtils.setIdentity4Matrix(rootMat)
    }

    override fun onInit() {
        Log.d("TAG","ROOT\n${rootJoint.name}\n${rootJoint.localTransform.toList()}")
        mBuffer = VertexBuffer()
        //rootJoint.calcInverseBindTransform(animation.inverseTrans)
        program = mScene.loadProgram("armateur")

        locs["position"] = program.getAttribLoc("position")
        locs["tex"] = program.getAttribLoc("tex")
        locs["normal"] = program.getAttribLoc("normal")
        locs["jointIndices"] =  program.getAttribLoc("jointIndices")
        locs["weights"] = program.getAttribLoc("weights")

        mBuffer.loadIndicesBuffer(mesh.indices,true)
        mBuffer.loadFloatVertexData(mesh,locs,true, loadTex = {mTex=mScene.loadTexture(mTexPath)})
        mBuffer.loadIntVertexData(mesh,locs,true)
        mBuffer.checkGlError("ANIMATED ONJ ERROR")

        program.use()
        //animator.doAnimation(animation)
    }

    override fun destroy() {

    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        //animator.update()
        addJointsToArray(rootJoint,jointTransforms)

        program.use()
        mBuffer.bind()
        mTex.bindTexture()
        val transforms = jointTransforms.toList()
        for(i in transforms.indices){
            program.setUniformMat("jointTransforms[${transforms[i]}]",transforms[i])
        }

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawElements(nVertices)
        jointTransforms.clear()
    }

    private fun createJoints(data:JointData):Joint{
        val joint = Joint(data.index,data.nameId,data.localTransform)
        for(child in data.children){
            joint.addChild(createJoints(child))
        }
        return joint
    }



    private fun addJointsToArray(headJoint: Joint,jointMatrices:MutableList<FloatArray>){
        Log.d("TAG","AJT\n${headJoint.name}:${headJoint.index}:${headJoint.animatedTransform.toList()}")
        jointMatrices.add(headJoint.index,headJoint.animatedTransform)
        for(child in headJoint.children){
            addJointsToArray(child,jointMatrices)
        }
        Log.d("TAG","Count\n${headJoint.name}=${headJoint.index}")
    }
}