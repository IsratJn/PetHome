

package com.example.pethome.storeapp.data.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.Pair;

import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.models.Animal;
import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerInfo;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;
import com.example.pethome.storeapp.data.local.models.Rescuer;
import com.example.pethome.storeapp.data.local.models.RescuerContact;
import com.example.pethome.storeapp.data.local.utils.QueryArgsUtility;
import com.example.pethome.storeapp.data.local.utils.SqliteUtility;
import com.example.pethome.storeapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.AND;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.EQUALS;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.PLACEHOLDER;

public class StoreLocalRepository implements DataRepository {

    private static final String LOG_TAG = StoreLocalRepository.class.getSimpleName();

    private static volatile StoreLocalRepository INSTANCE;

    private final ContentResolver mContentResolver;

    private final AppExecutors mAppExecutors;

    private StoreLocalRepository(@NonNull ContentResolver contentResolver, @NonNull AppExecutors appExecutors) {
        mContentResolver = contentResolver;
        mAppExecutors = appExecutors;
    }

    public static StoreLocalRepository getInstance(@NonNull ContentResolver contentResolver, @NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (StoreLocalRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StoreLocalRepository(contentResolver, appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getAllCategories(@NonNull GetQueryCallback<List<String>> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    AnimalContract.AnimalCategory.CONTENT_URI,
                    QueryArgsUtility.CategoriesQuery.getProjection(),
                    null,
                    null,
                    AnimalContract.AnimalCategory.COLUMN_ITEM_CATEGORY_NAME
            );

            ArrayList<String> categoryList = new ArrayList<>();
            try {
                if (cursor != null && cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        categoryList.add(cursor.getString(QueryArgsUtility.CategoriesQuery.COLUMN_ITEM_CATEGORY_NAME_INDEX));
                    }
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            mAppExecutors.getMainThread().execute(() -> {
                if (categoryList.size() > 0) {
                    queryCallback.onResults(categoryList);
                } else {
                    queryCallback.onEmpty();
                }
            });

        });
    }

    @Override
    public void getCategoryByName(@NonNull String categoryName, @NonNull GetQueryCallback<Integer> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    AnimalContract.AnimalCategory.buildCategoryNameUri(categoryName),
                    QueryArgsUtility.CategoryByNameQuery.getProjection(),
                    null,
                    null,
                    null
            );

            try {
                if (cursor != null && cursor.moveToFirst()) {

                    final Integer categoryId = cursor.getInt(QueryArgsUtility.CategoryByNameQuery.COLUMN_ITEM_CATEGORY_ID_INDEX);

                    final boolean cursorNull = cursor.isNull(QueryArgsUtility.CategoryByNameQuery.COLUMN_ITEM_CATEGORY_ID_INDEX);

                    mAppExecutors.getMainThread().execute(() -> {
                        if (cursorNull) {
                            queryCallback.onFailure(R.string.animal_config_category_null_error, categoryName);
                        } else {
                            queryCallback.onResults(categoryId);
                        }
                    });

                } else {
                    queryCallback.onEmpty();
                }

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        });
    }

    @Override
    public void getAnimalDetailsById(int animalId, @NonNull GetQueryCallback<Animal> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    ContentUris.withAppendedId(AnimalContract.Animal.CONTENT_URI, animalId),
                    QueryArgsUtility.ItemByIdQuery.getProjection(),
                    null,
                    null,
                    null
            );

            try {
                if (cursor != null && cursor.moveToFirst()) {

                    int itemId = cursor.getInt(QueryArgsUtility.ItemByIdQuery.COLUMN_ITEM_ID_INDEX);
                    String itemName = cursor.getString(QueryArgsUtility.ItemByIdQuery.COLUMN_ITEM_NAME_INDEX);
                    String itemSku = cursor.getString(QueryArgsUtility.ItemByIdQuery.COLUMN_ITEM_SKU_INDEX);
                    String itemDescription = cursor.getString(QueryArgsUtility.ItemByIdQuery.COLUMN_ITEM_DESCRIPTION_INDEX);
                    String itemCategoryName = cursor.getString(QueryArgsUtility.ItemByIdQuery.COLUMN_ITEM_CATEGORY_NAME_INDEX);

                    ArrayList<AnimalAttribute> animalAttributes = getAnimalAttributesById(animalId);

                    ArrayList<AnimalImage> animalImages = getAnimalImagesById(animalId);

                    final Animal animal = new Animal.Builder()
                            .setId(itemId)
                            .setName(itemName)
                            .setSku(itemSku)
                            .setDescription(itemDescription)
                            .setCategory(itemCategoryName)
                            .setAnimalAttributes(animalAttributes)
                            .setAnimalImages(animalImages)
                            .createAnimal();

                    mAppExecutors.getMainThread().execute(() -> {
                        queryCallback.onResults(animal);
                    });
                } else {

                    queryCallback.onFailure(R.string.animal_config_no_animal_found_error, animalId);
                }

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        });
    }

    @WorkerThread
    private ArrayList<AnimalImage> getAnimalImagesById(int animalId) {
        Cursor cursor = mContentResolver.query(
                ContentUris.withAppendedId(AnimalContract.AnimalImage.CONTENT_URI, animalId),
                QueryArgsUtility.ItemImagesQuery.getProjection(),
                null,
                null,
                null
        );

        ArrayList<AnimalImage> animalImages = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String imageUri = cursor.getString(QueryArgsUtility.ItemImagesQuery.COLUMN_ITEM_IMAGE_URI_INDEX);
                    int defaultImageId = cursor.getInt(QueryArgsUtility.ItemImagesQuery.COLUMN_ITEM_IMAGE_DEFAULT_INDEX);

                    AnimalImage animalImage = new AnimalImage.Builder()
                            .setImageUri(imageUri)
                            .setIsDefault(defaultImageId == AnimalContract.AnimalImage.ITEM_IMAGE_DEFAULT)
                            .createAnimalImage();

                    animalImages.add(animalImage);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return animalImages;
    }

    @WorkerThread
    private ArrayList<AnimalAttribute> getAnimalAttributesById(int animalId) {
        Cursor cursor = mContentResolver.query(
                ContentUris.withAppendedId(AnimalContract.AnimalAttribute.CONTENT_URI, animalId),
                QueryArgsUtility.ItemAttributesQuery.getProjection(),
                null,
                null,
                null

        );

        ArrayList<AnimalAttribute> animalAttributes = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String attrName = cursor.getString(QueryArgsUtility.ItemAttributesQuery.COLUMN_ITEM_ATTR_NAME_INDEX);
                    String attrValue = cursor.getString(QueryArgsUtility.ItemAttributesQuery.COLUMN_ITEM_ATTR_VALUE_INDEX);

                    AnimalAttribute animalAttribute = new AnimalAttribute.Builder()
                            .setAttributeName(attrName)
                            .setAttributeValue(attrValue)
                            .createAnimalAttribute();

                    animalAttributes.add(animalAttribute);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return animalAttributes;
    }

    @Override
    public void getAnimalSkuUniqueness(@NonNull String animalSku, @NonNull GetQueryCallback<Boolean> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    AnimalContract.Animal.buildItemSkuUri(animalSku),
                    QueryArgsUtility.ItemBySkuQuery.getProjection(),
                    null,
                    null,
                    null
            );

            try {
                boolean isSkuUnique;

                if (cursor != null && cursor.moveToFirst()) {

                    int itemId = cursor.getInt(QueryArgsUtility.ItemBySkuQuery.COLUMN_ITEM_ID_INDEX);

                    isSkuUnique = (cursor.isNull(QueryArgsUtility.ItemBySkuQuery.COLUMN_ITEM_ID_INDEX)
                            || itemId < 0);

                } else {

                    isSkuUnique = true;
                }

                final Boolean isSkuUniqueFinal = isSkuUnique;
                mAppExecutors.getMainThread().execute(() -> {
                    queryCallback.onResults(isSkuUniqueFinal);
                });
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        });
    }

    @Override
    public void saveNewAnimal(@NonNull Animal newAnimal, @NonNull DataOperationsCallback operationsCallback) {
        String categoryName = newAnimal.getCategory();

        getCategoryByName(categoryName, new GetQueryCallback<Integer>() {
            @MainThread
            @Override
            public void onResults(Integer categoryId) {

                mAppExecutors.getDiskIO().execute(() -> proceedToSaveAnimal(categoryId));
            }

            @MainThread
            @Override
            public void onEmpty() {

                mAppExecutors.getDiskIO().execute(() -> {
                    ContentValues categoryContentValues = new ContentValues();
                    categoryContentValues.put(AnimalContract.AnimalCategory.COLUMN_ITEM_CATEGORY_NAME, categoryName);

                    Uri categoryInsertUri = mContentResolver.insert(
                            AnimalContract.AnimalCategory.CONTENT_URI,
                            categoryContentValues
                    );

                    if (categoryInsertUri == null) {
                        mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_insert_category_error, categoryName));
                    } else {

                        proceedToSaveAnimal((int) ContentUris.parseId(categoryInsertUri));
                    }
                });

            }

            @MainThread
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                operationsCallback.onFailure(messageId, args);
            }

            @WorkerThread
            private void proceedToSaveAnimal(final int categoryId) {

                ContentValues itemContentValues = new ContentValues();
                itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_NAME, newAnimal.getName());
                itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_SKU, newAnimal.getSku());
                itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_DESCRIPTION, newAnimal.getDescription());
                itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_CATEGORY_ID, categoryId);

                Uri itemInsertUri = mContentResolver.insert(
                        AnimalContract.Animal.CONTENT_URI,
                        itemContentValues
                );

                if (itemInsertUri == null) {
                    mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_insert_item_error, newAnimal.getName()));
                } else {

                    int itemId = (int) ContentUris.parseId(itemInsertUri);

                    int noOfAnimalAttrsInserted = 0;
                    int noOfAnimalAttrsPresent = newAnimal.getAnimalAttributes().size();
                    if (noOfAnimalAttrsPresent > 0) {
                        noOfAnimalAttrsInserted = saveAnimalAttributes(itemId, newAnimal.getAnimalAttributes());
                    }

                    int noOfAnimalImagesInserted = 0;
                    int noOfAnimalImagesPresent = newAnimal.getAnimalImages().size();
                    if (noOfAnimalImagesPresent > 0) {
                        noOfAnimalImagesInserted = saveAnimalImages(itemId, newAnimal.getAnimalImages());
                    }

                    if (noOfAnimalAttrsPresent == noOfAnimalAttrsInserted
                            && noOfAnimalImagesPresent == noOfAnimalImagesInserted) {

                        mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
                    } else {

                        mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_insert_item_addtnl_dtls_error, newAnimal.getName(), newAnimal.getSku()));
                    }
                }
            }

        });

    }

    @Override
    public void saveUpdatedAnimal(@NonNull Animal existingAnimal, @NonNull Animal newAnimal,
                                   @NonNull DataOperationsCallback operationsCallback) {

        String newCategoryName = newAnimal.getCategory();

        getCategoryByName(newCategoryName, new GetQueryCallback<Integer>() {
            @MainThread
            @Override
            public void onResults(Integer categoryId) {

                mAppExecutors.getDiskIO().execute(() -> proceedToUpdateAnimal(categoryId));
            }

            @MainThread
            @Override
            public void onEmpty() {

                mAppExecutors.getDiskIO().execute(() -> {
                    ContentValues categoryContentValues = new ContentValues();
                    categoryContentValues.put(AnimalContract.AnimalCategory.COLUMN_ITEM_CATEGORY_NAME, newCategoryName);

                    Uri categoryInsertUri = mContentResolver.insert(
                            AnimalContract.AnimalCategory.CONTENT_URI,
                            categoryContentValues
                    );

                    if (categoryInsertUri == null) {
                        mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_update_category_error, existingAnimal.getSku(), newCategoryName));
                    } else {

                        proceedToUpdateAnimal((int) ContentUris.parseId(categoryInsertUri));
                    }
                });
            }

            @MainThread
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                operationsCallback.onFailure(messageId, args);
            }

            @WorkerThread
            private void proceedToUpdateAnimal(final int categoryId) {

                int itemId = existingAnimal.getId();

                int noOfAnimalAttrsInserted = 0;
                int noOfAnimalAttrsPresent = newAnimal.getAnimalAttributes().size();
                if (noOfAnimalAttrsPresent > 0) {
                    noOfAnimalAttrsInserted = saveAnimalAttributes(itemId, newAnimal.getAnimalAttributes());
                } else {
                    mContentResolver.delete(
                            ContentUris.withAppendedId(AnimalContract.AnimalAttribute.CONTENT_URI, existingAnimal.getId()),
                            null,
                            null
                    );
                }

                int noOfAnimalImagesInserted = 0;
                int noOfAnimalImagesPresent = newAnimal.getAnimalImages().size();
                if (noOfAnimalImagesPresent > 0) {
                    noOfAnimalImagesInserted = saveAnimalImages(itemId, newAnimal.getAnimalImages());
                } else {
                    mContentResolver.delete(
                            ContentUris.withAppendedId(AnimalContract.AnimalImage.CONTENT_URI, existingAnimal.getId()),
                            null,
                            null
                    );
                }

                if (noOfAnimalAttrsPresent == noOfAnimalAttrsInserted
                        && noOfAnimalImagesPresent == noOfAnimalImagesInserted) {

                    boolean isNameChanged = !newAnimal.getName().equals(existingAnimal.getName());
                    boolean isSkuChanged = !newAnimal.getSku().equals(existingAnimal.getSku());
                    boolean isDescriptionChanged = !newAnimal.getDescription().equals(existingAnimal.getDescription());
                    boolean isCategoryChanged = !newAnimal.getCategory().equals(existingAnimal.getCategory());

                    if (isNameChanged || isSkuChanged || isDescriptionChanged || isCategoryChanged) {

                        ContentValues itemContentValues = new ContentValues();
                        itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_NAME, newAnimal.getName());
                        itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_SKU, newAnimal.getSku());
                        itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_DESCRIPTION, newAnimal.getDescription());
                        itemContentValues.put(AnimalContract.Animal.COLUMN_ITEM_CATEGORY_ID, categoryId);

                        Uri contentUri = ContentUris.withAppendedId(AnimalContract.Animal.CONTENT_URI, itemId);

                        int noOfItemRecordsUpdated = mContentResolver.update(
                                contentUri,
                                itemContentValues,
                                null,
                                null
                        );

                        if (noOfItemRecordsUpdated == 0) {
                            mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_update_item_error, existingAnimal.getSku()));
                        } else if (noOfItemRecordsUpdated == 1) {
                            mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
                        } else if (noOfItemRecordsUpdated > 1) {
                            mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_update_item_inconsistent_error, existingAnimal.getSku()));
                        }
                    } else {
                        mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
                    }

                } else {
                    mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.animal_config_update_item_addtnl_dtls_error, existingAnimal.getSku()));
                }
            }
        });

    }

    @WorkerThread
    private int saveAnimalAttributes(int animalId, ArrayList<AnimalAttribute> animalAttributes) {
        int noOfAnimalAttrsPresent = animalAttributes.size();

        ContentValues[] valuesArray = new ContentValues[noOfAnimalAttrsPresent];
        for (int index = 0; index < noOfAnimalAttrsPresent; index++) {
            AnimalAttribute animalAttribute = animalAttributes.get(index);
            ContentValues contentValues = new ContentValues();
            contentValues.put(AnimalContract.AnimalAttribute.COLUMN_ITEM_ATTR_NAME, animalAttribute.getAttributeName());
            contentValues.put(AnimalContract.AnimalAttribute.COLUMN_ITEM_ATTR_VALUE, animalAttribute.getAttributeValue());
            valuesArray[index] = contentValues;
        }

        return mContentResolver.bulkInsert(
                ContentUris.withAppendedId(AnimalContract.AnimalAttribute.CONTENT_URI, animalId),
                valuesArray
        );
    }

    @WorkerThread
    private int saveAnimalImages(int animalId, ArrayList<AnimalImage> animalImages) {
        int noOfAnimalImagesPresent = animalImages.size();

        ContentValues[] valuesArray = new ContentValues[noOfAnimalImagesPresent];
        for (int index = 0; index < noOfAnimalImagesPresent; index++) {
            AnimalImage animalImage = animalImages.get(index);
            ContentValues contentValues = new ContentValues();
            contentValues.put(AnimalContract.AnimalImage.COLUMN_ITEM_IMAGE_URI, animalImage.getImageUri());
            contentValues.put(AnimalContract.AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT,
                    animalImage.isDefault() ? AnimalContract.AnimalImage.ITEM_IMAGE_DEFAULT : AnimalContract.AnimalImage.ITEM_IMAGE_NON_DEFAULT);
            valuesArray[index] = contentValues;
        }

        return mContentResolver.bulkInsert(
                ContentUris.withAppendedId(AnimalContract.AnimalImage.CONTENT_URI, animalId),
                valuesArray
        );
    }

    @Override
    public void saveAnimalImages(@NonNull Animal existingAnimal, @NonNull ArrayList<AnimalImage> animalImages, @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            int noOfAnimalImagesInserted = 0;
            int noOfAnimalImagesPresent = animalImages.size();
            if (noOfAnimalImagesPresent > 0) {
                noOfAnimalImagesInserted = saveAnimalImages(existingAnimal.getId(), animalImages);
            } else {
                mContentResolver.delete(
                        ContentUris.withAppendedId(AnimalContract.AnimalImage.CONTENT_URI, existingAnimal.getId()),
                        null,
                        null
                );
            }

            if (noOfAnimalImagesPresent == noOfAnimalImagesInserted) {
                mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
            } else {
                mAppExecutors.getMainThread().execute(() -> {
                    operationsCallback.onFailure(R.string.animal_config_update_item_images_error, existingAnimal.getSku());
                });
            }
        });
    }

    @Override
    public void deleteAnimalById(int animalId, @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            final int noOfRecordsDeleted = mContentResolver.delete(
                    ContentUris.withAppendedId(AnimalContract.Animal.CONTENT_URI, animalId),
                    null,
                    null
            );

            mAppExecutors.getMainThread().execute(() -> {
                if (noOfRecordsDeleted > 0) {
                    operationsCallback.onSuccess();
                } else {
                    operationsCallback.onFailure(R.string.animal_config_delete_item_error, animalId);
                }
            });
        });
    }

    @Override
    public void registerContentObserver(@NonNull Uri uri, boolean notifyForDescendants, @NonNull ContentObserver observer) {
        mContentResolver.registerContentObserver(uri, notifyForDescendants, observer);
    }

    @Override
    public void unregisterContentObserver(@NonNull ContentObserver observer) {
        mContentResolver.unregisterContentObserver(observer);
    }

    @Override
    public void getRescuerDetailsById(int rescuerId, @NonNull GetQueryCallback<Rescuer> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    ContentUris.withAppendedId(RescuerContract.Rescuer.CONTENT_URI, rescuerId),
                    QueryArgsUtility.RescuerByIdQuery.getProjection(),
                    null,
                    null,
                    null
            );

            try {
                if (cursor != null && cursor.moveToFirst()) {

                    String name = cursor.getString(QueryArgsUtility.RescuerByIdQuery.COLUMN_RESCUER_NAME_INDEX);
                    String code = cursor.getString(QueryArgsUtility.RescuerByIdQuery.COLUMN_RESCUER_CODE_INDEX);

                    ArrayList<RescuerContact> rescuerContacts = getRescuerContacts(rescuerId);

                    ArrayList<AnimalRescuerInfo> animalRescuerInfoList = getAnimalRescuerInfoList(rescuerId);

                    final Rescuer rescuer = new Rescuer.Builder()
                            .setId(rescuerId)
                            .setName(name)
                            .setCode(code)
                            .setContacts(rescuerContacts)
                            .setAnimalRescuerInfoList(animalRescuerInfoList)
                            .createRescuer();

                    mAppExecutors.getMainThread().execute(() -> {
                        queryCallback.onResults(rescuer);
                    });

                } else {

                    queryCallback.onFailure(R.string.rescuer_config_no_rescuer_found_error, rescuerId);
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        });
    }

    @Override
    public void getRescuerContactsById(int rescuerId, @NonNull GetQueryCallback<List<RescuerContact>> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            ArrayList<RescuerContact> rescuerContacts = getRescuerContacts(rescuerId);

            if (rescuerContacts != null && rescuerContacts.size() > 0) {
                mAppExecutors.getMainThread().execute(() -> queryCallback.onResults(rescuerContacts));
            } else {
                mAppExecutors.getMainThread().execute(queryCallback::onEmpty);
            }

        });
    }

    @WorkerThread
    private ArrayList<AnimalRescuerInfo> getAnimalRescuerInfoList(int rescuerId) {
        Cursor cursor = mContentResolver.query(
                ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI_RESCUER_ITEMS, rescuerId),
                QueryArgsUtility.RescuerItemsQuery.getProjection(),
                null,
                null,
                null
        );

        ArrayList<AnimalRescuerInfo> animalRescuerInfoList = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int itemId = cursor.getInt(QueryArgsUtility.RescuerItemsQuery.COLUMN_ITEM_ID_INDEX);
                    float unitPrice = cursor.getFloat(QueryArgsUtility.RescuerItemsQuery.COLUMN_ITEM_UNIT_PRICE_INDEX);

                    AnimalRescuerInfo animalRescuerInfo = new AnimalRescuerInfo.Builder()
                            .setItemId(itemId)
                            .setRescuerId(rescuerId)
                            .setUnitPrice(unitPrice)
                            .createAnimalRescuerInfo();

                    animalRescuerInfoList.add(animalRescuerInfo);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return animalRescuerInfoList;
    }

    @WorkerThread
    private ArrayList<RescuerContact> getRescuerContacts(int rescuerId) {
        Cursor cursor = mContentResolver.query(
                ContentUris.withAppendedId(RescuerContract.RescuerContact.CONTENT_URI, rescuerId),
                QueryArgsUtility.RescuerContactsQuery.getProjection(),
                null,
                null,
                null
        );

        ArrayList<RescuerContact> rescuerContacts = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int contactTypeId = cursor.getInt(QueryArgsUtility.RescuerContactsQuery.COLUMN_RESCUER_CONTACT_TYPE_ID_INDEX);
                    String contactValue = cursor.getString(QueryArgsUtility.RescuerContactsQuery.COLUMN_RESCUER_CONTACT_VALUE_INDEX);
                    int defaultContactIndex = cursor.getInt(QueryArgsUtility.RescuerContactsQuery.COLUMN_RESCUER_CONTACT_DEFAULT_INDEX);

                    RescuerContact rescuerContact = new RescuerContact.Builder()
                            .setType(RescuerContract.RescuerContactType.getPreloadedContactTypes()[contactTypeId])
                            .setValue(contactValue)
                            .setIsDefault(defaultContactIndex == RescuerContract.RescuerContact.RESCUER_CONTACT_DEFAULT)
                            .createRescuerContact();

                    rescuerContacts.add(rescuerContact);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return rescuerContacts;
    }

    @Override
    public void getRescuerCodeUniqueness(@NonNull String rescuerCode, @NonNull GetQueryCallback<Boolean> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    RescuerContract.Rescuer.buildRescuerCodeUri(rescuerCode),
                    QueryArgsUtility.RescuerByCodeQuery.getProjection(),
                    null,
                    null,
                    null
            );

            try {
                boolean isRescuerCodeUnique;

                if (cursor != null && cursor.moveToFirst()) {

                    int rescuerId = cursor.getInt(QueryArgsUtility.RescuerByCodeQuery.COLUMN_RESCUER_ID_INDEX);

                    isRescuerCodeUnique = (cursor.isNull(QueryArgsUtility.RescuerByCodeQuery.COLUMN_RESCUER_ID_INDEX)
                            || rescuerId < 0);
                } else {

                    isRescuerCodeUnique = true;
                }

                final Boolean isRescuerCodeUniqueFinal = isRescuerCodeUnique;
                mAppExecutors.getMainThread().execute(() -> {
                    queryCallback.onResults(isRescuerCodeUniqueFinal);
                });

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        });
    }

    @Override
    public void getShortAnimalInfoForAnimals(@Nullable List<String> animalIds,
                                               @NonNull GetQueryCallback<List<AnimalLite>> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Pair<String, String[]> selectionPairs = SqliteUtility.makeSelectionForInClause(
                    AnimalContract.Animal.getQualifiedColumnName(AnimalContract.Animal._ID),
                    animalIds
            );

            Cursor cursor = mContentResolver.query(
                    AnimalContract.Animal.CONTENT_URI_SHORT_INFO,
                    QueryArgsUtility.ItemsShortInfoQuery.getProjection(),
                    selectionPairs == null ? null : selectionPairs.first,
                    selectionPairs == null ? null : selectionPairs.second,
                    AnimalContract.Animal.getQualifiedColumnName(AnimalContract.Animal.COLUMN_ITEM_SKU)
            );

            ArrayList<AnimalLite> animalList = new ArrayList<>();
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        AnimalLite animalLite = AnimalLite.from(cursor);
                        animalList.add(animalLite);
                    }
                }

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            mAppExecutors.getMainThread().execute(() -> {
                if (animalList.size() > 0) {
                    queryCallback.onResults(animalList);
                } else {
                    queryCallback.onEmpty();
                }
            });

        });
    }

    @Override
    public void saveNewRescuer(@NonNull Rescuer newRescuer, @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            ContentValues rescuerContentValues = new ContentValues();
            rescuerContentValues.put(RescuerContract.Rescuer.COLUMN_RESCUER_NAME, newRescuer.getName());
            rescuerContentValues.put(RescuerContract.Rescuer.COLUMN_RESCUER_CODE, newRescuer.getCode());

            Uri rescuerInsertUri = mContentResolver.insert(
                    RescuerContract.Rescuer.CONTENT_URI,
                    rescuerContentValues
            );

            if (rescuerInsertUri == null) {
                mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.rescuer_config_insert_rescuer_error, newRescuer.getName()));
            } else {

                int rescuerId = (int) ContentUris.parseId(rescuerInsertUri);


                int noOfRescuerContactsInserted = 0;
                int noOfRescuerContactsPresent = newRescuer.getContacts().size();
                if (noOfRescuerContactsPresent > 0) {
                    noOfRescuerContactsInserted = saveRescuerContacts(rescuerId, newRescuer.getContacts());
                }

                int noOfRescuerItemsInserted = 0;
                int noOfRescuerItemsPresent = newRescuer.getAnimalRescuerInfoList().size();
                if (noOfRescuerItemsPresent > 0) {
                    noOfRescuerItemsInserted = saveRescuerItems(rescuerId, newRescuer.getAnimalRescuerInfoList());
                }

                int noOfRescuerItemsInventoryInserted = 0;
                if (noOfRescuerItemsPresent > 0) {
                    noOfRescuerItemsInventoryInserted = insertZeroRescuerInventoryForItems(rescuerId, newRescuer.getAnimalRescuerInfoList());
                }

                if (noOfRescuerContactsInserted == noOfRescuerContactsPresent
                        && noOfRescuerItemsInserted == noOfRescuerItemsPresent
                        && noOfRescuerItemsInventoryInserted == noOfRescuerItemsPresent) {

                    mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
                } else {

                    mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.rescuer_config_insert_rescuer_addtnl_dtls_error, newRescuer.getName(), newRescuer.getCode()));
                }
            }
        });
    }

    @WorkerThread
    private int saveRescuerContacts(int rescuerId, ArrayList<RescuerContact> contacts) {
        int noOfRescuerContactsPresent = contacts.size();

        ContentValues[] valuesArray = new ContentValues[noOfRescuerContactsPresent];
        for (int index = 0; index < noOfRescuerContactsPresent; index++) {
            RescuerContact rescuerContact = contacts.get(index);
            ContentValues contentValues = new ContentValues();
            contentValues.put(RescuerContract.RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID,
                    rescuerContact.getType().equals(
                            RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE) ?
                            RescuerContract.RescuerContactType.CONTACT_TYPE_ID_PHONE :
                            RescuerContract.RescuerContactType.CONTACT_TYPE_ID_EMAIL);
            contentValues.put(RescuerContract.RescuerContact.COLUMN_RESCUER_CONTACT_VALUE,
                    rescuerContact.getValue());
            contentValues.put(RescuerContract.RescuerContact.COLUMN_RESCUER_CONTACT_DEFAULT,
                    rescuerContact.isDefault() ?
                            RescuerContract.RescuerContact.RESCUER_CONTACT_DEFAULT :
                            RescuerContract.RescuerContact.RESCUER_CONTACT_NON_DEFAULT);
            valuesArray[index] = contentValues;
        }

        return mContentResolver.bulkInsert(
                ContentUris.withAppendedId(RescuerContract.RescuerContact.CONTENT_URI, rescuerId),
                valuesArray
        );
    }

    @WorkerThread
    private int saveRescuerItems(int rescuerId, ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
        int noOfRescuerItemsPresent = animalRescuerInfoList.size();

        ContentValues[] valuesArray = new ContentValues[noOfRescuerItemsPresent];
        for (int index = 0; index < noOfRescuerItemsPresent; index++) {
            AnimalRescuerInfo animalRescuerInfo = animalRescuerInfoList.get(index);
            ContentValues contentValues = new ContentValues();
            contentValues.put(AdoptionsContract.AnimalRescuerInfo.COLUMN_ITEM_ID, animalRescuerInfo.getItemId());
            contentValues.put(AdoptionsContract.AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE, animalRescuerInfo.getUnitPrice());
            valuesArray[index] = contentValues;
        }

        return mContentResolver.bulkInsert(
                ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI_RESCUER_ITEMS, rescuerId),
                valuesArray
        );
    }

    @WorkerThread
    private int insertZeroRescuerInventoryForItems(int rescuerId, ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
        int noOfRescuerItemsPresent = animalRescuerInfoList.size();

        ContentValues[] valuesArray = new ContentValues[noOfRescuerItemsPresent];
        for (int index = 0; index < noOfRescuerItemsPresent; index++) {
            AnimalRescuerInfo animalRescuerInfo = animalRescuerInfoList.get(index);
            ContentValues contentValues = new ContentValues();
            contentValues.put(AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_ID, animalRescuerInfo.getItemId());
            valuesArray[index] = contentValues;
        }

        return mContentResolver.bulkInsert(
                ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_INV_RESCUER, rescuerId),
                valuesArray
        );
    }

    @WorkerThread
    private int saveItemRescuersInventory(int animalId, List<AnimalRescuerAdoptions> animalRescuerAdoptionsList) {
        int noOfItemRescuersInventoryPresent = animalRescuerAdoptionsList.size();

        ContentValues[] valuesArray = new ContentValues[noOfItemRescuersInventoryPresent];
        for (int index = 0; index < noOfItemRescuersInventoryPresent; index++) {
            AnimalRescuerAdoptions animalRescuerAdoptions = animalRescuerAdoptionsList.get(index);
            ContentValues contentValues = new ContentValues();
            contentValues.put(AdoptionsContract.AnimalRescuerInventory.COLUMN_RESCUER_ID, animalRescuerAdoptions.getRescuerId());
            contentValues.put(AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY, animalRescuerAdoptions.getAvailableQuantity());
            valuesArray[index] = contentValues;
        }

        return mContentResolver.bulkInsert(
                ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_INV_ITEM, animalId),
                valuesArray
        );
    }

    @Override
    public void saveUpdatedRescuer(@NonNull Rescuer existingRescuer,
                                    @NonNull Rescuer newRescuer,
                                    @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            int rescuerId = existingRescuer.getId();

            ArrayList<RescuerContact> existingRescuerContacts = existingRescuer.getContacts();
            ArrayList<RescuerContact> newRescuerContacts = newRescuer.getContacts();

            ArrayList<RescuerContact> removedRescuerContacts = new ArrayList<>(existingRescuerContacts);
            removeSimilarRescuerContacts(removedRescuerContacts, newRescuerContacts);

            int noOfRescuerContactsDeleted = 0;
            int noOfRescuerContactsToDelete = removedRescuerContacts.size();
            if (noOfRescuerContactsToDelete > 0) {
                noOfRescuerContactsDeleted = deleteRescuerContacts(rescuerId, removedRescuerContacts);
            }

            int noOfRescuerContactsInserted = 0;
            int noOfRescuerContactsToInsert = newRescuerContacts.size();
            if (noOfRescuerContactsToInsert > 0) {
                noOfRescuerContactsInserted = saveRescuerContacts(rescuerId, newRescuerContacts);
            }

            ArrayList<AnimalRescuerInfo> existingAnimalRescuerInfoList = existingRescuer.getAnimalRescuerInfoList();
            ArrayList<AnimalRescuerInfo> newAnimalRescuerInfoList = newRescuer.getAnimalRescuerInfoList();

            ArrayList<AnimalRescuerInfo> removedAnimalRescuerInfoList = new ArrayList<>(existingAnimalRescuerInfoList);
            removeSimilarAnimalRescuerInfo(removedAnimalRescuerInfoList, newAnimalRescuerInfoList);

            int noOfRescuerItemsRemoved = 0;
            int noOfRescuerItemsToRemove = removedAnimalRescuerInfoList.size();
            if (noOfRescuerItemsToRemove > 0) {
                noOfRescuerItemsRemoved = unlinkRescuerItems(rescuerId, removedAnimalRescuerInfoList);
            }

            int noOfRescuerItemsInventoryRemoved = 0;
            if (noOfRescuerItemsToRemove > 0) {
                noOfRescuerItemsInventoryRemoved = unlinkRescuerItemsInventory(rescuerId, removedAnimalRescuerInfoList);
            }

            ArrayList<AnimalRescuerInfo> addedAnimalRescuerInfoList = new ArrayList<>(newAnimalRescuerInfoList);
            removeSimilarAnimalRescuerInfo(addedAnimalRescuerInfoList, existingAnimalRescuerInfoList);

            int noOfRescuerItemsInventoryInserted = 0;
            int noOfRescuerItemsInventoryToInsert = addedAnimalRescuerInfoList.size();
            if (noOfRescuerItemsInventoryToInsert > 0) {
                noOfRescuerItemsInventoryInserted = insertZeroRescuerInventoryForItems(rescuerId, addedAnimalRescuerInfoList);
            }

            int noOfRescuerItemsInserted = 0;
            int noOfRescuerItemsToInsert = newAnimalRescuerInfoList.size();
            if (noOfRescuerItemsToInsert > 0) {
                noOfRescuerItemsInserted = saveRescuerItems(rescuerId, newAnimalRescuerInfoList);
            }

            if (noOfRescuerContactsDeleted == noOfRescuerContactsToDelete
                    && noOfRescuerContactsInserted == noOfRescuerContactsToInsert
                    && noOfRescuerItemsRemoved == noOfRescuerItemsToRemove
                    && noOfRescuerItemsInventoryRemoved == noOfRescuerItemsToRemove
                    && noOfRescuerItemsInventoryInserted == noOfRescuerItemsInventoryToInsert
                    && noOfRescuerItemsInserted == noOfRescuerItemsToInsert) {

                boolean isNameChanged = !newRescuer.getName().equals(existingRescuer.getName());
                boolean isCodeChanged = !newRescuer.getCode().equals(existingRescuer.getCode());

                if (isNameChanged || isCodeChanged) {

                    ContentValues rescuerContentValues = new ContentValues();
                    rescuerContentValues.put(RescuerContract.Rescuer.COLUMN_RESCUER_NAME, newRescuer.getName());
                    rescuerContentValues.put(RescuerContract.Rescuer.COLUMN_RESCUER_CODE, newRescuer.getCode());

                    int noOfRescuerRecordsUpdated = mContentResolver.update(
                            ContentUris.withAppendedId(RescuerContract.Rescuer.CONTENT_URI, rescuerId),
                            rescuerContentValues,
                            null,
                            null
                    );

                    if (noOfRescuerRecordsUpdated == 0) {
                        mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.rescuer_config_update_rescuer_error, existingRescuer.getCode()));
                    } else if (noOfRescuerRecordsUpdated == 1) {
                        mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
                    } else if (noOfRescuerRecordsUpdated > 1) {
                        mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.rescuer_config_update_rescuer_inconsistent_error, existingRescuer.getCode()));
                    }

                } else {
                    mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);
                }

            } else {
                mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.rescuer_config_update_rescuer_addtnl_dtls_error, existingRescuer.getCode()));
            }

        });

    }

    @Override
    public void deleteRescuerById(int rescuerId, @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            final int noOfRecordsDeleted = mContentResolver.delete(
                    ContentUris.withAppendedId(RescuerContract.Rescuer.CONTENT_URI, rescuerId),
                    null,
                    null
            );

            mAppExecutors.getMainThread().execute(() -> {
                if (noOfRecordsDeleted > 0) {
                    operationsCallback.onSuccess();
                } else {
                    operationsCallback.onFailure(R.string.rescuer_config_delete_rescuer_error, rescuerId);
                }
            });
        });
    }

    @WorkerThread
    private int unlinkRescuerItemsInventory(int rescuerId, ArrayList<AnimalRescuerInfo> removedAnimalRescuerInfoList) {
        int noOfRescuerItemsInventoryRemoved = 0;

        for (AnimalRescuerInfo animalRescuerInfo : removedAnimalRescuerInfoList) {
            String selection = AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
            String[] selectionArgs = new String[]{String.valueOf(animalRescuerInfo.getItemId())};
            noOfRescuerItemsInventoryRemoved += mContentResolver.delete(
                    ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_INV_RESCUER, rescuerId),
                    selection,
                    selectionArgs
            );
        }

        return noOfRescuerItemsInventoryRemoved;
    }

    @WorkerThread
    private int unlinkItemRescuersInventory(int animalId, List<AnimalRescuerAdoptions> removedAnimalRescuerAdoptionsList) {
        int noOfItemRescuersInventoryRemoved = 0;

        for (AnimalRescuerAdoptions animalRescuerAdoptions : removedAnimalRescuerAdoptionsList) {
            String selection = AdoptionsContract.AnimalRescuerInventory.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
            String[] selectionArgs = new String[]{String.valueOf(animalRescuerAdoptions.getRescuerId())};
            noOfItemRescuersInventoryRemoved += mContentResolver.delete(
                    ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_INV_ITEM, animalId),
                    selection,
                    selectionArgs
            );
        }

        return noOfItemRescuersInventoryRemoved;
    }

    @WorkerThread
    private int unlinkRescuerItems(int rescuerId, ArrayList<AnimalRescuerInfo> removedAnimalRescuerInfoList) {
        int noOfRescuerItemsRemoved = 0;

        for (AnimalRescuerInfo animalRescuerInfo : removedAnimalRescuerInfoList) {
            String selection = AdoptionsContract.AnimalRescuerInfo.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
            String[] selectionArgs = new String[]{String.valueOf(animalRescuerInfo.getItemId())};
            noOfRescuerItemsRemoved += mContentResolver.delete(
                    ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI_RESCUER_ITEMS, rescuerId),
                    selection,
                    selectionArgs
            );
        }

        return noOfRescuerItemsRemoved;
    }

    @WorkerThread
    private int unlinkItemRescuers(int animalId, List<AnimalRescuerAdoptions> removedAnimalRescuerAdoptionsList) {
        int noOfItemRescuersRemoved = 0;

        for (AnimalRescuerAdoptions animalRescuerAdoptions : removedAnimalRescuerAdoptionsList) {
            String selection = AdoptionsContract.AnimalRescuerInfo.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
            String[] selectionArgs = new String[]{String.valueOf(animalRescuerAdoptions.getRescuerId())};
            noOfItemRescuersRemoved += mContentResolver.delete(
                    ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI_ITEM_RESCUERS, animalId),
                    selection,
                    selectionArgs
            );
        }

        return noOfItemRescuersRemoved;
    }

    @WorkerThread
    private int deleteRescuerContacts(int rescuerId, ArrayList<RescuerContact> removedRescuerContacts) {
        int noOfRecordsDeleted = 0;

        for (RescuerContact rescuerContact : removedRescuerContacts) {
            String selection = RescuerContract.RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID + EQUALS + PLACEHOLDER
                    + AND + RescuerContract.RescuerContact.COLUMN_RESCUER_CONTACT_VALUE + EQUALS + PLACEHOLDER;
            int contactTypeId = rescuerContact.getType().equals(
                    RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE) ?
                    RescuerContract.RescuerContactType.CONTACT_TYPE_ID_PHONE :
                    RescuerContract.RescuerContactType.CONTACT_TYPE_ID_EMAIL;
            String[] selectionArgs = new String[]{String.valueOf(contactTypeId), rescuerContact.getValue()};
            noOfRecordsDeleted += mContentResolver.delete(
                    ContentUris.withAppendedId(RescuerContract.RescuerContact.CONTENT_URI, rescuerId),
                    selection,
                    selectionArgs
            );
        }

        return noOfRecordsDeleted;
    }

    @WorkerThread
    private void removeSimilarRescuerContacts(ArrayList<RescuerContact> sourceRescuerContacts,
                                               ArrayList<RescuerContact> rescuerContactsToRemove) {
        ArrayList<String> contactsToRemove = new ArrayList<>();
        for (RescuerContact rescuerContact : rescuerContactsToRemove) {
            contactsToRemove.add(rescuerContact.getValue());
        }

        Iterator<RescuerContact> sourceRescuerContactsIterator = sourceRescuerContacts.iterator();
        while (sourceRescuerContactsIterator.hasNext()) {
            RescuerContact rescuerContact = sourceRescuerContactsIterator.next();
            if (contactsToRemove.contains(rescuerContact.getValue())) {
                sourceRescuerContactsIterator.remove();
            }
        }
    }

    @WorkerThread
    private void removeSimilarAnimalRescuerInfo(ArrayList<AnimalRescuerInfo> sourceAnimalRescuerInfoList,
                                                  ArrayList<AnimalRescuerInfo> animalRescuerInfoListToRemove) {
        ArrayList<Pair<Integer, Integer>> animalRescuerPairsToRemove = new ArrayList<>();
        for (AnimalRescuerInfo animalRescuerInfo : animalRescuerInfoListToRemove) {
            animalRescuerPairsToRemove.add(Pair.create(animalRescuerInfo.getItemId(), animalRescuerInfo.getRescuerId()));
        }

        Iterator<AnimalRescuerInfo> sourceAnimalRescuerInfoIterator = sourceAnimalRescuerInfoList.iterator();
        while (sourceAnimalRescuerInfoIterator.hasNext()) {
            AnimalRescuerInfo animalRescuerInfo = sourceAnimalRescuerInfoIterator.next();
            Pair<Integer, Integer> animalRescuerPair = Pair.create(animalRescuerInfo.getItemId(), animalRescuerInfo.getRescuerId());
            if (animalRescuerPairsToRemove.contains(animalRescuerPair)) {
                sourceAnimalRescuerInfoIterator.remove();
            }
        }
    }

    @WorkerThread
    private void removeSimilarAnimalRescuerAdoptions(List<AnimalRescuerAdoptions> sourceAnimalRescuerAdoptionsList,
                                                   List<AnimalRescuerAdoptions> animalRescuerAdoptionsListToRemove) {
        ArrayList<Pair<Integer, Integer>> animalRescuerPairsToRemove = new ArrayList<>();
        for (AnimalRescuerAdoptions animalRescuerAdoptions : animalRescuerAdoptionsListToRemove) {
            animalRescuerPairsToRemove.add(Pair.create(animalRescuerAdoptions.getItemId(), animalRescuerAdoptions.getRescuerId()));
        }

        Iterator<AnimalRescuerAdoptions> sourceAnimalRescuerAdoptionsIterator = sourceAnimalRescuerAdoptionsList.iterator();
        while (sourceAnimalRescuerAdoptionsIterator.hasNext()) {
            AnimalRescuerAdoptions animalRescuerAdoptions = sourceAnimalRescuerAdoptionsIterator.next();
            Pair<Integer, Integer> animalRescuerPair = Pair.create(animalRescuerAdoptions.getItemId(), animalRescuerAdoptions.getRescuerId());
            if (animalRescuerPairsToRemove.contains(animalRescuerPair)) {
                sourceAnimalRescuerAdoptionsIterator.remove();
            }
        }
    }

    @Override
    public void decreaseAnimalRescuerInventory(int animalId, String animalSku,
                                                 int rescuerId, String rescuerCode,
                                                 int availableQuantity, int decreaseQuantityBy,
                                                 @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY, availableQuantity - decreaseQuantityBy);

            String selection = AdoptionsContract.AnimalRescuerInventory.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
            String[] selectionArgs = new String[]{String.valueOf(rescuerId)};

            final int noOfRecordsUpdated = mContentResolver.update(
                    ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_INV_ITEM, animalId),
                    contentValues,
                    selection,
                    selectionArgs
            );

            mAppExecutors.getMainThread().execute(() -> {
                if (noOfRecordsUpdated > 0) {
                    operationsCallback.onSuccess();
                } else {
                    operationsCallback.onFailure(R.string.adoptions_list_item_decrease_availability_error, animalSku, rescuerCode);
                }
            });
        });
    }

    @Override
    public void getAnimalRescuersAdoptionsInfo(int animalId, @NonNull GetQueryCallback<List<AnimalRescuerAdoptions>> queryCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            Cursor cursor = mContentResolver.query(
                    ContentUris.withAppendedId(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_INV_ITEM, animalId),
                    QueryArgsUtility.ItemRescuersAdoptionsQuery.getProjection(),
                    null,
                    null,
                    RescuerContract.Rescuer.getQualifiedColumnName(RescuerContract.Rescuer.COLUMN_RESCUER_CODE)
            );

            ArrayList<AnimalRescuerAdoptions> animalRescuerAdoptionsList = new ArrayList<>();
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        AnimalRescuerAdoptions animalRescuerAdoptions
                                = new AnimalRescuerAdoptions.Builder()
                                .setItemId(cursor.getInt(QueryArgsUtility.ItemRescuersAdoptionsQuery.COLUMN_ITEM_ID_INDEX))
                                .setRescuerId(cursor.getInt(QueryArgsUtility.ItemRescuersAdoptionsQuery.COLUMN_RESCUER_ID_INDEX))
                                .setRescuerName(cursor.getString(QueryArgsUtility.ItemRescuersAdoptionsQuery.COLUMN_RESCUER_NAME_INDEX))
                                .setRescuerCode(cursor.getString(QueryArgsUtility.ItemRescuersAdoptionsQuery.COLUMN_RESCUER_CODE_INDEX))
                                .setUnitPrice(cursor.getFloat(QueryArgsUtility.ItemRescuersAdoptionsQuery.COLUMN_ITEM_UNIT_PRICE_INDEX))
                                .setAvailableQuantity(cursor.getInt(QueryArgsUtility.ItemRescuersAdoptionsQuery.COLUMN_AVAIL_QUANTITY_INDEX))
                                .createAnimalRescuerAdoptions();

                        animalRescuerAdoptionsList.add(animalRescuerAdoptions);
                    }
                }
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            mAppExecutors.getMainThread().execute(() -> {
                if (animalRescuerAdoptionsList.size() > 0) {
                    queryCallback.onResults(animalRescuerAdoptionsList);
                } else {
                    queryCallback.onEmpty();
                }
            });

        });
    }

    @Override
    public void saveUpdatedAnimalAdoptionsInfo(int animalId, String animalSku,
                                            @NonNull List<AnimalRescuerAdoptions> existingAnimalRescuerAdoptions,
                                            @NonNull List<AnimalRescuerAdoptions> updatedAnimalRescuerAdoptions,
                                            @NonNull DataOperationsCallback operationsCallback) {
        mAppExecutors.getDiskIO().execute(() -> {
            ArrayList<AnimalRescuerAdoptions> removedAnimalRescuerAdoptionsList = new ArrayList<>(existingAnimalRescuerAdoptions);
            removeSimilarAnimalRescuerAdoptions(removedAnimalRescuerAdoptionsList, updatedAnimalRescuerAdoptions);

            int noOfItemRescuersRemoved = 0;
            int noOfItemRescuersToRemove = removedAnimalRescuerAdoptionsList.size();
            if (noOfItemRescuersToRemove > 0) {
                noOfItemRescuersRemoved = unlinkItemRescuers(animalId, removedAnimalRescuerAdoptionsList);
            }

            int noOfItemRescuersInventoryRemoved = 0;
            if (noOfItemRescuersToRemove > 0) {
                noOfItemRescuersInventoryRemoved = unlinkItemRescuersInventory(animalId, removedAnimalRescuerAdoptionsList);
            }

            int noOfItemRescuersInventoryInserted = 0;
            int noOfItemRescuersInventoryToInsert = updatedAnimalRescuerAdoptions.size();
            if (noOfItemRescuersInventoryToInsert > 0) {
                noOfItemRescuersInventoryInserted = saveItemRescuersInventory(animalId, updatedAnimalRescuerAdoptions);
            }

            if (noOfItemRescuersRemoved == noOfItemRescuersToRemove
                    && noOfItemRescuersInventoryRemoved == noOfItemRescuersToRemove
                    && noOfItemRescuersInventoryInserted == noOfItemRescuersInventoryToInsert) {

                mAppExecutors.getMainThread().execute(operationsCallback::onSuccess);

            } else {

                mAppExecutors.getMainThread().execute(() -> operationsCallback.onFailure(R.string.adoptions_config_inventory_update_error, animalSku));
            }

        });

    }

}