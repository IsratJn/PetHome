package com.example.pethome.storeapp.ui.animals.config;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalImage;
import com.example.pethome.storeapp.ui.animals.image.AnimalImageActivity;
import com.example.pethome.storeapp.utils.InjectorUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
import java.util.ArrayList;
public class AnimalConfigActivity extends AppCompatActivity
        implements AnimalConfigNavigator, DefaultPhotoChangeListener, View.OnClickListener {
    public static final int REQUEST_ADD_ANIMALS = 20;
    public static final int REQUEST_EDIT_ANIMALS = 22;
    public static final int RESULT_ADD_ANIMALS = REQUEST_ADD_ANIMALS + RESULT_FIRST_USER;
    public static final int RESULT_EDIT_ANIMALS = REQUEST_EDIT_ANIMALS + RESULT_FIRST_USER;
    public static final int RESULT_DELETE_ANIMALS = RESULT_EDIT_ANIMALS + RESULT_FIRST_USER;
    public static final String EXTRA_ANIMALS_ID = AnimalConfigActivity.class.getPackage() + "extra.ANIMALS_ID";
    public static final String EXTRA_RESULT_ANIMALS_ID = AnimalConfigActivity.class.getPackage() + "extra.ANIMALS_ID";
    public static final String EXTRA_RESULT_ANIMALS_SKU = AnimalConfigActivity.class.getPackage() + "extra.ANIMALS_SKU";
    private AnimalConfigContract.Presenter mPresenter;
    private ImageView mImageViewItemPhoto;
    private AppBarLayout mAppBarLayout;
    private boolean mIsEnterTransitionPostponed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_config);
        supportPostponeEnterTransition();
        mAppBarLayout = findViewById(R.id.app_bar_animal_config);
        mImageViewItemPhoto = findViewById(R.id.image_animal_config_item_photo);
        int animalId = getIntent().getIntExtra(EXTRA_ANIMALS_ID, AnimalConfigContract.NEW_ANIMALS_INT);
        setupToolbar(animalId);
        findViewById(R.id.image_animal_config_add_photo).setOnClickListener(this);
        AnimalConfigActivityFragment contentFragment = obtainContentFragment(animalId);
        mPresenter = obtainPresenter(animalId, contentFragment);
    }
    @NonNull
    private AnimalConfigContract.Presenter obtainPresenter(int animalId, AnimalConfigActivityFragment contentFragment) {
        return new AnimalConfigPresenter(
                animalId,
                InjectorUtility.provideStoreRepository(this),
                contentFragment,
                this,
                this
        );
    }
    private AnimalConfigActivityFragment obtainContentFragment(int animalId) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        AnimalConfigActivityFragment fragment
                = (AnimalConfigActivityFragment) supportFragmentManager
                .findFragmentById(R.id.content_animal_config);
        if (fragment == null) {
            fragment = AnimalConfigActivityFragment.newInstance(animalId);
            supportFragmentManager.beginTransaction()
                    .add(R.id.content_animal_config, fragment)
                    .commit();
        }
        return fragment;
    }
    private void setupToolbar(int animalId) {
        Toolbar toolbar = findViewById(R.id.toolbar_animal_config);
        if (animalId == AnimalConfigContract.NEW_ANIMALS_INT) {
            toolbar.setTitle("Add Animals");
        } else {
            toolbar.setTitle("Edit Animals");
        }
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
        mPresenter.onUpOrBackAction();
    }
    @Override
    public void doSetResult(final int resultCode, final int animalId, @NonNull final String animalSku) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESULT_ANIMALS_ID, animalId);
        resultIntent.putExtra(EXTRA_RESULT_ANIMALS_SKU, animalSku);
        setResult(resultCode, resultIntent);
        supportFinishAfterTransition();
    }
    @Override
    public void doCancel() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
    @Override
    public void launchAnimalImagesView(ArrayList<AnimalImage> animalImages) {
        Intent animalImagesIntent = new Intent(this, AnimalImageActivity.class);
        animalImagesIntent.putParcelableArrayListExtra(AnimalImageActivity.EXTRA_ANIMALS_IMAGES, animalImages);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                mImageViewItemPhoto,
                ViewCompat.getTransitionName(mImageViewItemPhoto)
        );
        ActivityCompat.startActivityForResult(this, animalImagesIntent,
                AnimalImageActivity.REQUEST_ANIMALS_IMAGE, activityOptionsCompat.toBundle());
    }
    @Override
    public void showDefaultImage() {
        mImageViewItemPhoto.setImageResource(R.drawable.ic_all_animal_default);
        mAppBarLayout.setExpanded(true);
        ViewCompat.setTransitionName(mImageViewItemPhoto, getString(R.string.transition_name_animal_photo));
        if (mIsEnterTransitionPostponed) {
            supportStartPostponedEnterTransition();
        }
    }
    @Override
    public void showSelectedAnimalImage(String imageUri) {
        ViewCompat.setTransitionName(mImageViewItemPhoto, imageUri);
        ImageDownloaderFragment.newInstance(getSupportFragmentManager(), mImageViewItemPhoto.getId())
                .setOnSuccessListener(bitmap -> {
                    if (mIsEnterTransitionPostponed) {
                        supportStartPostponedEnterTransition();
                    }
                })
                .executeAndUpdate(mImageViewItemPhoto, imageUri, mImageViewItemPhoto.getId(), getSupportLoaderManager());
        mAppBarLayout.setExpanded(true);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_animal_config_add_photo:
                mPresenter.openAnimalImages();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
