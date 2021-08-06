
package com.example.pethome.storeapp.ui.rescuers.config;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.SparseArray;

import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerInfo;
import com.example.pethome.storeapp.data.local.models.RescuerContact;
import com.example.pethome.storeapp.ui.BasePresenter;
import com.example.pethome.storeapp.ui.BaseView;

import java.util.ArrayList;

public interface RescuerConfigContract {

    //Integer Constant used as the Rescuer ID for New Rescuer Configuration
    int NEW_RESCUER_INT = -1;
    interface View extends BaseView<Presenter> {
        void showProgressIndicator(@StringRes int statusTextId);
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void lockRescuerCodeField();
        void syncExistingRescuerState(boolean isExistingRescuerRestored);
        void syncRescuerCodeValidity(boolean isRescuerCodeValid);
        void syncRescuerNameEnteredState(boolean isRescuerNameEntered);
        void showRescuerCodeConflictError();
        void updateRescuerNameField(String rescuerName);
        void updateRescuerCodeField(String rescuerCode);
        void updatePhoneContacts(ArrayList<RescuerContact> phoneContacts);
        void updateEmailContacts(ArrayList<RescuerContact> emailContacts);
        void updateRescuerAnimals(ArrayList<AnimalRescuerInfo> animalRescuerInfoList,
                                    @Nullable SparseArray<AnimalLite> animalLiteSparseArray);
        void showRescuerCodeEmptyError();
        void triggerFocusLost();
        void showEmptyFieldsValidationError();
        void showRescuerContactConflictError(@StringRes int conflictMessageResId,
                                              String contactValue);
        void showEmptyContactsError();
        void showDiscardDialog();
        void showDeleteRescuerDialog();
        void showRescuerAnimalSwiped(String animalSku);
        void showUpdateSuccess(String animalSku);
        void showDeleteSuccess(String animalSku);
        void notifyAnimalChanged(int animalId);
        void showRescuerContactsInvalidError(@StringRes int invalidMessageResId);
    }
    interface Presenter extends BasePresenter {
        void updateAndSyncExistingRescuerState(boolean isExistingRescuerRestored);
        void updateAndSyncRescuerCodeValidity(boolean isRescuerCodeValid);
        void updateAndSyncRescuerNameEnteredState(boolean isRescuerNameEntered);
        void updateRescuerNameField(String rescuerName);
        void updateRescuerCodeField(String rescuerCode);
        void updateRescuerContacts(ArrayList<RescuerContact> rescuerContacts);
        void updateRescuerAnimals(ArrayList<AnimalRescuerInfo> animalRescuerInfoList,
                                    @Nullable SparseArray<AnimalLite> animalLiteSparseArray);
        void validateRescuerCode(String rescuerCode);
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void triggerFocusLost();
        void onSave(String rescuerName,
                    String rescuerCode,
                    ArrayList<RescuerContact> phoneContacts,
                    ArrayList<RescuerContact> emailContacts,
                    ArrayList<AnimalRescuerInfo> animalRescuerInfoList);
        void showDeleteRescuerDialog();
        void deleteRescuer();
        void onUpOrBackAction();
        void finishActivity();
        void doSetResult(final int resultCode, final int rescuerId, @NonNull final String rescuerCode);
        void doCancel();
        void editAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat);
        void onRescuerAnimalSwiped(String animalSku);
        void pickAnimals(ArrayList<AnimalLite> animalLiteList);
    }

}
