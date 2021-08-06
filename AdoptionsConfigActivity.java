package com.example.pethome.storeapp.ui.adoptions.config;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.ui.animals.config.DefaultPhotoChangeListener;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigContract;
import com.example.pethome.storeapp.ui.rescuers.config.RescuerConfigActivity;
import com.example.pethome.storeapp.utils.InjectorUtility;
import com.example.pethome.storeapp.workers.ImageDownloaderFragment;
public class AdoptionsConfigActivity extends AppCompatActivity implements AdoptionsConfigNavigator, DefaultPhotoChangeListener {
    public static final int REQUEST_EDIT_ADOPTIONS = 60;
    public static final int RESULT_EDIT_ADOPTIONS = RESULT_FIRST_USER + REQUEST_EDIT_ADOPTIONS;
    public static final String EXTRA_ANIMALS_ID = AdoptionsConfigActivity.class.getPackage() + "extra.ANIMALS_ID";
    public static final String EXTRA_RESULT_ANIMALS_ID = AdoptionsConfigActivity.class.getPackage() + "extra.ANIMALS_ID";
    public static final String EXTRA_RESULT_ANIMALS_SKU = AdoptionsConfigActivity.class.getPackage() + "extra.ANIMALS_SKU";
    private AdoptionsConfigContract.Presenter mPresenter;
    private ImageView mImageViewItemPhoto;
    private boolean mIsEnterTransitionPostponed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptions_config);
        supportPostponeEnterTransition();
        mImageViewItemPhoto = findViewById(R.id.image_adoptions_config_item_photo);
        int animalId = getIntent().getIntExtra(EXTRA_ANIMALS_ID, AnimalConfigContract.NEW_ANIMALS_INT);
        if (animalId == AnimalConfigContract.NEW_ANIMALS_INT) {
            doCancel();
        }
        setupToolbar();
        AdoptionsConfigActivityFragment contentFragment = obtainContentFragment(animalId);
        mPresenter = obtainPresenter(animalId, contentFragment);
    }
    @NonNull
    private AdoptionsConfigContract.Presenter obtainPresenter(int animalId, AdoptionsConfigActivityFragment contentFragment) {
        return new AdoptionsConfigPresenter(animalId,
                InjectorUtility.provideStoreRepository(this),
                contentFragment,
                this,
                this);
    }
    private AdoptionsConfigActivityFragment obtainContentFragment(int animalId) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        AdoptionsConfigActivityFragment fragment
                = (AdoptionsConfigActivityFragment) supportFragmentManager.findFragmentById(R.id.content_adoptions_config);
        if (fragment == null) {
            fragment = AdoptionsConfigActivityFragment.newInstance(animalId);
            supportFragmentManager.beginTransaction()
                    .add(R.id.content_adoptions_config, fragment)
                    .commit();
        }
        return fragment;
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_adoptions_config);
        toolbar.setTitle(R.string.adoptions_config_title_edit_adoptions);
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
    public void doSetResult(int resultCode, int animalId, @NonNull String animalSku) {
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
    public void launchEditAnimal(int animalId) {
        Intent animalConfigIntent = new Intent(this, AnimalConfigActivity.class);
        animalConfigIntent.putExtra(AnimalConfigActivity.EXTRA_ANIMALS_ID, animalId);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                mImageViewItemPhoto,
                ViewCompat.getTransitionName(mImageViewItemPhoto)
        );
        ActivityCompat.startActivityForResult(this, animalConfigIntent, AnimalConfigActivity.REQUEST_EDIT_ANIMALS, activityOptionsCompat.toBundle());
    }
//    @Override
//    public void launchProcureAnimal(AnimalRescuerAdoptions animalRescuerAdoptions, AnimalImage animalImageToBeShown, String animalName, String animalSku) {
//        Intent adoptionsConfigIntent = new Intent(this, AdoptionsProcurementActivity.class);
//        adoptionsConfigIntent.putExtra(AdoptionsProcurementActivity.EXTRA_ANIMALS_NAME, animalName);
//        adoptionsConfigIntent.putExtra(AdoptionsProcurementActivity.EXTRA_ANIMALS_SKU, animalSku);
//        adoptionsConfigIntent.putExtra(AdoptionsProcurementActivity.EXTRA_ANIMALS_IMAGE_DEFAULT, animalImageToBeShown);
//        adoptionsConfigIntent.putExtra(AdoptionsProcurementActivity.EXTRA_ANIMALS_RESCUER_ADOPTIONS, animalRescuerAdoptions);
//        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                this,
//                mImageViewItemPhoto,
//                ViewCompat.getTransitionName(mImageViewItemPhoto)
//        );
//        ActivityCompat.startActivity(this, adoptionsConfigIntent, activityOptionsCompat.toBundle());
//    }
    @Override
    public void launchEditRescuer(int rescuerId) {
        Intent rescuerConfigIntent = new Intent(this, RescuerConfigActivity.class);
        rescuerConfigIntent.putExtra(RescuerConfigActivity.EXTRA_RESCUER_ID, rescuerId);
        startActivityForResult(rescuerConfigIntent, RescuerConfigActivity.REQUEST_EDIT_RESCUER);
    }
    @Override
    public void showDefaultImage() {
        mImageViewItemPhoto.setImageResource(R.drawable.ic_all_animal_default);
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
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
