package com.example.pethome.storeapp.ui;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pethome.storeapp.R;
import com.example.pethome.storeapp.data.local.LoaderProvider;
import com.example.pethome.storeapp.ui.adoptions.AdoptionsListFragment;
import com.example.pethome.storeapp.ui.adoptions.AdoptionsListPresenter;
import com.example.pethome.storeapp.ui.animals.AnimalListFragment;
import com.example.pethome.storeapp.ui.animals.AnimalListPresenter;
import com.example.pethome.storeapp.ui.rescuers.RescuerListFragment;
import com.example.pethome.storeapp.ui.rescuers.RescuerListPresenter;
import com.example.pethome.storeapp.utils.InjectorUtility;
import java.util.Set;
public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private static final int ANIMALSS_PAGE_POSITION = 0;
    private static final int RESCUERS_PAGE_POSITION = 1;
    private static final int ADOPTIONS_PAGE_POSITION = 2;
    private static final int TOTAL_VIEW_COUNT = 3;
    private Context mContext;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();
    private FragmentManager mFragmentManager;
    MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ANIMALSS_PAGE_POSITION:
                return provideAnimalListFragment();
            case RESCUERS_PAGE_POSITION:
                return provideRescuerListFragment();
            case ADOPTIONS_PAGE_POSITION:
                return provideAdoptionsListFragment();
            default:
                return null;
        }
    }
    private Fragment provideAnimalListFragment() {
        AnimalListFragment fragment = AnimalListFragment.newInstance();
        initPresenter(fragment);
        return fragment;
    }
    private Fragment provideRescuerListFragment() {
        RescuerListFragment fragment = RescuerListFragment.newInstance();
        initPresenter(fragment);
        return fragment;
    }
    private Fragment provideAdoptionsListFragment() {
        AdoptionsListFragment fragment = AdoptionsListFragment.newInstance();
        initPresenter(fragment);
        return fragment;
    }
    private void initPresenter(Fragment fragment) {
        if (fragment instanceof AnimalListFragment) {
            AnimalListPresenter presenter = new AnimalListPresenter(
                    LoaderProvider.getInstance(mContext),
                    ((FragmentActivity) mContext).getSupportLoaderManager(),
                    InjectorUtility.provideStoreRepository(mContext),
                    (AnimalListFragment) fragment
            );
        } else if (fragment instanceof RescuerListFragment) {
            RescuerListPresenter presenter = new RescuerListPresenter(
                    LoaderProvider.getInstance(mContext),
                    ((FragmentActivity) mContext).getSupportLoaderManager(),
                    InjectorUtility.provideStoreRepository(mContext),
                    (RescuerListFragment) fragment
            );
        } else if (fragment instanceof AdoptionsListFragment) {
            AdoptionsListPresenter presenter = new AdoptionsListPresenter(
                    LoaderProvider.getInstance(mContext),
                    ((FragmentActivity) mContext).getSupportLoaderManager(),
                    InjectorUtility.provideStoreRepository(mContext),
                    (AdoptionsListFragment) fragment
            );
        }
    }
    @Override
    public int getCount() {
        return TOTAL_VIEW_COUNT;
    }
    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.delete(position);
        super.destroyItem(container, position, object);
    }
    @Nullable
    Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Set<String> keyStringSet = bundle.keySet();
            for (String keyString : keyStringSet) {
                if (keyString.startsWith("f")) {
                    int position = Integer.parseInt(keyString.substring(1));
                    Fragment fragment = mFragmentManager.getFragment(bundle, keyString);
                    if (fragment != null) {
                        mRegisteredFragments.put(position, fragment);
                        initPresenter(fragment);
                    }
                }
            }
        }
    }
    @NonNull
    View getTabView(ViewGroup container, int position) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_main_tab, container, false);
        ImageView imageViewTabIcon = rootView.findViewById(R.id.image_main_tab_icon);
        ImageView imageViewTabIconTemp = rootView.findViewById(R.id.image_main_tab_icon_temp);
        TextView textViewTabTitle = rootView.findViewById(R.id.text_main_tab_title);
        switch (position) {
            case ANIMALSS_PAGE_POSITION:
                imageViewTabIcon.setImageResource(R.drawable.state_main_tab_animal_material);
                imageViewTabIconTemp.setImageResource(R.drawable.state_main_tab_animal_material);
                textViewTabTitle.setText("Animals");
                break;
            case RESCUERS_PAGE_POSITION:
                imageViewTabIcon.setImageResource(R.drawable.state_main_tab_rescuer);
                imageViewTabIconTemp.setImageResource(R.drawable.state_main_tab_rescuer);
                textViewTabTitle.setText("Rescuers");
                break;
            case ADOPTIONS_PAGE_POSITION:
                imageViewTabIcon.setImageResource(R.drawable.state_main_tab_cart);
                imageViewTabIconTemp.setImageResource(R.drawable.state_main_tab_cart);
                textViewTabTitle.setText("Adoptions");
                break;
        }
        return rootView;
    }
    void changeTabView(TabLayout.Tab tab, boolean visibility) {
        View rootView = tab.getCustomView();
        if (rootView != null) {
            ImageView imageViewTabIconTemp = rootView.findViewById(R.id.image_main_tab_icon_temp);
            Group groupIconTitle = rootView.findViewById(R.id.group_main_tab_icon_title);
            if (visibility) {
                groupIconTitle.setVisibility(View.VISIBLE);
                imageViewTabIconTemp.setVisibility(View.GONE);
            } else {
                groupIconTitle.setVisibility(View.INVISIBLE);
                imageViewTabIconTemp.setVisibility(View.VISIBLE);
            }
        }
    }
}
