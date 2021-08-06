package com.example.pethome.storeapp.ui.animals.config;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.ui.common.ProgressDialogFragment;
import com.example.pethome.storeapp.utils.OrientationUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import java.util.ArrayList;
import java.util.List;
public class AnimalConfigActivityFragment extends Fragment
        implements AnimalConfigContract.View, View.OnClickListener, View.OnFocusChangeListener {
    private static final String LOG_TAG = AnimalConfigActivityFragment.class.getSimpleName();
    private static final String ARGUMENT_INT_ANIMALS_ID = "argument.ANIMALS_ID";
    private static final String BUNDLE_ANIMALS_ID_INT_KEY = "AnimalConfig.AnimalId";
    private static final String BUNDLE_CATEGORY_SELECTED_STR_KEY = "AnimalConfig.Category";
    private static final String BUNDLE_CATEGORY_OTHER_STR_KEY = "AnimalConfig.CategoryOther";
    private static final String BUNDLE_ANIMALS_ATTRS_LIST_KEY = "AnimalConfig.Attributes";
    private static final String BUNDLE_ANIMALS_IMAGES_LIST_KEY = "AnimalConfig.Images";
    private static final String BUNDLE_EXISTING_ANIMALS_RESTORED_BOOL_KEY = "AnimalConfig.IsExistingAnimalRestored";
    private static final String BUNDLE_ANIMALS_SKU_VALID_BOOL_KEY = "AnimalConfig.IsAnimalSkuValid";
    private static final String BUNDLE_ANIMALS_NAME_ENTERED_BOOL_KEY = "AnimalConfig.IsAnimalNameEntered";
    private AnimalConfigContract.Presenter mPresenter;
    private EditText mEditTextAnimalName;
    private TextInputLayout mTextInputAnimalSku;
    private TextInputEditText mEditTextAnimalSku;
    private TextInputEditText mEditTextAnimalDescription;
    private Spinner mSpinnerAnimalCategory;
    private EditText mEditTextAnimalCategoryOther;
    private RecyclerView mRecyclerViewAnimalAttrs;
    private View mLastRegisteredFocusChangeView;
    private ArrayAdapter<String> mCategorySpinnerAdapter;
    private AnimalAttributesAdapter mAnimalAttributesAdapter;
    private int mAnimalId;
    private String mCategoryLastSelected;
    private String mCategoryOtherText;
    private ArrayList<AnimalImage> mAnimalImages;
    private boolean mIsExistingAnimalRestored;
    private boolean mIsAnimalSkuValid;
    private boolean mIsAnimalNameEntered;
    private DialogInterface.OnClickListener mAnimalDeleteDialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    mPresenter.deleteAnimal();
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
                    saveAnimal();
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
    public AnimalConfigActivityFragment() {
    }
    public static AnimalConfigActivityFragment newInstance(int animalId) {
        Bundle args = new Bundle(1);
        args.putInt(ARGUMENT_INT_ANIMALS_ID, animalId);
        AnimalConfigActivityFragment fragment = new AnimalConfigActivityFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_animal_config, container, false);
        mEditTextAnimalName = rootView.findViewById(R.id.edittext_animal_config_name);
        mTextInputAnimalSku = rootView.findViewById(R.id.textinput_animal_config_sku);
        mEditTextAnimalSku = rootView.findViewById(R.id.edittext_animal_config_sku);
        mEditTextAnimalDescription = rootView.findViewById(R.id.edittext_animal_config_description);
        mSpinnerAnimalCategory = rootView.findViewById(R.id.spinner_animal_config_category);
        mEditTextAnimalCategoryOther = rootView.findViewById(R.id.edittext_animal_config_category_other);
        mRecyclerViewAnimalAttrs = rootView.findViewById(R.id.recyclerview_animal_config_attrs);
        mEditTextAnimalName.setOnFocusChangeListener(this);
        mEditTextAnimalSku.setOnFocusChangeListener(this);
        mEditTextAnimalCategoryOther.setOnFocusChangeListener(this);
        mEditTextAnimalSku.addTextChangedListener(new AnimalSkuTextWatcher());
        Bundle arguments = getArguments();
        if (arguments != null) {
            mAnimalId = arguments.getInt(ARGUMENT_INT_ANIMALS_ID, AnimalConfigContract.NEW_ANIMALS_INT);
        }
        rootView.findViewById(R.id.btn_animal_config_add_attrs).setOnClickListener(this);
        setupCategorySpinner();
        setupAnimalAttrsRecyclerView();
        return rootView;
    }
    private void setupAnimalAttrsRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewAnimalAttrs.setLayoutManager(linearLayoutManager);
        mAnimalAttributesAdapter = new AnimalAttributesAdapter();
        mPresenter.updateAnimalAttributes(null);
        mRecyclerViewAnimalAttrs.setAdapter(mAnimalAttributesAdapter);
    }
    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        mCategorySpinnerAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.item_animal_config_category_spinner, categories);
        mCategorySpinnerAdapter.setDropDownViewResource(R.layout.item_animal_config_category_spinner_dropdown);
        mSpinnerAnimalCategory.setAdapter(mCategorySpinnerAdapter);
        mSpinnerAnimalCategory.setOnItemSelectedListener(new CategorySpinnerClickListener());
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_animal_config, menu);
        if (mAnimalId == AnimalConfigContract.NEW_ANIMALS_INT) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
            deleteMenuItem.setEnabled(false);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mPresenter.showDeleteAnimalDialog();
                return true;
            case R.id.action_save:
                saveAnimal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void saveAnimal() {
        mPresenter.triggerFocusLost();
        String animalName = mEditTextAnimalName.getText().toString().trim();
        String animalSku = mEditTextAnimalSku.getText().toString().trim();
        String animalDescription = mEditTextAnimalDescription.getText().toString().trim();
        ArrayList<AnimalAttribute> animalAttributes = mAnimalAttributesAdapter.getAnimalAttributes();
        mCategoryOtherText = mEditTextAnimalCategoryOther.getText().toString().trim();
        mPresenter.onSave(animalName,
                animalSku,
                animalDescription,
                mCategoryLastSelected,
                mCategoryOtherText,
                animalAttributes
        );
    }
    public void triggerFocusLost() {
        if (mLastRegisteredFocusChangeView != null) {
            mLastRegisteredFocusChangeView.clearFocus();
            mLastRegisteredFocusChangeView = null;
        }
        if (mAnimalAttributesAdapter != null) {
            mAnimalAttributesAdapter.triggerFocusLost();
        }
    }
    @Override
    public void showUpdateImagesSuccess() {
        if (getView() != null) {
            Snackbar.make(getView(),
                    getString(R.string.animal_config_update_item_images_success, mEditTextAnimalSku.getText()),
                    Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showCategoryOtherEditTextField() {
        mEditTextAnimalCategoryOther.setVisibility(View.VISIBLE);
    }
    @Override
    public void hideCategoryOtherEditTextField() {
        mEditTextAnimalCategoryOther.setVisibility(View.INVISIBLE);
    }
    @Override
    public void clearCategoryOtherEditTextField() {
        mEditTextAnimalCategoryOther.setText("");
    }
    @Override
    public void showEmptyFieldsValidationError() {
        showError(R.string.animal_config_empty_fields_validation_error);
    }
    @Override
    public void showAttributesPartialValidationError() {
        showError(R.string.animal_config_attrs_partial_empty_fields_validation_error);
    }
    @Override
    public void showAttributeNameConflictError(String attributeName) {
        showError(R.string.animal_config_attrs_name_conflict_error, attributeName);
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
    public void showError(@StringRes int messageId, @Nullable Object... args) {
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
    public void showAnimalSkuConflictError() {
        mTextInputAnimalSku.setError(getString(R.string.animal_config_sku_invalid_error));
    }
    @Override
    public void showAnimalSkuEmptyError() {
        mTextInputAnimalSku.setError(getString(R.string.animal_config_sku_empty_error));
    }
    @Override
    public void updateAnimalNameField(String name) {
        mEditTextAnimalName.setText(name);
    }
    @Override
    public void updateAnimalSkuField(String sku) {
        mEditTextAnimalSku.setText(sku);
    }
    @Override
    public void lockAnimalSkuField() {
        mTextInputAnimalSku.setEnabled(false);
    }
    @Override
    public void updateAnimalDescriptionField(String description) {
        mEditTextAnimalDescription.setText(description);
    }
    @Override
    public void updateCategorySelection(String selectedCategory, @Nullable String categoryOtherText) {
        mCategoryLastSelected = selectedCategory;
        mSpinnerAnimalCategory.setSelection(mCategorySpinnerAdapter.getPosition(selectedCategory));
        if (!TextUtils.isEmpty(categoryOtherText)) {
            mEditTextAnimalCategoryOther.setText(categoryOtherText);
        }
    }
    @Override
    public void syncExistingAnimalState(boolean isExistingAnimalRestored) {
        mIsExistingAnimalRestored = isExistingAnimalRestored;
    }
    @Override
    public void syncAnimalSkuValidity(boolean isAnimalSkuValid) {
        mIsAnimalSkuValid = isAnimalSkuValid;
    }
    @Override
    public void syncAnimalNameEnteredState(boolean isAnimalNameEntered) {
        mIsAnimalNameEntered = isAnimalNameEntered;
    }
    @Override
    public void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.animal_config_unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.animal_config_unsaved_changes_dialog_positive_text, mUnsavedDialogOnClickListener);
        builder.setNegativeButton(R.string.animal_config_unsaved_changes_dialog_negative_text, mUnsavedDialogOnClickListener);
        builder.setNeutralButton(R.string.animal_config_unsaved_changes_dialog_neutral_text, mUnsavedDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void showDeleteAnimalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.animal_config_delete_animal_confirm_dialog_message);
        builder.setPositiveButton(android.R.string.yes, mAnimalDeleteDialogOnClickListener);
        builder.setNegativeButton(android.R.string.no, mAnimalDeleteDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
        mAnimalAttributesAdapter.replaceAnimalAttributes(animalAttributes);
    }
    @Override
    public void updateAnimalImages(ArrayList<AnimalImage> animalImages) {
        mAnimalImages = animalImages;
    }
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            mLastRegisteredFocusChangeView = null;
            switch (view.getId()) {
                case R.id.edittext_animal_config_name:
                    mPresenter.updateAndSyncAnimalNameEnteredState(!TextUtils.isEmpty(mEditTextAnimalName.getText().toString().trim()));
                    break;
                case R.id.edittext_animal_config_sku:
                    if (mAnimalId == AnimalConfigContract.NEW_ANIMALS_INT) {
                        mPresenter.validateAnimalSku(mEditTextAnimalSku.getText().toString().trim());
                    }
                    break;
                case R.id.edittext_animal_config_category_other:
                    mPresenter.updateCategorySelection(mCategoryLastSelected, mEditTextAnimalCategoryOther.getText().toString());
                    break;
            }
        } else {
            mLastRegisteredFocusChangeView = view;
        }
    }
    @Override
    public void setPresenter(AnimalConfigContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mAnimalId = savedInstanceState.getInt(BUNDLE_ANIMALS_ID_INT_KEY);
            mCategoryLastSelected = savedInstanceState.getString(BUNDLE_CATEGORY_SELECTED_STR_KEY);
            mCategoryOtherText = savedInstanceState.getString(BUNDLE_CATEGORY_OTHER_STR_KEY);
            mPresenter.updateAnimalAttributes(savedInstanceState.getParcelableArrayList(BUNDLE_ANIMALS_ATTRS_LIST_KEY));
            mPresenter.updateAnimalImages(savedInstanceState.getParcelableArrayList(BUNDLE_ANIMALS_IMAGES_LIST_KEY));
            mPresenter.updateAndSyncAnimalNameEnteredState(savedInstanceState.getBoolean(BUNDLE_ANIMALS_NAME_ENTERED_BOOL_KEY,
                    false));
            mPresenter.updateAndSyncExistingAnimalState(savedInstanceState.getBoolean(BUNDLE_EXISTING_ANIMALS_RESTORED_BOOL_KEY,
                    false));
            mPresenter.updateAndSyncAnimalSkuValidity(savedInstanceState.getBoolean(BUNDLE_ANIMALS_SKU_VALID_BOOL_KEY,
                    false));
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.triggerFocusLost();
        outState.putInt(BUNDLE_ANIMALS_ID_INT_KEY, mAnimalId);
        outState.putString(BUNDLE_CATEGORY_SELECTED_STR_KEY, mCategoryLastSelected);
        outState.putString(BUNDLE_CATEGORY_OTHER_STR_KEY, mEditTextAnimalCategoryOther.getText().toString());
        outState.putParcelableArrayList(BUNDLE_ANIMALS_ATTRS_LIST_KEY, mAnimalAttributesAdapter.getAnimalAttributes());
        outState.putParcelableArrayList(BUNDLE_ANIMALS_IMAGES_LIST_KEY, mAnimalImages);
        outState.putBoolean(BUNDLE_ANIMALS_NAME_ENTERED_BOOL_KEY, mIsAnimalNameEntered);
        outState.putBoolean(BUNDLE_EXISTING_ANIMALS_RESTORED_BOOL_KEY, mIsExistingAnimalRestored);
        outState.putBoolean(BUNDLE_ANIMALS_SKU_VALID_BOOL_KEY, mIsAnimalSkuValid);
    }
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
    @Override
    public void updateCategories(List<String> categories) {
        mCategorySpinnerAdapter.clear();
        categories.add(0, mSpinnerAnimalCategory.getPrompt().toString());
        mCategorySpinnerAdapter.addAll(categories);
        mCategorySpinnerAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(mCategoryLastSelected)) {
            mPresenter.updateCategorySelection(mCategoryLastSelected, mCategoryOtherText);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_animal_config_add_attrs:
                mAnimalAttributesAdapter.addEmptyRecord();
                break;
        }
    }
    private static class AnimalAttributesAdapter extends RecyclerView.Adapter<AnimalAttributesAdapter.ViewHolder> {
        private View mLastFocusedView;
        private ArrayList<AnimalAttribute> mAnimalAttributes;
        AnimalAttributesAdapter() {
            mAnimalAttributes = new ArrayList<>();
        }
        @NonNull
        @Override
        public AnimalAttributesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_animal_config_attr, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull AnimalAttributesAdapter.ViewHolder holder, int position) {
            holder.bind(mAnimalAttributes.get(position));
        }
        @Override
        public int getItemCount() {
            return mAnimalAttributes.size();
        }
        void addEmptyRecord() {
            AnimalAttribute animalAttribute = new AnimalAttribute.Builder()
                    .createAnimalAttribute();
            mAnimalAttributes.add(animalAttribute);
            notifyItemInserted(mAnimalAttributes.size() - 1);
        }
        void replaceAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
            if (animalAttributes != null && animalAttributes.size() >= 0) {
                mAnimalAttributes.clear();
                mAnimalAttributes.addAll(animalAttributes);
                notifyDataSetChanged();
            }
            if (mAnimalAttributes.size() == 0) {
                addEmptyRecord();
            }
        }
        ArrayList<AnimalAttribute> getAnimalAttributes() {
            return mAnimalAttributes;
        }
        void triggerFocusLost() {
            if (mLastFocusedView != null) {
                mLastFocusedView.clearFocus();
                mLastFocusedView = null;
            }
        }
        void deleteRecord(int position) {
            if (position > RecyclerView.NO_POSITION) {
                mAnimalAttributes.remove(position);
                notifyItemRemoved(position);
            }
        }
        class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnFocusChangeListener {
            private TextInputEditText mEditTextAttrName;
            private TextInputEditText mEditTextAttrValue;
            private ImageButton mImageButtonRemoveAction;
            ViewHolder(View itemView) {
                super(itemView);
                mEditTextAttrName = itemView.findViewById(R.id.edittext_item_animal_config_attr_name);
                mEditTextAttrValue = itemView.findViewById(R.id.edittext_item_animal_config_attr_value);
                mImageButtonRemoveAction = itemView.findViewById(R.id.imgbtn_item_animal_config_attr_remove);
                mEditTextAttrName.setOnFocusChangeListener(this);
                mEditTextAttrValue.setOnFocusChangeListener(this);
                mImageButtonRemoveAction.setOnClickListener(this);
            }
            void bind(AnimalAttribute animalAttribute) {
                mEditTextAttrName.setText(animalAttribute.getAttributeName());
                mEditTextAttrValue.setText(animalAttribute.getAttributeValue());
            }
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    if (view.getId() == R.id.imgbtn_item_animal_config_attr_remove) {
                        deleteRecord(adapterPosition);
                    }
                }
            }
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AnimalAttribute animalAttribute = mAnimalAttributes.get(adapterPosition);
                    if (!hasFocus) {
                        mLastFocusedView = null;
                        switch (view.getId()) {
                            case R.id.edittext_item_animal_config_attr_name:
                                animalAttribute.setAttributeName(mEditTextAttrName.getText().toString().trim());
                                break;
                            case R.id.edittext_item_animal_config_attr_value:
                                animalAttribute.setAttributeValue(mEditTextAttrValue.getText().toString().trim());
                                break;
                        }
                    } else {
                        mLastFocusedView = view;
                    }
                }
            }
        }
    }
    private class AnimalSkuTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mTextInputAnimalSku.setError(null);
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }
    private class CategorySpinnerClickListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                mCategoryLastSelected = parent.getItemAtPosition(position).toString();
                mPresenter.onCategorySelected(mCategoryLastSelected);
            } else {
                mCategoryLastSelected = "";
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
