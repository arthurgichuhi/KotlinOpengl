package com.arthurgichuhi.kotlinopengl.viewModel

import androidx.lifecycle.ViewModel

class MyViewModel:ViewModel() {
    var frameRate:String=""

    fun updateFrameRate(rate:String){
        frameRate=rate
    }
}