package com.leslie.thumbnail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Author by haonan, Date on 2020/11/18.
 * Email:278913810@qq.com
 * PS:
 */
class ThumbnailView(context: Context?) :
    View(context) {

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)

    constructor(context: Context?, attrs: AttributeSet?)

    private var scrollChange = false
    private var mWidth = 0
    private var mHeight = 0
    private var mTextHeight = 0
    private var mTextWidth = 0

    private var mTipsWidth = 0
    private var mTipsHeight = 0
    private val mPaint: Paint = Paint()
    private val mTextPaint: Paint = Paint()

    private val rectF: RectF = RectF() //左边拖动条的范围
    private val rectF2: RectF = RectF() //右边拖动条的范围
    private val rectF3: RectF = RectF()//左边阴影区域
    private val rectF4: RectF = RectF()//右边阴影区域
    private val rectF5: RectF = RectF()//左边的时间提示器
    private val rectF6: RectF = RectF()//右边的时间提示器
    private val rectF7: RectF = RectF()//左边的向下箭头
    private val rectF8: RectF = RectF()//右边的向下箭头

    private var rectWidth = 0 //拖动条宽度

    private var bitmapLeft: Bitmap
    private var bitmapRight: Bitmap

    //    private lateinit var bitmapText: Bitmap
    private var bitmapDownTips: Bitmap
    private var onScrollBorderListener: OnScrollBorderListener? = null
    private var minPx = 0
    private var maxPx = 0
    private var downX = 0f
    private var scrollLeft = false
    private var scrollRight = false
    private var paintStrokeWidth = 0

    private var tipsText: Array<String> = arrayOf("", "")

    init {
        mPaint.isAntiAlias = true
        paintStrokeWidth = resources.getDimension(R.dimen.paintStrokeWidth).toInt()
        mPaint.strokeWidth = paintStrokeWidth.toFloat()

        mTextPaint.textSize = resources.getDimension(R.dimen.paintTextSize)
        mTextPaint.color = getContext().getColor(R.color.paintTextColor)
        mTextPaint.textAlign = Paint.Align.CENTER

        bitmapLeft = BitmapFactory.decodeResource(resources, R.mipmap.video_thumbnail_left)
        bitmapRight = BitmapFactory.decodeResource(resources, R.mipmap.video_thumbnail_right)
//        bitmapText = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit_drop_down);
        //        bitmapText = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit_drop_down);
        bitmapDownTips = BitmapFactory.decodeResource(resources, R.mipmap.ic_edit_drop_down)

        rectWidth = resources.getDimension(R.dimen.scrollBarWidth).toInt()
        mTextHeight = resources.getDimension(R.dimen.textRectHeight).toInt()
        mTextWidth = resources.getDimension(R.dimen.textRectWidth).toInt()
        mTipsWidth = resources.getDimension(R.dimen.downTipsWidth).toInt()
        mTipsHeight = resources.getDimension(R.dimen.downTipsHeight).toInt()

        minPx = rectWidth
    }

    private fun readAttrs(attrs: AttributeSet?) {
        //获取xml中数据
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThumbnailView)
        val tipsTextSize = typedArray.getDimension(R.styleable.ThumbnailView_tipsTextSize, 12f)
        //        name = typedArray.getString(R.styleable.LocationViewWithAttrs_locationName);
//        address = typedArray.getString(R.styleable.LocationViewWithAttrs_locationDesc);
        typedArray.recycle()
    }

    fun setMinInterval(minPx: Int) {
        var minPx = minPx
        if (mWidth > 0 && minPx > mWidth) {
            minPx = mWidth
        }
        this.minPx = minPx
    }

    fun setMaxInterval(maxPx: Int) {
        if (mWidth > 0 && maxPx > mWidth) {
            minPx = mWidth
        }
        this.maxPx = maxPx
        //刷新右边滑块位置
        rectF2.left = this.maxPx - rectWidth.toFloat()
        rectF2.top = mTextHeight.toFloat()
        rectF2.right = this.maxPx.toFloat()
        rectF2.bottom = mHeight.toFloat()
        invalidate()
    }

    fun getItemWidth(): Int {
        return mWidth
    }

    fun setOnScrollBorderListener(listener: OnScrollBorderListener?) {
        onScrollBorderListener = listener
    }

    fun getLeftInterval(): Float {
        return rectF.left
    }

    fun getRightInterval(): Float {
        return rectF2.right
    }

    fun setTipsText(tipsText: Array<String>?) {
        if (tipsText != null) {
            this.tipsText = tipsText
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mWidth == 0) {
            mWidth = width
            mHeight = height
            rectF.left = 0f
            rectF.top = mTextHeight.toFloat()
            rectF.right = 0 + rectWidth.toFloat()
            rectF.bottom = mHeight.toFloat()
            rectF2.left = maxPx - rectWidth.toFloat()
            rectF2.top = mTextHeight.toFloat()
            rectF2.right = maxPx.toFloat()
            rectF2.bottom = mHeight.toFloat()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        move(event)
        return scrollLeft || scrollRight
    }

    private fun move(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                if (downX > rectF.left - rectWidth / 2 && downX < rectF.right + rectWidth / 2) {
                    scrollLeft = true
                }
                if (downX > rectF2.left - rectWidth / 2 && downX < rectF2.right + rectWidth / 2) {
                    scrollRight = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                val scrollX = moveX - downX
                if (scrollLeft) {
                    rectF.left = rectF.left + scrollX
                    rectF.right = rectF.right + scrollX
                    if (rectF.left < 0) {
                        rectF.left = 0f
                        rectF.right = rectWidth.toFloat()
                    }
                    if (rectF.left > rectF2.right - minPx) {
                        rectF.left = rectF2.right - minPx
                        rectF.right = rectF.left + rectWidth
                    }
                    if (rectF.left < rectF2.right - maxPx) {
                        rectF.left = rectF2.right - maxPx
                        rectF.right = rectF.left + rectWidth
                    }
                    scrollChange = true
                    invalidate()
                } else if (scrollRight) {
                    rectF2.left = rectF2.left + scrollX
                    rectF2.right = rectF2.right + scrollX
                    if (rectF2.right > mWidth) {
                        rectF2.right = mWidth.toFloat()
                        rectF2.left = rectF2.right - rectWidth
                    }
                    if (rectF2.right < rectF.left + minPx) {
                        rectF2.right = rectF.left + minPx
                        rectF2.left = rectF2.right - rectWidth
                    }
                    if (rectF2.right > rectF.left + maxPx) {
                        rectF2.right = rectF.left + maxPx
                        rectF2.left = rectF2.right - rectWidth
                    }
                    scrollChange = true
                    invalidate()
                }
                if (onScrollBorderListener != null) {
                    tipsText = onScrollBorderListener!!.onScrollBorder(rectF.left, rectF2.right)
                }
                downX = moveX
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                downX = 0f
                scrollLeft = false
                scrollRight = false
                if (scrollChange && onScrollBorderListener != null) {
                    onScrollBorderListener!!.onScrollStateChange()
                }
                scrollChange = false
            }
        }
        return true
    }


}