package com.vyy.sugaria.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.vyy.sugaria.databinding.CatalogViewPagerLayoutBinding;

public class CatalogViewPagerFragment extends Fragment {

    private CatalogViewPagerLayoutBinding binding;
    Context mContext;
    private int mImage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static CatalogViewPagerFragment getInstance(int image) {
        CatalogViewPagerFragment fragment = new CatalogViewPagerFragment();

        if (image != 0) {
            Bundle bundle = new Bundle();
            bundle.putInt("imageKey", image);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mImage = getArguments().getInt("imageKey");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CatalogViewPagerLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        //widgets
        ImageView catalogImage = binding.catalogImage;

        if(mImage != 0) {
            Glide.with(mContext).load(mImage).into(catalogImage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
