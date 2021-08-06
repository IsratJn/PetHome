

package com.example.pethome.storeapp.ui.adoptions;

import android.widget.ImageView;

import com.example.pethome.storeapp.data.local.models.AdoptionsLite;


public interface AdoptionsListUserActionsListener {
    void onEditAdoptions(final int itemPosition, AdoptionsLite adoptionsLite, ImageView imageViewAnimalPhoto);
    void onDeleteAnimal(final int itemPosition, AdoptionsLite adoptionsLite);
    void onSellOneQuantity(final int itemPosition, AdoptionsLite adoptionsLite);
}
