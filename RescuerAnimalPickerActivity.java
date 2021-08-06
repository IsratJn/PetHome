package com.example.pethome.storeapp.ui.rescuers.animal;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.models.AnimalLite;
import com.example.pethome.storeapp.utils.InjectorUtility;
import java.util.ArrayList;
public class RescuerAnimalPickerActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        RescuerAnimalPickerNavigator, RescuerAnimalPickerMultiSelectListener, RescuerAnimalPickerSearchActionsListener {
    public static final int REQUEST_RESCUER_ANIMALSS = 50;
    public static final int RESULT_RESCUER_ANIMALSS = REQUEST_RESCUER_ANIMALSS + RESULT_FIRST_USER;
    public static final String EXTRA_RESCUER_ANIMALSS = RescuerAnimalPickerActivity.class.getPackage() + "extra.RESCUER_ANIMALSS";
    private static final String BUNDLE_SEARCH_QUERY_STR_KEY = "RescuerAnimalPicker.SearchQuery";
    private RescuerAnimalPickerContract.Presenter mPresenter;
    private SearchView mSearchView;
    private String mSearchQueryStr;
    private int mSearchStartThreshold;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuer_animal_picker);
        ArrayList<AnimalLite> rescuerAnimals = getIntent().getParcelableArrayListExtra(EXTRA_RESCUER_ANIMALSS);
        if (rescuerAnimals == null) {
            rescuerAnimals = new ArrayList<>();
        }
        setupToolbar();
        RescuerAnimalPickerActivityFragment contentFragment = obtainContentFragment(rescuerAnimals);
        mPresenter = obtainPresenter(contentFragment);
        setupSearchView();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_SEARCH_QUERY_STR_KEY, mSearchQueryStr);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSearchQueryStr = savedInstanceState.getString(BUNDLE_SEARCH_QUERY_STR_KEY);
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!TextUtils.isEmpty(mSearchQueryStr) && mSearchQueryStr.length() >= mSearchStartThreshold) {
            mSearchView.setQuery(mSearchQueryStr, true);
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
    private RescuerAnimalPickerContract.Presenter obtainPresenter(RescuerAnimalPickerActivityFragment contentFragment) {
        return new RescuerAnimalPickerPresenter(
                InjectorUtility.provideStoreRepository(this),
                contentFragment,
                this,
                this,
                this
        );
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_rescuer_animal_picker);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    private RescuerAnimalPickerActivityFragment obtainContentFragment(ArrayList<AnimalLite> rescuerAnimals) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        RescuerAnimalPickerActivityFragment fragment
                = (RescuerAnimalPickerActivityFragment) supportFragmentManager.findFragmentById(R.id.content_rescuer_animal_picker);
        if (fragment == null) {
            fragment = RescuerAnimalPickerActivityFragment.newInstance(rescuerAnimals);
            supportFragmentManager.beginTransaction()
                    .add(R.id.content_rescuer_animal_picker, fragment)
                    .commit();
        }
        return fragment;
    }
    private void setupSearchView() {
        mSearchView = findViewById(R.id.search_view_rescuer_animal_picker);
        mSearchView.setOnQueryTextListener(this);
        mSearchStartThreshold = getResources().getInteger(R.integer.rescuer_config_picker_search_start_threshold);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mSearchView.isEnabled()) {
            mSearchQueryStr = query;
            mPresenter.filterResults(mSearchQueryStr);
            mSearchView.clearFocus();
        }
        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        if (mSearchView.isEnabled()) {
            mSearchQueryStr = newText;
            if (mSearchQueryStr.length() >= mSearchStartThreshold) {
                mPresenter.filterResults(mSearchQueryStr);
            } else if (TextUtils.isEmpty(mSearchQueryStr)) {
                mPresenter.clearFilter();
            }
        }
        return true;
    }
    @Override
    public void doSetResult(ArrayList<AnimalLite> animalsToSell) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESCUER_ANIMALSS, animalsToSell);
        setResult(RESULT_RESCUER_ANIMALSS, resultIntent);
        finish();
    }
    @Override
    public void doCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
    @Override
    public void showSelectedCount(int countOfAnimalsSelected) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setSubtitle(getString(R.string.rescuer_animal_picker_action_pick_live_count, countOfAnimalsSelected));
        }
    }
    @Override
    public void onBackPressed() {
        mPresenter.onUpOrBackAction();
    }
    @Override
    public void disableSearch() {
        mSearchView.setEnabled(false);
    }
}
