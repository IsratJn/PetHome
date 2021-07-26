

package com.example.pethome.storeapp.data;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.example.pethome.storeapp.data.local.StoreLocalRepository;
import com.example.pethome.storeapp.data.local.models.Animal;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;
import com.example.pethome.storeapp.data.local.models.Rescuer;
import com.example.pethome.storeapp.data.local.models.RescuerContact;

import java.util.ArrayList;
import java.util.List;


public interface DataRepository {

    void getAllCategories(@NonNull GetQueryCallback<List<String>> queryCallback);

    void getCategoryByName(@NonNull String categoryName, @NonNull GetQueryCallback<Integer> queryCallback);

    void getAnimalDetailsById(int animalId, @NonNull GetQueryCallback<Animal> queryCallback);

    void getAnimalSkuUniqueness(@NonNull String animalSku, @NonNull GetQueryCallback<Boolean> queryCallback);

    void saveNewAnimal(@NonNull Animal newAnimal, @NonNull DataOperationsCallback operationsCallback);

    void saveUpdatedAnimal(@NonNull Animal existingAnimal, @NonNull Animal newAnimal,
                            @NonNull DataOperationsCallback operationsCallback);

    void deleteAnimalById(int animalId, @NonNull DataOperationsCallback operationsCallback);

    void saveAnimalImages(@NonNull Animal existingAnimal, @NonNull ArrayList<AnimalImage> animalImages, @NonNull DataOperationsCallback operationsCallback);

    void registerContentObserver(@NonNull Uri uri, boolean notifyForDescendants,
                                 @NonNull ContentObserver observer);

    void unregisterContentObserver(@NonNull ContentObserver observer);

    void getRescuerDetailsById(int rescuerId, @NonNull GetQueryCallback<Rescuer> queryCallback);

    void getRescuerContactsById(int rescuerId, @NonNull GetQueryCallback<List<RescuerContact>> queryCallback);

    void getRescuerCodeUniqueness(@NonNull String rescuerCode, @NonNull GetQueryCallback<Boolean> queryCallback);

    void getShortAnimalInfoForAnimals(@Nullable List<String> animalIds, @NonNull GetQueryCallback<List<AnimalLite>> queryCallback);

    void saveNewRescuer(@NonNull Rescuer newRescuer, @NonNull DataOperationsCallback operationsCallback);

    void saveUpdatedRescuer(@NonNull Rescuer existingRescuer, @NonNull Rescuer newRescuer,
                             @NonNull DataOperationsCallback operationsCallback);

    void deleteRescuerById(int rescuerId, @NonNull DataOperationsCallback operationsCallback);

    void decreaseAnimalRescuerInventory(int animalId, String animalSku, int rescuerId, String rescuerCode, int availableQuantity,
                                          int decreaseQuantityBy, @NonNull DataOperationsCallback operationsCallback);

    void getAnimalRescuersAdoptionsInfo(int animalId, @NonNull GetQueryCallback<List<AnimalRescuerAdoptions>> queryCallback);

    void saveUpdatedAnimalAdoptionsInfo(int animalId, String animalSku, @NonNull List<AnimalRescuerAdoptions> existingAnimalRescuerAdoptions,
                                     @NonNull List<AnimalRescuerAdoptions> updatedAnimalRescuerAdoptions,
                                     @NonNull DataOperationsCallback operationsCallback);

    interface GetQueryCallback<T> {
        void onResults(T results);

        void onEmpty();

        default void onFailure(@StringRes int messageId, @Nullable Object... args) {
        }
    }

    interface DataOperationsCallback {
        void onSuccess();

        void onFailure(@StringRes int messageId, @Nullable Object... args);
    }

    interface CursorDataLoaderCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();

        void onContentChange();
    }
}
