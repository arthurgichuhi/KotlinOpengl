package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.utils.Utils

class PCTNObj(
    vertices:FloatArray,hasColor:Boolean,
    hasNormal:Boolean,
    hasTex:Boolean, texPath:String
):AObject() {
    private lateinit var program: Program
    private lateinit var mColor: Vec3f
    private var mVertices:FloatArray = vertices
    private lateinit var mBuffer:VertexBuffer
    private var nVertices:Int=0
    private var stride:Int= Utils.FloatsPerPosition
    private var mHasColor=hasColor
    private var mHasTex=hasTex
    private var mTexPath=texPath
    private var mHasNormal = hasNormal
    private lateinit var mTex: Texture

    init {
        mHasTex=hasTex
        mTexPath=texPath
        stride+=if(hasColor)Utils.FloatsPerColor else 0
        stride+=if(hasTex)Utils.FloatsPerTexture else 0
        stride+=if(hasNormal)Utils.FloatsPerNormal else 0
        mHasColor=hasColor
        nVertices=vertices.size/stride
    }

    override fun onInit() {
        program=mScene.loadProgram("allShader")
        mBuffer=VertexBuffer()
        mBuffer.load(mVertices,true)
        program.use()
        var offset=0
        program.setFloat("position",Utils.FloatsPerPosition,stride,offset)
        offset+=Utils.FloatsPerPosition
        if(mHasColor){
            program.setFloat("color",Utils.FloatsPerColor,stride,offset)
            offset+=Utils.FloatsPerColor
        }
        if(mHasTex){
            program.setFloat("tex",Utils.FloatsPerTexture,stride,offset)
            mTex=mScene.loadTexture(mTexPath)
            offset+=Utils.FloatsPerTexture
        }
        if(mHasNormal){
            program.setFloat("normal",Utils.FloatsPerNormal,stride,offset)
            offset+=Utils.FloatsPerNormal
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

        drawTriangles(0, nVertices)
    }
}