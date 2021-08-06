package com.example.pethome.storeapp.ui.rescuers;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.example.pethome.storeapp.data.local.models.RescuerLite;
import com.example.pethome.storeapp.ui.PagerPresenter;
import com.example.pethome.storeapp.ui.PagerView;

import java.util.ArrayList;
public interface RescuerListContract {
    interface View extends PagerView<Presenter> {
        void showProgressIndicator();
        void hideProgressIndicator();
        void showError(@StringRes int messageId, @Nullable Object... args);
        void showEmptyView();
        void hideEmptyView();
        void loadRescuers(ArrayList<RescuerLite> rescuerList);
        void launchAddNewRescuer();
        void launchEditRescuer(int rescuerId);
        void showAddSuccess(String rescuerCode);
        void showUpdateSuccess(String rescuerCode);
        void showDeleteSuccess(String rescuerCode);
        void dialPhoneNumber(String phoneNumber);
        void composeEmail(String toEmailAddress);
    }
    interface Presenter extends PagerPresenter {
        void triggerRescuersLoad(boolean forceLoad);
        void editRescuer(int rescuerId);
        void deleteRescuer(RescuerLite rescuer);
        void addNewRescuer();
        void onActivityResult(int requestCode, int resultCode, Intent data);
        void releaseResources();
        void defaultPhoneClicked(String phoneNumber);
        void defaultEmailClicked(String toEmailAddress);
    }
}
