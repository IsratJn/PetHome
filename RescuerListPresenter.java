package com.example.pethome.storeapp.ui.rescuers;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.LoaderProvider;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;
import com.example.pethome.storeapp.data.local.contracts.StoreContract;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.models.RescuerLite;
import com.example.pethome.storeapp.ui.rescuers.config.RescuerConfigActivity;
import com.example.pethome.storeapp.utils.AppConstants;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
public class RescuerListPresenter implements RescuerListContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, DataRepository.CursorDataLoaderCallback {
    private static final String LOG_TAG = RescuerListPresenter.class.getSimpleName();
    private static final String CONTENT_OBSERVER_THREAD_NAME = "RescuerListContentObserverThread";
    @NonNull
    private final RescuerListContract.View mRescuerListView;
    @NonNull
    private final LoaderProvider mLoaderProvider;
    @NonNull
    private final LoaderManager mLoaderManager;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final HandlerThread mContentObserverHandlerThread;
    private final AtomicBoolean mDeliveredNotification = new AtomicBoolean(false);
    private RescuerContentObserver mRescuerContentObserver;
    private RescuerContentObserver mPriceContentObserver;
    public RescuerListPresenter(@NonNull LoaderProvider loaderProvider,
                                @NonNull LoaderManager loaderManager,
                                @NonNull StoreRepository storeRepository,
                                @NonNull RescuerListContract.View rescuerListView) {
        mLoaderProvider = loaderProvider;
        mLoaderManager = loaderManager;
        mStoreRepository = storeRepository;
        mRescuerListView = rescuerListView;
        mContentObserverHandlerThread = new HandlerThread(CONTENT_OBSERVER_THREAD_NAME);
        mContentObserverHandlerThread.start();
        mRescuerListView.setPresenter(this);
    }
    @Override
    public void start() {
        registerContentObservers();
        triggerRescuersLoad(false);
    }
    private void registerContentObservers() {
        if (mRescuerContentObserver == null) {
            mRescuerContentObserver = new RescuerContentObserver(RescuerContract.Rescuer.CONTENT_URI);
            mStoreRepository.registerContentObserver(RescuerContract.Rescuer.CONTENT_URI,
                    true,
                    mRescuerContentObserver
            );
        }
        if (mPriceContentObserver == null) {
            mPriceContentObserver = new RescuerContentObserver(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI);
            mStoreRepository.registerContentObserver(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI,
                    true,
                    mPriceContentObserver
            );
        }
        resetObservers();
    }
    private void unregisterContentObservers() {
        if (mRescuerContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mRescuerContentObserver);
            mRescuerContentObserver = null;
        }
        if (mPriceContentObserver != null) {
            mStoreRepository.unregisterContentObserver(mPriceContentObserver);
            mPriceContentObserver = null;
        }
    }
    private void resetObservers() {
        mDeliveredNotification.set(false);
    }
    @Override
    public void triggerRescuersLoad(boolean forceLoad) {
        mRescuerListView.showProgressIndicator();
        if (forceLoad) {
            mLoaderManager.restartLoader(AppConstants.RESCUERS_LOADER, null, this);
        } else {
            mLoaderManager.initLoader(AppConstants.RESCUERS_LOADER, null, this);
        }
    }
    @Override
    public void editRescuer(int rescuerId) {
        resetObservers();
        mRescuerListView.launchEditRescuer(rescuerId);
    }
    @Override
    public void deleteRescuer(RescuerLite rescuer) {
        mRescuerListView.showProgressIndicator();
        resetObservers();
        mStoreRepository.deleteRescuerById(rescuer.getId(), new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mRescuerListView.hideProgressIndicator();
                mRescuerListView.showDeleteSuccess(rescuer.getCode());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mRescuerListView.hideProgressIndicator();
                mRescuerListView.showError(messageId, args);
            }
        });
    }
    @Override
    public void addNewRescuer() {
        resetObservers();
        mRescuerListView.launchAddNewRescuer();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode >= FragmentActivity.RESULT_FIRST_USER) {
            if (requestCode == RescuerConfigActivity.REQUEST_EDIT_RESCUER) {
                if (resultCode == RescuerConfigActivity.RESULT_EDIT_RESCUER) {
                    mRescuerListView.showUpdateSuccess(data.getStringExtra(RescuerConfigActivity.EXTRA_RESULT_RESCUER_CODE));
                } else if (resultCode == RescuerConfigActivity.RESULT_DELETE_RESCUER) {
                    mRescuerListView.showDeleteSuccess(data.getStringExtra(RescuerConfigActivity.EXTRA_RESULT_RESCUER_CODE));
                }
            } else if (requestCode == RescuerConfigActivity.REQUEST_ADD_RESCUER &&
                    resultCode == RescuerConfigActivity.RESULT_ADD_RESCUER) {
                mRescuerListView.showAddSuccess(data.getStringExtra(RescuerConfigActivity.EXTRA_RESULT_RESCUER_CODE));
            }
        }
    }
    @Override
    public void releaseResources() {
        unregisterContentObservers();
        mContentObserverHandlerThread.quit();
    }
    @Override
    public void defaultPhoneClicked(String phoneNumber) {
        mRescuerListView.dialPhoneNumber(phoneNumber);
    }
    @Override
    public void defaultEmailClicked(String toEmailAddress) {
        mRescuerListView.composeEmail(toEmailAddress);
    }
    @Override
    public void onFabAddClicked() {
        addNewRescuer();
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return mLoaderProvider.createCursorLoader(LoaderProvider.RESCUER_LIST_TYPE);
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
        mRescuerListView.hideEmptyView();
        ArrayList<RescuerLite> rescuerList = new ArrayList<>();
        if (data.isAfterLast()) {
            data.moveToPosition(-1);
        }
        while (data.moveToNext()) {
            rescuerList.add(RescuerLite.from(data));
        }
        mRescuerListView.loadRescuers(rescuerList);
        mRescuerListView.hideProgressIndicator();
    }
    @Override
    public void onDataEmpty() {
        mRescuerListView.hideProgressIndicator();
        mRescuerListView.showEmptyView();
    }
    @Override
    public void onDataNotAvailable() {
        mRescuerListView.hideProgressIndicator();
        mRescuerListView.showError(R.string.rescuer_list_load_error);
    }
    @Override
    public void onDataReset() {
        mRescuerListView.loadRescuers(new ArrayList<>());
        mRescuerListView.showEmptyView();
    }
    @Override
    public void onContentChange() {
        Loader<Cursor> rescuersLoader = mLoaderManager.getLoader(AppConstants.RESCUERS_LOADER);
        if (rescuersLoader != null) {
            rescuersLoader.onContentChanged();
        } else {
            triggerRescuersLoad(true);
        }
    }
    private class RescuerContentObserver extends ContentObserver {
        private static final int RESCUER_ID = 10;
        private static final int RESCUER_CONTACTS_ID = 11;
        private static final int RESCUER_ITEMS_ID = 20;
        private final UriMatcher mUriMatcher = buildUriMatcher();
        private final Uri mObserverUri;
        private final Handler mMainThreadHandler;
        RescuerContentObserver(Uri observerUri) {
            super(new Handler(mContentObserverHandlerThread.getLooper()));
            mMainThreadHandler = new Handler(Looper.getMainLooper());
            mObserverUri = observerUri;
        }
        private UriMatcher buildUriMatcher() {
            UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    RescuerContract.PATH_RESCUER + "/#", RESCUER_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    RescuerContract.PATH_RESCUER + "/" + RescuerContract.PATH_RESCUER_CONTACT + "/#",
                    RESCUER_CONTACTS_ID);
            matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                    AdoptionsContract.PATH_ITEM_RESCUER_INFO + "/" + RescuerContract.PATH_RESCUER + "/#",
                    RESCUER_ITEMS_ID);
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
                if (mObserverUri.equals(RescuerContract.Rescuer.CONTENT_URI)) {
                    switch (uriMatch) {
                        case RESCUER_ID:
                            triggerNotification(uri);
                            break;
                        case RESCUER_CONTACTS_ID:
                            triggerNotification(uri);
                            break;
                    }
                } else if (mObserverUri.equals(AdoptionsContract.AnimalRescuerInfo.CONTENT_URI)) {
                    switch (uriMatch) {
                        case RESCUER_ITEMS_ID:
                            triggerNotification(uri);
                            break;
                    }
                }
            } else if (selfChange) {
                mMainThreadHandler.post(RescuerListPresenter.this::onContentChange);
            }
        }
        private void triggerNotification(Uri uri) {
            if (mDeliveredNotification.compareAndSet(false, true)) {
                Log.i(LOG_TAG, "triggerNotification: Called for " + uri);
                mMainThreadHandler.post(RescuerListPresenter.this::onContentChange);
            }
        }
    }
}
