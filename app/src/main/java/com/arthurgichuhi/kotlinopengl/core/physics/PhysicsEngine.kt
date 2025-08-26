package com.arthurgichuhi.kotlinopengl.core.physics

import android.util.Log
import com.arthurgichuhi.kotlinopengl.core.animation.animatedModel.Bone
import com.arthurgichuhi.kotlinopengl.customObjs.Actor
import com.arthurgichuhi.kotlinopengl.customObjs.ActorNPC
import com.arthurgichuhi.kotlinopengl.models.CollisionData
import de.javagl.jgltf.model.NodeModel
import org.joml.Matrix4f
import org.joml.Vector4f
import java.util.regex.Pattern

class PhysicsEngine(val actor: Actor) {
    companion object{
        private val RightArm = Pattern.compile("mixamorig:RightArm")
        private val LeftArm = Pattern.compile("mixamorig:LeftArm")
        private val RightForeArm = Pattern.compile("mixamorig:RightForeArm")
        private val LeftForeArm = Pattern.compile("mixamorig:LeftForeArm")

        private val Hand = Pattern.compile(".*Hand$")
        private val RightHand = Pattern.compile("mixamorig:RightHand$")
        private val LeftHand = Pattern.compile("mixamorig:LeftHand$")

        private val LeftUpLeg = Pattern.compile("mixamorig:LeftUpLeg")
        private val RightUpLeg = Pattern.compile("mixamorig:RightUpLeg")
        private val LeftLeg = Pattern.compile("mixamorig:LeftLeg")
        private val RightLeg = Pattern.compile("mixamorig:RightLeg")

        private val Foot = Pattern.compile(".*Foot$")
        private val LeftFoot = Pattern.compile("mixamorig:LeftFoot")
        private val RightFoot = Pattern.compile("mixamorig:RightFoot")

        private val Neck = Pattern.compile("mixamorig:Neck")

        private val LeftShoulder = Pattern.compile("mixamorig:LeftShoulder")
        private val RightShoulder = Pattern.compile("mixamorig:RightShoulder")

        private val Head = Pattern.compile("mixamorig:Head")
        private val Spine = Pattern.compile("mixamorig:Spine1")
        private val Spine1 =  Pattern.compile("mixamorig:Spine")
        private val Spine2 =  Pattern.compile("mixamorig:Spine2")
    }
    var skeleton:Map<NodeModel, Bone>
    init {
        skeleton = actor.bones
            .filter {
                        RightArm.matcher(it.key.name).find() ||
                        LeftArm.matcher(it.key.name).find() ||
                        RightForeArm.matcher(it.key.name).find() ||
                        LeftForeArm.matcher(it.key.name).find() ||
                        RightHand.matcher(it.key.name).find() ||
                        LeftHand.matcher(it.key.name).find() ||
                        LeftLeg.matcher(it.key.name).find() ||
                        RightLeg.matcher(it.key.name).find() ||
                        LeftUpLeg.matcher(it.key.name).find() ||
                        RightUpLeg.matcher(it.key.name).find() ||
                        LeftFoot.matcher(it.key.name).find() ||
                        RightFoot.matcher(it.key.name).find() ||
                        LeftShoulder.matcher(it.key.name).find() ||
                        RightShoulder.matcher(it.key.name).find() ||
                        Head.matcher(it.key.name).find() ||
                        Neck.matcher(it.key.name).find() ||
                        Spine.matcher(it.key.name).find() ||
                        Spine1.matcher(it.key.name).find()||
                        Spine2.matcher(it.key.name).find()
            }
    }

    fun trackBones(npc: ActorNPC): CollisionData?{
        val skeleton2 = npc.bones.filter {
                    LeftArm.matcher(it.key.name).find() ||
                    RightForeArm.matcher(it.key.name).find() ||
                    LeftForeArm.matcher(it.key.name).find() ||
                    RightHand.matcher(it.key.name).find() ||
                    LeftHand.matcher(it.key.name).find() ||
                    LeftLeg.matcher(it.key.name).find() ||
                    RightLeg.matcher(it.key.name).find() ||
                    LeftUpLeg.matcher(it.key.name).find() ||
                    RightUpLeg.matcher(it.key.name).find() ||
                    LeftFoot.matcher(it.key.name).find() ||
                    RightFoot.matcher(it.key.name).find() ||
                    LeftShoulder.matcher(it.key.name).find() ||
                    RightShoulder.matcher(it.key.name).find() ||
                    Head.matcher(it.key.name).find() ||
                    Neck.matcher(it.key.name).find() ||
                    Spine.matcher(it.key.name).find() ||
                    Spine1.matcher(it.key.name).find()||
                    Spine2.matcher(it.key.name).find()
        }
        for(bone in skeleton){
            for (npcBone in skeleton2){
                val boneOrigin = Vector4f()
                val npcBoneOrigin = Vector4f()

                Matrix4f().set(actor.modelMat).mul(Matrix4f().set(bone.value.animatedTransform))
                    .transform(boneOrigin)

                Matrix4f().set(npc.modelMat).mul(Matrix4f().set(npcBone.value.animatedTransform))
                    .transform(npcBoneOrigin)

                val x = boneOrigin[0]
                val y = boneOrigin[1]
                val z = boneOrigin[2]

                val x2 = npcBoneOrigin[0]
                val y2 = npcBoneOrigin[1]
                val z2 = npcBoneOrigin[2]

                val halfSize = 0.1f

                if (x + halfSize < x2 - halfSize || x - halfSize > x2 + halfSize) break
                if (y + halfSize < y2 - halfSize || y - halfSize > y2 + halfSize) break
                if (z + halfSize < z2 - halfSize || z - halfSize > z2 + halfSize) break
                Log.d("TAG","Success Collision")
                var actorHit = false
                var npcHit = false
                var headHit = false
                var bodyHit = false
                if(
                    Foot.matcher(bone.value.node.name).find() ||
                    Hand.matcher(bone.value.node.name).find()
                    ){
                    actorHit = true
                    if(Head.matcher(bone.value.node.name).find() ||
                        Neck.matcher(bone.value.node.name).find()){
                        headHit = true
                    }
                    else{
                        bodyHit = true
                    }
                }
                if(
                    Foot.matcher(npcBone.value.node.name).find() ||
                    Hand.matcher(npcBone.value.node.name).find()
                ){
                    npcHit = true
                    if(Head.matcher(bone.value.node.name).find() ||
                        Neck.matcher(bone.value.node.name).find()){
                        headHit = true
                    }
                    else{
                        bodyHit = true
                    }
                }
                return CollisionData(1,actorHit,npcHit,headHit,bodyHit)
            }
        }
        return null
    }
}