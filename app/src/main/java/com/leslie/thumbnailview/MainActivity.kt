package com.leslie.thumbnailview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leslie.thumbnail.OnScrollBorderListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tnv_main.apply {
            setInterval(200f,800f)
            setOnScrollBorderListener(listener = object : OnScrollBorderListener {
                override fun onScrollBorder(start: Float, end: Float): Array<String> {
                    return arrayOf("$start", "$end")
                }

                override fun onScrollEnd() {

                }

            })
        }
    }
}