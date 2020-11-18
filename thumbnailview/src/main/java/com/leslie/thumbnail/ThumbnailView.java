package com.leslie.thumbnail;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Author by haonan, Date on 2020/11/18.
 * Email:278913810@qq.com
 * PS:
 */
public class ThumbnailView extends View {
    boolean scrollChange;
    private int mWidth;
    private int mHeight;
    private int mTextHeight;
    private int mTextWidth;
    //    private int mMarginHorizontal;
    private int mTipsWidth;
    private int mTipsHeight;
    private Paint mPaint;
    private Paint mTextPaint;
    private RectF rectF;//左边拖动条的范围
    private RectF rectF2;//右边拖动条的范围
    private int rectWidth;//拖动条宽度
    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;
    private Bitmap bitmapText;
    private Bitmap bitmapDownTips;
    private OnScrollBorderListener onScrollBorderListener;
    private int minPx;
    private int maxPx;
    private float downX;
    private boolean scrollLeft;
    private boolean scrollRight;
    private int paintStrokeWidth;

    private String[] tipsText;

    public ThumbnailView(Context context) {
        super(context);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttrs(attrs);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(attrs);
        init();
    }

    private void readAttrs(@Nullable AttributeSet attrs) {
        //获取xml中数据
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ThumbnailView);
        float tipsTextSize = typedArray.getDimension(R.styleable.ThumbnailView_tipsTextSize, 12);
//        name = typedArray.getString(R.styleable.LocationViewWithAttrs_locationName);
//        address = typedArray.getString(R.styleable.LocationViewWithAttrs_locationDesc);
        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        paintStrokeWidth = (int) getResources().getDimension(R.dimen.paintStrokeWidth);
        mPaint.setStrokeWidth(paintStrokeWidth);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.paintTextSize));
        mTextPaint.setColor(getContext().getColor(R.color.paintTextColor));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        bitmapLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.video_thumbnail_left);
        bitmapRight = BitmapFactory.decodeResource(getResources(), R.mipmap.video_thumbnail_right);
