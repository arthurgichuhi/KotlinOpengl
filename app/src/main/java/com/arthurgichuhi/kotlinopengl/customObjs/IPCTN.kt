package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.collada.dataStructures.MeshData
import com.arthurgichuhi.kotlinopengl.utils.Utils

class IPCTN(data:Pair<FloatArray,MeshData>,hasColor:Boolean,
            hasNormal:Boolean,
            hasTex:Boolean, texPath:String
): AObject() {
    private var utils= Utils()
    private lateinit var program: Program
    private lateinit var mColor: Vec3
    private var mVertices:FloatArray = data.first
    private var mesh:MeshData = data.second
    private lateinit var mBuffer: VertexBuffer
    private var nVertices:Int=0
    private var stride:Int=utils.FloatsPerPosition
    private var mHasColor=hasColor
    private var mHasTex=hasTex
    private var mTexPath=texPath
    private var mHasNormal = hasNormal
    private lateinit var mTex: Texture

    init {
        mHasTex=hasTex
        mTexPath=texPath
        stride+=if(hasColor)utils.FloatsPerColor else 0
        stride+=if(hasTex)utils.FloatsPerTexture else 0
        stride+=if(hasNormal)utils.FloatsPerNormal else 0
        mHasColor=hasColor
        nVertices=data.second.indices.size
    }

    override fun onInit() {
        program=mScene.loadProgram("allShader2")
        mBuffer= VertexBuffer()
        mBuffer.load(mVertices,true)
        mBuffer.loadIndicesBuffer(mesh.indices,true)
        program.use()
        var offset=0
        program.setFloat("position",utils.FloatsPerPosition,stride,offset)
        offset+=utils.FloatsPerPosition
        if(mHasColor){
            program.setFloat("color",utils.FloatsPerColor,stride,offset)
            offset+=utils.FloatsPerColor
        }
        if(mHasTex){
            program.setFloat("tex",utils.FloatsPerTexture,stride,offset)
            mTex=mScene.loadTexture(mTexPath)
            offset+=utils.FloatsPerTexture
        }
        if(mHasNormal){
            program.setFloat("normal",utils.FloatsPerNormal,stride,offset)
            offset+=utils.FloatsPerNormal
        }

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
        program.setUniformInt("hasColor",if(mHasColor)1 else 0)
        program.setUniformInt("hasTex",if(mHasTex)1 else 0)
        program.setUniformInt("hasNormal",if(mHasNormal)1 else 0)

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        val lightPos = Vec3()

        program.setUniform3fv("light.position",lightPos.toArray())
        program.setUniform3fv("cameraPos",mScene.camera.defaultPos.toArray())

        program.setUniform3fv("light.ambient", Vec3(1f,1f).toArray())
        program.setUniform3fv("light.diffuse", Vec3(1f,1f).toArray())
        program.setUniform3fv("light.specular", Vec3(1f,1f).toArray())

        program.setUniform3fv("material.ambient", Vec3(.1f,.1f,.1f).toArray())
        program.setUniform3fv("material.diffuse", Vec3(.7f,.7f,.7f).toArray())
        program.setUniform3fv("material.specular", Vec3(1f,1f,1f).toArray())
        program.setUniformFloat("material.shininess",20f)

        drawElements(nVertices)
        mBuffer.checkGlError("IPCTN-Draw")
    }
}