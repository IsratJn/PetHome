package com.example.pethome.storeapp.ui.animals;
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
import com.example.pethome.storeapp.data.local.contracts.StoreContract;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.utils.AppConstants;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
public class AnimalListPresenter
        implements AnimalListContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, DataRepository.CursorDataLoaderCallback {
    private static final String LOG_TAG = AnimalListPresenter.class.getSimpleName();
    private static final String CONTENT_OBSERVER_THREAD_NAME = "AnimalListContentObserverThread";
    @NonNull
    private final AnimalListContract.View mAnimalListView;
    @NonNull
    private final LoaderProvider mLoaderProvider;
    @NonNull
    private final LoaderManager mLoaderManager;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final HandlerThread mContentObserverHandlerThread;
    private AnimalContentObserver mAnimalContentObserver;
    public AnimalListPresenter(@NonNull LoaderProvider loaderProvider,
                               @NonNull LoaderManager loaderManager,
                               @NonNull StoreRepository storeRepository,
                               @NonNull AnimalListContract.View animalListView) {
        mLoaderProvider = loaderProvider;
        mLoaderManager = loaderManager;
        mStoreRepository = storeRepository;
        mAnimalListView = animalListView;
        mContentObserverHandlerThread = new HandlerThread(CONTENT_OBSERVER_THREAD_NAME);
        mContentObserverHandlerThread.start();
        mAnimalListView.setPresenter(this);
    }
    @Override
    public void start() {
        registerContentObserver();
        triggerAnimalsLoad(false);
    }
    @Override
    public void onFabAddClicked() {
        addNewAnimal();
    }
    private void registerContentObserver() {
        if (mAnimalContentObserver == null) {
            mAnimalContentObserver = new AnimalContentObserver();
            mStoreRepository.registerContentObserver(mAnimalContentObserver.OBSERVER_URI,
                    true,
                    mAnimalContentObserver
            );
        } else {
            mAnimalContentObserver.resetObserver();
        }
    }
    private void unregisterContentObserver() {
        if (mAnimalContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mAnimalContentObserver);
            mAnimalContentObserver = null;
        }
    }
    private void resetObservers() {
        if (mAnimalContentObserver != null) {
            mAnimalContentObserver.resetObserver();
        }
    }
    @Override
    public void triggerAnimalsLoad(boolean forceLoad) {
        mAnimalListView.showProgressIndicator();
        if (forceLoad) {
            mLoaderManager.restartLoader(AppConstants.ANIMALSS_LOADER, null, this);
        } else {
            mLoaderManager.initLoader(AppConstants.ANIMALSS_LOADER, null, this);
        }
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return mLoaderProvider.createCursorLoader(LoaderProvider.ANIMALS_LIST_TYPE);
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
        mAnimalListView.hideEmptyView();
        ArrayList<AnimalLite> animalList = new ArrayList<>();
        if (data.isAfterLast()) {
            data.moveToPosition(-1);
        }
        while (data.moveToNext()) {
            animalList.add(AnimalLite.from(data));
        }
        mAnimalListView.loadAnimals(animalList);
        mAnimalListView.hideProgressIndicator();
    }
    @Override
    public void onDataEmpty() {
        mAnimalListView.hideProgressIndicator();
        mAnimalListView.showEmptyView();
    }
    @Override
    public void onDataNotAvailable() {
        mAnimalListView.hideProgressIndicator();
        mAnimalListView.showError(R.string.animal_list_load_error);
    }
    @Override
    public void onDataReset() {
        mAnimalListView.loadAnimals(new ArrayList<>());
        mAnimalListView.showEmptyView();
    }
    @Override
    public void onContentChange() {
        Loader<Cursor> animalsLoader = mLoaderManager.getLoader(AppConstants.ANIMALSS_LOADER);
        if (animalsLoader != null) {
            animalsLoader.onContentChanged();
        } else {
            triggerAnimalsLoad(true);
        }
        Loader<Cursor> adoptionsLoader = mLoaderManager.getLoader(AppConstants.ADOPTIONS_LOADER);
        if (adoptionsLoader != null) {
            adoptionsLoader.onContentChanged();
        }
    }
    @Override
    public void editAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat) {
        resetObservers();
        mAnimalListView.launchEditAnimal(animalId, activityOptionsCompat);
    }
    @Override
    public void deleteAnimal(AnimalLite animal) {
        mAnimalListView.showProgressIndicator();
        resetObservers();
        mStoreRepository.deleteAnimalById(animal.getId(), new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mAnimalListView.hideProgressIndicator();
                mAnimalListView.showDeleteSuccess(animal.getSku());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mAnimalListView.hideProgressIndicator();
                mAnimalListView.showError(messageId, args);
            }
        });
    }
    @Override
    public void addNewAnimal() {
        resetObservers();
        mAnimalListView.launchAddNewAnimal();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode >= FragmentActivity.RESULT_FIRST_USER) {
            if (requestCode == AnimalConfigActivity.REQUEST_EDIT_ANIMALS) {
                if (resultCode == AnimalConfigActivity.RESULT_EDIT_ANIMALS) {
                    mAnimalListView.showUpdateSuccess(data.getStringExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_SKU));
                } else if (resultCode == AnimalConfigActivity.RESULT_DELETE_ANIMALS) {
                    mAnimalListView.showDeleteSuccess(data.getStringExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_SKU));
                }
            } else if (requestCode == AnimalConfigActivity.REQUEST_ADD_ANIMALS &&
                    resultCode == AnimalConfigActivity.RESULT_ADD_ANIMALS) {
                mAnimalListView.showAddSuccess(data.getStringExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_SKU));
            }
        }
    }
    @Override
    public void releaseResources() {
        unregisterContentObserver();
        mContentObserverHandlerThread.quit();
    }
    private class AnimalContentObserver extends ContentObserver {
        private static final int ITEM_ID = 10;
        private static final int ITEM_IMAGES_ID = 11;
        private final UriMatcher mUriMatcher = buildUriMatcher();
        private final Uri OBSERVER_URI = AnimalContract.Animal.CONTENT_URI;
        private final Handler mMainThreadHandler;
        private final AtomicBoolean mDeliveredNotification = new AtomicBoolean(false);
        AnimalContentObserver() {
            super(new Handler(mContentObserverHandlerThread.getLooper()));
            mMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        private UriMatcher buildUriMatcher() {
            UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AnimalContract.PATH_ITEM + "/#", ITEM_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AnimalContract.PATH_ITEM + "/" + AnimalContract.PATH_ITEM_IMAGE + "/#",
                    ITEM_IMAGES_ID);
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
                switch (mUriMatcher.match(uri)) {
                    case ITEM_ID:
                        triggerNotification(uri);
                        break;
                    case ITEM_IMAGES_ID:
                        triggerNotification(uri);
                        break;
                }
            } else if (selfChange) {
                mMainThreadHandler.post(AnimalListPresenter.this::onContentChange);
            }
        }
        private void triggerNotification(Uri uri) {
            if (mDeliveredNotification.compareAndSet(false, true)) {
                Log.i(LOG_TAG, "triggerNotification: Called for " + uri);
                mMainThreadHandler.post(AnimalListPresenter.this::onContentChange);
            }
        }
        private void resetObserver() {
            mDeliveredNotification.set(false);
        }
    }
}