//        bitmapText = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit_drop_down);
        bitmapDownTips = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit_drop_down);

        rectWidth = (int) getResources().getDimension(R.dimen.scrollBarWidth);
        mTextHeight = (int) getResources().getDimension(R.dimen.textRectHeight);
        mTextWidth = (int) getResources().getDimension(R.dimen.textRectWidth);
        mTipsWidth = (int) getResources().getDimension(R.dimen.downTipsWidth);
        mTipsHeight = (int) getResources().getDimension(R.dimen.downTipsHeight);

        minPx = rectWidth;

        rectF = new RectF();
        rectF2 = new RectF();
        rectF3 = new RectF();
        rectF4 = new RectF();
        rectF5 = new RectF();
        rectF6 = new RectF();
        rectF7 = new RectF();
        rectF8 = new RectF();

        tipsText = new String[]{"", ""};
        Log.d("测试", "测量前宽度=" + getWidth());
    }

    public void setMinInterval(int minPx) {
        if (mWidth > 0 && minPx > mWidth) {
            minPx = mWidth;
        }
        this.minPx = minPx;
    }

    public void setMaxInterval(int maxPx) {
        if (mWidth > 0 && maxPx > mWidth) {
            minPx = mWidth;
        }
        this.maxPx = maxPx;
        //刷新右边滑块位置
        rectF2.left = this.maxPx - rectWidth;
        rectF2.top = mTextHeight;
        rectF2.right = this.maxPx;
        rectF2.bottom = mHeight;
        invalidate();
    }

    public int getItemWidth() {
        return mWidth;
    }

    public void setOnScrollBorderListener(OnScrollBorderListener listener) {
        this.onScrollBorderListener = listener;
    }

    public float getLeftInterval() {
        return rectF.left;
    }

    public float getRightInterval() {
        return rectF2.right;
    }

    public void setTipsText(String[] tipsText) {
        this.tipsText = tipsText;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            rectF.left = 0;
            rectF.top = mTextHeight;
            rectF.right = 0 + rectWidth;
            rectF.bottom = mHeight;

            rectF2.left = maxPx - rectWidth;
            rectF2.top = mTextHeight;
            rectF2.right = maxPx;
            rectF2.bottom = mHeight;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        move(event);
        return scrollLeft || scrollRight;
    }

    private boolean move(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                if (downX > rectF.left - rectWidth / 2 && downX < rectF.right + rectWidth / 2) {
                    scrollLeft = true;
                }
                if (downX > rectF2.left - rectWidth / 2 && downX < rectF2.right + rectWidth / 2) {
                    scrollRight = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();

                float scrollX = moveX - downX;

                if (scrollLeft) {
                    rectF.left = rectF.left + scrollX;
                    rectF.right = rectF.right + scrollX;

                    if (rectF.left < 0) {
                        rectF.left = 0;
                        rectF.right = rectWidth;
                    }
                    if (rectF.left > rectF2.right - minPx) {
                        rectF.left = rectF2.right - minPx;
                        rectF.right = rectF.left + rectWidth;
                    }
                    if (rectF.left < rectF2.right - maxPx) {
                        rectF.left = rectF2.right - maxPx;
                        rectF.right = rectF.left + rectWidth;
                    }
                    scrollChange = true;
                    invalidate();
                } else if (scrollRight) {
                    rectF2.left = rectF2.left + scrollX;
                    rectF2.right = rectF2.right + scrollX;

                    if (rectF2.right > mWidth) {
                        rectF2.right = mWidth;
                        rectF2.left = rectF2.right - rectWidth;
                    }
                    if (rectF2.right < rectF.left + minPx) {
                        rectF2.right = rectF.left + minPx;
                        rectF2.left = rectF2.right - rectWidth;
                    }
                    if (rectF2.right > rectF.left + maxPx) {
                        rectF2.right = rectF.left + maxPx;
                        rectF2.left = rectF2.right - rectWidth;
                    }
                    scrollChange = true;
                    invalidate();
                }

                if (onScrollBorderListener != null) {
                    tipsText = onScrollBorderListener.onScrollBorder(rectF.left, rectF2.right);
                }


                downX = moveX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                downX = 0;
                scrollLeft = false;
                scrollRight = false;
                if (scrollChange && onScrollBorderListener != null) {
                    onScrollBorderListener.onScrollStateChange();
                }
                scrollChange = false;
                break;
        }
        return true;
    }

    private RectF rectF3;//左边阴影区域
    private RectF rectF4;//右边阴影区域
    private RectF rectF5;//左边的时间提示器
    private RectF rectF6;//右边的时间提示器
    private RectF rectF7;//左边的向下箭头
    private RectF rectF8;//右边的向下箭头

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(getContext().getColor(R.color.thumbnailColor));
        //左边的拖动条
        canvas.drawBitmap(bitmapLeft, null, rectF, mPaint);
        //右边的拖动条
        canvas.drawBitmap(bitmapRight, null, rectF2, mPaint);
        //顶部和底部的两条红线
        canvas.drawLine(rectF.left + paintStrokeWidth, mTextHeight + paintStrokeWidth / 2, rectF2.right - paintStrokeWidth, mTextHeight + paintStrokeWidth / 2, mPaint);
        canvas.drawLine(rectF.left + paintStrokeWidth, mHeight - paintStrokeWidth / 2, rectF2.right - paintStrokeWidth, mHeight - paintStrokeWidth / 2, mPaint);

        mPaint.setColor(Color.parseColor("#99313133"));
        //左边的阴影
        rectF3.left = 0;
        rectF3.top = mTextHeight + paintStrokeWidth;
        rectF3.right = rectF.left;
        rectF3.bottom = mHeight - paintStrokeWidth;
        canvas.drawRect(rectF3, mPaint);
        //右边的阴影
        rectF4.left = rectF2.right;
        rectF4.top = mTextHeight + paintStrokeWidth;
        rectF4.right = mWidth;
        rectF4.bottom = mHeight - paintStrokeWidth;
        canvas.drawRect(rectF4, mPaint);

        //左边文字背景
        rectF5.left = rectF.left - mTextWidth / 2;
        rectF5.right = rectF.left + mTextWidth / 2;
        rectF5.bottom = rectF.top;
        rectF5.top = 0;
//        canvas.drawBitmap(bitmapText, null, rectF5, mPaint);
        //左边向下箭头
        rectF7.left = rectF5.left + mTextWidth / 2 - mTipsWidth / 2;
        rectF7.top = rectF5.bottom - mTipsHeight * 2;//向上移一个tips的距离
        rectF7.right = rectF7.left + mTipsWidth;
        rectF7.bottom = rectF5.bottom - mTipsHeight;
        canvas.drawBitmap(bitmapDownTips, null, rectF7, mPaint);
        //左边文字
        canvas.drawText(tipsText[0], rectF5.left + (mTextWidth / 2), rectF5.top + (mTextHeight / 2), mTextPaint);

        //右边文字背景
        rectF6.left = rectF2.right - mTextWidth / 2;
        rectF6.right = rectF2.right + mTextWidth / 2;
        rectF6.bottom = rectF2.top;
        rectF6.top = 0;
//        canvas.drawBitmap(bitmapText, null, rectF6, mPaint);
        //左边向下箭头
        rectF8.left = rectF6.left + mTextWidth / 2 - mTipsWidth / 2;
        rectF8.top = rectF6.bottom - mTipsHeight * 2;//向上移一个tips的距离
        rectF8.right = rectF8.left + mTipsWidth;
        rectF8.bottom = rectF6.bottom - mTipsHeight;
        canvas.drawBitmap(bitmapDownTips, null, rectF8, mPaint);
        //右边文字
        canvas.drawText(tipsText[1], rectF6.left + (mTextWidth / 2), rectF6.top + (mTextHeight / 2), mTextPaint);
    }

}
