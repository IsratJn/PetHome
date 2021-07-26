
package com.example.pethome.storeapp.data;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.pethome.storeapp.data.local.models.Animal;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;
import com.example.pethome.storeapp.data.local.models.Rescuer;
import com.example.pethome.storeapp.data.local.models.RescuerContact;

import java.util.ArrayList;
import java.util.List;

public class StoreRepository implements DataRepository, FileRepository {

    private static final String LOG_TAG = StoreRepository.class.getSimpleName();

    private static volatile StoreRepository INSTANCE;

    private final DataRepository mLocalDataSource;

    private final FileRepository mLocalFileSource;

    private StoreRepository(@NonNull DataRepository localDataSource, @NonNull FileRepository localFileSource) {
        mLocalDataSource = localDataSource;
        mLocalFileSource = localFileSource;
    }

    public static StoreRepository getInstance(@NonNull DataRepository localDataSource, @NonNull FileRepository localFileSource) {
        if (INSTANCE == null) {
            synchronized (StoreRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StoreRepository(localDataSource, localFileSource);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getAllCategories(@NonNull GetQueryCallback<List<String>> queryCallback) {
        mLocalDataSource.getAllCategories(queryCallback);
    }

    @Override
    public void getCategoryByName(@NonNull String categoryName, @NonNull GetQueryCallback<Integer> queryCallback) {
        mLocalDataSource.getCategoryByName(categoryName, queryCallback);
    }

    @Override
    public void getAnimalDetailsById(int animalId, @NonNull GetQueryCallback<Animal> queryCallback) {
        mLocalDataSource.getAnimalDetailsById(animalId, queryCallback);
    }

    @Override
    public void getAnimalSkuUniqueness(@NonNull String animalSku, @NonNull GetQueryCallback<Boolean> queryCallback) {
        mLocalDataSource.getAnimalSkuUniqueness(animalSku, queryCallback);
    }

    @Override
    public void saveNewAnimal(@NonNull Animal newAnimal, @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.saveNewAnimal(newAnimal, operationsCallback);
    }

    @Override
    public void saveUpdatedAnimal(@NonNull Animal existingAnimal, @NonNull Animal newAnimal,
                                   @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.saveUpdatedAnimal(existingAnimal, newAnimal, operationsCallback);
    }

    @Override
    public void deleteAnimalById(int animalId, @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.deleteAnimalById(animalId, operationsCallback);
    }

    @Override
    public void saveAnimalImages(@NonNull Animal existingAnimal, @NonNull ArrayList<AnimalImage> animalImages, @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.saveAnimalImages(existingAnimal, animalImages, operationsCallback);
    }

    @Override
    public void registerContentObserver(@NonNull Uri uri, boolean notifyForDescendants, @NonNull ContentObserver observer) {
        mLocalDataSource.registerContentObserver(uri, notifyForDescendants, observer);
    }

    @Override
    public void unregisterContentObserver(@NonNull ContentObserver observer) {
        mLocalDataSource.unregisterContentObserver(observer);
    }

    @Override
    public void getRescuerDetailsById(int rescuerId, @NonNull GetQueryCallback<Rescuer> queryCallback) {
        mLocalDataSource.getRescuerDetailsById(rescuerId, queryCallback);
    }

    @Override
    public void getRescuerContactsById(int rescuerId, @NonNull GetQueryCallback<List<RescuerContact>> queryCallback) {
        mLocalDataSource.getRescuerContactsById(rescuerId, queryCallback);
    }

    @Override
    public void getRescuerCodeUniqueness(@NonNull String rescuerCode, @NonNull GetQueryCallback<Boolean> queryCallback) {
        mLocalDataSource.getRescuerCodeUniqueness(rescuerCode, queryCallback);
    }

    @Override
    public void getShortAnimalInfoForAnimals(@Nullable List<String> animalIds, @NonNull GetQueryCallback<List<AnimalLite>> queryCallback) {
        mLocalDataSource.getShortAnimalInfoForAnimals(animalIds, queryCallback);
    }

    @Override
    public void saveNewRescuer(@NonNull Rescuer newRescuer, @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.saveNewRescuer(newRescuer, operationsCallback);
    }

    @Override
    public void saveUpdatedRescuer(@NonNull Rescuer existingRescuer, @NonNull Rescuer newRescuer, @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.saveUpdatedRescuer(existingRescuer, newRescuer, operationsCallback);
    }

    @Override
    public void deleteRescuerById(int rescuerId, @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.deleteRescuerById(rescuerId, operationsCallback);
    }

    @Override
    public void decreaseAnimalRescuerInventory(int animalId, String animalSku,
                                                 int rescuerId, String rescuerCode,
                                                 int availableQuantity, int decreaseQuantityBy,
                                                 @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.decreaseAnimalRescuerInventory(animalId, animalSku, rescuerId, rescuerCode, availableQuantity, decreaseQuantityBy, operationsCallback);
    }

    @Override
    public void getAnimalRescuersAdoptionsInfo(int animalId, @NonNull GetQueryCallback<List<AnimalRescuerAdoptions>> queryCallback) {
        mLocalDataSource.getAnimalRescuersAdoptionsInfo(animalId, queryCallback);
    }

    @Override
    public void saveUpdatedAnimalAdoptionsInfo(int animalId, String animalSku,
                                            @NonNull List<AnimalRescuerAdoptions> existingAnimalRescuerAdoptions,
                                            @NonNull List<AnimalRescuerAdoptions> updatedAnimalRescuerAdoptions,
                                            @NonNull DataOperationsCallback operationsCallback) {
        mLocalDataSource.saveUpdatedAnimalAdoptionsInfo(animalId, animalSku,
                existingAnimalRescuerAdoptions, updatedAnimalRescuerAdoptions, operationsCallback);
    }

    @Override
    public void saveImageToFile(Context context, Uri fileContentUri, FileOperationsCallback<Uri> operationsCallback) {
        mLocalFileSource.saveImageToFile(context, fileContentUri, operationsCallback);
    }

    @Override
    public void takePersistablePermissions(Uri fileContentUri, int intentFlags) {
        mLocalFileSource.takePersistablePermissions(fileContentUri, intentFlags);
    }

    @Override
    public void deleteImageFiles(List<String> fileContentUriList, FileOperationsCallback<Boolean> operationsCallback) {
        mLocalFileSource.deleteImageFiles(fileContentUriList, operationsCallback);
    }

    @Override
    public void deleteImageFilesSilently(List<String> fileContentUriList) {
        mLocalFileSource.deleteImageFiles(fileContentUriList, new FileOperationsCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean results) {
                Log.i(LOG_TAG, "onSuccess: deleteImageFilesSilently: All Image files deleted");
            }

            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                Log.i(LOG_TAG, "onFailure: deleteImageFilesSilently: Some Image files were not deleted");
            }
        });
    }


}
