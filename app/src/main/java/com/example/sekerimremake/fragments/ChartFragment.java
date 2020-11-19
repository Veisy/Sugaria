package com.example.sekerimremake.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.sekerimremake.R;
import com.example.sekerimremake.adapters.ChartAdapter;
import com.example.sekerimremake.database.ChartDbHelper;
import com.example.sekerimremake.databinding.FragmentChartBinding;

import java.util.List;
import java.util.Map;

public class ChartFragment extends Fragment implements ChartAdapter.OnRowClickListener{

    private FragmentChartBinding binding;
    private Context mContext;
    private NavController navController;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        ChartAdapter chartAdapter = new ChartAdapter(mContext, new ChartDbHelper(mContext)
                .getEveryone(), this);
        binding.recyclerViewChart.setAdapter(chartAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerViewChart.setLayoutManager(linearLayoutManager);
        LayoutAnimationController layoutAnimationController = AnimationUtils
                .loadLayoutAnimation(mContext, R.anim.recyclerview_layout_animation);
        binding.recyclerViewChart.setLayoutAnimation(layoutAnimationController);
        if (chartAdapter.getItemCount() > 1) {
            binding.recyclerViewChart.smoothScrollToPosition(chartAdapter.getItemCount() - 1);
        }

       binding.floatingActionButtonAddRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("button_flag", true);
                navController.navigate(R.id.action_chartFragment_to_chartRowFragment, bundle);
            }
        });
    }

    @Override
    public void onRowClick(int position, final View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("button_flag", false);
        bundle.putInt("position", position);

        final FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(view,
                        view.getTransitionName())
                .build();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                sharedElements.put(view.getTransitionName(), view);
            }
        });
        navController.navigate(
                R.id.action_chartFragment_to_chartRowFragment,
                bundle,
                null,
                extras);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}