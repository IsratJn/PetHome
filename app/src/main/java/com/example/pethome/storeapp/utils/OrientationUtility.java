
package com.example.pethome.storeapp.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;

public final class OrientationUtility {
    private OrientationUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    public static boolean isDeviceInPortraitMode(Context context) {
        int screenOrientation = context.getResources().getConfiguration().orientation;

        return (screenOrientation == Configuration.ORIENTATION_PORTRAIT);
    }
    public static void lockCurrentScreenOrientation(FragmentActivity activity) {
        int screenOrientation = activity.getResources().getConfiguration().orientation;

        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }
    public static void unlockScreenOrientation(FragmentActivity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
