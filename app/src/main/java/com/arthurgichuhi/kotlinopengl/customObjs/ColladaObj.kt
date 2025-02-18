package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.utils.Utils

class ColladaObj(private val meshData: MeshData,private val texPath:String):AObject() {
    private var utils= Utils()
    private lateinit var program: Program
    private lateinit var mBuffer: VertexBuffer
    private lateinit var mTex: Texture


    override fun onInit() {
        program = mScene.loadProgram("armateur")
        mBuffer=VertexBuffer()
        mBuffer.loadIndexVertex(meshData,true)
        program.use()
        program.setFloat("position",utils.FloatsPerPosition,0,0)
        program.setFloat("tex",utils.FloatsPerTexture,0,0)
        mTex=mScene.loadTexture(texPath)
        program.setFloat("normal",utils.FloatsPerNormal,0,0)
        program.setInt("jointIndices",utils.FloatsPerJoint,0,0)
        program.setFloat("weights",utils.FloatsPerWeight,0,0)

    }

    override fun destroy() {
        if(::mBuffer.isInitialized){
            mBuffer.destroy()
        }
    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        mBuffer.bind()
        mTex.bindTexture()

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawElements(meshData.vertices.size)
    }
}