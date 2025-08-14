package app.own.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.util.Log
import app.own.internal.OwnInternal
import java.net.URL

internal object BitmapHelper {

    fun getBitmapFromUrl(url: String): Bitmap? {
        Log.i(OwnInternal.TAG, "Image Url : $url")
        return try {
            BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
        } catch (e: Exception) {
            Log.e(OwnInternal.TAG, "Image Url : $url", e)
            null
        }
    }

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return output
    }
}
