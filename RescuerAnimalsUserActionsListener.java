

package com.example.pethome.storeapp.ui.rescuers.config;

import android.widget.ImageView;

import com.example.pethome.storeapp.data.local.models.AnimalLite;

public interface RescuerAnimalsUserActionsListener {
    void onEditAnimal(final int itemPosition, AnimalLite animal, ImageView imageViewAnimalPhoto);
    void onSwiped(final int itemPosition, AnimalLite animal);
}
