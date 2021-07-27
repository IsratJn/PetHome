package com.example.pethome.storeapp.ui;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.cache.BitmapImageCache;
import com.example.pethome.storeapp.ui.adoptions.AdoptionsListFragment;
import com.example.pethome.storeapp.ui.animals.AnimalListFragment;
import com.example.pethome.storeapp.ui.rescuers.RescuerListFragment;
public class MainActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener, View.OnClickListener {
    private ViewPager mViewPager;
    private MainPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mFabAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViewPager();
        setupFab();
        mTabLayout = findViewById(R.id.tablayout_main);
        mTabLayout.setupWithViewPager(mViewPager);
        int tabCount = mTabLayout.getTabCount();
        for (int tabIndex = 0; tabIndex < tabCount; tabIndex++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(tabIndex);
            if (tab != null) {
                View tabView = mPagerAdapter.getTabView(mViewPager, tabIndex);
                tab.setCustomView(tabView);
                if (savedInstanceState == null && tabIndex == 0) {
                    mViewPager.setCurrentItem(tabIndex);
                    mPagerAdapter.changeTabView(tab, true);
                    mFabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mainAnimalListFabColor)));
                }
            }
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onTabSelected(mTabLayout.getTabAt(mViewPager.getCurrentItem()));
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }
    private void setupViewPager() {
        mViewPager = findViewById(R.id.viewpager_main);
        mPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }
    private void setupFab() {
        mFabAdd = findViewById(R.id.fab_main_add);
        mFabAdd.setOnClickListener(this);
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int newPosition = tab.getPosition();
        if (mViewPager.getCurrentItem() != newPosition) {
            mViewPager.setCurrentItem(newPosition);
        }
        mPagerAdapter.changeTabView(tab, true);
        Fragment pagerFragment = mPagerAdapter.getRegisteredFragment(newPosition);
        if (pagerFragment instanceof AdoptionsListFragment) {
            mFabAdd.hide();
        } else {
            mFabAdd.show();
            if (pagerFragment instanceof AnimalListFragment) {
                mFabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mainAnimalListFabColor)));
            } else if (pagerFragment instanceof RescuerListFragment) {
                mFabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mainRescuerListFabColor)));
            }
        }
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        mPagerAdapter.changeTabView(tab, false);
    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
    @Override
    protected void onResume() {
        super.onResume();
        mTabLayout.addOnTabSelectedListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mTabLayout.removeOnTabSelectedListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_main_add:
                fabAddButtonClicked();
                break;
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                onRefreshMenuClicked();
//                return true;
//            case R.id.action_about:
//                launchAboutActivity();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//    private void launchAboutActivity() {
//        Intent aboutIntent = new Intent(this, AboutActivity.class);
//        startActivity(aboutIntent);
//    }
    private void fabAddButtonClicked() {
        Fragment pagerFragment = getCurrentPagerFragment();
        if (pagerFragment != null) {
            PagerPresenter presenter = ((PagerView) pagerFragment).getPresenter();
            if (presenter != null) {
                presenter.onFabAddClicked();
            }
        }
    }
// v
    private Fragment getCurrentPagerFragment() {
        int position = mViewPager.getCurrentItem();
        return mPagerAdapter.getRegisteredFragment(position);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BitmapImageCache.clearCache();
    }
}
