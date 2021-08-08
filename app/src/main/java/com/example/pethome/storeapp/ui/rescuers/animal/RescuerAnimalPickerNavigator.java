

package com.example.pethome.storeapp.ui.rescuers.animal;

import com.example.pethome.storeapp.data.local.models.AnimalLite;

import java.util.ArrayList;

public interface RescuerAnimalPickerNavigator {
    void doSetResult(ArrayList<AnimalLite> animalsToSell);
    void doCancel();
}
