
package com.example.pethome.storeapp.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public final class WindowDimensionsUtility {
    private WindowDimensionsUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    public static int getDisplayWindowWidth(Context context) {
        DisplayMetrics displayWindowMetrics = getDisplayWindowMetrics(context);
        if (displayWindowMetrics.widthPixels > 0) {
            return displayWindowMetrics.widthPixels;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getDisplayWindowHeight(Context context) {
        DisplayMetrics displayWindowMetrics = getDisplayWindowMetrics(context);
        if (displayWindowMetrics.heightPixels > 0) {
            return displayWindowMetrics.heightPixels;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    private static DisplayMetrics getDisplayWindowMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

}
