

package com.example.pethome.storeapp.ui.adoptions.config;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;
import com.example.pethome.storeapp.ui.BasePresenter;
import com.example.pethome.storeapp.ui.BaseView;

import java.util.ArrayList;
import java.util.List;


public interface AdoptionsConfigContract {
    interface View extends BaseView<Presenter> {
        void syncAnimalState(boolean isAnimalRestored);
        void syncRescuersState(boolean areRescuersRestored);
        void syncOldTotalAvailability(int oldTotalAvailableQuantity);
        void showProgressIndicator(@StringRes int statusTextId);
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void updateAnimalName(String animalName);
        void updateAnimalSku(String animalSku);
        void updateAnimalCategory(String animalCategory);
        void updateAnimalDescription(String description);
        void updateAnimalImages(ArrayList<AnimalImage> animalImages);
        void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes);
        void loadAnimalRescuersData(ArrayList<AnimalRescuerAdoptions> animalRescuerAdoptionsList);
        void updateAvailability(int totalAvailableQuantity);
        void showOutOfStockAlert();
        void showAnimalRescuerSwiped(String rescuerCode);
        void showUpdateAnimalSuccess(String animalSku);
        void showUpdateRescuerSuccess(String rescuerCode);
        void showDeleteRescuerSuccess(String rescuerCode);
        void triggerFocusLost();
        void showDiscardDialog();
        void showDeleteAnimalDialog();

    }
    interface Presenter extends BasePresenter {
        void updateAndSyncAnimalState(boolean isAnimalRestored);
        void updateAndSyncRescuersState(boolean areRescuersRestored);
        void updateAndSyncOldTotalAvailability(int oldTotalAvailableQuantity);
        void updateAnimalName(String animalName);
        void updateAnimalSku(String animalSku);
        void updateAnimalCategory(String animalCategory);
        void updateAnimalDescription(String description);
        void updateAnimalImage(ArrayList<AnimalImage> animalImages);
        void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes);
        void updateAnimalRescuerAdoptionsList(List<AnimalRescuerAdoptions> animalRescuerAdoptionsList);
        void editAnimal(int animalId);
        void editRescuer(int rescuerId);
        void onAnimalRescuerSwiped(String rescuerCode);
        void updateAvailability(int totalAvailableQuantity);
        void changeAvailability(int changeInAvailableQuantity);
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void triggerFocusLost();
        void onSave(ArrayList<AnimalRescuerAdoptions> updatedAnimalRescuerAdoptionsList);
        void showDeleteAnimalDialog();
        void deleteAnimal();
        void doSetResult(final int resultCode, final int animalId, @NonNull final String animalSku);
        void doCancel();
        void onUpOrBackAction();
        void finishActivity();

    }

}