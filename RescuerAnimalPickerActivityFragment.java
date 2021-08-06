package com.example.pethome.storeapp.ui.rescuers.animal;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.ui.common.ListItemSpacingDecoration;
import com.example.pethome.storeapp.ui.common.ProgressDialogFragment;
import com.example.pethome.storeapp.utils.OrientationUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
import java.util.ArrayList;
import java.util.List;
public class RescuerAnimalPickerActivityFragment extends Fragment implements RescuerAnimalPickerContract.View {
    private static final String LOG_TAG = RescuerAnimalPickerActivityFragment.class.getSimpleName();
    private static final String ARGUMENT_LIST_RESCUER_ANIMALSS = "argument.RESCUER_ANIMALSS";
    private static final String BUNDLE_REMAINING_ANIMALSS_LIST_KEY = "RescuerAnimalPicker.RemainingAnimals";
    private static final String BUNDLE_SELECTED_ANIMALSS_LIST_KEY = "RescuerAnimalPicker.SelectedAnimals";
    private RescuerAnimalPickerContract.Presenter mPresenter;
    private RecyclerView mRecyclerViewAnimals;
    private TextView mTextViewEmptyList;
    private AnimalListAdapter mAnimalListAdapter;
    private ArrayList<AnimalLite> mRegisteredAnimals;
    private DialogInterface.OnClickListener mUnsavedDialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    saveSelectedAnimals();
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

    public RescuerAnimalPickerActivityFragment() {
    }

