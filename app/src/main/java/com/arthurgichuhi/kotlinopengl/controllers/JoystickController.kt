package com.arthurgichuhi.kotlinopengl.controllers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color

class JoystickController(context:Context):View(context) {
    private var pos = Pair(0f,0f)
    private var innerPos = Pair(0f,0f)
    private var outerCircleRadius = 0f
    private var myCanvas: Canvas? = null

    fun updateJoystickPos(pos1:Pair<Float,Float>,pos2:Pair<Float,Float>){
        Log.d("TAG","Positions ${pos1.toList()}  ${pos2.toList()}")
        pos = pos1
        innerPos = pos2
    }

    private val outerCirclePaint = Paint().apply {
        setColor(Color.LightGray.hashCode())
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    private val innerCirclePaint = Paint().apply {
        setColor(Color.Transparent.hashCode())
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d("TAG","Size Changed")
        outerCircleRadius = (h/2f)*.3f
        pos = Pair(w/5f,h/3f)
        innerPos = pos
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("TAG","Drawing")
        myCanvas = canvas
        canvas.drawCircle(pos.first,pos.second,outerCircleRadius,outerCirclePaint)
        canvas.drawCircle(innerPos.first, innerPos.second, (outerCircleRadius/2), innerCirclePaint)
    }
}