package com.arthurgichuhi.kotlinopengl.io_Operations

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.arthurgichuhi.kotlinopengl.core.IReceiveInput
import com.arthurgichuhi.kotlinopengl.core.InputMode
import java.util.ArrayList

class Input(ctx: Context): OnTouchListener, GestureDetector.OnGestureListener {
    private var gestureDetector:GestureDetector?=null
    private var mode= InputMode.MOVE
    private val receivers:MutableList<IReceiveInput> = ArrayList()
    init {
        gestureDetector=GestureDetector(ctx,this)
    }

    fun destroy(){
        gestureDetector = null
        receivers.clear()
    }

    fun addReceiver(camera:IReceiveInput){
        receivers.add(camera)
    }

    fun setCurrentMode(iMode:InputMode){
        mode=iMode
    }

    private fun onScroll(distanceX: Float, distanceY: Float){
        for(i in receivers){
            i.scroll(mode,distanceX,distanceY)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector?.onTouchEvent(event!!)
        Log.d("TAG","On Touch ${gestureDetector?.isLongpressEnabled}")
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.d("TAG","Down ${e.x}    ${e.y}    ${e.downTime}")
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
        Log.d("TAG","Long Press ${e.x}   ${e.y}   ${e.buttonState}")
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