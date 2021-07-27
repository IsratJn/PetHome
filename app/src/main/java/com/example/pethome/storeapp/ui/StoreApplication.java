

package com.example.pethome.storeapp.ui;

import android.app.Application;
import android.content.Context;

import com.example.pethome.storeapp.utils.AppConstants;
import com.facebook.stetho.Stetho;

public class StoreApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (AppConstants.LOG_STETHO) {
            initializeStetho(this);
        }
    }

    private void initializeStetho(final Context context) {
        Stetho.initializeWithDefaults(context);
    }
}
