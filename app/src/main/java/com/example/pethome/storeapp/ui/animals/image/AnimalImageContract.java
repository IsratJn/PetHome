
package com.example.pethome.storeapp.ui.animals.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;

import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.ui.BasePresenter;
import com.example.pethome.storeapp.ui.BaseView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;


public interface AnimalImageContract {

    int REQUEST_IMAGE_CAPTURE = 31;
    int REQUEST_IMAGE_PICK = 32;

    String MODE_SELECT = "PhotoGrid.SELECT_MODE";
    String MODE_DELETE = "PhotoGrid.DELETE_MODE";

    @StringDef({MODE_SELECT, MODE_DELETE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PhotoGridSelectModeDef {
    }
    interface View extends BaseView<Presenter> {
        void showImagePickerDialog();
        void showProgressIndicator(@StringRes int statusTextId);
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void onImageCaptured();
        void updateGridItemsState(ArrayList<ImageSelectionTracker> imageSelectionTrackers);
        void showDeleteSuccess();
        void submitListToAdapter(ArrayList<AnimalImage> animalImages);
        void syncSelectionTrackers(ArrayList<ImageSelectionTracker> imageSelectionTrackers);
        void showGridView();
        void hideGridView();
        void showDiscardDialog();
        void showImageAlreadyPicked();
    }
    interface Presenter extends BasePresenter {
        void openImagePickerDialog();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void submitListToAdapter(ArrayList<AnimalImage> animalImages);
        void saveImageToFile(Context context, @Nullable Uri tempCaptureImageUri);
        void onItemImageClicked(int itemPosition, AnimalImage animalImage, @AnimalImageContract.PhotoGridSelectModeDef String gridMode);
        void onItemImageLongClicked(int itemPosition, AnimalImage animalImage, @AnimalImageContract.PhotoGridSelectModeDef String gridMode);
        void showDeleteCount();
        void deleteSelection();
        void clearSelectedItems();
        void onDeleteModeExit();
        void restoreSelectionTrackers(ArrayList<ImageSelectionTracker> imageSelectionTrackers);
        void restoreAnimalImages(ArrayList<AnimalImage> animalImages);
        void syncLastChosenAnimalImage(AnimalImage animalImage);
        void onSelectAction();
        void onIgnoreAction();
        void onUpOrBackAction();
        void doSetResult();
        void showSelectedImage(Bitmap bitmap, String imageUri);
    }

}
