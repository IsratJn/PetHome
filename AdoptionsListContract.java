
package com.example.pethome.storeapp.ui.adoptions;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityOptionsCompat;

import com.example.pethome.storeapp.data.local.models.AdoptionsLite;
import com.example.pethome.storeapp.ui.PagerPresenter;
import com.example.pethome.storeapp.ui.PagerView;

import java.util.ArrayList;

public interface AdoptionsListContract {
    interface View extends PagerView<Presenter> {
        void showProgressIndicator();
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void showEmptyView();
        void hideEmptyView();
        void loadAdoptionsList(ArrayList<AdoptionsLite> adoptionsList);
        void showDeleteSuccess(String animalSku);
        void showSellQuantitySuccess(String animalSku, String rescuerCode);
        void showUpdateInventorySuccess(String animalSku);
        void launchEditAnimalAdoptions(int animalId, ActivityOptionsCompat activityOptionsCompat);

    }
    interface Presenter extends PagerPresenter {
        void triggerAnimalAdoptionsLoad(boolean forceLoad);
        void onAnimalContentChange();
        void releaseResources();
        void deleteAnimal(int animalId, String animalSku);
        void sellOneQuantity(AdoptionsLite adoptionsLite);
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void editAnimalAdoptions(int animalId, ActivityOptionsCompat activityOptionsCompat);
    }
}
