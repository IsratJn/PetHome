

package com.example.pethome.storeapp.ui.animals;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityOptionsCompat;

import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.ui.PagerPresenter;
import com.example.pethome.storeapp.ui.PagerView;

import java.util.ArrayList;

public interface AnimalListContract {
    interface View extends PagerView<Presenter> {
        void showProgressIndicator();
        void hideProgressIndicator();
        void loadAnimals(ArrayList<AnimalLite> animalList);
        void launchEditAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat);
        void showError(@StringRes int messageId, @Nullable Object... args);
        void showAddSuccess(String animalSku);
        void showUpdateSuccess(String animalSku);
        void showDeleteSuccess(String animalSku);
        void showEmptyView();
        void hideEmptyView();
        void launchAddNewAnimal();

    }
    interface Presenter extends PagerPresenter {
        void triggerAnimalsLoad(boolean forceLoad);
        void editAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat);
        void deleteAnimal(AnimalLite animal);
        void addNewAnimal();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void releaseResources();
    }

}
