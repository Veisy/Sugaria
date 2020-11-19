package com.example.sekerimremake.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sekerimremake.fragments.CatalogViewPagerFragment;

public class CatalogChildPagerAdapter extends FragmentStateAdapter {
    private final int[] mImages;

    public CatalogChildPagerAdapter(@NonNull Fragment fragment, int[] images) {
        super(fragment);
        mImages = images;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CatalogViewPagerFragment.getInstance(mImages[position]);
    }

    @Override
    public int getItemCount() {
        return mImages.length;
    }
}
