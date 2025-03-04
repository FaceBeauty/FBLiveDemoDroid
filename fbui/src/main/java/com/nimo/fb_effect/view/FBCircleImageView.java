package com.nimo.fb_effect.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.nimo.fb_effect.R;



public class FBCircleImageView extends AppCompatImageView {

    private float radius;
    private Path path;

    public FBCircleImageView(Context context) {
        this(context, null);
    }

    public FBCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FBCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        path = new Path();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HtCircleImageView);
        radius = ta.getDimension(R.styleable.HtCircleImageView_riv_circle, 0);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取视图的最小尺寸（宽度或高度），以确保是圆形
        int diameter = Math.min(getWidth(), getHeight());

        // 如果设置了特定的半径，则使用它；否则，默认使用直径的一半作为半径
        float r = radius > 0 ? radius : diameter / 2f;

        // 创建一个圆形路径
        path.reset();
        path.addCircle(diameter / 2f, diameter / 2f, r, Path.Direction.CW);

        // 裁剪画布
        canvas.clipPath(path);

        // 调整图像绘制区域以匹配圆形区域
        canvas.translate((getWidth() - diameter) / 2f, (getHeight() - diameter) / 2f);
        canvas.scale(diameter / (float)getWidth(), diameter / (float)getHeight());

        // 绘制
        super.onDraw(canvas);
    }
}
