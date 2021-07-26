/*
 * Copyright 2018 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http:
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pethome.storeapp.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapImageCache {

    private static final int DEFAULT_CACHE_SIZE = 25 * 1024 * 1024;
    private static volatile BitmapImageCache INSTANCE;
    private LruCache<String, Bitmap> mMemoryCache;

    private BitmapImageCache() {
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int maxMemoryThreshold = maxMemory / 8;
        final int cacheSizeSelected = DEFAULT_CACHE_SIZE > maxMemoryThreshold ? maxMemoryThreshold : DEFAULT_CACHE_SIZE;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSizeSelected) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }

        };
    }

    private static BitmapImageCache getInstance() {
        if (INSTANCE == null) {
            synchronized (BitmapImageCache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BitmapImageCache();
                }
            }
        }
        return INSTANCE;
    }
    public static Bitmap getBitmapFromCache(String imageURLStr) {
        return getInstance().mMemoryCache.get(imageURLStr);
    }

    public static void addBitmapToCache(String imageURLStr, Bitmap bitmap) {
        if (getBitmapFromCache(imageURLStr) == null
                && bitmap != null) {
            getInstance().mMemoryCache.put(imageURLStr, bitmap);
        }
    }
    public static void clearCache() {
        getInstance().mMemoryCache.evictAll();
    }
}
