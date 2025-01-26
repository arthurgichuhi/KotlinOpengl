package com.arthurgichuhi.kotlinopengl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.gl_objects.PObj
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView
import com.arthurgichuhi.kotlinopengl.my_ui.buttons.HomeButton
import com.arthurgichuhi.kotlinopengl.viewModel.MyViewModel

class MainActivity : ComponentActivity(){
    private val myModel:MyViewModel by viewModels<MyViewModel>()
    private lateinit var myScene:MyScene
    private val object1= floatArrayOf(
        -0.5F, 0.5F, 0.0F,
        -0.5F, 0.1F, 0.0F,
        0.5F, 0.1F, 0.0F,

        -0.5F, 0.5F, 0.0F,
        0.5F, 0.1F, 0.0F,
        0.5F, 0.5F, 0.0F
    )

    private val object2= floatArrayOf(
        -0.5F, -0.5F, 0.0F,
        -0.5F, -0.1F, 0.0F,
        0.5F, -0.1F, 0.0F,

        -0.5F, -0.5F, 0.0F,
        0.5F, -0.1F, 0.0F,
        0.5F, -0.5F, 0.0F
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myScene=MyScene(this)
        myScene.addObject(PObj(object1, Vec3(.0f,.5f,0f)))
        setContent{
            HomeScreen()
        }
    }

    @Composable
    fun HomeScreen(){
        Box{
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {context-> MySurfaceView(context,myScene) }
            )
            Text("Frame Rate:00FPS", modifier = Modifier.align(alignment = Alignment.TopEnd))

            Column(
                modifier = Modifier.fillMaxWidth(.3f).align(Alignment.CenterStart,),
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                HomeButton(
                    callback = { myScene.camera.position.z +=.5f },
                    icon=Icons.Filled.KeyboardArrowUp,
                    )

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    HomeButton(
                        callback = { myScene.camera.rotation.y+=20f },
                        icon=Icons.Filled.KeyboardArrowLeft
                    )
                    HomeButton(
                        callback = { myScene.camera.rotation.y-=20f },
                        icon=Icons.Filled.KeyboardArrowRight
                    )
                }
                HomeButton(
                    callback = { myScene.camera.position.z -=.5f },
                    icon = Icons.Default.KeyboardArrowDown
                ) }
                HomeButton(
                    callback ={ myScene.camera.resetCamera() },
                    icon = Icons.Default.Home
                )

        }
    }
}