

package com.example.pethome.storeapp.ui.rescuers.config;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;

import com.example.pethome.storeapp.data.local.models.AnimalLite;

import java.util.ArrayList;

public interface RescuerConfigNavigator {
    void doSetResult(final int resultCode, final int rescuerId, @NonNull final String rescuerCode);
    void doCancel();
    void launchEditAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat);
    void launchPickAnimals(ArrayList<AnimalLite> animalLiteList);

}
