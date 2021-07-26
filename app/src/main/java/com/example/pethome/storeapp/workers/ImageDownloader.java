package com.example.pethome.storeapp.workers;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;
import com.example.pethome.storeapp.cache.BitmapImageCache;
import com.example.pethome.storeapp.utils.ImageStorageUtility;
import java.io.IOException;
public class ImageDownloader extends AsyncTaskLoader<Bitmap> {
    final static int IMAGE_LOADER = 1000;
    private static final String LOG_TAG = ImageDownloader.class.getSimpleName();
    private String mImageURLStr;

    private Bitmap mDownloadedBitmap;
    ImageDownloader(Context context, String imageURLStr) {
        super(context);
        mImageURLStr = imageURLStr;
    }
    @Override
    public Bitmap loadInBackground() {
        if (!TextUtils.isEmpty(mImageURLStr)) {
            try {
                Bitmap cachedBitmap = BitmapImageCache.getBitmapFromCache(mImageURLStr);
                if (cachedBitmap != null) {
                    return cachedBitmap;
                } else {
                    Bitmap downloadedBitmap = ImageStorageUtility.getOptimizedBitmapFromContentUri(getContext(), Uri.parse(mImageURLStr));
                    if (downloadedBitmap != null) {
                        downloadedBitmap.prepareToDraw();
                        BitmapImageCache.addBitmapToCache(mImageURLStr, downloadedBitmap);
                        return downloadedBitmap;
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "loadInBackground: Failed while downloading the bitmap for the URI " + mImageURLStr, e);
            }
        }
        return null;
    }
    @Override
    public void deliverResult(Bitmap newBitmap) {
        if (isReset()) {
            newBitmap = null;
            return;
        }
        Bitmap oldBitmap = mDownloadedBitmap;
        mDownloadedBitmap = newBitmap;
        if (isStarted()) {
            super.deliverResult(newBitmap);
        }
        if (oldBitmap != null && oldBitmap != newBitmap) {
            oldBitmap = null;
        }
    }
    @Override
    protected void onStartLoading() {
        if (mDownloadedBitmap != null) {
            deliverResult(mDownloadedBitmap);
        }
        if (takeContentChanged() || mDownloadedBitmap == null) {
            forceLoad();
        }
    }
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
    @Override
    protected void onReset() {
        onStopLoading();
        releaseResources();
    }
    @Override
    public void onCanceled(Bitmap data) {
        super.onCanceled(data);
        releaseResources();
    }
    private void releaseResources() {
        if (mDownloadedBitmap != null) {
            mDownloadedBitmap = null;
            mImageURLStr = null;
        }
    }
    String getImageURLStr() {
        return mImageURLStr;
    }
}