    public static RescuerAnimalPickerActivityFragment newInstance(ArrayList<AnimalLite> rescuerAnimals) {
        Bundle args = new Bundle(1);
        args.putParcelableArrayList(ARGUMENT_LIST_RESCUER_ANIMALSS, rescuerAnimals);
        RescuerAnimalPickerActivityFragment fragment = new RescuerAnimalPickerActivityFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_rescuer_animal_picker, container, false);
        mTextViewEmptyList = rootView.findViewById(R.id.text_rescuer_animal_picker_empty_list);
        mRecyclerViewAnimals = rootView.findViewById(R.id.recyclerview_rescuer_animal_picker);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mRegisteredAnimals = arguments.getParcelableArrayList(ARGUMENT_LIST_RESCUER_ANIMALSS);
        }
        setupAnimalListRecyclerView();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAnimalListAdapter != null) {
            outState.putParcelableArrayList(BUNDLE_REMAINING_ANIMALSS_LIST_KEY, mAnimalListAdapter.getRemainingAnimals());
            outState.putParcelableArrayList(BUNDLE_SELECTED_ANIMALSS_LIST_KEY, mAnimalListAdapter.getSelectedAnimals());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<AnimalLite> remainingAnimals = savedInstanceState.getParcelableArrayList(BUNDLE_REMAINING_ANIMALSS_LIST_KEY);
            ArrayList<AnimalLite> selectedAnimals = savedInstanceState.getParcelableArrayList(BUNDLE_SELECTED_ANIMALSS_LIST_KEY);
            mPresenter.loadAnimalsToPick(mRegisteredAnimals, remainingAnimals, selectedAnimals);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_rescuer_animal_picker, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadAnimalsToPick(mRegisteredAnimals, null, null);
    }

    @Override
    public void setPresenter(RescuerAnimalPickerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveSelectedAnimals();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSelectedAnimals() {
        mPresenter.onSave(mRegisteredAnimals, mAnimalListAdapter.getSelectedAnimals());
    }

    private void setupAnimalListRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewAnimals.setLayoutManager(linearLayoutManager);
        mAnimalListAdapter = new AnimalListAdapter(requireContext(), new UserActionsListener());
        mRecyclerViewAnimals.setAdapter(mAnimalListAdapter);
        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.rescuer_animal_list_items_spacing);
        mRecyclerViewAnimals.addItemDecoration(new ListItemSpacingDecoration(itemSpacing, itemSpacing));
        mRecyclerViewAnimals.setHasFixedSize(true);
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
    public void hideEmptyView() {
        mRecyclerViewAnimals.setVisibility(View.VISIBLE);
        mTextViewEmptyList.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView(int emptyTextResId, @Nullable Object... args) {
        mRecyclerViewAnimals.setVisibility(View.INVISIBLE);
        mTextViewEmptyList.setVisibility(View.VISIBLE);
        String messageToBeShown;
        if (args != null && args.length > 0) {
            messageToBeShown = getString(emptyTextResId, args);
        } else {
            messageToBeShown = getString(emptyTextResId);
        }
        mTextViewEmptyList.setText(messageToBeShown);
    }

    @Override
    public void submitDataToAdapter(ArrayList<AnimalLite> remainingAnimals, @Nullable ArrayList<AnimalLite> selectedAnimals) {
        mAnimalListAdapter.submitData(remainingAnimals, selectedAnimals);
    }

    @Override
    public void filterAdapterData(String searchQueryStr, Filter.FilterListener filterListener) {
        mAnimalListAdapter.getFilter().filter(searchQueryStr, filterListener);
    }

    @Override
    public void clearAdapterFilter(Filter.FilterListener filterListener) {
        mAnimalListAdapter.getFilter().filter(null, filterListener);
    }

    @Override
    public void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.rescuer_animal_picker_unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.rescuer_animal_picker_unsaved_changes_dialog_positive_text, mUnsavedDialogOnClickListener);
        builder.setNegativeButton(R.string.rescuer_animal_picker_unsaved_changes_dialog_negative_text, mUnsavedDialogOnClickListener);
        builder.setNeutralButton(R.string.rescuer_animal_picker_unsaved_changes_dialog_neutral_text, mUnsavedDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }

    private static class AnimalListAdapter extends ListAdapter<AnimalLite, AnimalListAdapter.ViewHolder>
            implements Filterable {
        private static final String PAYLOAD_SELECTED_ANIMALS = "Payload.SelectedAnimalPosition";
        private static final String PAYLOAD_UNSELECTED_ANIMALS = "Payload.UnselectedAnimalPosition";
        private static DiffUtil.ItemCallback<AnimalLite> DIFF_ANIMALSS
                = new DiffUtil.ItemCallback<AnimalLite>() {
            @Override
            public boolean areItemsTheSame(AnimalLite oldItem, AnimalLite newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(AnimalLite oldItem, AnimalLite newItem) {
                return oldItem.equals(newItem);
            }
        };
        private Typeface mAnimalSkuTypeface;
        private RescuerAnimalPickerListUserActionsListener mActionsListener;
        private ArrayList<AnimalLite> mRemainingAnimals;
        private ArrayList<AnimalLite> mSelectedAnimals;

        AnimalListAdapter(Context context, RescuerAnimalPickerListUserActionsListener userActionsListener) {
            super(DIFF_ANIMALSS);
            mActionsListener = userActionsListener;
            mSelectedAnimals = new ArrayList<>();
            mAnimalSkuTypeface = ResourcesCompat.getFont(context, R.font.libre_barcode_128_text_regular);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_animal_picker, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AnimalLite animalLite = getItem(position);
            holder.bind(position, animalLite);
            holder.setSelected(mSelectedAnimals.contains(animalLite));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                Bundle bundle = (Bundle) payloads.get(0);
                for (String keyStr : bundle.keySet()) {
                    switch (keyStr) {
                        case PAYLOAD_SELECTED_ANIMALS:
                            int selectedAnimalPosition = bundle.getInt(keyStr, RecyclerView.NO_POSITION);
                            if (selectedAnimalPosition > RecyclerView.NO_POSITION
                                    && selectedAnimalPosition == position) {
                                holder.setSelected(true);
                            }
                            break;
                        case PAYLOAD_UNSELECTED_ANIMALS:
                            int unselectedAnimalPosition = bundle.getInt(keyStr, RecyclerView.NO_POSITION);
                            if (unselectedAnimalPosition > RecyclerView.NO_POSITION
                                    && unselectedAnimalPosition == position) {
                                holder.setSelected(false);
                            }
                            break;
                    }
                }
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (TextUtils.isEmpty(constraint)) {
                        filterResults.values = mRemainingAnimals;
                        filterResults.count = mRemainingAnimals.size();
                    } else {
                        constraint = constraint.toString().trim();
                        ArrayList<AnimalLite> filteredAnimalList = new ArrayList<>();
                        for (AnimalLite animal : mRemainingAnimals) {
                            String animalName = animal.getName();
                            String animalSku = animal.getSku();
                            String category = animal.getCategory();
                            if (animalName.contains(constraint) || animalSku.contains(constraint)
                                    || category.contains(constraint)) {
                                filteredAnimalList.add(animal);
                            }
                        }
                        filterResults.values = filteredAnimalList;
                        filterResults.count = filteredAnimalList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    ArrayList<AnimalLite> filteredAnimalList = (ArrayList<AnimalLite>) results.values;
                    submitList(filteredAnimalList);
                }
            };
        }

        void submitData(ArrayList<AnimalLite> remainingAnimals,
                        @Nullable ArrayList<AnimalLite> selectedAnimals) {
            if (selectedAnimals != null) {
                mSelectedAnimals.clear();
                mSelectedAnimals.addAll(selectedAnimals);
            }
            mRemainingAnimals = remainingAnimals;
            submitList(mRemainingAnimals);
        }

        ArrayList<AnimalLite> getRemainingAnimals() {
            return mRemainingAnimals;
        }

        ArrayList<AnimalLite> getSelectedAnimals() {
            return mSelectedAnimals;
        }

        private void updateSelectList(int position, AnimalLite animalLite) {
            if (mSelectedAnimals.contains(animalLite)) {
                mSelectedAnimals.remove(animalLite);
                Bundle payloadBundle = new Bundle(1);
                payloadBundle.putInt(PAYLOAD_UNSELECTED_ANIMALS, position);
                notifyItemChanged(position, payloadBundle);
            } else {
                mSelectedAnimals.add(animalLite);
                Bundle payloadBundle = new Bundle(1);
                payloadBundle.putInt(PAYLOAD_SELECTED_ANIMALS, position);
                notifyItemChanged(position, payloadBundle);
            }
            mActionsListener.onItemClicked(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mTextViewAnimalName;
            private ImageView mImageViewAnimalPhoto;
            private TextView mTextViewAnimalSku;
            private TextView mTextViewAnimalCategory;
            private ImageView mImageViewSelect;

            ViewHolder(View itemView) {
                super(itemView);
                mTextViewAnimalName = itemView.findViewById(R.id.text_animal_item_name);
                mImageViewAnimalPhoto = itemView.findViewById(R.id.image_animal_item_photo);
                mTextViewAnimalSku = itemView.findViewById(R.id.text_animal_item_sku);
                mTextViewAnimalCategory = itemView.findViewById(R.id.text_animal_item_category);
                mImageViewSelect = itemView.findViewById(R.id.image_rescuer_animal_picker_item_select);
                itemView.setOnClickListener(this);
            }

            void bind(int position, AnimalLite animalLite) {
                mTextViewAnimalName.setText(animalLite.getName());
                mTextViewAnimalSku.setText(animalLite.getSku());
                mTextViewAnimalSku.setTypeface(mAnimalSkuTypeface);
                ImageDownloaderFragment.newInstance(
                        ((FragmentActivity) mImageViewAnimalPhoto.getContext()).getSupportFragmentManager(), position)
                        .executeAndUpdate(mImageViewAnimalPhoto, animalLite.getDefaultImageUri(), position);
                mTextViewAnimalCategory.setText(animalLite.getCategory());
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AnimalLite animalLite = getItem(adapterPosition);
                    int clickedViewId = view.getId();
                    if (clickedViewId == itemView.getId()) {
                        updateSelectList(adapterPosition, animalLite);
                    }
                }
            }

            public void setSelected(boolean selected) {
                mImageViewSelect.setSelected(selected);
            }
        }
    }

    private class UserActionsListener implements RescuerAnimalPickerListUserActionsListener {
        @Override
        public void onItemClicked(int itemPosition) {
            mPresenter.updateSelectedAnimalCount(mAnimalListAdapter.getSelectedAnimals().size());
        }
    }
}