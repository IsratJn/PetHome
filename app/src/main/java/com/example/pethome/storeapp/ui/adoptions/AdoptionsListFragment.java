package com.example.pethome.storeapp.ui.adoptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.support.v4.content.ContextCompat;
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
import com.example.pethome.storeapp.data.local.models.AdoptionsLite;
import com.example.pethome.storeapp.ui.common.ListItemSpacingDecoration;
import com.example.pethome.storeapp.ui.adoptions.config.AdoptionsConfigActivity;
import com.example.pethome.storeapp.utils.ColorUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import com.example.pethome.storeapp.utils.TextAppearanceUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
public class AdoptionsListFragment extends Fragment
        implements AdoptionsListContract.View, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = AdoptionsListFragment.class.getSimpleName();
    private AdoptionsListContract.Presenter mPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewContentList;
    private Group mGroupEmptyList;
    private AdoptionsListAdapter mAdapter;
    public AdoptionsListFragment() {
    }
    public static AdoptionsListFragment newInstance() {
        return new AdoptionsListFragment();
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
        imageViewStepNumber.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_main_adoptions_page_number));
        textViewEmptyList.setText("Add Animals and Rescuers first");
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
                    int positionLast = getItemCount() - 1;
                    if (positionLast > positionStart) {
                        for (int index = positionStart; index <= positionLast; index++) {
                            removeView(findViewByPosition(index));
                        }
                        recyclerView.smoothScrollToPosition(positionStart);
                    }
                }
            }
        };
        mRecyclerViewContentList.setLayoutManager(linearLayoutManager);
        mAdapter = new AdoptionsListAdapter(requireContext(), new UserActionsListener());
        mRecyclerViewContentList.setAdapter(mAdapter);
        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.adoptions_list_items_spacing);
        mRecyclerViewContentList.addItemDecoration(new ListItemSpacingDecoration(
                itemSpacing, itemSpacing
        ));
    }
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.releaseResources();
    }
    @Nullable
    @Override
    public AdoptionsListContract.Presenter getPresenter() {
        return mPresenter;
    }
    @Override
    public void setPresenter(AdoptionsListContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void onRefresh() {
        mPresenter.triggerAnimalAdoptionsLoad(true);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
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
    public void loadAdoptionsList(ArrayList<AdoptionsLite> adoptionsList) {
        mAdapter.submitList(adoptionsList);
    }
    @Override
    public void showDeleteSuccess(String animalSku) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.animal_list_item_delete_success, animalSku), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showSellQuantitySuccess(String animalSku, String rescuerCode) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.adoptions_list_item_sell_success, animalSku, rescuerCode), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showUpdateInventorySuccess(String animalSku) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.adoptions_list_item_update_inventory_success, animalSku), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void launchEditAnimalAdoptions(int animalId, ActivityOptionsCompat activityOptionsCompat) {
        Intent adoptionsConfigIntent = new Intent(requireContext(), AdoptionsConfigActivity.class);
        adoptionsConfigIntent.putExtra(AdoptionsConfigActivity.EXTRA_ANIMALS_ID, animalId);
        startActivityForResult(adoptionsConfigIntent, AdoptionsConfigActivity.REQUEST_EDIT_ADOPTIONS, activityOptionsCompat.toBundle());
    }
    private static class AdoptionsListAdapter extends ListAdapter<AdoptionsLite, AdoptionsListAdapter.ViewHolder> {
        private static DiffUtil.ItemCallback<AdoptionsLite> DIFF_ADOPTIONS
                = new DiffUtil.ItemCallback<AdoptionsLite>() {
            @Override
            public boolean areItemsTheSame(AdoptionsLite oldItem, AdoptionsLite newItem) {
                return oldItem.getAnimalId() == newItem.getAnimalId()
                        && oldItem.getRescuerId() == newItem.getRescuerId();
            }
            @Override
            public boolean areContentsTheSame(AdoptionsLite oldItem, AdoptionsLite newItem) {
                return oldItem.equals(newItem);
            }
        };
        private Typeface mAnimalSkuTypeface;
        private AdoptionsListUserActionsListener mActionsListener;
        AdoptionsListAdapter(Context context, AdoptionsListUserActionsListener userActionsListener) {
            super(DIFF_ADOPTIONS);
            mActionsListener = userActionsListener;
            mAnimalSkuTypeface = ResourcesCompat.getFont(context, R.font.libre_barcode_128_text_regular);
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adoptions_list, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AdoptionsLite adoptionsLite = getItem(position);
            holder.bind(position, adoptionsLite);
        }
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mTextViewAnimalName;
            private ImageView mImageViewAnimalPhoto;
            private TextView mTextViewAnimalSku;
            private TextView mTextViewAnimalCategory;
            private TextView mTextViewTotalAvailable;
            private TextView mTextViewRescuerNameCode;
            private TextView mTextViewRescuerPrice;
            private TextView mTextViewRescuerAvailability;
            private Button mButtonDeleteAnimal;
            private Button mButtonSell;
            private Group mGroupTopRescuer;
            private Typeface mTotalAvailableTypeface;
            ViewHolder(View itemView) {
                super(itemView);
                mTextViewAnimalName = itemView.findViewById(R.id.text_animal_item_name);
                mImageViewAnimalPhoto = itemView.findViewById(R.id.image_animal_item_photo);
                mTextViewAnimalSku = itemView.findViewById(R.id.text_animal_item_sku);
                mTextViewAnimalCategory = itemView.findViewById(R.id.text_animal_item_category);
                mTextViewTotalAvailable = itemView.findViewById(R.id.text_adoptions_list_item_total_available);
                mTextViewRescuerNameCode = itemView.findViewById(R.id.text_adoptions_list_item_rescuer_name_code);
                mTextViewRescuerPrice = itemView.findViewById(R.id.text_adoptions_list_item_rescuer_price);
                mTextViewRescuerAvailability = itemView.findViewById(R.id.text_adoptions_list_item_rescuer_availability);
                mButtonDeleteAnimal = itemView.findViewById(R.id.btn_adoptions_list_item_delete);
                mButtonSell = itemView.findViewById(R.id.btn_adoptions_list_item_sell);
                mGroupTopRescuer = itemView.findViewById(R.id.group_adoptions_list_item_top_rescuer);
                mTotalAvailableTypeface = mTextViewTotalAvailable.getTypeface();
                mButtonDeleteAnimal.setOnClickListener(this);
                mButtonSell.setOnClickListener(this);
                itemView.setOnClickListener(this);
            }
            void bind(int position, AdoptionsLite adoptionsLite) {
                mTextViewAnimalName.setText(adoptionsLite.getAnimalName());
                mTextViewAnimalSku.setText(adoptionsLite.getAnimalSku());
                mTextViewAnimalSku.setTypeface(mAnimalSkuTypeface);
                ImageDownloaderFragment.newInstance(
                        ((FragmentActivity) mImageViewAnimalPhoto.getContext()).getSupportFragmentManager(), position)
                        .executeAndUpdate(mImageViewAnimalPhoto, adoptionsLite.getDefaultImageUri(), position);
                mTextViewAnimalCategory.setText(adoptionsLite.getCategoryName());
                Resources resources = itemView.getContext().getResources();
                int totalAvailableQuantity = adoptionsLite.getTotalAvailableQuantity();
                if (totalAvailableQuantity > 0) {
                    TextAppearanceUtility.setHtmlText(mTextViewTotalAvailable, resources.getString(R.string.adoptions_list_item_total_available, totalAvailableQuantity));
                    mTextViewTotalAvailable.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.adoptionsListItemTotalAvailableColor));
                    mTextViewTotalAvailable.setTypeface(mTotalAvailableTypeface);
                    mTextViewTotalAvailable.setAllCaps(false);
                    mGroupTopRescuer.setVisibility(View.VISIBLE);
                    mTextViewRescuerNameCode.setText(resources.getString(R.string.adoptions_list_item_rescuer_name_code_format, adoptionsLite.getTopRescuerName(), adoptionsLite.getTopRescuerCode()));
                    mTextViewRescuerPrice.setText(resources.getString(R.string.adoptions_list_item_rescuer_rescuing_price,
                            Currency.getInstance(Locale.getDefault()).getSymbol() + " " + adoptionsLite.getRescuerUnitPrice()));
                    mTextViewRescuerAvailability.setText(String.valueOf(adoptionsLite.getRescuerAvailableQuantity()));
                } else {
                    mTextViewTotalAvailable.setText(resources.getString(R.string.adoptions_list_item_out_of_stock));
                    mTextViewTotalAvailable.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.adoptionsListItemOutOfStockColor));
                    mTextViewTotalAvailable.setTypeface(mTotalAvailableTypeface, Typeface.BOLD);
                    mTextViewTotalAvailable.setAllCaps(true);
                    mGroupTopRescuer.setVisibility(View.GONE);
                }
            }
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    AdoptionsLite adoptionsLite = getItem(adapterPosition);
                    int clickedViewId = view.getId();
                    if (clickedViewId == itemView.getId()) {
                        mActionsListener.onEditAdoptions(adapterPosition, adoptionsLite, mImageViewAnimalPhoto);
                    } else if (clickedViewId == mButtonDeleteAnimal.getId()) {
                        mActionsListener.onDeleteAnimal(adapterPosition, adoptionsLite);
                    } else if (clickedViewId == mButtonSell.getId()) {
                        mActionsListener.onSellOneQuantity(adapterPosition, adoptionsLite);
                    }
                }
            }
        }
    }
    private class UserActionsListener implements AdoptionsListUserActionsListener {
        @Override
        public void onEditAdoptions(int itemPosition, AdoptionsLite adoptionsLite, ImageView imageViewAnimalPhoto) {
            ActivityOptionsCompat activityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                            imageViewAnimalPhoto,
                            TextUtils.isEmpty(adoptionsLite.getDefaultImageUri()) ? getString(R.string.transition_name_animal_photo) : adoptionsLite.getDefaultImageUri()
                    );
            mPresenter.editAnimalAdoptions(adoptionsLite.getAnimalId(), activityOptionsCompat);
        }
        @Override
        public void onDeleteAnimal(int itemPosition, AdoptionsLite adoptionsLite) {
            mPresenter.deleteAnimal(adoptionsLite.getAnimalId(), adoptionsLite.getAnimalSku());
        }
        @Override
        public void onSellOneQuantity(int itemPosition, AdoptionsLite adoptionsLite) {
            mPresenter.sellOneQuantity(adoptionsLite);
        }
    }
}
