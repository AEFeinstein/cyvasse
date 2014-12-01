package com.gelakinetic.cyvasse.uiHelpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.gelakinetic.cyvasse.R;

public class MaxWidthButton extends Button {
    private final int mMaxWidth;

    public MaxWidthButton(Context context) {
        super(context);
        mMaxWidth = 0;
    }

    public MaxWidthButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaxWidthButton);
        mMaxWidth = a.getDimensionPixelSize(R.styleable.MaxWidthButton_maxWidth, Integer.MAX_VALUE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (mMaxWidth > 0 && mMaxWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, measureMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}