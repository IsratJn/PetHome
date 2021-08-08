
package com.example.pethome.storeapp.ui.adoptions;

import android.content.Intent;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.LoaderProvider;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;
import com.example.pethome.storeapp.data.local.contracts.StoreContract;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.models.AdoptionsLite;
import com.example.pethome.storeapp.ui.adoptions.config.AdoptionsConfigActivity;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.utils.AppConstants;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdoptionsListPresenter implements AdoptionsListContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, DataRepository.CursorDataLoaderCallback {

    private static final String LOG_TAG = AdoptionsListPresenter.class.getSimpleName();
    private static final String CONTENT_OBSERVER_THREAD_NAME = "AnimalAdoptionsContentObserverThread";
    @NonNull
    private final AdoptionsListContract.View mAdoptionsListView;
    @NonNull
    private final LoaderProvider mLoaderProvider;
    @NonNull
    private final LoaderManager mLoaderManager;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final HandlerThread mContentObserverHandlerThread;
    private final AtomicBoolean mDeliveredNotification = new AtomicBoolean(false);
    private AnimalAdoptionsContentObserver mAnimalContentObserver;
    private AnimalAdoptionsContentObserver mRescuerContentObserver;
    private AnimalAdoptionsContentObserver mPriceContentObserver;
    private AnimalAdoptionsContentObserver mInventoryContentObserver;

    public AdoptionsListPresenter(@NonNull LoaderProvider loaderProvider,
                                  @NonNull LoaderManager loaderManager,
                                  @NonNull StoreRepository storeRepository,
                                  @NonNull AdoptionsListContract.View adoptionsListView) {
        mLoaderProvider = loaderProvider;
        mLoaderManager = loaderManager;
        mStoreRepository = storeRepository;
        mAdoptionsListView = adoptionsListView;

        mContentObserverHandlerThread = new HandlerThread(CONTENT_OBSERVER_THREAD_NAME);
        mContentObserverHandlerThread.start();

        mAdoptionsListView.setPresenter(this);
    }

    @Override
    public void start() {
        registerContentObservers();
        triggerAnimalAdoptionsLoad(false);
    }

    private void registerContentObservers() {
        if (mAnimalContentObserver == null) {

            mAnimalContentObserver = new AnimalAdoptionsContentObserver(AnimalContract.Animal.CONTENT_URI);
            mStoreRepository.registerContentObserver(AnimalContract.Animal.CONTENT_URI,
                    true, mAnimalContentObserver);
        }

        if (mRescuerContentObserver == null) {

            mRescuerContentObserver = new AnimalAdoptionsContentObserver(RescuerContract.Rescuer.CONTENT_URI);
            mStoreRepository.registerContentObserver(RescuerContract.Rescuer.CONTENT_URI,
                    true, mRescuerContentObserver);
        }

        if (mPriceContentObserver == null) {

            mPriceContentObserver = new AnimalAdoptionsContentObserver(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI);
            mStoreRepository.registerContentObserver(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI,
                    true, mPriceContentObserver);
        }

        if (mInventoryContentObserver == null) {

            mInventoryContentObserver = new AnimalAdoptionsContentObserver(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI);
            mStoreRepository.registerContentObserver(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI,
                    true, mInventoryContentObserver);
        }

        resetObservers();
    }

    private void unregisterContentObservers() {
        if (mAnimalContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mAnimalContentObserver);
            mAnimalContentObserver = null;
        }

        if (mRescuerContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mRescuerContentObserver);
            mRescuerContentObserver = null;
        }

        if (mPriceContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mPriceContentObserver);
            mPriceContentObserver = null;
        }

        if (mInventoryContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mInventoryContentObserver);
            mInventoryContentObserver = null;
        }
    }

    private void resetObservers() {
        mDeliveredNotification.set(false);
    }

    @Override
    public void onFabAddClicked() {
    }

    @Override
    public void triggerAnimalAdoptionsLoad(boolean forceLoad) {
        mAdoptionsListView.showProgressIndicator();
        if (forceLoad) {
            mLoaderManager.restartLoader(AppConstants.ADOPTIONS_LOADER, null, this);
        } else {
            mLoaderManager.initLoader(AppConstants.ADOPTIONS_LOADER, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return mLoaderProvider.createCursorLoader(LoaderProvider.ADOPTIONS_LIST_TYPE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.getCount() > 0) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        onDataReset();
    }

    @Override
    public void onDataLoaded(Cursor data) {
        mAdoptionsListView.hideEmptyView();
        ArrayList<AdoptionsLite> adoptionsList = new ArrayList<>();
        if (data.isAfterLast()) {
            data.moveToPosition(-1);
        }
        while (data.moveToNext()) {
            adoptionsList.add(AdoptionsLite.from(data));
        }
        mAdoptionsListView.loadAdoptionsList(adoptionsList);
        mAdoptionsListView.hideProgressIndicator();
    }

    @Override
    public void onDataEmpty() {
        mAdoptionsListView.hideProgressIndicator();
        mAdoptionsListView.showEmptyView();
    }

    @Override
    public void onDataNotAvailable() {
        mAdoptionsListView.hideProgressIndicator();
        mAdoptionsListView.showError(R.string.adoptions_list_load_error);
    }

    @Override
    public void onDataReset() {
        mAdoptionsListView.loadAdoptionsList(new ArrayList<>());
        mAdoptionsListView.showEmptyView();
    }

    @Override
    public void onContentChange() {
        Loader<Cursor> adoptionsLoader = mLoaderManager.getLoader(AppConstants.ADOPTIONS_LOADER);
        if (adoptionsLoader != null) {
            adoptionsLoader.onContentChanged();
        } else {
            triggerAnimalAdoptionsLoad(true);
        }
    }

    @Override
    public void onAnimalContentChange() {
        Loader<Cursor> animalsLoader = mLoaderManager.getLoader(AppConstants.ANIMALSS_LOADER);
        if (animalsLoader != null) {
            animalsLoader.onContentChanged();
        }
    }

    @Override
    public void releaseResources() {
        unregisterContentObservers();
        mContentObserverHandlerThread.quit();
    }

    @Override
    public void deleteAnimal(int animalId, String animalSku) {
        mAdoptionsListView.showProgressIndicator();

        resetObservers();

        mStoreRepository.deleteAnimalById(animalId, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mAdoptionsListView.hideProgressIndicator();

                mAdoptionsListView.showDeleteSuccess(animalSku);
            }

            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mAdoptionsListView.hideProgressIndicator();

                mAdoptionsListView.showError(messageId, args);
            }
        });
    }

    @Override
    public void sellOneQuantity(AdoptionsLite adoptionsLite) {
        mAdoptionsListView.showProgressIndicator();

        resetObservers();

        mStoreRepository.decreaseAnimalRescuerInventory(adoptionsLite.getAnimalId(),
                adoptionsLite.getAnimalSku(), adoptionsLite.getRescuerId(), adoptionsLite.getTopRescuerCode(),
                adoptionsLite.getRescuerAvailableQuantity(), 1, new DataRepository.DataOperationsCallback() {

                    @Override
                    public void onSuccess() {
                        mAdoptionsListView.hideProgressIndicator();

                        mAdoptionsListView.showSellQuantitySuccess(adoptionsLite.getAnimalSku(), adoptionsLite.getTopRescuerCode());
                    }

                    @Override
                    public void onFailure(int messageId, @Nullable Object... args) {
                        mAdoptionsListView.hideProgressIndicator();

                        mAdoptionsListView.showError(messageId, args);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode >= FragmentActivity.RESULT_FIRST_USER) {

            if (requestCode == AdoptionsConfigActivity.REQUEST_EDIT_ADOPTIONS) {

                if (resultCode == AdoptionsConfigActivity.RESULT_EDIT_ADOPTIONS) {
                    mAdoptionsListView.showUpdateInventorySuccess(data.getStringExtra(AdoptionsConfigActivity.EXTRA_RESULT_ANIMALS_SKU));

                } else if (resultCode == AnimalConfigActivity.RESULT_DELETE_ANIMALS) {
                    mAdoptionsListView.showDeleteSuccess(data.getStringExtra(AdoptionsConfigActivity.EXTRA_RESULT_ANIMALS_SKU));
                }

            }
        }
    }

    @Override
    public void editAnimalAdoptions(int animalId, ActivityOptionsCompat activityOptionsCompat) {
        resetObservers();
        mAdoptionsListView.launchEditAnimalAdoptions(animalId, activityOptionsCompat);
    }

    private class AnimalAdoptionsContentObserver extends ContentObserver {
        private static final int ITEM_ID = 10;
        private static final int ITEM_IMAGES_ID = 11;
        private static final int RESCUER_ID = 20;
        private static final int RESCUER_ITEMS_ID = 30;
        private static final int ADOPTIONS_INVENTORY_ITEM_ID = 40;
        private final UriMatcher mUriMatcher = buildUriMatcher();
        private final Uri mObserverUri;
        private final Handler mMainThreadHandler;

        AnimalAdoptionsContentObserver(Uri observerUri) {
            super(new Handler(mContentObserverHandlerThread.getLooper()));
            mMainThreadHandler = new Handler(Looper.getMainLooper());
            mObserverUri = observerUri;
        }

        private UriMatcher buildUriMatcher() {
            UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AnimalContract.PATH_ITEM + "/#", ITEM_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AnimalContract.PATH_ITEM + "/" + AnimalContract.PATH_ITEM_IMAGE + "/#",
                    ITEM_IMAGES_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    RescuerContract.PATH_RESCUER + "/#", RESCUER_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AdoptionsContract.PATH_ITEM_RESCUER_INFO + "/" + RescuerContract.PATH_RESCUER + "/#",
                    RESCUER_ITEMS_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AdoptionsContract.PATH_ITEM_RESCUER_INVENTORY + "/" + AnimalContract.PATH_ITEM + "/#",
                    ADOPTIONS_INVENTORY_ITEM_ID);
            return matcher;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri != null) {

                int uriMatch = mUriMatcher.match(uri);

                if (mObserverUri.equals(AnimalContract.Animal.CONTENT_URI)) {

                    switch (uriMatch) {
                        case ITEM_ID:
                            triggerNotification(uri);
                            break;
                        case ITEM_IMAGES_ID:
                            triggerNotification(uri);
                            break;
                    }
                } else if (mObserverUri.equals(RescuerContract.Rescuer.CONTENT_URI)) {

                    switch (uriMatch) {
                        case RESCUER_ID:
                            triggerNotification(uri);
                            break;
                    }
                } else if (mObserverUri.equals(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI)) {

                    switch (uriMatch) {
                        case RESCUER_ITEMS_ID:
                            triggerNotification(uri);
                            break;
                    }
                } else if (mObserverUri.equals(AdoptionsContract.AnimalRescuerInventory.CONTENT_URI)) {

                    switch (uriMatch) {
                        case ADOPTIONS_INVENTORY_ITEM_ID:
                            triggerNotification(uri);
                            break;
                    }
                }

            } else if (selfChange) {

                mMainThreadHandler.post(AdoptionsListPresenter.this::onContentChange);
            }
        }

        private void triggerNotification(Uri uri) {
            if (mDeliveredNotification.compareAndSet(false, true)) {

                Log.i(LOG_TAG, "triggerNotification: Called for " + uri);

                mMainThreadHandler.post(AdoptionsListPresenter.this::onContentChange);

                if (mObserverUri.equals(AnimalContract.Animal.CONTENT_URI)) {
                    mMainThreadHandler.post(AdoptionsListPresenter.this::onAnimalContentChange);
                }
            }
        }

    }

}
