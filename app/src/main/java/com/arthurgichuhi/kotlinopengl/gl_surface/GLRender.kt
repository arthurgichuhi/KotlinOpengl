package com.arthurgichuhi.kotlinopengl.gl_surface

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Build
import com.arthurgichuhi.kotlinopengl.camera.MyCamera
import com.arthurgichuhi.kotlinopengl.io_Operations.MyIO
import com.arthurgichuhi.kotlinopengl.shaders.Shaders
import java.util.Date
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRender(val context: Context,val camera: MyCamera):GLSurfaceView.Renderer {
    private val shaders=Shaders(context)
    private val myIo=MyIO(context)
    //
    private var mWidth=0
    private var mHeight=0
    //
    private var program=0
    private var position=0
    private var glColor=0
    private var glArray1=0
    private var glArray2=0
    private var glTex=0
    private var glTex1=0
    private var glTex2=0
    //matrices
    private val projection=FloatArray(16)
    private val model=FloatArray(16);
    private val view=FloatArray(16)
    private val identity=FloatArray(16)
    //
    private var glProjection=0
    private var glModel=0
    private var glView=0
    //objects
    private val object1= floatArrayOf(
        -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F,
        -0.5F, 0.1F, 0.0F, 1.0F, 1.0F, 0.0F, 0F, 0F,
        0.5F, 0.1F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0F,

        -0.5F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F,
        0.5F, 0.1F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F,
        0.5F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F
    )
    val object2= floatArrayOf(
        -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F,
        -0.5F, -0.1F, 0.0F, 1.0F, 1.0F, 1.0F, 0F, 0F,
        0.5F, -0.1F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0F,

        -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F,
        0.5F, -0.1F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0F,
        0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F
    )
    //
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f,.3f,1f,1.0f)

        glTex1 = myIo.loadTexture("textures/wall.png")
        glTex2= myIo.loadTexture("textures/wall_with_face.png")

        program=shaders.createProgram("texColorVs.txt","texColorFs.txt")
        glTex=GLES32.glGetAttribLocation(program,"tex")

        GLES32.glUseProgram(program)
        position=GLES32.glGetAttribLocation(program,"position")
        glColor=GLES32.glGetAttribLocation(program,"color")
        glModel=GLES32.glGetUniformLocation(program,"model")
        glView=GLES32.glGetUniformLocation(program,"view")
        glProjection=GLES32.glGetUniformLocation(program,"projection")

        glArray1=shaders.sendVertexDataToGL(object1,position,glColor,glTex)[0]
        glArray2=shaders.sendVertexDataToGL(object2,position,glColor,glTex)[0]

        Matrix.setIdentityM(model,0)
        Matrix.setIdentityM(view,0)
        Matrix.setIdentityM(projection,0)
        Matrix.setIdentityM(identity,0)
        Matrix.translateM(view,0,0f,0f,-3f)

    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        mWidth=width
        mHeight=height
        camera.width=width
        camera.height=height
        //set viewport
        GLES32.glViewport(0,0,width,height)

        val aspect=mWidth.toFloat()/mHeight.toFloat()

        Matrix.perspectiveM(projection,0,45.0f,aspect,.1f,100.0f)

    }
    private var startTime:Long= Date().time
    private var loop=0
    private var frameRate=0.0

    @SuppressLint("NewApi")
    override fun onDrawFrame(p0: GL10?) {

        GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)

        Matrix.rotateM(model,0,3f,0f,1f,0f)

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.TIRAMISU){
            Matrix.setRotateEulerM2(view,0,camera.rotation.x,camera.rotation.y,0f)
        }
        else{
            Matrix.setRotateEulerM(view,0,camera.rotation.x,camera.rotation.y,0f)
        }
        Matrix.translateM(view,0,camera.position.x,camera.position.y,camera.position.z)

        GLES32.glUseProgram(program)

        GLES32.glUniformMatrix4fv(glModel,1,false,model,0)
        GLES32.glUniformMatrix4fv(glView,1,false,view,0)
        GLES32.glUniformMatrix4fv(glProjection,1,false,projection,0)

        val glColor=GLES32.glGetUniformLocation(program,"color")

        GLES32.glUniform3f(glColor, 1.0F,1.0F,0F,)
        GLES32.glBindVertexArray(glArray1)
        GLES32.glBindTexture(GLES20.GL_TEXTURE_2D,glTex1)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES,0,6)

        GLES32.glUniformMatrix4fv(glModel,1,false,identity,0)
        GLES32.glUniform3f(glColor, 1.0F,1.0F,1F,)
        GLES32.glBindVertexArray(glArray2)
        GLES32.glBindTexture(GLES20.GL_TEXTURE_2D,glTex2)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES,0,6)
    }
}