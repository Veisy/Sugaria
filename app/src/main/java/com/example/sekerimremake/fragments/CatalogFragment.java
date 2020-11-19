package com.example.sekerimremake.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import com.example.sekerimremake.R;
import com.example.sekerimremake.adapters.CatalogListAdapter;
import com.example.sekerimremake.databinding.FragmentCatalogBinding;
import com.example.sekerimremake.resources.CatalogItems;

import java.util.List;
import java.util.Map;

public class CatalogFragment extends Fragment implements CatalogListAdapter.CatalogListHolder
        .OnCatalogClickListener {

    private FragmentCatalogBinding binding;
    private Context mContext;
    private  NavController navController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.recyclerViewCatalog.setAdapter(new CatalogListAdapter(mContext,
                CatalogItems.getCatalogItems(),
                this));
        binding.recyclerViewCatalog.setLayoutManager(new GridLayoutManager(mContext,
                getResources().getInteger(R.integer.grid_column_count)));
        LayoutAnimationController layoutAnimationController = AnimationUtils
                .loadLayoutAnimation(mContext, R.anim.recyclerview_grid_layout_animation);
        binding.recyclerViewCatalog.setLayoutAnimation(layoutAnimationController);

    }

    @Override
    public void onCatalogClick(int position, final View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        int orientation = getResources().getConfiguration().orientation;
        if (navController != null) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // create the transition animation
                setExitTransition(TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.grid_exit_transition));
                ((TransitionSet) getExitTransition()).excludeTarget(view, true);
                final FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(view,
                                view.getTransitionName())
                        .build();
               setExitSharedElementCallback(new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        super.onMapSharedElements(names, sharedElements);
                        sharedElements.put(view.getTransitionName(), view);
                    }
                });
                navController.navigate(
                        R.id.action_catalogFragment_to_catalogItemFragment,
                        bundle,
                        null,
                        extras);

            } else {
                navController.navigate(
                        R.id.action_catalogFragment_to_catalogItemFragment,
                        bundle);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
