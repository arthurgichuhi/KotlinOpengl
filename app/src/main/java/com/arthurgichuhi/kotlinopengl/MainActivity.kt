package com.arthurgichuhi.kotlinopengl

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.arthurgichuhi.aopengl.models.Vec3
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.InputMode
import com.arthurgichuhi.kotlinopengl.core.ObjUpdateCall
import com.arthurgichuhi.kotlinopengl.core.WaveFrontLoader
import com.arthurgichuhi.kotlinopengl.customObjs.Cube
import com.arthurgichuhi.kotlinopengl.customObjs.PCTObj
import com.arthurgichuhi.kotlinopengl.customObjs.PObj
import com.arthurgichuhi.kotlinopengl.customObjs.PathVert
import com.arthurgichuhi.kotlinopengl.customObjs.SkyBox
import com.arthurgichuhi.kotlinopengl.customObjs.Sphere
import com.arthurgichuhi.kotlinopengl.customObjs.Sphere2
import com.arthurgichuhi.kotlinopengl.customObjs.SphereObj
import com.arthurgichuhi.kotlinopengl.customObjs.WireObj
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView
import com.arthurgichuhi.kotlinopengl.io_Operations.Input
import com.arthurgichuhi.kotlinopengl.my_ui.HomeButton
import com.arthurgichuhi.kotlinopengl.utils.Utils
import com.arthurgichuhi.kotlinopengl.viewModel.MyViewModel

class MainActivity : ComponentActivity(){
    //private val myModel:MyViewModel by viewModels<MyViewModel>()
    private lateinit var myScene:MyScene
    private lateinit var input:Input

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        input=Input(this)
        myScene=MyScene(this,input,this@MainActivity)

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

        val sb = SkyBox(300f,
            "textures/milkyway2/left.png",
            "textures/milkyway2/right.png",
            "textures/milkyway2/top.png",
            "textures/milkyway2/bottom.png",
            "textures/milkyway2/front.png",
            "textures/milkyway2/back.png")

        myScene.addObject(sb)

        val earth=SphereObj(
            1f,3,
            "textures/earth/left.png",
            "textures/earth/right.png",
            "textures/earth/top.png",
            "textures/earth/bottom.png",
            "textures/earth/front.png",
            "textures/earth/back.png")
        earth.setUpdateCall(object:ObjUpdateCall{
            override fun update(time: Long, obj: AObject) {
                moveEarth(time,obj)
            }
        })
        myScene.addObject(earth)

        val sun = PCTObj(
            Sphere2(1f,10).getPositionsAndTex(),
            false,false,true,"textures/sun.jpg")
        sun.setUpdateCall(object:ObjUpdateCall{
            override fun update(time: Long, obj: AObject) {
                moveSun(time,obj)
            }
        })

        myScene.addObject(sun)

        val ellipses = WireObj()
        ellipses.setColor(Vec3(.75f,.6f,.45f))
        val ellipsesVert = PathVert().generateEllipses(3f,.5f,100,0f)
        ellipses.setVerticesFromPath(ellipsesVert,3,0)
        myScene.addObject(ellipses)

        val wvLoader = WaveFrontLoader(this,"models/cubelike/cube-like.obj")
        val cubeLike = PCTObj(wvLoader.getFaces(true,true),false,true,true,"models/cubelike/simpletexture.png")
        cubeLike.translate(Vec3(0f,3f,3f))
        cubeLike.setUpdateCall(object:ObjUpdateCall{
            override fun update(time: Long, obj: AObject) {
                obj.rotate(2f,Vec3(1f,1f,0f))
            }
        })
        myScene.addObject(cubeLike)
//        val wireObj=WireObj()
//        wireObj.setColor(Vec3(0f,1f,0f))
//        wireObj.setVerticesFromTriangleBuffer(earth,0,Utils().FloatsPerPosition+Utils().FloatsPerTexture)
//
//        wireObj.setUpdateCall(object:ObjUpdateCall{
//            override fun update(time: Long, obj: AObject) {
//                obj.rotate(1f,Vec3(0f,1f,0f))
//            }
//        })
//        myScene.addObject(wireObj)

        setContent{
            HomeScreen()
        }
    }
    private var theta = 0f
    private fun moveEarth(time: Long, earth: AObject) {
        theta -= (1f/180f*Math.PI.toFloat())
        val res = PathVert().ellipse(3f,.5f,theta)
        val currentPos = floatArrayOf(res[1],0f,res[2])
        earth.setTransMat4(Vec3())
        earth.rotate(5f, Vec3(0f,1f,0f))
        earth.setTransMat4(Vec3(currentPos[0],currentPos[1],currentPos[2]))
    }
    var currentView = 0
    private fun changeView(){
        if(currentView==0){
            myScene.camera.setDefaultView(Vec3(0f,0f,10f), Vec3(0f,0f,-.1f))
        }
        else if(currentView==1){
            myScene.camera.setDefaultView(Vec3(0f,25f,1f), Vec3(0f,-1f,-.1f))
        }
    }

    private fun moveSun(time: Long,sun:AObject){
        sun.rotate(1.5f, Vec3(0f,1f,0f))
    }

    @Composable
    fun HomeScreen(){
        Box{
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {context-> MySurfaceView(context,myScene,input) }
            )
            Text("Frame Rate:00FPS", modifier = Modifier.align(alignment = Alignment.TopEnd))
            Row(modifier=Modifier.align(Alignment.TopStart)) {
                HomeButton(
                    callback ={ myScene.camera.resetCamera() },
                    icon = Icons.Default.Home
                )
                HomeButton(
                    callback = {
                        currentView = if(currentView==0) {
                            1
                        } else {
                            0
                        }
                        changeView()
                    },
                    icon = Icons.Filled.Star
                )
            }
            Row(modifier = Modifier.fillMaxWidth(.6f).align(Alignment.BottomStart,),) {
                //move
                HomeButton(
                    callback = {input.setCurrentMode(InputMode.MOVE)},
                    icon = Icons.Filled.AddCircle
                )
                //rotate
                HomeButton(
                    callback={input.setCurrentMode(InputMode.ROTATE)},
                    icon = Icons.Filled.Refresh
                )
                //up_down
               HomeButton(
                   callback={input.setCurrentMode(InputMode.UP_DOWN)},
                   icon = Icons.Filled.KeyboardArrowUp
               )
            }
    }
}
}