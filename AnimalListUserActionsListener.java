
package com.example.pethome.storeapp.ui.animals;

import android.widget.ImageView;

import com.example.pethome.storeapp.data.local.models.AnimalLite;

public interface AnimalListUserActionsListener {
    void onEditAnimal(final int itemPosition, AnimalLite animal, ImageView imageViewAnimalPhoto);
    void onDeleteAnimal(final int itemPosition, AnimalLite animal);
}
