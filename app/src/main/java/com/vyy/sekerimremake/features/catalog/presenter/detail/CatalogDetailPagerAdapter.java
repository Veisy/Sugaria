package com.vyy.sekerimremake.features.catalog.presenter.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CatalogDetailPagerAdapter extends FragmentStateAdapter {
    private final int[] mImages;

    public CatalogDetailPagerAdapter(@NonNull Fragment fragment, int[] images) {
        super(fragment);
        mImages = images;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CatalogDetailPagerFragment.getInstance(mImages[position]);
    }

    @Override
    public int getItemCount() {
        return mImages.length;
    }
}
