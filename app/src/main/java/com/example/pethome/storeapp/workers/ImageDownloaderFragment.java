package com.example.pethome.storeapp.workers;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.ImageView;
import com.example.pethome.storeapp.R;

public class ImageDownloaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Bitmap> {
    private static final String LOG_TAG = ImageDownloaderFragment.class.getSimpleName();
    private ImageView mImageView;
    private String mImageURLStr;
    private OnFailureListener mOnFailureListener;
    private OnSuccessListener mOnSuccessListener;
    public static ImageDownloaderFragment newInstance(FragmentManager fragmentManager, int tagId) {
        String fragmentTagStr = LOG_TAG + "_" + tagId;
        ImageDownloaderFragment imageDownloaderFragment
                = (ImageDownloaderFragment) fragmentManager.findFragmentByTag(fragmentTagStr);
        if (imageDownloaderFragment == null) {
            imageDownloaderFragment = new ImageDownloaderFragment();
            fragmentManager.beginTransaction().add(imageDownloaderFragment, fragmentTagStr).commitAllowingStateLoss();
        }
        return imageDownloaderFragment;
    }
    public void executeAndUpdate(ImageView imageView, String imageURLStr, int loaderId) {
        executeAndUpdate(imageView, imageURLStr, loaderId, obtainLoaderManager(imageView));
    }
    public void executeAndUpdate(ImageView imageView, String imageURLStr, int loaderId, LoaderManager loaderManager) {
        mImageView = imageView;
        mImageURLStr = imageURLStr;
        loaderId += ImageDownloader.IMAGE_LOADER;
        if (loaderManager == null) {
            throw new IllegalStateException("LoaderManager is not attached.");
        }
        ImageDownloader imageDownloader = getImageDownloader(loaderId, loaderManager);
        mImageView.setImageResource(R.drawable.ic_all_animal_default);
        boolean isNewImageURLStr = false;
        if (imageDownloader != null) {
            isNewImageURLStr = TextUtils.isEmpty(mImageURLStr) || !mImageURLStr.equals(imageDownloader.getImageURLStr());
        }
        if (isNewImageURLStr) {
            loaderManager.restartLoader(loaderId, null, this);
        } else {
            loaderManager.initLoader(loaderId, null, this);
        }
    }
    @Nullable
    private ImageDownloader getImageDownloader(int loaderId, LoaderManager loaderManager) {
        Loader<Bitmap> loader = loaderManager.getLoader(loaderId);
        if (loader instanceof ImageDownloader) {
            return (ImageDownloader) loader;
        } else {
            return null;
        }
    }
    @Nullable
    private FragmentActivity obtainActivity(@Nullable Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof FragmentActivity) {
            return (FragmentActivity) context;
        } else if (context instanceof ContextWrapper) {
            return obtainActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
    @Nullable
    private LoaderManager obtainLoaderManager(ImageView imageView) {
        FragmentActivity activity = obtainActivity(imageView.getContext());
        if (activity != null) {
            return activity.getSupportLoaderManager();
        }
        return null;
    }
    @NonNull
    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        return new ImageDownloader(mImageView.getContext(), mImageURLStr);
    }
    @Override
    public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap bitmapImage) {
        if (bitmapImage != null && mImageView != null) {
            onDownloadSuccess(bitmapImage);
        } else if (mImageView != null) {
            onDownloadFailure();
        }
    }
    private void onDownloadFailure() {
        mImageView.setImageResource(R.drawable.ic_all_animal_default);
        if (mOnFailureListener != null) {
            mOnFailureListener.onFailure();
        }
    }
    private void onDownloadSuccess(Bitmap bitmapImage) {
        mImageView.setImageBitmap(bitmapImage);
        if (mOnSuccessListener != null) {
            mOnSuccessListener.onSuccess(bitmapImage);
        }
    }
    @Override
    public void onLoaderReset(@NonNull Loader<Bitmap> loader) {
        mImageView.setImageResource(R.drawable.ic_all_animal_default);
    }
    public ImageDownloaderFragment setOnFailureListener(OnFailureListener listener) {
        mOnFailureListener = listener;
        return this;
    }
    public ImageDownloaderFragment setOnSuccessListener(OnSuccessListener listener) {
        mOnSuccessListener = listener;
        return this;
    }
    public interface OnFailureListener {
        void onFailure();
    }
    public interface OnSuccessListener {
        void onSuccess(@NonNull Bitmap resultBitmap);
    }
}
