package com.arthurgichuhi.kotlinopengl.core.collada.dataStructures

data class MeshData(
    val vertices :FloatArray,
    val textureCords : FloatArray,
    var normals : FloatArray,
    var indices : ShortArray,
    var jointIds : IntArray,
    var vertexWeights : FloatArray
)
