
package com.example.pethome.storeapp.ui.animals.config;

import android.support.annotation.NonNull;

import com.example.pethome.storeapp.data.local.models.AnimalImage;

import java.util.ArrayList;

public interface AnimalConfigNavigator {
    void doSetResult(final int resultCode, final int animalId, @NonNull final String animalSku);
    void doCancel();
    void launchAnimalImagesView(ArrayList<AnimalImage> animalImages);

}

