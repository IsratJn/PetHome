package com.example.pethome.storeapp.ui.adoptions.config;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.models.Animal;
import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;
import com.example.pethome.storeapp.ui.animals.config.DefaultPhotoChangeListener;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.ui.rescuers.config.RescuerConfigActivity;
import java.util.ArrayList;
import java.util.List;
public class AdoptionsConfigPresenter implements AdoptionsConfigContract.Presenter {
    private static final String LOG_TAG = AdoptionsConfigPresenter.class.getSimpleName();
    private final int mAnimalId;
    @NonNull
    private final AdoptionsConfigContract.View mAdoptionsConfigView;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final AdoptionsConfigNavigator mAdoptionsConfigNavigator;
    private final DefaultPhotoChangeListener mDefaultPhotoChangeListener;
    private String mAnimalSku;
    private String mAnimalName;
    private boolean mIsAnimalRestored;
    private boolean mAreRescuersRestored;
    private List<AnimalRescuerAdoptions> mExistingAnimalRescuerAdoptionsList;
    private ArrayList<AnimalImage> mAnimalImages;
    private int mOldTotalAvailableQuantity;
    private int mNewTotalAvailableQuantity;
    private AnimalImage mAnimalImageToBeShown;
    AdoptionsConfigPresenter(int animalId,
                             @NonNull StoreRepository storeRepository,
                             @NonNull AdoptionsConfigContract.View adoptionsConfigView,
                             @NonNull AdoptionsConfigNavigator adoptionsConfigNavigator,
                             @NonNull DefaultPhotoChangeListener defaultPhotoChangeListener) {
        mAnimalId = animalId;
        mStoreRepository = storeRepository;
        mAdoptionsConfigView = adoptionsConfigView;
        mAdoptionsConfigNavigator = adoptionsConfigNavigator;
        mDefaultPhotoChangeListener = defaultPhotoChangeListener;
        mAdoptionsConfigView.setPresenter(this);
    }
    @Override
    public void start() {
        loadAnimalDetails();
        loadAnimalRescuers();
    }
    @Override
    public void updateAndSyncAnimalState(boolean isAnimalRestored) {
        mIsAnimalRestored = isAnimalRestored;
        mAdoptionsConfigView.syncAnimalState(mIsAnimalRestored);
    }
    @Override
    public void updateAndSyncRescuersState(boolean areRescuersRestored) {
        mAreRescuersRestored = areRescuersRestored;
        mAdoptionsConfigView.syncRescuersState(mAreRescuersRestored);
    }
    @Override
    public void updateAndSyncOldTotalAvailability(int oldTotalAvailableQuantity) {
        mOldTotalAvailableQuantity = oldTotalAvailableQuantity;
        mAdoptionsConfigView.syncOldTotalAvailability(mOldTotalAvailableQuantity);
    }
    private void loadAnimalDetails() {
        if (!mIsAnimalRestored) {
            mAdoptionsConfigView.showProgressIndicator(R.string.animal_config_status_loading_existing_animal);
            mStoreRepository.getAnimalDetailsById(mAnimalId, new DataRepository.GetQueryCallback<Animal>() {
                @Override
                public void onResults(Animal animal) {
                    updateAnimalName(animal.getName());
                    updateAnimalSku(animal.getSku());
                    updateAnimalCategory(animal.getCategory());
                    updateAnimalDescription(animal.getDescription());
                    updateAnimalImage(animal.getAnimalImages());
                    updateAnimalAttributes(animal.getAnimalAttributes());
                    updateAndSyncAnimalState(true);
                    mAdoptionsConfigView.hideProgressIndicator();
                }
                @Override
                public void onFailure(@StringRes int messageId, @Nullable Object... args) {
                    mAdoptionsConfigView.hideProgressIndicator();
                    mAdoptionsConfigView.showError(messageId, args);
                }
                @Override
                public void onEmpty() {
                }
            });
        }
    }
    @Override
    public void updateAnimalName(String animalName) {
        mAnimalName = animalName;
        mAdoptionsConfigView.updateAnimalName(animalName);
    }
    @Override
    public void updateAnimalSku(String animalSku) {
        mAnimalSku = animalSku;
        mAdoptionsConfigView.updateAnimalSku(animalSku);
    }
    public void updateAnimalCategory(String animalCategory) {
        mAdoptionsConfigView.updateAnimalCategory(animalCategory);
    }
    public void updateAnimalDescription(String description) {
        mAdoptionsConfigView.updateAnimalDescription(description);
    }
    public void updateAnimalImage(ArrayList<AnimalImage> animalImages) {
        if (animalImages != null && animalImages.size() > 0) {
            mAnimalImages = animalImages;
            mAdoptionsConfigView.updateAnimalImages(mAnimalImages);
            mAnimalImageToBeShown = null;
            for (AnimalImage animalImage : mAnimalImages) {
                if (animalImage.isDefault()) {
                    mAnimalImageToBeShown = animalImage;
                    break;
                }
            }
            if (mAnimalImageToBeShown != null) {
                mDefaultPhotoChangeListener.showSelectedAnimalImage(mAnimalImageToBeShown.getImageUri());
            } else {
                Log.e(LOG_TAG, "ERROR!!! updateAnimalImages: Animal Images found but no default image");
                mDefaultPhotoChangeListener.showDefaultImage();
            }
        } else {
            mDefaultPhotoChangeListener.showDefaultImage();
            mAnimalImages = new ArrayList<>();
        }
    }
    public void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
        mAdoptionsConfigView.updateAnimalAttributes(animalAttributes);
    }
    private void loadAnimalRescuers() {
        if (!mAreRescuersRestored) {
            mAdoptionsConfigView.showProgressIndicator(R.string.adoptions_config_status_loading_rescuers);
            mStoreRepository.getAnimalRescuersAdoptionsInfo(mAnimalId, new DataRepository.GetQueryCallback<List<AnimalRescuerAdoptions>>() {
                @Override
                public void onResults(List<AnimalRescuerAdoptions> animalRescuerAdoptionsList) {
                    updateAnimalRescuerAdoptionsList(animalRescuerAdoptionsList);
                    updateAndSyncRescuersState(true);
                    mAdoptionsConfigView.hideProgressIndicator();
                }
                @Override
                public void onEmpty() {
                    updateAnimalRescuerAdoptionsList(new ArrayList<>());
                    mAdoptionsConfigView.hideProgressIndicator();
                }
            });
        }
    }
    public void updateAnimalRescuerAdoptionsList(List<AnimalRescuerAdoptions> animalRescuerAdoptionsList) {
        mExistingAnimalRescuerAdoptionsList = animalRescuerAdoptionsList;
        int totalAvailableQuantity = 0;
        ArrayList<AnimalRescuerAdoptions> newAnimalRescuerAdoptionsList = new ArrayList<>();
        for (AnimalRescuerAdoptions animalRescuerAdoptions : animalRescuerAdoptionsList) {
            newAnimalRescuerAdoptionsList.add((AnimalRescuerAdoptions) animalRescuerAdoptions.clone());
            totalAvailableQuantity += animalRescuerAdoptions.getAvailableQuantity();
        }
        updateAvailability(totalAvailableQuantity);
        updateAndSyncOldTotalAvailability(totalAvailableQuantity);
        mAdoptionsConfigView.loadAnimalRescuersData(newAnimalRescuerAdoptionsList);
    }
    @Override
    public void editAnimal(int animalId) {
        mAdoptionsConfigNavigator.launchEditAnimal(animalId);
    }
    @Override
    public void editRescuer(int rescuerId) {
        mAdoptionsConfigNavigator.launchEditRescuer(rescuerId);
    }
    @Override
    public void onAnimalRescuerSwiped(String rescuerCode) {
        mAdoptionsConfigView.showAnimalRescuerSwiped(rescuerCode);
    }
    @Override
    public void updateAvailability(int totalAvailableQuantity) {
        mNewTotalAvailableQuantity = totalAvailableQuantity;
        if (mNewTotalAvailableQuantity > 0) {
            mAdoptionsConfigView.updateAvailability(mNewTotalAvailableQuantity);
        } else {
            mAdoptionsConfigView.showOutOfStockAlert();
        }
    }
    @Override
    public void changeAvailability(int changeInAvailableQuantity) {
        updateAvailability(mNewTotalAvailableQuantity + changeInAvailableQuantity);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode >= FragmentActivity.RESULT_FIRST_USER) {
            if (requestCode == RescuerConfigActivity.REQUEST_EDIT_RESCUER) {
                if (resultCode == RescuerConfigActivity.RESULT_EDIT_RESCUER) {
                    updateAndSyncRescuersState(false);
                    mAdoptionsConfigView.showUpdateRescuerSuccess(data.getStringExtra(RescuerConfigActivity.EXTRA_RESULT_RESCUER_CODE));
                } else if (resultCode == RescuerConfigActivity.RESULT_DELETE_RESCUER) {
                    updateAndSyncRescuersState(false);
                    mAdoptionsConfigView.showDeleteRescuerSuccess(data.getStringExtra(RescuerConfigActivity.EXTRA_RESULT_RESCUER_CODE));
                }
            } else if (requestCode == AnimalConfigActivity.REQUEST_EDIT_ANIMALS) {
                if (resultCode == AnimalConfigActivity.RESULT_EDIT_ANIMALS) {
                    updateAndSyncAnimalState(false);
                    mAdoptionsConfigView.showUpdateAnimalSuccess(data.getStringExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_SKU));
                } else if (resultCode == AnimalConfigActivity.RESULT_DELETE_ANIMALS) {
                    doSetResult(AnimalConfigActivity.RESULT_DELETE_ANIMALS, mAnimalId, mAnimalSku);
                }
            }
        }
    }
    @Override
    public void triggerFocusLost() {
        mAdoptionsConfigView.triggerFocusLost();
    }
    @Override
    public void onSave(ArrayList<AnimalRescuerAdoptions> updatedAnimalRescuerAdoptionsList) {
        mAdoptionsConfigView.showProgressIndicator(R.string.adoptions_config_status_saving);
        mStoreRepository.saveUpdatedAnimalAdoptionsInfo(mAnimalId, mAnimalSku,
                mExistingAnimalRescuerAdoptionsList,
                updatedAnimalRescuerAdoptionsList, new DataRepository.DataOperationsCallback() {
                    @Override
                    public void onSuccess() {
                        mAdoptionsConfigView.hideProgressIndicator();
                        doSetResult(AdoptionsConfigActivity.RESULT_EDIT_ADOPTIONS, mAnimalId, mAnimalSku);
                    }
                    @Override
                    public void onFailure(int messageId, @Nullable Object... args) {
                        mAdoptionsConfigView.hideProgressIndicator();
                        mAdoptionsConfigView.showError(messageId, args);
                    }
                });
    }
    @Override
    public void showDeleteAnimalDialog() {
        mAdoptionsConfigView.showDeleteAnimalDialog();
    }
    @Override
    public void deleteAnimal() {
        mAdoptionsConfigView.showProgressIndicator(R.string.animal_config_status_deleting);
        ArrayList<String> fileContentUriList = new ArrayList<>();
        for (AnimalImage animalImage : mAnimalImages) {
            fileContentUriList.add(animalImage.getImageUri());
        }
        mStoreRepository.deleteAnimalById(mAnimalId, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mAdoptionsConfigView.hideProgressIndicator();
                if (fileContentUriList.size() > 0) {
                    mStoreRepository.deleteImageFilesSilently(fileContentUriList);
                }
                doSetResult(AnimalConfigActivity.RESULT_DELETE_ANIMALS, mAnimalId, mAnimalSku);
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mAdoptionsConfigView.hideProgressIndicator();
                mAdoptionsConfigView.showError(messageId, args);
            }
        });
    }
    @Override
    public void doSetResult(int resultCode, int animalId, @NonNull String animalSku) {
        mAdoptionsConfigNavigator.doSetResult(resultCode, animalId, animalSku);
    }
    @Override
    public void doCancel() {
        mAdoptionsConfigNavigator.doCancel();
    }
    @Override
    public void onUpOrBackAction() {
        if (mNewTotalAvailableQuantity != mOldTotalAvailableQuantity) {
            mAdoptionsConfigView.showDiscardDialog();
        } else {
            finishActivity();
        }
    }
    @Override
    public void finishActivity() {
        doCancel();
    }
}
