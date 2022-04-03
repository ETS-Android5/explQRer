package com.example.explqrer;
import android.graphics.Bitmap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by John on 07-Sep-15.
 * Source: http://xperience57.blogspot.com/2015/09/android-saving-bitmap-as-serializable.html
 */
public class ProxyBitmap implements Serializable {
    private final ArrayList<Integer> pixels;
    private final int width , height;

    public ProxyBitmap(Bitmap bitmap){
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int [] pix = new int [width*height];
        bitmap.getPixels(pix,0,width,0,0,width,height);

        /*
         * Source: stackoverflow.com
         * Link: https://stackoverflow.com/questions/1073919/how-to-convert-int-into-listinteger-in-java#comment50356300_1073933
         * Author: user4910279 (no link)
         */
        pixels = new ArrayList<Integer>() {{ for (int i : pix) add(i); }};
    }

    public Bitmap getBitmap(){
        /*
         * Source: stackoverflow.com
         * Link: https://stackoverflow.com/a/23688547
         * Author: https://stackoverflow.com/users/1587046/alexis-c
         */
        int[] pix = pixels.stream().mapToInt(i -> i).toArray();

        return Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
    }
}