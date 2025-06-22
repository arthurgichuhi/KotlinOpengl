package com.arthurgichuhi.kotlinopengl.customObjs

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.Program
import com.arthurgichuhi.kotlinopengl.core.Texture
import com.arthurgichuhi.kotlinopengl.core.VertexBuffer
import com.arthurgichuhi.kotlinopengl.models.ModelInputs
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import de.javagl.jgltf.model.MeshModel
import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class CollisionObj(
    val mesh: MeshModel,
    val nodeModel: NodeModel,
    val texPath: String,
    val modelInputs: ModelInputs
) : AObject() {
    private lateinit var program: Program
    private lateinit var buffer: VertexBuffer
    private lateinit var tex: Texture
    private val noOfVertices = mesh.meshPrimitiveModels[0].indices.count

    init {
        Log.d("TAG","Rotation ${nodeModel.rotation?.toList()}")
        if(nodeModel.translation!=null)this.translate(Vec3f(nodeModel.translation))
        if(nodeModel.rotation!=null)this.rotateQuartenion(nodeModel.rotation)
        if(nodeModel.scale!=null)this.scale(Vec3f(nodeModel.scale))
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
}