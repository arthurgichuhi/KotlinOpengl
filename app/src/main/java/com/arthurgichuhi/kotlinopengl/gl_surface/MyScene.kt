package com.arthurgichuhi.kotlinopengl.gl_surface

import android.content.Context
import android.opengl.GLES32.*
import android.opengl.GLSurfaceView
import com.arthurgichuhi.kotlinopengl.MainActivity
import com.arthurgichuhi.kotlinopengl.core.AScene
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyScene(context: Context,activity: MainActivity):AScene(context),GLSurfaceView.Renderer {
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glClearColor(0f,.3f,1f,1.0f)
        glEnable(GL_DEPTH_TEST)
        camera.getScene(this)
        initObjects()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        camera.width=width
        camera.height=height
        //set viewport
        glViewport(0,0,width,height)
        camera.update()
    }

    override fun onDrawFrame(p0: GL10?) {
        //super.draw(p0)
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        updateObjects()
        drawObjects()
    }
}