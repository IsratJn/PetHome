package com.example.pethome.storeapp.ui.animals.image;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.FileRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.models.AnimalImage;

import java.util.ArrayList;
import java.util.Iterator;
public class AnimalImagePresenter implements AnimalImageContract.Presenter {
    private static final String LOG_TAG = AnimalImagePresenter.class.getSimpleName();
    @NonNull
    private final AnimalImageContract.View mAnimalImageView;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final PhotoGridDeleteModeListener mPhotoGridDeleteModeListener;
    private final AnimalImageNavigator mAnimalImageNavigator;
    private final SelectedPhotoActionsListener mSelectedPhotoActionsListener;
    private ArrayList<ImageSelectionTracker> mImageSelectionTrackers;
    private ArrayList<AnimalImage> mAnimalImages;
    private ArrayList<String> mAnimalImageUris;
    private AnimalImage mLastChosenAnimalImage;
    @AnimalImageContract.PhotoGridSelectModeDef
    private String mGridMode;
    AnimalImagePresenter(@NonNull StoreRepository storeRepository,
                         @NonNull AnimalImageContract.View animalImageView,
                         @NonNull PhotoGridDeleteModeListener photoGridDeleteModeListener,
                         @NonNull AnimalImageNavigator animalImageNavigator,
                         @NonNull SelectedPhotoActionsListener selectedPhotoActionsListener) {
        mStoreRepository = storeRepository;
        mAnimalImageView = animalImageView;
        mPhotoGridDeleteModeListener = photoGridDeleteModeListener;
        mAnimalImageNavigator = animalImageNavigator;
        mSelectedPhotoActionsListener = selectedPhotoActionsListener;
        mImageSelectionTrackers = new ArrayList<>();
        mAnimalImageView.setPresenter(this);
    }
    @Override
    public void start() {
    }
    private ArrayList<AnimalImage> deepCopyAnimalImages(ArrayList<AnimalImage> animalImages) {
        ArrayList<AnimalImage> newAnimalImages = new ArrayList<>();
        for (AnimalImage animalImage : animalImages) {
            newAnimalImages.add((AnimalImage) animalImage.clone());
        }
        return newAnimalImages;
    }
    private ArrayList<ImageSelectionTracker> deepCopyImageSelectionTrackers(ArrayList<ImageSelectionTracker> imageSelectionTrackers) {
        ArrayList<ImageSelectionTracker> newImageSelectionTrackers = new ArrayList<>();
        for (ImageSelectionTracker selectionTracker : imageSelectionTrackers) {
            newImageSelectionTrackers.add((ImageSelectionTracker) selectionTracker.clone());
        }
        return newImageSelectionTrackers;
    }
    @Override
    public void openImagePickerDialog() {
        mAnimalImageView.showImagePickerDialog();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            if (requestCode == AnimalImageContract.REQUEST_IMAGE_PICK) {
                if (data.getData() != null) {
                    mAnimalImageView.showProgressIndicator(R.string.animal_image_status_loading_picked_single);
                    Uri fileContentUri = data.getData();
                    mStoreRepository.takePersistablePermissions(fileContentUri, data.getFlags());
                    String fileContentUriStr = fileContentUri.toString();
                    if (!mAnimalImageUris.contains(fileContentUriStr)) {
                        mAnimalImageUris.add(fileContentUriStr);
                        AnimalImage newAnimalImage
                                = new AnimalImage.Builder()
                                .setImageUri(fileContentUriStr)
                                .createAnimalImage();
                        mAnimalImages.add(newAnimalImage);
                        mAnimalImageView.showGridView();
                        submitListToAdapter(mAnimalImages);
                        mGridMode = AnimalImageContract.MODE_SELECT;
                        showAnimalImageAsSelected(mAnimalImages.size() - 1, newAnimalImage, mGridMode);
                    } else {
                        mAnimalImageView.showImageAlreadyPicked();
                    }
                    mAnimalImageView.hideProgressIndicator();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            int noOfClips = clipData.getItemCount();
                            if (noOfClips > 0) {
                                mAnimalImageView.showProgressIndicator(R.string.animal_image_status_loading_picked_multiple);
                                for (int index = 0; index < noOfClips; index++) {
                                    ClipData.Item clipItem = clipData.getItemAt(index);
                                    Uri fileContentUri = clipItem.getUri();
                                    mStoreRepository.takePersistablePermissions(fileContentUri, data.getFlags());
                                    String fileContentUriStr = fileContentUri.toString();
                                    if (!mAnimalImageUris.contains(fileContentUriStr)) {
                                        mAnimalImageUris.add(fileContentUriStr);
                                        AnimalImage newAnimalImage = new AnimalImage.Builder()
                                                .setImageUri(fileContentUri.toString())
                                                .createAnimalImage();
                                        mAnimalImages.add(newAnimalImage);
                                    } else {
                                        mAnimalImageView.showImageAlreadyPicked();
                                    }
                                }
                                submitListToAdapter(mAnimalImages);
                                if (mAnimalImages.size() > 0) {
                                    mAnimalImageView.showGridView();
                                    mGridMode = AnimalImageContract.MODE_SELECT;
                                    int lastItemPosition = mAnimalImages.size() - 1;
                                    showAnimalImageAsSelected(lastItemPosition, mAnimalImages.get(lastItemPosition), mGridMode);
                                }
                                mAnimalImageView.hideProgressIndicator();
                            }
                        }
                    }
                }
            } else if (requestCode == AnimalImageContract.REQUEST_IMAGE_CAPTURE) {
                mAnimalImageView.onImageCaptured();
            }
        }
    }
    @Override
    public void submitListToAdapter(ArrayList<AnimalImage> animalImages) {
        mAnimalImageView.submitListToAdapter(deepCopyAnimalImages(animalImages));
    }
    @Override
    public void saveImageToFile(Context context, @Nullable Uri tempCaptureImageUri) {
        if (tempCaptureImageUri == null) {
            return;
        }
        mAnimalImageView.showProgressIndicator(R.string.animal_image_status_saving_capture);
        mStoreRepository.saveImageToFile(context, tempCaptureImageUri, new FileRepository.FileOperationsCallback<Uri>() {
            @Override
            public void onSuccess(Uri results) {
                String fileContentUriStr = results.toString();
                mAnimalImageUris.add(fileContentUriStr);
                AnimalImage newAnimalImage = new AnimalImage.Builder()
                        .setImageUri(fileContentUriStr)
                        .createAnimalImage();
                mAnimalImages.add(newAnimalImage);
                mAnimalImageView.showGridView();
                submitListToAdapter(mAnimalImages);
                mGridMode = AnimalImageContract.MODE_SELECT;
                showAnimalImageAsSelected(mAnimalImages.size() - 1, newAnimalImage, mGridMode);
                mAnimalImageView.hideProgressIndicator();
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mAnimalImageView.hideProgressIndicator();
                mAnimalImageView.showError(messageId, args);
            }
        });
    }
    @Override
    public void onItemImageClicked(int itemPosition, AnimalImage animalImage,
                                   @AnimalImageContract.PhotoGridSelectModeDef String gridMode) {
        mGridMode = gridMode;
        ArrayList<ImageSelectionTracker> tempImageSelectionTrackers = deepCopyImageSelectionTrackers(mImageSelectionTrackers);
        boolean isSameItemClicked = onSameItemImageClicked(animalImage);
        if (mGridMode.equals(AnimalImageContract.MODE_SELECT)) {
            if (!isSameItemClicked) {
                Iterator<ImageSelectionTracker> trackerIterator = mImageSelectionTrackers.iterator();
                while (trackerIterator.hasNext()) {
                    ImageSelectionTracker tracker = trackerIterator.next();
                    if (tracker.getPhotoGridMode().equals(mGridMode)) {
                        if (tracker.isSelected()) {
                            tracker.setSelected(false);
                        } else {
                            trackerIterator.remove();
                        }
                    } else {
                        trackerIterator.remove();
                    }
                }
            } else {
                mImageSelectionTrackers = tempImageSelectionTrackers;
                return;
            }
        } else if (mGridMode.equals(AnimalImageContract.MODE_DELETE)) {
            Iterator<ImageSelectionTracker> trackerIterator = mImageSelectionTrackers.iterator();
            while (trackerIterator.hasNext()) {
                ImageSelectionTracker tracker = trackerIterator.next();
                if (tracker.getPhotoGridMode().equals(mGridMode)) {
                    if (!tracker.getImageContentUri().equals(animalImage.getImageUri())
                            && !tracker.isSelected()) {
                        trackerIterator.remove();
                    }
                } else {
                    trackerIterator.remove();
                }
            }
        }
        if (!isSameItemClicked) {
            addItemImageToSelectionTracker(itemPosition, animalImage, mGridMode);
        }
        updateSelectionStateAndSyncTrackers();
        if (mGridMode.equals(AnimalImageContract.MODE_DELETE)) {
            showDeleteCount();
        }
    }
    @Override
    public void onItemImageLongClicked(int itemPosition, AnimalImage animalImage,
                                       @AnimalImageContract.PhotoGridSelectModeDef String gridMode) {
        if (gridMode.equals(AnimalImageContract.MODE_DELETE)
                && !gridMode.equals(mGridMode)) {
            clearSelectedItems();
            mGridMode = gridMode;
            mPhotoGridDeleteModeListener.onGridItemDeleteMode();
        }
        if (!onSameItemImageClicked(animalImage)) {
            addItemImageToSelectionTracker(itemPosition, animalImage, mGridMode);
        }
        updateSelectionStateAndSyncTrackers();
        showDeleteCount();
    }
    @Override
    public void showDeleteCount() {
        if (mImageSelectionTrackers != null && mImageSelectionTrackers.size() > 0 && mGridMode.equals(AnimalImageContract.MODE_DELETE)) {
            int liveDeleteItemCount = 0;
            for (ImageSelectionTracker selectionTracker : mImageSelectionTrackers) {
                if (selectionTracker.getPhotoGridMode().equals(mGridMode)
                        && selectionTracker.isSelected()) {
                    liveDeleteItemCount++;
                }
            }
            mPhotoGridDeleteModeListener.showSelectedItemCount(liveDeleteItemCount);
        }
    }
    private boolean onSameItemImageClicked(AnimalImage animalImage) {
        boolean previouslySelected = false;
        for (ImageSelectionTracker selectionTracker : mImageSelectionTrackers) {
            if (selectionTracker.getImageContentUri().equals(animalImage.getImageUri())
                    && selectionTracker.isSelected()) {
                selectionTracker.setSelected(false);
                previouslySelected = true;
                break;
            }
        }
        return previouslySelected;
    }
    private void addItemImageToSelectionTracker(int itemPosition, AnimalImage animalImage,
                                                @AnimalImageContract.PhotoGridSelectModeDef String gridMode) {
        ImageSelectionTracker tracker = new ImageSelectionTracker.Builder()
                .setPosition(itemPosition)
                .setImageContentUri(animalImage.getImageUri())
                .setPhotoGridMode(gridMode)
                .setSelected(true)
                .createTracker();
        mImageSelectionTrackers.add(tracker);
    }
    private void showAnimalImageAsSelected(int itemPosition, AnimalImage animalImage,
                                            @AnimalImageContract.PhotoGridSelectModeDef String gridMode) {
        clearSelectedItems();
        addItemImageToSelectionTracker(itemPosition, animalImage, gridMode);
        updateSelectionStateAndSyncTrackers();
    }
    private void updateSelectionStateAndSyncTrackers() {
        mAnimalImageView.updateGridItemsState(mImageSelectionTrackers);
        mAnimalImageView.syncSelectionTrackers(mImageSelectionTrackers);
    }
    @Override
    public void deleteSelection() {
        mAnimalImageView.showProgressIndicator(R.string.animal_image_status_deleting);
        ArrayList<String> fileContentUriList = new ArrayList<>();
        for (ImageSelectionTracker selectionTracker : mImageSelectionTrackers) {
            fileContentUriList.add(selectionTracker.getImageContentUri());
        }
        mStoreRepository.deleteImageFiles(fileContentUriList, new FileRepository.FileOperationsCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean results) {
                startDeleteItems();
                mAnimalImageView.showDeleteSuccess();
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                startDeleteItems();
                mAnimalImageView.showError(messageId, args);
            }
            private void startDeleteItems() {
                Iterator<AnimalImage> animalImagesIterator = mAnimalImages.iterator();
                while (animalImagesIterator.hasNext()) {
                    AnimalImage currentAnimalImage = animalImagesIterator.next();
                    if (fileContentUriList.contains(currentAnimalImage.getImageUri())) {
                        animalImagesIterator.remove();
                    }
                }
                mImageSelectionTrackers.clear();
                updateSelectionStateAndSyncTrackers();
                mAnimalImageUris.removeAll(fileContentUriList);
                submitListToAdapter(mAnimalImages);
                restoreLastChosenAnimalImage();
                mAnimalImageView.hideProgressIndicator();
            }
        });
    }
    @Override
    public void clearSelectedItems() {
        if (mImageSelectionTrackers.size() > 0) {
            for (ImageSelectionTracker selectionTracker : mImageSelectionTrackers) {
                if (selectionTracker.isSelected()) {
                    selectionTracker.setSelected(false);
                }
            }
            mAnimalImageView.updateGridItemsState(mImageSelectionTrackers);
            mImageSelectionTrackers.clear();
            updateSelectionStateAndSyncTrackers();
        }
    }
    @Override
    public void onDeleteModeExit() {
        clearSelectedItems();
        restoreLastChosenAnimalImage();
    }
    @Override
    public void restoreSelectionTrackers(ArrayList<ImageSelectionTracker> imageSelectionTrackers) {
        if (imageSelectionTrackers != null && imageSelectionTrackers.size() > 0) {
            mImageSelectionTrackers = imageSelectionTrackers;
            updateSelectionStateAndSyncTrackers();
            restoreMode();
        }
    }
    private void restoreMode() {
        int noOfItemsMarkedForSelect = 0;
        int noOfItemsMarkedForDelete = 0;
        int noOfItemsMarkedAndSelectedForDelete = 0;
        for (ImageSelectionTracker selectionTracker : mImageSelectionTrackers) {
            if (selectionTracker.getPhotoGridMode().equals(AnimalImageContract.MODE_SELECT)) {
                noOfItemsMarkedForSelect++;
            } else if (selectionTracker.getPhotoGridMode().equals(AnimalImageContract.MODE_DELETE)) {
                noOfItemsMarkedForDelete++;
                if (selectionTracker.isSelected()) {
                    noOfItemsMarkedAndSelectedForDelete++;
                }
            }
        }
        if (noOfItemsMarkedForDelete + noOfItemsMarkedForSelect == 0) {
            mGridMode = "";
        } else if (noOfItemsMarkedForDelete > noOfItemsMarkedForSelect) {
            mGridMode = AnimalImageContract.MODE_DELETE;
            mPhotoGridDeleteModeListener.onGridItemDeleteMode();
            mPhotoGridDeleteModeListener.showSelectedItemCount(noOfItemsMarkedAndSelectedForDelete);
        } else {
            mGridMode = AnimalImageContract.MODE_SELECT;
        }
    }
    @Override
    public void restoreAnimalImages(ArrayList<AnimalImage> animalImages) {
        mAnimalImages = animalImages;
        submitListToAdapter(mAnimalImages);
        restorePreselectedAnimalImage();
        mAnimalImageUris = new ArrayList<>();
        for (AnimalImage animalImage : mAnimalImages) {
            mAnimalImageUris.add(animalImage.getImageUri());
        }
    }
    private void restorePreselectedAnimalImage() {
        int noOfAnimalImages = mAnimalImages.size();
        if (noOfAnimalImages > 0) {
            mAnimalImageView.showGridView();
            AnimalImage animalImageToBeShown = null;
            int itemPosition = -1;
            for (int index = 0; index < noOfAnimalImages; index++) {
                AnimalImage animalImage = mAnimalImages.get(index);
                if (animalImage.isDefault()) {
                    animalImageToBeShown = animalImage;
                    itemPosition = index;
                    break;
                }
            }
            if (animalImageToBeShown == null) {
                itemPosition = 0;
                animalImageToBeShown = mAnimalImages.get(itemPosition);
            }
            mGridMode = AnimalImageContract.MODE_SELECT;
            showAnimalImageAsSelected(itemPosition, animalImageToBeShown, mGridMode);
        } else {
            mAnimalImageView.hideGridView();
            mSelectedPhotoActionsListener.showDefaultImage();
        }
    }
    @Override
    public void syncLastChosenAnimalImage(AnimalImage animalImage) {
        mLastChosenAnimalImage = animalImage;
    }
    private void restoreLastChosenAnimalImage() {
        if (mLastChosenAnimalImage != null && mAnimalImages != null && mAnimalImages.size() > 0) {
            int itemPosition = -1;
            int noOfAnimalImages = mAnimalImages.size();
            for (int index = 0; index < noOfAnimalImages; index++) {
                AnimalImage animalImage = mAnimalImages.get(index);
                if (animalImage.getImageUri().equals(mLastChosenAnimalImage.getImageUri())) {
                    itemPosition = index;
                }
            }
            if (itemPosition > -1) {
                mGridMode = AnimalImageContract.MODE_SELECT;
                showAnimalImageAsSelected(itemPosition, mLastChosenAnimalImage, mGridMode);
            } else {
                mLastChosenAnimalImage = null;
                restorePreselectedAnimalImage();
            }
        } else if (mAnimalImages != null && mAnimalImages.size() > 0) {
            restorePreselectedAnimalImage();
        } else {
            mAnimalImageView.hideGridView();
            mSelectedPhotoActionsListener.showDefaultImage();
        }
    }
    @Override
    public void onSelectAction() {
        if (mLastChosenAnimalImage != null && mAnimalImages != null && mAnimalImages.size() > 0) {
            boolean oldItemUnSelected = false;
            boolean newItemSelected = false;
            if (mAnimalImages.size() == 1) {
                oldItemUnSelected = true;
            }
            for (AnimalImage animalImage : mAnimalImages) {
                if (!oldItemUnSelected && animalImage.isDefault()) {
                    animalImage.setDefault(false);
                    oldItemUnSelected = true;
                }
                if (!newItemSelected && animalImage.getImageUri().equals(mLastChosenAnimalImage.getImageUri())) {
                    animalImage.setDefault(true);
                    newItemSelected = true;
                }
                if (oldItemUnSelected && newItemSelected) {
                    break;
                }
            }
        }
        doSetResult();
    }
    @Override
    public void onIgnoreAction() {
        if (mAnimalImages != null && mAnimalImages.size() > 0) {
            boolean anyItemSelected = false;
            for (AnimalImage animalImage : mAnimalImages) {
                if (animalImage.isDefault()) {
                    anyItemSelected = true;
                    break;
                }
            }
            if (!anyItemSelected) {
                if (mLastChosenAnimalImage != null) {
                    for (AnimalImage animalImage : mAnimalImages) {
                        if (animalImage.getImageUri().equals(mLastChosenAnimalImage.getImageUri())) {
                            animalImage.setDefault(true);
                            anyItemSelected = true;
                        }
                    }
                    if (!anyItemSelected) {
                        mAnimalImages.get(0).setDefault(true);
                    }
                } else {
                    mAnimalImages.get(0).setDefault(true);
                }
            }
        }
        doSetResult();
    }
    @Override
    public void onUpOrBackAction() {
        if (mLastChosenAnimalImage != null) {
            mAnimalImageView.showDiscardDialog();
        } else {
            doSetResult();
        }
    }
    @Override
    public void doSetResult() {
        mAnimalImageNavigator.doSetResult(mAnimalImages);
    }
    @Override
    public void showSelectedImage(Bitmap bitmap, String imageUri) {
        mSelectedPhotoActionsListener.showSelectedImage(bitmap, imageUri);
    }
}