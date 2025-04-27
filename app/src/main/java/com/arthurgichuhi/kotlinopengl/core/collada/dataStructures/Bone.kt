package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f

data class Bone(
    val node: NodeModel,
    val name: String,
    val children: MutableList<Bone> = mutableListOf(),
    var inverseBindMatrix : Matrix4f = Matrix4f(),
    var globalTransform: Matrix4f = Matrix4f()
)
