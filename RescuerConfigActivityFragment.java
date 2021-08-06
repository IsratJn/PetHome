package com.example.pethome.storeapp.ui.rescuers.config;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerInfo;
import com.example.pethome.storeapp.data.local.models.RescuerContact;
import com.example.pethome.storeapp.ui.common.ProgressDialogFragment;
import com.example.pethome.storeapp.utils.ContactUtility;
import com.example.pethome.storeapp.utils.OrientationUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
import java.util.ArrayList;
import java.util.List;

public class RescuerConfigActivityFragment extends Fragment
        implements RescuerConfigContract.View, View.OnFocusChangeListener, View.OnClickListener {
    private static final String LOG_TAG = RescuerConfigActivityFragment.class.getSimpleName();
    private static final String ARGUMENT_INT_RESCUER_ID = "argument.RESCUER_ID";
    private static final String BUNDLE_RESCUER_CONTACTS_PHONE_KEY = "RescuerConfig.Contacts.Phone";
    private static final String BUNDLE_RESCUER_CONTACTS_EMAIL_KEY = "RescuerConfig.Contacts.Email";
    private static final String BUNDLE_RESCUER_ANIMALSS_DATA_SPARSE_ARRAY_KEY = "RescuerConfig.AnimalLites";
    private static final String BUNDLE_RESCUER_ANIMALSS_PRICE_KEY = "RescuerConfig.AnimalRescuerInfos";
    private static final String BUNDLE_EXISTING_RESCUER_RESTORED_BOOL_KEY = "RescuerConfig.IsExistingRescuerRestored";
    private static final String BUNDLE_RESCUER_CODE_VALID_BOOL_KEY = "RescuerConfig.IsRescuerCodeValid";
    private static final String BUNDLE_RESCUER_NAME_ENTERED_BOOL_KEY = "RescuerConfig.IsRescuerNameEntered";
    private RescuerConfigContract.Presenter mPresenter;
    private EditText mEditTextRescuerName;
    private TextInputLayout mTextInputRescuerCode;
    private EditText mEditTextRescuerCode;
    private RecyclerView mRecyclerViewContactPhone;
    private RecyclerView mRecyclerViewContactEmail;
    private RecyclerView mRecyclerViewRescuerAnimal;
    private RescuerContactAdapter mPhoneContactsAdapter;
    private RescuerContactAdapter mEmailContactsAdapter;
    private RescuerAnimalsAdapter mRescuerAnimalsAdapter;
    private View mLastRegisteredFocusChangeView;
    private int mRescuerId;
    private boolean mIsExistingRescuerRestored;
    private boolean mIsRescuerCodeValid;
    private boolean mIsRescuerNameEntered;
    private DialogInterface.OnClickListener mRescuerDeleteDialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    mPresenter.deleteRescuer();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    break;
            }
        }
    };
    private DialogInterface.OnClickListener mUnsavedDialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    saveRescuer();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    mPresenter.finishActivity();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    break;
            }
        }
    };
    public RescuerConfigActivityFragment() {
    }
    public static RescuerConfigActivityFragment newInstance(int rescuerId) {
        Bundle args = new Bundle(1);
        args.putInt(ARGUMENT_INT_RESCUER_ID, rescuerId);
        RescuerConfigActivityFragment fragment = new RescuerConfigActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rescuer_config, container, false);
        mEditTextRescuerName = rootView.findViewById(R.id.edittext_rescuer_config_name);
        mTextInputRescuerCode = rootView.findViewById(R.id.textinput_rescuer_config_code);
        mEditTextRescuerCode = rootView.findViewById(R.id.edittext_rescuer_config_code);
        mRecyclerViewContactPhone = rootView.findViewById(R.id.recyclerview_rescuer_config_phone);
        mRecyclerViewContactEmail = rootView.findViewById(R.id.recyclerview_rescuer_config_email);
        mRecyclerViewRescuerAnimal = rootView.findViewById(R.id.recyclerview_rescuer_config_items);
        mEditTextRescuerName.setOnFocusChangeListener(this);
        mEditTextRescuerCode.setOnFocusChangeListener(this);
        mEditTextRescuerCode.addTextChangedListener(new RescuerCodeTextWatcher());
        rootView.findViewById(R.id.btn_rescuer_config_add_phone).setOnClickListener(this);
        rootView.findViewById(R.id.btn_rescuer_config_add_email).setOnClickListener(this);
        rootView.findViewById(R.id.btn_rescuer_config_add_item).setOnClickListener(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mRescuerId = arguments.getInt(ARGUMENT_INT_RESCUER_ID, RescuerConfigContract.NEW_RESCUER_INT);
        }
        setupContactPhonesRecyclerView();
        setupContactEmailsRecyclerView();
        setupRescuerItemsRecyclerView();
        return rootView;
    }
    @Override
    public void setPresenter(RescuerConfigContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<RescuerContact> phoneContactList = savedInstanceState.getParcelableArrayList(BUNDLE_RESCUER_CONTACTS_PHONE_KEY);
            ArrayList<RescuerContact> emailContactList = savedInstanceState.getParcelableArrayList(BUNDLE_RESCUER_CONTACTS_EMAIL_KEY);
            ArrayList<RescuerContact> rescuerContacts = new ArrayList<>();
            rescuerContacts.addAll(phoneContactList);
            rescuerContacts.addAll(emailContactList);
            mPresenter.updateRescuerContacts(rescuerContacts);
            SparseArray<AnimalLite> animalLiteSparseArray = savedInstanceState.getSparseParcelableArray(BUNDLE_RESCUER_ANIMALSS_DATA_SPARSE_ARRAY_KEY);
            ArrayList<AnimalRescuerInfo> animalRescuerInfoList = savedInstanceState.getParcelableArrayList(BUNDLE_RESCUER_ANIMALSS_PRICE_KEY);
            mPresenter.updateRescuerAnimals(animalRescuerInfoList, animalLiteSparseArray);
            mPresenter.updateAndSyncRescuerNameEnteredState(savedInstanceState.getBoolean(BUNDLE_RESCUER_NAME_ENTERED_BOOL_KEY, false));
            mPresenter.updateAndSyncExistingRescuerState(savedInstanceState.getBoolean(BUNDLE_EXISTING_RESCUER_RESTORED_BOOL_KEY, false));
            mPresenter.updateAndSyncRescuerCodeValidity(savedInstanceState.getBoolean(BUNDLE_RESCUER_CODE_VALID_BOOL_KEY, false));
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.triggerFocusLost();
        outState.putParcelableArrayList(BUNDLE_RESCUER_CONTACTS_PHONE_KEY, mPhoneContactsAdapter.getRescuerContacts());
        outState.putParcelableArrayList(BUNDLE_RESCUER_CONTACTS_EMAIL_KEY, mEmailContactsAdapter.getRescuerContacts());
        outState.putSparseParcelableArray(BUNDLE_RESCUER_ANIMALSS_DATA_SPARSE_ARRAY_KEY, mRescuerAnimalsAdapter.getAnimalLiteSparseArray());
        outState.putParcelableArrayList(BUNDLE_RESCUER_ANIMALSS_PRICE_KEY, mRescuerAnimalsAdapter.getAnimalRescuerInfoList());
        outState.putBoolean(BUNDLE_EXISTING_RESCUER_RESTORED_BOOL_KEY, mIsExistingRescuerRestored);
        outState.putBoolean(BUNDLE_RESCUER_CODE_VALID_BOOL_KEY, mIsRescuerCodeValid);
        outState.putBoolean(BUNDLE_RESCUER_NAME_ENTERED_BOOL_KEY, mIsRescuerNameEntered);
    }
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_rescuer_config, menu);
        if (mRescuerId == RescuerConfigContract.NEW_RESCUER_INT) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
            deleteMenuItem.setEnabled(false);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mPresenter.showDeleteRescuerDialog();
                return true;
            case R.id.action_save:
                saveRescuer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void saveRescuer() {
        mPresenter.triggerFocusLost();
        String rescuerName = mEditTextRescuerName.getText().toString().trim();
        String rescuerCode = mEditTextRescuerCode.getText().toString().trim();
        ArrayList<RescuerContact> phoneContacts = mPhoneContactsAdapter.getRescuerContacts();
        ArrayList<RescuerContact> emailContacts = mEmailContactsAdapter.getRescuerContacts();
        ArrayList<AnimalRescuerInfo> animalRescuerInfoList = mRescuerAnimalsAdapter.getAnimalRescuerInfoList();
        mPresenter.onSave(rescuerName,
                rescuerCode,
                phoneContacts,
                emailContacts,
                animalRescuerInfoList);
    }
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            mLastRegisteredFocusChangeView = null;
            switch (view.getId()) {
                case R.id.edittext_rescuer_config_name:
                    mPresenter.updateAndSyncRescuerNameEnteredState(!TextUtils.isEmpty(mEditTextRescuerName.getText().toString().trim()));
                    break;
                case R.id.edittext_rescuer_config_code:
                    if (mRescuerId == RescuerConfigContract.NEW_RESCUER_INT) {
                        mPresenter.validateRescuerCode(mEditTextRescuerCode.getText().toString().trim());
                    }
                    break;
            }
        } else {
            mLastRegisteredFocusChangeView = view;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rescuer_config_add_phone:
                mPhoneContactsAdapter.addEmptyRecord();
                break;
            case R.id.btn_rescuer_config_add_email:
                mEmailContactsAdapter.addEmptyRecord();
                break;
            case R.id.btn_rescuer_config_add_item:
                mPresenter.pickAnimals(mRescuerAnimalsAdapter.getAnimalLiteList());
                break;
        }
    }
    @Override
    public void showProgressIndicator(@StringRes int statusTextId) {
        ProgressDialogFragment.showDialog(getChildFragmentManager(), getString(statusTextId));
    }
    @Override
    public void hideProgressIndicator() {
        ProgressDialogFragment.dismissDialog(getChildFragmentManager());
    }
    @Override
    public void showError(int messageId, @Nullable Object... args) {
        if (getView() != null) {
            String messageToBeShown;
            if (args != null && args.length > 0) {
                messageToBeShown = getString(messageId, args);
            } else {
                messageToBeShown = getString(messageId);
            }
            if (!TextUtils.isEmpty(messageToBeShown)) {
                new SnackbarUtility(Snackbar.make(getView(), messageToBeShown, Snackbar.LENGTH_INDEFINITE))
                        .revealCompleteMessage()
                        .setDismissAction(R.string.snackbar_action_ok)
                        .showSnack();
            }
        }
    }
    @Override
    public void lockRescuerCodeField() {
        mTextInputRescuerCode.setEnabled(false);
    }
    @Override
    public void syncExistingRescuerState(boolean isExistingRescuerRestored) {
        mIsExistingRescuerRestored = isExistingRescuerRestored;
    }
    @Override
    public void syncRescuerCodeValidity(boolean isRescuerCodeValid) {
        mIsRescuerCodeValid = isRescuerCodeValid;
    }
    @Override
    public void syncRescuerNameEnteredState(boolean isRescuerNameEntered) {
        mIsRescuerNameEntered = isRescuerNameEntered;
    }
    @Override
    public void showRescuerCodeConflictError() {
        mTextInputRescuerCode.setError(getString(R.string.rescuer_config_code_invalid_error));
    }
    @Override
    public void updateRescuerNameField(String rescuerName) {
        mEditTextRescuerName.setText(rescuerName);
    }
    @Override
    public void updateRescuerCodeField(String rescuerCode) {
        mEditTextRescuerCode.setText(rescuerCode);
    }
    @Override
    public void updatePhoneContacts(ArrayList<RescuerContact> phoneContacts) {
        mPhoneContactsAdapter.replaceRescuerContactList(phoneContacts);
    }
    @Override
    public void updateEmailContacts(ArrayList<RescuerContact> emailContacts) {
        mEmailContactsAdapter.replaceRescuerContactList(emailContacts);
    }
    @Override
    public void updateRescuerAnimals(ArrayList<AnimalRescuerInfo> animalRescuerInfoList,
                                       @Nullable SparseArray<AnimalLite> animalLiteSparseArray) {
        mRescuerAnimalsAdapter.submitData(animalRescuerInfoList, animalLiteSparseArray);
    }
    @Override
    public void showRescuerCodeEmptyError() {
        mTextInputRescuerCode.setError(getString(R.string.rescuer_config_code_empty_error));
    }
    @Override
    public void triggerFocusLost() {
        if (mLastRegisteredFocusChangeView != null) {
            mLastRegisteredFocusChangeView.clearFocus();
            mLastRegisteredFocusChangeView = null;
        }
        if (mPhoneContactsAdapter != null) {
            mPhoneContactsAdapter.triggerFocusLost();
        }
        if (mEmailContactsAdapter != null) {
            mEmailContactsAdapter.triggerFocusLost();
        }
        if (mRescuerAnimalsAdapter != null) {
            mRescuerAnimalsAdapter.triggerFocusLost();
        }
    }
    @Override
    public void showEmptyFieldsValidationError() {
        showError(R.string.rescuer_config_empty_fields_validation_error);
    }
    @Override
    public void showRescuerContactConflictError(@StringRes int conflictMessageResId, String contactValue) {
        showError(conflictMessageResId, contactValue);
    }
    @Override
    public void showEmptyContactsError() {
        showError(R.string.rescuer_config_empty_contacts_error);
    }
    @Override
    public void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.rescuer_config_unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.rescuer_config_unsaved_changes_dialog_positive_text, mUnsavedDialogOnClickListener);
        builder.setNegativeButton(R.string.rescuer_config_unsaved_changes_dialog_negative_text, mUnsavedDialogOnClickListener);
        builder.setNeutralButton(R.string.rescuer_config_unsaved_changes_dialog_neutral_text, mUnsavedDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void showDeleteRescuerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.rescuer_config_delete_rescuer_confirm_dialog_message);
        builder.setPositiveButton(android.R.string.yes, mRescuerDeleteDialogOnClickListener);
        builder.setNegativeButton(android.R.string.no, mRescuerDeleteDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void showRescuerAnimalSwiped(String animalSku) {
        if (getView() != null) {
            new SnackbarUtility(Snackbar.make(getView(),
                    getString(R.string.rescuer_config_animal_swipe_action_success,
                            animalSku), Snackbar.LENGTH_LONG))
                    .revealCompleteMessage()
                    .setAction(R.string.snackbar_action_undo, (view) -> {
                        if (mRescuerAnimalsAdapter.restoreLastRemovedAnimal()) {
                            Snackbar.make(getView(),
                                    getString(R.string.rescuer_config_animal_swipe_action_undo_success, animalSku),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .showSnack();
        }
    }
    @Override
    public void showUpdateSuccess(String animalSku) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.animal_list_item_update_success, animalSku), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showDeleteSuccess(String animalSku) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.animal_list_item_delete_success, animalSku), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void notifyAnimalChanged(int animalId) {
        mRescuerAnimalsAdapter.notifyAnimalChanged(animalId);
    }
    @Override
    public void showRescuerContactsInvalidError(@StringRes int invalidMessageResId) {
        showError(invalidMessageResId);
    }
    private void setupContactPhonesRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewContactPhone.setLayoutManager(linearLayoutManager);
        mPhoneContactsAdapter = new RescuerContactAdapter(RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE);
        mRecyclerViewContactPhone.setAdapter(mPhoneContactsAdapter);
    }
    private void setupContactEmailsRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewContactEmail.setLayoutManager(linearLayoutManager);
        mEmailContactsAdapter = new RescuerContactAdapter(RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL);
        mRecyclerViewContactEmail.setAdapter(mEmailContactsAdapter);
    }
    private void setupRescuerItemsRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewRescuerAnimal.setLayoutManager(linearLayoutManager);
        mRescuerAnimalsAdapter = new RescuerAnimalsAdapter(requireContext(), new RescuerAnimalItemUserActionsListener());
        mRecyclerViewRescuerAnimal.setAdapter(mRescuerAnimalsAdapter);
        mRescuerAnimalsAdapter.getItemTouchHelper().attachToRecyclerView(mRecyclerViewRescuerAnimal);
    }
    private static class RescuerContactAdapter extends RecyclerView.Adapter<RescuerContactAdapter.ViewHolder> {
        private static final String PAYLOAD_NEW_DEFAULT_CONTACT = "Payload.NewDefaultContactPosition";
        private static final String PAYLOAD_OLD_DEFAULT_CONTACT = "Payload.OldDefaultContactPosition";
        @RescuerContact.RescuerContactTypeDef
        private final String mContactType;
        private ArrayList<RescuerContact> mRescuerContacts;
        private SparseArray<String> mTrackerContactValuesSparseArray;
        private RescuerContact mDefaultRescuerContact;
        private View mLastFocusedView;
        RescuerContactAdapter(@RescuerContact.RescuerContactTypeDef String contactType) {
            mContactType = contactType;
            mRescuerContacts = new ArrayList<>();
            addEmptyRecord();
            mTrackerContactValuesSparseArray = new SparseArray<>();
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_config_contact, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RescuerContact rescuerContact = mRescuerContacts.get(position);
            holder.bind(rescuerContact);
            if (rescuerContact.isDefault()) {
                mDefaultRescuerContact = rescuerContact;
                holder.showContactAsDefault(true, false);
            } else {
                holder.showContactAsDefault(false, false);
            }
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                Bundle bundle = (Bundle) payloads.get(0);
                for (String keyStr : bundle.keySet()) {
                    switch (keyStr) {
                        case PAYLOAD_NEW_DEFAULT_CONTACT:
                            int newDefaultContactItemPosition = bundle.getInt(keyStr, RecyclerView.NO_POSITION);
                            if (newDefaultContactItemPosition > RecyclerView.NO_POSITION
                                    && newDefaultContactItemPosition == position) {
                                holder.showContactAsDefault(true, true);
                            }
                            break;
                        case PAYLOAD_OLD_DEFAULT_CONTACT:
                            int oldDefaultContactItemPosition = bundle.getInt(keyStr, RecyclerView.NO_POSITION);
                            if (oldDefaultContactItemPosition > RecyclerView.NO_POSITION
                                    && oldDefaultContactItemPosition == position) {
                                holder.showContactAsDefault(false, false);
                            }
                            break;
                    }
                }
            }
        }
        @Override
        public int getItemCount() {
            return mRescuerContacts.size();
        }
        int getItemPosition(RescuerContact rescuerContactToFind) {
            int position = RecyclerView.NO_POSITION;
            if (TextUtils.isEmpty(rescuerContactToFind.getValue())) {
                return position;
            }
            if (mRescuerContacts != null && mRescuerContacts.size() > 0) {
                int itemCount = getItemCount();
                for (int index = 0; index < itemCount; index++) {
                    RescuerContact rescuerContact = mRescuerContacts.get(index);
                    if (rescuerContactToFind.getValue().equals(rescuerContact.getValue())) {
                        position = index;
                        break;
                    }
                }
            }
            return position;
        }
        void replaceRescuerContactList(ArrayList<RescuerContact> rescuerContacts) {
            if (rescuerContacts == null || rescuerContacts.size() == 0) {
                mRescuerContacts.clear();
            } else {
                mRescuerContacts.clear();
                mRescuerContacts.addAll(rescuerContacts);
            }
            rebuildContactTrackers();
            mDefaultRescuerContact = null;
            notifyDataSetChanged();
        }
        private void rebuildContactTrackers() {
            mTrackerContactValuesSparseArray.clear();
            int noOfContacts = mRescuerContacts.size();
            for (int index = 0; index < noOfContacts; index++) {
                mTrackerContactValuesSparseArray.put(index, mRescuerContacts.get(index).getValue());
            }
        }
        void addEmptyRecord() {
            int currentItemCount = getItemCount();
            RescuerContact rescuerContact = new RescuerContact.Builder()
                    .setType(mContactType)
                    .createRescuerContact();
            if (currentItemCount == 0) {
                rescuerContact.setDefault(true);
                mDefaultRescuerContact = rescuerContact;
            }
            mRescuerContacts.add(rescuerContact);
            notifyItemInserted(currentItemCount);
        }
        void deleteRecord(int position) {
            if (position > RecyclerView.NO_POSITION) {
                RescuerContact removedRescuerContact = mRescuerContacts.remove(position);
                String removedContactValue = removedRescuerContact.getValue();
                notifyItemRemoved(position);
                if (removedRescuerContact.isDefault() && mDefaultRescuerContact != null &&
                        removedContactValue.equals(mDefaultRescuerContact.getValue())) {
                    if (getItemCount() > 0) {
                        changeDefaultContact(0);
                    }
                }
                String trackerContactValue = mTrackerContactValuesSparseArray.get(position);
                if (!TextUtils.isEmpty(removedContactValue) && !TextUtils.isEmpty(trackerContactValue) && trackerContactValue.equals(removedContactValue)) {
                    rebuildContactTrackers();
                }
            }
        }
        void changeDefaultContact(int newDefaultContactItemPosition) {
            if (newDefaultContactItemPosition > RecyclerView.NO_POSITION) {
                RescuerContact newDefaultRescuerContact = mRescuerContacts.get(newDefaultContactItemPosition);
                if (!TextUtils.isEmpty(newDefaultRescuerContact.getValue())) {
                    boolean previouslyDefaulted = newDefaultRescuerContact.isDefault();
                    newDefaultRescuerContact.setDefault(true);
                    if (mDefaultRescuerContact != null) {
                        int oldDefaultContactItemPosition = getItemPosition(mDefaultRescuerContact);
                        if (oldDefaultContactItemPosition > RecyclerView.NO_POSITION) {
                            RescuerContact oldDefaultRescuerContact = mRescuerContacts.get(oldDefaultContactItemPosition);
                            oldDefaultRescuerContact.setDefault(false);
                            Bundle payloadBundle = new Bundle(1);
                            payloadBundle.putInt(PAYLOAD_OLD_DEFAULT_CONTACT, oldDefaultContactItemPosition);
                            notifyItemChanged(oldDefaultContactItemPosition, payloadBundle);
                        }
                    }
                    if (!previouslyDefaulted) {
                        mDefaultRescuerContact = newDefaultRescuerContact;
                        Bundle payloadBundle = new Bundle(1);
                        payloadBundle.putInt(PAYLOAD_NEW_DEFAULT_CONTACT, newDefaultContactItemPosition);
                        notifyItemChanged(newDefaultContactItemPosition, payloadBundle);
                    } else {
                        mDefaultRescuerContact = null;
                    }
                }
            }
        }
        void triggerFocusLost() {
            if (mLastFocusedView != null) {
                mLastFocusedView.clearFocus();
            }
        }
        ArrayList<RescuerContact> getRescuerContacts() {
            return mRescuerContacts;
        }
        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnFocusChangeListener {
            private ImageButton mImageButtonRemoveAction;
            private TextInputLayout mTextInputContact;
            private TextInputEditText mEditTextContact;
            private ImageView mImageViewContactDefault;
            private TextView mTextViewContactDefaultLabel;
            ViewHolder(View itemView) {
                super(itemView);
                mImageButtonRemoveAction = itemView.findViewById(R.id.imgbtn_rescuer_config_item_contact_remove);
                mTextInputContact = itemView.findViewById(R.id.textinput_rescuer_config_item_contact);
                mEditTextContact = itemView.findViewById(R.id.edittext_rescuer_config_item_contact);
                mImageViewContactDefault = itemView.findViewById(R.id.image_rescuer_config_item_contact_default);
                mTextViewContactDefaultLabel = itemView.findViewById(R.id.text_rescuer_config_item_contact_default_label);
                if (mContactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE)) {
                    mEditTextContact.setInputType(InputType.TYPE_CLASS_PHONE);
                    mTextInputContact.setHint(itemView.getContext().getString(R.string.rescuer_config_item_contact_hint_phone));
                    mEditTextContact.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
                } else if (mContactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL)) {
                    mEditTextContact.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    mTextInputContact.setHint(mContactType);
                    int emailMaxLength = ContactUtility.getEmailMaxLength();
                    mEditTextContact.setFilters(new InputFilter[]{new InputFilter.LengthFilter(emailMaxLength)});
                }
                mImageButtonRemoveAction.setOnClickListener(this);
                mImageViewContactDefault.setOnClickListener(this);
                mEditTextContact.setOnFocusChangeListener(this);
            }
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    switch (view.getId()) {
                        case R.id.imgbtn_rescuer_config_item_contact_remove:
                            deleteRecord(adapterPosition);
                            break;
                        case R.id.image_rescuer_config_item_contact_default:
                            changeDefaultContact(adapterPosition);
                            break;
                    }
                }
            }
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    RescuerContact rescuerContact = mRescuerContacts.get(adapterPosition);
                    if (!hasFocus) {
                        mLastFocusedView = null;
                        switch (view.getId()) {
                            case R.id.edittext_rescuer_config_item_contact:
                                String contactValue = mEditTextContact.getText().toString().trim();
                                if (!TextUtils.isEmpty(contactValue)) {
                                    if (mContactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE)) {
                                        contactValue = ContactUtility.convertAndStripPhoneNumber(contactValue);
                                    }
                                    rescuerContact.setValue(contactValue);
                                    if (isContactDuplicate(adapterPosition, contactValue)) {
                                        mTextInputContact.setErrorEnabled(true);
                                        mTextInputContact.setError(view.getContext().getString(R.string.rescuer_config_item_contact_duplicate_error));
                                        mTextInputContact.invalidate();
                                    } else {
                                        if (isContactValid(contactValue)) {
                                            mTrackerContactValuesSparseArray.put(adapterPosition, contactValue);
                                        }
                                    }
                                }
                                break;
                        }
                    } else {
                        mLastFocusedView = view;
                        if (mLastFocusedView.getId() == R.id.edittext_rescuer_config_item_contact) {
                            if (mTextInputContact.isErrorEnabled()) {
                                mTextInputContact.setError(null);
                                mTextInputContact.setErrorEnabled(false);
                            }
                        }
                    }
                }
            }
            private boolean isContactValid(String contactValue) {
                if (TextUtils.isEmpty(contactValue)) {
                    return false;
                }
                if (mContactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE)) {
                    if (!ContactUtility.isValidPhoneNumber(contactValue)) {
                        mTextInputContact.setErrorEnabled(true);
                        mTextInputContact.setError(itemView.getContext().getString(R.string.rescuer_config_phone_number_invalid_error, ContactUtility.getPhoneNumberMinLength(), ContactUtility.getPhoneNumberMaxLength()));
                        mTextInputContact.invalidate();
                        return false;
                    }
                } else if (mContactType.equals(RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL)) {
                    if (!ContactUtility.isValidEmail(contactValue)) {
                        mTextInputContact.setErrorEnabled(true);
                        mTextInputContact.setError(itemView.getContext().getString(R.string.rescuer_config_email_invalid_error));
                        mTextInputContact.invalidate();
                        return false;
                    }
                }
                return true;
            }
            private boolean isContactDuplicate(int position, String contactValue) {
                int noOfTrackedContacts = mTrackerContactValuesSparseArray.size();
                for (int index = 0; index < noOfTrackedContacts; index++) {
                    if (mTrackerContactValuesSparseArray.valueAt(index).equals(contactValue)
                            && mTrackerContactValuesSparseArray.keyAt(index) != position) {
                        return true;
                    }
                }
                return false;
            }
            void bind(RescuerContact rescuerContact) {
                mEditTextContact.setText(rescuerContact.getValue());
                mTextInputContact.setError(null);
                mTextInputContact.setErrorEnabled(false);
                isContactValid(rescuerContact.getValue());
            }
            void showContactAsDefault(boolean isDefault, boolean takeFocus) {
                if (isDefault) {
                    mImageViewContactDefault.setSelected(true);
                    mTextViewContactDefaultLabel.setVisibility(View.VISIBLE);
                    if (takeFocus) {
                        mEditTextContact.requestFocus();
                    }
                } else {
                    mImageViewContactDefault.setSelected(false);
                    mTextViewContactDefaultLabel.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
    private static class RescuerAnimalsAdapter extends ListAdapter<AnimalRescuerInfo, RescuerAnimalsAdapter.ViewHolder> {
        private static DiffUtil.ItemCallback<AnimalRescuerInfo> DIFF_ANIMALSS
                = new DiffUtil.ItemCallback<AnimalRescuerInfo>() {
            @Override
            public boolean areItemsTheSame(AnimalRescuerInfo oldItem, AnimalRescuerInfo newItem) {
                return (oldItem.getItemId() == newItem.getItemId()) && (oldItem.getRescuerId() == newItem.getRescuerId());
            }
            @Override
            public boolean areContentsTheSame(AnimalRescuerInfo oldItem, AnimalRescuerInfo newItem) {
                return oldItem.getUnitPrice() == newItem.getUnitPrice();
            }
        };
        private Typeface mAnimalSkuTypeface;
        private SparseArray<AnimalLite> mAnimalLiteSparseArray;
        private ArrayList<AnimalRescuerInfo> mAnimalRescuerInfoList;
        private View mLastFocusedView;
        private AnimalRescuerInfo mLastRemovedAnimalRescuerInfo;
        private AnimalLite mLastRemovedAnimalLite;
        private RescuerAnimalsUserActionsListener mActionsListener;
        RescuerAnimalsAdapter(Context context, RescuerAnimalsUserActionsListener userActionsListener) {
            super(DIFF_ANIMALSS);
            mActionsListener = userActionsListener;
            mAnimalSkuTypeface = ResourcesCompat.getFont(context, R.font.libre_barcode_128_text_regular);
        }
        @NonNull
        @Override
        public RescuerAnimalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_config_animal, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull RescuerAnimalsAdapter.ViewHolder holder, int position) {
            AnimalRescuerInfo animalRescuerInfo = getItem(position);
            AnimalLite animalLite = mAnimalLiteSparseArray.get(animalRescuerInfo.getItemId());
            holder.bind(position, animalRescuerInfo, animalLite);
        }
        void submitData(ArrayList<AnimalRescuerInfo> animalRescuerInfoList,
                        @Nullable SparseArray<AnimalLite> animalLiteSparseArray) {
            mAnimalLiteSparseArray = (animalLiteSparseArray == null) ? new SparseArray<>() : animalLiteSparseArray;
            mAnimalRescuerInfoList = animalRescuerInfoList;
            submitList(mAnimalRescuerInfoList);
        }
        @Override
        public void submitList(List<AnimalRescuerInfo> submittedList) {
            ArrayList<AnimalRescuerInfo> animalRescuerInfoList = new ArrayList<>(submittedList);
            super.submitList(animalRescuerInfoList);
        }
        void triggerFocusLost() {
            if (mLastFocusedView != null) {
                mLastFocusedView.clearFocus();
            }
        }
        SparseArray<AnimalLite> getAnimalLiteSparseArray() {
            return mAnimalLiteSparseArray;
        }
        ArrayList<AnimalLite> getAnimalLiteList() {
            ArrayList<AnimalLite> animalLiteList = new ArrayList<>();
            if (mAnimalLiteSparseArray != null && mAnimalLiteSparseArray.size() > 0) {
                int noOfAnimals = mAnimalLiteSparseArray.size();
                for (int index = 0; index < noOfAnimals; index++) {
                    animalLiteList.add(mAnimalLiteSparseArray.valueAt(index));
                }
            }
            return animalLiteList;
        }
        ArrayList<AnimalRescuerInfo> getAnimalRescuerInfoList() {
            return mAnimalRescuerInfoList;
        }
        ItemTouchHelper getItemTouchHelper() {
            return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int itemPosition = viewHolder.getAdapterPosition();
                    AnimalRescuerInfo animalRescuerInfo = getItem(itemPosition);
                    mLastRemovedAnimalLite = mAnimalLiteSparseArray.get(animalRescuerInfo.getItemId());
                    mLastRemovedAnimalRescuerInfo = animalRescuerInfo;
                    mAnimalLiteSparseArray.remove(animalRescuerInfo.getItemId());
                    mAnimalRescuerInfoList.remove(animalRescuerInfo);
                    submitList(mAnimalRescuerInfoList);
                    mActionsListener.onSwiped(itemPosition, mLastRemovedAnimalLite);
                }
            });
        }
        boolean restoreLastRemovedAnimal() {
            if (mLastRemovedAnimalRescuerInfo != null) {
                mAnimalRescuerInfoList.add(mLastRemovedAnimalRescuerInfo);
                mAnimalLiteSparseArray.put(mLastRemovedAnimalRescuerInfo.getItemId(), mLastRemovedAnimalLite);
                submitList(mAnimalRescuerInfoList);
                return true;
            }
            return false;
        }
        void notifyAnimalChanged(int animalId) {
            int noOfAnimals = mAnimalRescuerInfoList.size();
            for (int index = 0; index < noOfAnimals; index++) {
                AnimalRescuerInfo animalRescuerInfo = mAnimalRescuerInfoList.get(index);
                if (animalRescuerInfo.getItemId() == animalId) {
                    notifyItemChanged(index);
                    break;
                }
            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnFocusChangeListener, View.OnClickListener {
            private TextView mTextViewAnimalName;
            private ImageView mImageViewAnimalPhoto;
            private TextView mTextViewAnimalSku;
            private TextView mTextViewAnimalCategory;
            private TextView mTextViewAnimalPriceLabel;
            private EditText mEditTextAnimalPrice;
            ViewHolder(View itemView) {
                super(itemView);
                mTextViewAnimalName = itemView.findViewById(R.id.text_animal_item_name);
                mImageViewAnimalPhoto = itemView.findViewById(R.id.image_animal_item_photo);
                mTextViewAnimalSku = itemView.findViewById(R.id.text_animal_item_sku);
                mTextViewAnimalCategory = itemView.findViewById(R.id.text_animal_item_category);
                mTextViewAnimalPriceLabel = itemView.findViewById(R.id.text_rescuer_config_item_animal_price_label);
                mEditTextAnimalPrice = itemView.findViewById(R.id.edittext_rescuer_config_item_animal_price);
                Resources resources = itemView.getContext().getResources();

//                mTextViewAnimalPriceLabel.setText(resources.getString(R.string.rescuer_config_item_animal_label_price,
//                        Currency.getInstance(Locale.getDefault()).getSymbol()));
                mTextViewAnimalPriceLabel.setText("");
                mEditTextAnimalPrice.setHint("Image");
//                mEditTextAnimalPrice.setHint(Currency.getInstance(Locale.getDefault()).getCurrencyCode());
                mEditTextAnimalPrice.setOnFocusChangeListener(this);
                itemView.setOnClickListener(this);
            }
            void bind(int position, AnimalRescuerInfo animalRescuerInfo, @Nullable AnimalLite animalLite) {
                if (animalLite != null) {
                    mTextViewAnimalName.setText(animalLite.getName());
                    mTextViewAnimalSku.setText(animalLite.getSku());
                    mTextViewAnimalSku.setTypeface(mAnimalSkuTypeface);
                    ImageDownloaderFragment.newInstance(
                            ((FragmentActivity) mImageViewAnimalPhoto.getContext()).getSupportFragmentManager(), position)
                            .executeAndUpdate(mImageViewAnimalPhoto, animalLite.getDefaultImageUri(), position);
                    mTextViewAnimalCategory.setText(animalLite.getCategory());
                }
                if (animalRescuerInfo.getUnitPrice() > 0.0f) {
                    mEditTextAnimalPrice.setText(String.valueOf(animalRescuerInfo.getUnitPrice()));
                } else {
                    mEditTextAnimalPrice.setText(" ");
                }
            }
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AnimalRescuerInfo animalRescuerInfo = getItem(adapterPosition);
                    if (!hasFocus) {
                        mLastFocusedView = null;
                        switch (view.getId()) {
                            case R.id.edittext_rescuer_config_item_animal_price:
                                String priceStr = mEditTextAnimalPrice.getText().toString().trim();
                                if (!TextUtils.isEmpty(priceStr)) {
                                    animalRescuerInfo.setUnitPrice(Float.parseFloat(priceStr));
                                }
                                break;
                        }
                    } else {
                        mLastFocusedView = view;
                    }
                }
            }
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AnimalRescuerInfo animalRescuerInfo = getItem(adapterPosition);
                    AnimalLite animalLite = mAnimalLiteSparseArray.get(animalRescuerInfo.getItemId());
                    int clickedViewId = view.getId();
                    if (clickedViewId == itemView.getId()) {
                        mActionsListener.onEditAnimal(adapterPosition, animalLite, mImageViewAnimalPhoto);
                    }
                }
            }
        }
    }
    private class RescuerCodeTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mTextInputRescuerCode.setError(null);
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }
    private class RescuerAnimalItemUserActionsListener implements RescuerAnimalsUserActionsListener {
        @Override
        public void onEditAnimal(int itemPosition, AnimalLite animal, ImageView imageViewAnimalPhoto) {
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    imageViewAnimalPhoto,
                    TextUtils.isEmpty(animal.getDefaultImageUri()) ? getString(R.string.transition_name_animal_photo) : animal.getDefaultImageUri()
            );
            mPresenter.editAnimal(animal.getId(), activityOptionsCompat);
        }
        @Override
        public void onSwiped(int itemPosition, AnimalLite animal) {
            mPresenter.onRescuerAnimalSwiped(animal.getSku());
        }
    }
}
