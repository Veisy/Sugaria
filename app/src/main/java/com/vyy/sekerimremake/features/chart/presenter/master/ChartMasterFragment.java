package com.vyy.sekerimremake.features.chart.presenter.master;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vyy.sekerimremake.R;
import com.vyy.sekerimremake.features.chart.data.source.local.ChartDbHelper;
import com.vyy.sekerimremake.databinding.FragmentChartBinding;

import java.util.List;
import java.util.Map;

public class ChartMasterFragment extends Fragment implements ChartAdapter.OnRowClickListener {

    private FragmentChartBinding binding;
    private Context mContext;
    private NavController navController;
    private int scrollPosition;

    public ChartMasterFragment() {
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
        //To fill the chart with random data.
//        fillChartWithRandomData(0, 12, 1, 29);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChartBinding.inflate(inflater, container, false);
        scrollPosition = 0;
        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt("scrollPosition");
        } else {
            if (getArguments() != null) {
                scrollPosition = getArguments().getInt("scrollPosition");
            }
        }
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
        binding.recyclerViewChart.setLayoutManager(linearLayoutManager);
        LayoutAnimationController layoutAnimationController = AnimationUtils
                .loadLayoutAnimation(mContext, R.anim.recyclerview_layout_animation);
        binding.recyclerViewChart.setLayoutAnimation(layoutAnimationController);
        if (chartAdapter.getItemCount() > 1) {
            binding.recyclerViewChart.scrollToPosition(chartAdapter.getItemCount() - 1);
        }
        binding.floatingActionButtonAddRow.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("button_flag", true);
            navController.navigate(R.id.action_chartMasterFragment_to_chartDetailsFragment, bundle);
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
                R.id.action_chartMasterFragment_to_chartDetailsFragment,
                bundle,
                null,
                extras);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scrollPosition", scrollPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //To fill the chart with random data.

//    private void fillChartWithRandomData(int monthStart, int monthEnd, int dayStart, int dayEnd) {
//        ChartDbHelper helper = new ChartDbHelper(mContext);
//        ChartRowModel model;
//        for(int i = monthStart; i < monthEnd; i++) {
//            for(int j = dayStart; j < dayEnd; j++){
//                model = new ChartRowModel(
//                        -1,
//                        j,
//                        i,
//                        2021,
//                        new Random().nextInt(10) > 1 ? new Random().nextInt(75) + 65 : 0,
//                        new Random().nextInt(10) > 1 ? new Random().nextInt(95) + 95 : 0,
//                        new Random().nextInt(10) > 1 ? new Random().nextInt(75) + 65 : 0,
//                        new Random().nextInt(10) > 1 ? new Random().nextInt(95) + 95 : 0,
//                        new Random().nextInt(10) > 1 ? new Random().nextInt(75) + 65 : 0,
//                        new Random().nextInt(10) > 1 ? new Random().nextInt(95) + 95 : 0,
//                        new Random().nextInt(10) > 5 ? new Random().nextInt(75) + 85 : 0
//                );
//                helper.addOne(model);
//            }
//        }
//    }


}