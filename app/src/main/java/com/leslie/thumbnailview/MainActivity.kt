package com.leslie.thumbnailview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leslie.thumbnail.OnScrollBorderListener
import com.leslie.thumbnail.util.DisplayUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        tnv_main.setInterval(200f,800f)
        tnv_main.setOnScrollBorderListener(object : OnScrollBorderListener {
            override fun onScrollBorder(start: Float, end: Float): Array<String> {
                return arrayOf("$start", "$end")
            }

            override fun onScrolled() {

            }

        })
        val bitmapDownTips = DisplayUtil.vectorToBitmap(
            this, com.leslie.thumbnail.R.drawable.bg_drop_down,
            DisplayUtil.dp2px(this, 8f), DisplayUtil.dp2px(this, 4f)
        )
        iv_mian.setImageBitmap(bitmapDownTips)
    }
}