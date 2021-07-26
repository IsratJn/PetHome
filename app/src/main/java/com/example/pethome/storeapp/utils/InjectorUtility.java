
package com.example.pethome.storeapp.utils;

import android.content.Context;

import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.StoreFileRepository;
import com.example.pethome.storeapp.data.local.StoreLocalRepository;


public final class InjectorUtility {
    private InjectorUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    private static StoreLocalRepository provideLocalRepository(Context context) {
        return StoreLocalRepository.getInstance(context.getContentResolver(), AppExecutors.getInstance());
    }
    private static StoreFileRepository provideFileRepository(Context context) {
        return StoreFileRepository.getInstance(context.getContentResolver(), AppExecutors.getInstance());
    }
    public static StoreRepository provideStoreRepository(Context context) {
        return StoreRepository.getInstance(provideLocalRepository(context), provideFileRepository(context));
    }

}
