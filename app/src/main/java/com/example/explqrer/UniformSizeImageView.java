package com.example.explqrer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Creates a specific size to crop the galley images to
 */

public class UniformSizeImageView extends androidx.appcompat.widget.AppCompatImageView {

    public UniformSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();
        setMeasuredDimension(widthMeasureSpec,widthMeasureSpec);

    }
}
