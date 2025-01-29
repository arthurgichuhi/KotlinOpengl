package com.arthurgichuhi.kotlinopengl.customObjs

import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.AScene
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.utils.Utils

class WireObj:AObject() {
    private var Utils=Utils()
    private lateinit var program: Program
    private lateinit var color:Vec3
    private lateinit var mVertices:FloatArray
    private lateinit var buffer: VertexBuffer
    private var nLines=0
    private var stride=0
    private var lineWidth=20f


    fun setColor(myColor:Vec3){
        color=myColor
    }

    fun setVerticesFromTriangleBuffer(vert:FloatArray,offset:Int,stride:Int){
        nLines=vert.size/stride
        val linesCount=nLines
        mVertices=FloatArray(linesCount*2*3)
        var l=0
        var i=0
        while(i<nLines){
            val x1=vert[stride*(i)+offset]
            val y1=vert[stride*(i)+offset+1]
            val z1=vert[stride*(i)+offset+2]

            val x2=vert[stride*(i+1)+offset]
            val y2=vert[stride*(i+1)+offset+1]
            val z2=vert[stride*(i+1)+offset+2]

            val x3=vert[stride*(i+2)+offset]
            val y3=vert[stride*(i+2)+offset+1]
            val z3=vert[stride*(i+2)+offset+2]

            mVertices[l] = x1
            l++
            mVertices[l] = y1
            l++
            mVertices[l] = z1
            l++
            mVertices[l] = x2
            l++
            mVertices[l] = y2
            l++
            mVertices[l] = z2
            l++

            mVertices[l] = x1
            l++
            mVertices[l] = y1
            l++
            mVertices[l] = z1
            l++
            mVertices[l] = x3
            l++
            mVertices[l] = y3
            l++
            mVertices[l] = z3
            l++

            mVertices[l] = x3
            l++
            mVertices[l] = y3
            l++
            mVertices[l] = z3
            l++
            mVertices[l] = x2
            l++
            mVertices[l] = y2
            l++
            mVertices[l] = z2
            l++

            i+=3
        }
    }

    fun setVerticesFromPath(vert:FloatArray,stride:Int,offset:Int){
        nLines=(vert.size/Utils.FloatsPerPosition -1)
        val linesCount= nLines
        mVertices=FloatArray(linesCount*2*3)
        var l=0
        var i=0
        for(i in 0..<nLines){
            val x1=vert[stride*(i)+offset]
            val y1 = vert[stride*(i) + offset + 1]
            val z1 = vert[stride*(i) + offset + 2]

            val x2 = vert[stride*(i + 1)+offset]
            val y2 = vert[stride*(i + 1) + offset + 1]
            val z2 = vert[stride*(i + 1) + offset + 2]

            mVertices[l] = x1
            l++
            mVertices[l] = y1
            l++
            mVertices[l] = z1
            l++
            mVertices[l] = x2
            l++
            mVertices[l] = y2
            l++
            mVertices[l] = z2
            l++
        }
    }

    override fun onInit() {
        program=mScene.loadProgram("wireFrame")
        buffer=VertexBuffer()
        buffer.load(mVertices,true)
        program.use()

        var currentOffset=0

        program.setFloat("position",Utils.FloatsPerPosition,Utils.FloatsPerPosition,currentOffset)


    }

    override fun destroy(aScene: AScene) {

    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        buffer.bind()

        program.setUniform3f("color",color)
        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawLines(0,nLines*2,lineWidth)
    }

}