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
            // ceil not round - avoid thin vertical gaps along the left/right edges
           // int width = MeasureSpec.getSize(widthMeasureSpec);
            //int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(350,350 );
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
