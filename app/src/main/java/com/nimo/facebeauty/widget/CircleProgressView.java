package com.nimo.facebeauty.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {
    private Paint paint;
    private float progress = 0f; // 0 ~ 360

    public CircleProgressView(Context context) {
        super(context);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#38A8FF"));
        paint.setStrokeWidth(16);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = Math.min(getWidth(), getHeight()) / 2f - 8;
        canvas.drawArc(
                new RectF(getWidth()/2 - radius, getHeight()/2 - radius,
                        getWidth()/2 + radius, getHeight()/2 + radius),
                -90, progress, false, paint);
    }
}

