package com.arthurgichuhi.kotlinopengl.core.physics

import com.arthurgichuhi.kotlinopengl.core.AObject
import com.arthurgichuhi.kotlinopengl.customObjs.Actor
import com.arthurgichuhi.kotlinopengl.models.Vec3f

class PhysicsEngine(val actor: Actor,val sphereObj: AObject) {

    fun trackBones(){
        for(bone in actor.bones){
            sphereObj.translate(Vec3f(bone.key.translation))
        }
    }
}