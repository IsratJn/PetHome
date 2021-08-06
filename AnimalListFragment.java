package com.example.pethome.storeapp.ui.animals;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.ui.common.ListItemSpacingDecoration;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.utils.ColorUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
import java.util.ArrayList;
public class AnimalListFragment extends Fragment
        implements AnimalListContract.View, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = AnimalListFragment.class.getSimpleName();
    private AnimalListContract.Presenter mPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewContentList;
    private Group mGroupEmptyList;
    private AnimalListAdapter mAdapter;
    public AnimalListFragment() {
    }
    @NonNull
    public static AnimalListFragment newInstance() {
        return new AnimalListFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_main_content_page, container, false);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_content_page);
        mRecyclerViewContentList = rootView.findViewById(R.id.recyclerview_content_page);
        TextView textViewEmptyList = rootView.findViewById(R.id.text_content_page_empty_list);
        ImageView imageViewStepNumber = rootView.findViewById(R.id.image_content_page_step_number);
        mGroupEmptyList = rootView.findViewById(R.id.group_content_page_empty);
        imageViewStepNumber.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_main_animal_page_number));
        textViewEmptyList.setText("Tap "+" button to add an animal");
        setupSwipeRefresh();
        setupRecyclerView();
        return rootView;
    }
    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(ColorUtility.obtainColorsFromTypedArray(requireContext(), R.array.swipe_refresh_colors, R.color.colorPrimary));
    }
    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
                if (getChildCount() > 0 && itemCount == 1) {
                    if (positionStart == getItemCount() - 1 && getItemCount() > 1) {
                        removeView(findViewByPosition(positionStart - 1));
                    }
                }
            }
            @Override
            public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
                if (getChildCount() > 0 && itemCount == 1) {
                    if (positionStart == getItemCount() && getItemCount() > 1) {
                        removeView(findViewByPosition(positionStart - 1));
                    }
                }
            }
        };
        mRecyclerViewContentList.setLayoutManager(linearLayoutManager);
        mAdapter = new AnimalListAdapter(requireContext(), new UserActionsListener());
        mRecyclerViewContentList.setAdapter(mAdapter);
        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.animal_list_items_spacing);
        mRecyclerViewContentList.addItemDecoration(new ListItemSpacingDecoration(
                itemSpacing, itemSpacing, true
        ));
    }
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
    @Nullable
    @Override
    public AnimalListContract.Presenter getPresenter() {
        return mPresenter;
    }
    @Override
    public void setPresenter(AnimalListContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void showProgressIndicator() {
        if (!mSwipeRefreshLayout.isEnabled()) {
            mSwipeRefreshLayout.setEnabled(true);
        }
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
    @Override
    public void hideProgressIndicator() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void loadAnimals(ArrayList<AnimalLite> animalList) {
        mAdapter.submitList(animalList);
    }
    @Override
    public void launchEditAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat) {
        Intent animalConfigIntent = new Intent(requireContext(), AnimalConfigActivity.class);
        animalConfigIntent.putExtra(AnimalConfigActivity.EXTRA_ANIMALS_ID, animalId);
        startActivityForResult(animalConfigIntent, AnimalConfigActivity.REQUEST_EDIT_ANIMALS, activityOptionsCompat.toBundle());
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
    public void showAddSuccess(String animalSku) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.animal_list_item_add_success, animalSku), Snackbar.LENGTH_LONG).show();
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
    public void showEmptyView() {
        mRecyclerViewContentList.setVisibility(View.INVISIBLE);
        mGroupEmptyList.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(false);
    }
    @Override
    public void hideEmptyView() {
        mRecyclerViewContentList.setVisibility(View.VISIBLE);
        mGroupEmptyList.setVisibility(View.GONE);
    }
    @Override
    public void launchAddNewAnimal() {
        Intent animalConfigIntent = new Intent(requireContext(), AnimalConfigActivity.class);
        startActivityForResult(animalConfigIntent, AnimalConfigActivity.REQUEST_ADD_ANIMALS);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRefresh() {
        mPresenter.triggerAnimalsLoad(true);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.releaseResources();
    }
    private static class AnimalListAdapter extends ListAdapter<AnimalLite, AnimalListAdapter.ViewHolder> {
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
        private AnimalListUserActionsListener mActionsListener;
        AnimalListAdapter(Context context, AnimalListUserActionsListener userActionsListener) {
            super(DIFF_ANIMALSS);
            mActionsListener = userActionsListener;
            mAnimalSkuTypeface = ResourcesCompat.getFont(context, R.font.libre_barcode_128_text_regular);
        }
        @NonNull
        @Override
        public AnimalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal_list, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull AnimalListAdapter.ViewHolder holder, int position) {
            AnimalLite animalLite = getItem(position);
            holder.bind(position, animalLite);
        }
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mTextViewAnimalName;
            private ImageView mImageViewAnimalPhoto;
            private TextView mTextViewAnimalSku;
            private TextView mTextViewAnimalCategory;
            private Button mButtonDelete;
            private Button mButtonEdit;
            ViewHolder(View itemView) {
                super(itemView);
                mTextViewAnimalName = itemView.findViewById(R.id.text_animal_item_name);
                mImageViewAnimalPhoto = itemView.findViewById(R.id.image_animal_item_photo);
                mTextViewAnimalSku = itemView.findViewById(R.id.text_animal_item_sku);
                mTextViewAnimalCategory = itemView.findViewById(R.id.text_animal_item_category);
                mButtonDelete = itemView.findViewById(R.id.btn_animal_list_item_delete);
                mButtonEdit = itemView.findViewById(R.id.btn_animal_list_item_edit);
                mButtonDelete.setOnClickListener(this);
                mButtonEdit.setOnClickListener(this);
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
                    if (clickedViewId == itemView.getId()
                            || clickedViewId == R.id.btn_animal_list_item_edit) {
                        mActionsListener.onEditAnimal(adapterPosition, animalLite, mImageViewAnimalPhoto);
                    } else if (clickedViewId == R.id.btn_animal_list_item_delete) {
                        mActionsListener.onDeleteAnimal(adapterPosition, animalLite);
                    }
                }
            }
        }
    }
    private class UserActionsListener implements AnimalListUserActionsListener {
        @Override
        public void onEditAnimal(final int itemPosition, AnimalLite animal, ImageView imageViewAnimalPhoto) {
            ActivityOptionsCompat activityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                            imageViewAnimalPhoto,
                            TextUtils.isEmpty(animal.getDefaultImageUri()) ? getString(R.string.transition_name_animal_photo) : animal.getDefaultImageUri()
                    );
            mPresenter.editAnimal(animal.getId(), activityOptionsCompat);
        }
        @Override
        public void onDeleteAnimal(final int itemPosition, AnimalLite animal) {
            mPresenter.deleteAnimal(animal);
        }
    }
}
