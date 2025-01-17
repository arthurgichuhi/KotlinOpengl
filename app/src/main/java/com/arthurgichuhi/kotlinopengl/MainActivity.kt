package com.arthurgichuhi.kotlinopengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {context->
                    MySurfaceView(context)
                }
            )
        }
    }
}