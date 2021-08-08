package com.example.pethome.storeapp.ui.rescuers.animal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.models.AnimalLite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
public class RescuerAnimalPickerPresenter implements RescuerAnimalPickerContract.Presenter {
    private static final String LOG_TAG = RescuerAnimalPickerPresenter.class.getSimpleName();
    @NonNull
    private final StoreRepository mStoreRepository;
    @NonNull
    private final RescuerAnimalPickerContract.View mRescuerAnimalPickerView;
    private final RescuerAnimalPickerNavigator mRescuerAnimalPickerNavigator;
    private final RescuerAnimalPickerMultiSelectListener mRescuerAnimalPickerMultiSelectListener;
    private final RescuerAnimalPickerSearchActionsListener mRescuerAnimalPickerSearchActionsListener;
    private AtomicBoolean mIsLoadAnimalsCalled = new AtomicBoolean(false);
    private int mCountOfAnimalsSelected;
    RescuerAnimalPickerPresenter(@NonNull StoreRepository storeRepository,
                                 @NonNull RescuerAnimalPickerContract.View rescuerAnimalPickerView,
                                 @NonNull RescuerAnimalPickerNavigator rescuerAnimalPickerNavigator,
                                 @NonNull RescuerAnimalPickerMultiSelectListener multiSelectListener,
                                 @NonNull RescuerAnimalPickerSearchActionsListener searchActionsListener) {
        mStoreRepository = storeRepository;
        mRescuerAnimalPickerView = rescuerAnimalPickerView;
        mRescuerAnimalPickerNavigator = rescuerAnimalPickerNavigator;
        mRescuerAnimalPickerMultiSelectListener = multiSelectListener;
        mRescuerAnimalPickerSearchActionsListener = searchActionsListener;
        mRescuerAnimalPickerView.setPresenter(this);
    }
    @Override
    public void start() {
    }
    @Override
    public void filterResults(String searchQueryStr) {
        mRescuerAnimalPickerView.filterAdapterData(searchQueryStr, (count) -> {
            if (count > 0) {
                mRescuerAnimalPickerView.hideEmptyView();
            } else {
                mRescuerAnimalPickerView.showEmptyView(R.string.rescuer_animal_picker_empty_search_result, searchQueryStr);
            }
        });
    }
    @Override
    public void clearFilter() {
        mRescuerAnimalPickerView.clearAdapterFilter((count) -> {
            if (count > 0) {
                mRescuerAnimalPickerView.hideEmptyView();
            }
        });
    }
    @Override
    public void loadAnimalsToPick(ArrayList<AnimalLite> registeredAnimals,
                                   @Nullable ArrayList<AnimalLite> remainingAnimals,
                                   @Nullable ArrayList<AnimalLite> selectedAnimals) {
        if (mIsLoadAnimalsCalled.compareAndSet(false, true)) {
            if (remainingAnimals != null) {
                if (remainingAnimals.size() > 0) {
                    mRescuerAnimalPickerView.hideEmptyView();
                    mRescuerAnimalPickerView.submitDataToAdapter(remainingAnimals, selectedAnimals);
                    if (selectedAnimals != null) {
                        updateSelectedAnimalCount(selectedAnimals.size());
                    }
                } else {
                    mRescuerAnimalPickerView.showEmptyView(R.string.rescuer_animal_picker_list_empty_all_picked);
                    mRescuerAnimalPickerSearchActionsListener.disableSearch();
                }
            } else {
                mRescuerAnimalPickerView.showProgressIndicator(R.string.rescuer_animal_picker_status_loading_animals);
                mStoreRepository.getShortAnimalInfoForAnimals(null, new DataRepository.GetQueryCallback<List<AnimalLite>>() {
                    @Override
                    public void onResults(List<AnimalLite> animals) {
                        ArrayList<AnimalLite> allAnimals = new ArrayList<>(animals);
                        allAnimals.removeAll(registeredAnimals);
                        if (allAnimals.size() > 0) {
                            mRescuerAnimalPickerView.hideEmptyView();
                            mRescuerAnimalPickerView.submitDataToAdapter(allAnimals, null);
                        } else {
                            mRescuerAnimalPickerView.showEmptyView(R.string.rescuer_animal_picker_list_empty_all_picked);
                            mRescuerAnimalPickerSearchActionsListener.disableSearch();
                        }
                        mRescuerAnimalPickerView.hideProgressIndicator();
                    }
                    @Override
                    public void onEmpty() {
                        mRescuerAnimalPickerView.hideProgressIndicator();
                        mRescuerAnimalPickerView.showEmptyView(R.string.rescuer_animal_picker_list_empty_no_animal);
                    }
                });
            }
        }
    }
    @Override
    public void updateSelectedAnimalCount(int countOfAnimalsSelected) {
        mCountOfAnimalsSelected = countOfAnimalsSelected;
        mRescuerAnimalPickerMultiSelectListener.showSelectedCount(countOfAnimalsSelected);
    }
    @Override
    public void onSave(@NonNull ArrayList<AnimalLite> registeredAnimals,
                       @NonNull ArrayList<AnimalLite> selectedAnimals) {
        ArrayList<AnimalLite> animalsToSell = new ArrayList<>();
        animalsToSell.addAll(registeredAnimals);
        animalsToSell.addAll(selectedAnimals);
        doSetResult(animalsToSell);
    }
    @Override
    public void onUpOrBackAction() {
        if (mCountOfAnimalsSelected > 0) {
            mRescuerAnimalPickerView.showDiscardDialog();
        } else {
            finishActivity();
        }
    }
    @Override
    public void finishActivity() {
        doCancel();
    }
    @Override
    public void doSetResult(ArrayList<AnimalLite> animalsToSell) {
        mRescuerAnimalPickerNavigator.doSetResult(animalsToSell);
    }
    @Override
    public void doCancel() {
        mRescuerAnimalPickerNavigator.doCancel();
    }
}
