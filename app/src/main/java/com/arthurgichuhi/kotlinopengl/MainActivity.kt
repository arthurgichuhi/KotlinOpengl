package com.arthurgichuhi.kotlinopengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView

class MainActivity : ComponentActivity(){
    private lateinit var myGlSurface:GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myGlSurface=MySurfaceView(this)
        setContentView(myGlSurface)
    }
}
