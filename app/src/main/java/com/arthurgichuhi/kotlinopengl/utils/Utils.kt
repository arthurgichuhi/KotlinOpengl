package com.arthurgichuhi.kotlinopengl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Utils {
    val TAG: String = "Utils"

    val BytesPerFloat: Int = 4
    val BytesPerShort: Int = 2
    val BytesPerInt: Int = 4
    val FloatsPerPosition: Int = 3
    val FloatsPerColor: Int = 3
    val FloatsPerTexture: Int = 2
    val FloatsPerNormal:Int =3
    val FloatsPerJoint : Int = 3
    val IntsPerJoint: Int = 3
    val FloatsPerWeight: Int = 3

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
            reader = BufferedReader(InputStreamReader(ctx.assets.open(fileName)))
            val sb = StringBuilder()
            var mLine: String?
            while ((reader.readLine().also {
                mLine = it }) != null) {
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

    fun readXMLFile(ctx: Context,fileName:String):BufferedReader{
        return BufferedReader(InputStreamReader(ctx.assets.open(fileName)))
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

    fun isNullOrEmpty(str:String?):Boolean{
        if(str==null)return true
        if(str.isEmpty())return true
        return false
    }

    fun getCurrentTime():Float{

        val currentMillis = System.currentTimeMillis()
        Log.d("TAG","Current Millis - ${(currentMillis/1_000_000_000_000_0)}")
        return (currentMillis/1_000_000_000_000_0f)
    }

    fun createMatricesArrayBuffer(data:Array<FloatArray>):FloatBuffer{
        val buffer = ByteBuffer.allocate(data.size * 16 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        for(i in data){
            buffer.put(i)
        }
        return buffer.asReadOnlyBuffer()
    }
}