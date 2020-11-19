package com.leslie.thumbnail

/**
 * Author by haonan, Date on 2020/11/18.
 * Email:278913810@qq.com
 * PS:
 */
interface OnScrollBorderListener {
    fun onScrollBorder(start: Float, end: Float): Array<String>

    fun onScrolled()
}