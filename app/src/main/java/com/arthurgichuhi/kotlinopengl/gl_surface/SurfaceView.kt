package com.arthurgichuhi.kotlinopengl.gl_surface

import android.content.Context
import android.opengl.GLSurfaceView
import com.arthurgichuhi.kotlinopengl.io_Operations.Input

class MySurfaceView(context: Context,myScene:MyScene,input: Input): GLSurfaceView(context) {
    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(myScene)
        this.setOnTouchListener(input)
    }
}