package com.arthurgichuhi.kotlinopengl.io_Operations

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.arthurgichuhi.kotlinopengl.core.IReceiveInput
import com.arthurgichuhi.kotlinopengl.core.InputMode
import java.util.ArrayList

class Input(ctx: Context): OnTouchListener, GestureDetector.OnGestureListener {
    private val gestureDetector=GestureDetector(ctx,this)
    private var mode= InputMode.MOVE
    private val receivers:MutableList<IReceiveInput> = ArrayList()

    fun addReceiver(camera:IReceiveInput){
        receivers.add(camera)
    }

    fun setCurrentMode(iMode:InputMode){
        mode=iMode
    }

    fun onScroll(distanceX: Float,distanceY: Float){
        for(i in receivers){
            i.scroll(mode,distanceX,distanceY)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event!!)
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

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
        onScroll(distanceX,distanceY)
        return false
    }

    override fun onLongPress(e: MotionEvent) {

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