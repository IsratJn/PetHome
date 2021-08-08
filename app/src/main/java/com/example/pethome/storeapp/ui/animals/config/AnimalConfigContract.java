

package com.example.pethome.storeapp.ui.animals.config;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.ui.BasePresenter;
import com.example.pethome.storeapp.ui.BaseView;

import java.util.ArrayList;
import java.util.List;

public interface AnimalConfigContract {

    int NEW_ANIMALS_INT = -1;
    String CATEGORY_OTHER = "Other";
    interface View extends BaseView<Presenter> {
        void updateCategories(List<String> categories);
        void showCategoryOtherEditTextField();
        void hideCategoryOtherEditTextField();
        void clearCategoryOtherEditTextField();
        void showEmptyFieldsValidationError();
        void showAttributesPartialValidationError();
        void showAttributeNameConflictError(String attributeName);
        void showProgressIndicator(@StringRes int statusTextId);
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void showAnimalSkuConflictError();
        void showAnimalSkuEmptyError();
        void updateAnimalNameField(String name);
        void updateAnimalSkuField(String sku);
        void lockAnimalSkuField();
        void updateAnimalDescriptionField(String description);
        void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes);
        void updateAnimalImages(ArrayList<AnimalImage> animalImages);
        void updateCategorySelection(String selectedCategory, @Nullable String categoryOtherText);
        void syncExistingAnimalState(boolean isExistingAnimalRestored);
        void syncAnimalSkuValidity(boolean isAnimalSkuValid);
        void syncAnimalNameEnteredState(boolean isAnimalNameEntered);
        void showDiscardDialog();
        void showDeleteAnimalDialog();
        void triggerFocusLost();
        void showUpdateImagesSuccess();
    }
    interface Presenter extends BasePresenter {
        void updateAndSyncExistingAnimalState(boolean isExistingAnimalRestored);
        void updateAndSyncAnimalSkuValidity(boolean isAnimalSkuValid);
        void updateAndSyncAnimalNameEnteredState(boolean isAnimalNameEntered);
        void onCategorySelected(String categoryName);
        void updateAnimalNameField(String name);
        void updateAnimalSkuField(String sku);
        void updateAnimalDescriptionField(String description);
        void updateCategorySelection(String selectedCategory, @Nullable String categoryOtherText);
        void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes);
        void updateAnimalImages(ArrayList<AnimalImage> animalImages);
        void onSave(String animalName, String animalSku, String animalDescription,
                    String categorySelected, String categoryOtherText,
                    ArrayList<AnimalAttribute> animalAttributes);
        void validateAnimalSku(String animalSku);
        void openAnimalImages();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void onUpOrBackAction();
        void finishActivity();
        void showDeleteAnimalDialog();
        void deleteAnimal();
        void triggerFocusLost();
        void doSetResult(final int resultCode, final int animalId, @NonNull final String animalSku);
        void doCancel();
    }
}
