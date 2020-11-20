package com.leslie.thumbnailview

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leslie.thumbnail.OnScrollBorderListener
import com.leslie.thumbnail.util.DisplayUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tnv_main.apply {
            setInterval(200f,800f)
            setOnScrollBorderListener(object : OnScrollBorderListener {
                override fun onScrollBorder(start: Float, end: Float): Array<String> {
                    return arrayOf("$start", "$end")
                }

                override fun onScrollEnd() {

                }

            })
        }

//        val bitmapDownTips = DisplayUtil.vectorToBitmap(
//            this, com.leslie.thumbnail.R.drawable.bg_drop_down,
//            DisplayUtil.dp2px(this, 30f), DisplayUtil.dp2px(this, 30f)
//        )
//        iv_mian.setImageBitmap(bitmapDownTips)
    }
}