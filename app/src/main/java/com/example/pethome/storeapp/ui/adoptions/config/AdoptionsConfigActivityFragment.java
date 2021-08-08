package com.example.pethome.storeapp.ui.adoptions.config;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalAttribute;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.data.local.models.AnimalRescuerAdoptions;
import com.example.pethome.storeapp.ui.common.ProgressDialogFragment;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigContract;
import com.example.pethome.storeapp.utils.OrientationUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
public class AdoptionsConfigActivityFragment extends Fragment implements AdoptionsConfigContract.View, View.OnClickListener {
    private static final String LOG_TAG = AdoptionsConfigActivityFragment.class.getSimpleName();
    private static final String ARGUMENT_INT_ANIMALS_ID = "argument.ANIMALS_ID";
    private static final String BUNDLE_ANIMALS_NAME_KEY = "AdoptionsConfig.AnimalName";
    private static final String BUNDLE_ANIMALS_SKU_KEY = "AdoptionsConfig.AnimalSku";
    private static final String BUNDLE_ANIMALS_DESCRIPTION_KEY = "AdoptionsConfig.AnimalDescription";
    private static final String BUNDLE_ANIMALS_CATEGORY_KEY = "AdoptionsConfig.AnimalCategory";
    private static final String BUNDLE_ANIMALS_ORIGINAL_TOTAL_AVAIL_QTY_INT_KEY = "AdoptionsConfig.OriginalTotalAvailableQuantity";
    private static final String BUNDLE_ANIMALS_IMAGES_LIST_KEY = "AdoptionsConfig.AnimalImages";
    private static final String BUNDLE_ANIMALS_ATTRS_LIST_KEY = "AdoptionsConfig.AnimalAttributes";
    private static final String BUNDLE_ANIMALS_RESCUERS_LIST_KEY = "AdoptionsConfig.AnimalRescuers";
    private static final String BUNDLE_ANIMALS_RESTORED_BOOL_KEY = "AdoptionsConfig.IsAnimalRestored";
    private static final String BUNDLE_RESCUERS_RESTORED_BOOL_KEY = "AdoptionsConfig.AreRescuersRestored";
    private AdoptionsConfigContract.Presenter mPresenter;
    private TextView mTextViewAnimalName;
    private TextView mTextViewAnimalSku;
    private TextView mTextViewAnimalDesc;
    private TextView mTextViewAnimalCategory;
    private TextView mTextViewAnimalAvailableQuantity;
    private TableLayout mTableLayoutAnimalAttrs;
    private RecyclerView mRecyclerViewAnimalRescuers;
    private AnimalRescuersAdapter mAnimalRescuersAdapter;
    private int mAnimalId;
    private ArrayList<AnimalAttribute> mAnimalAttributes;
    private ArrayList<AnimalImage> mAnimalImages;
    private boolean mIsAnimalRestored;
    private boolean mAreRescuersRestored;
    private int mOldTotalAvailableQuantity;
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
                    saveAnimalAdoptions();
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
    public AdoptionsConfigActivityFragment() {
    }
    public static AdoptionsConfigActivityFragment newInstance(int animalId) {
        Bundle args = new Bundle(1);
        args.putInt(ARGUMENT_INT_ANIMALS_ID, animalId);
        AdoptionsConfigActivityFragment fragment = new AdoptionsConfigActivityFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_adoptions_config, container, false);
        mTextViewAnimalName = rootView.findViewById(R.id.text_adoptions_config_animal_name);
        mTextViewAnimalSku = rootView.findViewById(R.id.text_adoptions_config_animal_sku);
        mTextViewAnimalDesc = rootView.findViewById(R.id.text_adoptions_config_animal_desc);
        mTextViewAnimalCategory = rootView.findViewById(R.id.text_adoptions_config_animal_category);
        mTextViewAnimalAvailableQuantity = rootView.findViewById(R.id.text_adoptions_config_total_available_quantity);
        mTableLayoutAnimalAttrs = rootView.findViewById(R.id.tablelayout_adoptions_config_animal_attrs);
        mRecyclerViewAnimalRescuers = rootView.findViewById(R.id.recyclerview_adoptions_config_rescuers);
        rootView.findViewById(R.id.imgbtn_adoptions_config_animal_edit).setOnClickListener(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mAnimalId = arguments.getInt(ARGUMENT_INT_ANIMALS_ID, AnimalConfigContract.NEW_ANIMALS_INT);
        }
        setupAnimalRescuersRecyclerView();
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mAnimalId == AnimalConfigContract.NEW_ANIMALS_INT) {
            mPresenter.doCancel();
        } else {
            mPresenter.start();
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mPresenter.updateAnimalName(savedInstanceState.getString(BUNDLE_ANIMALS_NAME_KEY));
            mPresenter.updateAnimalSku(savedInstanceState.getString(BUNDLE_ANIMALS_SKU_KEY));
            mPresenter.updateAnimalDescription(savedInstanceState.getString(BUNDLE_ANIMALS_DESCRIPTION_KEY));
            mPresenter.updateAnimalCategory(savedInstanceState.getString(BUNDLE_ANIMALS_CATEGORY_KEY));
            mPresenter.updateAnimalImage(savedInstanceState.getParcelableArrayList(BUNDLE_ANIMALS_IMAGES_LIST_KEY));
            mPresenter.updateAnimalAttributes(savedInstanceState.getParcelableArrayList(BUNDLE_ANIMALS_ATTRS_LIST_KEY));
            mPresenter.updateAnimalRescuerAdoptionsList(savedInstanceState.getParcelableArrayList(BUNDLE_ANIMALS_RESCUERS_LIST_KEY));
            mPresenter.updateAndSyncOldTotalAvailability(savedInstanceState.getInt(BUNDLE_ANIMALS_ORIGINAL_TOTAL_AVAIL_QTY_INT_KEY));
            mPresenter.updateAndSyncAnimalState(savedInstanceState.getBoolean(BUNDLE_ANIMALS_RESTORED_BOOL_KEY, false));
            mPresenter.updateAndSyncRescuersState(savedInstanceState.getBoolean(BUNDLE_RESCUERS_RESTORED_BOOL_KEY, false));
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.triggerFocusLost();
        outState.putString(BUNDLE_ANIMALS_NAME_KEY, mTextViewAnimalName.getText().toString());
        outState.putString(BUNDLE_ANIMALS_SKU_KEY, mTextViewAnimalSku.getText().toString());
        outState.putString(BUNDLE_ANIMALS_DESCRIPTION_KEY, mTextViewAnimalDesc.getText().toString());
        outState.putString(BUNDLE_ANIMALS_CATEGORY_KEY, mTextViewAnimalCategory.getText().toString());
        outState.putInt(BUNDLE_ANIMALS_ORIGINAL_TOTAL_AVAIL_QTY_INT_KEY, mOldTotalAvailableQuantity);
        outState.putParcelableArrayList(BUNDLE_ANIMALS_IMAGES_LIST_KEY, mAnimalImages);
        outState.putParcelableArrayList(BUNDLE_ANIMALS_ATTRS_LIST_KEY, mAnimalAttributes);
        outState.putParcelableArrayList(BUNDLE_ANIMALS_RESCUERS_LIST_KEY, mAnimalRescuersAdapter.getAnimalRescuerAdoptionsList());
        outState.putBoolean(BUNDLE_ANIMALS_RESTORED_BOOL_KEY, mIsAnimalRestored);
        outState.putBoolean(BUNDLE_RESCUERS_RESTORED_BOOL_KEY, mAreRescuersRestored);
    }
    @Override
    public void setPresenter(AdoptionsConfigContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_adoptions_config, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                mPresenter.showDeleteAnimalDialog();
                return true;
            case R.id.action_save:
                saveAnimalAdoptions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void saveAnimalAdoptions() {
        mPresenter.triggerFocusLost();
        mPresenter.onSave(mAnimalRescuersAdapter.getAnimalRescuerAdoptionsList());
    }
    private void setupAnimalRescuersRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewAnimalRescuers.setLayoutManager(linearLayoutManager);
        mAnimalRescuersAdapter = new AnimalRescuersAdapter(new AnimalRescuerItemUserActionsListener());
        mRecyclerViewAnimalRescuers.setAdapter(mAnimalRescuersAdapter);
        mAnimalRescuersAdapter.getItemTouchHelper().attachToRecyclerView(mRecyclerViewAnimalRescuers);
    }
    @Override
    public void syncAnimalState(boolean isAnimalRestored) {
        mIsAnimalRestored = isAnimalRestored;
    }
    @Override
    public void syncRescuersState(boolean areRescuersRestored) {
        mAreRescuersRestored = areRescuersRestored;
    }
    @Override
    public void syncOldTotalAvailability(int oldTotalAvailableQuantity) {
        mOldTotalAvailableQuantity = oldTotalAvailableQuantity;
    }
    @Override
    public void showProgressIndicator(int statusTextId) {
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
    public void updateAnimalName(String animalName) {
        mTextViewAnimalName.setText(animalName);
    }
    @Override
    public void updateAnimalSku(String animalSku) {
        mTextViewAnimalSku.setText(animalSku);
        mTextViewAnimalSku.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.libre_barcode_128_text_regular));
    }
    @Override
    public void updateAnimalCategory(String animalCategory) {
        mTextViewAnimalCategory.setText(animalCategory);
    }
    @Override
    public void updateAnimalDescription(String description) {
        mTextViewAnimalDesc.setText(description);
    }
    @Override
    public void updateAnimalImages(ArrayList<AnimalImage> animalImages) {
        mAnimalImages = animalImages;
    }
    @Override
    public void updateAnimalAttributes(ArrayList<AnimalAttribute> animalAttributes) {
        mAnimalAttributes = animalAttributes;
        mTableLayoutAnimalAttrs.setStretchAllColumns(true);
        mTableLayoutAnimalAttrs.removeAllViewsInLayout();
        int noOfAnimalAttrs = mAnimalAttributes.size();
        for (int index = 0; index < noOfAnimalAttrs; index++) {
            AnimalAttribute animalAttribute = mAnimalAttributes.get(index);
            TableRow tableRow = new TableRow(requireContext());
            TableLayout.LayoutParams tableRowLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            mTableLayoutAnimalAttrs.addView(tableRow, tableRowLayoutParams);
            if (index % 2 == 0) {
                tableRow.setBackgroundResource(R.drawable.shape_adoptions_config_animal_attrs_table_bg_even_row);
            } else {
                tableRow.setBackgroundResource(R.drawable.shape_adoptions_config_animal_attrs_table_bg_odd_row);
            }
            TableRow.LayoutParams textViewCellLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
            TextView textViewAttrName = (TextView) LayoutInflater.from(requireContext()).inflate(R.layout.layout_adoptions_config_animal_attrs_table_cell_name, tableRow, false);
            textViewAttrName.setText(animalAttribute.getAttributeName());
            tableRow.addView(textViewAttrName, textViewCellLayoutParams);
            TextView textViewAttrValue = (TextView) LayoutInflater.from(requireContext()).inflate(R.layout.layout_adoptions_config_animal_attrs_table_cell_value, tableRow, false);
            textViewAttrValue.setText(animalAttribute.getAttributeValue());
            tableRow.addView(textViewAttrValue, textViewCellLayoutParams);
        }
    }
    @Override
    public void loadAnimalRescuersData(ArrayList<AnimalRescuerAdoptions> animalRescuerAdoptionsList) {
        mAnimalRescuersAdapter.submitList(animalRescuerAdoptionsList);
    }
    @Override
    public void updateAvailability(int totalAvailableQuantity) {
        mTextViewAnimalAvailableQuantity.setText(String.valueOf(totalAvailableQuantity));
        mTextViewAnimalAvailableQuantity.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
    }
    @Override
    public void showOutOfStockAlert() {
        mTextViewAnimalAvailableQuantity.setText(getString(R.string.adoptions_list_item_out_of_stock));
        mTextViewAnimalAvailableQuantity.setTextColor(ContextCompat.getColor(requireContext(), R.color.adoptionsListItemOutOfStockColor));
    }
    @Override
    public void showAnimalRescuerSwiped(String rescuerCode) {
        if (getView() != null) {
            new SnackbarUtility(Snackbar.make(getView(),
                    getString(R.string.adoptions_config_rescuer_swipe_action_success,
                            rescuerCode), Snackbar.LENGTH_LONG))
                    .revealCompleteMessage()
                    .setAction(R.string.snackbar_action_undo, (view) -> {
                        if (mAnimalRescuersAdapter.restoreLastRemovedAnimalRescuerAdoptions()) {
                            Snackbar.make(getView(),
                                    getString(R.string.adoptions_config_rescuer_swipe_action_undo_success, rescuerCode),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .showSnack();
        }
    }
    @Override
    public void showUpdateAnimalSuccess(String animalSku) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.animal_list_item_update_success, animalSku), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showUpdateRescuerSuccess(String rescuerCode) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.rescuer_list_item_update_success, rescuerCode), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showDeleteRescuerSuccess(String rescuerCode) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.rescuer_list_item_delete_success, rescuerCode), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void triggerFocusLost() {
        if (mAnimalRescuersAdapter != null) {
            mAnimalRescuersAdapter.triggerFocusLost();
        }
    }
    @Override
    public void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.adoptions_config_unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.adoptions_config_unsaved_changes_dialog_positive_text, mUnsavedDialogOnClickListener);
        builder.setNegativeButton(R.string.adoptions_config_unsaved_changes_dialog_negative_text, mUnsavedDialogOnClickListener);
        builder.setNeutralButton(R.string.adoptions_config_unsaved_changes_dialog_neutral_text, mUnsavedDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void showDeleteAnimalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.adoptions_config_delete_animal_confirm_dialog_message);
        builder.setPositiveButton(android.R.string.yes, mAnimalDeleteDialogOnClickListener);
        builder.setNegativeButton(android.R.string.no, mAnimalDeleteDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_adoptions_config_animal_edit:
                mPresenter.editAnimal(mAnimalId);
                break;
        }
    }
    private static class AnimalRescuersAdapter extends ListAdapter<AnimalRescuerAdoptions, AnimalRescuersAdapter.ViewHolder> {
        private static DiffUtil.ItemCallback<AnimalRescuerAdoptions> DIFF_RESCUERS
                = new DiffUtil.ItemCallback<AnimalRescuerAdoptions>() {
            @Override
            public boolean areItemsTheSame(AnimalRescuerAdoptions oldItem, AnimalRescuerAdoptions newItem) {
                return (oldItem.getItemId() == newItem.getItemId()) && (oldItem.getRescuerId() == newItem.getRescuerId());
            }
            @Override
            public boolean areContentsTheSame(AnimalRescuerAdoptions oldItem, AnimalRescuerAdoptions newItem) {
                return oldItem.equals(newItem);
            }
        };
        private ArrayList<AnimalRescuerAdoptions> mAnimalRescuerAdoptionsList;
        private View mLastFocusedView;
        private AnimalRescuerAdoptions mLastRemovedAnimalRescuerAdoptions;
        private AnimalRescuersUserActionsListener mActionsListener;
        AnimalRescuersAdapter(AnimalRescuersUserActionsListener userActionsListener) {
            super(DIFF_RESCUERS);
            mActionsListener = userActionsListener;
        }
        @NonNull
        @Override
        public AnimalRescuersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adoptions_config_rescuer, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull AnimalRescuersAdapter.ViewHolder holder, int position) {
            AnimalRescuerAdoptions animalRescuerAdoptions = getItem(position);
            holder.bind(animalRescuerAdoptions);
        }
        @Override
        public void submitList(List<AnimalRescuerAdoptions> submittedList) {
            if (mAnimalRescuerAdoptionsList != null && mAnimalRescuerAdoptionsList.size() > 0) {
                SparseIntArray rescuersQuantityArray = new SparseIntArray();
                for (AnimalRescuerAdoptions animalRescuerAdoptions : mAnimalRescuerAdoptionsList) {
                    rescuersQuantityArray.put(animalRescuerAdoptions.getRescuerId(), animalRescuerAdoptions.getAvailableQuantity());
                }
                if (rescuersQuantityArray.size() > 0) {
                    for (AnimalRescuerAdoptions animalRescuerAdoptions : submittedList) {
                        animalRescuerAdoptions.setAvailableQuantity(rescuersQuantityArray.get(animalRescuerAdoptions.getRescuerId()));
                    }
                }
            }
            mAnimalRescuerAdoptionsList = new ArrayList<>();
            mAnimalRescuerAdoptionsList.addAll(submittedList);
            int totalAvailableQuantity = 0;
            for (AnimalRescuerAdoptions animalRescuerAdoptions : mAnimalRescuerAdoptionsList) {
                totalAvailableQuantity += animalRescuerAdoptions.getAvailableQuantity();
            }
            mActionsListener.onUpdatedAvailability(totalAvailableQuantity);
            ArrayList<AnimalRescuerAdoptions> newAnimalRescuerAdoptionsList = new ArrayList<>(submittedList);
            super.submitList(newAnimalRescuerAdoptionsList);
        }
        void triggerFocusLost() {
            if (mLastFocusedView != null) {
                mLastFocusedView.clearFocus();
            }
        }
        ArrayList<AnimalRescuerAdoptions> getAnimalRescuerAdoptionsList() {
            return mAnimalRescuerAdoptionsList;
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
                    AnimalRescuerAdoptions animalRescuerAdoptions = getItem(itemPosition);
                    mLastRemovedAnimalRescuerAdoptions = animalRescuerAdoptions;
                    mAnimalRescuerAdoptionsList.remove(animalRescuerAdoptions);
                    submitList(mAnimalRescuerAdoptionsList);
                    mActionsListener.onSwiped(itemPosition, mLastRemovedAnimalRescuerAdoptions);
                }
            });
        }
        boolean restoreLastRemovedAnimalRescuerAdoptions() {
            if (mLastRemovedAnimalRescuerAdoptions != null) {
                mAnimalRescuerAdoptionsList.add(mLastRemovedAnimalRescuerAdoptions);
                submitList(mAnimalRescuerAdoptionsList);
                return true;
            }
            return false;
        }
        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnFocusChangeListener {
            private TextView mTextViewRescuerNameCode;
            private TextView mTextViewRescuerPrice;
            private TextView mTextViewOutOfStockAlert;
            private EditText mEditTextAvailableQuantity;
            ViewHolder(View itemView) {
                super(itemView);
                mTextViewRescuerNameCode = itemView.findViewById(R.id.text_adoptions_config_item_rescuer_name_code);
                mTextViewRescuerPrice = itemView.findViewById(R.id.text_adoptions_config_item_rescuer_rescuing_price);
                mTextViewOutOfStockAlert = itemView.findViewById(R.id.text_adoptions_config_item_rescuer_out_of_stock_alert);
                mEditTextAvailableQuantity = itemView.findViewById(R.id.edittext_adoptions_config_item_rescuer_qty);
                itemView.findViewById(R.id.imgbtn_adoptions_config_item_rescuer_increase_qty).setOnClickListener(this);
                itemView.findViewById(R.id.imgbtn_adoptions_config_item_rescuer_decrease_qty).setOnClickListener(this);
                itemView.findViewById(R.id.btn_adoptions_config_item_rescuer_edit).setOnClickListener(this);
                mEditTextAvailableQuantity.setOnFocusChangeListener(this);
            }
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AnimalRescuerAdoptions animalRescuerAdoptions = getItem(adapterPosition);
                    switch (view.getId()) {
                        case R.id.imgbtn_adoptions_config_item_rescuer_increase_qty:
                        {
                            triggerFocusLost();
                            String availableQtyStr = mEditTextAvailableQuantity.getText().toString().trim();
                            if (!TextUtils.isEmpty(availableQtyStr)) {
                                int currentAvailableQuantity = Integer.parseInt(availableQtyStr);
                                int updatedAvailableQuantity = currentAvailableQuantity + 1;
                                mEditTextAvailableQuantity.setText(String.valueOf(updatedAvailableQuantity));
                                animalRescuerAdoptions.setAvailableQuantity(updatedAvailableQuantity);
                                mActionsListener.onChangeInAvailability(updatedAvailableQuantity - currentAvailableQuantity);
                                setOutOfStockAlertVisibility(updatedAvailableQuantity);
                            }
                        }
                        break;
                        case R.id.imgbtn_adoptions_config_item_rescuer_decrease_qty:
                        {
                            triggerFocusLost();
                            String availableQtyStr = mEditTextAvailableQuantity.getText().toString().trim();
                            if (!TextUtils.isEmpty(availableQtyStr)) {
                                int currentAvailableQuantity = Integer.parseInt(availableQtyStr);
                                int updatedAvailableQuantity = currentAvailableQuantity - 1;
                                if (updatedAvailableQuantity >= 0) {
                                    mEditTextAvailableQuantity.setText(String.valueOf(updatedAvailableQuantity));
                                    animalRescuerAdoptions.setAvailableQuantity(updatedAvailableQuantity);
                                    mActionsListener.onChangeInAvailability(updatedAvailableQuantity - currentAvailableQuantity);
                                    setOutOfStockAlertVisibility(updatedAvailableQuantity);
                                }
                            }
                        }
                        break;
                        case R.id.btn_adoptions_config_item_rescuer_edit:
                            mActionsListener.onEditRescuer(adapterPosition, animalRescuerAdoptions);
                            break;
                    }
                }
            }
            private void setOutOfStockAlertVisibility(int availableQuantity) {
                if (availableQuantity > 0) {
                    mTextViewOutOfStockAlert.setVisibility(View.GONE);
                } else {
                    mTextViewOutOfStockAlert.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AnimalRescuerAdoptions animalRescuerAdoptions = getItem(adapterPosition);
                    if (!hasFocus) {
                        mLastFocusedView = null;
                        switch (view.getId()) {
                            case R.id.edittext_adoptions_config_item_rescuer_qty:
                                String availableQtyStr = mEditTextAvailableQuantity.getText().toString().trim();
                                int oldAvailableQuantity = animalRescuerAdoptions.getAvailableQuantity();
                                int updatedAvailableQuantity = 0;
                                if (!TextUtils.isEmpty(availableQtyStr)) {
                                    updatedAvailableQuantity = Integer.parseInt(availableQtyStr);
                                } else {
                                    mEditTextAvailableQuantity.setText(String.valueOf(updatedAvailableQuantity));
                                }
                                if (updatedAvailableQuantity < 0) {
                                    updatedAvailableQuantity = 0;
                                    mEditTextAvailableQuantity.setText(String.valueOf(updatedAvailableQuantity));
                                }
                                animalRescuerAdoptions.setAvailableQuantity(updatedAvailableQuantity);
                                setOutOfStockAlertVisibility(updatedAvailableQuantity);
                                mActionsListener.onChangeInAvailability(updatedAvailableQuantity - oldAvailableQuantity);
                                break;
                        }
                    } else {
                        mLastFocusedView = view;
                    }
                }
            }
            void bind(AnimalRescuerAdoptions animalRescuerAdoptions) {
                if (animalRescuerAdoptions != null) {
                    Resources resources = itemView.getContext().getResources();
                    mTextViewRescuerNameCode.setText(resources.getString(R.string.adoptions_list_item_rescuer_name_code_format,
                            animalRescuerAdoptions.getRescuerName(), animalRescuerAdoptions.getRescuerCode()));
                    mTextViewRescuerPrice.setText(resources.getString(R.string.adoptions_config_item_rescuer_rescuing_price,
                            animalRescuerAdoptions.getUnitPrice() + " " + Currency.getInstance(Locale.getDefault()).getCurrencyCode()));
                    int availableQuantity = animalRescuerAdoptions.getAvailableQuantity();
                    mEditTextAvailableQuantity.setText(String.valueOf(availableQuantity));
                    setOutOfStockAlertVisibility(availableQuantity);
                }
            }
        }
    }
    private class AnimalRescuerItemUserActionsListener implements AnimalRescuersUserActionsListener {
        @Override
        public void onEditRescuer(int itemPosition, AnimalRescuerAdoptions animalRescuerAdoptions) {
            mPresenter.editRescuer(animalRescuerAdoptions.getRescuerId());
        }
        @Override
        public void onSwiped(int itemPosition, AnimalRescuerAdoptions animalRescuerAdoptions) {
            mPresenter.onAnimalRescuerSwiped(animalRescuerAdoptions.getRescuerCode());
        }
        @Override
        public void onUpdatedAvailability(int totalAvailableQuantity) {
            mPresenter.updateAvailability(totalAvailableQuantity);
        }
        @Override
        public void onChangeInAvailability(int changeInAvailableQuantity) {
            mPresenter.changeAvailability(changeInAvailableQuantity);
        }
    }
}
