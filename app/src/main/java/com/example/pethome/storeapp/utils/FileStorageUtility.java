
package com.example.pethome.storeapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;

import java.io.File;
import java.io.IOException;

public final class FileStorageUtility {
    private FileStorageUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    private static boolean createDirectoryIfNotExists(File storageDir) {
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            return false;
        }
        return true;
    }
    @NonNull
    public static File createTempFile(Context context, String authority, String filename, String extension, File[] storageDirs) throws IOException {
        File preferredStorageDir = null;

        for (File storageDir : storageDirs) {
            if (EnvironmentCompat.getStorageState(storageDir).equals(Environment.MEDIA_MOUNTED)
                    && checkContentUriPossible(context, authority, filename, extension, storageDir)) {
                preferredStorageDir = storageDir;
            }
        }

        if (preferredStorageDir == null || !createDirectoryIfNotExists(preferredStorageDir)) {
            throw new IOException(preferredStorageDir + " could not be created or accessed");
        }

        return File.createTempFile(filename, extension, preferredStorageDir);
    }
    @NonNull
    public static File createFile(Context context, String authority, String filename, String extension, File[] storageDirs) throws IOException {
        File preferredStorageDir = null;

        for (File storageDir : storageDirs) {
            if (EnvironmentCompat.getStorageState(storageDir).equals(Environment.MEDIA_MOUNTED)
                    && checkContentUriPossible(context, authority, filename, extension, storageDir)) {
                preferredStorageDir = storageDir;
            }
        }

        if (preferredStorageDir == null || !createDirectoryIfNotExists(preferredStorageDir)) {
            throw new IOException(preferredStorageDir + " could not be created or accessed");
        }

        return new File(preferredStorageDir, filename + extension);
    }
    public static boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    public static Uri getContentUriForFile(Context context, String authority, File file) {
        return FileProvider.getUriForFile(context, authority, file);
    }
    private static boolean checkContentUriPossible(Context context, String authority, String filename, String extension, File storageDir) {
        File fileToTest = new File(storageDir, filename + extension);
        return checkContentUriPossible(context, authority, fileToTest);
    }
    private static boolean checkContentUriPossible(Context context, String authority, File fileToTest) {
        boolean uriGenerated;
        try {
            getContentUriForFile(context, authority, fileToTest);
            uriGenerated = true;
        } catch (Exception e) {
            uriGenerated = false;
        }
        return uriGenerated;
    }
    @Nullable
    public static File getFileForContentUri(Context context, Uri fileContentUri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(fileContentUri,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {

                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                File contentUriFile = context.getFileStreamPath(displayName);
                String filePath = Uri.fromFile(contentUriFile).getPath();
                return filePath != null ? new File(filePath) : null;
            }

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }
    public static boolean deleteFile(Uri fileContentUri,
                                     ContentResolver contentResolver, String fileProviderAuthority) {
        int deleteResult = 0;
        if (fileContentUri.getAuthority() != null && fileContentUri.getAuthority().contains(fileProviderAuthority)) {
            deleteResult = contentResolver.delete(fileContentUri, null, null);
        }
        return (deleteResult == 1);
    }

}
