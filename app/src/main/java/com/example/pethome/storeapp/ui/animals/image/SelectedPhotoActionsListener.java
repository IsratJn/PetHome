
package com.example.pethome.storeapp.ui.animals.image;

import android.graphics.Bitmap;

public interface SelectedPhotoActionsListener {
    void showDefaultImage();
    void showSelectedImage(Bitmap bitmap, String imageUri);

}
