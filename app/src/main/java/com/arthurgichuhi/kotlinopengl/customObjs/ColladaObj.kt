package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
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
        //mBuffer.loadSkinData(meshData,true, loadTex = {mTex=mScene.loadTexture(texPath)})
        //mBuffer.loadVertexData(meshData,,true, loadTex = {mTex=mScene.loadTexture(texPath)})
        program.use()

//        program.setFloat("pos",utils.FloatsPerPosition,3,0)
//        program.setFloat("tex",utils.FloatsPerTexture,2,3)
//        program.setFloat("norm",utils.FloatsPerNormal,3,5)
//        program.setInt("jointIndices",utils.FloatsPerJoint,3,0)
//        program.setFloat("weights",utils.FloatsPerWeight,3,0)
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
        mBuffer.checkGlError("DRAW1")
        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawElements(meshData.indices.size)
        mBuffer.checkGlError("Draw ELEMENTS")
    }
}