

package com.example.pethome.storeapp.ui.adoptions.config;

import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;

public interface AnimalRescuersUserActionsListener {

    void onEditRescuer(final int itemPosition, AnimalRescuerAdoptions animalRescuerAdoptions);

    void onSwiped(final int itemPosition, AnimalRescuerAdoptions animalRescuerAdoptions);

    void onUpdatedAvailability(final int totalAvailableQuantity);

    void onChangeInAvailability(final int changeInAvailableQuantity);
}
