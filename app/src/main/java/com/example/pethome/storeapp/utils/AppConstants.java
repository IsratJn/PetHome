
package com.example.pethome.storeapp.utils;

import com.example.pethome.storeapp.BuildConfig;

public final class AppConstants {
    private AppConstants() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }

    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;

    public static final boolean LOG_CURSOR_QUERIES = BuildConfig.LOG_CURSOR_QUERIES;

    public static final boolean LOG_STETHO = BuildConfig.LOG_STETHO;

    public static final int ANIMALSS_LOADER = 1;

    public static final int RESCUERS_LOADER = 2;

    public static final int ADOPTIONS_LOADER = 3;
}
