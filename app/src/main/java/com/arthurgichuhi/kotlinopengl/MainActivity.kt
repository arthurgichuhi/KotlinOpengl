package com.arthurgichuhi.kotlinopengl

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.arthurgichuhi.kotlinopengl.controllers.JoystickController
import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.core.InputMode
import com.arthurgichuhi.kotlinopengl.core.ObjUpdateCall
import com.arthurgichuhi.kotlinopengl.customObjs.Actor
import com.arthurgichuhi.kotlinopengl.customObjs.CollisionObj
import com.arthurgichuhi.kotlinopengl.customObjs.GltfObj
import com.arthurgichuhi.kotlinopengl.customObjs.PCTNObj
import com.arthurgichuhi.kotlinopengl.customObjs.PathVert
import com.arthurgichuhi.kotlinopengl.customObjs.SkyBox
import com.arthurgichuhi.kotlinopengl.customObjs.Sphere2
import com.arthurgichuhi.kotlinopengl.customObjs.SphereObj
import com.arthurgichuhi.kotlinopengl.enums.AObjectType
import com.arthurgichuhi.kotlinopengl.gl_surface.MyScene
import com.arthurgichuhi.kotlinopengl.gl_surface.MySurfaceView
import com.arthurgichuhi.kotlinopengl.io_Operations.Input
import com.arthurgichuhi.kotlinopengl.models.ModelInputs
import com.arthurgichuhi.kotlinopengl.models.Vec3f
import com.arthurgichuhi.kotlinopengl.my_ui.HomeButton
import de.javagl.jgltf.model.GltfAnimations
import de.javagl.jgltf.model.animation.AnimationManager.AnimationPolicy
import de.javagl.jgltf.model.io.GltfModelReader

class MainActivity : ComponentActivity(){
    private lateinit var myScene:MyScene
    private lateinit var input:Input

    private val gltfModelReader = GltfModelReader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        input = Input(this)
        myScene = MyScene(this,input)


        val sb = SkyBox(300f,
            "textures/milkyway2/left.png",
            "textures/milkyway2/right.png",
            "textures/milkyway2/top.png",
            "textures/milkyway2/bottom.png",
            "textures/milkyway2/front.png",
            "textures/milkyway2/back.png")

        //myScene.addObject(sb)

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
        //myScene.addObject(earth)

        val sun = PCTNObj(
            Sphere2(1f,10).getPositionsAndTex(),
            false,false,true,"textures/sun.jpg")
        sun.setUpdateCall(object:ObjUpdateCall{
            override fun update(time: Long, obj: AObject) {
                moveSun(time,obj)
            }
        })

        //myScene.addObject(sun)

        //GLTF
        val file = this.assets.open("models/model/Model.glb")
        val gltfModel = gltfModelReader.readWithoutReferences(file)
        file.close()
        val gltObj = GltfObj(
            gltfModel,
            ModelInputs(false, false, false, false),
            "models/model/diffuse.png"
        )
        gltObj.setUpdateCall(object : ObjUpdateCall {
            override fun update(time: Long, obj: AObject) {

            }
        })
        myScene.addObject(gltObj)

//        val actor = Actor(gltfModel, "models/model/diffuse.png")
//        actor.scale(Vec3f(5f,5f,5f))
//        myScene.addObject(actor)


//        val interiorFile = this.assets.open("models/model/house-4.glb")
//        val interiorGltf = gltfModelReader.readWithoutReferences(interiorFile)
//        interiorFile.close()
//        Array(interiorGltf.meshModels.size) {
//            CollisionObj(interiorGltf.meshModels[it],interiorGltf.nodeModels[it], "", ModelInputs(false, false, false, false))
//        }.run {
//            for(obj in this){
//                obj.objectType = AObjectType.COLLISION_TYPE
//                myScene.addObject(obj)
//            }
//        }

        setContent{
            HomeScreen()
        }
    }

    override fun onDestroy() {
        myScene.destroy()
        super.onDestroy()
    }

    private var theta = 0f
    private fun moveEarth(time: Long, earth: AObject) {
        theta -= (1f/180f*Math.PI.toFloat())
        val res = PathVert().ellipse(3f,.5f,theta)
        val currentPos = floatArrayOf(res[1],0f,res[2])
        earth.setTransMat4(Vec3f())
        earth.rotate(5f, Vec3f(0f,1f,0f))
        earth.setTransMat4(Vec3f(currentPos[0],currentPos[1],currentPos[2]))
    }
    var currentView = 0
    private fun changeView(){
        if(currentView==0){
            myScene.camera.setDefaultView(Vec3f(0f,0f,10f), Vec3f(0f,0f,-.1f))
        }
        else if(currentView==1){
            myScene.camera.setDefaultView(Vec3f(0f,25f,1f), Vec3f(0f,-1f,-.1f))
        }
    }

    private fun moveSun(time: Long,sun:AObject){
        sun.rotate(1.5f, Vec3f(0f,1f,0f))
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
            Row(modifier = Modifier
                .fillMaxWidth(.6f)
                .align(Alignment.BottomStart,),) {
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