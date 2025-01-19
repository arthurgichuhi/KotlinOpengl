package com.arthurgichuhi.kotlinopengl.gl_surface

import android.content.Context
import android.opengl.GLSurfaceView
import com.arthurgichuhi.kotlinopengl.MainActivity
import com.arthurgichuhi.kotlinopengl.camera.MyCamera

class MySurfaceView(context: Context,camera: MyCamera): GLSurfaceView(context) {
    private val renderer: MyRender
    private var value: IntArray = IntArray(1)

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(3)
        renderer = MyRender(context,camera)
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}