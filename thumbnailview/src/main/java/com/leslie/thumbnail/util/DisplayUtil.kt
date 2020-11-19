package com.leslie.thumbnail.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Author by haonan, Date on 2020/11/19.
 * Email:278913810@qq.com
 * PS:
 */
object DisplayUtil {

    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun screenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun screenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun vectorToBitmap(
        context: Context,
        vectorDrawableId: Int,
        width: Int,
        height: Int
    ): Bitmap {
        val vectorDrawable = context.getDrawable(vectorDrawableId)
        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable?.draw(canvas)
        return bitmap
    }
}