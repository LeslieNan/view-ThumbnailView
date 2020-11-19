package com.leslie.thumbnail

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.leslie.thumbnail.util.DisplayUtil


/**
 * Author by haonan, Date on 2020/11/18.
 * Email:278913810@qq.com
 * PS:
 */
class ThumbnailView : View {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    private var scrollChange = false
    private var mWidth = 0f
    private var mHeight = 0f
    private var mTextBgHeight = 0f//文字区域高度
    private var mTextBgWidth = 0f//文字区域宽度

    private var mTipsWidth = 0f//文字区域小箭头宽度
    private var mTipsHeight = 0f//文字区域小箭头高度
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
    private var rectWidth = 0f //拖动条宽度

    private lateinit var bitmapLeft: Bitmap
    private lateinit var bitmapRight: Bitmap
    private lateinit var bitmapTipsBg: Bitmap
    private lateinit var bitmapDownTips: Bitmap
    private var onScrollBorderListener: OnScrollBorderListener? = null
    private var minPx = 0f
    private var maxPx = 0f
    private var downX = 0f
    private var scrollLeft = false
    private var scrollRight = false
    private var paintStrokeWidth = 0f//两条横线宽度
    private var tipsText: Array<String> = arrayOf("", "")

    private fun init() {
        mPaint.isAntiAlias = true
        paintStrokeWidth = resources.getDimension(R.dimen.paintStrokeWidth)
        mPaint.strokeWidth = paintStrokeWidth

        mTextPaint.textSize = resources.getDimension(R.dimen.paintTextSize)
        mTextPaint.color = context.getColor(R.color.paintTextColor)
        mTextPaint.textAlign = Paint.Align.CENTER

        rectWidth = resources.getDimension(R.dimen.scrollBarWidth)
        mTextBgHeight = resources.getDimension(R.dimen.textRectHeight)//需要根据xml传来
        mTextBgWidth = resources.getDimension(R.dimen.textRectWidth)//需要根据xml传进来
        mTipsWidth = resources.getDimension(R.dimen.downTipsWidth)
        mTipsHeight = resources.getDimension(R.dimen.downTipsHeight)

        minPx = rectWidth
    }

    /**
     * 获取根据颜色，大小生成bitmap
     */
    private fun initBitmap() {
        bitmapLeft = DisplayUtil.vectorToBitmap(
            context, R.drawable.icon_scroll_bar_left,
            context.resources.getDimension(R.dimen.scrollBarWidth).toInt(),
            mHeight.toInt()
        )
        bitmapRight = DisplayUtil.vectorToBitmap(
            context, R.drawable.icon_scroll_bar_right,
            context.resources.getDimension(R.dimen.scrollBarWidth).toInt(),
            mHeight.toInt()
        )
        bitmapTipsBg = DisplayUtil.vectorToBitmap(
            context, R.drawable.bg_pop,
            mTextBgHeight.toInt(), mTextBgHeight.toInt()
        )
        bitmapDownTips = DisplayUtil.vectorToBitmap(
            context, R.drawable.bg_drop_down,
            mTipsWidth.toInt(), mTipsHeight.toInt()
        )
    }


