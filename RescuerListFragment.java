package com.example.pethome.storeapp.ui.rescuers;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.RescuerLite;
import com.example.pethome.storeapp.ui.common.ListItemSpacingDecoration;
import com.example.pethome.storeapp.ui.rescuers.config.RescuerConfigActivity;
import com.example.pethome.storeapp.utils.ColorUtility;
import com.example.pethome.storeapp.utils.IntentUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import java.util.ArrayList;
import java.util.Locale;
public class RescuerListFragment extends Fragment
        implements RescuerListContract.View, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = RescuerListFragment.class.getSimpleName();
    private RescuerListContract.Presenter mPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewContentList;
    private Group mGroupEmptyList;
    private RescuerListAdapter mAdapter;
    public RescuerListFragment() {
    }
    public static RescuerListFragment newInstance() {
        return new RescuerListFragment();
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
        imageViewStepNumber.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_main_rescuer_page_number));
        textViewEmptyList.setText("Tap "+" button to add a rescuer.");
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
        mAdapter = new RescuerListAdapter(new UserActionsListener());
        mRecyclerViewContentList.setAdapter(mAdapter);
        int itemSpacing = getResources().getDimensionPixelSize(R.dimen.rescuer_list_items_spacing);
        mRecyclerViewContentList.addItemDecoration(new ListItemSpacingDecoration(
                itemSpacing, itemSpacing, true
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
    public RescuerListContract.Presenter getPresenter() {
        return mPresenter;
    }
    @Override
    public void setPresenter(RescuerListContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void onRefresh() {
        mPresenter.triggerRescuersLoad(true);
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
    public void loadRescuers(ArrayList<RescuerLite> rescuerList) {
        mAdapter.submitList(rescuerList);
    }
    @Override
    public void launchAddNewRescuer() {
        Intent rescuerConfigIntent = new Intent(requireContext(), RescuerConfigActivity.class);
        startActivityForResult(rescuerConfigIntent, RescuerConfigActivity.REQUEST_ADD_RESCUER);
    }
    @Override
    public void launchEditRescuer(int rescuerId) {
        Intent rescuerConfigIntent = new Intent(requireContext(), RescuerConfigActivity.class);
        rescuerConfigIntent.putExtra(RescuerConfigActivity.EXTRA_RESCUER_ID, rescuerId);
        startActivityForResult(rescuerConfigIntent, RescuerConfigActivity.REQUEST_EDIT_RESCUER);
    }
    @Override
    public void showAddSuccess(String rescuerCode) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.rescuer_list_item_add_success, rescuerCode), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showUpdateSuccess(String rescuerCode) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.rescuer_list_item_update_success, rescuerCode), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void showDeleteSuccess(String rescuerCode) {
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.rescuer_list_item_delete_success, rescuerCode), Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void dialPhoneNumber(String phoneNumber) {
        IntentUtility.dialPhoneNumber(requireActivity(), phoneNumber);
    }
    @Override
    public void composeEmail(String toEmailAddress) {
        IntentUtility.composeEmail(
                requireActivity(),
                new String[]{toEmailAddress},
                null,
                null,
                null
        );
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
    private static class RescuerListAdapter extends ListAdapter<RescuerLite, RescuerListAdapter.ViewHolder> {
        private static DiffUtil.ItemCallback<RescuerLite> DIFF_RESCUERS
                = new DiffUtil.ItemCallback<RescuerLite>() {
            @Override
            public boolean areItemsTheSame(RescuerLite oldItem, RescuerLite newItem) {
                return oldItem.getId() == newItem.getId();
            }
            @Override
            public boolean areContentsTheSame(RescuerLite oldItem, RescuerLite newItem) {
                return oldItem.equals(newItem);
            }
        };
        private RescuerListUserActionsListener mActionsListener;
        RescuerListAdapter(RescuerListUserActionsListener userActionsListener) {
            super(DIFF_RESCUERS);
            mActionsListener = userActionsListener;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rescuer_list, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RescuerLite rescuerLite = getItem(position);
            holder.bind(rescuerLite);
        }
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mTextViewRescuerName;
            private TextView mTextViewRescuerCode;
            private TextView mTextViewRescuerItemCount;
            private Button mButtonDefaultPhone;
            private Button mButtonDefaultEmail;
            private Button mButtonDelete;
            private Button mButtonEdit;
            ViewHolder(View itemView) {
                super(itemView);
                mTextViewRescuerName = itemView.findViewById(R.id.text_rescuer_list_item_name);
                mTextViewRescuerCode = itemView.findViewById(R.id.text_rescuer_list_item_code);
                mTextViewRescuerItemCount = itemView.findViewById(R.id.text_rescuer_list_item_animals_count);
                mButtonDefaultPhone = itemView.findViewById(R.id.btn_rescuer_list_item_default_phone);
                mButtonDefaultEmail = itemView.findViewById(R.id.btn_rescuer_list_item_default_email);
                mButtonDelete = itemView.findViewById(R.id.btn_rescuer_list_item_delete);
                mButtonEdit = itemView.findViewById(R.id.btn_rescuer_list_item_edit);
                mButtonDefaultPhone.setOnClickListener(this);
                mButtonDefaultEmail.setOnClickListener(this);
                mButtonDelete.setOnClickListener(this);
                mButtonEdit.setOnClickListener(this);
                itemView.setOnClickListener(this);
            }
            void bind(RescuerLite rescuerLite) {
                Resources resources = itemView.getContext().getResources();
                mTextViewRescuerName.setText(rescuerLite.getName());
                mTextViewRescuerCode.setText(rescuerLite.getCode());
                mTextViewRescuerItemCount.setText(resources.getString(R.string.rescuer_list_item_animals_count_desc, rescuerLite.getItemCount()));
                String defaultPhone = rescuerLite.getDefaultPhone();
                String defaultEmail = rescuerLite.getDefaultEmail();
                if (!TextUtils.isEmpty(defaultPhone)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mButtonDefaultPhone.setText(PhoneNumberUtils.formatNumber(defaultPhone, Locale.getDefault().getCountry()));
                    } else {
                        mButtonDefaultPhone.setText(PhoneNumberUtils.formatNumber(defaultPhone));
                    }
                    mButtonDefaultPhone.setVisibility(View.VISIBLE);
                } else {
                    mButtonDefaultPhone.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(defaultEmail)) {
                    mButtonDefaultEmail.setText(defaultEmail);
                    mButtonDefaultEmail.setVisibility(View.VISIBLE);
                } else {
                    mButtonDefaultEmail.setVisibility(View.GONE);
                }
            }
            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition > RecyclerView.NO_POSITION) {
                    RescuerLite rescuerLite = getItem(adapterPosition);
                    int clickedViewId = view.getId();
                    if (clickedViewId == itemView.getId()
                            || clickedViewId == R.id.btn_rescuer_list_item_edit) {
                        mActionsListener.onEditRescuer(adapterPosition, rescuerLite);
                    } else if (clickedViewId == R.id.btn_rescuer_list_item_delete) {
                        mActionsListener.onDeleteRescuer(adapterPosition, rescuerLite);
                    } else if (clickedViewId == R.id.btn_rescuer_list_item_default_phone) {
                        mActionsListener.onDefaultPhoneClicked(adapterPosition, rescuerLite);
                    } else if (clickedViewId == R.id.btn_rescuer_list_item_default_email) {
                        mActionsListener.onDefaultEmailClicked(adapterPosition, rescuerLite);
                    }
                }
            }
        }
    }
    private class UserActionsListener implements RescuerListUserActionsListener {
        @Override
        public void onEditRescuer(int itemPosition, RescuerLite rescuer) {
            mPresenter.editRescuer(rescuer.getId());
        }
        @Override
        public void onDeleteRescuer(int itemPosition, RescuerLite rescuer) {
            mPresenter.deleteRescuer(rescuer);
        }
        @Override
        public void onDefaultPhoneClicked(int itemPosition, RescuerLite rescuer) {
            mPresenter.defaultPhoneClicked(rescuer.getDefaultPhone());
        }
        @Override
        public void onDefaultEmailClicked(int itemPosition, RescuerLite rescuer) {
            mPresenter.defaultEmailClicked(rescuer.getDefaultEmail());
        }
    }
}
