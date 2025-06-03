package com.arthurgichuhi.kotlinopengl.gl_surface

import android.content.Context
import android.opengl.GLES32.GL_DEPTH_TEST
import android.opengl.GLES32.glClearColor
import android.opengl.GLES32.glEnable
import android.opengl.GLES32.glViewport
import android.opengl.GLSurfaceView
import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.AScene
import com.arthurgichuhi.kotlinopengl.core.IReceiveInput
import com.arthurgichuhi.kotlinopengl.io_Operations.Input
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyScene(context: Context, val input: Input):AScene(context),GLSurfaceView.Renderer {

    init {
        input.addReceiver(this.camera)
        myInput = input
    }
    override fun updateReceivers(receiver:IReceiveInput){
        input.addReceiver(receiver)
    }

    override fun destroy(){
        myInput?.destroy()
        super.destroy()
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glClearColor(0f,.3f,1f,1.0f)
        glEnable(GL_DEPTH_TEST)
        camera.getScene(this)
        initObjects()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        Log.d("TAG","Width $width  Height $height")
        glViewport(0,0,width,height)
        this.width = width.toFloat()
        this.height = height.toFloat()
        camera.width=width.toFloat()
        camera.height=height.toFloat()
        //set viewport
        glViewport(0,0,width,height)
        camera.update()
    }

    override fun onDrawFrame(p0: GL10?) {
        super.draw()
    }
}