    private fun readAttrs(attrs: AttributeSet?) {
        //获取xml中数据
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThumbnailView)
        val tipsTextSize = typedArray.getDimension(R.styleable.ThumbnailView_tipsTextSize, 12f)
        //        name = typedArray.getString(R.styleable.LocationViewWithAttrs_locationName);
//        address = typedArray.getString(R.styleable.LocationViewWithAttrs_locationDesc);
        typedArray.recycle()
    }

    /**
     * 设置滑动最小最大间隔
     */
    fun setInterval(minIntervalPx: Float, maxIntervalPx: Float) {
        var minPx = minIntervalPx
        var maxPx = maxIntervalPx
        if (minPx > maxPx) {
            val temp = minPx
            minPx = maxPx
            maxPx = temp
        }
        post {
            refreshInterval(minPx, maxPx)
        }
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
        tipsText?.let {
            this.tipsText = it
            invalidate()
        }
    }

    /**
     * 刷新间隔
     */
    private fun refreshInterval(minPx: Float, maxPx: Float) {
        Log.d("测试", "mwidth=$mWidth")
        this.minPx = when {
            minPx <= 0 -> 0f
            (mWidth != 0f && minPx > mWidth) -> mWidth
            else -> minPx
        }
        this.maxPx = when {
            maxPx <= 0 -> 0f
            (mWidth != 0f && maxPx > mWidth) -> mWidth
            else -> maxPx
        }
        Log.d("测试", "minPx=${this.minPx}")
        Log.d("测试", "maxPx=${this.maxPx}")
        //刷新左边滑块位置
        rectF.left = 0f
        rectF.top = mTextBgHeight + mTipsHeight
        rectF.right = rectWidth
        rectF.bottom = mHeight
        //刷新右边滑块位置
        rectF2.left = this.maxPx - rectWidth
        rectF2.top = mTextBgHeight + mTipsHeight
        rectF2.right = this.maxPx
        rectF2.bottom = mHeight
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mWidth == 0f) {
            mWidth = width.toFloat()
            mHeight = height.toFloat()
            rectF.left = 0f
            rectF.top = mTextBgHeight + mTipsHeight
            rectF.right = 0 + rectWidth
            rectF.bottom = mHeight
            rectF2.left = maxPx - rectWidth
            rectF2.top = mTextBgHeight + mTipsHeight
            rectF2.right = maxPx
            rectF2.bottom = mHeight
            //若未设置maxPx，则默认maxPx为控件宽度
            if (this.maxPx == 0f) {
                refreshInterval(0f, mWidth)
            }
            initBitmap()
        }
        Log.d("测试", "onLayout")
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { move(it) }
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
                        rectF.right = rectWidth
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
                        rectF2.right = mWidth
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
                onScrollBorderListener?.let {
                    tipsText = it.onScrollBorder(rectF.left, rectF2.right)
                }
                downX = moveX
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                downX = 0f
                scrollLeft = false
                scrollRight = false
                if (scrollChange && onScrollBorderListener != null) {
                    onScrollBorderListener?.onScrolled()
                }
                scrollChange = false
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = context.getColor(R.color.thumbnailColor)
        //左边的拖动条
        canvas.drawBitmap(bitmapLeft, null, rectF, mPaint)
        //右边的拖动条
        canvas.drawBitmap(bitmapRight, null, rectF2, mPaint)
        //顶部和底部的两条红线
        canvas.drawLine(
            rectF.left + paintStrokeWidth,
            rectF.top + paintStrokeWidth / 2,
            rectF2.right - paintStrokeWidth,
            rectF2.top + paintStrokeWidth / 2,
            mPaint
        )
        canvas.drawLine(
            rectF.left + paintStrokeWidth,
            mHeight - paintStrokeWidth / 2,
            rectF2.right - paintStrokeWidth,
            mHeight - paintStrokeWidth / 2,
            mPaint
        )
        mPaint.color = context.getColor(R.color.shadowColor)
        //左边的阴影
        rectF3.left = 0f
        rectF3.top = rectF.top + paintStrokeWidth
        rectF3.right = rectF.left
        rectF3.bottom = mHeight - paintStrokeWidth
        canvas.drawRect(rectF3, mPaint)
        //右边的阴影
        rectF4.left = rectF2.right
        rectF4.top = rectF2.top + paintStrokeWidth
        rectF4.right = mWidth
        rectF4.bottom = mHeight - paintStrokeWidth
        canvas.drawRect(rectF4, mPaint)

        //左边文字背景区域
        rectF5.left = if ((rectF.left - mTextBgWidth / 2) > 0) rectF.left - mTextBgWidth / 2 else 0F
        rectF5.right = rectF5.left + mTextBgWidth
        rectF5.bottom = rectF.top - mTipsHeight
        rectF5.top = 0f
        canvas.drawBitmap(bitmapTipsBg, null, rectF5, mPaint)
        //左边向下箭头
        rectF7.left = (rectF.left - mTextBgWidth / 2) + mTextBgWidth / 2 - mTipsWidth / 2
        rectF7.top = rectF5.bottom
        rectF7.right = rectF7.left + mTipsWidth
        rectF7.bottom = rectF.top
        canvas.drawBitmap(bitmapDownTips, null, rectF7, mPaint)
        //左边文字
        canvas.drawText(
            tipsText[0],
            rectF5.left + mTextBgWidth / 2,
            rectF5.top + mTextBgHeight / 2,
            mTextPaint
        )

        //右边文字背景区域
        rectF6.right =
            if ((rectF2.right + mTextBgWidth / 2) < mWidth) rectF2.right + mTextBgWidth / 2 else mWidth
        rectF6.left = rectF6.right - mTextBgWidth
        rectF6.bottom = rectF2.top - mTipsHeight
        rectF6.top = 0f
        canvas.drawBitmap(bitmapTipsBg, null, rectF6, mPaint)
        //右边向下箭头
        rectF8.left = rectF2.right - mTipsWidth / 2
        rectF8.top = rectF6.bottom
        rectF8.right = rectF8.left + mTipsWidth
        rectF8.bottom = rectF2.top
        canvas.drawBitmap(bitmapDownTips, null, rectF8, mPaint)
        //右边文字
        canvas.drawText(
            tipsText[1],
            rectF6.left + mTextBgWidth / 2,
            rectF6.top + mTextBgHeight / 2,
            mTextPaint
        )
    }


}