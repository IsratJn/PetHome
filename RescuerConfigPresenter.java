package com.example.pethome.storeapp.ui.rescuers.config;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.DataRepository;
import com.example.pethome.storeapp.data.StoreRepository;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerInfo;
import com.example.pethome.storeapp.data.local.models.Rescuer;
import com.example.pethome.storeapp.data.local.models.RescuerContact;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigContract;
import com.example.pethome.storeapp.ui.rescuers.animal.RescuerAnimalPickerActivity;
import com.example.pethome.storeapp.utils.ContactUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class RescuerConfigPresenter implements RescuerConfigContract.Presenter {
    private static final String LOG_TAG = RescuerConfigPresenter.class.getSimpleName();
    private final int mRescuerId;
    @NonNull
    private final RescuerConfigContract.View mRescuerConfigView;
    @NonNull
    private final StoreRepository mStoreRepository;
    private final RescuerConfigNavigator mRescuerConfigNavigator;
    private Rescuer mExistingRescuer;
    private boolean mIsExistingRescuerRestored;
    private boolean mIsRescuerCodeValid;
    private boolean mIsRescuerNameEntered;
    private SparseArray<AnimalLite> mAnimalLiteSparseArray;
    private ArrayList<AnimalRescuerInfo> mAnimalRescuerInfoList;
    RescuerConfigPresenter(int rescuerId,
                            @NonNull StoreRepository storeRepository,
                            @NonNull RescuerConfigContract.View rescuerConfigView,
                            @NonNull RescuerConfigNavigator rescuerConfigNavigator) {
        mRescuerId = rescuerId;
        mStoreRepository = storeRepository;
        mRescuerConfigView = rescuerConfigView;
        mRescuerConfigNavigator = rescuerConfigNavigator;
        mRescuerConfigView.setPresenter(this);
    }
    @Override
    public void start() {
        loadExistingRescuer();
    }
    private void loadExistingRescuer() {
        if (mRescuerId != RescuerConfigContract.NEW_RESCUER_INT) {
            mRescuerConfigView.showProgressIndicator(R.string.rescuer_config_status_loading_existing_rescuer);
            mStoreRepository.getRescuerDetailsById(mRescuerId, new DataRepository.GetQueryCallback<Rescuer>() {
                @Override
                public void onResults(Rescuer rescuer) {
                    if (!mIsExistingRescuerRestored) {
                        updateRescuerNameField(rescuer.getName());
                        updateRescuerCodeField(rescuer.getCode());
                        updateRescuerContacts(rescuer.getContacts());
                        updateRescuerAnimals(rescuer.getAnimalRescuerInfoList(), null);
                        updateAndSyncExistingRescuerState(true);
                        updateAndSyncRescuerCodeValidity(true);
                        updateAndSyncRescuerNameEnteredState(true);
                    }
                    mRescuerConfigView.lockRescuerCodeField();
                    mExistingRescuer = rescuer;
                    mRescuerConfigView.hideProgressIndicator();
                }
                @Override
                public void onFailure(int messageId, @Nullable Object... args) {
                    mRescuerConfigView.hideProgressIndicator();
                    mRescuerConfigView.showError(messageId, args);
                }
                @Override
                public void onEmpty() {
                }
            });
        }
    }
    @Override
    public void updateRescuerNameField(String rescuerName) {
        mRescuerConfigView.updateRescuerNameField(rescuerName);
    }
    @Override
    public void updateRescuerCodeField(String rescuerCode) {
        mRescuerConfigView.updateRescuerCodeField(rescuerCode);
    }
    @Override
    public void updateRescuerContacts(ArrayList<RescuerContact> rescuerContacts) {
        ArrayList<RescuerContact> phoneContacts = new ArrayList<>();
        ArrayList<RescuerContact> emailContacts = new ArrayList<>();
        if (rescuerContacts != null && rescuerContacts.size() > 0) {
            for (RescuerContact rescuerContact : rescuerContacts) {
                switch (rescuerContact.getType()) {
                    case RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE:
                        phoneContacts.add((RescuerContact) rescuerContact.clone());
                        break;
                    case RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL:
                        emailContacts.add((RescuerContact) rescuerContact.clone());
                        break;
                }
            }
        }
        mRescuerConfigView.updatePhoneContacts(phoneContacts);
        mRescuerConfigView.updateEmailContacts(emailContacts);
    }
    @Override
    public void updateRescuerAnimals(ArrayList<AnimalRescuerInfo> animalRescuerInfoList,
                                       @Nullable SparseArray<AnimalLite> animalLiteSparseArray) {
        if (animalRescuerInfoList != null && animalRescuerInfoList.size() > 0) {
            mAnimalRescuerInfoList = new ArrayList<>();
            for (AnimalRescuerInfo animalRescuerInfo : animalRescuerInfoList) {
                mAnimalRescuerInfoList.add((AnimalRescuerInfo) animalRescuerInfo.clone());
            }
            if (animalLiteSparseArray == null) {
                if (mAnimalLiteSparseArray == null) {
                    mAnimalLiteSparseArray = new SparseArray<>();
                } else {
                    mAnimalLiteSparseArray.clear();
                }
                ArrayList<String> animalIds = new ArrayList<>();
                for (AnimalRescuerInfo animalRescuerInfo : mAnimalRescuerInfoList) {
                    animalIds.add(String.valueOf(animalRescuerInfo.getItemId()));
                }
                mStoreRepository.getShortAnimalInfoForAnimals(animalIds, new DataRepository.GetQueryCallback<List<AnimalLite>>() {
                    @Override
                    public void onResults(List<AnimalLite> animals) {
                        for (AnimalLite animal : animals) {
                            mAnimalLiteSparseArray.put(animal.getId(), animal);
                        }
                        mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, mAnimalLiteSparseArray);
                    }
                    @Override
                    public void onEmpty() {
                        mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, mAnimalLiteSparseArray);
                    }
                });
            } else {
                mAnimalLiteSparseArray = animalLiteSparseArray;
                mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, mAnimalLiteSparseArray);
            }
        } else {
            if (mAnimalRescuerInfoList == null) {
                mAnimalRescuerInfoList = new ArrayList<>();
            } else {
                mAnimalRescuerInfoList.clear();
            }
            mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, null);
        }
    }
    private void updateRescuerAnimals(ArrayList<AnimalLite> animalList) {
        if (animalList != null && animalList.size() > 0) {
            if (mAnimalRescuerInfoList == null) {
                mAnimalRescuerInfoList = new ArrayList<>();
            }
            SparseArray<AnimalRescuerInfo> animalRescuerInfoSparseArray = new SparseArray<>();
            for (AnimalRescuerInfo animalRescuerInfo : mAnimalRescuerInfoList) {
                animalRescuerInfoSparseArray.put(animalRescuerInfo.getItemId(), animalRescuerInfo);
            }
            if (mAnimalLiteSparseArray == null) {
                mAnimalLiteSparseArray = new SparseArray<>();
            }
            for (AnimalLite animal : animalList) {
                if (animalRescuerInfoSparseArray.get(animal.getId()) == null) {
                    AnimalRescuerInfo newAnimalRescuerInfo = new AnimalRescuerInfo.Builder()
                            .setItemId(animal.getId())
                            .setRescuerId(mRescuerId)
                            .createAnimalRescuerInfo();
                    mAnimalRescuerInfoList.add(newAnimalRescuerInfo);
                    mAnimalLiteSparseArray.put(animal.getId(), animal);
                }
            }
            mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, mAnimalLiteSparseArray);
        }
    }
    @Override
    public void validateRescuerCode(String rescuerCode) {
        if (TextUtils.isEmpty(rescuerCode)) {
            mRescuerConfigView.showRescuerCodeEmptyError();
            return;
        }
        mStoreRepository.getRescuerCodeUniqueness(rescuerCode, new DataRepository.GetQueryCallback<Boolean>() {
            @Override
            public void onResults(Boolean results) {
                updateAndSyncRescuerCodeValidity(results);
            }
            @Override
            public void onEmpty() {
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode > FragmentActivity.RESULT_FIRST_USER) {
            if (requestCode == RescuerAnimalPickerActivity.REQUEST_RESCUER_ANIMALSS
                    && resultCode == RescuerAnimalPickerActivity.RESULT_RESCUER_ANIMALSS) {
                if (data != null && data.hasExtra(RescuerAnimalPickerActivity.EXTRA_RESCUER_ANIMALSS)) {
                    ArrayList<AnimalLite> animalList = data.getParcelableArrayListExtra(RescuerAnimalPickerActivity.EXTRA_RESCUER_ANIMALSS);
                    updateRescuerAnimals(animalList);
                }
            } else if (requestCode == AnimalConfigActivity.REQUEST_EDIT_ANIMALS) {
                if (resultCode == AnimalConfigActivity.RESULT_EDIT_ANIMALS) {
                    String animalSku = data.getStringExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_SKU);
                    int animalId = data.getIntExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_ID, AnimalConfigContract.NEW_ANIMALS_INT);
                    if (animalId != AnimalConfigContract.NEW_ANIMALS_INT) {
                        ArrayList<String> animalIds = new ArrayList<>();
                        animalIds.add(String.valueOf(animalId));
                        mStoreRepository.getShortAnimalInfoForAnimals(animalIds, new DataRepository.GetQueryCallback<List<AnimalLite>>() {
                            @Override
                            public void onResults(List<AnimalLite> animals) {
                                for (AnimalLite animal : animals) {
                                    mAnimalLiteSparseArray.put(animal.getId(), animal);
                                }
                                mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, mAnimalLiteSparseArray);
                                for (AnimalLite animal : animals) {
                                    mRescuerConfigView.notifyAnimalChanged(animal.getId());
                                }
                                mRescuerConfigView.showUpdateSuccess(animalSku);
                            }
                            @Override
                            public void onEmpty() {
                            }
                        });
                    }
                } else if (resultCode == AnimalConfigActivity.RESULT_DELETE_ANIMALS) {
                    String animalSku = data.getStringExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_SKU);
                    int animalId = data.getIntExtra(AnimalConfigActivity.EXTRA_RESULT_ANIMALS_ID, AnimalConfigContract.NEW_ANIMALS_INT);
                    if (animalId != AnimalConfigContract.NEW_ANIMALS_INT) {
                        mAnimalLiteSparseArray.remove(animalId);
                        Iterator<AnimalRescuerInfo> animalRescuerInfoIterator = mAnimalRescuerInfoList.iterator();
                        while (animalRescuerInfoIterator.hasNext()) {
                            AnimalRescuerInfo animalRescuerInfo = animalRescuerInfoIterator.next();
                            if (animalRescuerInfo.getItemId() == animalId) {
                                animalRescuerInfoIterator.remove();
                                break;
                            }
                        }
                        mRescuerConfigView.updateRescuerAnimals(mAnimalRescuerInfoList, mAnimalLiteSparseArray);
                        mRescuerConfigView.showDeleteSuccess(animalSku);
                    }
                }
            }
        }
    }
    @Override
    public void triggerFocusLost() {
        mRescuerConfigView.triggerFocusLost();
    }
    @Override
    public void onSave(String rescuerName,
                       String rescuerCode,
                       ArrayList<RescuerContact> phoneContacts,
                       ArrayList<RescuerContact> emailContacts,
                       ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
        mRescuerConfigView.showProgressIndicator(R.string.rescuer_config_status_saving);
        if (!mIsExistingRescuerRestored && !mIsRescuerCodeValid) {
            mRescuerConfigView.hideProgressIndicator();
            if (TextUtils.isEmpty(rescuerCode)) {
                mRescuerConfigView.showRescuerCodeEmptyError();
            } else {
                mRescuerConfigView.showRescuerCodeConflictError();
            }
            return;
        }
        if (TextUtils.isEmpty(rescuerName) || TextUtils.isEmpty(rescuerCode)) {
            mRescuerConfigView.hideProgressIndicator();
            mRescuerConfigView.showEmptyFieldsValidationError();
            return;
        }
        if (!validateRescuerContactList(phoneContacts, R.string.rescuer_config_phone_contact_conflict_error)) {
            return;
        }
        if (!validateRescuerContactList(emailContacts, R.string.rescuer_config_email_contact_conflict_error)) {
            return;
        }
        if (!validateRescuerContactValues(RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE,
                phoneContacts, R.string.rescuer_config_phone_contacts_invalid_error)) {
            return;
        }
        if (!validateRescuerContactValues(RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL,
                emailContacts, R.string.rescuer_config_email_contacts_invalid_error)) {
            return;
        }
        if (phoneContacts.size() + emailContacts.size() == 0) {
            mRescuerConfigView.hideProgressIndicator();
            mRescuerConfigView.showEmptyContactsError();
            return;
        }
        Rescuer newRescuer = createRescuerForUpdate(rescuerName, rescuerCode,
                phoneContacts, emailContacts, animalRescuerInfoList);
        if (mRescuerId > RescuerConfigContract.NEW_RESCUER_INT) {
            saveUpdatedRescuer(mExistingRescuer, newRescuer);
        } else {
            saveNewRescuer(newRescuer);
        }
    }
    @Override
    public void showDeleteRescuerDialog() {
        mRescuerConfigView.showDeleteRescuerDialog();
    }
    private boolean validateRescuerContactValues(String contactType,
                                                  ArrayList<RescuerContact> rescuerContacts,
                                                  @StringRes int invalidMessageResId) {
        for (RescuerContact rescuerContact : rescuerContacts) {
            if (contactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE)) {
                if (!ContactUtility.isValidPhoneNumber(rescuerContact.getValue())) {
                    mRescuerConfigView.hideProgressIndicator();
                    mRescuerConfigView.showRescuerContactsInvalidError(invalidMessageResId);
                    return false;
                }
            } else if (contactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL)) {
                if (!ContactUtility.isValidEmail(rescuerContact.getValue())) {
                    mRescuerConfigView.hideProgressIndicator();
                    mRescuerConfigView.showRescuerContactsInvalidError(invalidMessageResId);
                    return false;
                }
            }
        }
        return true;
    }
    private boolean validateRescuerContactList(ArrayList<RescuerContact> rescuerContacts,
                                                @StringRes int conflictMessageResId) {
        ArrayList<String> rescuerContactValues = new ArrayList<>();
        Iterator<RescuerContact> rescuerContactsIterator = rescuerContacts.iterator();
        while (rescuerContactsIterator.hasNext()) {
            RescuerContact contact = rescuerContactsIterator.next();
            String contactValue = contact.getValue();
            if (TextUtils.isEmpty(contactValue)) {
                rescuerContactsIterator.remove();
            } else {
                if (rescuerContactValues.contains(contactValue)) {
                    mRescuerConfigView.hideProgressIndicator();
                    mRescuerConfigView.showRescuerContactConflictError(conflictMessageResId, contactValue);
                    return false;
                } else {
                    rescuerContactValues.add(contactValue);
                }
            }
        }
        return true;
    }
    private Rescuer createRescuerForUpdate(String rescuerName,
                                             String rescuerCode,
                                             ArrayList<RescuerContact> phoneContacts,
                                             ArrayList<RescuerContact> emailContacts,
                                             ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
        phoneContacts.addAll(emailContacts);
        return new Rescuer.Builder()
                .setId(mRescuerId)
                .setName(rescuerName)
                .setCode(rescuerCode)
                .setContacts(phoneContacts)
                .setAnimalRescuerInfoList(animalRescuerInfoList)
                .createRescuer();
    }
    private void saveNewRescuer(Rescuer newRescuer) {
        mStoreRepository.saveNewRescuer(newRescuer, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mRescuerConfigView.hideProgressIndicator();
                doSetResult(RescuerConfigActivity.RESULT_ADD_RESCUER, newRescuer.getId(), newRescuer.getCode());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mRescuerConfigView.hideProgressIndicator();
                mRescuerConfigView.showError(messageId, args);
            }
        });
    }
    private void saveUpdatedRescuer(Rescuer existingRescuer, Rescuer newRescuer) {
        mStoreRepository.saveUpdatedRescuer(existingRescuer, newRescuer, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mRescuerConfigView.hideProgressIndicator();
                doSetResult(RescuerConfigActivity.RESULT_EDIT_RESCUER, newRescuer.getId(), newRescuer.getCode());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mRescuerConfigView.hideProgressIndicator();
                mRescuerConfigView.showError(messageId, args);
            }
        });
    }
    @Override
    public void deleteRescuer() {
        mRescuerConfigView.showProgressIndicator(R.string.rescuer_config_status_deleting);
        mStoreRepository.deleteRescuerById(mRescuerId, new DataRepository.DataOperationsCallback() {
            @Override
            public void onSuccess() {
                mRescuerConfigView.hideProgressIndicator();
                doSetResult(RescuerConfigActivity.RESULT_DELETE_RESCUER, mExistingRescuer.getId(), mExistingRescuer.getCode());
            }
            @Override
            public void onFailure(int messageId, @Nullable Object... args) {
                mRescuerConfigView.hideProgressIndicator();
                mRescuerConfigView.showError(messageId, args);
            }
        });
    }
    @Override
    public void onUpOrBackAction() {
        if (mIsRescuerNameEntered) {
            mRescuerConfigView.showDiscardDialog();
        } else {
            finishActivity();
        }
    }
    @Override
    public void finishActivity() {
        doCancel();
    }
    @Override
    public void doSetResult(int resultCode, int rescuerId, @NonNull String rescuerCode) {
        mRescuerConfigNavigator.doSetResult(resultCode, rescuerId, rescuerCode);
    }
    @Override
    public void doCancel() {
        mRescuerConfigNavigator.doCancel();
    }
    @Override
    public void editAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat) {
        mRescuerConfigNavigator.launchEditAnimal(animalId, activityOptionsCompat);
    }
    @Override
    public void onRescuerAnimalSwiped(String animalSku) {
        mRescuerConfigView.showRescuerAnimalSwiped(animalSku);
    }
    @Override
    public void pickAnimals(ArrayList<AnimalLite> animalLiteList) {
        mRescuerConfigNavigator.launchPickAnimals(animalLiteList);
    }
    @Override
    public void updateAndSyncExistingRescuerState(boolean isExistingRescuerRestored) {
        mIsExistingRescuerRestored = isExistingRescuerRestored;
        mRescuerConfigView.syncExistingRescuerState(mIsExistingRescuerRestored);
    }
    @Override
    public void updateAndSyncRescuerCodeValidity(boolean isRescuerCodeValid) {
        mIsRescuerCodeValid = isRescuerCodeValid;
        mRescuerConfigView.syncRescuerCodeValidity(mIsRescuerCodeValid);
        if (!mIsRescuerCodeValid) {
            mRescuerConfigView.showRescuerCodeConflictError();
        }
    }
    @Override
    public void updateAndSyncRescuerNameEnteredState(boolean isRescuerNameEntered) {
        mIsRescuerNameEntered = isRescuerNameEntered;
        mRescuerConfigView.syncRescuerNameEnteredState(mIsRescuerNameEntered);
    }
}
