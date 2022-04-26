package com.example.sekerimremake.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sekerimremake.R;
import com.example.sekerimremake.adapters.CatalogChildPagerAdapter;
import com.example.sekerimremake.databinding.FragmentCatalogDetailBinding;
import com.example.sekerimremake.models.CatalogModel;
import com.example.sekerimremake.resources.ApplicationOfInsulin;
import com.example.sekerimremake.resources.CatalogItems;
import com.example.sekerimremake.resources.Chiropody;
import com.example.sekerimremake.resources.Exercise;
import com.example.sekerimremake.resources.Hyperglycemia;
import com.example.sekerimremake.resources.Hypoglycemia;
import com.example.sekerimremake.resources.MeasurementItems;
import com.example.sekerimremake.resources.Nutrition;
import com.example.sekerimremake.resources.OralAntidiabeticDrugs;
import com.example.sekerimremake.transformers.DepthTransformation;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Map;

public class CatalogDetailFragment extends Fragment implements View.OnClickListener {

    private FragmentCatalogDetailBinding binding;
    private Context mContext;

    private CatalogModel catalogModel;

    private int mPosition;
    private int[] mImages;
    private boolean isRotated;

    public CatalogDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRotated = savedInstanceState != null;
        if (getArguments() != null) {
            mPosition = getArguments().getInt("pos");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonPrevious.setOnClickListener(this);
        binding.buttonNext.setOnClickListener(this);
        setImagesAndCatalogModel();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setHeaderView();
            if (mPosition == 1)
                setSubtitleTabLayout();
        }
        setViewPagerAndIndicator();
    }

    private void setImagesAndCatalogModel() {
        switch (mPosition) {
            case 0:
                mImages = MeasurementItems.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(0);
                break;
            case 1:
                catalogModel = CatalogItems.getSingleITEM(1);
                mImages = ApplicationOfInsulin.getIMAGES();
                break;
            case 2:
                mImages = Nutrition.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(2);
                break;
            case 3:
                mImages = OralAntidiabeticDrugs.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(3);
                break;
            case 4:
                mImages = Exercise.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(4);
                break;
            case 5:
                mImages = Hypoglycemia.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(5);
                break;
            case 6:
                mImages = Hyperglycemia.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(6);
                break;
            case 7:
                mImages = Chiropody.getIMAGES();
                catalogModel = CatalogItems.getSingleITEM(7);
                break;
        }
    }

    private void setSubtitleTabLayout() {
        if (binding.tabLayoutSubTitles != null) {
            binding.tabLayoutSubTitles.setVisibility(View.VISIBLE);
            binding.tabLayoutSubTitles.addTab(binding.tabLayoutSubTitles.newTab()
                    .setText(R.string.insulin_types_and_information), 0, true);
            binding.tabLayoutSubTitles.addTab(binding.tabLayoutSubTitles.newTab()
                    .setText(R.string.insulin_application_regions), 1);
            binding.tabLayoutSubTitles.addTab(binding.tabLayoutSubTitles.newTab()
                    .setText(R.string.insulin_storage_conditions), 2);
            binding.tabLayoutSubTitles.clearOnTabSelectedListeners();
            binding.tabLayoutSubTitles.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int currentPosition = binding.viewPagerCatalogItem.getCurrentItem();
                    int[] insulinCategoryPositions = ApplicationOfInsulin.getCategoryPositions();
                    if (tab.getPosition() == 0 && !(currentPosition >= insulinCategoryPositions[0] &&
                            currentPosition < insulinCategoryPositions[1])) {
                        binding.viewPagerCatalogItem.setCurrentItem(0);
                    } else if (tab.getPosition() == 1 && !(currentPosition >= insulinCategoryPositions[1] &&
                            currentPosition < insulinCategoryPositions[2])) {
                        binding.viewPagerCatalogItem.setCurrentItem(12);
                    } else if (tab.getPosition() == 2 && !(currentPosition == insulinCategoryPositions[2])) {
                        binding.viewPagerCatalogItem.setCurrentItem(14);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }
    }

    private void setHeaderView() {
        if (binding.catalogItemHeader != null) {
            postponeEnterTransition();
            final ImageView headerImage = binding.catalogItemHeader.imageViewCatalogItem;
            ViewCompat.setTransitionName(binding.catalogItemHeader.cartViewCatalogListItem,
                    String.valueOf(CatalogItems.getCatalogItems()[mPosition].getImage()));
            Transition transition = TransitionInflater.from(mContext)
                    .inflateTransition(R.transition.shared_image);
            setSharedElementEnterTransition(transition);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(@NonNull Transition transition) {}
                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    if (binding != null) {
                        fadeInAnimation(binding.viewPagerCatalogItem);
                        fadeInAnimation(binding.tabLayoutIndicator);
                        fadeInAnimation(binding.buttonNext);
                    }
                }
                @Override
                public void onTransitionCancel(@NonNull Transition transition) {}

                @Override
                public void onTransitionPause(@NonNull Transition transition) {}

                @Override
                public void onTransitionResume(@NonNull Transition transition) {}
            });
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    sharedElements.put(binding.catalogItemHeader.cartViewCatalogListItem.getTransitionName(),
                            binding.catalogItemHeader.cartViewCatalogListItem);
                }
            });
            Glide.with(this).load(catalogModel.getImage()).centerCrop()
                    .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Drawable> target, boolean isFirstResource) {
                    startPostponedEnterTransition();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                               DataSource dataSource, boolean isFirstResource) {
                    startPostponedEnterTransition();
                    return false;
                }
            }).into(headerImage);
            binding.catalogItemHeader.textViewCatalogItem.setText(catalogModel.getTitle());
            binding.catalogItemHeader.imageViewCatalogItem.setContentDescription(catalogModel.getTitle());
        }
    }

    private void setViewPagerAndIndicator() {
        binding.viewPagerCatalogItem.setAdapter(new CatalogChildPagerAdapter(this, mImages));
        binding.viewPagerCatalogItem.setPageTransformer(new DepthTransformation());

        if (isRotated) {
            fadeInAnimation(binding.viewPagerCatalogItem);
            fadeInAnimation(binding.tabLayoutIndicator);
            fadeInAnimation(binding.buttonNext);
        }

        new TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerCatalogItem, true,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //TabLayout is an indicator, so it does not change.
                    }
                }).attach();
        binding.viewPagerCatalogItem.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == mImages.length - 1)
                    binding.buttonNext.setText(R.string.end);
                else
                    binding.buttonNext.setText(R.string.next);

                if (position == 0)
                    binding.buttonPrevious.setVisibility(View.GONE);
                else
                    binding.buttonPrevious.setVisibility(View.VISIBLE);

                if (mPosition == 1 && getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    int[] insulinCategoryPositions = ApplicationOfInsulin.getCategoryPositions();
                    TabLayout.Tab tab;
                    if (position < insulinCategoryPositions[1]) {
                        assert binding.tabLayoutSubTitles != null;
                        tab = binding.tabLayoutSubTitles.getTabAt(0);
                    } else if (position < insulinCategoryPositions[2]) {
                        assert binding.tabLayoutSubTitles != null;
                        tab = binding.tabLayoutSubTitles.getTabAt(1);
                    } else {
                        assert binding.tabLayoutSubTitles != null;
                        tab = binding.tabLayoutSubTitles.getTabAt(2);
                    }
                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        });
    }

    private void fadeInAnimation(View view) {
        int mediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        view.animate()
                .alpha(1f)
                .setDuration(mediumAnimationDuration)
                .setListener(null);
    }

    @Override
    public void onClick(View v) {
        int currentPosition = binding.viewPagerCatalogItem.getCurrentItem();
        if (v.getId() == binding.buttonPrevious.getId()) {
            currentPosition--;

        } else if (v.getId() == binding.buttonNext.getId()) {
            if (currentPosition == mImages.length - 1) {
                requireActivity().onBackPressed();
            } else {
                currentPosition++;
            }

            if (currentPosition == 1) {
                Snackbar.make(v,
                        getResources().getString(R.string.catalog_item_swipe_reminder),
                        Snackbar.LENGTH_SHORT).show();
            }
        }
        binding.viewPagerCatalogItem.setCurrentItem(currentPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        catalogModel = null;
        binding = null;
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

}