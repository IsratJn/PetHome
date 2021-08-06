package com.example.pethome.storeapp.ui.rescuers.config;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.ui.animals.config.AnimalConfigActivity;
import com.example.pethome.storeapp.ui.rescuers.animal.RescuerAnimalPickerActivity;
import com.example.pethome.storeapp.utils.InjectorUtility;
import java.util.ArrayList;
public class RescuerConfigActivity extends AppCompatActivity implements RescuerConfigNavigator {
    public static final int REQUEST_ADD_RESCUER = 40;
    public static final int REQUEST_EDIT_RESCUER = 42;
    public static final int RESULT_ADD_RESCUER = REQUEST_ADD_RESCUER + RESULT_FIRST_USER;
    public static final int RESULT_EDIT_RESCUER = REQUEST_EDIT_RESCUER + RESULT_FIRST_USER;
    public static final int RESULT_DELETE_RESCUER = RESULT_EDIT_RESCUER + RESULT_FIRST_USER;
    public static final String EXTRA_RESCUER_ID = RescuerConfigActivity.class.getPackage() + "extra.RESCUER_ID";
    public static final String EXTRA_RESULT_RESCUER_ID = RescuerConfigActivity.class.getPackage() + "extra.RESCUER_ID";
    public static final String EXTRA_RESULT_RESCUER_CODE = RescuerConfigActivity.class.getPackage() + "extra.RESCUER_CODE";
    private RescuerConfigContract.Presenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuer_config);
        int rescuerId = getIntent().getIntExtra(EXTRA_RESCUER_ID, RescuerConfigContract.NEW_RESCUER_INT);
        setupToolbar(rescuerId);
        RescuerConfigActivityFragment contentFragment = obtainContentFragment(rescuerId);
        mPresenter = obtainPresenter(rescuerId, contentFragment);
    }
    @NonNull
    private RescuerConfigContract.Presenter obtainPresenter(int rescuerId, RescuerConfigActivityFragment contentFragment) {
        return new RescuerConfigPresenter(
                rescuerId,
                InjectorUtility.provideStoreRepository(this),
                contentFragment,
                this
        );
    }
    private RescuerConfigActivityFragment obtainContentFragment(int rescuerId) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        RescuerConfigActivityFragment fragment = (RescuerConfigActivityFragment) supportFragmentManager.findFragmentById(R.id.content_rescuer_config);
        if (fragment == null) {
            fragment = RescuerConfigActivityFragment.newInstance(rescuerId);
            supportFragmentManager.beginTransaction()
                    .add(R.id.content_rescuer_config, fragment)
                    .commit();
        }
        return fragment;
    }
    public void setupToolbar(int rescuerId) {
        Toolbar toolbar = findViewById(R.id.toolbar_rescuer_config);
        if (rescuerId == RescuerConfigContract.NEW_RESCUER_INT) {
            toolbar.setTitle("Add Rescuer");
        } else {
            toolbar.setTitle("Edit Rescuer");
        }
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
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
    public void doSetResult(int resultCode, int rescuerId, @NonNull String rescuerCode) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESULT_RESCUER_ID, rescuerId);
        resultIntent.putExtra(EXTRA_RESULT_RESCUER_CODE, rescuerCode);
        setResult(resultCode, resultIntent);
        supportFinishAfterTransition();
    }
    @Override
    public void doCancel() {
        setResult(RESULT_CANCELED);
        supportFinishAfterTransition();
    }
    @Override
    public void launchEditAnimal(int animalId, ActivityOptionsCompat activityOptionsCompat) {
        Intent animalConfigIntent = new Intent(this, AnimalConfigActivity.class);
        animalConfigIntent.putExtra(AnimalConfigActivity.EXTRA_ANIMALS_ID, animalId);
        ActivityCompat.startActivityForResult(this, animalConfigIntent, AnimalConfigActivity.REQUEST_EDIT_ANIMALS, activityOptionsCompat.toBundle());
    }
    @Override
    public void launchPickAnimals(ArrayList<AnimalLite> animalLiteList) {
        Intent animalPickerIntent = new Intent(this, RescuerAnimalPickerActivity.class);
        animalPickerIntent.putExtra(RescuerAnimalPickerActivity.EXTRA_RESCUER_ANIMALSS, animalLiteList);
        startActivityForResult(animalPickerIntent, RescuerAnimalPickerActivity.REQUEST_RESCUER_ANIMALSS);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
