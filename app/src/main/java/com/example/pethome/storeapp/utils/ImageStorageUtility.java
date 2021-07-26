

package com.example.pethome.storeapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ImageStorageUtility {

    private static final String IMAGE_FILE_PROVIDER_AUTHORITY
            = AppConstants.APPLICATION_ID + ".fileprovider";

    private static final String JPEG_FILE_EXT = ".jpg";

    private static final String FILE_NAME_PREFIX = "IMG_";

    private static final String TEMP_FILE_NAME_PREFIX = "TMP_";

    private static final String FILE_TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";
    private ImageStorageUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    private static String getCurrentTimestampPattern() {
        return new SimpleDateFormat(FILE_TIMESTAMP_PATTERN, Locale.getDefault()).format(new Date());
    }
    @NonNull
    public static File createTempImageFile(Context context) throws IOException {
        return FileStorageUtility.createTempFile(context, IMAGE_FILE_PROVIDER_AUTHORITY,
                TEMP_FILE_NAME_PREFIX + getCurrentTimestampPattern(),
                JPEG_FILE_EXT,
                ContextCompat.getExternalCacheDirs(context)
        );
    }
    @NonNull
    public static File createImageFile(Context context) throws IOException {
        return FileStorageUtility.createFile(context, IMAGE_FILE_PROVIDER_AUTHORITY,
                FILE_NAME_PREFIX + getCurrentTimestampPattern(),
                JPEG_FILE_EXT,
                ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES)
        );
    }
    @Nullable
    public static Bitmap getBitmapFromContentUri(Uri fileContentUri, ContentResolver contentResolver) throws IOException {
        InputStream uriInputStream = contentResolver.openInputStream(fileContentUri);
        Bitmap decodedBitmap = null;

        if (uriInputStream != null) {
            try {
                decodedBitmap = BitmapFactory.decodeStream(uriInputStream);
            } finally {
                uriInputStream.close();
            }
        }
        return decodedBitmap;
    }
    @Nullable
    public static Bitmap getOptimizedBitmapFromContentUri(Context context, Uri fileContentUri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        int targetW = (int) (WindowDimensionsUtility.getDisplayWindowWidth(context) * 0.5);
        int targetH = (int) (WindowDimensionsUtility.getDisplayWindowHeight(context) * 0.5);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;

        try (InputStream decodeBoundsInputStream = contentResolver.openInputStream(fileContentUri)) {
            BitmapFactory.decodeStream(decodeBoundsInputStream, null, bitmapOptions);
        }

        int photoW = bitmapOptions.outWidth;
        int photoH = bitmapOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;

        Bitmap optimizedBitmap = null;

        InputStream decodeBitmapInputStream = contentResolver.openInputStream(fileContentUri);
        if (decodeBitmapInputStream != null) {
            try {
                optimizedBitmap = BitmapFactory.decodeStream(decodeBitmapInputStream, null, bitmapOptions);
            } finally {
                decodeBitmapInputStream.close();
            }
        }

        return optimizedBitmap;
    }
    public static Uri getContentUriForImageFile(Context context, File file) {
        return FileStorageUtility.getContentUriForFile(context, IMAGE_FILE_PROVIDER_AUTHORITY, file);
    }
    @Nullable
    public static Uri saveImage(Context context, Uri fileContentUri) throws IOException {
        File outputImageFile = createImageFile(context);

        Bitmap bitmap = rotateImageBasedOnExif(context, fileContentUri);

        if (bitmap != null) {

            boolean writeSuccess;

            try (FileOutputStream fileOutputStream = new FileOutputStream(outputImageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                writeSuccess = true;
            } catch (Exception e) {
                writeSuccess = false;
            }

            if (writeSuccess) {
                addPhotoToGallery(context, outputImageFile);

                deleteImageFile(fileContentUri, context.getContentResolver());

                return getContentUriForImageFile(context, outputImageFile);
            }
        }

        return null;
    }
    @Nullable
    private static Bitmap rotateImageBasedOnExif(Context context, Uri fileContentUri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        Bitmap bitmap = getBitmapFromContentUri(fileContentUri, contentResolver);

        if (bitmap == null) {
            return null;
        }

        InputStream uriInputStream = contentResolver.openInputStream(fileContentUri);
        if (uriInputStream != null) {
            try {
                ExifInterface exifInterface = new ExifInterface(uriInputStream);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:

                        return rotateImage(bitmap, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:

                        return rotateImage(bitmap, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:

                        return rotateImage(bitmap, 270);
                    default:
                        return bitmap;
                }
            } finally {
                uriInputStream.close();
            }
        }
        return bitmap;
    }
    private static Bitmap rotateImage(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, (float) bitmap.getWidth() / (float) 2, (float) bitmap.getHeight() / (float) 2);
        Bitmap bitmapRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return bitmapRotated;
    }
    private static void addPhotoToGallery(Context context, File savedImageFile) {
        Intent mediaScanBroadcastIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileUri = Uri.fromFile(savedImageFile);
        mediaScanBroadcastIntent.setData(fileUri);
        context.sendBroadcast(mediaScanBroadcastIntent);
    }
    public static boolean deleteImageFile(Uri fileContentUri, ContentResolver contentResolver) {
        return FileStorageUtility.deleteFile(fileContentUri, contentResolver, IMAGE_FILE_PROVIDER_AUTHORITY);
    }
}
