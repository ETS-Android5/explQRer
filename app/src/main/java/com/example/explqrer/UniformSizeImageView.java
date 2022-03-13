package com.example.explqrer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;


public class UniformSizeImageView extends androidx.appcompat.widget.AppCompatImageView {

    public UniformSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();

        if(d!=null){
            setMeasuredDimension(350,350 );
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
