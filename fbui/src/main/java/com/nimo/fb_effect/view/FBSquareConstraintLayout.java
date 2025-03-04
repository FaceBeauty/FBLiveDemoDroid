package com.nimo.fb_effect.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 贴纸等资源的布局控件
 */
public class FBSquareConstraintLayout extends ConstraintLayout {
    public FBSquareConstraintLayout(Context context) {
        super(context);
    }

    public FBSquareConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FBSquareConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
