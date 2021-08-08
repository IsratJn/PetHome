

package com.example.pethome.storeapp.ui.rescuers.animal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.Filter;

import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.ui.BasePresenter;
import com.example.pethome.storeapp.ui.BaseView;

import java.util.ArrayList;

public interface RescuerAnimalPickerContract {
    interface View extends BaseView<Presenter> {
        void showProgressIndicator(@StringRes int statusTextId);
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void hideEmptyView();
        void showEmptyView(@StringRes int emptyTextResId, @Nullable Object... args);
        void submitDataToAdapter(ArrayList<AnimalLite> remainingAnimals,
                                 @Nullable ArrayList<AnimalLite> selectedAnimals);
        void filterAdapterData(String searchQueryStr, Filter.FilterListener filterListener);
        void clearAdapterFilter(Filter.FilterListener filterListener);
        void showDiscardDialog();
    }
    interface Presenter extends BasePresenter {
        void filterResults(String searchQueryStr);
        void clearFilter();
        void loadAnimalsToPick(ArrayList<AnimalLite> registeredAnimals,
                                @Nullable ArrayList<AnimalLite> remainingAnimals,
                                @Nullable ArrayList<AnimalLite> selectedAnimals);
        void updateSelectedAnimalCount(int countOfAnimalsSelected);
        void onSave(@NonNull ArrayList<AnimalLite> registeredAnimals, @NonNull ArrayList<AnimalLite> selectedAnimals);
        void onUpOrBackAction();
        void finishActivity();
        void doSetResult(ArrayList<AnimalLite> animalsToSell);
        void doCancel();
    }
}
