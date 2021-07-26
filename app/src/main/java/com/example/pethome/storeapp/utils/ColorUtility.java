
package com.example.pethome.storeapp.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

public final class ColorUtility {
    private ColorUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    public static int[] obtainColorsFromTypedArray(Context context, @ArrayRes int colorArrayRes, @ColorRes int defaultColorRes) {
        TypedArray typedArrayColors = context.getResources().obtainTypedArray(colorArrayRes);
        int noOfColors = typedArrayColors.length();
        int[] colors = new int[noOfColors];
        int defaultColorInt = ContextCompat.getColor(context, defaultColorRes);
        for (int index = 0; index < noOfColors; index++) {
            colors[index] = typedArrayColors.getColor(index, defaultColorInt);
        }
        typedArrayColors.recycle();
        return colors;
    }
}
