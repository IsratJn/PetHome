package com.example.pethome.storeapp.ui.animals.config;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.models.Animal;
import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.ui.animals.image.AnimalImageActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class AnimalConfigPresenter implements AnimalConfigContract.Presenter {
    private static final String LOG_TAG = AnimalConfigPresenter.class.getSimpleName();
    private final int mAnimalId;
    @NonNull
    private final AnimalConfigContract.View mAnimalConfigView;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final AnimalConfigNavigator mAnimalConfigNavigator;
    private final DefaultPhotoChangeListener mDefaultPhotoChangeListener;
    private Animal mExistingAnimal;
    private ArrayList<String> mCategoriesDownloaded;
    private ArrayList<AnimalImage> mAnimalImages;
    private boolean mIsExistingAnimalRestored;
    private boolean mIsAnimalSkuValid;
    private boolean mIsAnimalNameEntered;
    private boolean mIsExtraAnimalImagesReceived;
    AnimalConfigPresenter(int animalId,
                          @NonNull StoreRepository storeRepository,
                          @NonNull AnimalConfigContract.View animalConfigView,
                          @NonNull AnimalConfigNavigator animalConfigNavigator,
                          @NonNull DefaultPhotoChangeListener defaultPhotoChangeListener) {
        mAnimalId = animalId;
        mStoreRepository = storeRepository;
        mAnimalConfigView = animalConfigView;
        mAnimalConfigNavigator = animalConfigNavigator;
        mDefaultPhotoChangeListener = defaultPhotoChangeListener;
        mAnimalConfigView.setPresenter(this);
    }
    @Override
    public void start() {
        loadCategories();
        if (mAnimalId == AnimalConfigContract.NEW_ANIMALS_INT) {
            if (mAnimalImages == null) {
                mDefaultPhotoChangeListener.showDefaultImage();
            }
        } else {
            loadExistingAnimal();
        }
    }
    @Override
    public void updateAndSyncExistingAnimalState(boolean isExistingAnimalRestored) {
        mIsExistingAnimalRestored = isExistingAnimalRestored;
        mAnimalConfigView.syncExistingAnimalState(mIsExistingAnimalRestored);
    }
    @Override
    public void updateAndSyncAnimalSkuValidity(boolean isAnimalSkuValid) {
        mIsAnimalSkuValid = isAnimalSkuValid;
        mAnimalConfigView.syncAnimalSkuValidity(mIsAnimalSkuValid);
        if (!mIsAnimalSkuValid) {
            mAnimalConfigView.showAnimalSkuConflictError();
        }
    }
    @Override
    public void updateAndSyncAnimalNameEnteredState(boolean isAnimalNameEntered) {
        mIsAnimalNameEntered = isAnimalNameEntered;
        mAnimalConfigView.syncAnimalNameEnteredState(mIsAnimalNameEntered);
    }
    private void loadCategories() {
        mStoreRepository.getAllCategories(new DataRepository.GetQueryCallback<List<String>>() {
            @Override
            public void onResults(List<String> results) {
                mCategoriesDownloaded = new ArrayList<>();
                mCategoriesDownloaded.addAll(results);
                results.add(AnimalConfigContract.CATEGORY_OTHER);
                mAnimalConfigView.updateCategories(results);
            }
            @Override
            public void onEmpty() {
                List<String> categories = new ArrayList<>();
                categories.add(AnimalConfigContract.CATEGORY_OTHER);
                mAnimalConfigView.updateCategories(categories);
            }
        });
    }
    private void loadExistingAnimal() {
        mAnimalConfigView.showProgressIndicator(R.string.animal_config_status_loading_existing_animal);
        mStoreRepository.getAnimalDetailsById(mAnimalId, new DataRepository.GetQueryCallback<Animal>() {
            @Override
            public void onResults(Animal animal) {
                if (!mIsExistingAnimalRestored) {
                    updateAnimalNameField(animal.getName());
                    updateAnimalSkuField(animal.getSku());
                    updateAnimalDescriptionField(animal.getDescription());
                    updateCategorySelection(animal.getCategory(), null);
                    updateAnimalAttributes(animal.getAnimalAttributes());
                    updateAnimalImages(animal.getAnimalImages());
                    updateAndSyncExistingAnimalState(true);
                    updateAndSyncAnimalSkuValidity(true);
                    updateAndSyncAnimalNameEnteredState(true);
                }
                mAnimalConfigView.lockAnimalSkuField();
                mExistingAnimal = animal;
                if (mIsExtraAnimalImagesReceived) {
                    onExtraAnimalImagesReceived();
                }
                mAnimalConfigView.hideProgressIndicator();
            }
            @Override
            public void onFailure(@StringRes int messageId, @Nullable Object... args) {
                mAnimalConfigView.hideProgressIndicator();
                mAnimalConfigView.showError(messageId, args);
            }
            @Override
            public void onEmpty() {
            }
        });
    }
    @Override
    public void updateCategorySelection(String selectedCategory, @Nullable String categoryOtherText) {
        if (selectedCategory.equals(AnimalConfigContract.CATEGORY_OTHER)) {
            if (TextUtils.isEmpty(categoryOtherText)) {
                mAnimalConfigView.showCategoryOtherEditTextField();
                mAnimalConfigView.clearCategoryOtherEditTextField();
                mAnimalConfigView.updateCategorySelection(selectedCategory, null);
            } else {
                categoryOtherText = categoryOtherText.trim();
                if (mCategoriesDownloaded.contains(categoryOtherText)) {
                    mAnimalConfigView.updateCategorySelection(categoryOtherText, null);
                    mAnimalConfigView.clearCategoryOtherEditTextField();
                    mAnimalConfigView.hideCategoryOtherEditTextField();
                } else {
                    mAnimalConfigView.showCategoryOtherEditTextField();
                    mAnimalConfigView.updateCategorySelection(selectedCategory, categoryOtherText);
                }
            }
        } else {
            mAnimalConfigView.updateCategorySelection(selectedCategory, null);
        }
    }
    @Override
    public void updateAnimalImages(ArrayList<AnimalImage> animalImages) {
        mAnimalImages = animalImages;
        mAnimalConfigView.updateAnimalImages(animalImages);
        if (mAnimalImages != null && mAnimalImages.size() > 0) {
            AnimalImage animalImageToBeShown = null;
            for (AnimalImage animalImage : mAnimalImages) {
                if (animalImage.isDefault()) {
                    animalImageToBeShown = animalImage;
                    break;
                }
            }
            if (animalImageToBeShown != null) {
                mDefaultPhotoChangeListener.showSelectedAnimalImage(animalImageToBeShown.getImageUri());
            } else {
                Log.e(LOG_TAG, "ERROR!!! updateAnimalImages: Animal Images found but no default image");
                mDefaultPhotoChangeListener.showDefaultImage();
            }
        } else {
            mDefaultPhotoChangeListener.showDefaultImage();
        }
    }
    @Override
    public void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
        mAnimalConfigView.updateAnimalAttributes(animalAttributes);
    }
    @Override
    public void updateAnimalDescriptionField(String description) {
        mAnimalConfigView.updateAnimalDescriptionField(description);
    }
    @Override
    public void updateAnimalSkuField(String sku) {
        mAnimalConfigView.updateAnimalSkuField(sku);
    }
    @Override
    public void updateAnimalNameField(String name) {
        mAnimalConfigView.updateAnimalNameField(name);
    }
    @Override
    public void onCategorySelected(String categoryName) {
        if (categoryName.equals(AnimalConfigContract.CATEGORY_OTHER)) {
            mAnimalConfigView.showCategoryOtherEditTextField();
        } else {
            mAnimalConfigView.hideCategoryOtherEditTextField();
        }
    }
    @Override
    public void onSave(String animalName, String animalSku, String animalDescription,
                       String categorySelected, String categoryOtherText,
                       ArrayList<AnimalAttribute> animalAttributes) {
        mAnimalConfigView.showProgressIndicator(R.string.animal_config_status_saving);
        if (!mIsExistingAnimalRestored && !mIsAnimalSkuValid) {
            mAnimalConfigView.hideProgressIndicator();
            if (TextUtils.isEmpty(animalSku)) {
                mAnimalConfigView.showAnimalSkuEmptyError();
            } else {
                mAnimalConfigView.showAnimalSkuConflictError();
            }
            return;
        }
        if (TextUtils.isEmpty(animalName) || TextUtils.isEmpty(animalSku) ||
                TextUtils.isEmpty(animalDescription) || TextUtils.isEmpty(categorySelected) ||
                (categorySelected.equals(AnimalConfigContract.CATEGORY_OTHER)
                        && TextUtils.isEmpty(categoryOtherText))) {
            mAnimalConfigView.hideProgressIndicator();
            mAnimalConfigView.showEmptyFieldsValidationError();
            return;
        }
        Iterator<AnimalAttribute> animalAttributeIterator = animalAttributes.iterator();
        ArrayList<String> animalAttributeNames = new ArrayList<>();
        while (animalAttributeIterator.hasNext()) {
            AnimalAttribute animalAttribute = animalAttributeIterator.next();
            String attributeName = animalAttribute.getAttributeName();
            String attributeValue = animalAttribute.getAttributeValue();
            if (TextUtils.isEmpty(attributeName) && TextUtils.isEmpty(attributeValue)) {
                animalAttributeIterator.remove();
            } else if (TextUtils.isEmpty(attributeName) || TextUtils.isEmpty(attributeValue)) {
                mAnimalConfigView.hideProgressIndicator();
                mAnimalConfigView.showAttributesPartialValidationError();
                return;
            }
            if (!TextUtils.isEmpty(attributeName)) {
                if (animalAttributeNames.contains(attributeName)) {
                    mAnimalConfigView.hideProgressIndicator();
                    mAnimalConfigView.showAttributeNameConflictError(attributeName);
                    return;
                } else {
                    animalAttributeNames.add(attributeName);
                }
            }
        }
        Animal newAnimal = createAnimalForUpdate(animalName, animalSku, animalDescription,
                categorySelected, categoryOtherText, animalAttributes, mAnimalImages);
        if (mAnimalId > AnimalConfigContract.NEW_ANIMALS_INT) {
            saveUpdatedAnimal(mExistingAnimal, newAnimal);
        } else {
            saveNewAnimal(newAnimal);
        }
    }
    @Override
    public void validateAnimalSku(String animalSku) {
        if (TextUtils.isEmpty(animalSku)) {
            mAnimalConfigView.showAnimalSkuEmptyError();
            return;
        }
        mStoreRepository.getAnimalSkuUniqueness(animalSku, new DataRepository.GetQueryCallback<Boolean>() {
            @Override
            public void onResults(Boolean results) {
                updateAndSyncAnimalSkuValidity(results);
            }
            @Override
            public void onEmpty() {
            }
        });
    }
    private Animal createAnimalForUpdate(String animalName, String animalSku,
                                           String animalDescription, String categorySelected,
                                           String categoryOtherText,
                                           ArrayList<AnimalAttribute> animalAttributes,
                                           ArrayList<AnimalImage> animalImages) {
        String categoryName = categorySelected;
        if (categorySelected.equals(AnimalConfigContract.CATEGORY_OTHER)) {
            categoryName = categoryOtherText;
        }
        return new Animal.Builder()
                .setId(mAnimalId)
                .setName(animalName)
                .setSku(animalSku)
                .setDescription(animalDescription)
                .setCategory(categoryName)
                .setAnimalAttributes(animalAttributes)
                .setAnimalImages(animalImages)
                .createAnimal();
    }
    private void saveUpdatedAnimal(Animal existingAnimal, Animal newAnimal) {
        mStoreRepository.saveUpdatedAnimal(existingAnimal, newAnimal,
                new DataRepository.DataOperationsCallback() {
                    @Override
                    public void onSuccess() {
                        mAnimalConfigView.hideProgressIndicator();
                        doSetResult(AnimalConfigActivity.RESULT_EDIT_ANIMALS, newAnimal.getId(), newAnimal.getSku());
                    }
                    @Override
                    public void onFailure(int messageId, @Nullable Object... args) {
                        mAnimalConfigView.hideProgressIndicator();
                        mAnimalConfigView.showError(messageId, args);
                    }
                });
    }
    private void saveNewAnimal(Animal newAnimal) {
        mStoreRepository.saveNewAnimal(newAnimal, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mAnimalConfigView.hideProgressIndicator();
                doSetResult(AnimalConfigActivity.RESULT_ADD_ANIMALS, newAnimal.getId(), newAnimal.getSku());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mAnimalConfigView.hideProgressIndicator();
                mAnimalConfigView.showError(messageId, args);
            }
        });
    }
    @Override
    public void openAnimalImages() {
        if (mAnimalImages == null) {
            mAnimalImages = new ArrayList<>();
        }
        mAnimalConfigNavigator.launchAnimalImagesView(mAnimalImages);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            if (requestCode == AnimalImageActivity.REQUEST_ANIMALS_IMAGE) {
                if (data != null && data.hasExtra(AnimalImageActivity.EXTRA_ANIMALS_IMAGES)) {
                    ArrayList<AnimalImage> animalImages = data.getParcelableArrayListExtra(AnimalImageActivity.EXTRA_ANIMALS_IMAGES);
                    updateAnimalImages(animalImages);
                    mIsExtraAnimalImagesReceived = true;
                }
            }
        }
    }
    private void onExtraAnimalImagesReceived() {
        if (mAnimalId != AnimalConfigContract.NEW_ANIMALS_INT) {
            int noOfExistingAnimalImages = mExistingAnimal.getAnimalImages().size();
            int noOfAnimalImagesReceived = mAnimalImages.size();
            if (!(noOfExistingAnimalImages == 0 && noOfAnimalImagesReceived == noOfExistingAnimalImages)) {
                mStoreRepository.saveAnimalImages(mExistingAnimal, mAnimalImages, new DataRepository.DataOperationsCallback() {
                    @Override
                    public void onSuccess() {
                        mAnimalConfigView.showUpdateImagesSuccess();
                    }
                    @Override
                    public void onFailure(int messageId, @Nullable Object... args) {
                        mAnimalConfigView.showError(messageId, args);
                    }
                });
            }
        }
    }
    @Override
    public void onUpOrBackAction() {
        if (mIsAnimalNameEntered) {
            mAnimalConfigView.showDiscardDialog();
        } else {
            finishActivity();
        }
    }
    @Override
    public void finishActivity() {
        deleteUncommittedImages();
        doCancel();
    }
    @Override
    public void showDeleteAnimalDialog() {
        mAnimalConfigView.showDeleteAnimalDialog();
    }
    private void deleteUncommittedImages() {
        if (mAnimalId == AnimalConfigContract.NEW_ANIMALS_INT && mAnimalImages != null && mAnimalImages.size() > 0) {
            ArrayList<String> fileContentUriList = new ArrayList<>();
            for (AnimalImage animalImage : mAnimalImages) {
                fileContentUriList.add(animalImage.getImageUri());
            }
            mStoreRepository.deleteImageFilesSilently(fileContentUriList);
        }
    }
    @Override
    public void deleteAnimal() {
        mAnimalConfigView.showProgressIndicator(R.string.animal_config_status_deleting);
        ArrayList<String> fileContentUriList = new ArrayList<>();
        for (AnimalImage animalImage : mAnimalImages) {
            fileContentUriList.add(animalImage.getImageUri());
        }
        mStoreRepository.deleteAnimalById(mAnimalId, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mAnimalConfigView.hideProgressIndicator();
                if (fileContentUriList.size() > 0) {
                    mStoreRepository.deleteImageFilesSilently(fileContentUriList);
                }
                doSetResult(AnimalConfigActivity.RESULT_DELETE_ANIMALS, mExistingAnimal.getId(), mExistingAnimal.getSku());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mAnimalConfigView.hideProgressIndicator();
                mAnimalConfigView.showError(messageId, args);
            }
        });
    }
    @Override
    public void triggerFocusLost() {
        mAnimalConfigView.triggerFocusLost();
    }
    @Override
    public void doSetResult(int resultCode, int animalId, @NonNull String animalSku) {
        mAnimalConfigNavigator.doSetResult(resultCode, animalId, animalSku);
    }
    @Override
    public void doCancel() {
        mAnimalConfigNavigator.doCancel();
    }
}