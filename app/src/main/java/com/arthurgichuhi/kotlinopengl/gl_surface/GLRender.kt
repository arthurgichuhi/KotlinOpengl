package com.arthurgichuhi.kotlinopengl.gl_surface

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.Log
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO
import com.arthurgichuhi.kotlinopengl.shaders.Shaders
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender(context: Context):GLSurfaceView.Renderer {
    val shaders=Shaders(context)
    val myIO=MyIO(context)
    //
    var program=0
    var position=0
    var glArray1=0
    var glArray2=0
    //matrices
    val projectionMatrix=FloatArray(16)
    //objects
    val object1= floatArrayOf(
        -0.5F, -0.5F, 0.0F,
        -0.5F, -0.1F, 0.0F,
        0.5F, -0.1F, 0.0F,

        -0.5F, -0.5F, 0.0F,
        0.5F, -0.1F, 0.0F,
        0.5F, -0.5F, 0.0F,
    )
    val object2= floatArrayOf(
        -0.5F, 0.5F, 0.0F,
        -0.5F, 0.1F, 0.0F,
        0.5F, 0.1F, 0.0F,

        -0.5F, 0.5F, 0.0F,
        0.5F, 0.1F, 0.0F,
        0.5F, 0.5F, 0.0F,
    )
    //
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f,.3f,1f,1.0f)

        program=shaders.createProgram("vs1.txt","fs1.txt")

        GLES32.glUseProgram(program)
        position=GLES32.glGetAttribLocation(program,"position")

        glArray1=shaders.sendVertexDataToGL(object1,position)[0]
        glArray2=shaders.sendVertexDataToGL(object2,position)[0]

    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(p0: GL10?) {
        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)

        GLES32.glUseProgram(program)
        val glColor=GLES32.glGetUniformLocation(program,"color")
        GLES32.glUniform3f(glColor, 1.0F,1.0F,0F,)
        GLES32.glBindVertexArray(glArray1)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES,0,6)

        GLES32.glUniform3f(glColor, 1.0F,1.0F,1F,)
        GLES32.glBindVertexArray(glArray2)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES,0,6)

    }
}