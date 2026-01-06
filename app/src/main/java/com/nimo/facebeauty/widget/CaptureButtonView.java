package com.nimo.facebeauty.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.nimo.facebeauty.R;

public class CaptureButtonView extends View implements View.OnTouchListener {

    private static final String TAG = "CaptureButtonView";

    /**
     * 画笔对象
     */
    private Paint paint;
    private Paint mPaintFill;
    private RectF mOvalRectF;
    private int mShadowPadding;

    /**
     * btn 绘制的宽度
     */
    private int drawWidth = 0;

    /**
     * 圆环的颜色
     */
    private int ringColor = Color.WHITE;

    /**
     * 圆环进度的颜色
     */
    private int criclesecondColor;

    /**
     * 圆环的宽度
     */
    private float ringWidth = 0f;

    /**
     * 最大进度
     */
    private long max = 10000L;

    /**
     * 当前进度
     */
    private long mSecond = 0;
    private long mStartTimestamp = 0;

    private volatile boolean mIsLongClick = false;
    private final int mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
    private OnRecordListener mOnRecordListener;
    private Handler mHandler = new Handler();

    public CaptureButtonView(Context context) {
        this(context, null);
    }

    public CaptureButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        criclesecondColor = context.getResources().getColor(R.color.theme_color);
        setOnTouchListener(this);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);

        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(Color.parseColor("#47FFFFFF"));

        mShadowPadding = (int) (getResources().getDisplayMetrics().density * 2 + 0.5f);
        paint.setShadowLayer(mShadowPadding, 0, 0, Color.parseColor("#802D2D2D"));

        mOvalRectF = new RectF();
        Log.d(TAG, "RecordBtn: mLongPressTimeout " + mLongPressTimeout);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawWidth <= 0) {
            drawWidth = getWidth();
        }
        ringWidth = 15f * drawWidth / 228f;

        int centreX = getWidth() / 2;
        int centreY = getHeight() - drawWidth / 2;
        int radius = (int) (drawWidth / 2 - ringWidth / 2) - mShadowPadding;

        paint.setColor(ringColor);
        paint.setStrokeWidth(ringWidth);
        canvas.drawCircle(centreX, centreY, radius, mPaintFill);
        canvas.drawCircle(centreX, centreY, radius, paint);

        // 画进度
        paint.setStrokeWidth(ringWidth * 0.75f);
        paint.setColor(criclesecondColor);
        mOvalRectF.set(centreX - radius, centreY - radius, centreX + radius, centreY + radius);
        canvas.drawArc(mOvalRectF, 270f, 360f * mSecond / max, false, paint);

        if (mSecond >= max && mOnRecordListener != null) {
            mOnRecordListener.stopRecord();
        }
    }

    /**
     * 设置进度最大值
     */
    public synchronized void setMax(int max) {
        if (max < 0) throw new IllegalArgumentException("max not less than 0");
        this.max = max;
    }

    /**
     * 获取进度
     */
    public synchronized long getSecond() {
        return mSecond;
    }

    /**
     * 设置进度
     */
    public synchronized void setSecond(long second) {
        if (second < 0) throw new IllegalArgumentException("mSecond not less than 0");
        if (second >= max) {
            mSecond = max;
        } else {
            mSecond = second;
        }
        postInvalidate();
    }

    public int getCricleColor() {
        return ringColor;
    }

    public void setCricleColor(int cricleColor) {
        this.ringColor = cricleColor;
        invalidate();
    }

    public float getRingWidth() {
        return ringWidth;
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
        invalidate();
    }

    public int getDrawWidth() {
        return drawWidth;
    }

    public void setDrawWidth(int drawWidth) {
        this.drawWidth = drawWidth;
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOnRecordListener != null) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mStartTimestamp = System.currentTimeMillis();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsLongClick = true;
                        mOnRecordListener.startRecord();
                    }
                }, 500);
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - mStartTimestamp < 500) {
                    mHandler.removeCallbacksAndMessages(null);
                    mOnRecordListener.takePic();
                } else if (mIsLongClick) {
                    mOnRecordListener.stopRecord();
                }
                mIsLongClick = false;
                mStartTimestamp = 0;
                return true;
            }
        }
        return false;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.mOnRecordListener = onRecordListener;
    }

    public interface OnRecordListener {
        void takePic();
        void startRecord();
        void stopRecord();
    }
}

