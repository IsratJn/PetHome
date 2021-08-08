package com.example.pethome.storeapp.ui.animals.image;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.ui.common.ProgressDialogFragment;
import com.example.pethome.storeapp.utils.FileStorageUtility;
import com.example.pethome.storeapp.utils.ImageStorageUtility;
import com.example.pethome.storeapp.utils.OrientationUtility;
import com.example.pethome.storeapp.utils.SnackbarUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class AnimalImageActivityFragment extends Fragment
        implements AnimalImageContract.View, AnimalImagePickerDialogFragment.ImagePickerOptionListener {
    public static final String ARGUMENT_LIST_ANIMALS_IMAGES = "argument.ANIMALS_IMAGES";
    private static final String LOG_TAG = AnimalImageActivityFragment.class.getSimpleName();
    private static final String BUNDLE_CAPTURE_IMAGE_URI_KEY = "AnimalImage.TempCaptureImageUri";
    private static final String BUNDLE_ANIMALS_IMAGES_KEY = "AnimalImage.Images";
    private static final String BUNDLE_SELECTION_TRACKERS_KEY = "AnimalImage.ImageSelectionTrackers";
    private AnimalImageContract.Presenter mPresenter;
    private RecyclerView mRecyclerViewPhotoGrid;
    private TextView mTextViewEmptyGrid;
    private AnimalImagePhotosGridAdapter mPhotosGridAdapter;
    private ArrayList<AnimalImage> mAnimalImages;
    private Uri mTempCaptureImageUri;
    private ArrayList<ImageSelectionTracker> mImageSelectionTrackers;
    private DialogInterface.OnClickListener mUnsavedDialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    mPresenter.onSelectAction();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    OrientationUtility.unlockScreenOrientation(requireActivity());
                    mPresenter.onIgnoreAction();
                    break;
            }
        }
    };
    public AnimalImageActivityFragment() {
    }
    public static AnimalImageActivityFragment newInstance(ArrayList<AnimalImage> animalImages) {
        Bundle args = new Bundle(1);
        args.putParcelableArrayList(ARGUMENT_LIST_ANIMALS_IMAGES, animalImages);
        AnimalImageActivityFragment fragment = new AnimalImageActivityFragment();
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
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_animal_image, container, false);
        mRecyclerViewPhotoGrid = rootView.findViewById(R.id.recyclerview_animal_image_photo_grid);
        mTextViewEmptyGrid = rootView.findViewById(R.id.text_animal_image_empty_grid);
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mAnimalImages = arguments.getParcelableArrayList(ARGUMENT_LIST_ANIMALS_IMAGES);
            }
        } else {
            mAnimalImages = savedInstanceState.getParcelableArrayList(BUNDLE_ANIMALS_IMAGES_KEY);
        }
        setupPhotoGridRecyclerView();
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mTempCaptureImageUri = savedInstanceState.getParcelable(BUNDLE_CAPTURE_IMAGE_URI_KEY);
            mPresenter.restoreSelectionTrackers(savedInstanceState.getParcelableArrayList(BUNDLE_SELECTION_TRACKERS_KEY));
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_CAPTURE_IMAGE_URI_KEY, mTempCaptureImageUri);
        outState.putParcelableArrayList(BUNDLE_SELECTION_TRACKERS_KEY, mImageSelectionTrackers);
        outState.putParcelableArrayList(BUNDLE_ANIMALS_IMAGES_KEY, mAnimalImages);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_animal_image, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select:
                mPresenter.onSelectAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void setupPhotoGridRecyclerView() {
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.animal_image_grid_span_count),
                GridLayoutManager.VERTICAL,
                false
        );
        mRecyclerViewPhotoGrid.setLayoutManager(gridLayoutManager);
        mPhotosGridAdapter = new AnimalImagePhotosGridAdapter(new UserActionsListener());
        mRecyclerViewPhotoGrid.setAdapter(mPhotosGridAdapter);
        mPresenter.restoreAnimalImages(mAnimalImages);
        mRecyclerViewPhotoGrid.addItemDecoration(new GridSpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.image_item_photo_grid_spacing)));
    }
    @Override
    public void setPresenter(AnimalImageContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public void showImagePickerDialog() {
        AnimalImagePickerDialogFragment.showDialog(getChildFragmentManager());
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
    public void onImageCaptured() {
        mPresenter.saveImageToFile(requireContext(), mTempCaptureImageUri);
    }
    @Override
    public void updateGridItemsState(ArrayList<ImageSelectionTracker> imageSelectionTrackers) {
        mPhotosGridAdapter.refreshItemsState(imageSelectionTrackers);
    }
    @Override
    public void showDeleteSuccess() {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.animal_image_delete_success, Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void submitListToAdapter(ArrayList<AnimalImage> animalImages) {
        mAnimalImages = animalImages;
        mPhotosGridAdapter.submitList(animalImages);
    }
    @Override
    public void syncSelectionTrackers(ArrayList<ImageSelectionTracker> imageSelectionTrackers) {
        mImageSelectionTrackers = imageSelectionTrackers;
    }
    @Override
    public void showGridView() {
        mRecyclerViewPhotoGrid.setVisibility(View.VISIBLE);
        mTextViewEmptyGrid.setVisibility(View.GONE);
    }
    @Override
    public void hideGridView() {
        mRecyclerViewPhotoGrid.setVisibility(View.INVISIBLE);
        mTextViewEmptyGrid.setVisibility(View.VISIBLE);
    }
    @Override
    public void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(R.string.animal_image_unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.animal_image_unsaved_changes_dialog_positive_text, mUnsavedDialogOnClickListener);
        builder.setNegativeButton(R.string.animal_image_unsaved_changes_dialog_negative_text, mUnsavedDialogOnClickListener);
        OrientationUtility.lockCurrentScreenOrientation(requireActivity());
        builder.create().show();
    }
    @Override
    public void showImageAlreadyPicked() {
        if (getView() != null) {
            Snackbar.make(getView(), R.string.animal_image_already_picked_message, Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onTakeNewPhoto() {
        if (requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            dispatchTakePictureIntent();
        } else {
            showError(R.string.animal_image_no_camera_hardware_error);
        }
    }
    @Override
    public void onPickPhotoFromGallery() {
        Intent pickerIntent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            pickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            pickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        pickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickerIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            pickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(pickerIntent, getString(R.string.animal_image_picker_chooser_title)), AnimalImageContract.REQUEST_IMAGE_PICK);
    }
    private void dispatchTakePictureIntent() {
        Intent capturePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (capturePictureIntent.resolveActivity(requireContext().getPackageManager()) != null
                && FileStorageUtility.isExternalStorageMounted()) {
            File tempPhotoFile = null;
            try {
                tempPhotoFile = ImageStorageUtility.createTempImageFile(requireContext());
            } catch (IOException e) {
                Log.e(LOG_TAG, "dispatchTakePictureIntent: Error occurred while creating a temp file ", e);
            }
            if (tempPhotoFile != null && tempPhotoFile.exists()) {
                mTempCaptureImageUri = ImageStorageUtility.getContentUriForImageFile(requireContext(), tempPhotoFile);
                capturePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTempCaptureImageUri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                        && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    capturePictureIntent.setClipData(ClipData.newRawUri("", mTempCaptureImageUri));
                    capturePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resolveInfoList = requireContext().getPackageManager().queryIntentActivities(capturePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resolveInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        requireContext().grantUriPermission(packageName, mTempCaptureImageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                }
                startActivityForResult(capturePictureIntent, AnimalImageContract.REQUEST_IMAGE_CAPTURE);
            }
        } else if (!FileStorageUtility.isExternalStorageMounted()) {
            showError(R.string.animal_image_disk_not_mounted_save_error);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
    private static class AnimalImagePhotosGridAdapter extends ListAdapter<AnimalImage, AnimalImagePhotosGridAdapter.ViewHolder> {
        private static final String PAYLOAD_TRACKER = "Payload.SelectionTracker";
        private static DiffUtil.ItemCallback<AnimalImage> DIFF_IMAGES
                = new DiffUtil.ItemCallback<AnimalImage>() {
            @Override
            public boolean areItemsTheSame(AnimalImage oldItem, AnimalImage newItem) {
                return oldItem.getImageUri().equals(newItem.getImageUri());
            }
            @Override
            public boolean areContentsTheSame(AnimalImage oldItem, AnimalImage newItem) {
                return oldItem.isDefault() == newItem.isDefault();
            }
        };
        private PhotoGridUserActionsListener mActionsListener;
        @AnimalImageContract.PhotoGridSelectModeDef
        private String mGridMode;
        private SparseArray<ImageSelectionTracker> mImageSelectionTrackerMap;
        AnimalImagePhotosGridAdapter(PhotoGridUserActionsListener userActionsListener) {
            super(DIFF_IMAGES);
            mActionsListener = userActionsListener;
        }
        @NonNull
        @Override
        public AnimalImagePhotosGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal_image, parent, false);
            return new ViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull AnimalImagePhotosGridAdapter.ViewHolder holder, int position) {
            AnimalImage itemAnimalImage = getItem(position);
            Context context = holder.itemView.getContext();
            ImageDownloaderFragment.newInstance(((FragmentActivity) context).getSupportFragmentManager(), position)
                    .setOnSuccessListener(bitmap -> {
                        if (mImageSelectionTrackerMap != null && mImageSelectionTrackerMap.size() > 0 && mGridMode.equals(AnimalImageContract.MODE_SELECT)) {
                            ImageSelectionTracker currentItemTracker = mImageSelectionTrackerMap.get(position);
                            if (currentItemTracker != null && currentItemTracker.getPhotoGridMode().equals(mGridMode)
                                    && currentItemTracker.isSelected()) {
                                mActionsListener.showSelectedImage(bitmap, itemAnimalImage);
                            }
                        }
                    })
                    .executeAndUpdate(holder.mImageViewItemPhoto,
                            itemAnimalImage.getImageUri(),
                            position);
            if (mImageSelectionTrackerMap != null && mImageSelectionTrackerMap.size() > 0) {
                ImageSelectionTracker currentItemTracker = mImageSelectionTrackerMap.get(position);
                if (currentItemTracker != null) {
                    holder.updateItemSelectionState(currentItemTracker.getPhotoGridMode(), currentItemTracker.isSelected());
                }
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
                        case PAYLOAD_TRACKER:
                            ImageSelectionTracker currentItemTracker = getLatestSelectionTracker(bundle.getParcelable(keyStr));
                            if (currentItemTracker != null && currentItemTracker.getPosition() == position) {
                                if (mGridMode.equals(AnimalImageContract.MODE_SELECT)) {
                                    AnimalImage itemAnimalImage = getItem(position);
                                    Context context = holder.itemView.getContext();
                                    ImageView imageViewItemPhoto = holder.mImageViewItemPhoto;
                                    if (currentItemTracker.getPhotoGridMode().equals(mGridMode)
                                            && currentItemTracker.isSelected()) {
                                        if (imageViewItemPhoto.getDrawable() instanceof BitmapDrawable) {
                                            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageViewItemPhoto.getDrawable();
                                            mActionsListener.showSelectedImage(bitmapDrawable.getBitmap(), itemAnimalImage);
                                        } else {
                                            ImageDownloaderFragment.newInstance(((FragmentActivity) context).getSupportFragmentManager(), position)
                                                    .setOnSuccessListener(bitmap -> {
                                                        mActionsListener.showSelectedImage(bitmap, itemAnimalImage);
                                                    })
                                                    .executeAndUpdate(imageViewItemPhoto,
                                                            itemAnimalImage.getImageUri(),
                                                            position);
                                        }
                                    }
                                }
                                holder.updateItemSelectionState(currentItemTracker.getPhotoGridMode(), currentItemTracker.isSelected());
                            }
                            break;
                    }
                }
            }
        }
        private ImageSelectionTracker getLatestSelectionTracker(ImageSelectionTracker imageSelectionTracker) {
            if (mImageSelectionTrackerMap != null && mImageSelectionTrackerMap.size() > 0) {
                ImageSelectionTracker latestSelectionTracker = mImageSelectionTrackerMap.get(imageSelectionTracker.getPosition());
                if (latestSelectionTracker != null) {
                    return latestSelectionTracker;
                }
            }
            return imageSelectionTracker;
        }
        void refreshItemsState(List<ImageSelectionTracker> imageSelectionTrackers) {
            buildSparseArrayOfTrackers(imageSelectionTrackers);
            int noOfItemsMarkedForSelect = 0;
            int noOfItemsMarkedForDelete = 0;
            for (ImageSelectionTracker selectionTracker : imageSelectionTrackers) {
                if (selectionTracker.getPhotoGridMode().equals(AnimalImageContract.MODE_SELECT)) {
                    noOfItemsMarkedForSelect++;
                } else if (selectionTracker.getPhotoGridMode().equals(AnimalImageContract.MODE_DELETE)) {
                    noOfItemsMarkedForDelete++;
                }
            }
            if (noOfItemsMarkedForDelete + noOfItemsMarkedForSelect == 0) {
                mGridMode = "";
            } else if (noOfItemsMarkedForDelete > noOfItemsMarkedForSelect) {
                mGridMode = AnimalImageContract.MODE_DELETE;
            } else {
                mGridMode = AnimalImageContract.MODE_SELECT;
            }
            for (ImageSelectionTracker selectionTracker : imageSelectionTrackers) {
                if (selectionTracker.getPhotoGridMode().equals(mGridMode)) {
                    Bundle payloadBundle = new Bundle(1);
                    payloadBundle.putParcelable(PAYLOAD_TRACKER, selectionTracker);
                    notifyItemChanged(selectionTracker.getPosition(), payloadBundle);
                }
            }
        }
        private void buildSparseArrayOfTrackers(List<ImageSelectionTracker> imageSelectionTrackers) {
            if (mImageSelectionTrackerMap != null && mImageSelectionTrackerMap.size() > 0) {
                mImageSelectionTrackerMap.clear();
            } else {
                mImageSelectionTrackerMap = new SparseArray<>();
            }
            for (ImageSelectionTracker selectionTracker : imageSelectionTrackers) {
                mImageSelectionTrackerMap.append(selectionTracker.getPosition(), selectionTracker);
            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {
            private final ImageView mImageViewItemPhoto;
            private final Group mGroupItemPhotoSelect;
            private final Group mGroupItemPhotoDelete;
            ViewHolder(View itemView) {
                super(itemView);
                mImageViewItemPhoto = itemView.findViewById(R.id.image_item_animal_photo);
                mGroupItemPhotoSelect = itemView.findViewById(R.id.group_item_animal_photo_select);
                mGroupItemPhotoDelete = itemView.findViewById(R.id.group_item_animal_photo_delete);
                mImageViewItemPhoto.setOnClickListener(this);
                mImageViewItemPhoto.setOnLongClickListener(this);
            }
            @Override
            public void onClick(View view) {
                int itemPosition = getAdapterPosition();
                if (itemPosition > RecyclerView.NO_POSITION) {
                    AnimalImage animalImage = getItem(itemPosition);
                    switch (view.getId()) {
                        case R.id.image_item_animal_photo:
                            if (TextUtils.isEmpty(mGridMode)) {
                                mGridMode = AnimalImageContract.MODE_SELECT;
                            }
                            mActionsListener.onItemClicked(itemPosition,
                                    animalImage,
                                    mGridMode);
                            break;
                    }
                }
            }
            @Override
            public boolean onLongClick(View view) {
                int itemPosition = getAdapterPosition();
                if (itemPosition > RecyclerView.NO_POSITION) {
                    AnimalImage animalImage = getItem(itemPosition);
                    switch (view.getId()) {
                        case R.id.image_item_animal_photo:
                            if (TextUtils.isEmpty(mGridMode)
                                    || !mGridMode.equals(AnimalImageContract.MODE_DELETE)) {
                                mGridMode = AnimalImageContract.MODE_DELETE;
                            }
                            mActionsListener.onItemLongClicked(itemPosition,
                                    animalImage,
                                    mGridMode);
                            return true;
                    }
                }
                return false;
            }
            void updateItemSelectionState(@AnimalImageContract.PhotoGridSelectModeDef String photoGridMode, boolean isSelected) {
                if (photoGridMode.equals(AnimalImageContract.MODE_SELECT)) {
                    mGroupItemPhotoDelete.setVisibility(View.INVISIBLE);
                    if (isSelected) {
                        mGroupItemPhotoSelect.setVisibility(View.VISIBLE);
                    } else {
                        mGroupItemPhotoSelect.setVisibility(View.INVISIBLE);
                    }
                } else if (photoGridMode.equals(AnimalImageContract.MODE_DELETE)) {
                    mGroupItemPhotoSelect.setVisibility(View.INVISIBLE);
                    if (isSelected) {
                        mGroupItemPhotoDelete.setVisibility(View.VISIBLE);
                    } else {
                        mGroupItemPhotoDelete.setVisibility(View.INVISIBLE);
                    }
                }
                mGroupItemPhotoSelect.requestLayout();
                mGroupItemPhotoDelete.requestLayout();
            }
        }
    }
    private static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int mOffsetSize;
        GridSpacingItemDecoration(int offsetSize) {
            mOffsetSize = offsetSize;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;
            boolean isFirstColumn = (column == 0);
            boolean isLastColumn = (column + 1 == spanCount);
            boolean isFirstRow = (position < spanCount);
            if (isFirstColumn) {
                outRect.left = mOffsetSize;
            } else {
                outRect.left = mOffsetSize / 2;
            }
            if (isLastColumn) {
                outRect.right = mOffsetSize;
            } else {
                outRect.right = mOffsetSize / 2;
            }
            if (isFirstRow) {
                outRect.top = mOffsetSize;
            } else {
                outRect.top = 0;
            }
            outRect.bottom = mOffsetSize;
        }
    }
    private class UserActionsListener implements PhotoGridUserActionsListener {
        @Override
        public void onItemClicked(int itemPosition, AnimalImage animalImage, @AnimalImageContract.PhotoGridSelectModeDef String gridMode) {
            mPresenter.onItemImageClicked(itemPosition, animalImage, gridMode);
        }
        @Override
        public void onItemLongClicked(int itemPosition, AnimalImage animalImage, @AnimalImageContract.PhotoGridSelectModeDef String gridMode) {
            mPresenter.onItemImageLongClicked(itemPosition, animalImage, gridMode);
        }
        @Override
        public void showSelectedImage(Bitmap bitmap, AnimalImage animalImage) {
            mPresenter.showSelectedImage(bitmap, animalImage.getImageUri());
            mPresenter.syncLastChosenAnimalImage(animalImage);
        }
    }
}
