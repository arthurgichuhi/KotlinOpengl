package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.utils.Utils

/**
 * This class is similar to the PCTNObj
 * class with the only difference is its configured to use an indices buffer to render
 * I - Indices
 * P - Position
 * C - Color
 * T - Texture
 * N - normals
 */

class IPCTN(data:MeshData,hasColor:Boolean,
            hasNormal:Boolean,
            hasTex:Boolean, texPath:String
): AObject() {
    private lateinit var program: Program
    private lateinit var mColor: Vec3f
    private var mesh:MeshData = data
    private lateinit var mBuffer: VertexBuffer
    private var nVertices:Int=0
    private var stride:Int= Utils.FloatsPerPosition
    private var mHasColor=hasColor
    private var mHasTex=hasTex
    private var mTexPath=texPath
    private var mHasNormal = hasNormal
    private lateinit var mTex: Texture
    private val locs: MutableMap<String,Int> = HashMap()

    init {
        mHasTex=hasTex
        mTexPath=texPath
        stride+=if(hasColor)Utils.FloatsPerColor else 0
        stride+=if(hasTex)Utils.FloatsPerTexture else 0
        stride+=if(hasNormal)Utils.FloatsPerNormal else 0
        mHasColor=hasColor
        nVertices=data.indices.size
    }

    override fun onInit() {
        program=mScene.loadProgram("allShader2")
        mBuffer= VertexBuffer()
        mBuffer.loadIndicesBuffer(mesh.indices,true)
        locs["position"] = program.getAttribLoc("position")
        locs["tex"] = program.getAttribLoc("tex")
        locs["normal"] = program.getAttribLoc("normal")
        mBuffer.loadFloatVertexData(mesh,locs,true, loadTex = {mTex=mScene.loadTexture(mTexPath)})
        program.use()

    }

    override fun destroy() {


    }

    override fun onUpdate(time: Long) {
    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        mBuffer.bind()
        if(mHasTex){
            mTex.bindTexture()
        }
        mBuffer.checkGlError("IPCTN-Draw")
        program.setUniformInt("hasColor",if(mHasColor)1 else 0)
        program.setUniformInt("hasTex",if(mHasTex)1 else 0)
        program.setUniformInt("hasNormal",if(mHasNormal)1 else 0)

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        val lightPos = Vec3f()

        program.setUniform3fv("light.position",lightPos.toArray())
        program.setUniform3fv("cameraPos",mScene.camera.defaultPos.toArray())

        program.setUniform3fv("light.ambient", Vec3f(1f,1f).toArray())
        program.setUniform3fv("light.diffuse", Vec3f(1f,1f).toArray())
        program.setUniform3fv("light.specular", Vec3f(1f,1f).toArray())

        program.setUniform3fv("material.ambient", Vec3f(.1f,.1f,.1f).toArray())
        program.setUniform3fv("material.diffuse", Vec3f(.7f,.7f,.7f).toArray())
        program.setUniform3fv("material.specular", Vec3f(1f,1f,1f).toArray())
        program.setUniformFloat("material.shininess",20f)

        drawElements(nVertices)
        mBuffer.checkGlError("IPCTN-Draw")
    }
}