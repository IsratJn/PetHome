

package com.example.pethome.storeapp.data.local;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.FileRepository;
import com.example.pethome.storeapp.utils.AppExecutors;
import com.example.pethome.storeapp.utils.FileStorageUtility;
import com.example.pethome.storeapp.utils.ImageStorageUtility;

import java.io.IOException;
import java.util.List;

public class StoreFileRepository implements FileRepository {


    private static final String LOG_TAG = StoreFileRepository.class.getSimpleName();


    private static volatile StoreFileRepository INSTANCE;


    private final ContentResolver mContentResolver;


    private final AppExecutors mAppExecutors;

    private StoreFileRepository(@NonNull ContentResolver contentResolver, @NonNull AppExecutors appExecutors) {
        mContentResolver = contentResolver;
        mAppExecutors = appExecutors;
    }

    public static StoreFileRepository getInstance(@NonNull ContentResolver contentResolver, @NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {

            synchronized (StoreFileRepository.class) {

                if (INSTANCE == null) {

                    INSTANCE = new StoreFileRepository(contentResolver, appExecutors);
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void saveImageToFile(Context context, Uri fileContentUri, FileOperationsCallback<Uri> operationsCallback) {
        if (FileStorageUtility.isExternalStorageMounted()) {



            mAppExecutors.getDiskIO().execute(() -> {

                Uri savedImageFileUri = null;
                try {

                    savedImageFileUri = ImageStorageUtility.saveImage(context, fileContentUri);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "saveImageToFile: Error occurred while saving the image " + fileContentUri, e);
                }


                final Uri finalSavedImageFileUri = savedImageFileUri;

                mAppExecutors.getMainThread().execute(() -> {
                    if (finalSavedImageFileUri != null) {

                        operationsCallback.onSuccess(finalSavedImageFileUri);
                    } else {

                        operationsCallback.onFailure(R.string.animal_image_save_error);
                    }
                });
            });

        } else {

            operationsCallback.onFailure(R.string.animal_image_disk_not_mounted_save_error);
        }
    }

    @Override
    public void takePersistablePermissions(Uri fileContentUri, int intentFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            final int takeFlags = intentFlags
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mContentResolver.takePersistableUriPermission(fileContentUri, takeFlags);
        }
    }

    @Override
    public void deleteImageFiles(List<String> fileContentUriList, FileOperationsCallback<Boolean> operationsCallback) {
        if (FileStorageUtility.isExternalStorageMounted()) {



            mAppExecutors.getDiskIO().execute(() -> {

                int noOfFilesDeleted = 0;


                for (String fileContentUriStr : fileContentUriList) {
                    boolean fileDeleted = ImageStorageUtility.deleteImageFile(
                            Uri.parse(fileContentUriStr),
                            mContentResolver
                    );
                    noOfFilesDeleted += fileDeleted ? 1 : 0;
                }


                final int finalNoOfFilesDeleted = noOfFilesDeleted;


                mAppExecutors.getMainThread().execute(() -> {
                    if (finalNoOfFilesDeleted == fileContentUriList.size()) {

                        operationsCallback.onSuccess(true);
                    } else {

                        operationsCallback.onFailure(R.string.animal_image_delete_error);
                    }
                });
            });
        }
    }

    @Override
    public void deleteImageFilesSilently(List<String> fileContentUriList) {


    }


}
