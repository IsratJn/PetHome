
package com.example.pethome.storeapp.ui.adoptions.config;

import android.support.annotation.NonNull;

public interface AdoptionsConfigNavigator {
    void doSetResult(final int resultCode, final int animalId, @NonNull final String animalSku);
    void doCancel();
    void launchEditAnimal(int animalId);
    void launchEditRescuer(int rescuerId);
}
