package com.arthurgichuhi.kotlinopengl.customObjs

import kotlin.math.cos
import kotlin.math.sin

class PathVert {

    fun ellipse(scale:Float,e:Float,theta:Float):FloatArray{
        // https://en.wikipedia.org/wiki/Orbit_equation
        val r = scale.toDouble()/(1+e* cos(theta)).toDouble()
        val x = (r*cos(theta)).toFloat()
        val z = (r* sin(theta)).toFloat()
        return floatArrayOf(r.toFloat(),x,z)
    }

    fun generateEllipses(scale: Float,e: Float,nPoints:Int,y:Float):FloatArray{
        val path = FloatArray(3*(nPoints+1))
        var l = 0
        for(i in 0..nPoints){
            val theta =(i.toFloat()/nPoints.toFloat())*(Math.PI.toFloat()*2)
            val tmp = ellipse(scale,e,theta)
            val r =tmp[0].toDouble()
            val x = tmp[1]
            val z = tmp[2]

            path[l] = x
            l++
            path[l] = y
            l++
            path[l] = z
            l++
        }
        return path
    }
}