package com.leslie.thumbnail.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.leslie.thumbnail.R

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

    fun drawableToBitmap(
        drawable: Drawable?,
        width: Int,
        height: Int
    ): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    fun drawableColoring(drawable: Drawable?, @ColorInt color: Int): Drawable? {
        drawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

    fun drawableColoring(layerDrawable: LayerDrawable?, @ColorInt color: Int): Drawable? {
        val item = layerDrawable?.findDrawableByLayerId(R.id.drawable_item_bg)
        item?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        return layerDrawable
    }


}