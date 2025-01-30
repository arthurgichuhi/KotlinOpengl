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
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.ObjUpdateCall
import com.arthurgichuhi.kotlinopengl.customObjs.Cube
import com.arthurgichuhi.kotlinopengl.customObjs.PCTObj
import com.arthurgichuhi.kotlinopengl.customObjs.PObj
import com.arthurgichuhi.kotlinopengl.customObjs.Sphere
import com.arthurgichuhi.kotlinopengl.customObjs.WireObj
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView
import com.arthurgichuhi.kotlinopengl.my_ui.HomeButton
import com.arthurgichuhi.kotlinopengl.utils.Utils
import com.arthurgichuhi.kotlinopengl.viewModel.MyViewModel

class MainActivity : ComponentActivity(){
    //private val myModel:MyViewModel by viewModels<MyViewModel>()
    private lateinit var myScene:MyScene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myScene=MyScene(this,this@MainActivity)

//        val cube=PObj(Cube().create(Vec3(1f,1f,1f)), Vec3(.5f,.5f,0f))
//
//        val verts=Cube().createWithOneFileTex(Vec3(1f,1f,1f),4,2)
//        val cube2=PCTObj(verts,false,true,"textures/eightcolors.png",false)
//
//        cube2.setUpdateCall(object:ObjUpdateCall{
//            override fun update(time: Long, obj: AObject) {
//                obj.rotate(1f,Vec3(1f,0f,0f))
//            }
//        })
//        myScene.addObject(cube2)
//
//        val wireObj=WireObj()
//        wireObj.setColor(Vec3(0f,1f,0f))
//        wireObj.setVerticesFromTriangleBuffer(verts,0,Utils().FloatsPerPosition+Utils().FloatsPerTexture)
//
//        wireObj.setUpdateCall(object:ObjUpdateCall{
//            override fun update(time: Long, obj: AObject) {
//                obj.rotate(1f,Vec3(1f,0f,0f))
//            }
//        })
//        myScene.addObject(wireObj)

        val sv=Sphere(2)
        val verts = sv.getPositions()
        val sphere = PObj(verts,Vec3(1f,1f,0f))
        sphere.setUpdateCall(object:ObjUpdateCall{
            override fun update(time: Long, obj: AObject) {
                obj.rotate(1f,Vec3(1f,0f,0f))
            }
        })
        myScene.addObject(sphere)

        val wireObj=WireObj()
        wireObj.setColor(Vec3(0f,1f,0f))
        wireObj.setVerticesFromTriangleBuffer(verts,0,Utils().FloatsPerPosition)

        wireObj.setUpdateCall(object:ObjUpdateCall{
            override fun update(time: Long, obj: AObject) {
                obj.rotate(1f,Vec3(1f,0f,0f))
            }
        })
        myScene.addObject(wireObj)

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

            Row(modifier = Modifier.fillMaxWidth(.6f).align(Alignment.CenterStart,),) {
                //Position
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HomeButton(
                        callback = {
                            myScene.camera.position.z +=.5f
                            myScene.camera.update()
                        },
                        icon=Icons.Filled.KeyboardArrowUp,
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        HomeButton(
                            callback = {
                                myScene.camera.position.x +=.5f
                                myScene.camera.update()

                            },
                            icon=Icons.Filled.KeyboardArrowLeft
                        )
                        HomeButton(
                            callback = {
                                myScene.camera.position.x -=.5f
                                myScene.camera.update()
                            },
                            icon=Icons.Filled.KeyboardArrowRight
                        )
                    }
                    HomeButton(
                        callback = {
                            myScene.camera.position.z -=.5f
                            myScene.camera.update()
                        },
                        icon = Icons.Default.KeyboardArrowDown
                    ) }
                //Rotation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HomeButton(
                        callback = {
                            myScene.camera.rotation.y+=10f
                            myScene.camera.update()
                        },
                        icon=Icons.Filled.KeyboardArrowUp,
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        HomeButton(
                            callback = {
                                myScene.camera.rotation.z+=10f
                                myScene.camera.update()

                            },
                            icon=Icons.Filled.KeyboardArrowLeft
                        )
                        HomeButton(
                            callback = {
                                myScene.camera.rotation.z-=10f
                                myScene.camera.update()
                            },
                            icon=Icons.Filled.KeyboardArrowRight
                        )
                    }
                    HomeButton(
                        callback = {
                            myScene.camera.rotation.y-=10f
                            myScene.camera.update()
                        },
                        icon = Icons.Default.KeyboardArrowDown
                    ) }
            }

                HomeButton(
                    callback ={ myScene.camera.resetCamera() },
                    icon = Icons.Default.Home
                )

        }
    }
}