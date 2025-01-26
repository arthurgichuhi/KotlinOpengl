package com.arthurgichuhi.kotlinopengl.utils

import android.opengl.GLES32
import android.util.Log

class GlUtils {
    fun checkErr(loop: Int) {
        val err = GLES32.glGetError()
        if (err != 0) {
            Log.e("Err(", "$err) in loop ($loop)")
        }
    }
}