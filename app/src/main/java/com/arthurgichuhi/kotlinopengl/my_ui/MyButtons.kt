package com.arthurgichuhi.kotlinopengl.my_ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun HomeButton(modifier:Modifier?=Modifier,callback:()->Unit,icon:ImageVector){
    IconButton(onClick = callback) {
        Icon(icon,"", modifier = modifier?:Modifier)
    }
}