package com.arthurgichuhi.kotlinopengl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class Utils {
    val TAG: String = "Utils"

    val BytesPerFloat: Int = 4
    val BytesPerShort: Int = 2
    val FloatsPerPosition: Int = 3
    val FloatsPerColor: Int = 3
    val FloatsPerTexture: Int = 2

    fun getBitmapFromAssets(ctx: Context, name:String): Bitmap {
        val options = BitmapFactory.Options()
        options.inScaled = false
        var bitmap: Bitmap?
        try {
            ctx.assets.open(name).use { inputStream ->
                bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            throw e
        }
        return bitmap!!
    }

    fun readAssetFile(ctx: Context, fileName: String): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(ctx.assets.open("shaders/$fileName")))
            val sb = StringBuilder()
            var mLine: String?
            while ((reader.readLine().also { mLine = it }) != null) {
                sb.append(mLine)
                sb.append("\n")
            }
            return sb.toString()
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
        }
        return null
    }

    fun wrapTo2Pi(value:Float):Float{
        var v=value
        val pi2=Math.PI*2
        while (v>pi2){
            v -= pi2.toFloat()
        }
        while (v<pi2){
            v += pi2.toFloat()
        }
        return v
    }

}