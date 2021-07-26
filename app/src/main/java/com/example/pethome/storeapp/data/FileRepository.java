

package com.example.pethome.storeapp.data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.example.pethome.storeapp.data.local.StoreFileRepository;

import java.util.List;


public interface FileRepository {

    void saveImageToFile(Context context, Uri fileContentUri, FileOperationsCallback<Uri> operationsCallback);

    void takePersistablePermissions(Uri fileContentUri, int intentFlags);

    void deleteImageFiles(List<String> fileContentUriList, FileOperationsCallback<Boolean> operationsCallback);

    void deleteImageFilesSilently(List<String> fileContentUriList);

    interface FileOperationsCallback<T> {
        void onSuccess(T results);

        default void onFailure(@StringRes int messageId, @Nullable Object... args) {
        }
    }

}
