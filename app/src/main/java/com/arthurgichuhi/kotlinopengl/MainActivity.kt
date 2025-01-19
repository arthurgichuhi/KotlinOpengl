package com.arthurgichuhi.kotlinopengl

import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.arthurgichuhi.aopengl.models.Vec2f
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.camera.MyCamera
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView
import com.arthurgichuhi.kotlinopengl.my_ui.buttons.HomeButton
import com.arthurgichuhi.kotlinopengl.viewModel.MyViewModel

class MainActivity : ComponentActivity(){
    private val myModel:MyViewModel by viewModels<MyViewModel>()
    private lateinit var myGestureDetector: GestureDetector
    private var myCamera=MyCamera()

    enum class InputMode {
        MOVE,
        ROTATE,
        UP_DOWN
    }
    var inputMode=InputMode.MOVE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myGestureDetector=GestureDetector(this, activityGestureDetector())
        setContent{
            HomeScreen(myViewModel = myModel)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("OpenGl-Cam","-------Touch Event--------")
        if (event != null) {
            myGestureDetector.onTouchEvent(event)
        }
        return true
    }

    private fun activityGestureDetector():GestureDetector.OnGestureListener{
        return object:GestureDetector.OnGestureListener{
            override fun onDown(e: MotionEvent): Boolean {
                return false
            }

            override fun onShowPress(e: MotionEvent) {
                TODO("Not yet implemented")
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return false
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                when(inputMode){
                    InputMode.MOVE->{
                        myCamera.position.x = 5*distanceX/myCamera.width
                        myCamera.position.z = 5*distanceY/myCamera.height
                    }
                    InputMode.ROTATE->{
                        myCamera.rotation = Vec2f(30*distanceX/myCamera.width,30*distanceY/myCamera.height)
                    }
                    InputMode.UP_DOWN->{
                        myCamera.position.y = 10*(distanceY/myCamera.height)
                    }
                }
                Log.d("OpenGl","Camera Position -------- ${myCamera.position}")
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                TODO("Not yet implemented")
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return false
            }

        }
    }

    fun resetMyCamera(){
        myCamera.position= Vec3(0f,0f,-3f)
        myCamera.rotation=Vec2f(0f,0f)
    }

    @Composable
    fun HomeScreen(myViewModel: MyViewModel){
        Box{
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {context->
                    MySurfaceView(context,myCamera)
                }
            )
            Text("Frame Rate:${myViewModel.frameRate}", modifier = Modifier.align(alignment = Alignment.TopEnd))

            Column(
                modifier = Modifier.fillMaxWidth(.3f).align(Alignment.CenterStart,),
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                HomeButton(
                    callback = { myCamera.position.z +=.5f },
                    icon=Icons.Filled.KeyboardArrowUp,
                    )

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    HomeButton(
                        callback = {
                            myCamera.rotation.y+=20f
                        },icon=Icons.Filled.KeyboardArrowLeft
                    )
                    HomeButton(
                        callback = {
                            myCamera.rotation.y-=20f
                        },icon=Icons.Filled.KeyboardArrowRight
                    )
                }

                HomeButton(
                    callback = {
                        myCamera.position.z -=.5f
                    },
                    icon = Icons.Default.KeyboardArrowDown
                )
            }

            Row(
                modifier = Modifier.align(alignment = Alignment.BottomStart,
                    )
            ) {
                HomeButton(
                    callback={inputMode=InputMode.MOVE},icon= Icons.Default.LocationOn)

                HomeButton(
                    callback = {inputMode=InputMode.ROTATE},icon=Icons.Filled.Refresh
                )
                HomeButton(
                    callback = { inputMode=InputMode.UP_DOWN },icon=Icons.Filled.ArrowDropDown
                )
                HomeButton(
                    callback ={
                        resetMyCamera()
                    }, icon = Icons.Default.Home
                )
            }
        }
    }
}