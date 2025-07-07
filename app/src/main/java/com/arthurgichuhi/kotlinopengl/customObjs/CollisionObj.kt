package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.core.animation.animation.Animator
import com.arthurgichuhi.kotlinopengl.models.ModelInputs
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import de.javagl.jgltf.model.MathUtils
import de.javagl.jgltf.model.MeshModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Intersectionf
import org.joml.Matrix4f
import org.joml.Vector3f

class CollisionObj(
    val mesh: MeshModel,
    private val nodeModel: NodeModel,
    private val texPath: String,
    private val modelInputs: ModelInputs
) : AObject() {
    private lateinit var program: Program
    private lateinit var buffer: VertexBuffer
    private lateinit var tex: Texture
    private val noOfVertices = mesh.meshPrimitiveModels[0].indices.count

//    val lowest = Vector3f()
//    val highest = Vector3f()


    init {
        if(nodeModel.translation!=null)this.translate(Vec3f(nodeModel.translation))
        if(nodeModel.rotation!=null)this.rotateQuartenion(nodeModel.rotation)
        if(nodeModel.scale!=null)this.scale(Vec3f(nodeModel.scale))
        findObjectBounds()
    }

    override fun onInit() {
        program = mScene.loadProgram(if (modelInputs.hasTextures) "texColor" else "noTexture")

        buffer = VertexBuffer()
        buffer.loadGltfIndices(mesh.meshPrimitiveModels[0], true)
        buffer.loadGltfFloats(
            mesh.meshPrimitiveModels[0],
            modelInputs,
            loadTex = { tex = mScene.loadTexture(texPath) },
            true
        )

        program.use()
    }

    override fun destroy() {
        this.destroy()
    }

    override fun onUpdate(time: Long) {

    }

    override fun draw(viewMat: FloatArray, projectionMat: FloatArray) {
        program.use()
        buffer.bind()

        program.setUniformMat("model",modelMat)
        program.setUniformMat("view",viewMat)
        program.setUniformMat("projection",projectionMat)

        drawElements(noOfVertices)
    }

    fun findObjectBounds(){
        val lowest = Vector3f()
        val highest = Vector3f()
        val data = mesh.meshPrimitiveModels[0].attributes["POSITION"]
        val vertexData = Animator.getFloatData(data!!)
        for(i in 0..<data.count){
            val x = vertexData[i]
            val y = vertexData[i+1]
            val z = vertexData[i+2]
            if(i==0){
                lowest.x = x
                highest.x = x
                lowest.y = y
                highest.y = y
                lowest.z = z
                highest.z = z
            }
            if(lowest.x>x)lowest.x = x
            if(lowest.y>y)lowest.y = y
            if(lowest.z>z)lowest.z = z

            if(highest.x<x)highest.x = x
            if(highest.y<y)highest.y = y
            if(highest.z<z)highest.z = z
        }

        Intersectionf.intersectRayTriangle(Vector3f(),Vector3f(),Vector3f(),Vector3f(),Vector3f(),.5f)
        Log.d("TAG","Max ${data.max?.toList()} Min ${data.min?.toList()}" +
                "\n Max ${highest.x} ${highest.y} ${highest.z}  Min ${lowest.x},${lowest.y},${lowest.z}")
    }
}