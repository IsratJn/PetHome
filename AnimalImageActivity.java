package com.example.pethome.storeapp.ui.animals.image;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.utils.InjectorUtility;
import java.util.ArrayList;
public class AnimalImageActivity extends AppCompatActivity
        implements PhotoGridDeleteModeListener, AnimalImageNavigator, SelectedPhotoActionsListener {
    public static final int REQUEST_ANIMALS_IMAGE = 30;
    public static final String EXTRA_ANIMALS_IMAGES = AnimalImageActivity.class.getPackage() + "extra.ANIMALS_IMAGES";
    private AnimalImageContract.Presenter mPresenter;
    private FloatingActionButton mFabAddImages;
    private ImageView mImageViewSelectedPhoto;
    private AppBarLayout mAppBarLayout;
    private ActionMode mActionMode;
    private boolean mIsEnterTransitionPostponed;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        private boolean mDeleteEventHandled = false;
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(getString(R.string.animal_image_contextual_action_delete_title));
            mode.getMenuInflater().inflate(R.menu.cab_menu_fragment_animal_image, menu);
            mFabAddImages.hide();
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    mPresenter.deleteSelection();
                    mDeleteEventHandled = true;
                    mode.finish();
                    return mDeleteEventHandled;
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mFabAddImages.show();
            if (!mDeleteEventHandled) {
                mPresenter.onDeleteModeExit();
            } else {
                mDeleteEventHandled = false;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_image);
        supportPostponeEnterTransition();
        mAppBarLayout = findViewById(R.id.app_bar_animal_image);
        mImageViewSelectedPhoto = findViewById(R.id.image_animal_selected_item_photo);
        setupToolbar();
        setupFab();
        ArrayList<AnimalImage> animalImages = getIntent().getParcelableArrayListExtra(EXTRA_ANIMALS_IMAGES);
        if (animalImages == null) {
            animalImages = new ArrayList<>();
        }
        AnimalImageActivityFragment contentFragment = obtainContentFragment(animalImages);
        mPresenter = obtainPresenter(contentFragment);
    }
    @NonNull
    private AnimalImageContract.Presenter obtainPresenter(AnimalImageActivityFragment contentFragment) {
        return new AnimalImagePresenter(
                InjectorUtility.provideStoreRepository(this),
                contentFragment,
                this,
                this,
                this
        );
    }
    private AnimalImageActivityFragment obtainContentFragment(ArrayList<AnimalImage> animalImages) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        AnimalImageActivityFragment fragment
                = (AnimalImageActivityFragment) supportFragmentManager.findFragmentById(R.id.content_animal_image);
        if (fragment == null) {
            fragment = AnimalImageActivityFragment.newInstance(animalImages);
            supportFragmentManager.beginTransaction()
                    .add(R.id.content_animal_image, fragment)
                    .commit();
        }
        return fragment;
    }
    private void setupFab() {
        mFabAddImages = findViewById(R.id.fab_animal_image);
        mFabAddImages.setOnClickListener(view -> mPresenter.openImagePickerDialog());
        mFabAddImages.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.animalImageFabColor)));
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_animal_image);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void supportPostponeEnterTransition() {
        super.supportPostponeEnterTransition();
        mIsEnterTransitionPostponed = true;
    }
    @Override
    public void supportStartPostponedEnterTransition() {
        super.supportStartPostponedEnterTransition();
        mIsEnterTransitionPostponed = false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mPresenter.onUpOrBackAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if (mActionMode != null) {
            mActionMode.finish();
            return;
        }
        mPresenter.onUpOrBackAction();
    }
    @Override
    public void onGridItemDeleteMode() {
        if (mActionMode == null) {
            mActionMode = startSupportActionMode(mActionModeCallback);
        }
    }
    @Override
    public void showSelectedItemCount(int itemCount) {
        if (mActionMode != null) {
            mActionMode.setSubtitle(getString(R.string.animal_image_contextual_action_delete_live_count, itemCount));
        }
    }
    @Override
    public void doSetResult(ArrayList<AnimalImage> animalImages) {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra(EXTRA_ANIMALS_IMAGES, animalImages);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    @Override
    public void showDefaultImage() {
        mImageViewSelectedPhoto.setImageResource(R.drawable.ic_all_animal_default);
        mAppBarLayout.setExpanded(true);
        ViewCompat.setTransitionName(mImageViewSelectedPhoto, getString(R.string.transition_name_animal_photo));
        if (mIsEnterTransitionPostponed) {
            supportStartPostponedEnterTransition();
        }
    }
    @Override
    public void showSelectedImage(Bitmap bitmap, String imageUri) {
        ViewCompat.setTransitionName(mImageViewSelectedPhoto, imageUri);
        mImageViewSelectedPhoto.setImageBitmap(bitmap);
        mAppBarLayout.setExpanded(true);
        if (mIsEnterTransitionPostponed) {
            supportStartPostponedEnterTransition();
        }
    }
}
